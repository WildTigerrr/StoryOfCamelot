package com.wildtigerrr.StoryOfCamelot.bin.base.service;

import com.wildtigerrr.StoryOfCamelot.bin.enums.Language;
import com.wildtigerrr.StoryOfCamelot.bin.enums.ReplyButton;
import com.wildtigerrr.StoryOfCamelot.database.jpa.schema.Location;
import com.wildtigerrr.StoryOfCamelot.database.jpa.schema.enums.Stats;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.List;

@Component
public class KeyboardManager {

    public static InlineKeyboardMarkup getKeyboardForStatUp(int freePoints) {
        if (freePoints < 1) return null;
        List<String> statMilestones = getStatMilestones(freePoints);

        int buttonsLine = statMilestones.size() != 1 ? statMilestones.size() : 3;
        KeyboardBuilder<InlineKeyboardMarkup> builder = new KeyboardBuilder<>(KeyboardBuilder.Type.INLINE, buttonsLine);
        for (Stats stat : Stats.values()) {
            for (String val : statMilestones) {
                builder.addButton(
                        new InlineKeyboardButton()
                                .setText(stat.emoji() + "+" + val)
                                .setCallbackData("/up_" + stat.getCharacter().toLowerCase() + "_" + val)
                );
            }
        }
        return builder.build();
    }

    private static List<String> getStatMilestones(int freePoints) {
        List<String> milestones = new ArrayList<>();
        milestones.add("1");
        if (freePoints > 1) {
            if (freePoints > 4) {
                milestones.add("5");
                if (freePoints > 24) {
                    milestones.add("25");
                }
            }
            if (freePoints != 5 && freePoints != 25) {
                milestones.add("" + freePoints);
            }
        }
        return milestones;
    }

    public static InlineKeyboardMarkup getKeyboardForLanguageSelect() {
        KeyboardBuilder<InlineKeyboardMarkup> builder = new KeyboardBuilder<>(KeyboardBuilder.Type.INLINE, 2);
        for (Language lang : Language.values()) {
            builder.addButton(
                    new InlineKeyboardButton()
                            .setText(lang.getName())
                            .setCallbackData("/lang " + lang.ordinal())
            );
        }
        return builder.build();
    }

    public static InlineKeyboardMarkup getKeyboardForLocations(ArrayList<Location> nearLocations, Language lang) {
        KeyboardBuilder<InlineKeyboardMarkup> builder = new KeyboardBuilder<>(KeyboardBuilder.Type.INLINE, 2);
        for (Location loc : nearLocations) {
            builder.addButton(
                    new InlineKeyboardButton()
                            .setText(loc.getName(lang))
                            .setCallbackData("/move " + loc.getId())
            );
        }
        return builder.build();
    }

    public static ReplyKeyboardMarkup getReplyByButtons(List<ReplyButton> buttons, Language lang) {
        KeyboardBuilder<ReplyKeyboardMarkup> builder = new KeyboardBuilder<>(KeyboardBuilder.Type.REPLY, 2);
        for (ReplyButton button : buttons) {
            if (button == ReplyButton.SKIP_LINE) {
                builder.nextRow();
            } else {
                builder.addButton(button.getLabel(lang));
            }
        }
        return builder.resize().build();
    }


}
