package com.wildtigerrr.StoryOfCamelot.bin.base.service;

import com.wildtigerrr.StoryOfCamelot.bin.enums.Language;
import com.wildtigerrr.StoryOfCamelot.bin.translation.TranslationManager;
import com.wildtigerrr.StoryOfCamelot.web.service.ResponseManager;
import com.wildtigerrr.StoryOfCamelot.web.service.message.template.TextResponseMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LanguageService {

    private final ResponseManager messages;
    private final TranslationManager translation;

    @Autowired
    public LanguageService(ResponseManager messages, TranslationManager translation) {
        this.messages = messages;
        this.translation = translation;
    }

    public void sendLanguageSelector(String userId, Language lang) {
        messages.sendMessage(TextResponseMessage.builder()
                .text(translation.getMessage("tutorial.lang.choose", lang))
                .keyboard(KeyboardManager.getKeyboardForLanguageSelect())
                .targetId(userId).build()
        );
    }

}
