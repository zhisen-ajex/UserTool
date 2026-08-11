package com.verify.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;

@Repository
@RequiredArgsConstructor
public class DataMonitorRepository {

    @Qualifier("dataMonitorJdbcTemplate")
    private final JdbcTemplate jdbcTemplate;

    public List<Map<String,Object>> execute(String sql){

        return jdbcTemplate.queryForList(sql);

    }

    public void queryStream(String sql, BiConsumer<List<String>, List<Object>> rowConsumer) {
        jdbcTemplate.query(
                con -> {
                    PreparedStatement ps = con.prepareStatement(sql, ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
                    ps.setFetchSize(Integer.MIN_VALUE);
                    return ps;
                },
                rs -> {
                    ResultSetMetaData meta = rs.getMetaData();
                    int columnCount = meta.getColumnCount();
                    List<String> columnNames = new ArrayList<>(columnCount);
                    for (int i = 1; i <= columnCount; i++) {
                        columnNames.add(meta.getColumnLabel(i));
                    }

                    while (rs.next()) {
                        List<Object> row = new ArrayList<>(columnCount);
                        for (int i = 1; i <= columnCount; i++) {
                            row.add(rs.getObject(i));
                        }
                        rowConsumer.accept(columnNames, row);
                    }
                    return null;
                }
        );
    }


}
