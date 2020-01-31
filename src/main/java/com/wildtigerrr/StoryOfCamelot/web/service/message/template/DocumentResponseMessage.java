package com.wildtigerrr.StoryOfCamelot.web.service.message.template;

import com.wildtigerrr.StoryOfCamelot.web.service.ResponseType;
import com.wildtigerrr.StoryOfCamelot.web.service.message.ResponseMessage;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;

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

    @Override
    public String getText() {
        return "Document without text";
    }

    @Override
    public boolean isApplyMarkup() {
        return false;
    }

}
