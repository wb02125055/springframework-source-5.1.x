package org.springframework.mock.sourcestudy.bean;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

/**
 * @author wangbin33
 * @date Created in 22:57 2019/10/2
 */
@Component
public class MyBeanPostProcessor implements BeanPostProcessor {
	@Nullable
	@Override
	public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
		System.out.println("postProcessBeforeInitialization..." + beanName + "=> " + bean);
		return bean;
	}

	@Nullable
	@Override
	public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
		System.out.println("postProcessAfterInitialization..." + beanName + "=> " + bean);
		return bean;
	}
}
