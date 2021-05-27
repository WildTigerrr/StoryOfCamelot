package com.wildtigerrr.StoryOfCamelot.bin.base.service;

import com.wildtigerrr.StoryOfCamelot.ServiceBaseTest;

import com.wildtigerrr.StoryOfCamelot.bin.enums.Language;
import com.wildtigerrr.StoryOfCamelot.bin.service.ApplicationContextProvider;
import com.wildtigerrr.StoryOfCamelot.bin.translation.TranslationManager;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MoneyCalculationTest extends ServiceBaseTest {

    @Test
    void whenGettingGoldShouldReturnValidValueTest() {
        assertEquals(0L, MoneyCalculation.goldFrom(0));
        assertEquals(0L, MoneyCalculation.goldFrom(10));
        assertEquals(0L, MoneyCalculation.goldFrom(100));
        assertEquals(0L, MoneyCalculation.goldFrom(9999));
        assertEquals(1L, MoneyCalculation.goldFrom(10000));
        assertEquals(1L, MoneyCalculation.goldFrom(MoneyCalculation.goldOf(1)));
        assertEquals(1L, MoneyCalculation.goldFrom(MoneyCalculation.silverOf(100)));
        assertEquals(1L, MoneyCalculation.goldFrom(MoneyCalculation.bronzeOf(10000)));
        assertEquals(90L, MoneyCalculation.goldFrom(909090));
    }

    @Test
    void whenGettingSilverShouldReturnValidValueTest() {
        assertEquals(0L, MoneyCalculation.silverFrom(0));
        assertEquals(0L, MoneyCalculation.silverFrom(10));
        assertEquals(1L, MoneyCalculation.silverFrom(100));
        assertEquals(99L, MoneyCalculation.silverFrom(9999));
        assertEquals(0L, MoneyCalculation.silverFrom(10000));
        assertEquals(90L, MoneyCalculation.silverFrom(909090));
    }

    @Test
    void whenGettingBronzeShouldReturnValidValueTest() {
        assertEquals(0L, MoneyCalculation.bronzeFrom(0));
        assertEquals(10L, MoneyCalculation.bronzeFrom(10));
        assertEquals(0L, MoneyCalculation.bronzeFrom(100));
        assertEquals(99L, MoneyCalculation.bronzeFrom(9999));
        assertEquals(0L, MoneyCalculation.bronzeFrom(10000));
        assertEquals(90L, MoneyCalculation.bronzeFrom(909090));
    }

    @Test
    void whenGettingMoneyStringShouldReturnValidInSpecifiedLanguageTest() {
        // Given
        TranslationManager translation = ApplicationContextProvider.bean(TranslationManager.class);
        String none;
        String bronze;
        String silver;
        String gold;
        for (Language lang : Language.values()) {
            none = translation.getMessage("money.none", lang);
            bronze = translation.getMessage("money.bronze", lang);
            silver = translation.getMessage("money.silver", lang);
            gold = translation.getMessage("money.gold", lang);
            
            // Then
            assertEquals(none, MoneyCalculation.moneyOf(0, lang));
            assertEquals(bronze + ": 1", MoneyCalculation.moneyOf(1, lang));
            assertEquals(bronze + ": 1, " + silver + ": 1", MoneyCalculation.moneyOf(101, lang));
            assertEquals(bronze + ": 55, " + silver + ": 55", MoneyCalculation.moneyOf(5555, lang));
            assertEquals(bronze + ": 1, " + silver + ": 1, " + gold + ": 1", MoneyCalculation.moneyOf(10101, lang));
            assertEquals(bronze + ": 55, " + silver + ": 55, " + gold + ": 55", MoneyCalculation.moneyOf(555555, lang));
            assertEquals(bronze + ": 55, " + silver + ": 55, " + gold + ": 55555", MoneyCalculation.moneyOf(555555555, lang));
            assertEquals(gold + ": 1", MoneyCalculation.moneyOf(MoneyCalculation.goldOf(1), lang));
            assertEquals(silver + ": 1, " + gold + ": 1", MoneyCalculation.moneyOf(MoneyCalculation.goldOf(1) + MoneyCalculation.silverOf(1), lang));
            assertEquals(silver + ": 55, " + gold + ": 55555", MoneyCalculation.moneyOf(555555500, lang));
            assertEquals(bronze + ": 55, " + gold + ": 55", MoneyCalculation.moneyOf(550055, lang));
            assertEquals(bronze + ": 55, " + gold + ": 55", MoneyCalculation.moneyOf(MoneyCalculation.goldOf(55) + MoneyCalculation.bronzeOf(55), lang));
        }
        
    }

    @Test
    void whenRoundShouldRoundDownDependingOnTheValueTest() {
        assertEquals(7, MoneyCalculation.round(7));
        assertEquals(73, MoneyCalculation.round(73));
        assertEquals(100, MoneyCalculation.round(MoneyCalculation.silverOf(1)));
        assertEquals(100, MoneyCalculation.round(MoneyCalculation.silverOf(1) + 1));
        assertEquals(100, MoneyCalculation.round(MoneyCalculation.silverOf(1) + 9));
        assertEquals(110, MoneyCalculation.round(MoneyCalculation.silverOf(1) + 13));
        assertEquals(1110, MoneyCalculation.round(MoneyCalculation.silverOf(11) + 11));
        assertEquals(11100, MoneyCalculation.round(MoneyCalculation.goldOf(1) + MoneyCalculation.silverOf(11) + 11));
    }

    @Test
    void whenDivideShouldReturnValidTest() {
        assertEquals(4, MoneyCalculation.round(7 * 7/10));
        assertEquals(5, MoneyCalculation.round(8 * 7/10));
        assertEquals(14, MoneyCalculation.round(21 * 7/10));
        assertEquals(99, MoneyCalculation.round(142 * 7/10));
        assertEquals(100, MoneyCalculation.round(143 * 7/10));
        assertEquals(100, MoneyCalculation.round(157 * 7/10));
        assertEquals(110, MoneyCalculation.round(158 * 7/10));
        assertEquals(7770, MoneyCalculation.round((MoneyCalculation.goldOf(1) + MoneyCalculation.silverOf(11) + 11) * 7/10));
        assertEquals(10000, MoneyCalculation.round((MoneyCalculation.goldOf(1) + MoneyCalculation.silverOf(43) + 99) * 7/10));
    }

}