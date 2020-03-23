package com.wildtigerrr.StoryOfCamelot.bin.base.service;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.ArrayList;
import java.util.List;

public class KeyboardBuilder {
    private int rowLimit = 100;
    private Type type;

    private InlineKeyboardMarkup inlineKeyboard;
    private List<List<InlineKeyboardButton>> rowInlineList;
    private List<InlineKeyboardButton> buttonsInlineRow;

    private ReplyKeyboardMarkup replyKeyboard;
    private List<KeyboardRow> rowReplyList;
    private KeyboardRow buttonsReplyRow;

    public KeyboardBuilder(Type type) {
        this.type = type;
        if (type == Type.INLINE) {
            this.inlineKeyboard = new InlineKeyboardMarkup();
            this.rowInlineList = new ArrayList<>();
            this.buttonsInlineRow = new ArrayList<>();
        } else if (type == Type.REPLY) {
            this.replyKeyboard = new ReplyKeyboardMarkup();
            this.rowReplyList = new ArrayList<>();
            this.buttonsReplyRow = new KeyboardRow();
        }
    }

    public KeyboardBuilder(Type type, int rowLimit) {
        this(type);
        this.rowLimit = rowLimit - 1;
    }

    public KeyboardBuilder setRowLimit(int rowLimit) {
        this.rowLimit = rowLimit;
        return this;
    }

    public KeyboardBuilder addButton(InlineKeyboardButton button) {
        if (type != Type.INLINE) throw new InvalidKeyboardTypeException("Text button can't be added to " + type.name() + " keyboard");
        if (buttonsInlineRow.size() > rowLimit) nextRow();
        buttonsInlineRow.add(button);
        return this;
    }

    public KeyboardBuilder addButton(String buttonText) {
        if (type != Type.REPLY) throw new InvalidKeyboardTypeException("Text button can't be added to " + type.name() + " keyboard");
        if (buttonsReplyRow.size() > rowLimit) nextRow();
        buttonsReplyRow.add(buttonText);
        return this;
    }

    public KeyboardBuilder nextRow() {
        if (type == Type.INLINE) {
            this.rowInlineList.add(buttonsInlineRow);
            this.buttonsInlineRow = new ArrayList<>();
        } else if (type == Type.REPLY) {
            this.rowReplyList.add(buttonsReplyRow);
            this.buttonsReplyRow = new KeyboardRow();
        }
        return this;
    }

    public Object build() {
        if (type == Type.INLINE) {
            nextRow();
            this.inlineKeyboard.setKeyboard(rowInlineList);
            return this.inlineKeyboard;
        } else if (type == Type.REPLY) {
            nextRow();
            this.replyKeyboard.setKeyboard(rowReplyList);
            return this.replyKeyboard;
        }
        throw new InvalidKeyboardTypeException("Wrong keyboard type: " + this.type.name());
    }

    public enum Type {
        INLINE,
        REPLY
    }

    public class InvalidKeyboardTypeException extends RuntimeException {
        public InvalidKeyboardTypeException(String message) {
            super(message);
        }
    }
}
