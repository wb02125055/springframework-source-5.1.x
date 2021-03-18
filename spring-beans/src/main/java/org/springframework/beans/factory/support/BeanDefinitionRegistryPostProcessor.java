/*
 * Copyright 2002-2010 the original author or authors.
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

package org.springframework.beans.factory.support;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;

/**
 * Extension to the standard {@link BeanFactoryPostProcessor} SPI, allowing for
 * the registration of further bean definitions <i>before</i> regular
 * BeanFactoryPostProcessor detection kicks in. In particular,
 * BeanDefinitionRegistryPostProcessor may register further bean definitions
 * which in turn define BeanFactoryPostProcessor instances.
 *
 * @author Juergen Hoeller
 * @since 3.0.1
 * @see org.springframework.context.annotation.ConfigurationClassPostProcessor
 */

/**
 * 我们通常在使用 Mybatis + Spring 时，经常用到的 org.mybatis.spring.mapper.MapperScannerConfigurer
 * 就是一个BeanDefinitionRegistryPostProcessor。MapperScannerConfigurer 在 postProcessBeanDefinitionRegistry 方法中进行了一些操作，
 * 主要是：扫描 basePackage 指定的目录，将该目录下的类（通常是 DAO/MAPPER 接口）封装成 BeanDefinition 并加载到 BeanFactory 中。
 * 因此，我们可以看到我们项目中的 DAO（MAPPER）接口，通常都没有使用注解或 XML 的方式注册到 Spring 容器，但是我们还是可以在 Service 服务中，
 * 使用 @Autowire 注解来将其注入到 Service 中，就是因为这个原因。
 *
 * 用法示例：
 * 		@Component
		public class MyBeanDefinitionRegistryPostProcessor implements BeanDefinitionRegistryPostProcessor, Ordered {
			@Override
			public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {
				System.out.println("MyBeanDefinitionRegistryPostProcessor#postProcessBeanDefinitionRegistry");
				// 自己的逻辑处理
			}
			@Override
			public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
				System.out.println("MyBeanDefinitionRegistryPostProcessor#postProcessBeanFactory");
				// 自己的逻辑处理
			}
			@Override
			public int getOrder() {
				return 0;
			}
		}
 */
public interface BeanDefinitionRegistryPostProcessor extends BeanFactoryPostProcessor {

	/**
	 * Modify the application context's internal bean definition registry after its
	 * standard initialization. All regular bean definitions will have been loaded,
	 * but no beans will have been instantiated yet. This allows for adding further
	 * bean definitions before the next post-processing phase kicks in.
	 * @param registry the bean definition registry used by the application context
	 * @throws org.springframework.beans.BeansException in case of errors
	 */
	void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException;

}
