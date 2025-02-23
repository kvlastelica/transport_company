package com.transam.invoice.domain;

import java.util.UUID;

public class InvoiceTestSamples {

    public static Invoice getInvoiceSample1() {
        return new Invoice()
            .id(UUID.fromString("23d8dc04-a48b-45d9-a01d-4b728f0ad4aa"))
            .code("code1")
            .ticketOrderCode("ticketOrderCode1")
            .details("details1");
    }

    public static Invoice getInvoiceSample2() {
        return new Invoice()
            .id(UUID.fromString("ad79f240-3727-46c3-b89f-2cf6ebd74367"))
            .code("code2")
            .ticketOrderCode("ticketOrderCode2")
            .details("details2");
    }

    public static Invoice getInvoiceRandomSampleGenerator() {
        return new Invoice()
            .id(UUID.randomUUID())
            .code(UUID.randomUUID().toString())
            .ticketOrderCode(UUID.randomUUID().toString())
            .details(UUID.randomUUID().toString());
    }
}
