package com.wildtigerrr.StoryOfCamelot.bin.service;

import com.wildtigerrr.StoryOfCamelot.ServiceBaseTest;
import com.wildtigerrr.StoryOfCamelot.bin.enums.RandomDistribution;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

@Log4j2
class RandomUtilsTest extends ServiceBaseTest {

    private Map<Integer, Integer> mapping;

    @Test
    void whenUniformShouldBeLineTest() {
        int valuesCount = 100;
        setMappingForRandom(valuesCount);
        int value;
        for (int i = 0; i < 2500; i++) {
            value = RandomDistribution.DISCRETE.nextInt(valuesCount) + 1;
            mapping.put(value, mapping.get(value) + 1);
        }
        printRandom();
    }

    @Test
    void whenUniformBooleanShouldBeLineTest() {
        for (int i = 0; i < 25; i++) {
            log.info(RandomDistribution.DISCRETE_BOOLEAN.nextBoolean());
        }
    }

    @Test
    void whenGaussianShouldByWaveTest() {
        int valuesCount = 1000;
        int attempts = 10000000;
        setMappingForRandom(valuesCount);
        int value;
        for (int i = 0; i < attempts; i++) {
            value = RandomDistribution.GAUSSIAN.nextInt(valuesCount);
            mapping.put(value, mapping.get(value) + 1);
        }
        printRandom(attempts);
    }

    @Test
    void whenExponentialShouldByOneSideTest() {
        int valuesCount = 200;
        int attempts = 10000000;
        setMappingForRandom(valuesCount);
        int value;
        for (int i = 0; i < attempts; i++) {
//            value = RandomUtils.nextExponential(100, lambda);
            value = RandomDistribution.EXPONENTIAL_MIDDLE.nextInt();
//            if (value <= 2) continue;
            try {
                mapping.put(value, mapping.get(value) + 1);
            } catch (NullPointerException e) {
                log.warn("Exception with: " + value);
            }
        }
        printRandom(attempts);
    }

//    @Test
//    void whenGammaShouldByOneSideTest() {
//        int valuesCount = 5000;
//        int attempts = 10000000;
//        setMappingForRandom(valuesCount);
//        int value;
//        double lambda = RandomDistribution.EXPONENTIAL_MIDDLE.getLambda();
//        for (int i = 0; i < attempts; i++) {
//            value = RandomUtils.nextGamma(lambda, 250);
//            try {
//                mapping.put(value, mapping.get(value) + 1);
//            } catch (NullPointerException e) {
//                log.warn("Exception with: " + value);
//            }
//        }
//        printRandom(attempts);
//    }


    private void printRandom() {
        log.info("Random mapping:");
        for (Integer key : mapping.keySet()) {
            log.info(key + " -> " + "|".repeat(Math.max(0, mapping.get(key))) + " - " + mapping.get(key));
        }
    }


    private void printRandom(int attempts) {
        log.info("Random mapping:");
        for (Integer key : mapping.keySet()) {
            log.info(key + " -> " + " - " + mapping.get(key) + " (" + (((double)mapping.get(key))/attempts * 100) + "%)");
        }
    }

    private void setMappingForRandom(int mappingLength) {
        mapping = new HashMap<>();
        for (int i = 0; i < mappingLength + 1; i++) {
            mapping.put(i, 0);
        }
    }

}