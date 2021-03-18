package com.wb.spring.postProcessBeanFactory;

import org.springframework.context.annotation.Bean;

/**
 * Created by wangbin33 on 2020/1/26.
 */
//@Configuration
//@ComponentScan(basePackages = "com.wb.spring.postProcessBeanFactory")
public class MainConfig {

	@Bean
	public MyUser user() {
		MyUser myUser = new MyUser();
		myUser.setAge(10);
		myUser.setName("aaa");
		return myUser;
	}
}
