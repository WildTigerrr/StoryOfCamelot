package com.wildtigerrr.StoryOfCamelot.bin.base.service;

import com.wildtigerrr.StoryOfCamelot.bin.enums.Language;
import com.wildtigerrr.StoryOfCamelot.bin.enums.ReplyButton;
import com.wildtigerrr.StoryOfCamelot.database.jpa.schema.Location;
import com.wildtigerrr.StoryOfCamelot.database.jpa.schema.enums.Stats;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.ArrayList;
import java.util.List;

@Component
public class KeyboardManager {

    public static InlineKeyboardMarkup getKeyboardForStatUp(int freePoints) {
        if (freePoints < 1) return null;
        ArrayList<String> addings = new ArrayList<>();
        addings.add("1");
        if (freePoints > 1) {
            if (freePoints > 4) {
                addings.add("5");
                if (freePoints > 24) {
                    addings.add("25");
                }
            }
            if (freePoints != 5 && freePoints != 25) {
                addings.add("" + freePoints);
            }
        }

        int buttonsLine = addings.size() != 1 ? addings.size() : 3;
        KeyboardBuilder builder = new KeyboardBuilder(KeyboardBuilder.Type.INLINE, buttonsLine);
        for (Stats stat : Stats.values()) {
            for (String val : addings) {
                builder.addButton(
                        new InlineKeyboardButton()
                                .setText(stat.emoji() + "+" + val)
                                .setCallbackData("/up_" + stat.getCharacter().toLowerCase() + "_" + val)
                );
            }
        }
        return (InlineKeyboardMarkup) builder.build();
    }

    public static InlineKeyboardMarkup getKeyboardForLanguageSelect() {
        KeyboardBuilder builder = new KeyboardBuilder(KeyboardBuilder.Type.INLINE, 2);
        for (Language lang : Language.values()) {
            builder.addButton(
                    new InlineKeyboardButton()
                            .setText(lang.getName())
                            .setCallbackData("/lang " + lang.ordinal())
            );
        }
        return (InlineKeyboardMarkup) builder.build();
    }

    public static InlineKeyboardMarkup getKeyboardForLocations(ArrayList<Location> nearLocations, Language lang) {
        KeyboardBuilder builder = new KeyboardBuilder(KeyboardBuilder.Type.INLINE, 2);
        for (Location loc : nearLocations) {
            builder.addButton(
                    new InlineKeyboardButton()
                            .setText(loc.getName(lang))
                            .setCallbackData("/move " + loc.getId())
            );
        }
        return (InlineKeyboardMarkup) builder.build();
    }

    public static ReplyKeyboardMarkup getReplyByButtons(List<ReplyButton> buttons, Language lang) {
        KeyboardBuilder builder = new KeyboardBuilder(KeyboardBuilder.Type.REPLY, 2);
        for (ReplyButton button : buttons) {
            builder.addButton(button.getLabel(lang));
        }
        return (ReplyKeyboardMarkup) builder.resize().build();
    }


}
