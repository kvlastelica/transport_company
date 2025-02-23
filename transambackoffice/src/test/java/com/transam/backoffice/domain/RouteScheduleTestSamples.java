package com.transam.backoffice.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class RouteScheduleTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static RouteSchedule getRouteScheduleSample1() {
        return new RouteSchedule().id(1L).code("code1").description("description1");
    }

    public static RouteSchedule getRouteScheduleSample2() {
        return new RouteSchedule().id(2L).code("code2").description("description2");
    }

    public static RouteSchedule getRouteScheduleRandomSampleGenerator() {
        return new RouteSchedule()
            .id(longCount.incrementAndGet())
            .code(UUID.randomUUID().toString())
            .description(UUID.randomUUID().toString());
    }
}
