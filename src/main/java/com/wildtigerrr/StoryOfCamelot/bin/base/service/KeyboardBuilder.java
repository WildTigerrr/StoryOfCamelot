package com.wildtigerrr.StoryOfCamelot.bin.base.service;

import com.wildtigerrr.StoryOfCamelot.exception.InvalidKeyboardTypeException;
import io.netty.util.internal.StringUtil;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.ArrayList;
import java.util.List;

public class KeyboardBuilder<T extends ReplyKeyboard> {
    private int rowLimit = 100;
    private final Type type;

    private InlineKeyboardMarkup inlineKeyboard;
    private List<List<InlineKeyboardButton>> rowInlineList;
    private List<InlineKeyboardButton> buttonsInlineRow;

    private ReplyKeyboardMarkup replyKeyboard;
    private List<KeyboardRow> rowReplyList;
    private KeyboardRow buttonsReplyRow;

    private String pageCommandStart;
    private String pageCommandEnd;
    private int page;
    private boolean hasNext;
    private String separatorValue;

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

    public KeyboardBuilder<T> setRowLimit(int rowLimit) {
        this.rowLimit = rowLimit - 1;
        return this;
    }

    public KeyboardBuilder<T> addButton(InlineKeyboardButton button) {
        if (type != Type.INLINE) throw new InvalidKeyboardTypeException("Text button can't be added to " + type.name() + " keyboard");
        if (buttonsInlineRow.size() > rowLimit) nextRow();
        buttonsInlineRow.add(button);
        return this;
    }

    public KeyboardBuilder<T> addButton(String text, String callbackData) {
        return addButton(InlineKeyboardButton.builder().text(text).callbackData(callbackData).build());
    }

    public KeyboardBuilder<T> addButton(String buttonText) {
        if (type != Type.REPLY) throw new InvalidKeyboardTypeException("Text button can't be added to " + type.name() + " keyboard");
        if (buttonsReplyRow.size() > rowLimit) nextRow();
        buttonsReplyRow.add(buttonText);
        return this;
    }

    public KeyboardBuilder<T> nextRow() {
        if (type == Type.INLINE) {
            if (!buttonsInlineRow.isEmpty()) {
                this.rowInlineList.add(buttonsInlineRow);
                this.buttonsInlineRow = new ArrayList<>();
            }
        } else if (type == Type.REPLY) {
            if (buttonsReplyRow.size() > 0) {
                this.rowReplyList.add(buttonsReplyRow);
                this.buttonsReplyRow = new KeyboardRow();
            }
        }
        return this;
    }

    public KeyboardBuilder<T> resize() {
        if (type != Type.REPLY) throw new InvalidKeyboardTypeException("Resize can't be set to " + type.name() + " keyboard");
        replyKeyboard.setResizeKeyboard(true);
        return this;
    }

    public KeyboardBuilder<T> addPaginationRow(String pageCommandStart, String pageCommandEnd, int page, boolean hasNext) {
        this.pageCommandStart = pageCommandStart;
        this.pageCommandEnd = pageCommandEnd;
        this.page = page;
        this.hasNext = hasNext;
        return this;
    }

    public KeyboardBuilder<T> addPaginationRow(String pageCommandStart, String pageCommandEnd, int page, String separatorValue, boolean hasNext) {
        this.pageCommandStart = pageCommandStart;
        this.pageCommandEnd = pageCommandEnd;
        this.page = page;
        this.separatorValue = separatorValue;
        this.hasNext = hasNext;
        return this;
    }

    public T build() {
        if (type == Type.INLINE) {
            if (page > 0) addPaginationRow();
            nextRow();
            this.inlineKeyboard.setKeyboard(rowInlineList);
            return (T) this.inlineKeyboard;
        } else if (type == Type.REPLY) {
            nextRow();
            this.replyKeyboard.setKeyboard(rowReplyList);
            return (T) this.replyKeyboard;
        }
        throw new InvalidKeyboardTypeException("Wrong keyboard type: " + this.type.name());
    }

    private void addPaginationRow() {
        if (page > 1) {
            addButton("<", pageCommandStart + (page - 1) + pageCommandEnd);
        }
        if (!StringUtil.isNullOrEmpty(separatorValue)) {
            addButton(separatorValue, "/ignore");
        }
        if (hasNext) {
            addButton(">", pageCommandStart + (page + 1) + pageCommandEnd);
        }
    }

    public enum Type {
        INLINE,
        REPLY
    }

}
