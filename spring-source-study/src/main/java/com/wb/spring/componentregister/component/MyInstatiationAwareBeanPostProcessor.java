package com.wb.spring.componentregister.component;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;

/**
 * Created by wangbin33 on 2020/1/10.
 *
 * beanFactory的后置处理器.
 */
public class MyInstatiationAwareBeanPostProcessor implements BeanFactoryPostProcessor {
	@Override
	public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
		System.out.println("postProcessBeanFactory...");
	}
}
