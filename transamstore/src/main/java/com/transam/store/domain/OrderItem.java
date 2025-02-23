package com.transam.store.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/**
 * A OrderItem.
 */
@Table("order_item")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class OrderItem implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column("id")
    private Long id;

    @NotNull(message = "must not be null")
    @Min(value = 0)
    @Column("quantity")
    private Integer quantity;

    @NotNull(message = "must not be null")
    @DecimalMin(value = "0")
    @Column("total_price")
    private BigDecimal totalPrice;

    @org.springframework.data.annotation.Transient
    private Ticket ticket;

    @org.springframework.data.annotation.Transient
    @JsonIgnoreProperties(value = { "orderItems", "customer" }, allowSetters = true)
    private TicketOrder order;

    @Column("ticket_id")
    private Long ticketId;

    @Column("order_id")
    private Long orderId;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public OrderItem id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getQuantity() {
        return this.quantity;
    }

    public OrderItem quantity(Integer quantity) {
        this.setQuantity(quantity);
        return this;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getTotalPrice() {
        return this.totalPrice;
    }

    public OrderItem totalPrice(BigDecimal totalPrice) {
        this.setTotalPrice(totalPrice);
        return this;
    }

    public void setTotalPrice(BigDecimal totalPrice) {
        this.totalPrice = totalPrice != null ? totalPrice.stripTrailingZeros() : null;
    }

    public Ticket getTicket() {
        return this.ticket;
    }

    public void setTicket(Ticket ticket) {
        this.ticket = ticket;
        this.ticketId = ticket != null ? ticket.getId() : null;
    }

    public OrderItem ticket(Ticket ticket) {
        this.setTicket(ticket);
        return this;
    }

    public TicketOrder getOrder() {
        return this.order;
    }

    public void setOrder(TicketOrder ticketOrder) {
        this.order = ticketOrder;
        this.orderId = ticketOrder != null ? ticketOrder.getId() : null;
    }

    public OrderItem order(TicketOrder ticketOrder) {
        this.setOrder(ticketOrder);
        return this;
    }

    public Long getTicketId() {
        return this.ticketId;
    }

    public void setTicketId(Long ticket) {
        this.ticketId = ticket;
    }

    public Long getOrderId() {
        return this.orderId;
    }

    public void setOrderId(Long ticketOrder) {
        this.orderId = ticketOrder;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof OrderItem)) {
            return false;
        }
        return getId() != null && getId().equals(((OrderItem) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "OrderItem{" +
            "id=" + getId() +
            ", quantity=" + getQuantity() +
            ", totalPrice=" + getTotalPrice() +
            "}";
    }
}
