package com.wb.spring.instantiation_by_constructor;

import com.wb.spring.selfapplicationcontext.ClasspathJsonApplicationContext;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Description:
 *
 * @author wangbin33
 * @date 2020/11/26 11:35
 */
public class TestMain {
	public static void main(String[] args) {
		ApplicationContext acx =
				new ClassPathXmlApplicationContext("instant_by_constructor/instant.xml");
		Object myConsObj1 = acx.getBean("myConsObj");
		Object myConsObj2 = acx.getBean("myConsObj");

		System.out.println(myConsObj1);
		System.out.println(myConsObj2);
	}
}
