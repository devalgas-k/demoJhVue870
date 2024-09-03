package com.demo.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class JobTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static Job getJobSample1() {
        return new Job().id(1L).jobTitle("jobTitle1").maxSalary(1L).codeCode(UUID.fromString("23d8dc04-a48b-45d9-a01d-4b728f0ad4aa"));
    }

    public static Job getJobSample2() {
        return new Job().id(2L).jobTitle("jobTitle2").maxSalary(2L).codeCode(UUID.fromString("ad79f240-3727-46c3-b89f-2cf6ebd74367"));
    }

    public static Job getJobRandomSampleGenerator() {
        return new Job()
            .id(longCount.incrementAndGet())
            .jobTitle(UUID.randomUUID().toString())
            .maxSalary(longCount.incrementAndGet())
            .codeCode(UUID.randomUUID());
    }
}
