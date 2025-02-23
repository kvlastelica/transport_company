package com.transam.store.repository.rowmapper;

import com.transam.store.domain.TicketOrder;
import com.transam.store.domain.enumeration.OrderStatus;
import io.r2dbc.spi.Row;
import java.time.Instant;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link TicketOrder}, with proper type conversions.
 */
@Service
public class TicketOrderRowMapper implements BiFunction<Row, String, TicketOrder> {

    private final ColumnConverter converter;

    public TicketOrderRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link TicketOrder} stored in the database.
     */
    @Override
    public TicketOrder apply(Row row, String prefix) {
        TicketOrder entity = new TicketOrder();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setPlacedDate(converter.fromRow(row, prefix + "_placed_date", Instant.class));
        entity.setStatus(converter.fromRow(row, prefix + "_status", OrderStatus.class));
        entity.setCode(converter.fromRow(row, prefix + "_code", String.class));
        entity.setInvoiceId(converter.fromRow(row, prefix + "_invoice_id", Long.class));
        entity.setCustomerId(converter.fromRow(row, prefix + "_customer_id", Long.class));
        return entity;
    }
}
