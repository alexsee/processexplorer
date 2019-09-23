package de.tk.processmining.webservice;

import javax.sql.DataSource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

@Configuration
@ComponentScan("de.tk.processmining")
public class WebserviceJdbcConfig {
    @Bean
    public DataSource postgresqlDataSource() {
        var dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName("org.postgresql.Driver");
        dataSource.setUrl("jdbc:postgresql://localhost:5432/openpmdb");
        dataSource.setUsername("postgres");
        dataSource.setPassword("test123");

        return dataSource;
    }
}