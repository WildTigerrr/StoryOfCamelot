package com.wildtigerrr.StoryOfCamelot.bin.handler;

import com.wildtigerrr.StoryOfCamelot.ServiceBaseTest;
import com.wildtigerrr.StoryOfCamelot.TestFactory;
import com.wildtigerrr.StoryOfCamelot.bin.base.service.MoneyCalculation;
import com.wildtigerrr.StoryOfCamelot.bin.enums.Command;
import com.wildtigerrr.StoryOfCamelot.bin.enums.ReplyButton;
import com.wildtigerrr.StoryOfCamelot.bin.enums.Skill;
import com.wildtigerrr.StoryOfCamelot.bin.translation.TranslationManager;
import com.wildtigerrr.StoryOfCamelot.database.jpa.schema.Player;
import com.wildtigerrr.StoryOfCamelot.database.jpa.service.template.PlayerService;
import com.wildtigerrr.StoryOfCamelot.testutils.TestUpdate;
import com.wildtigerrr.StoryOfCamelot.testutils.TestUpdateMessage;
import com.wildtigerrr.StoryOfCamelot.web.service.ResponseManager;
import com.wildtigerrr.StoryOfCamelot.web.service.message.IncomingMessage;
import com.wildtigerrr.StoryOfCamelot.web.service.message.ResponseMessage;
import com.wildtigerrr.StoryOfCamelot.web.service.message.template.TextIncomingMessage;
import com.wildtigerrr.StoryOfCamelot.web.service.message.template.TextResponseMessage;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;

@Log4j2
class SkillsCommandHandlerTest extends ServiceBaseTest {

    @Autowired
    private SkillsCommandHandler skillsCommandHandler;

    @Autowired
    private TestFactory testFactory;

    @Autowired
    private TranslationManager translation;

    @Autowired
    private PlayerService playerService;

    @MockBean
    private ResponseManager messages;

    @Captor
    ArgumentCaptor<ResponseMessage> messageArguments;

    @Captor
    ArgumentCaptor<String> answerArguments;

    @Test
    void whenLevelUpShouldReturnTheKeyboardForAvailableSkillsTest() {
        // Given
        Player player = testFactory.createPlayer();
        TextIncomingMessage message = (TextIncomingMessage) IncomingMessage.from(
                TestUpdate.builder().message(TestUpdateMessage.builder().text(ReplyButton.LEVEL_UP.getLabel(player)).build()).build().get()
        );
        message.setPlayer(player);

        // When
        skillsCommandHandler.process(message);

        // Then
        verify(messages).sendMessage(messageArguments.capture());

        assertEquals(translation.getMessage("player.stats.level-up-choose", message), messageArguments.getValue().getText());
        assertTrue(((TextResponseMessage) messageArguments.getValue()).getKeyboard().toString().contains(" STRONG_ATTACK"));
        assertTrue(((TextResponseMessage) messageArguments.getValue()).getKeyboard().toString().contains(" FAST_ATTACK"));
        assertFalse(((TextResponseMessage) messageArguments.getValue()).getKeyboard().toString().contains(" ADVANCED_STRONG_ATTACK"));
    }

    @Test
    void whenLevelUpWithSomeStatsShouldReturnTheKeyboardForMoreAvailableSkillsTest() {
        // Given
        Player player = testFactory.createPlayer();
        player.stats().setStrength(10);
        player.getSkills().add(Skill.STRONG_ATTACK);
        TextIncomingMessage message = (TextIncomingMessage) IncomingMessage.from(
                TestUpdate.builder().message(TestUpdateMessage.builder().text(ReplyButton.LEVEL_UP.getLabel(player)).build()).build().get()
        );
        message.setPlayer(player);

        // When
        skillsCommandHandler.process(message);

        // Then
        verify(messages).sendMessage(messageArguments.capture());

        assertEquals(translation.getMessage("player.stats.level-up-choose", message), messageArguments.getValue().getText());
        assertFalse(((TextResponseMessage) messageArguments.getValue()).getKeyboard().toString().contains(" STRONG_ATTACK"));
        assertTrue(((TextResponseMessage) messageArguments.getValue()).getKeyboard().toString().contains(" FAST_ATTACK"));
        assertTrue(((TextResponseMessage) messageArguments.getValue()).getKeyboard().toString().contains(" ADVANCED_STRONG_ATTACK"));
    }

    @Test
    void whenChangePageShouldSendValidSkillsTest() {
        // Given
        Player player = testFactory.createPlayer();
        player.stats().setStrength(10);
        player.getSkills().add(Skill.STRONG_ATTACK);
        TextIncomingMessage message = (TextIncomingMessage) IncomingMessage.from(
                TestUpdate.builder().isCallback(true).message(TestUpdateMessage.builder().text("/new_skill 1 page").build()).build().get()
        );
        message.setPlayer(player);

        // When
        skillsCommandHandler.process(message);

        // Then
        verify(messages).sendMessage(messageArguments.capture());

        assertEquals(translation.getMessage("player.stats.level-up-choose", message), messageArguments.getValue().getText());
        assertFalse(((TextResponseMessage) messageArguments.getValue()).getKeyboard().toString().contains(" STRONG_ATTACK"));
        assertTrue(((TextResponseMessage) messageArguments.getValue()).getKeyboard().toString().contains(" FAST_ATTACK"));
        assertTrue(((TextResponseMessage) messageArguments.getValue()).getKeyboard().toString().contains(" ADVANCED_STRONG_ATTACK"));
    }

    @Test
    void whenLevelUpSkillsShouldBeFilteredByStatsTest() {
        // Given
        Player player = testFactory.createPlayer();
        player.stats().setStrength(9);
        player.getSkills().add(Skill.STRONG_ATTACK);
        TextIncomingMessage message = (TextIncomingMessage) IncomingMessage.from(
                TestUpdate.builder().message(TestUpdateMessage.builder().text(ReplyButton.LEVEL_UP.getLabel(player)).build()).build().get()
        );
        message.setPlayer(player);

        // When
        skillsCommandHandler.process(message);

        // Then
        verify(messages).sendMessage(messageArguments.capture());

        assertEquals(translation.getMessage("player.stats.level-up-choose", message), messageArguments.getValue().getText());
        assertFalse(((TextResponseMessage) messageArguments.getValue()).getKeyboard().toString().contains(" ADVANCED_STRONG_ATTACK"));
    }

    @Test
    void whenLevelUpSkillsShouldBeFilteredByPreviousSkillsTest() {
        // Given
        Player player = testFactory.createPlayer();
        player.stats().setStrength(10);
        TextIncomingMessage message = (TextIncomingMessage) IncomingMessage.from(
                TestUpdate.builder().message(TestUpdateMessage.builder().text(ReplyButton.LEVEL_UP.getLabel(player)).build()).build().get()
        );
        message.setPlayer(player);

        // When
        skillsCommandHandler.process(message);

        // Then
        verify(messages).sendMessage(messageArguments.capture());

        assertEquals(translation.getMessage("player.stats.level-up-choose", message), messageArguments.getValue().getText());
        assertFalse(((TextResponseMessage) messageArguments.getValue()).getKeyboard().toString().contains(" ADVANCED_STRONG_ATTACK"));
    }

    @Test
    void whenSkillInfoShouldSendDescriptionIfAvailableTest() {
        // Given
        Player player = testFactory.createPlayer();
        Skill withDescription = Skill.FAST_ATTACK;
        TextIncomingMessage message = (TextIncomingMessage) IncomingMessage.from(
                TestUpdate.builder().isCallback(true).message(TestUpdateMessage.builder().text("/new_skill 1 skill_info " + withDescription.name()).build()).build().get()
        );
        message.setPlayer(player);

        // When
        skillsCommandHandler.process(message);

        // Then
        verify(messages).sendMessage(messageArguments.capture());
        verify(messages, Mockito.atMostOnce()).sendAnswer(any());

        assertEquals(withDescription.getDescription(player.getLanguage()), messageArguments.getValue().getText());
    }

    @Test
    void whenSkillInfoWithoutDescriptionShouldReplyOnlyTest() {
        // Given
        Player player = testFactory.createPlayer();
        Skill withoutDescription = Skill.DO_NOTHING;
        TextIncomingMessage message = (TextIncomingMessage) IncomingMessage.from(
                TestUpdate.builder().isCallback(true).message(TestUpdateMessage.builder().text("/new_skill 1 skill_info " + withoutDescription.name()).build()).build().get()
        );
        message.setPlayer(player);

        // When
        skillsCommandHandler.process(message);

        // Then
        verify(messages, Mockito.atMostOnce()).sendAnswer(any());
    }

    @Test
    void whenLearnNewSkillShouldAddItToTheListTest() {
        // Given
        Player player = testFactory.createPlayer();
        Skill newSkill = Skill.FAST_ATTACK;
        TextIncomingMessage message = (TextIncomingMessage) IncomingMessage.from(
                TestUpdate.builder().isCallback(true).message(TestUpdateMessage.builder().text("/new_skill 1 skill_learn " + newSkill.name()).build()).build().get()
        );
        message.setPlayer(player);
        assertFalse(player.getSkills().contains(newSkill));

        // When
        skillsCommandHandler.process(message);

        // Then
        verify(messages).sendMessage(messageArguments.capture());

        assertEquals(translation.getMessage("player.stats.skill-learned", message), messageArguments.getValue().getText());

        player = playerService.getPlayer(player.getExternalId());
        assertTrue(player.getSkills().contains(newSkill));
    }

    @Test
    void whenLearnNewSkillShouldCheckAvailabilityTest() {
        // Given
        Player player = testFactory.createPlayer();
        Skill newSkill = Skill.ADVANCED_STRONG_ATTACK;
        TextIncomingMessage message = (TextIncomingMessage) IncomingMessage.from(
                TestUpdate.builder().isCallback(true).message(TestUpdateMessage.builder().text("/new_skill 1 skill_learn " + newSkill.name()).build()).build().get()
        );
        message.setPlayer(player);
        assertFalse(player.getSkills().contains(newSkill));

        // When
        skillsCommandHandler.process(message);

        // Then
        verify(messages).sendAnswer(anyString(), answerArguments.capture());

        assertEquals(translation.getMessage("player.stats.skill-not-available", message), answerArguments.getValue());
    }

}