package com.transam.store.domain;

import static com.transam.store.domain.CustomerTestSamples.*;
import static com.transam.store.domain.TicketOrderTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.transam.store.web.rest.TestUtil;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class CustomerTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Customer.class);
        Customer customer1 = getCustomerSample1();
        Customer customer2 = new Customer();
        assertThat(customer1).isNotEqualTo(customer2);

        customer2.setId(customer1.getId());
        assertThat(customer1).isEqualTo(customer2);

        customer2 = getCustomerSample2();
        assertThat(customer1).isNotEqualTo(customer2);
    }

    @Test
    void orderTest() {
        Customer customer = getCustomerRandomSampleGenerator();
        TicketOrder ticketOrderBack = getTicketOrderRandomSampleGenerator();

        customer.addOrder(ticketOrderBack);
        assertThat(customer.getOrders()).containsOnly(ticketOrderBack);
        assertThat(ticketOrderBack.getCustomer()).isEqualTo(customer);

        customer.removeOrder(ticketOrderBack);
        assertThat(customer.getOrders()).doesNotContain(ticketOrderBack);
        assertThat(ticketOrderBack.getCustomer()).isNull();

        customer.orders(new HashSet<>(Set.of(ticketOrderBack)));
        assertThat(customer.getOrders()).containsOnly(ticketOrderBack);
        assertThat(ticketOrderBack.getCustomer()).isEqualTo(customer);

        customer.setOrders(new HashSet<>());
        assertThat(customer.getOrders()).doesNotContain(ticketOrderBack);
        assertThat(ticketOrderBack.getCustomer()).isNull();
    }
}
