package com.demo.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class MessageTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static Message getMessageSample1() {
        return new Message().id(1L).name("name1").email("email1").phone("phone1").city("city1").otherCountry("otherCountry1");
    }

    public static Message getMessageSample2() {
        return new Message().id(2L).name("name2").email("email2").phone("phone2").city("city2").otherCountry("otherCountry2");
    }

    public static Message getMessageRandomSampleGenerator() {
        return new Message()
            .id(longCount.incrementAndGet())
            .name(UUID.randomUUID().toString())
            .email(UUID.randomUUID().toString())
            .phone(UUID.randomUUID().toString())
            .city(UUID.randomUUID().toString())
            .otherCountry(UUID.randomUUID().toString());
    }
}
