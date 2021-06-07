package com.wildtigerrr.StoryOfCamelot.web.service.message.template;

import com.wildtigerrr.StoryOfCamelot.bin.enums.Language;
import com.wildtigerrr.StoryOfCamelot.web.service.ResponseType;
import com.wildtigerrr.StoryOfCamelot.web.service.message.ResponseMessage;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import org.telegram.telegrambots.meta.api.objects.InputFile;

import java.io.File;
import java.io.InputStream;

@Builder
@Getter
public class StickerResponseMessage implements ResponseMessage {

    @Builder.Default
    private final ResponseType type = ResponseType.STICKER;
    @NonNull
    private final String targetId;

    private final File file;
    private final String fileId;
    private final String fileName;
    private final InputStream inputStream;
    @Builder.Default
    private final boolean disableNotification = false;
    @NonNull
    private final Language lang;

    @Override
    public Language getLanguage() {
        return lang;
    }

    @Override
    public String getText() {
        return "Sticker without text";
    }

    @Override
    public boolean isApplyMarkup() {
        return false;
    }

    public InputFile getInputFile() {
        if (getFile() != null) {
            return new InputFile(getFile());
        } else if (getInputStream() != null) {
            return new InputFile(getInputStream(), getFileName());
        } else if (getFileId() != null) {
            return new InputFile(getFileId());
        }
        return new InputFile();
    }

}
