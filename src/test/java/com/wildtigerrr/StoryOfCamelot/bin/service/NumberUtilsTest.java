package com.wildtigerrr.StoryOfCamelot.bin.service;

import com.wildtigerrr.StoryOfCamelot.ServiceBaseTest;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class NumberUtilsTest extends ServiceBaseTest {

    @Test
    void whenBetweenShouldCheckWithoutBoundsTest() {
        assertTrue(NumberUtils.between(1, 0, 2));
        assertFalse(NumberUtils.between(0, 0, 2));
        assertFalse(NumberUtils.between(-1, 0, 2));
    }

    @Test
    void whenBetweenInclShouldCheckWithBoundsTest() {
        assertTrue(NumberUtils.betweenIncl(1, 0, 2));
        assertTrue(NumberUtils.betweenIncl(0, 0, 2));
        assertTrue(NumberUtils.betweenIncl(2, 0, 2));
        assertFalse(NumberUtils.betweenIncl(-1, 0, 2));
    }

    @Test
    void whenBetweenRightInclShouldCheckWithRightBoundTest() {
        assertTrue(NumberUtils.betweenRightIncl(1, 0, 2));
        assertTrue(NumberUtils.betweenRightIncl(2, 0, 2));
        assertFalse(NumberUtils.betweenRightIncl(0, 0, 2));
        assertFalse(NumberUtils.betweenRightIncl(-1, 0, 2));
    }

    @Test
    void whenBetweenLeftInclShouldCheckWithLeftBoundsTest() {
        assertTrue(NumberUtils.betweenLeftIncl(1, 0, 2));
        assertTrue(NumberUtils.betweenLeftIncl(0, 0, 2));
        assertFalse(NumberUtils.betweenLeftIncl(2, 0, 2));
        assertFalse(NumberUtils.betweenLeftIncl(-1, 0, 2));
    }

    @Test
    void whenRoundShouldHaveTwoDecimalPointsMaxTest() {
        assertEquals(5.11, NumberUtils.round(5.111));
        assertEquals(5.12, NumberUtils.round(5.119));
        assertEquals(0, NumberUtils.round(0));
        assertEquals(0, NumberUtils.round(0.001));
        assertEquals(0.01, NumberUtils.round(0.005));
    }

}