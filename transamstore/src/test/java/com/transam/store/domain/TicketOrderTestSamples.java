package com.transam.store.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class TicketOrderTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static TicketOrder getTicketOrderSample1() {
        return new TicketOrder().id(1L).code("code1").invoiceId(1L);
    }

    public static TicketOrder getTicketOrderSample2() {
        return new TicketOrder().id(2L).code("code2").invoiceId(2L);
    }

    public static TicketOrder getTicketOrderRandomSampleGenerator() {
        return new TicketOrder().id(longCount.incrementAndGet()).code(UUID.randomUUID().toString()).invoiceId(longCount.incrementAndGet());
    }
}
