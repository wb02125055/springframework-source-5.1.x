package com.wb.spring.beans;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import java.util.UUID;

/**
 * Created by wangbin33 on 2020/2/9.
 */
@Configuration
@ComponentScan(basePackages = "com.wb.spring.beans")
public class FactoryBeanConfig {

	@Bean
	public ConsumerBean consumerBean() {
		ConsumerBean consumerBean = new ConsumerBean();
		consumerBean.setId(UUID.randomUUID().toString());
		return consumerBean;
	}
}
