package com.wildtigerrr.StoryOfCamelot.bin.handler;

import com.wildtigerrr.StoryOfCamelot.bin.translation.TranslationManager;
import com.wildtigerrr.StoryOfCamelot.web.service.ResponseManager;
import com.wildtigerrr.StoryOfCamelot.web.service.message.IncomingMessage;
import com.wildtigerrr.StoryOfCamelot.web.service.message.template.TextResponseMessage;

public class SendCommandHandler extends TextMessageHandler {

    public SendCommandHandler(ResponseManager messages, TranslationManager translation) {
        super(messages, translation);
    }

    @Override
    public void process(IncomingMessage message) {
        // In-Game chat
        // /send {nickname} {message}, /mute {nickname}, /muted, /unmute {nickname} (space == _?)
        // /send WildTigerrr Hello mate, what's up?
        // Search by nickname,
        //          if can't find - answer,
        //          if {nickname} in blacklist - warn "You can't send messages to users which you blocked",
        //          if sender in recipient's personal block-list - ignore
        // On incoming message - buttons Answer and Mute
        // Answer - prepopulate (switch_inline_query_current_chat) with /send *author nickname*
        // Mute - add Player in personal block-list, to not receive messages
        // /muted - get personal block-list
        // /unmute {nickname} - remove user from black-list
    }

//    public void sendMessageToUser(String message) {
//        String[] commandParts = message.split(" ", 3);
////        Player receiver = playerService.findByExternalId(commandParts[1]);
//        messages.sendMessage(TextResponseMessage.builder()
//                .text(commandParts[2]).targetId(commandParts[1]).build()
//        );
//        //                    messages.sendMessage("Пользователь не найден", message.getUserId());
//    }

}