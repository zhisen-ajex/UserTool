package com.verify.config;

import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;

@Configuration
public class DataMonitorDataSourceConfig {

    @Bean
    @Primary
    @ConfigurationProperties(prefix = "spring.datasource")
    public DataSource dataSource() {

        return DataSourceBuilder.create().type(HikariDataSource.class).build();
    }


    @Bean("reportDataSource")
    @ConfigurationProperties(prefix = "report.datasource")
    public DataSource reportDataSource() {

        return DataSourceBuilder.create().type(HikariDataSource.class).build();

    }


    @Bean("dataMonitorJdbcTemplate")
    public JdbcTemplate dataMonitorJdbcTemplate(@Qualifier("reportDataSource") DataSource dataSource) {

        return new JdbcTemplate(dataSource);
    }

}
