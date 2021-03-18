package com.wb.spring.invoke.order;

import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;

/**
 * Description:
 *
 * @author wangbin33
 * @date 2020/10/9 15:13
 */
public class MySessionFactoryBean implements FactoryBean<String>, ApplicationListener<ApplicationEvent>, InitializingBean {
	@Override
	public String getObject() throws Exception {
		return "getObject...";
	}

	@Override
	public Class<?> getObjectType() {
		return String.class;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		System.out.println("afterPropertiesSet...");
	}

	@Override
	public void onApplicationEvent(ApplicationEvent event) {
		System.out.println("onApplicationEvent...");
	}
}
