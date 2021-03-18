package com.wb.spring.postProcessBeanFactory;

import org.springframework.beans.PropertyValue;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Created by wangbin33 on 2020/1/26.
 */
public class MyApplicationContext extends ClassPathXmlApplicationContext {
	@Override
	protected void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) {
		BeanDefinition beanDefinition = beanFactory.getBeanDefinition("myUser");
		PropertyValue propertyValue = new PropertyValue("name", "兵王...");
		beanDefinition.getPropertyValues().addPropertyValue(propertyValue);
		System.out.println("definition: " + beanDefinition);
	}
}
