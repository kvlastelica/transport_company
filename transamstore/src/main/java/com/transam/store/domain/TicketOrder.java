package com.transam.store.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.transam.store.domain.enumeration.OrderStatus;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/**
 * A TicketOrder.
 */
@Table("ticket_order")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class TicketOrder implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column("id")
    private Long id;

    @NotNull(message = "must not be null")
    @Column("placed_date")
    private Instant placedDate;

    @NotNull(message = "must not be null")
    @Column("status")
    private OrderStatus status;

    @NotNull(message = "must not be null")
    @Column("code")
    private String code;

    @Column("invoice_id")
    private Long invoiceId;

    @org.springframework.data.annotation.Transient
    @JsonIgnoreProperties(value = { "ticket", "order" }, allowSetters = true)
    private Set<OrderItem> orderItems = new HashSet<>();

    @org.springframework.data.annotation.Transient
    @JsonIgnoreProperties(value = { "user", "orders" }, allowSetters = true)
    private Customer customer;

    @Column("customer_id")
    private Long customerId;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public TicketOrder id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Instant getPlacedDate() {
        return this.placedDate;
    }

    public TicketOrder placedDate(Instant placedDate) {
        this.setPlacedDate(placedDate);
        return this;
    }

    public void setPlacedDate(Instant placedDate) {
        this.placedDate = placedDate;
    }

    public OrderStatus getStatus() {
        return this.status;
    }

    public TicketOrder status(OrderStatus status) {
        this.setStatus(status);
        return this;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    public String getCode() {
        return this.code;
    }

    public TicketOrder code(String code) {
        this.setCode(code);
        return this;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Long getInvoiceId() {
        return this.invoiceId;
    }

    public TicketOrder invoiceId(Long invoiceId) {
        this.setInvoiceId(invoiceId);
        return this;
    }

    public void setInvoiceId(Long invoiceId) {
        this.invoiceId = invoiceId;
    }

    public Set<OrderItem> getOrderItems() {
        return this.orderItems;
    }

    public void setOrderItems(Set<OrderItem> orderItems) {
        if (this.orderItems != null) {
            this.orderItems.forEach(i -> i.setOrder(null));
        }
        if (orderItems != null) {
            orderItems.forEach(i -> i.setOrder(this));
        }
        this.orderItems = orderItems;
    }

    public TicketOrder orderItems(Set<OrderItem> orderItems) {
        this.setOrderItems(orderItems);
        return this;
    }

    public TicketOrder addOrderItem(OrderItem orderItem) {
        this.orderItems.add(orderItem);
        orderItem.setOrder(this);
        return this;
    }

    public TicketOrder removeOrderItem(OrderItem orderItem) {
        this.orderItems.remove(orderItem);
        orderItem.setOrder(null);
        return this;
    }

    public Customer getCustomer() {
        return this.customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
        this.customerId = customer != null ? customer.getId() : null;
    }

    public TicketOrder customer(Customer customer) {
        this.setCustomer(customer);
        return this;
    }

    public Long getCustomerId() {
        return this.customerId;
    }

    public void setCustomerId(Long customer) {
        this.customerId = customer;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof TicketOrder)) {
            return false;
        }
        return getId() != null && getId().equals(((TicketOrder) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "TicketOrder{" +
            "id=" + getId() +
            ", placedDate='" + getPlacedDate() + "'" +
            ", status='" + getStatus() + "'" +
            ", code='" + getCode() + "'" +
            ", invoiceId=" + getInvoiceId() +
            "}";
    }
}
