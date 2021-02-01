package com.wildtigerrr.StoryOfCamelot.bin.handler;

import com.wildtigerrr.StoryOfCamelot.bin.base.service.ActionHandler;
import com.wildtigerrr.StoryOfCamelot.bin.base.service.KeyboardManager;
import com.wildtigerrr.StoryOfCamelot.bin.base.service.MoneyCalculation;
import com.wildtigerrr.StoryOfCamelot.bin.enums.Language;
import com.wildtigerrr.StoryOfCamelot.bin.translation.TranslationManager;
import com.wildtigerrr.StoryOfCamelot.database.jpa.schema.Backpack;
import com.wildtigerrr.StoryOfCamelot.database.jpa.schema.Item;
import com.wildtigerrr.StoryOfCamelot.database.jpa.schema.Store;
import com.wildtigerrr.StoryOfCamelot.database.jpa.service.template.BackpackService;
import com.wildtigerrr.StoryOfCamelot.database.jpa.service.template.ItemService;
import com.wildtigerrr.StoryOfCamelot.database.jpa.service.template.PlayerService;
import com.wildtigerrr.StoryOfCamelot.database.jpa.service.template.StoreService;
import com.wildtigerrr.StoryOfCamelot.web.bot.update.ParsedCommand;
import com.wildtigerrr.StoryOfCamelot.web.service.ResponseManager;
import com.wildtigerrr.StoryOfCamelot.web.service.message.IncomingMessage;
import com.wildtigerrr.StoryOfCamelot.web.service.message.template.EditResponseMessage;
import com.wildtigerrr.StoryOfCamelot.web.service.message.template.TextIncomingMessage;
import com.wildtigerrr.StoryOfCamelot.web.service.message.template.TextResponseMessage;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
@Log4j2
public class StoreCommandHandler extends TextMessageHandler {

    private final ActionHandler actionHandler;
    private final ItemService itemService;
    private final StoreService storeService;
    private final BackpackService backpackService;
    private final PlayerService playerService;

    public StoreCommandHandler(ResponseManager messages, TranslationManager translation, ActionHandler actionHandler, ItemService itemService, StoreService storeService, BackpackService backpackService, PlayerService playerService) {
        super(messages, translation);
        this.actionHandler = actionHandler;
        this.itemService = itemService;
        this.storeService = storeService;
        this.backpackService = backpackService;
        this.playerService = playerService;
    }

    @Override
    public void process(IncomingMessage message) {
        switch (message.getCommand()) {
            case STORE: proceedWithStoreItem((TextIncomingMessage) message); break;
            case STORES: sendAvailableStores(message); break;
            case STORE_SELECT: sendStore((TextIncomingMessage) message); break;
        }
    }

    private void proceedWithStoreItem(TextIncomingMessage message) {
        ParsedCommand command = message.getParsedCommand();
        if (command.paramsCount() > 3) {
            switch (command.paramByNum(3)) {
                case "page": sendStore(message, command.paramByNum(1), command.intByNum(2)); break;
                case "item_info": sendItemInfo(message); break;
                case "item_buy": buyItem(message); break;
                default: log.debug(command.paramByNum(3));
            }
        } else if (command.paramsCount() == 2) { // TODO WTF is that condition <<<
            log.debug(command.paramByNum(2));
            if (command.paramByNum(2).equals("sell")) {
                messages.sendMessage(TextResponseMessage.builder().by(message)
                        .text("Здесь может быть ваша реклама").build()
                );
            }
            messages.sendAnswer(message.getQueryId(), command.toString());
        } else {
            log.debug("Parameters quantity: " + command.paramsCount());
            messages.sendAnswer(message.getQueryId(), command.paramsCount() + " params");
        }
    }

    private void sendStore(TextIncomingMessage message) {
        String storeId = getStoreId(message.getPlayer().getLocation().getStores(), message.text(), message.getPlayer().getLanguage());
        if (storeId == null) {
            messages.sendMessage(TextResponseMessage.builder().by(message)
                    .text("Здесь нет такого магазина") // TODO
                    .build()
            );
            return;
        }
        sendStore(message, storeId, 1);
    }

    private void sendStore(TextIncomingMessage message, String storeId, int page) {
        Store store = storeService.getById(storeId);
        List<Item> items = itemService.getByStoreTypes(store.getStoreType());
        if (message.isQuery()) {
            messages.sendMessage(EditResponseMessage.builder().by(message)
                    .text("Ассоритимент для магазина: " + message.text()) // TODO
                    .keyboard(KeyboardManager.getKeyboardForStoreItems(store, items, page, message.getPlayer().getLanguage(), translation))
                    .build()
            );
            messages.sendAnswer(message.getQueryId(), "Страница: " + page);
        } else {
            messages.sendMessage(TextResponseMessage.builder().by(message)
                    .text("Ассоритимент для магазина: " + message.text()) // TODO
                    .keyboard(KeyboardManager.getKeyboardForStoreItems(store, items, page, message.getPlayer().getLanguage(), translation))
                    .build()
            );
        }
    }

    private void buyItem(TextIncomingMessage message) {
        ParsedCommand command = message.getParsedCommand();
        Item item = itemService.findById(command.paramByNum(4));
        if (item == null) {
            messages.sendMessage(TextResponseMessage.builder().by(message)
                    .text("Такого предмета нет")
                    .build()
            );
            messages.sendAnswer(message.getQueryId());
            return;
        }
        Store store = storeService.getById(command.paramByNum(1));
        List<Item> items = itemService.getByStoreTypes(store.getStoreType());
        if (!items.contains(item)) {
            messages.sendMessage(TextResponseMessage.builder().by(message)
                    .text("Этот предмет здесь не продаётся")
                    .build()
            );
            messages.sendAnswer(message.getQueryId());
            return;
        }
        if (message.getPlayer().getMoney() < item.getPrice()) {
            messages.sendMessage(TextResponseMessage.builder().by(message)
                    .text("У вас недостаточно денег")
                    .build()
            );
            messages.sendAnswer(message.getQueryId(), "Остаток: " + MoneyCalculation.moneyOf(message.getPlayer(), translation));
            return;
        }
        message.getPlayer().pay(item.getPrice());
        Backpack backpack = backpackService.findMainByPlayerId(message.getPlayer().getId());
        backpack.put(item);
        backpackService.update(backpack); // TODO Take backpack from Player
        playerService.update(message.getPlayer());
        messages.sendMessage(TextResponseMessage.builder().by(message)
                .text("Предмет куплен: " + item.getName(message.getPlayer()))
                .build()
        );
        messages.sendAnswer(message.getQueryId(), "Осталось: " + MoneyCalculation.moneyOf(message.getPlayer(), translation));
    }

    private void sendItemInfo(TextIncomingMessage message) {
        ParsedCommand command = message.getParsedCommand();
        if (command.paramsCount() < 4) return;
        Item item = itemService.findById(command.paramByNum(4));
        if (item != null) {
            messages.sendMessage(TextResponseMessage.builder().by(message)
                    .text(item.getDescribe(message.getPlayer()))
                    .applyMarkup(true).build()
            );
        } else {
            messages.sendMessage(TextResponseMessage.builder().by(message)
                    .text(translation.getMessage("location.store.wrong-item", message)).build()
            );
        }
        messages.sendAnswer(message.getQueryId());
    }

    private void sendAvailableStores(IncomingMessage message) {
        if (message.getPlayer().getLocation().getHasStores()) {
            messages.sendMessage(TextResponseMessage.builder().by(message)
                    .keyboard(KeyboardManager.getReplyByStores(message.getPlayer().getLocation().getStores(), message.getPlayer().getLanguage()))
                    .text(translation.getMessage("location.store.stores", message, new Object[]{MoneyCalculation.moneyOf(message.getPlayer())}))
                    .build()
            );
        } else {
            messages.sendMessage(TextResponseMessage.builder()
                    .text(translation.getMessage("location.store.no-stores", message))
                    .build()
            );
            actionHandler.sendAvailableActions(message.getPlayer());
        }
    }

    private String getStoreId(Set<Store> stores, String storeName, Language lang) {
        Store store = stores.stream().filter(str -> str.getLabel(lang).equals(storeName)).findFirst().orElse(null);
        return store == null ? null : store.getId();
    }

}
