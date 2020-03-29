package com.wildtigerrr.StoryOfCamelot.bin.service;

import java.util.concurrent.ThreadLocalRandom;

public class RandomUtils {

    /**
     * Generates random int in range [0, limit] with uniform (discrete) distribution
     *
     * @param limit - maximum
     * @return int
     */
    public static int nextInt(int limit) {
        return rng().nextInt(limit);
    }

    /**
     * Generates random boolean with uniform (discrete) distribution
     *
     * @return boolean
     */
    public static boolean nextBoolean() {
        return rng().nextBoolean();
    }

    /**
     * Generates random int in range [0, limit] with normal (Gaussian) distribution:
     *
     * under 70% of instances will tend to have a value within one standard deviation (+- limit/8)
     * under 95% of instances will tend to have a value within two standard deviations (+- limit/4)
     * more than 99% of instances will tend to have a value within three standard deviations (+- limit/2)
     *
     * https://www.javamex.com/tutorials/random_numbers/gaussian_distribution_2.shtml
     *
     * @param limit - maximum
     * @return int
     */
    public static int nextGaussian(int limit) {
        double middle = ((double) limit) / 2;
        double deviation = middle / 4;
        int next;
        do {
            next = (int) Math.round(rng().nextGaussian() * deviation + middle);
        } while (!NumberUtils.betweenIncl(next, 0, limit));
        return next;
    }

//    /**
//     * Generates random int
//     *
//     * https://habr.com/ru/post/263993/
//     *
//     * @param lambda
//     * @param multiplier - shift from left side
//     * @return int
//     */
//    public static int nextGamma(double lambda, int multiplier) {
//        double next = 0;
//        for (int i = 0; i < multiplier; i++) {
//            next += nextExponentialDouble(lambda);
//        }
//        return (int) Math.ceil(next);
//    }

    /**
     * Generates random int in range [1, limit] inclusive with exponential distribution:
     *
     * @param lambda:
     * lambda = 0.05; // 5% for 1... 1% for 32... up to 100 in normal, may be more than 200
     * lambda = 0.1; // 10% for 1, 9% for 2, 8% for 3, 7% for 4, 6% for 5...
     * lambda = 0.3; // 26% for 1, 20% for 2, 14% for 3, 11% for 4, 8% for 5, 6% for 6, 4.5% for 7, 3% for 8...
     * lambda = 0.7; // 25% for 2, 12% for 3, 6% for 4, 3% for 5, 1.5% for 6...
     * lambda = 1; // 23% for 2, 8.5% for 3, 3% for 4...
     * lambda = 1.5; // 17% for 2, 3.5% for 3, 0.75% for 4...
     * lambda = 2; // 12% for 2, 1.6% for 3...
     * lambda = 3; // 5% for 2, 0.2% for 3...
     * lambda = 5; // 0.6% for 2, 0.005% for 3...
     * lambda = 7; // 0.08% for 2...
     * lambda = 9; // 0.012% for 2...
     * @return int
     */
    public static int nextExponential(int limit, double lambda) {
        int next;
        do {
            next = (int) Math.ceil(nextExponentialDouble(lambda));
        } while (!NumberUtils.betweenRightIncl(next, 0, limit));
        return next;
    }

    /**
     * Generates random double with exponential distribution:
     *
     * @param lambda - distribution parameter
     * @return int
     */
    public static double nextExponentialDouble(double lambda) {
        return Math.log(1 - rng().nextDouble())/(-lambda);
    }

    private static ThreadLocalRandom rng() {
        return ThreadLocalRandom.current();
    }

}