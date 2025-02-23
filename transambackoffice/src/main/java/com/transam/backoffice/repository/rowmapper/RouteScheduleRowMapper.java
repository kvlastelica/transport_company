package com.transam.backoffice.repository.rowmapper;

import com.transam.backoffice.domain.RouteSchedule;
import io.r2dbc.spi.Row;
import java.time.Instant;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link RouteSchedule}, with proper type conversions.
 */
@Service
public class RouteScheduleRowMapper implements BiFunction<Row, String, RouteSchedule> {

    private final ColumnConverter converter;

    public RouteScheduleRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link RouteSchedule} stored in the database.
     */
    @Override
    public RouteSchedule apply(Row row, String prefix) {
        RouteSchedule entity = new RouteSchedule();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setCode(converter.fromRow(row, prefix + "_code", String.class));
        entity.setDescription(converter.fromRow(row, prefix + "_description", String.class));
        entity.setDeparture(converter.fromRow(row, prefix + "_departure", Instant.class));
        entity.setArrival(converter.fromRow(row, prefix + "_arrival", Instant.class));
        entity.setRouteId(converter.fromRow(row, prefix + "_route_id", Long.class));
        return entity;
    }
}
