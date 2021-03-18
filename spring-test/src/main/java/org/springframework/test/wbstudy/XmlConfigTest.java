package org.springframework.test.wbstudy;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * @author wangbin33
 * @date Created in 16:06 2019/10/2
 */
public class XmlConfigTest {

	private static ApplicationContext acx = null;

	static {
//		acx = new AnnotationConfigApplicationContext(MainConfig.class);
		acx = new ClassPathXmlApplicationContext("spring.xml");
	}

	public static void main(String[] args) {
		String[] names = acx.getBeanDefinitionNames();
		for (String name : names) {
			System.out.println(name);
		}
	}
}
