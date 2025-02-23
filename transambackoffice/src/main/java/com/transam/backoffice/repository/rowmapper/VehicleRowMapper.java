package com.transam.backoffice.repository.rowmapper;

import com.transam.backoffice.domain.Vehicle;
import com.transam.backoffice.domain.enumeration.VehicleType;
import io.r2dbc.spi.Row;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link Vehicle}, with proper type conversions.
 */
@Service
public class VehicleRowMapper implements BiFunction<Row, String, Vehicle> {

    private final ColumnConverter converter;

    public VehicleRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link Vehicle} stored in the database.
     */
    @Override
    public Vehicle apply(Row row, String prefix) {
        Vehicle entity = new Vehicle();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setCode(converter.fromRow(row, prefix + "_code", String.class));
        entity.setDescription(converter.fromRow(row, prefix + "_description", String.class));
        entity.setFormat(converter.fromRow(row, prefix + "_format", VehicleType.class));
        return entity;
    }
}
