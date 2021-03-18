package com.wb.spring.beanInitializeTest.beans;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.*;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.lang.Nullable;

import java.util.Arrays;

/**
 * @author wangbin33
 * @date Created in 11:04 2019/11/30
 */
public class UserBean implements InitializingBean,BeanNameAware,BeanFactoryAware,
		ApplicationContextAware,BeanPostProcessor,DisposableBean {

	public UserBean() {
		System.out.println("UserBean init...");
	}

	private String uuid;

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		System.out.println("afterPropertiesSet...");
	}

	@Override
	public void setBeanName(String name) {
		System.out.println("setBeanName: " + name);
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		String[] beanDefinitionNames = applicationContext.getBeanDefinitionNames();
		System.out.println("setApplicationContext..." + Arrays.toString(beanDefinitionNames));
//		for (String beanName : beanDefinitionNames) {
//			System.out.println(beanName);
//		}
	}

	@Override
	public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
		System.out.println("set BeanFactory...");
	}

	@Nullable
	@Override
	public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
		System.out.println("postProcessBeforeInitialization..." + beanName);
		return null;
	}

	@Nullable
	@Override
	public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
		System.out.println("postProcessAfterInitialization..." + beanName);
		return null;
	}

	@Override
	public void destroy() throws Exception {
		System.out.println("destory...");
	}

	@Override
	public String toString() {
		return "UUID: " + this.uuid;
	}
}
