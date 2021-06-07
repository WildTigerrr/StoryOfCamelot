package com.wildtigerrr.StoryOfCamelot.web.service.message.template;

import com.wildtigerrr.StoryOfCamelot.bin.enums.Language;
import com.wildtigerrr.StoryOfCamelot.web.service.ResponseType;
import com.wildtigerrr.StoryOfCamelot.web.service.message.ResponseMessage;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import org.telegram.telegrambots.meta.api.objects.InputFile;

import java.io.File;

@Builder
@Getter
public class DocumentResponseMessage implements ResponseMessage {

    @Builder.Default
    private final ResponseType type = ResponseType.DOCUMENT;
    @NonNull
    private final String targetId;
    @NonNull
    private final File file;
    @NonNull
    private final Language lang;
    @Builder.Default
    private final boolean disableNotification = false;

    @Override
    public Language getLanguage() {
        return lang;
    }

    @Override
    public String getText() {
        return "Document without text";
    }

    @Override
    public boolean isApplyMarkup() {
        return false;
    }

    public InputFile getInputFile() {
        return new InputFile(getFile());
    }

}
