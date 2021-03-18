package com.wb.spring.autowired.beans;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * Description:
 *
 * @author wangbin33
 * @date 2020/6/25 11:08
 */
@Component
public class ApplicationContextUtils implements ApplicationContextAware, BeanNameAware {

	@Override
	public void setBeanName(String name) {
		System.out.println("beanNameAware...");
	}

	private static ApplicationContext applicationContext;

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		ApplicationContextUtils.applicationContext = applicationContext;
	}

	public static ApplicationContext getContext() {
		return applicationContext;
	}
}
