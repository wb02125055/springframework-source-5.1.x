package com.wb.spring.beanInitializeTest.lookup;

import com.wb.spring.beanInitializeTest.lookup.beans.SingletonBean;
import com.wb.spring.beanInitializeTest.lookup.config.LookUpConfig;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;


public class TestMain {

	private ApplicationContext acx = null;

	private Class configClass = LookUpConfig.class;

	@Before
	public void init() {
		acx = new AnnotationConfigApplicationContext(configClass);
	}

	@Test
	public void testPrototypeInject() {
		Object obj;
		for (int i = 0 ; i < 5 ; i++) {
			obj = acx.getBean("singletonBean");

			SingletonBean singletonBean = (SingletonBean) obj;

			singletonBean.printBean();
		}
	}
}
