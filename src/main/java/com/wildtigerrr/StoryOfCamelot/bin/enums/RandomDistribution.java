package com.wildtigerrr.StoryOfCamelot.bin.enums;

import com.wildtigerrr.StoryOfCamelot.bin.service.RandomUtils;
import com.wildtigerrr.StoryOfCamelot.exception.InvalidInputException;
import lombok.Getter;

@Getter
public enum RandomDistribution {
    DISCRETE(RandomType.DISCRETE),
    DISCRETE_BOOLEAN(RandomType.BOOLEAN),
    GAUSSIAN(RandomType.GAUSSIAN),
    // 5% for 1... 1% for 32...
    EXPONENTIAL_EXTREMELY_OFTEN(RandomType.EXPONENTIAL, 0.05),
    // 10% for 1, 9% for 2, 8% for 3, 7% for 4, 6% for 5...
    EXPONENTIAL_SUPER_OFTEN(RandomType.EXPONENTIAL, 0.1),
    // 26% for 1, 20% for 2, 14% for 3, 11% for 4, 8% for 5, 6% for 6, 4.5% for 7, 3% for 8...
    EXPONENTIAL_VERY_OFTEN(RandomType.EXPONENTIAL, 0.3),
    // 25% for 2, 12% for 3, 6% for 4, 3% for 5, 1.5% for 6...
    EXPONENTIAL_OFTEN(RandomType.EXPONENTIAL, 0.7),
    // 23% for 2, 8.5% for 3, 3% for 4...
    EXPONENTIAL_MIDDLE_OFTEN(RandomType.EXPONENTIAL, 1.0),
    // 17% for 2, 3.5% for 3, 0.75% for 4...
    EXPONENTIAL_MIDDLE(RandomType.EXPONENTIAL, 1.5),
    // 12% for 2, 1.6% for 3...
    EXPONENTIAL_MIDDLE_RARE(RandomType.EXPONENTIAL, 2.0),
    // 5% for 2, 0.2% for 3...
    EXPONENTIAL_RARE(RandomType.EXPONENTIAL, 10, 3.0),
    // 0.6% for 2, 0.005% for 3...
    EXPONENTIAL_VERY_RARE(RandomType.EXPONENTIAL, 10, 5.0),
    // 0.08% for 2...
    EXPONENTIAL_SUPER_RARE(RandomType.EXPONENTIAL, 10, 7.0),
    // 0.012% for 2...
    EXPONENTIAL_EXTREMELY_RARE(RandomType.EXPONENTIAL, 10, 9.0);
    
    private final int defaultLimit;
    private final double lambda;
    private final RandomType type;

    RandomDistribution(RandomType type, int defaultLimit, double lambda) {
        this.defaultLimit = defaultLimit;
        this.lambda = lambda;
        this.type = type;
    }
    
    RandomDistribution(RandomType type, int defaultLimit) {
        this(type, defaultLimit, 1.0);
    }
    
    RandomDistribution(RandomType type, double lambda) {
        this(type, 100, lambda);
    }

    RandomDistribution(RandomType type) {
        this(type, 100, 1.0);
    }

    private int nextIntByType(int limit) {
        limit = limit <= 0 ? getDefaultLimit() : limit;
        switch (getType()) {
            case DISCRETE: return RandomUtils.nextInt(limit);
            case GAUSSIAN: return RandomUtils.nextGaussian(limit);
            case EXPONENTIAL: return RandomUtils.nextExponential(limit, getLambda());
            case BOOLEAN: return RandomUtils.nextBoolean() ? 1 : 0;
            default: throw new InvalidInputException("Wrong RandomType: " + getType());
        }
    }
    
    public int nextInt(int limit) {
        return nextIntByType(limit);
    }

    public int nextInt() {
        return nextIntByType(getDefaultLimit());
    }
    
    public boolean nextBoolean() {
        return RandomUtils.nextBoolean();
    }

    enum RandomType {
        DISCRETE,
        BOOLEAN,
        GAUSSIAN,
        EXPONENTIAL
    }
    
}
