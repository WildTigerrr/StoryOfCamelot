package com.wildtigerrr.StoryOfCamelot.web.service;

import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;

import java.io.File;
import java.io.InputStream;

@Builder
@Getter
public class ResponseMessage {

    @NonNull
    private final ResponseType type;
    private final String text;
    private final boolean applyMarkup;
    private final ReplyKeyboard keyboard;
    @NonNull
    private final String targetId;
    private final ResponseFile file;

    private ResponseMessage(ResponseType type, String text, boolean applyMarkup, ReplyKeyboard keyboard, String targetId, ResponseFile file) {
        this.type = type;
        this.text = text;
        this.applyMarkup = applyMarkup;
        this.keyboard = keyboard;
        this.targetId = targetId;
        this.file = file;
    }

    @Builder
    @Getter
    public class ResponseFile {
        private final File file;
        private final String fileId;
        private final String fileName;
        private final InputStream inputStream;
    }

}
