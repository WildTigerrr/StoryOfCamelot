package com.wildtigerrr.StoryOfCamelot.web;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.wildtigerrr.StoryOfCamelot.bin.Command;
import com.wildtigerrr.StoryOfCamelot.bin.MainText;
import com.wildtigerrr.StoryOfCamelot.database.schema.Location;
import com.wildtigerrr.StoryOfCamelot.database.schema.Player;
import com.wildtigerrr.StoryOfCamelot.database.service.implementation.FileLinkServiceImpl;
import com.wildtigerrr.StoryOfCamelot.database.service.implementation.LocationServiceImpl;
import com.wildtigerrr.StoryOfCamelot.database.service.implementation.PlayerServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;

import static com.wildtigerrr.StoryOfCamelot.web.BotConfig.WEBHOOK_ADMIN_ID;

@Service
public class ResponseHandler {

    @Autowired
    private WebHookHandler webHook;

    @Autowired
    private PlayerServiceImpl playerService;

    @Autowired
    private FileLinkServiceImpl fileLinkService;

    @Autowired
    private LocationServiceImpl locationService;

    public void handleMessage(UpdateWrapper message) {
        System.out.println("Working with message: " + message);
        logSender(message);
        message.setPlayer(getPlayer(message.getUserId()));
        if (message.getUserId().equals(WEBHOOK_ADMIN_ID)) {
            if (message.getText().equals("database test")) {
                // Some admin actions
                Location newLocation = locationService.findByName("Test Forest");
                if (newLocation != null) {
                    sendMessage(newLocation.toString(), message.getUserId());
                } else {
                    System.out.println("No such location");
                }
                StringBuilder loc = new StringBuilder();
                loc.append("Here're they: ");
                for (Location location : locationService.getAll()) {
                    loc.append(location.toString());
                }
                sendMessage(loc.toString(), message.getUserId());
                return;
            } else if (message.getText().equals("image test")) {
                sendTestImage(message.getUserId());
                return;
            }
        }
        if (message.getText().startsWith("/")) {
            performCommand(message);
            return;
        }
        Player player = message.getPlayer();
        if (message.getPlayer().isNew()) {
            System.out.println("New player");
            player.setup();
            playerService.update(player);
            sendMessage(MainText.MEET_NEW_PLAYER.text(), message.getUserId(), true);
        } else if (player.getExternalId().equals(player.getNickname())) {
            System.out.println("Here should be nickname set");
        }
        String answer = "You wrote me: " + message.getText();
        System.out.println("Answer: " + answer);
        sendMessage(answer, message.getUserId(), false);
    }

    private void sendTestImage(String userId) {
        AmazonS3 client = getClient();
        InputStream result = overlayImages(
                getImage(client, "images/locations/forest-test.png"),
                getImage(client, "images/items/weapons/swords/sword-test.png")
        );
        SendPhoto newMessage = new SendPhoto().setPhoto("Test Name", result);
        newMessage.setChatId(userId);
        try {
            new WebHookHandler().execute(newMessage);
        } catch (TelegramApiException e) {
            sendMessage(e.getMessage(), userId);
            e.printStackTrace();
        }
    }

    private InputStream overlayImages(InputStream inputBack, InputStream inputFront) {
        BufferedImage imageBack;
        BufferedImage imageFront;
        try {
            imageBack = ImageIO.read(inputBack);
            imageFront = ImageIO.read(inputFront);
            Graphics2D g = imageBack.createGraphics();
            g.setRenderingHint(
                    RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON
            );
            g.drawImage(imageBack, 0, 0, null);
            g.drawImage(imageFront, 0, 0, null);
            g.dispose();
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            ImageIO.write(imageBack, "png", os);
            return new ByteArrayInputStream(os.toByteArray());
        } catch (IOException e) {
            sendMessage(e.getMessage(), WEBHOOK_ADMIN_ID);
            e.printStackTrace();
        }
        return null;
    }

    private InputStream getImage(AmazonS3 client, String key) {
        S3Object object = client.getObject(new GetObjectRequest(
                "storyofcameloteu",
                key
        ));
        return object.getObjectContent();
    }

    private AmazonS3 getClient() {
        BasicAWSCredentials creds = new BasicAWSCredentials(System.getenv("AWS_S3_ID"), System.getenv("AWS_S3_KEY"));
        AmazonS3 client = AmazonS3ClientBuilder.standard()
                .withRegion(Regions.EU_CENTRAL_1)
                .withCredentials(new AWSStaticCredentialsProvider(creds))
                .build();
        return client;
    }

    private void logSender(UpdateWrapper message) {
        String log = "New message, User:"
                + (message.getFirstName() == null ? "" : " " + message.getFirstName())
                + (message.getLastName() == null ? "" : " " + message.getLastName())
                + " (id" + message.getUserId() + ")"
                + (message.getUsername() == null ? "" : ", also known as " + message.getUsername())
                + ", wrote a message: " + message.getText();
        System.out.println(log);

        if (!message.getUserId().equals(WEBHOOK_ADMIN_ID)) {
            sendMessage(log, WEBHOOK_ADMIN_ID);
        }
    }

    private void performCommand(UpdateWrapper message) {
        String commandParts[] = message.getText().split(" ", 2);
        Command command;
        try {
            command = Command.valueOf(commandParts[0].substring(1).toUpperCase());
        } catch (IllegalArgumentException e) {
            sendMessage(MainText.UNKNOWN_COMMAND.text(), message.getUserId(), true);
            return;
        }
        switch (command) {
            case ME:
                sendMessage(playerService.getPlayerInfo(message.getUserId()), message.getUserId(), true);
                break;
            case NICKNAME:
                if (commandParts.length > 1) {
                    setNickname(message.getPlayer(), commandParts[1]);
                } else {
                    sendMessage(MainText.EMPTY_NICKNAME.text(), message.getUserId(), true);
                }
                break;
            default:
                sendMessage(MainText.COMMAND_NOT_DEFINED.text(), message.getUserId(), true);
        }
    }

    private Player getPlayer(String externalId) {
        Player player;
        player = playerService.findByExternalId(externalId);
        if (player == null) {
            player = new Player(externalId, externalId);
            player = playerService.create(player);
        }
        return player;
    }

    private void setNickname(Player player, String newName) {
        System.out.println("New nickname would be: " + newName);
        player.setNickname(newName);
        playerService.update(player);
        sendMessage(MainText.NICKNAME_CHANGED.text() + player.getNickname() + "*", player.getExternalId(), true);
    }

    private Boolean alreadyRedirected;

    public void sendMessage(String text, String userId) {
        sendMessage(text, userId, false);
    }

    public void sendMessage(String text, String userId, Boolean useMarkdown) {
        if (alreadyRedirected == null || !alreadyRedirected) alreadyRedirected = true;
        else return;
        System.out.println("Message text: " + text);

        SendMessage sendMessage = new SendMessage();
        sendMessage.enableMarkdown(useMarkdown);
        sendMessage.setChatId(userId);
        sendMessage.setText(text);
        try {
            webHook.execute(sendMessage);
            alreadyRedirected = false;
        } catch (NullPointerException e) {
            System.out.println("Spring Startup Error (Autowired Services not initialized)");
            try {
                new WebHookHandler().execute(sendMessage);
            } catch (TelegramApiException ex) {
                sendMessageToAdmin(e.getMessage());
                ex.printStackTrace();
            }
        } catch (TelegramApiException e) {
            e.printStackTrace();
            sendMessageToAdmin(e.getMessage());
        }
    }

    public void sendMessageToAdmin(String text) {
        sendMessage(text, BotConfig.WEBHOOK_ADMIN_ID, false);
    }

}
