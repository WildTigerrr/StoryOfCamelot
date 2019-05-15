package com.wildtigerrr.StoryOfCamelot.bin;

import com.wildtigerrr.StoryOfCamelot.bin.enums.Language;
import com.wildtigerrr.StoryOfCamelot.bin.enums.ReplyButton;
import com.wildtigerrr.StoryOfCamelot.database.schema.Location;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.ArrayList;
import java.util.List;

@Component
public class KeyboardManager {

    public static InlineKeyboardMarkup getKeyboardForLanguageSelect() {
        InlineKeyboardMarkup keyboard = new InlineKeyboardMarkup();
        InlineKeyboardButton button;
        List<InlineKeyboardButton> buttonsRow = new ArrayList<>();
        List<List<InlineKeyboardButton>> rowList = new ArrayList<>();
        int buttonsCounter = 0;
        for (Language lang : Language.values()) {
            buttonsCounter++;
            if (buttonsCounter > 3) {
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

    public static InlineKeyboardMarkup getKeyboardForLocations(ArrayList<Location> nearLocations) {
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
            button.setText(loc.getName());
            button.setCallbackData("/move " + loc.getId());
            buttonsRow.add(button);
        }
        rowList.add(buttonsRow);
        keyboard.setKeyboard(rowList);
        return keyboard;
    }

    public static ReplyKeyboardMarkup getReplyByButtons(ArrayList<ReplyButton> buttons) {
        ReplyKeyboardMarkup keyboard = new ReplyKeyboardMarkup();
        List<KeyboardRow> keyboardMarkup = new ArrayList<>();
        KeyboardRow keyboardRow = new KeyboardRow();
        for (ReplyButton button : buttons) {
            keyboardRow.add(button.getLabel());
        }
        keyboardMarkup.add(keyboardRow);
        keyboard.setKeyboard(keyboardMarkup);
        keyboard.setResizeKeyboard(true);
        return keyboard;
    }


}
