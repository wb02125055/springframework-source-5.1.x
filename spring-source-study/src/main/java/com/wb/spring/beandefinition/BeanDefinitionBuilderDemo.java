package com.wb.spring.beandefinition;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;

/**
 * Created by wangbin33 on 2019/12/28.
 *
 * 通过BeanDefinitionBuilder 构造Bean定义
 */
public class BeanDefinitionBuilderDemo {
	public static void main(String[] args) {
		AbstractBeanDefinition beanDefinition = BeanDefinitionBuilder.genericBeanDefinition(ChildPO.class)
				.setRole(BeanDefinition.ROLE_APPLICATION)
				.setScope(BeanDefinition.SCOPE_SINGLETON)
				.addPropertyValue("name", "王兵")
				.setLazyInit(false)
				.applyCustomizers((bd) -> {
					AbstractBeanDefinition abd = (AbstractBeanDefinition) bd;
					abd.setAutowireMode(AbstractBeanDefinition.AUTOWIRE_NO);
				}).getRawBeanDefinition();
		// 使用getRawBeanDefinition()方法获取Bean定义时，不会执行bean定义的校验操作
		// 使用getBeanDefinition()方法获取Bean定义时，会执行this.beanDefinition.validate();操作先进行bean定义的校验
		System.out.println(beanDefinition);
	}
}
