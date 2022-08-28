package com.wb.spring.multipleBeanName;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;

/**
 * created by IDEA
 *
 * @author wangbing
 * @date 2022.08.2022/8/28.19:45
 */
public class MyBeanDefinitionRegistryPostProcessor implements BeanDefinitionRegistryPostProcessor {

	@Override
	public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
		//
	}

	@Override
	public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {
		System.out.println("以下属性来自于postProcessBeanDefinitionRegistry...");

		BeanDefinition beanDefinition = registry.getBeanDefinition("testBean100");
		String description = beanDefinition.getDescription();
		Object metaValue = beanDefinition.getAttribute("metaKey");

		System.out.println("description: " + description);
		System.out.println("metaValue: " + metaValue);
	}
}
