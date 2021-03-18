/*
 * Copyright 2002-2012 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.context.config;

import org.springframework.beans.factory.xml.NamespaceHandlerSupport;
import org.springframework.context.annotation.AnnotationConfigBeanDefinitionParser;
import org.springframework.context.annotation.ComponentScanBeanDefinitionParser;

/**
 * {@link org.springframework.beans.factory.xml.NamespaceHandler}
 * for the '{@code context}' namespace.
 *
 * @author Mark Fisher
 * @author Juergen Hoeller
 * @since 2.5
 *
 * 在Spring加载Bean定义时会执行该方法.解析标签定义
 */
public class ContextNamespaceHandler extends NamespaceHandlerSupport {

	@Override
	public void init() {
		// 解析properties配置文件中的key-value值
		registerBeanDefinitionParser("property-placeholder", new PropertyPlaceholderBeanDefinitionParser());
		registerBeanDefinitionParser("property-override", new PropertyOverrideBeanDefinitionParser());
		// 向容器中注册如下3个BeanPostProcessor:。
		//   注意：Sprint 4.2.7版本向容器注册的是4个BeanPostProcessor，其中还有另外一个RequiredAnnotationBeanPostProcessor
		/**
		 * AutowiredAnnotationBeanPostProcessor:
		 * CommonAnnotationBeanPostProcessor:
		 * PersistenceAnnotationBeanPostProcessor:
		 *   这4个BeanPostProcessor的作用，就是为了系统能够识别相应的注解
			 如果想使用@Resource 、@PostConstruct、@PreDestroy等注解就必须声明 CommonAnnotationBeanPostProcessor。
			 如果想使用@PersistenceContext注解，就必须声明 PersistenceAnnotationBeanPostProcessor的Bean。
			 如果想使用@Autowired注解，那么就必须事先在 Spring 容器中声明 AutowiredAnnotationBeanPostProcessor Bean。
			 如果想使用 @Required的注解，就必须声明 RequiredAnnotationBeanPostProcessor 的Bean。
		 */
		registerBeanDefinitionParser("annotation-config", new AnnotationConfigBeanDefinitionParser());
		registerBeanDefinitionParser("component-scan", new ComponentScanBeanDefinitionParser());
		registerBeanDefinitionParser("load-time-weaver", new LoadTimeWeaverBeanDefinitionParser());
		registerBeanDefinitionParser("spring-configured", new SpringConfiguredBeanDefinitionParser());
		registerBeanDefinitionParser("mbean-export", new MBeanExportBeanDefinitionParser());
		registerBeanDefinitionParser("mbean-server", new MBeanServerBeanDefinitionParser());
	}

}
