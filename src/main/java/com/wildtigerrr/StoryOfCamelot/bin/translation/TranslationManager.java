package com.wildtigerrr.StoryOfCamelot.bin.translation;

import com.wildtigerrr.StoryOfCamelot.bin.enums.Emoji;
import com.wildtigerrr.StoryOfCamelot.bin.enums.Language;
import com.wildtigerrr.StoryOfCamelot.bin.enums.RandomDistribution;
import com.wildtigerrr.StoryOfCamelot.bin.service.ApplicationContextProvider;
import com.wildtigerrr.StoryOfCamelot.database.jpa.schema.Player;
import com.wildtigerrr.StoryOfCamelot.exception.InvalidPropertyException;
import com.wildtigerrr.StoryOfCamelot.web.bot.update.UpdateWrapper;
import com.wildtigerrr.StoryOfCamelot.web.service.message.IncomingMessage;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import org.springframework.stereotype.Service;

import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Log4j2
@Service
public class TranslationManager {

    private final MessageSource messageSource;

    @Autowired
    public TranslationManager(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    public String getMessage(String code, Locale locale, Object[] args) {
        String message;
        try {
            message = messageSource.getMessage(code, args, locale);
            if (message.startsWith("*random:")) message = getRandomMessage(message, code, locale, args);
        } catch (NoSuchMessageException e) {
            throw new InvalidPropertyException("Attempt to access invalid message code: " + code);
        }
        return applyEmoji(message);
    }

    public String getMessage(String code, Locale locale) {
        return getMessage(code, locale, null);
    }

    public String getMessage(String code) {
        return getMessage(code, Language.getDefaultLocale());
    }

    public String getMessage(String code, Language lang, Object[] args) {
        return getMessage(code, lang.getLocale(), args);
    }

    public String getMessage(String code, Language lang) {
        return getMessage(code, lang.getLocale());
    }

    public String getMessage(String code, Player player, Object[] args) {
        return getMessage(code, player.getLanguage(), args);
    }

    public String getMessage(String code, Player player) {
        return getMessage(code, player.getLanguage());
    }

    public String getMessage(String code, UpdateWrapper message, Object[] args) {
        return getMessage(code, message.getPlayer(), args);
    }

    public String getMessage(String code, UpdateWrapper message) {
        return getMessage(code, message.getPlayer());
    }

    public String getMessage(String code, IncomingMessage message, Object[] args) {
        return getMessage(code, message.getPlayer(), args);
    }

    public String getMessage(String code, IncomingMessage message) {return getMessage(code, message.getPlayer());}

    private String getRandomMessage(String randomValue, String code, Locale locale, Object[] args) {
        if (ApplicationContextProvider.isRunningTest()) return getTestMessage(code, locale, args);
        return messageSource.getMessage(code + "." + (RandomDistribution.DISCRETE.nextInt(Integer.parseInt(randomValue.substring(8))) + 1), args, locale);
    }

    private String getTestMessage(String code, Locale locale, Object[] args) {
        return messageSource.getMessage(code + "." + 1, args, locale);
    }

    private String applyEmoji(String message) {
        if (!message.contains("[emj:")) {
            return message;
        }
        Pattern pattern = Pattern.compile("\\[emj:(.*?)]");
        Matcher matcher = pattern.matcher(message);
        if (matcher.find()) {
            message = matcher.replaceAll(matchResult -> getEmojiByName(matchResult.group(1)));
        }
        return message;
    }

    private String getEmojiByName(String name) {
        return Emoji.valueOf(name).getCode();
    }

}
