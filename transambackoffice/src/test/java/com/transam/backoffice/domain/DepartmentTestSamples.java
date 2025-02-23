package com.transam.backoffice.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class DepartmentTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static Department getDepartmentSample1() {
        return new Department().id(1L).departmentName("departmentName1").location("location1").division("division1");
    }

    public static Department getDepartmentSample2() {
        return new Department().id(2L).departmentName("departmentName2").location("location2").division("division2");
    }

    public static Department getDepartmentRandomSampleGenerator() {
        return new Department()
            .id(longCount.incrementAndGet())
            .departmentName(UUID.randomUUID().toString())
            .location(UUID.randomUUID().toString())
            .division(UUID.randomUUID().toString());
    }
}
