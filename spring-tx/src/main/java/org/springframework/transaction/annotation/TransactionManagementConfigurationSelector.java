/*
 * Copyright 2002-2018 the original author or authors.
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

package org.springframework.transaction.annotation;

import org.springframework.context.annotation.AdviceMode;
import org.springframework.context.annotation.AdviceModeImportSelector;
import org.springframework.context.annotation.AutoProxyRegistrar;
import org.springframework.transaction.config.TransactionManagementConfigUtils;
import org.springframework.util.ClassUtils;

/**
 * Selects which implementation of {@link AbstractTransactionManagementConfiguration}
 * should be used based on the value of {@link EnableTransactionManagement#mode} on the
 * importing {@code @Configuration} class.
 *
 * @author Chris Beams
 * @author Juergen Hoeller
 * @since 3.1
 * @see EnableTransactionManagement
 * @see ProxyTransactionManagementConfiguration
 * @see TransactionManagementConfigUtils#TRANSACTION_ASPECT_CONFIGURATION_CLASS_NAME
 * @see TransactionManagementConfigUtils#JTA_TRANSACTION_ASPECT_CONFIGURATION_CLASS_NAME
 *
 * 调用时机及调用链分析：
 * 	容器刷新：refresh()
 * 		-> AbstractApplicationContext方法类的invokeBeanFactoryPostProcessors方法
 * 		-> PostProcessorRegistrationDelegate.invokeBeanFactoryPostProcessors方法
 * 		-> PostProcessorRegistrationDelegate类的invokeBeanDefinitionRestryPostProcessors方法
 * 		-> ConfigurationClassPostProcessor类的postProcessBeanDefinitionRegistry方法
 * 		-> ConfigurationClassPostProcessor类的processConfigBeanDefinitions方法
 * 		-> ConfigurationClassParser.parse方法，即：parser.parse(candidates)
 * 		-> 当前bean定义的类型为AnnotatedBeanDefinition，将bean定义强转为AnnotatedBeanDefinition类型作为入参，调用parse方法.
 * 		-> ConfigurationClassParser类的processConfigurationClass方法
 * 		-> ConfigurationClassParser类的doProcessConfigurationClass方法，该方法中会解析JavaConfig配置类中一些列注解配置信息.
 * 		-> ConfigurationClassParser类的processImports方法
 * 		-> 调用AdviceModeImportSelector的selectImports方法
 * 		-> AdviceModeImportSelector的selectImports方法通过模板设计模式调用子类TransactionManagementConfigurationSelector的selectImports方法
 */
public class TransactionManagementConfigurationSelector extends AdviceModeImportSelector<EnableTransactionManagement> {

	/**
	 * Returns {@link ProxyTransactionManagementConfiguration} or
	 * {@code AspectJ(Jta)TransactionManagementConfiguration} for {@code PROXY}
	 * and {@code ASPECTJ} values of {@link EnableTransactionManagement#mode()},
	 * respectively.
	 */
	@Override
	protected String[] selectImports(AdviceMode adviceMode) {
		// 判断代理模式，给容器中导入对应的组件
		switch (adviceMode) {
			// 如果是JDK动态代理模式，默认使用的是PROXY模式
			case PROXY:
				return new String[] {AutoProxyRegistrar.class.getName(), // Bean的后置处理器
						ProxyTransactionManagementConfiguration.class.getName()}; // 注册注解事务解析器，解析事务注解上面的信息
			// 如果是AspectJ代理模式
			case ASPECTJ:
				return new String[] {determineTransactionAspectClass()};
			default:
				return null;
		}
	}

	private String determineTransactionAspectClass() {
		// ClassUtils.isPresent 判断的是类是否存在.
		return (ClassUtils.isPresent("javax.transaction.Transactional", getClass().getClassLoader()) ?
				TransactionManagementConfigUtils.JTA_TRANSACTION_ASPECT_CONFIGURATION_CLASS_NAME :
				TransactionManagementConfigUtils.TRANSACTION_ASPECT_CONFIGURATION_CLASS_NAME);
	}

}
