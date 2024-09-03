package com.demo.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class ExpertiseTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));
    private static final AtomicInteger intCount = new AtomicInteger(random.nextInt() + (2 * Short.MAX_VALUE));

    public static Expertise getExpertiseSample1() {
        return new Expertise().id(1L).title("title1").level(1);
    }

    public static Expertise getExpertiseSample2() {
        return new Expertise().id(2L).title("title2").level(2);
    }

    public static Expertise getExpertiseRandomSampleGenerator() {
        return new Expertise().id(longCount.incrementAndGet()).title(UUID.randomUUID().toString()).level(intCount.incrementAndGet());
    }
}
