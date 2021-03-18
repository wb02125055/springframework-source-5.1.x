package com.wb.spring.idgenerator.test;

import com.mchange.v2.c3p0.ComboPooledDataSource;
import com.wb.spring.idgenerator.handle.IdSpecHandle;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

/**
 * Description:
 *
 * @author wangbin33
 * @date 2020/8/8 10:25
 */
@Configuration
@ComponentScan(basePackages = "com.wb.spring.idgenerator")
public class IdGeneratorConfig {
	@Bean
	public DataSource dataSource() throws Exception {
		ComboPooledDataSource dataSource = new ComboPooledDataSource();
		dataSource.setUser("root");
		dataSource.setPassword("root");
		dataSource.setDriverClass("com.mysql.jdbc.Driver");
		dataSource.setJdbcUrl("jdbc:mysql://127.0.0.1:3306/db_id_pool?characterEncoding=UTF8&useSSL=false");
		return dataSource;
	}
	@Bean
	public IdSpecHandle idSpecHandle() throws Exception {
		IdSpecHandle handle = new IdSpecHandle();
		handle.setDataSource(dataSource());
		handle.setBlockSize(10);
//		handle.setTableName("test");
		return handle;
	}
}
