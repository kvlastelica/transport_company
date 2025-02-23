package com.transam.backoffice.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class RouteTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));
    private static final AtomicInteger intCount = new AtomicInteger(random.nextInt() + (2 * Short.MAX_VALUE));

    public static Route getRouteSample1() {
        return new Route()
            .id(1L)
            .code("code1")
            .description("description1")
            .start("start1")
            .destination("destination1")
            .passengerCapacity(1);
    }

    public static Route getRouteSample2() {
        return new Route()
            .id(2L)
            .code("code2")
            .description("description2")
            .start("start2")
            .destination("destination2")
            .passengerCapacity(2);
    }

    public static Route getRouteRandomSampleGenerator() {
        return new Route()
            .id(longCount.incrementAndGet())
            .code(UUID.randomUUID().toString())
            .description(UUID.randomUUID().toString())
            .start(UUID.randomUUID().toString())
            .destination(UUID.randomUUID().toString())
            .passengerCapacity(intCount.incrementAndGet());
    }
}
