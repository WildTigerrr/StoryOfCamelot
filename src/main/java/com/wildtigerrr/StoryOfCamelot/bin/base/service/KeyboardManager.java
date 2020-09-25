package com.wildtigerrr.StoryOfCamelot.bin.base.service;

import com.wildtigerrr.StoryOfCamelot.bin.enums.Language;
import com.wildtigerrr.StoryOfCamelot.bin.enums.ReplyButton;
import com.wildtigerrr.StoryOfCamelot.bin.translation.TranslationManager;
import com.wildtigerrr.StoryOfCamelot.database.jpa.schema.Backpack;
import com.wildtigerrr.StoryOfCamelot.database.jpa.schema.BackpackItem;
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

    public static InlineKeyboardMarkup getKeyboardForBackpack(Backpack backpack, int page, TranslationManager translation) {
        int pageSize = 10;
        KeyboardBuilder<InlineKeyboardMarkup> builder = new KeyboardBuilder<>(KeyboardBuilder.Type.INLINE);
        List<BackpackItem> items = backpack.getItems();
        if (items.isEmpty() || items.size() < pageSize * (page - 1)) {
            return null;
        }
        BackpackItem item;
        for (int i = pageSize * (page - 1); i < Math.min(pageSize * page, items.size() - pageSize * (page - 1)); i++) {
            item = items.get(i);
            builder.addButton(new InlineKeyboardButton()
                    .setText(item.backpackInfo(translation))
                    .setCallbackData("/backpack " + page + " item_info " + item.getId())
            );
            if (item.getItem().isEquippable()) {
                builder.addButton(new InlineKeyboardButton()
                        .setText(item.isEquipped() ? "Снять" : "Надеть")
                        .setCallbackData(item.isEquipped()
                                ? "/backpack " + page + " item_unequip " + item.getId()
                                : "/backpack " + page + " item_equip " + item.getId())
                );
            }
            builder.nextRow();
        }
        if (page > 1) {
            builder.addButton(new InlineKeyboardButton()
                    .setText("<")
                    .setCallbackData("/backpack page " + (page - 1))
            );
        }
        if (items.size() > pageSize * page) {
            builder.addButton(new InlineKeyboardButton()
                    .setText(">")
                    .setCallbackData("/backpack page " + (page - 1))
            );
        }
        return builder.build();
    }

}
