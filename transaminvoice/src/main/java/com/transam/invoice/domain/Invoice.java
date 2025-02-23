package com.transam.invoice.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.transam.invoice.domain.enumeration.InvoiceStatus;
import com.transam.invoice.domain.enumeration.PaymentMethod;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;
import org.springframework.data.annotation.Id;
import org.springframework.data.domain.Persistable;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/**
 * A Invoice.
 */
@Table("invoice")
@JsonIgnoreProperties(value = { "new" })
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Invoice implements Serializable, Persistable<UUID> {

    private static final long serialVersionUID = 1L;

    @NotNull(message = "must not be null")
    @Id
    @Column("id")
    private UUID id;

    @NotNull(message = "must not be null")
    @Column("code")
    private String code;

    @NotNull(message = "must not be null")
    @Column("ticket_order_code")
    private String ticketOrderCode;

    @NotNull(message = "must not be null")
    @Column("date")
    private Instant date;

    @Column("details")
    private String details;

    @NotNull(message = "must not be null")
    @Column("status")
    private InvoiceStatus status;

    @NotNull(message = "must not be null")
    @Column("payment_method")
    private PaymentMethod paymentMethod;

    @NotNull(message = "must not be null")
    @Column("payment_date")
    private Instant paymentDate;

    @NotNull(message = "must not be null")
    @Column("payment_amount")
    private BigDecimal paymentAmount;

    @org.springframework.data.annotation.Transient
    private boolean isPersisted;

    @org.springframework.data.annotation.Transient
    private User user;

    @Column("user_id")
    private Long userId;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public UUID getId() {
        return this.id;
    }

    public Invoice id(UUID id) {
        this.setId(id);
        return this;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getCode() {
        return this.code;
    }

    public Invoice code(String code) {
        this.setCode(code);
        return this;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getTicketOrderCode() {
        return this.ticketOrderCode;
    }

    public Invoice ticketOrderCode(String ticketOrderCode) {
        this.setTicketOrderCode(ticketOrderCode);
        return this;
    }

    public void setTicketOrderCode(String ticketOrderCode) {
        this.ticketOrderCode = ticketOrderCode;
    }

    public Instant getDate() {
        return this.date;
    }

    public Invoice date(Instant date) {
        this.setDate(date);
        return this;
    }

    public void setDate(Instant date) {
        this.date = date;
    }

    public String getDetails() {
        return this.details;
    }

    public Invoice details(String details) {
        this.setDetails(details);
        return this;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public InvoiceStatus getStatus() {
        return this.status;
    }

    public Invoice status(InvoiceStatus status) {
        this.setStatus(status);
        return this;
    }

    public void setStatus(InvoiceStatus status) {
        this.status = status;
    }

    public PaymentMethod getPaymentMethod() {
        return this.paymentMethod;
    }

    public Invoice paymentMethod(PaymentMethod paymentMethod) {
        this.setPaymentMethod(paymentMethod);
        return this;
    }

    public void setPaymentMethod(PaymentMethod paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public Instant getPaymentDate() {
        return this.paymentDate;
    }

    public Invoice paymentDate(Instant paymentDate) {
        this.setPaymentDate(paymentDate);
        return this;
    }

    public void setPaymentDate(Instant paymentDate) {
        this.paymentDate = paymentDate;
    }

    public BigDecimal getPaymentAmount() {
        return this.paymentAmount;
    }

    public Invoice paymentAmount(BigDecimal paymentAmount) {
        this.setPaymentAmount(paymentAmount);
        return this;
    }

    public void setPaymentAmount(BigDecimal paymentAmount) {
        this.paymentAmount = paymentAmount != null ? paymentAmount.stripTrailingZeros() : null;
    }

    @org.springframework.data.annotation.Transient
    @Override
    public boolean isNew() {
        return !this.isPersisted;
    }

    public Invoice setIsPersisted() {
        this.isPersisted = true;
        return this;
    }

    public User getUser() {
        return this.user;
    }

    public void setUser(User user) {
        this.user = user;
        this.userId = user != null ? user.getId() : null;
    }

    public Invoice user(User user) {
        this.setUser(user);
        return this;
    }

    public Long getUserId() {
        return this.userId;
    }

    public void setUserId(Long user) {
        this.userId = user;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Invoice)) {
            return false;
        }
        return getId() != null && getId().equals(((Invoice) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Invoice{" +
            "id=" + getId() +
            ", code='" + getCode() + "'" +
            ", ticketOrderCode='" + getTicketOrderCode() + "'" +
            ", date='" + getDate() + "'" +
            ", details='" + getDetails() + "'" +
            ", status='" + getStatus() + "'" +
            ", paymentMethod='" + getPaymentMethod() + "'" +
            ", paymentDate='" + getPaymentDate() + "'" +
            ", paymentAmount=" + getPaymentAmount() +
            "}";
    }
}
