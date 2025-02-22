package com.jhipster.demo.notification.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class NotificationTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static Notification getNotificationSample1() {
        return new Notification().id("id1").details("details1").userId(1L).productId(1L);
    }

    public static Notification getNotificationSample2() {
        return new Notification().id("id2").details("details2").userId(2L).productId(2L);
    }

    public static Notification getNotificationRandomSampleGenerator() {
        return new Notification()
            .id(UUID.randomUUID().toString())
            .details(UUID.randomUUID().toString())
            .userId(longCount.incrementAndGet())
            .productId(longCount.incrementAndGet());
    }
}
