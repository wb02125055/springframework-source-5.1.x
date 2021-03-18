package com.wb.spring.beanInitializeTest.beans;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.stereotype.Component;

/**
 * @author wangbin33
 * @date Created in 22:21 2019/11/30
 */
@Component
public class MyBeanFactoryPostProcessor implements BeanFactoryPostProcessor {
	@Override
	public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
		GenericBeanDefinition beanDefinition = (GenericBeanDefinition) beanFactory.getBeanDefinition("instA");
		// 修改Bean的类型
		//beanDefinition.setBeanClass(InstD.class);


		// 修改Bean的注入模式
		// beanDefinition.setAutowireMode(AbstractBeanDefinition.AUTOWIRE_NO);

		/*

		ConstructorArgumentValues constructorArgumentValues = new ConstructorArgumentValues();
		// 表示修改Spring实例化时使用的构造函数，第0个参数为String类型的参数
		constructorArgumentValues.addIndexedArgumentValue(0, "wangbing");
		// 表示修改Spring实例化时使用的构造函数，第0个参数为int类型的参数
		constructorArgumentValues.addIndexedArgumentValue(0, 12);
		beanDefinition.setConstructorArgumentValues(constructorArgumentValues);

		*/
	}
}
