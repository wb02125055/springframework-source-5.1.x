package com.wb.spring.ext;

import com.wb.spring.ext.beans.Blue;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.*;
import org.springframework.stereotype.Component;

/**
 * @author wangbin33
 * @date Created in 12:46 2019/12/7
 */
@Component
public class MyBeanDefinitionRegistryPostProcessor implements BeanDefinitionRegistryPostProcessor {
	@Override
	public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
		System.out.println("MyBeanDefinitionRegistryPostProcessor ...postProcessBeanFactory... bean的数量为：" + beanFactory.getBeanDefinitionCount());
	}

	/**
	 * bean定义信息的保存中心
	 *
	 * BeanFactory就是根据BeanDefinitionRegistry中保存的每一个Bean定义去创建bean示例的.
	 *
	 * @param registry the bean definition registry used by the application context
	 * @throws BeansException
	 */
	@Override
	public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {
		System.out.println("MyBeanDefinitionRegistryPostProcessor ...postProcessBeanDefinitionRegistry... bean的数量为：" + registry.getBeanDefinitionCount());
//		RootBeanDefinition beanDefinition = new RootBeanDefinition(Blue.class);
		AbstractBeanDefinition beanDefinition = BeanDefinitionBuilder.rootBeanDefinition(Blue.class).getBeanDefinition();
		registry.registerBeanDefinition("customerBlue", beanDefinition);
	}
}
