package com.wildtigerrr.StoryOfCamelot.web.bot.update;

import com.wildtigerrr.StoryOfCamelot.bin.enums.Command;
import com.wildtigerrr.StoryOfCamelot.bin.service.StringUtils;
import com.wildtigerrr.StoryOfCamelot.exception.InvalidInputException;
import lombok.Getter;

import java.util.Arrays;

public class ParsedCommand {

    @Getter
    private final Command command;
    private final String[] params;

    public ParsedCommand(Command command, String text) {
        this.command = command;
        this.params = text.split(" ");
    }

    public int paramsCount() {
        return params.length;
    }

    public String paramByNum(int order) {
        if (paramsCount() > order) {
            return params[order];
        } else {
            return "";
        }
    }

    public int intByNum(int order) {
        String param = paramByNum(order);
        if (StringUtils.isNumeric(param)) {
            return Integer.parseInt(param);
        } else {
            throw new InvalidInputException("Parameter not numeric");
        }
    }

}
