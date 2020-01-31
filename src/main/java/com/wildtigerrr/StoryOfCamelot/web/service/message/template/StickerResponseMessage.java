package com.wildtigerrr.StoryOfCamelot.web.service.message.template;

import com.wildtigerrr.StoryOfCamelot.web.service.ResponseType;
import com.wildtigerrr.StoryOfCamelot.web.service.message.ResponseMessage;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;

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

    @Override
    public String getText() {
        return "Sticker without text";
    }

    @Override
    public boolean isApplyMarkup() {
        return false;
    }

}
