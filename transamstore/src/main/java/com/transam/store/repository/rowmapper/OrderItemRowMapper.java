package com.transam.store.repository.rowmapper;

import com.transam.store.domain.OrderItem;
import io.r2dbc.spi.Row;
import java.math.BigDecimal;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link OrderItem}, with proper type conversions.
 */
@Service
public class OrderItemRowMapper implements BiFunction<Row, String, OrderItem> {

    private final ColumnConverter converter;

    public OrderItemRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link OrderItem} stored in the database.
     */
    @Override
    public OrderItem apply(Row row, String prefix) {
        OrderItem entity = new OrderItem();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setQuantity(converter.fromRow(row, prefix + "_quantity", Integer.class));
        entity.setTotalPrice(converter.fromRow(row, prefix + "_total_price", BigDecimal.class));
        entity.setTicketId(converter.fromRow(row, prefix + "_ticket_id", Long.class));
        entity.setOrderId(converter.fromRow(row, prefix + "_order_id", Long.class));
        return entity;
    }
}
