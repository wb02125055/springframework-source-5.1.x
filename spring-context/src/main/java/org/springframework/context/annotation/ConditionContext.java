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

package org.springframework.context.annotation;

import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ResourceLoader;
import org.springframework.lang.Nullable;

/**
 * Context information for use by {@link Condition Conditions}.
 *
 * @author Phillip Webb
 * @author Juergen Hoeller
 * @since 4.0
 */
public interface ConditionContext {

	/**
	 * Return the {@link BeanDefinitionRegistry} that will hold the bean definition
	 * should the condition match.
	 * @throws IllegalStateException if no registry is available (which is unusual:
	 * only the case with a plain {@link ClassPathScanningCandidateComponentProvider})
	 *
	 * 获取ConditionEvaluator匹配到的Bean定义注册中心
	 */
	BeanDefinitionRegistry getRegistry();
	/**
	 * Return the {@link ConfigurableListableBeanFactory} that will hold the bean
	 * definition should the condition match, or {@code null} if the bean factory is
	 * not available (or not downcastable to {@code ConfigurableListableBeanFactory}).
	 *
	 * 获取ConditionEvaluator匹配到的Bean工厂
	 */
	@Nullable
	ConfigurableListableBeanFactory getBeanFactory();

	/**
	 * Return the {@link Environment} for which the current application is running.
	 *
	 * 获取当前应用正在运行中的环境信息
	 */
	Environment getEnvironment();

	/**
	 * Return the {@link ResourceLoader} currently being used.
	 *
	 * 获取当前正在使用的资源加载器
	 */
	ResourceLoader getResourceLoader();

	/**
	 * Return the {@link ClassLoader} that should be used to load additional classes
	 * (only {@code null} if even the system ClassLoader isn't accessible).
	 * @see org.springframework.util.ClassUtils#forName(String, ClassLoader)
	 *
	 * 获取用来加载其他额外类的类加载器。当系统的类加载器不能使用的时候，会调用该方法获取可以使用的类加载器
	 */
	@Nullable
	ClassLoader getClassLoader();

}
