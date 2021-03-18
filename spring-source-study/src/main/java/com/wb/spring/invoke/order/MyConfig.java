package com.wb.spring.invoke.order;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * Description:
 *
 * @author wangbin33
 * @date 2020/10/9 15:12
 */
@Configuration
@ComponentScan(basePackages = "com.wb.spring.invoke.order")
public class MyConfig {

	@Bean
	public MySessionFactoryBean sessionBean() {
		return new MySessionFactoryBean();
	}
}
