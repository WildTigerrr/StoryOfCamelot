package com.wildtigerrr.StoryOfCamelot.bin.base.service;

import com.wildtigerrr.StoryOfCamelot.bin.enums.Language;
import com.wildtigerrr.StoryOfCamelot.bin.enums.ReplyButton;
import com.wildtigerrr.StoryOfCamelot.database.schema.Location;
import com.wildtigerrr.StoryOfCamelot.database.schema.enums.Stats;
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
        InlineKeyboardMarkup keyboard = new InlineKeyboardMarkup();
        InlineKeyboardButton button;
        List<InlineKeyboardButton> buttonsRow = new ArrayList<>();
        List<List<InlineKeyboardButton>> rowList = new ArrayList<>();

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
        int buttonsLine = 3;
        if (addings.size() != 1) {
            buttonsLine = addings.size();
        }
        int buttonsCounter = 0;
        for (Stats stat : Stats.values()) {
            for (String val : addings) {
                buttonsCounter++;
                if (buttonsCounter > buttonsLine) {
                    rowList.add(buttonsRow);
                    buttonsRow = new ArrayList<>();
                    buttonsCounter = 1;
                }
                button = new InlineKeyboardButton();
                button.setText(stat.emoji() + "+" + val);
                button.setCallbackData("/up_" + stat.getCharacter().toLowerCase() + "_" + val);
                buttonsRow.add(button);
            }
        }
        rowList.add(buttonsRow);
        keyboard.setKeyboard(rowList);
        return keyboard;
    }

    public static InlineKeyboardMarkup getKeyboardForLanguageSelect() {
        InlineKeyboardMarkup keyboard = new InlineKeyboardMarkup();
        InlineKeyboardButton button;
        List<InlineKeyboardButton> buttonsRow = new ArrayList<>();
        List<List<InlineKeyboardButton>> rowList = new ArrayList<>();
        int buttonsCounter = 0;
        for (Language lang : Language.values()) {
            buttonsCounter++;
            if (buttonsCounter > 2) {
                rowList.add(buttonsRow);
                buttonsRow = new ArrayList<>();
                buttonsCounter = 1;
            }
            button = new InlineKeyboardButton();
            button.setText(lang.getName());
            button.setCallbackData("/lang " + lang.ordinal());
            buttonsRow.add(button);
        }
        rowList.add(buttonsRow);
        keyboard.setKeyboard(rowList);
        return keyboard;
    }

    public static InlineKeyboardMarkup getKeyboardForLocations(ArrayList<Location> nearLocations, Language lang) {
        InlineKeyboardMarkup keyboard = new InlineKeyboardMarkup();
        InlineKeyboardButton button;
        List<InlineKeyboardButton> buttonsRow = new ArrayList<>();
        List<List<InlineKeyboardButton>> rowList = new ArrayList<>();
        int buttonsCounter = 0;
        for (Location loc : nearLocations) {
            buttonsCounter++;
            if (buttonsCounter > 2) {
                rowList.add(buttonsRow);
                buttonsRow = new ArrayList<>();
                buttonsCounter = 1;
            }
            button = new InlineKeyboardButton();
            button.setText(loc.getName(lang));
            button.setCallbackData("/move " + loc.getId());
            buttonsRow.add(button);
        }
        rowList.add(buttonsRow);
        keyboard.setKeyboard(rowList);
        return keyboard;
    }

    public static ReplyKeyboardMarkup getReplyByButtons(List<ReplyButton> buttons, Language lang) {
        ReplyKeyboardMarkup keyboard = new ReplyKeyboardMarkup();
        List<KeyboardRow> keyboardMarkup = new ArrayList<>();
        KeyboardRow keyboardRow = new KeyboardRow();
        for (ReplyButton button : buttons) {
            keyboardRow.add(button.getLabel(lang));
        }
        keyboardMarkup.add(keyboardRow);
        keyboard.setKeyboard(keyboardMarkup);
        keyboard.setResizeKeyboard(true);
        return keyboard;
    }


}
