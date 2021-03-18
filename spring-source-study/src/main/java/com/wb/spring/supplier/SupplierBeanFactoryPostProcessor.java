package com.wb.spring.supplier;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.GenericBeanDefinition;

/**
 * Description:
 *
 * @author wangbin33
 * @date 2020/11/20 8:47
 */
public class SupplierBeanFactoryPostProcessor implements BeanFactoryPostProcessor {
	@Override
	public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
		BeanDefinition userObj = beanFactory.getBeanDefinition("userObj");
		GenericBeanDefinition beanDefinition = (GenericBeanDefinition) userObj;
		beanDefinition.setInstanceSupplier(UserObjSupplier::createUser);
		beanDefinition.setBeanClass(UserObj.class);
	}
}
