package com.wb.spring.beans;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

import java.util.UUID;

/**
 * @author wangbin33
 * @date Created in 12:38 2019/11/30
 */
@Configuration
@ComponentScan(basePackages = "com.wb.spring.beans")
public class BeanConfig {

	@Bean(initMethod = "sayHello")
//	@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
//	@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
	public Person person() {
		Person person = new Person();
		person.setUuid(UUID.randomUUID().toString());
		return person;
	}

	@Bean
	public ColorFactoryBean colorFactoryBean() {
		return new ColorFactoryBean();
	}
}
