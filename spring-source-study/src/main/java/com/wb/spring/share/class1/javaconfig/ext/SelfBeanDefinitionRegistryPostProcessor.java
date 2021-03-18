package com.wb.spring.share.class1.javaconfig.ext;

import com.wb.spring.share.class1.domain.Teacher;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;

/**
 * Description:
 *
 * @author wangbin33
 * @date 2020/11/1 22:48
 */
public class SelfBeanDefinitionRegistryPostProcessor implements BeanDefinitionRegistryPostProcessor {
	@Override
	public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {
		BeanDefinitionBuilder beanDefinitionBuilder = BeanDefinitionBuilder.rootBeanDefinition(Teacher.class);
		beanDefinitionBuilder.setScope(BeanDefinition.SCOPE_SINGLETON);
		BeanDefinition beanDefinition = beanDefinitionBuilder.getBeanDefinition();
		registry.registerBeanDefinition("teacher", beanDefinition);
	}

	@Override
	public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
	}
}
