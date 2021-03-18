package com.wb.spring.beanInitializeTest.beans;

import com.wb.spring.beanInitializeTest.beans.config.BeanConfig;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * @author wangbin33
 * @date Created in 10:57 2019/11/30
 */
public class TestMain {

	public static void main(String[] args) {

		ApplicationContext acx = new AnnotationConfigApplicationContext(BeanConfig.class);

//		InstA obj = (InstA) acx.getBean("instA");
//		System.out.println(obj);
	}
}
