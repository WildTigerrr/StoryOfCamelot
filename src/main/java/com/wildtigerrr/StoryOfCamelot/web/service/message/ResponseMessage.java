package com.wildtigerrr.StoryOfCamelot.web.service.message;

import com.wildtigerrr.StoryOfCamelot.web.service.ResponseType;

public interface ResponseMessage {

    ResponseType getType();
    String getText();
    Boolean isApplyMarkup();

}
