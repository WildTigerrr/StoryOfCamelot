package com.wildtigerrr.StoryOfCamelot.bin.base.service;

import com.wildtigerrr.StoryOfCamelot.bin.enums.Language;
import com.wildtigerrr.StoryOfCamelot.bin.service.ApplicationContextProvider;
import com.wildtigerrr.StoryOfCamelot.bin.translation.TranslationManager;
import com.wildtigerrr.StoryOfCamelot.database.jpa.schema.Player;
import org.h2.util.MathUtils;

public class MoneyCalculation {

    private static final int BRONZE = 1;
    private static final int SILVER = BRONZE * 100;
    private static final int GOLD = SILVER * 100;

    public static String moneyOf(Player player) {
        return moneyOf(player.getMoney(), player.getLanguage());
    }

    public static String moneyOf(Player player, TranslationManager translation) {
        return moneyOf(player.getMoney(), player.getLanguage(), translation);
    }

    public static String moneyOf(long money, Language lang) {
        TranslationManager translation = ApplicationContextProvider.bean(TranslationManager.class);
        return moneyOf(money, lang, translation);
    }

    public static String moneyOf(long money, Language lang, TranslationManager translation) {

        if (money > 0) {
            String moneyString = "";
            if (bronzeFrom(money) != 0) {
                moneyString += translation.getMessage("money.bronze", lang) + ": " + bronzeFrom(money);
            }
            if (money >= SILVER && silverFrom(money) != 0) {
                moneyString += moneyString.isBlank() ? "" : ", ";
                moneyString += translation.getMessage("money.silver", lang) + ": " + silverFrom(money);
            }
            if (money >= GOLD && goldFrom(money) != 0) {
                moneyString += moneyString.isBlank() ? "" : ", ";
                moneyString += translation.getMessage("money.gold", lang) + ": " + goldFrom(money);
            }
            return moneyString;
        } else {
            return translation.getMessage("money.none", lang);
        }
    }

    public static long round(long money) {
        if (money < SILVER) return money;
        if (money < GOLD) return money / 10 * 10;
        else return money / 100 * 100;
    }

    public static long goldFrom(long money) {
        if (money < 0 || String.valueOf(money).length() < 5) return 0;
        return Long.parseLong(String.valueOf(money).substring(0, String.valueOf(money).length() - 4));
    }

    public static long silverFrom(long money) {
        money -= goldOf(goldFrom(money));
        if (money < 0 || String.valueOf(money).length() < 3) return 0;
        return Long.parseLong(String.valueOf(money).substring(0, String.valueOf(money).length() - 2));
    }

    public static long bronzeFrom(long money) {
        money -= goldOf(goldFrom(money));
        money -= silverOf(silverFrom(money));
        if (money < 0) return 0;
        return money;
    }

    public static long goldOf(long value) {
        return value * GOLD;
    }

    public static long silverOf(long value) {
        return value * SILVER;
    }

    public static long bronzeOf(long value) {
        return value * BRONZE;
    }

}
