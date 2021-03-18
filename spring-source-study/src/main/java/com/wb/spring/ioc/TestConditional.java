package com.wb.spring.ioc;

import com.wb.spring.ioc.config.ConditionalConfig;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * Created by wangbin33 on 2020/1/31.
 */
public class TestConditional {
	public static void main(String[] args) {
		ApplicationContext acx = new AnnotationConfigApplicationContext(ConditionalConfig.class);
		String[] names = acx.getBeanDefinitionNames();
		for (String name : names) {
			System.out.println(name);
		}
	}
}
