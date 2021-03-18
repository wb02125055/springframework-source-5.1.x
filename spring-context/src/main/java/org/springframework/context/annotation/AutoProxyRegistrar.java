/*
 * Copyright 2002-2019 the original author or authors.
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

package org.springframework.context.annotation;

import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.springframework.aop.config.AopConfigUtils;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.type.AnnotationMetadata;

/**
 * Registers an auto proxy creator against the current {@link BeanDefinitionRegistry}
 * as appropriate based on an {@code @Enable*} annotation having {@code mode} and
 * {@code proxyTargetClass} attributes set to the correct values.
 *
 * @author Chris Beams
 * @since 3.1
 * @see EnableAspectJAutoProxy
 *
 * 该组件主要是用来给容器中注册组件的.
 * 会调用registerBeanDefinitions方法给容器中注册组件.
 *
 * 调用时机：
 * 	容器刷新时调用 refresh()
 * 		-> invokeBeanFactoryPostProcessors()
 * 		-> PostProcessRegistrationDelegate.invokeBeanFactoryPostProcessors()
 * 		-> PostProcessRegistrationDelegate类中的invokeBeanDefinitionRegistryPostProcessors方法
 * 		-> ConfigurationClassPostProcessor类的postProcessBeanDefinitionRegistry方法
 * 		-> this.reader.loadBeanDefinitions(configClasses)
 * 		-> ConfigurationClassBeanDefinitionReader类的loadBeanDefinitions方法
 * 		-> ConfigurationClassBeanDefinitionReader类的loadBeanDefinitionsFromRegistrars方法
 * 		-> AutoProxyRegistrar类的registerBeanDefinitions方法.
 */
public class AutoProxyRegistrar implements ImportBeanDefinitionRegistrar {

	private final Log logger = LogFactory.getLog(getClass());

	/**
	 * Register, escalate, and configure the standard auto proxy creator (APC) against the
	 * given registry. Works by finding the nearest annotation declared on the importing
	 * {@code @Configuration} class that has both {@code mode} and {@code proxyTargetClass}
	 * attributes. If {@code mode} is set to {@code PROXY}, the APC is registered; if
	 * {@code proxyTargetClass} is set to {@code true}, then the APC is forced to use
	 * subclass (CGLIB) proxying.
	 * <p>Several {@code @Enable*} annotations expose both {@code mode} and
	 * {@code proxyTargetClass} attributes. It is important to note that most of these
	 * capabilities end up sharing a {@linkplain AopConfigUtils#AUTO_PROXY_CREATOR_BEAN_NAME
	 * single APC}. For this reason, this implementation doesn't "care" exactly which
	 * annotation it finds -- as long as it exposes the right {@code mode} and
	 * {@code proxyTargetClass} attributes, the APC can be registered and configured all
	 * the same.
	 *
	 * 在ConfigurationClassBeanDefinitionReader方法中会调用该方法进行bean定义的加载及注册.
	 *
	 * @see ConfigurationClassBeanDefinitionReader
	 */
	@Override
	public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
		boolean candidateFound = false;
		Set<String> annTypes = importingClassMetadata.getAnnotationTypes();
		for (String annType : annTypes) {
			// AnnotationConfigUtils工具类一般是用来获取注解中设置的所有属性
			// 得到一个key-value格式的map对象. key为annotation中的属性，value为annotation中属性对应的值.
			AnnotationAttributes candidate = AnnotationConfigUtils.attributesFor(importingClassMetadata, annType);
			if (candidate == null) {
				continue;
			}
			// 获取@EnableTransactionManagement注解中指定的mode属性
			Object mode = candidate.get("mode");
			// 获取@EnableTransactionManagement注解中指定的proxyTargetClass属性.
			Object proxyTargetClass = candidate.get("proxyTargetClass");
			// mode和proxyTargetClass存在 && mode是AdviceMode类型 && targetClass的类型为Boolean.
			if (mode != null && proxyTargetClass != null && AdviceMode.class == mode.getClass() &&
					Boolean.class == proxyTargetClass.getClass()) {
				candidateFound = true;
				if (mode == AdviceMode.PROXY) {
					// 注解中的默认属性为PROXY，此处会执行。默认给容器中注册一个InfrastructureAdvisorAutoProxyCreator
					AopConfigUtils.registerAutoProxyCreatorIfNecessary(registry);
					// proxyTargetClass如果在@EnableTransactionManagement中未指定，默认值为false，下面不会执行.
					if ((Boolean) proxyTargetClass) {
						AopConfigUtils.forceAutoProxyCreatorToUseClassProxying(registry);
						return;
					}
				}
			}
		}
		if (!candidateFound && logger.isInfoEnabled()) {
			String name = getClass().getSimpleName();
			logger.info(String.format("%s was imported but no annotations were found " +
					"having both 'mode' and 'proxyTargetClass' attributes of type " +
					"AdviceMode and boolean respectively. This means that auto proxy " +
					"creator registration and configuration may not have occurred as " +
					"intended, and components may not be proxied as expected. Check to " +
					"ensure that %s has been @Import'ed on the same class where these " +
					"annotations are declared; otherwise remove the import of %s " +
					"altogether.", name, name, name));
		}
	}

}
