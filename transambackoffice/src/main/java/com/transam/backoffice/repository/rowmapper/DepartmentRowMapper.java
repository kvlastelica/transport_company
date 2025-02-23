package com.transam.backoffice.repository.rowmapper;

import com.transam.backoffice.domain.Department;
import com.transam.backoffice.domain.enumeration.DepartmentType;
import io.r2dbc.spi.Row;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link Department}, with proper type conversions.
 */
@Service
public class DepartmentRowMapper implements BiFunction<Row, String, Department> {

    private final ColumnConverter converter;

    public DepartmentRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link Department} stored in the database.
     */
    @Override
    public Department apply(Row row, String prefix) {
        Department entity = new Department();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setDepartmentName(converter.fromRow(row, prefix + "_department_name", String.class));
        entity.setLocation(converter.fromRow(row, prefix + "_location", String.class));
        entity.setDivision(converter.fromRow(row, prefix + "_division", String.class));
        entity.setFormat(converter.fromRow(row, prefix + "_format", DepartmentType.class));
        return entity;
    }
}
