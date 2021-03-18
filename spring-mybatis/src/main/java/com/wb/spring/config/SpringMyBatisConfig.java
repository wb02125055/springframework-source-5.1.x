package com.wb.spring.config;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.jdbc.datasource.SimpleDriverDataSource;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;

/**
 * @author wangbin33
 * @date 2020/5/10 22:25
 */
@Configuration
@ComponentScan(basePackages = {"com.wb.spring"})
@MapperScan(basePackages = {"com.wb.spring.mapper"})
//@EnableTransactionManagement
public class SpringMyBatisConfig {

	/**
	 * 生成SqlSessionFactory对象，并加入到容器中
	 * @param dataSource 数据源
	 */
	@Bean
	public static SqlSessionFactory sqlSessionFactory(DataSource dataSource) throws Exception {
		SqlSessionFactoryBean factoryBean = new SqlSessionFactoryBean();
		factoryBean.setDataSource(dataSource);
		return factoryBean.getObject();
	}

	@Bean
	public DataSource dataSource() {
		DriverManagerDataSource dataSource = new DriverManagerDataSource();
		dataSource.setDriverClassName("com.mysql.jdbc.Driver");
		dataSource.setUrl("jdbc:mysql://127.0.0.1:3306/mybatis?characterEncoding=UTF8&useSSL=false");
		dataSource.setUsername("root");
		dataSource.setPassword("root");
		return dataSource;
	}

//	@Bean
//	public PlatformTransactionManager transactionManager(DataSource dataSource) {
//		return new DataSourceTransactionManager(dataSource);
//	}
}