package com.wb.spring.beans;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.*;

import java.util.UUID;

/**
 * Created by wangbin33 on 2020/1/31.
 */
@Configuration
@ComponentScan(basePackages = "com.wb.spring.beans")
public class TestConfig {

	@Bean(value = "student", initMethod = "initStudent", destroyMethod = "destoryStudent")
	@Lazy
//	@Scope(scopeName = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
	public Student student() {
		Student s = new Student();
		System.out.println("Student create...");
		s.setName(UUID.randomUUID().toString());
		return s;
	}
}
