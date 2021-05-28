package com.wildtigerrr.StoryOfCamelot.bin.handler;

import com.wildtigerrr.StoryOfCamelot.ServiceBaseTest;
import com.wildtigerrr.StoryOfCamelot.TestFactory;
import com.wildtigerrr.StoryOfCamelot.bin.base.service.MoneyCalculation;
import com.wildtigerrr.StoryOfCamelot.bin.enums.Command;
import com.wildtigerrr.StoryOfCamelot.bin.enums.ReplyButton;
import com.wildtigerrr.StoryOfCamelot.bin.enums.templates.StoreTemplate;
import com.wildtigerrr.StoryOfCamelot.bin.service.NumberUtils;
import com.wildtigerrr.StoryOfCamelot.bin.translation.TranslationManager;
import com.wildtigerrr.StoryOfCamelot.database.jpa.schema.Backpack;
import com.wildtigerrr.StoryOfCamelot.database.jpa.schema.Item;
import com.wildtigerrr.StoryOfCamelot.database.jpa.schema.Player;
import com.wildtigerrr.StoryOfCamelot.database.jpa.schema.Store;
import com.wildtigerrr.StoryOfCamelot.database.jpa.schema.enums.StoreType;
import com.wildtigerrr.StoryOfCamelot.database.jpa.service.template.BackpackService;
import com.wildtigerrr.StoryOfCamelot.database.jpa.service.template.ItemService;
import com.wildtigerrr.StoryOfCamelot.database.jpa.service.template.PlayerService;
import com.wildtigerrr.StoryOfCamelot.database.jpa.service.template.StoreService;
import com.wildtigerrr.StoryOfCamelot.testutils.TestUpdate;
import com.wildtigerrr.StoryOfCamelot.testutils.TestUpdateMessage;
import com.wildtigerrr.StoryOfCamelot.web.service.ResponseManager;
import com.wildtigerrr.StoryOfCamelot.web.service.message.IncomingMessage;
import com.wildtigerrr.StoryOfCamelot.web.service.message.ResponseMessage;
import com.wildtigerrr.StoryOfCamelot.web.service.message.template.EditResponseMessage;
import com.wildtigerrr.StoryOfCamelot.web.service.message.template.TextIncomingMessage;
import com.wildtigerrr.StoryOfCamelot.web.service.message.template.TextResponseMessage;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class StoreCommandHandlerTest extends ServiceBaseTest {

    @Autowired
    private StoreCommandHandler storeCommandHandler;

    @Autowired
    private TestFactory testFactory;

    @Autowired
    private TranslationManager translation;

    @Autowired
    private PlayerService playerService;

    @Autowired
    private BackpackService backpackService;

    @Autowired
    private StoreService storeService;

    @Autowired
    private ItemService itemService;

    @MockBean
    private ResponseManager messages;

    @Captor
    ArgumentCaptor<ResponseMessage> messageArguments;

    @Test
    void whenStoresShouldSendAvailableLocationStoresTest() {
        // Given
        Player player = testFactory.createPlayer();
        Store store = new Store(player.getLocation(), StoreTemplate.TRADING_SQUARE_GROCERY);
        ReflectionTestUtils.setField(player.getLocation(), "stores", new HashSet<>(Collections.singletonList(store)));
        TextIncomingMessage message = (TextIncomingMessage) IncomingMessage.from(
                TestUpdate.builder().message(TestUpdateMessage.builder().text("/" + Command.STORES.name()).build()).build().get()
        );
        message.setPlayer(player);

        // When
        storeCommandHandler.process(message);

        // Then
        verify(messages).sendMessage(messageArguments.capture());

        assertEquals(translation.getMessage("location.store.stores", message, new Object[]{MoneyCalculation.moneyOf(player)}), messageArguments.getValue().getText());
    }

    @Test
    void whenStoresMayNotHaveStoresTest() {
        // Given
        Player player = testFactory.createPlayer(true);
        player.getLocation().setHasStores(false);
        TextIncomingMessage message = (TextIncomingMessage) IncomingMessage.from(
                TestUpdate.builder().message(TestUpdateMessage.builder().text("/" + Command.STORES.name()).build()).build().get()
        );
        message.setPlayer(player);

        // When
        storeCommandHandler.process(message);

        // Then
        verify(messages, times(2)).sendMessage(messageArguments.capture());

        List<ResponseMessage> messages = messageArguments.getAllValues();
        assertEquals(translation.getMessage("location.store.no-stores", player), messages.get(0).getText());
    }

    @Test
    void whenStoreSelectWithoutStoresShouldInformPlayerTest() {
        // Given
        Player player = testFactory.createPlayer();
        TextIncomingMessage message = (TextIncomingMessage) IncomingMessage.from(
                TestUpdate.builder().message(TestUpdateMessage.builder().text(ReplyButton.STORE_GROCERY.getLabel(player)).build()).build().get()
        );
        message.setPlayer(player);

        // When
        storeCommandHandler.process(message);

        // Then
        verify(messages, atLeast(1)).sendMessage(messageArguments.capture());

        assertEquals(translation.getMessage("location.store.wrong-store", message), messageArguments.getValue().getText());
    }

    @Test
    void whenStoreSelectShouldSendAvailableItemsTest() {
        // Given
        Player player = testFactory.createPlayer();
        Store store = new Store(player.getLocation(), StoreTemplate.TRADING_SQUARE_MERCHANT);
        store = storeService.create(store);
        ReflectionTestUtils.setField(player.getLocation(), "stores", new HashSet<>(Collections.singletonList(store)));
        TextIncomingMessage message = (TextIncomingMessage) IncomingMessage.from(
                TestUpdate.builder().message(TestUpdateMessage.builder().text(ReplyButton.STORE_MERCHANT.getLabel(player)).build()).build().get()
        );
        message.setPlayer(player);

        // When
        storeCommandHandler.process(message);

        // Then
        verify(messages).sendMessage(messageArguments.capture());

        assertEquals(translation.getMessage("location.store.assortment", message, new Object[]{message.text()}), messageArguments.getValue().getText());
        assertTrue(((TextResponseMessage) messageArguments.getValue()).getKeyboard().toString().contains("InlineKeyboardButton"));
    }

    @Test
    void whenStoreHavePagesShouldSwitchPageTest() {
        // Given
        Player player = testFactory.createPlayer();
        Store store = new Store(player.getLocation(), StoreTemplate.TRADING_SQUARE_MERCHANT);
        store = storeService.create(store);
        ReflectionTestUtils.setField(player.getLocation(), "stores", new HashSet<>(Collections.singletonList(store)));
        TextIncomingMessage message = (TextIncomingMessage) IncomingMessage.from(
                TestUpdate.builder().isCallback(true).message(TestUpdateMessage.builder().text("/store " + store.getId() + " 2 page").build()).build().get()
        );
        message.setPlayer(player);

        // When
        storeCommandHandler.process(message);

        // Then
        verify(messages).sendMessage(messageArguments.capture());

        assertEquals(translation.getMessage("location.store.assortment", message, new Object[]{message.text()}), messageArguments.getValue().getText());
    }

    @Test
    void whenStoreSelectByQueryShouldSendAvailableItemsTest() {
        // Given
        Player player = testFactory.createPlayer();
        Store store = new Store(player.getLocation(), StoreTemplate.TRADING_SQUARE_MERCHANT);
        store = storeService.create(store);
        ReflectionTestUtils.setField(player.getLocation(), "stores", new HashSet<>(Collections.singletonList(store)));
        TextIncomingMessage message = (TextIncomingMessage) IncomingMessage.from(
                TestUpdate.builder().isCallback(true).message(TestUpdateMessage.builder().text(ReplyButton.STORE_MERCHANT.getLabel(player)).build()).build().get()
        );
        message.setPlayer(player);

        // When
        storeCommandHandler.process(message);

        // Then
        verify(messages).sendMessage(messageArguments.capture());

        assertEquals(translation.getMessage("location.store.assortment", message, new Object[]{message.text()}), messageArguments.getValue().getText());
        assertTrue(((EditResponseMessage) messageArguments.getValue()).getKeyboard().toString().contains("InlineKeyboardButton"));
    }

    @Test
    void whenBuyItemShouldAddToBackpackTest() {
        // Given
        Player player = testFactory.createPlayer();
        Store store = new Store(player.getLocation(), StoreTemplate.TRADING_SQUARE_MERCHANT);
        store = storeService.create(store);
        List<Item> items = itemService.getByStoreTypes(store.getStoreType());
        ReflectionTestUtils.setField(player.getLocation(), "stores", new HashSet<>(Collections.singletonList(store)));
        TextIncomingMessage message = (TextIncomingMessage) IncomingMessage.from(
                TestUpdate.builder().isCallback(true).message(TestUpdateMessage.builder().text("/store " + store.getId() + " 1 item_buy " + items.get(0).getId()).build()).build().get()
        );
        player.addMoney(items.get(0).getPrice());
        playerService.update(player);
        message.setPlayer(player);

        // When
        Backpack backpack = backpackService.findMainByPlayerId(message.getPlayer().getId());
        assertNull(backpack.getItemById(items.get(0).getId()));
        storeCommandHandler.process(message);

        // Then
        verify(messages).sendMessage(messageArguments.capture());

        assertEquals(translation.getMessage("location.store.item.purchased", message, new Object[]{items.get(0).getName(message.getPlayer())}), messageArguments.getValue().getText());
        backpack = backpackService.findMainByPlayerId(message.getPlayer().getId());
        assertNotNull(backpack.getItemById(items.get(0).getId()));
    }

    @Test
    void whenBuyItemShouldCheckIfEnoughMoneyTest() {
        // Given
        Player player = testFactory.createPlayer();
        Store store = new Store(player.getLocation(), StoreTemplate.TRADING_SQUARE_MERCHANT);
        store = storeService.create(store);
        List<Item> items = itemService.getByStoreTypes(store.getStoreType());
        ReflectionTestUtils.setField(player.getLocation(), "stores", new HashSet<>(Collections.singletonList(store)));
        TextIncomingMessage message = (TextIncomingMessage) IncomingMessage.from(
                TestUpdate.builder().isCallback(true).message(TestUpdateMessage.builder().text("/store " + store.getId() + " 1 item_buy " + items.get(0).getId()).build()).build().get()
        );
        player.pay(player.getMoney());
        playerService.update(player);
        message.setPlayer(player);

        // When
        storeCommandHandler.process(message);

        // Then
        verify(messages).sendMessage(messageArguments.capture());

        assertEquals(translation.getMessage("location.store.item.not-enough-money", message), messageArguments.getValue().getText());
    }

    @Test
    void whenBuyItemShouldCheckIfItsRightStoreTest() {
        // Given
        Player player = testFactory.createPlayer();
        Store store = new Store(player.getLocation(), StoreTemplate.TRADING_SQUARE_MERCHANT);
        store = storeService.create(store);
        List<Item> items = itemService.getByStoreTypes(Collections.singletonList(StoreType.EMPTY));
        ReflectionTestUtils.setField(player.getLocation(), "stores", new HashSet<>(Collections.singletonList(store)));
        TextIncomingMessage message = (TextIncomingMessage) IncomingMessage.from(
                TestUpdate.builder().isCallback(true).message(TestUpdateMessage.builder().text("/store " + store.getId() + " 1 item_buy " + items.get(0).getId()).build()).build().get()
        );
        message.setPlayer(player);

        // When
        storeCommandHandler.process(message);

        // Then
        verify(messages).sendMessage(messageArguments.capture());

        assertEquals(translation.getMessage("location.store.wrong-item", message), messageArguments.getValue().getText());
    }

    @Test
    void whenBuyItemShouldCheckItemValidityTest() {
        // Given
        Player player = testFactory.createPlayer();
        Store store = new Store(player.getLocation(), StoreTemplate.TRADING_SQUARE_MERCHANT);
        store = storeService.create(store);
        ReflectionTestUtils.setField(player.getLocation(), "stores", new HashSet<>(Collections.singletonList(store)));
        TextIncomingMessage message = (TextIncomingMessage) IncomingMessage.from(
                TestUpdate.builder().isCallback(true).message(TestUpdateMessage.builder().text("/store " + store.getId() + " 1 item_buy wrongId").build()).build().get()
        );
        message.setPlayer(player);

        // When
        storeCommandHandler.process(message);

        // Then
        verify(messages).sendMessage(messageArguments.capture());

        assertEquals(translation.getMessage("location.store.item.not-exist", message), messageArguments.getValue().getText());
    }

    @Test
    void whenItemInfoShouldSendInfoTest() {
        // Given
        Player player = testFactory.createPlayer();
        Store store = new Store(player.getLocation(), StoreTemplate.TRADING_SQUARE_MERCHANT);
        store = storeService.create(store);
        List<Item> items = itemService.getByStoreTypes(store.getStoreType());
        ReflectionTestUtils.setField(player.getLocation(), "stores", new HashSet<>(Collections.singletonList(store)));
        TextIncomingMessage message = (TextIncomingMessage) IncomingMessage.from(
                TestUpdate.builder().isCallback(true).message(TestUpdateMessage.builder().text("/store " + store.getId() + " 1 item_info " + items.get(0).getId()).build()).build().get()
        );
        message.setPlayer(player);

        // When
        storeCommandHandler.process(message);

        // Then
        verify(messages).sendMessage(messageArguments.capture());

        assertEquals(items.get(0).getDescribe(message.getPlayer()), messageArguments.getValue().getText());
    }

    @Test
    void whenItemInfoMayBeWrongIdTest() {
        // Given
        Player player = testFactory.createPlayer();
        Store store = new Store(player.getLocation(), StoreTemplate.TRADING_SQUARE_MERCHANT);
        store = storeService.create(store);
        ReflectionTestUtils.setField(player.getLocation(), "stores", new HashSet<>(Collections.singletonList(store)));
        TextIncomingMessage message = (TextIncomingMessage) IncomingMessage.from(
                TestUpdate.builder().isCallback(true).message(TestUpdateMessage.builder().text("/store " + store.getId() + " 1 item_info wrongId").build()).build().get()
        );
        message.setPlayer(player);

        // When
        storeCommandHandler.process(message);

        // Then
        verify(messages).sendMessage(messageArguments.capture());

        assertEquals(translation.getMessage("location.store.wrong-item", message), messageArguments.getValue().getText());
    }

    @Test
    void whenItemWrongCommandShouldInformAdminTest() {
        // Given
        Player player = testFactory.createPlayer();
        Store store = new Store(player.getLocation(), StoreTemplate.TRADING_SQUARE_MERCHANT);
        store = storeService.create(store);
        ReflectionTestUtils.setField(player.getLocation(), "stores", new HashSet<>(Collections.singletonList(store)));
        TextIncomingMessage message = (TextIncomingMessage) IncomingMessage.from(
                TestUpdate.builder().isCallback(true).message(TestUpdateMessage.builder().text("/store " + store.getId()).build()).build().get()
        );
        message.setPlayer(player);

        // When
        storeCommandHandler.process(message);

        // Then
        verify(messages).postMessageToAdminChannel(any());
    }

    @Test
    void whenSendSellWindowShouldShowBackpackItemsTest() {
        // Given
        Player player = testFactory.createPlayer();
        Store store = new Store(player.getLocation(), StoreTemplate.TRADING_SQUARE_MERCHANT);
        store = storeService.create(store);
        List<Item> items = itemService.getByStoreTypes(store.getStoreType());
        ReflectionTestUtils.setField(player.getLocation(), "stores", new HashSet<>(Collections.singletonList(store)));
        TextIncomingMessage message = (TextIncomingMessage) IncomingMessage.from(
                TestUpdate.builder().isCallback(true).message(TestUpdateMessage.builder().text("/store " + store.getId() + " 1 page_sell").build()).build().get()
        );
        message.setPlayer(player);
        Backpack backpack = backpackService.findMainByPlayerId(message.getPlayer().getId());
        backpack.addItem(items.get(0));
        backpackService.update(backpack);

        // When
        storeCommandHandler.process(message);

        // Then
        verify(messages).sendMessage(messageArguments.capture());

        assertEquals(translation.getMessage("location.store.sell", message, new Object[]{store.getLabel(player.getLanguage())}), messageArguments.getValue().getText());
        assertTrue(((EditResponseMessage) messageArguments.getValue()).getKeyboard().toString().contains("InlineKeyboardButton"));
        assertTrue(((EditResponseMessage) messageArguments.getValue()).getKeyboard().toString().contains(
                "Sell " + backpack.getItems().get(0).backpackInfo(translation))
        );
        assertTrue(((EditResponseMessage) messageArguments.getValue()).getKeyboard().toString().contains(
                MoneyCalculation.moneyOf(backpack.getItems().get(0).getSalePrice(), player.getLanguage())
        ));
        System.out.println(((EditResponseMessage) messageArguments.getValue()).getKeyboard().toString());
    }

    @Test
    void whenSellItemShouldRemoveItemAndUpdateWindowTest() {
        // Given
        Player player = testFactory.createPlayer();
        long initialMoney = player.getMoney();
        Store store = new Store(player.getLocation(), StoreTemplate.TRADING_SQUARE_MERCHANT);
        store = storeService.create(store);
        List<Item> items = itemService.getByStoreTypes(store.getStoreType());
        Backpack backpack = backpackService.findMainByPlayerId(player.getId());
        backpack.addItem(items.get(0));
        backpackService.update(backpack);
        backpack = backpackService.findMainByPlayerId(player.getId());
        ReflectionTestUtils.setField(player.getLocation(), "stores", new HashSet<>(Collections.singletonList(store)));
        TextIncomingMessage message = (TextIncomingMessage) IncomingMessage.from(
                TestUpdate.builder().isCallback(true).message(TestUpdateMessage.builder().text("/store " + store.getId() + " 1 item_sell " + backpack.getItems().get(0).getId()).build()).build().get()
        );
        message.setPlayer(player);

        // When
        storeCommandHandler.process(message);

        // Then
        verify(messages).sendMessage(messageArguments.capture());

        assertEquals(translation.getMessage("location.store.sell", message, new Object[]{store.getLabel(player.getLanguage())}), messageArguments.getValue().getText());
        assertNull(((EditResponseMessage) messageArguments.getValue()).getKeyboard());
        backpack = backpackService.findMainByPlayerId(message.getPlayer().getId());
        assertNull(backpack.getItemById(items.get(0).getId()));
        player = playerService.getPlayer(player.getExternalId());
        assertEquals(initialMoney + items.get(0).getSalePrice(), (long) player.getMoney());
    }

}