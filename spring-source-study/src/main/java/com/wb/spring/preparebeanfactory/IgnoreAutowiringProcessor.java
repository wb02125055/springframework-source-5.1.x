package com.wb.spring.preparebeanfactory;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;

import java.util.ArrayList;

/**
 * Created by wangbin33 on 2020/1/23.
 */
public class IgnoreAutowiringProcessor implements BeanFactoryPostProcessor {
	@Override
	public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
		beanFactory.ignoreDependencyType(ArrayList.class);
	}
}
