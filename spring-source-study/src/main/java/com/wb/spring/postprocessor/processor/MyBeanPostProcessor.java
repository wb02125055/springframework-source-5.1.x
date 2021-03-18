package com.wb.spring.postprocessor.processor;

import org.springframework.beans.BeansException;
import org.springframework.beans.PropertyValues;
import org.springframework.beans.factory.*;
import org.springframework.beans.factory.config.InstantiationAwareBeanPostProcessorAdapter;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

/**
 * Created by wangbin33 on 2020/1/8.
 *
 * 测试Bean的后置处理器
 */
@Component
public class MyBeanPostProcessor extends InstantiationAwareBeanPostProcessorAdapter
		implements BeanNameAware,BeanFactoryAware,ApplicationContextAware,InitializingBean,DisposableBean {

	@Override
	public void afterPropertiesSet() throws Exception {
		System.out.println("afterPropertiesSet...");
	}

	@Override
	public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
		System.out.println("setBeanFactory...");
	}

	@Override
	public void setBeanName(String name) {
		System.out.println("setBeanName...");
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		System.out.println("setApplicationContext...");
	}

	@Override
	public void destroy() throws Exception {
		System.out.println("destroy...");
	}

	@Nullable
	@Override
	public Object postProcessBeforeInstantiation(Class<?> beanClass, String beanName) throws BeansException {
		System.out.println("postProcessBeforeInstantiation...");
		return super.postProcessBeforeInstantiation(beanClass, beanName);
	}

	@Override
	public boolean postProcessAfterInstantiation(Object bean, String beanName) throws BeansException {
		System.out.println("postProcessAfterInstantiation...");
		return super.postProcessAfterInstantiation(bean, beanName);
	}

	@Override
	public PropertyValues postProcessProperties(PropertyValues pvs, Object bean, String beanName) throws BeansException {
		System.out.println("postProcessProperties...");
		return super.postProcessProperties(pvs, bean, beanName);
	}

	@Override
	public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
		System.out.println("postProcessBeforeInitialization...");
		if (bean instanceof MyAware) {
			((MyAware) bean).setMy("aaaa");
		}
		return super.postProcessBeforeInitialization(bean, beanName);
	}

	@Override
	public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
		System.out.println("postProcessAfterInitialization...");
		return super.postProcessAfterInitialization(bean, beanName);
	}
}
