package com.wb.spring.jdbc.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;

/**
 * @author wangbin33
 * @date Created in 15:48 2019/11/24
 */
@Configuration
public class JdbcConfig {

	@Bean
	public DataSource dataSource() {
		DriverManagerDataSource dataSource = new DriverManagerDataSource();
		dataSource.setDriverClassName("com.mysql.jdbc.Driver");
		dataSource.setUrl("jdbc:mysql://127.0.0.1:3306/test?useUnicode=true&characterEncoding=UTF-8&useSSL=false");
		dataSource.setUsername("root");
		dataSource.setPassword("root");
		return dataSource;
	}
}
