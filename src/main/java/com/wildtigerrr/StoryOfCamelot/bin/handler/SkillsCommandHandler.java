package com.wildtigerrr.StoryOfCamelot.bin.handler;

import com.wildtigerrr.StoryOfCamelot.bin.base.service.KeyboardManager;
import com.wildtigerrr.StoryOfCamelot.bin.service.StringUtils;
import com.wildtigerrr.StoryOfCamelot.bin.translation.TranslationManager;
import com.wildtigerrr.StoryOfCamelot.database.jpa.schema.Player;
import com.wildtigerrr.StoryOfCamelot.database.jpa.schema.enums.Stats;
import com.wildtigerrr.StoryOfCamelot.database.jpa.service.template.PlayerService;
import com.wildtigerrr.StoryOfCamelot.web.bot.update.ParsedCommand;
import com.wildtigerrr.StoryOfCamelot.web.service.ResponseManager;
import com.wildtigerrr.StoryOfCamelot.web.service.message.IncomingMessage;
import com.wildtigerrr.StoryOfCamelot.web.service.message.template.EditResponseMessage;
import com.wildtigerrr.StoryOfCamelot.web.service.message.template.TextIncomingMessage;
import com.wildtigerrr.StoryOfCamelot.web.service.message.template.TextResponseMessage;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

@Service
@Log4j2
public class SkillsCommandHandler extends TextMessageHandler {

    /*
    First action - add skill (Gathering Resources, Hunt, Fishing)
    On each 10 skill levels - add new action or improve existing ?:
    Searching, 10 Level -> Choose Gathering vs Hunting
    Hunting, 10 Level -> Advanced Hunt (Wild Animals vs Cursed Creatures)
    Gathering, 10 Level -> Advanced Gathering (Rare Resources vs Resource Quantity)
     */

    private final PlayerService playerService;

    public SkillsCommandHandler(ResponseManager messages, TranslationManager translation, PlayerService playerService) {
        super(messages, translation);
        this.playerService = playerService;
    }

    @Override
    public void process(IncomingMessage message) {
        switch (message.getCommand()) {
            case SKILLS: processSkills(message); break;
            case UP: processStatUp((TextIncomingMessage) message); break;
        }
    }

    private void processSkills(IncomingMessage message) {
        messages.sendMessage(TextResponseMessage.builder().by(message)
                .text(message.getPlayer().getStatMenu(translation))
                .keyboard(KeyboardManager.getKeyboardForStatUp(message.getPlayer().stats().getUnassignedPoints())).build()
        );
    }

    private void processStatUp(TextIncomingMessage message) {
        ParsedCommand command = message.getParsedCommand();
        if (command.paramsCount() == 3
                && command.paramByNum(1).length() == 1
                && StringUtils.isNumeric(command.paramByNum(2))) {
            Stats stat = Stats.getStat(command.paramByNum(1));
            if (stat == null) {
                messages.sendMessage(TextResponseMessage.builder().by(message)
                        .text(translation.getMessage("player.stats.invalid", message)).build()
                );
            } else {
                Player player = message.getPlayer();
                String result = player.stats().raiseStat(stat, command.intByNum(2), player.getLanguage(), translation);
                if (!result.equals(translation.getMessage("player.stats.invalid", message))){
                    playerService.update(player);
                }
                messages.sendAnswer(message.getQueryId(), result);
                if (player.stats().getUnassignedPoints() == 0) {
                    messages.sendMessage(EditResponseMessage.builder().by(message)
                            .messageId(message).text(player.getStatMenu(translation)).build()
                    );
                } else {
                    messages.sendMessage(EditResponseMessage.builder().by(message)
                            .text(player.getStatMenu(translation))
                            .keyboard(KeyboardManager.getKeyboardForStatUp(player.stats().getUnassignedPoints())).build()
                    );
                }
            }
        } else {
            log.debug(message.text());
            log.debug(command.paramsCount());
            log.debug(command.paramByNum(1));
            log.debug(command.paramByNum(2));
            messages.sendMessage(TextResponseMessage.builder().by(message)
                    .text(translation.getMessage("commands.invalid", message)).build()
            );
        }
    }

}
