package com.verify.repository;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;

@Repository
public class OrderRevenueExportRepository {

    private final JdbcTemplate jdbcTemplate;

    public OrderRevenueExportRepository(@Qualifier("dataSource") DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public void queryStream(String sql, BiConsumer<List<String>, List<Object>> rowConsumer) {
        jdbcTemplate.query(
                connection -> {
                    PreparedStatement statement = connection.prepareStatement(
                            sql,
                            ResultSet.TYPE_FORWARD_ONLY,
                            ResultSet.CONCUR_READ_ONLY
                    );
                    statement.setFetchSize(Integer.MIN_VALUE);
                    return statement;
                },
                resultSet -> {
                    ResultSetMetaData metadata = resultSet.getMetaData();
                    int columnCount = metadata.getColumnCount();
                    List<String> columnNames = new ArrayList<>(columnCount);
                    for (int column = 1; column <= columnCount; column++) {
                        columnNames.add(metadata.getColumnLabel(column));
                    }

                    while (resultSet.next()) {
                        List<Object> row = new ArrayList<>(columnCount);
                        for (int column = 1; column <= columnCount; column++) {
                            row.add(resultSet.getObject(column));
                        }
                        rowConsumer.accept(columnNames, row);
                    }
                    return null;
                }
        );
    }
}
