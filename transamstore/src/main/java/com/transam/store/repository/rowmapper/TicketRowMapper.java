package com.transam.store.repository.rowmapper;

import com.transam.store.domain.Ticket;
import com.transam.store.domain.enumeration.TicketType;
import io.r2dbc.spi.Row;
import java.math.BigDecimal;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link Ticket}, with proper type conversions.
 */
@Service
public class TicketRowMapper implements BiFunction<Row, String, Ticket> {

    private final ColumnConverter converter;

    public TicketRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link Ticket} stored in the database.
     */
    @Override
    public Ticket apply(Row row, String prefix) {
        Ticket entity = new Ticket();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setName(converter.fromRow(row, prefix + "_name", String.class));
        entity.setDescription(converter.fromRow(row, prefix + "_description", String.class));
        entity.setPrice(converter.fromRow(row, prefix + "_price", BigDecimal.class));
        entity.setProductSize(converter.fromRow(row, prefix + "_product_size", TicketType.class));
        entity.setImageContentType(converter.fromRow(row, prefix + "_image_content_type", String.class));
        entity.setImage(converter.fromRow(row, prefix + "_image", byte[].class));
        return entity;
    }
}
