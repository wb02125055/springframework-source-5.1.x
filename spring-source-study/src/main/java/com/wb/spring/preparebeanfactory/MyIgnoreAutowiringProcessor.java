package com.wb.spring.preparebeanfactory;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;

/**
 * Created by wangbin33 on 2020/1/26.
 */
public class MyIgnoreAutowiringProcessor implements BeanFactoryPostProcessor {
	@Override
	public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
//		beanFactory.ignoreDependencyInterface(MyIgnoreInterface.class);
	}
}
