package com.wb.spring.xml;

import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.PropertySource;

/**
 * Description:
 *
 * @author wangbin33
 * @date 2020/8/6 14:42
 */
public class MyXmlApplicationContext extends ClassPathXmlApplicationContext {

	public MyXmlApplicationContext() {}

	public MyXmlApplicationContext(String ...locations) {
		super(locations);
	}

	@Override
	protected void initPropertySources() {
		ConfigurableEnvironment environment = getEnvironment();
		environment.getSystemProperties().put("menu.env", "dev");
		super.initPropertySources();
	}

	/**
	 * 通过扩展点实现属性的赋值操作
	 * @param beanFactory the newly created bean factory for this context
	 */
	@Override
	protected void customizeBeanFactory(DefaultListableBeanFactory beanFactory) {

		// 通过直接调用父类的设置值的方法也可以实现属性的赋值操作.
		super.setAllowBeanDefinitionOverriding(true);
		super.setAllowCircularReferences(true);

//		beanFactory.setAllowBeanDefinitionOverriding(true);
//		beanFactory.setAllowCircularReferences(true);
		super.customizeBeanFactory(beanFactory);
	}
}
