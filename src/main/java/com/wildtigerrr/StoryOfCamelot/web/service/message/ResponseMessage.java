package com.wildtigerrr.StoryOfCamelot.web.service.message;

import com.wildtigerrr.StoryOfCamelot.bin.enums.Language;
import com.wildtigerrr.StoryOfCamelot.web.service.ResponseType;

public interface ResponseMessage {

    Language getLanguage();
    String getTargetId();
    ResponseType getType();
    String getText();
    boolean isApplyMarkup();

}
