package com.wb.spring.selfapplicationcontext;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.AbstractRefreshableApplicationContext;
import org.springframework.context.support.AbstractRefreshableConfigApplicationContext;

import java.io.IOException;

/**
 * Description: 加载并解析json配置文件，并转换为beanDefinition
 *
 * @author wangbin33
 * @date 2020/11/20 15:45
 */
public class ClasspathJsonApplicationContext extends AbstractRefreshableConfigApplicationContext {
	@Override
	protected void loadBeanDefinitions(DefaultListableBeanFactory beanFactory) throws BeansException, IOException {

		JsonBeanDefinitionReader beanDefinitionReader = new JsonBeanDefinitionReader(beanFactory);
		beanDefinitionReader.setEnvironment(getEnvironment());
		beanDefinitionReader.setResourceLoader(this);

		loadBeanDefinitions(beanDefinitionReader);
	}

	protected void loadBeanDefinitions(JsonBeanDefinitionReader reader) {
		String[] configLocations = getConfigLocations();
		if (null != configLocations) {
			reader.loadBeanDefinitions(configLocations);
		}
	}

	public ClasspathJsonApplicationContext(String configLocation) {
		this(new String[]{configLocation}, true, null);
	}

	public ClasspathJsonApplicationContext(String[] configLocations, boolean refresh, ApplicationContext parent) {
		super(parent);

		setConfigLocations(configLocations);

		if (refresh) {
			refresh();
		}
	}
}
