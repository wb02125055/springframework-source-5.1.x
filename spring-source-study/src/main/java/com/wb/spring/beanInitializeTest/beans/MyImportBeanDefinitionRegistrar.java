package com.wb.spring.beanInitializeTest.beans;

import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.stereotype.Component;

/**
 * @author wangbin33
 * @date Created in 23:03 2019/11/30
 *
 * 继承Bean定义注册器，可以通过该方式给容器中添加Bean定义.
 */
@Component
public class MyImportBeanDefinitionRegistrar implements ImportBeanDefinitionRegistrar {
	@Override
	public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
		RootBeanDefinition rootBeanDefinition = new RootBeanDefinition(InstC.class);
		registry.registerBeanDefinition("instC", rootBeanDefinition);
	}
}
