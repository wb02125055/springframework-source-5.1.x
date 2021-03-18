package com.wb.spring.selfapplicationcontext;

import org.springframework.context.ApplicationContext;

/**
 * Description:
 *
 * @author wangbin33
 * @date 2020/11/20 19:56
 */
public class TestMain {
	public static void main(String[] args) {
		ApplicationContext acx = new ClasspathJsonApplicationContext("beanDefinition.json");
		JsonTestService bean = acx.getBean(JsonTestService.class);
		System.out.println(bean);
	}
}
