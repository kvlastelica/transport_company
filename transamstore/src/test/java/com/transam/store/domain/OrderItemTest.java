package com.transam.store.domain;

import static com.transam.store.domain.OrderItemTestSamples.*;
import static com.transam.store.domain.TicketOrderTestSamples.*;
import static com.transam.store.domain.TicketTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.transam.store.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class OrderItemTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(OrderItem.class);
        OrderItem orderItem1 = getOrderItemSample1();
        OrderItem orderItem2 = new OrderItem();
        assertThat(orderItem1).isNotEqualTo(orderItem2);

        orderItem2.setId(orderItem1.getId());
        assertThat(orderItem1).isEqualTo(orderItem2);

        orderItem2 = getOrderItemSample2();
        assertThat(orderItem1).isNotEqualTo(orderItem2);
    }

    @Test
    void ticketTest() {
        OrderItem orderItem = getOrderItemRandomSampleGenerator();
        Ticket ticketBack = getTicketRandomSampleGenerator();

        orderItem.setTicket(ticketBack);
        assertThat(orderItem.getTicket()).isEqualTo(ticketBack);

        orderItem.ticket(null);
        assertThat(orderItem.getTicket()).isNull();
    }

    @Test
    void orderTest() {
        OrderItem orderItem = getOrderItemRandomSampleGenerator();
        TicketOrder ticketOrderBack = getTicketOrderRandomSampleGenerator();

        orderItem.setOrder(ticketOrderBack);
        assertThat(orderItem.getOrder()).isEqualTo(ticketOrderBack);

        orderItem.order(null);
        assertThat(orderItem.getOrder()).isNull();
    }
}
