package com.wildtigerrr.StoryOfCamelot.bin.handler;

import com.google.common.collect.Lists;
import com.wildtigerrr.StoryOfCamelot.bin.base.service.KeyboardManager;
import com.wildtigerrr.StoryOfCamelot.bin.enums.Skill;
import com.wildtigerrr.StoryOfCamelot.bin.service.StringUtils;
import com.wildtigerrr.StoryOfCamelot.bin.translation.TranslationManager;
import com.wildtigerrr.StoryOfCamelot.database.jpa.schema.Player;
import com.wildtigerrr.StoryOfCamelot.database.jpa.schema.enums.Stats;
import com.wildtigerrr.StoryOfCamelot.database.jpa.service.implementation.PlayerServiceImpl;
import com.wildtigerrr.StoryOfCamelot.database.jpa.service.template.PlayerService;
import com.wildtigerrr.StoryOfCamelot.web.bot.update.ParsedCommand;
import com.wildtigerrr.StoryOfCamelot.web.service.ResponseManager;
import com.wildtigerrr.StoryOfCamelot.web.service.message.IncomingMessage;
import com.wildtigerrr.StoryOfCamelot.web.service.message.template.EditResponseMessage;
import com.wildtigerrr.StoryOfCamelot.web.service.message.template.TextIncomingMessage;
import com.wildtigerrr.StoryOfCamelot.web.service.message.template.TextResponseMessage;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.util.Set;

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
            case SKILL_LEARN: processNewSkillChoose(message, 1); break;
            case NEW_SKILL: processNewSkill((TextIncomingMessage) message); break;
            case UP: processStatUp((TextIncomingMessage) message); break;
        }
    }

    /** Send skill choose window/page on level up
     *
     * @param message "/new_skill " + page + " page" || Command.SKILL_LEARN (Level Up)
     * @param page Page number
     */
    private void processNewSkillChoose(IncomingMessage message, int page) {
        Set<Skill> availableSkills = Skill.getAvailableSkills(message.getPlayer());
        availableSkills.removeAll(message.getPlayer().getSkills());
        messages.sendMessage(TextResponseMessage.builder().by(message)
                .text(translation.getMessage("player.stats.level-up-choose", message))
                .keyboard(KeyboardManager.getSkillLearnKeyboard(Lists.newArrayList(availableSkills), page, message.getPlayer().getLanguage())).build()
        );
    }

    /** Process skill choose window commands
     *
     * @param message
     *                Change page: "/new_skill " + page + " page"
     *                Skill description: "/new_skill " + page + " skill_info " + skill.name()
     *                Learn skill: "/new_skill " + page + " skill_learn " + skill.name()
     */
    private void processNewSkill(TextIncomingMessage message) {
        ParsedCommand command = message.getParsedCommand();
        if (command.paramsCount() > 2) {
            switch (command.paramByNum(2)) {
                case "page": processNewSkillChoose(message, command.intByNum(1)); break;
                case "skill_info": sendSkillInfo(message); break;
                case "skill_learn": learnNewSkill(message); break;
                default: {
                    log.error(command.paramByNum(2));
                    messages.postMessageToAdminChannel("Wrong parameters for NEW_SKILL: " + message.text());
                }
            }
        } else {
            log.error("Wrong parameters quantity: " + message.text());
            messages.postMessageToAdminChannel("Wrong parameters quantity for NEW_SKILL: " + message.text());
        }
    }

    /** Send skill description, if available. Send answer, if not
     *
     * @param message "/new_skill " + page + " skill_info " + skill.name()
     */
    private void sendSkillInfo(TextIncomingMessage message) {
        Skill newSkill = availableSkill(message);
        if (newSkill == null) return;
        String description = newSkill.getDescription(message.getPlayer().getLanguage());
        if (description.isBlank()) {
            messages.sendAnswer(message.getQueryId());
            return;
        }
        messages.sendMessage(TextResponseMessage.builder().by(message)
                .text(description)
                .build()
        );
        messages.sendAnswer(message.getQueryId());
    }

    /** Learn new skill
     *
     * @param message "/new_skill " + page + " skill_learn " + skill.name()
     */
    private void learnNewSkill(TextIncomingMessage message) {
        Skill newSkill;
        if ((newSkill = availableSkill(message)) == null) {
            messages.sendAnswer(message.getQueryId(), translation.getMessage("player.stats.skill-not-available", message));
            return;
        }
        Player player = message.getPlayer();
        player.getSkills().add(newSkill);
        playerService.update(player);
        messages.sendMessage(TextResponseMessage.builder().by(message)
                .text(translation.getMessage("player.stats.skill-learned", message))
                .build()
        );
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
                    PlayerServiceImpl.enableRegeneration(player);
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
            messages.sendMessage(TextResponseMessage.builder().by(message)
                    .text(translation.getMessage("commands.invalid", message)).build()
            );
        }
    }

    /** Get available skills list
     *
     * @param message "/new_skill " + page + " * " + skill.name()
     * @return Skill - nullable
     */
    private Skill availableSkill(TextIncomingMessage message) {
        Set<Skill> availableSkills = Skill.getAvailableSkills(message.getPlayer());
        Skill newSkill;
        try {
            newSkill = Skill.valueOf(message.getParsedCommand().paramByNum(3));
        } catch (IllegalArgumentException e) {
            messages.sendMessage(TextResponseMessage.builder().by(message)
                    .text(translation.getMessage("player.stats.skill-not-available", message))
                    .build()
            );
            return null;
        }
        if (!availableSkills.contains(newSkill)) {
            messages.sendMessage(TextResponseMessage.builder().by(message)
                    .text(translation.getMessage("player.stats.skill-not-available", message))
                    .build()
            );
            return null;
        }
        return newSkill;
    }

}
