package com.wb.spring.ext;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.stereotype.Component;

/**
 * @author wangbin33
 * @date Created in 12:24 2019/12/7
 */
@Component
public class MyBeanFactoryPostProcessor implements BeanFactoryPostProcessor {
	@Override
	public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
		// 获取已经加载的bean定义的数量
		int count = beanFactory.getBeanDefinitionCount();
		// 获取已经加载的bean定义的名称
		String[] beanNames = beanFactory.getBeanDefinitionNames();
//		System.out.println("已经被加载的bean定义的个数为：" + count);
//		System.out.println("已经加载的bean的名称如下：");
//		String[] beanNames = beanFactory.getBeanDefinitionNames();
		// 根据bean定义的名称获取具体的bean定义
		BeanDefinition perBeanDefinition = beanFactory.getBeanDefinition("person");
		System.out.println(perBeanDefinition);
		// 修改bean定义
		perBeanDefinition.setAutowireCandidate(false);
		System.out.println(perBeanDefinition);
//		for (String beanName : beanNames) {
//			System.out.println(beanName);
//		}
	}
}
