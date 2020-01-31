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
public class ImageResponseMessage implements ResponseMessage {

    @Builder.Default
    private final ResponseType type = ResponseType.PHOTO;
    @NonNull
    private final String targetId;
    private final String caption;

    private final File file;
    private final String fileId;
    private final String fileName;
    private final InputStream inputStream;

}
