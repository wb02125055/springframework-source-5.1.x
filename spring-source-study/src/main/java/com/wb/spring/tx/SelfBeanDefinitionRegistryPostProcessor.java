package com.wb.spring.tx;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.stereotype.Component;

/**
 * Description:
 *
 * @author wangbin33
 * @date 2020/10/29 10:53
 */
//@Component
public class SelfBeanDefinitionRegistryPostProcessor implements BeanDefinitionRegistryPostProcessor {
	@Override
	public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {
		String[] beanDefinitionNames = registry.getBeanDefinitionNames();
		for (String beanDefinitionName : beanDefinitionNames) {
			if ("jdbcTemplate".equals(beanDefinitionName)) {
				registry.removeBeanDefinition("jdbcTemplate");
			}
			System.out.println("--->" + beanDefinitionName);
		}
	}

	@Override
	public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {

	}
}
