package com.wb.spring.beaninit.config;

import com.wb.spring.beaninit.domain.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * Description:
 *
 * @author wangbin33
 * @date 2020/4/26 20:17
 */
@Configuration
@ComponentScan(basePackages = "com.wb.spring.beaninit")
public class BeanConfig1 {

	@Value("${flag:true}")
	private boolean flag;

	@Bean
	public User user() {
		System.out.println(flag);
		return new User();
	}

//	@Bean
//	public User user() {
//		return new User(UUID.randomUUID().toString());
//	}
}
