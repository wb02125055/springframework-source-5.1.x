package com.wb.spring.share.class1.javaconfig;

import com.wb.spring.share.class1.javaconfig.config.SimpleConfig;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * Description:
 *
 * @author wangbin33
 * @date 2020/11/17 9:49
 */
public class TestSimpleConfig {
	public static void main(String[] args) {
		ApplicationContext acx = new AnnotationConfigApplicationContext(SimpleConfig.class);
		Object hotDog = acx.getBean("hotDog");
		System.out.println(hotDog);
	}
}
