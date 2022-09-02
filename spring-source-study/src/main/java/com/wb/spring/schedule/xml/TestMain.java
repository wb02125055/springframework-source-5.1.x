package com.wb.spring.schedule.xml;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * created by IDEA
 *
 * @author wangbing
 * @date 2022.09.2022/9/2.21:28
 */
public class TestMain {
	public static void main(String[] args) {
		ApplicationContext acx = new ClassPathXmlApplicationContext("schedule/beans.xml");
	}
}
