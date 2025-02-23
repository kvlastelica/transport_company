package com.transam.backoffice.repository;

import java.util.ArrayList;
import java.util.List;
import org.springframework.data.relational.core.sql.Column;
import org.springframework.data.relational.core.sql.Expression;
import org.springframework.data.relational.core.sql.Table;

public class RouteSqlHelper {

    public static List<Expression> getColumns(Table table, String columnPrefix) {
        List<Expression> columns = new ArrayList<>();
        columns.add(Column.aliased("id", table, columnPrefix + "_id"));
        columns.add(Column.aliased("code", table, columnPrefix + "_code"));
        columns.add(Column.aliased("description", table, columnPrefix + "_description"));
        columns.add(Column.aliased("start", table, columnPrefix + "_start"));
        columns.add(Column.aliased("destination", table, columnPrefix + "_destination"));
        columns.add(Column.aliased("passenger_capacity", table, columnPrefix + "_passenger_capacity"));
        columns.add(Column.aliased("parcel_total_weight", table, columnPrefix + "_parcel_total_weight"));

        return columns;
    }
}
