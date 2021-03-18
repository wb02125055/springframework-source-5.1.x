package com.wb.spring.tx.config;

import com.mchange.v2.c3p0.ComboPooledDataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;

/**
 * @author wangbin33
 * @date Created in 22:53 2019/10/6
 *
 * 1.EnableTransactionManagement 利用@Import注解给容器中导入TransactionManagementConfigurationSelector组件
 * 		在TransactionManagementConfigurationSelector中根据代理模式最后会给容器中导入如下两个组件：
 * 		(1)	AutoProxyRegistrar
 * 		(2) ProxyTransactionManagementConfiguration
 * 2.AutoProxyRegistrar:
 * 		该组件的作用是给容器中注册组件。给容器中注册一个InfrastructureAdvisorAutoProxyCreator组件.
 * 		InfrastructureAdvisorAutoProxyCreator:
 * 	     利用后置处理器机制在对象创建之后，包装对象，返回一个代理对象(增强器)，代理对象执行的时候利用拦截器链进行调用.
 * 3.ProxyTransactionManagementConfiguration:
 *		用来给容器中注册事务增强器，如下：
 *			(1)使用AnnotationTransactionAttributeSource来解析事务注解中的信息，如：readOnly，isolation，timeout等.
 *          (2)事务拦截器:TransactionInterceptor，保存了事务属性信息和事务管理器
 *          TransactionInterceptor是一个MethodInterceptor，在目标方法执行的时候，执行拦截器链.
 */
// 开启基于注解的事务管理功能.
@EnableTransactionManagement
@Configuration
@ComponentScan(basePackages = "com.wb.spring.tx")
public class TxConfig {

	@Bean
	public DataSource dataSource() throws Exception {
		ComboPooledDataSource dataSource = new ComboPooledDataSource();
		dataSource.setUser("root");
		dataSource.setPassword("root");
		dataSource.setDriverClass("com.mysql.jdbc.Driver");
		dataSource.setJdbcUrl("jdbc:mysql://127.0.0.1:3306/test?characterEncoding=UTF8&useSSL=false");
		return dataSource;
	}

	@Bean
	public JdbcTemplate jdbcTemplate() throws Exception {
		// 多次调用dataSource()方法，只会触发Spring从容器中找组件的过程，不会再次调用dataSource方法创建.
		return new JdbcTemplate(dataSource());
	}

	/**
	 * 配置事务管理器
	 * 按照类型注入事务管理器到容器中
	 *
	 * 获取事务管理器时，或尝试从容器中按照事务管理器的类型获取.
	 */
	@Bean
	public PlatformTransactionManager transactionManager() throws Exception {
		return new DataSourceTransactionManager(dataSource());
	}
}
