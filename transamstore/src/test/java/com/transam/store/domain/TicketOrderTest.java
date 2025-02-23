package com.transam.store.domain;

import static com.transam.store.domain.CustomerTestSamples.*;
import static com.transam.store.domain.OrderItemTestSamples.*;
import static com.transam.store.domain.TicketOrderTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.transam.store.web.rest.TestUtil;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class TicketOrderTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(TicketOrder.class);
        TicketOrder ticketOrder1 = getTicketOrderSample1();
        TicketOrder ticketOrder2 = new TicketOrder();
        assertThat(ticketOrder1).isNotEqualTo(ticketOrder2);

        ticketOrder2.setId(ticketOrder1.getId());
        assertThat(ticketOrder1).isEqualTo(ticketOrder2);

        ticketOrder2 = getTicketOrderSample2();
        assertThat(ticketOrder1).isNotEqualTo(ticketOrder2);
    }

    @Test
    void orderItemTest() {
        TicketOrder ticketOrder = getTicketOrderRandomSampleGenerator();
        OrderItem orderItemBack = getOrderItemRandomSampleGenerator();

        ticketOrder.addOrderItem(orderItemBack);
        assertThat(ticketOrder.getOrderItems()).containsOnly(orderItemBack);
        assertThat(orderItemBack.getOrder()).isEqualTo(ticketOrder);

        ticketOrder.removeOrderItem(orderItemBack);
        assertThat(ticketOrder.getOrderItems()).doesNotContain(orderItemBack);
        assertThat(orderItemBack.getOrder()).isNull();

        ticketOrder.orderItems(new HashSet<>(Set.of(orderItemBack)));
        assertThat(ticketOrder.getOrderItems()).containsOnly(orderItemBack);
        assertThat(orderItemBack.getOrder()).isEqualTo(ticketOrder);

        ticketOrder.setOrderItems(new HashSet<>());
        assertThat(ticketOrder.getOrderItems()).doesNotContain(orderItemBack);
        assertThat(orderItemBack.getOrder()).isNull();
    }

    @Test
    void customerTest() {
        TicketOrder ticketOrder = getTicketOrderRandomSampleGenerator();
        Customer customerBack = getCustomerRandomSampleGenerator();

        ticketOrder.setCustomer(customerBack);
        assertThat(ticketOrder.getCustomer()).isEqualTo(customerBack);

        ticketOrder.customer(null);
        assertThat(ticketOrder.getCustomer()).isNull();
    }
}
