package org.springframework.test.wbstudy;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.support.AbstractApplicationContext;

/**
 * @author wangbin33
 * @date Created in 20:31 2019/10/2
 */
public class MyApplicationContext extends AbstractApplicationContext {

	@Override
	protected void refreshBeanFactory() throws BeansException, IllegalStateException {

	}

	@Override
	protected void closeBeanFactory() {

	}

	@Override
	public ConfigurableListableBeanFactory getBeanFactory() throws IllegalStateException {
		return null;
	}

	/**
	 * 可以在实现了AbstractApplicationContext之后，在initPropertySources方法中实现.
	 */
	@Override
	protected void initPropertySources() {
	}
}
