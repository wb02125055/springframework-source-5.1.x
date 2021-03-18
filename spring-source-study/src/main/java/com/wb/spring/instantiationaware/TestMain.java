package com.wb.spring.instantiationaware;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Description:
 *
 * @author wangbin33
 * @date 2020/11/20 8:59
 */
public class TestMain {
	public static void main(String[] args) {
		ApplicationContext acx = new ClassPathXmlApplicationContext("classpath:instantiationaware/instantiationaware.xml");
		BeforeInstance bean = acx.getBean(BeforeInstance.class);
		bean.sayHello();
	}
}
