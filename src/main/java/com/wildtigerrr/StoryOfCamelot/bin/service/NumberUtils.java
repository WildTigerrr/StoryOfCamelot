package com.wildtigerrr.StoryOfCamelot.bin.service;

public class NumberUtils {

    /**
     * Return true if value in bounds as (,)
     *
     * @param value - value to check
     * @param leftBound - left side
     * @param rightBound - right side
     * @return boolean
     */
    public static boolean between(int value, int leftBound, int rightBound) {
        return value > leftBound && value < rightBound;
    }

    /**
     * Return true if value in bounds as [,]
     *
     * @param value - value to check
     * @param leftBound - left side
     * @param rightBound - right side
     * @return boolean
     */
    public static boolean betweenIncl(int value, int leftBound, int rightBound) {
        return value >= leftBound && value <= rightBound;
    }

    /**
     * Return true if value in bounds as (,]
     *
     * @param value - value to check
     * @param leftBound - left side
     * @param rightBound - right side
     * @return boolean
     */
    public static boolean betweenRightIncl(int value, int leftBound, int rightBound) {
        return value > leftBound && value <= rightBound;
    }

    /**
     * Return true if value in bounds as [,)
     *
     * @param value - value to check
     * @param leftBound - left side
     * @param rightBound - right side
     * @return boolean
     */
    public static boolean betweenLeftIncl(int value, int leftBound, int rightBound) {
        return value >= leftBound && value < rightBound;
    }

    /**
     * Round double with 2 decimal places
     *
     * @param value - double to round
     * @return double
     */
    public static double round(double value) {
        return Math.round(value * 100) / 100.0;
    }

    /**
     *
     * @param value - value to subtract percentage from
     * @param percentage - how much to subtract
     * @return int
     */
    public static int percentageSubtract(int value, int percentage) {
        return (int) (value * ((100 - percentage) / 100.0F));
    }

    /**
     *
     * @param value - value to subtract percentage from
     * @param percentage - how much to subtract
     * @return long
     */
    public static long percentageSubtract(long value, int percentage) {
        return (long) (value * ((100 - percentage) / 100.0F));
    }

}