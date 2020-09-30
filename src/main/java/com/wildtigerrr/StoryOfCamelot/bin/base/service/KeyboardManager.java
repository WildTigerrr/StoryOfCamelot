package com.wildtigerrr.StoryOfCamelot.bin.base.service;

import com.wildtigerrr.StoryOfCamelot.bin.enums.Language;
import com.wildtigerrr.StoryOfCamelot.bin.enums.ReplyButton;
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

    public static ReplyKeyboardMarkup getReplyByStores(Set<Store> stores, Language lang) {
        KeyboardBuilder<ReplyKeyboardMarkup> builder = new KeyboardBuilder<>(KeyboardBuilder.Type.REPLY, 2);
        for (Store store : stores) {
            builder.addButton(store.getLabel(lang));
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
        String equip = translation.getMessage("player.backpack.item-equip", backpack.getPlayer());
        String unequip = translation.getMessage("player.backpack.item-unequip", backpack.getPlayer());
        for (int i = pageSize * (page - 1); i < Math.min(pageSize * page, items.size() - pageSize * (page - 1)); i++) {
            item = items.get(i);
            builder.addButton(new InlineKeyboardButton()
                    .setText(item.backpackInfo(translation))
                    .setCallbackData("/backpack " + page + " item_info " + item.getId())
            );
            if (item.getItem().isEquippable()) {
                builder.addButton(new InlineKeyboardButton()
                        .setText(item.isEquipped() ? equip : unequip)
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
                    .setCallbackData("/backpack " + (page - 1) + " page")
            );
        }
        if (items.size() > pageSize * page) {
            builder.addButton(new InlineKeyboardButton()
                    .setText(">")
                    .setCallbackData("/backpack " + (page + 1) + " page")
            );
        }
        return builder.build();
    }

    public static InlineKeyboardMarkup getKeyboardForStoreItems(Store store, List<Item> items, int page, Language lang, TranslationManager translation) {
        int pageSize = 10;
        KeyboardBuilder<InlineKeyboardMarkup> builder = new KeyboardBuilder<>(KeyboardBuilder.Type.INLINE);
        if (items.isEmpty() || items.size() < pageSize * (page - 1)) {
            return null;
        }
        Item item;
        log.debug("Items:");
        log.debug(items.size());
        log.debug("Start from " + (pageSize * (page - 1)));
        log.debug("To " + (Math.min(pageSize * page, items.size() - pageSize * (page - 1))));
        for (int i = pageSize * (page - 1); i < Math.min(pageSize * page, items.size() - pageSize * (page - 1)); i++) {
            log.debug("Item #:" + i);
            item = items.get(i);
            builder.addButton(new InlineKeyboardButton()
                    .setText(item.getName(lang))
                    .setCallbackData("/store " + store.getId() + page + " item_info " + item.getId())
            );
            builder.addButton(new InlineKeyboardButton()
                    .setText(MoneyCalculation.moneyOf(item.getPrice(), lang))
                    .setCallbackData("/store " + store.getId()  + page + " item_buy " + item.getId())
            );
            builder.nextRow();
        }
        if (page > 1) {
            builder.addButton(new InlineKeyboardButton()
                    .setText("<")
                    .setCallbackData("/store " + store.getId() + " " + (page - 1) + " page")
            );
        }
        if (items.size() > pageSize * page) {
            builder.addButton(new InlineKeyboardButton()
                    .setText(">")
                    .setCallbackData("/store " + store.getId() + " " + (page + 1) + " page")
            );
        }
        return builder.build();
    }

}
