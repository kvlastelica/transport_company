package com.transam.backoffice.repository.rowmapper;

import com.transam.backoffice.domain.Route;
import io.r2dbc.spi.Row;
import java.math.BigDecimal;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link Route}, with proper type conversions.
 */
@Service
public class RouteRowMapper implements BiFunction<Row, String, Route> {

    private final ColumnConverter converter;

    public RouteRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link Route} stored in the database.
     */
    @Override
    public Route apply(Row row, String prefix) {
        Route entity = new Route();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setCode(converter.fromRow(row, prefix + "_code", String.class));
        entity.setDescription(converter.fromRow(row, prefix + "_description", String.class));
        entity.setStart(converter.fromRow(row, prefix + "_start", String.class));
        entity.setDestination(converter.fromRow(row, prefix + "_destination", String.class));
        entity.setPassengerCapacity(converter.fromRow(row, prefix + "_passenger_capacity", Integer.class));
        entity.setParcelTotalWeight(converter.fromRow(row, prefix + "_parcel_total_weight", BigDecimal.class));
        return entity;
    }
}
