package com.transam.backoffice.repository.rowmapper;

import com.transam.backoffice.domain.Employee;
import io.r2dbc.spi.Row;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link Employee}, with proper type conversions.
 */
@Service
public class EmployeeRowMapper implements BiFunction<Row, String, Employee> {

    private final ColumnConverter converter;

    public EmployeeRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link Employee} stored in the database.
     */
    @Override
    public Employee apply(Row row, String prefix) {
        Employee entity = new Employee();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setEmployeCode(converter.fromRow(row, prefix + "_employe_code", String.class));
        entity.setFirstName(converter.fromRow(row, prefix + "_first_name", String.class));
        entity.setLastName(converter.fromRow(row, prefix + "_last_name", String.class));
        entity.setJobTitle(converter.fromRow(row, prefix + "_job_title", String.class));
        entity.setUserId(converter.fromRow(row, prefix + "_user_id", Long.class));
        entity.setDepartmentId(converter.fromRow(row, prefix + "_department_id", Long.class));
        return entity;
    }
}
