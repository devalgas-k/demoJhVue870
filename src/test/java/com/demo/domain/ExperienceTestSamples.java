package com.demo.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class ExperienceTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static Experience getExperienceSample1() {
        return new Experience().id(1L).title("title1").company("company1");
    }

    public static Experience getExperienceSample2() {
        return new Experience().id(2L).title("title2").company("company2");
    }

    public static Experience getExperienceRandomSampleGenerator() {
        return new Experience().id(longCount.incrementAndGet()).title(UUID.randomUUID().toString()).company(UUID.randomUUID().toString());
    }
}
