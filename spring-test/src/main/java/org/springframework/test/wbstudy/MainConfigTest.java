package org.springframework.test.wbstudy;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.mock.sourcestudy.BeanConfig1;
import org.springframework.mock.sourcestudy.MainConfig;

/**
 * @author wangbin33
 * @date Created in 14:46 2019/10/2
 */
public class MainConfigTest {
	public static void main(String[] args) {
		ApplicationContext acx = new AnnotationConfigApplicationContext(MainConfig.class, BeanConfig1.class);
		String[] names = acx.getBeanDefinitionNames();
		for (String name : names) {
			System.out.println(name);
		}
	}
}
