package org.springframework.test.wbstudy;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.mock.sourcestudy.BeanConfig;

/**
 * @author wangbin33
 * @date Created in 23:00 2019/10/2
 */
public class BeanLifeCycleTest {
	public static void main(String[] args) {
		ApplicationContext acx = new AnnotationConfigApplicationContext(BeanConfig.class);
	}
}
