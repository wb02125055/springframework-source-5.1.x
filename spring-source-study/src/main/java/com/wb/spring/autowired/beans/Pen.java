package com.wb.spring.autowired.beans;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.EmbeddedValueResolverAware;
import org.springframework.stereotype.Component;
import org.springframework.util.StringValueResolver;

/**
 * Created by wangbin33 on 2020/3/8.
 */
@Component
public class Pen implements ApplicationContextAware,BeanNameAware, EmbeddedValueResolverAware {
	/** 保存Spring容器的引用 */
	private ApplicationContext applicationContext;

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		System.out.println("获取到的当前IOC容器对象为：" + applicationContext);
		this.applicationContext = applicationContext;
	}

	@Override
	public void setBeanName(String name) {
		System.out.println("当前Bean的名称为：" + name);
	}

	@Override
	public void setEmbeddedValueResolver(StringValueResolver resolver) {

	}
}
