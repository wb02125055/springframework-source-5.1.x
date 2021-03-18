package com.wb.spring.beanInitializeTest.beans;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * @author wangbin33
 * @date Created in 22:59 2019/11/30
 */
//@Component
public class MyApplicationContext implements ApplicationContextAware {
	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		System.out.println("容器中的Bean定义如下：");
		String[] beanDefinitionNames = applicationContext.getBeanDefinitionNames();
		for (String beanDefinitionName : beanDefinitionNames) {
			System.out.println(beanDefinitionName);
		}
	}
}
