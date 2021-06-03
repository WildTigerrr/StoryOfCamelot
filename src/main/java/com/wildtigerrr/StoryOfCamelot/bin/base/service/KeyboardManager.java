package com.wildtigerrr.StoryOfCamelot.bin.base.service;

import com.wildtigerrr.StoryOfCamelot.bin.enums.Language;
import com.wildtigerrr.StoryOfCamelot.bin.enums.ReplyButton;
import com.wildtigerrr.StoryOfCamelot.bin.enums.Skill;
import com.wildtigerrr.StoryOfCamelot.bin.translation.TranslationManager;
import com.wildtigerrr.StoryOfCamelot.database.jpa.schema.*;
import com.wildtigerrr.StoryOfCamelot.database.jpa.schema.enums.Stats;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Component
@Log4j2
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
                                .setCallbackData("/up " + stat.getCharacter().toLowerCase() + " " + val)
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
            if (button.isSkip()) {
                builder.nextRow();
            } else {
                builder.addButton(button.getLabel(lang));
            }
        }
        return builder.resize().build();
    }

    public static ReplyKeyboardMarkup getReplyByStores(Set<Store> stores, Language lang) {
        KeyboardBuilder<ReplyKeyboardMarkup> builder = new KeyboardBuilder<>(KeyboardBuilder.Type.REPLY, 2);
        builder.addButton(ReplyButton.BACK.getLabel(lang))
                .nextRow();
        for (Store store : stores) {
            builder.addButton(store.getLabel(lang));
        }
        return builder.resize().build();
    }

    public static InlineKeyboardMarkup getKeyboardForBackpack(Backpack backpack, int page, TranslationManager translation) {
        int pageSize = 10;
        List<BackpackItem> items = backpack.getItems();
        if (items.isEmpty() || items.size() < pageSize * (page - 1)) {
            return null;
        }
        KeyboardBuilder<InlineKeyboardMarkup> builder = new KeyboardBuilder<>(KeyboardBuilder.Type.INLINE);
        BackpackItem item;
        String equip = translation.getMessage("player.backpack.item-equip", backpack.getPlayer());
        String unequip = translation.getMessage("player.backpack.item-unequip", backpack.getPlayer());
        for (int i = pageSize * (page - 1); i < Math.min(pageSize * page, items.size()); i++) {
            item = items.get(i);
            builder.addButton(new InlineKeyboardButton()
                    .setText(item.backpackInfo(translation))
                    .setCallbackData("/backpack " + page + " item_info " + item.getId())
            );
            if (item.getItem().isEquippable()) {
                builder.addButton(new InlineKeyboardButton()
                        .setText(item.isEquipped() ? unequip : equip)
                        .setCallbackData(item.isEquipped()
                                ? "/backpack " + page + " item_unequip " + item.getId()
                                : "/backpack " + page + " item_equip " + item.getId())
                );
            }
            builder.nextRow();
        }
        return builder.addPaginationRow("/backpack ", " page", page, items.size() > pageSize * page)
                .build();
    }

    public static InlineKeyboardMarkup getKeyboardForBackpackSell(Store store, Backpack backpack, int page, TranslationManager translation) {
        int pageSize = 10;
        List<BackpackItem> items = backpack.getItems();
        if (items.isEmpty() || items.size() < pageSize * (page - 1)) {
            return null;
        }
        KeyboardBuilder<InlineKeyboardMarkup> builder = new KeyboardBuilder<>(KeyboardBuilder.Type.INLINE);
        BackpackItem item;
        for (int i = pageSize * (page - 1); i < Math.min(pageSize * page, items.size()); i++) {
            item = items.get(i);
            builder.addButton(new InlineKeyboardButton()
                    .setText("Sell " + item.backpackInfo(translation) + (item.isEquipped() ? " (Equipped)" : "") +
                            MoneyCalculation.moneyOf(item.getSalePrice(), backpack.getPlayer().getLanguage(), translation))
                    .setCallbackData("/store " + store.getId() + " " + page + " item_sell " + item.getId())
            ).nextRow();
        }
        return builder.addPaginationRow("/store " + store.getId() + " ", " page_sell", page, items.size() > pageSize * page)
                .build();
    }

    public static InlineKeyboardMarkup getKeyboardForStoreItems(Store store, List<Item> items, int page, Language lang, TranslationManager translation) {
        int pageSize = 10;
        KeyboardBuilder<InlineKeyboardMarkup> builder = new KeyboardBuilder<>(KeyboardBuilder.Type.INLINE);
        if (items.isEmpty() || items.size() < pageSize * (page - 1)) {
            return null;
        }
        Item item;
        for (int i = pageSize * (page - 1); i < Math.min(pageSize * page, items.size()); i++) {
            item = items.get(i);
            builder.addButton(new InlineKeyboardButton()
                    .setText(item.getName(lang))
                    .setCallbackData("/store " + store.getId() + " " + page + " item_info " + item.getId())
            ).addButton(new InlineKeyboardButton()
                    .setText(MoneyCalculation.moneyOf(item.getPrice(), lang, translation))
                    .setCallbackData("/store " + store.getId() + " " + page + " item_buy " + item.getId())
            ).nextRow();
        }
        return builder.addPaginationRow("/store " + store.getId() + " ", " page", page,
                page + " / " + ((int) Math.ceil(((double) items.size()) / pageSize)), items.size() > pageSize * page)
                .addButton(new InlineKeyboardButton()
                        .setText("Продажа")
                        .setCallbackData("/store " + store.getId() + " 1 page_sell"))
                .build();
    }

    public static InlineKeyboardMarkup getSkillLearnKeyboard(List<Skill> skills, int page, Language lang) {
        int pageSize = 10;
        if (skills.isEmpty() || skills.size() < pageSize * (page - 1)) {
            return null;
        }
        KeyboardBuilder<InlineKeyboardMarkup> builder = new KeyboardBuilder<>(KeyboardBuilder.Type.INLINE, 2);
        Skill skill;
        for (int i = pageSize * (page - 1); i < Math.min(pageSize * page, skills.size()); i++) {
            skill = skills.get(i);
            builder.addButton(ReplyButton.skillToButton(skill).getLabel(lang), "/new_skill " + page + " skill_info " + skill.name())
                    .addButton("Learn", "/new_skill " + page + " skill_learn " + skill.name());
        }
        if (skills.size() > pageSize) {
            builder.addPaginationRow("/new_skill ", " page", page,
                    page + " / " + ((int) Math.ceil(((double) skills.size()) / pageSize)), skills.size() > pageSize * page);
        }
        return builder.build();
    }

}
