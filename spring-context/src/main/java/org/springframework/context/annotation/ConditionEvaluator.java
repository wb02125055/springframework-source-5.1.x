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

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ConfigurationCondition.ConfigurationPhase;
import org.springframework.core.annotation.AnnotationAwareOrderComparator;
import org.springframework.core.env.Environment;
import org.springframework.core.env.EnvironmentCapable;
import org.springframework.core.env.StandardEnvironment;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.type.AnnotatedTypeMetadata;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.MultiValueMap;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Internal class used to evaluate {@link Conditional} annotations.
 *
 * @author Phillip Webb
 * @author Juergen Hoeller
 * @since 4.0
 */
class ConditionEvaluator {

	/**
	 * 用于实现Condition注解的逻辑
	 */
	private final ConditionContextImpl context;


	/**
	 * Create a new {@link ConditionEvaluator} instance.
	 */
	public ConditionEvaluator(@Nullable BeanDefinitionRegistry registry,
			@Nullable Environment environment, @Nullable ResourceLoader resourceLoader) {
		// 创建ConditionEvaluator对象.
		this.context = new ConditionContextImpl(registry, environment, resourceLoader);
	}


	/**
	 * Determine if an item should be skipped based on {@code @Conditional} annotations.
	 * The {@link ConfigurationPhase} will be deduced from the type of item (i.e. a
	 * {@code @Configuration} class will be {@link ConfigurationPhase#PARSE_CONFIGURATION})
	 * @param metadata the meta data
	 * @return if the item should be skipped
	 *
	 * metadata: StandardAnnotationMetadata
	 */
	public boolean shouldSkip(AnnotatedTypeMetadata metadata) {
		return shouldSkip(metadata, null);
	}

	/**
	 * Determine if an item should be skipped based on {@code @Conditional} annotations.
	 * @param metadata the meta data
	 * @param phase the phase of the call
	 * @return if the item should be skipped
	 *
	 * // 1.自定义Condition
	 * public class I18nCondition implements Condition {
	 * 		@Override
	 * 		public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
	 * 			// ...
	 * 		 	return true;
	 * 		}
	 * }
	 *
	 * // 2.自定义注解
	 * @Target({ElementType.TYPE, ElementType.METHOD})
	 * @Retention(RetentionPolicy.RUNTIME)
	 * @Documented
	 * @Conditional(I18nCondition.class)
	 * public @interface I18n {
	 * }
	 */
	public boolean shouldSkip(@Nullable AnnotatedTypeMetadata metadata, @Nullable ConfigurationPhase phase) {
		// 如果当前的配置类中没有使用@Conditional注解，直接返回false
		if (metadata == null || !metadata.isAnnotated(Conditional.class.getName())) {
			return false;
		}

		if (phase == null) {
			// 是否标注有@Configuration
			if (metadata instanceof AnnotationMetadata &&
					ConfigurationClassUtils.isConfigurationCandidate((AnnotationMetadata) metadata)) {
				// 递归调用shouldSkip
				return shouldSkip(metadata, ConfigurationPhase.PARSE_CONFIGURATION);
			}
			// 递归调用shouldSkip
			return shouldSkip(metadata, ConfigurationPhase.REGISTER_BEAN);
		}

		List<Condition> conditions = new ArrayList<>();
		// metadata: StandardAnnotationMetaData
		for (String[] conditionClasses : getConditionClasses(metadata)) {
			// 条件类的class名称.
			for (String conditionClass : conditionClasses) {
				// 获取指定类中对应的condition列表
				Condition condition = getCondition(conditionClass, this.context.getClassLoader());
				conditions.add(condition);
			}
		}

		/**
		 * 排序逻辑：
		 * 	先判断是否实现了PriorityOrdered接口，实现了PriorityOrdered接口的Conditional优先级最高
		 * 	如果都实现了PriorityOrdered接口或者都未实现PriorityOrdered接口，则判断是否实现了Ordered接口，如果都未实现，优先级相同，否则实现了Ordered接口的优先级更高
		 */
		AnnotationAwareOrderComparator.sort(conditions);

		for (Condition condition : conditions) {
			ConfigurationPhase requiredPhase = null;
			if (condition instanceof ConfigurationCondition) {
				requiredPhase = ((ConfigurationCondition) condition).getConfigurationPhase();
			}
			// 通过扩展Condition接口，自定义实现类来判断当前的bean定义是否需要跳过. 通过Condition的matches方法的返回值判断
			//   matches方法返回了true，表示需要正常注册bean定义。如果返回了false，则表示不需要注册，直接跳过
			if ((requiredPhase == null || requiredPhase == phase) && !condition.matches(this.context, metadata)) {
				return true;
			}
		}
		// shouldSkip() 返回了false，表示不能跳过当前Bean的注册
		return false;
	}

	@SuppressWarnings("unchecked")
	private List<String[]> getConditionClasses(AnnotatedTypeMetadata metadata) {
		// metadata默认为StandardAnnotationMetadata
		MultiValueMap<String, Object> attributes = metadata.getAllAnnotationAttributes(Conditional.class.getName(), true);
		Object values = (attributes != null ? attributes.get("value") : null);
		// 返回@Conditional中的value对应的条件类的类名称。可以指定多个，为一个数组类型.
		return (List<String[]>) (values != null ? values : Collections.emptyList());
	}

	private Condition getCondition(String conditionClassName, @Nullable ClassLoader classloader) {
		// @Condition(I18nCondition.class) 加载I18nCondition的class
		Class<?> conditionClass = ClassUtils.resolveClassName(conditionClassName, classloader);
		// 实例化自定义的条件类.
		return (Condition) BeanUtils.instantiateClass(conditionClass);
	}


	/**
	 * Implementation of a {@link ConditionContext}.
	 */
	private static class ConditionContextImpl implements ConditionContext {

		@Nullable
		private final BeanDefinitionRegistry registry;

		@Nullable
		private final ConfigurableListableBeanFactory beanFactory;

		private final Environment environment;

		private final ResourceLoader resourceLoader;

		@Nullable
		private final ClassLoader classLoader;

		public ConditionContextImpl(@Nullable BeanDefinitionRegistry registry,
				@Nullable Environment environment, @Nullable ResourceLoader resourceLoader) {

			this.registry = registry;
			// deduce: 推演
			this.beanFactory = deduceBeanFactory(registry);
			this.environment = (environment != null ? environment : deduceEnvironment(registry));
			this.resourceLoader = (resourceLoader != null ? resourceLoader : deduceResourceLoader(registry));
			this.classLoader = deduceClassLoader(resourceLoader, this.beanFactory);
		}

		@Nullable
		private ConfigurableListableBeanFactory deduceBeanFactory(@Nullable BeanDefinitionRegistry source) {
			if (source instanceof ConfigurableListableBeanFactory) {
				return (ConfigurableListableBeanFactory) source;
			}

			// AnnotationConfigApplicationContext默认实现的接口为ConfigurableApplicationContext
			if (source instanceof ConfigurableApplicationContext) {
				return (((ConfigurableApplicationContext) source).getBeanFactory());
			}
			return null;
		}

		private Environment deduceEnvironment(@Nullable BeanDefinitionRegistry source) {
			if (source instanceof EnvironmentCapable) {
				return ((EnvironmentCapable) source).getEnvironment();
			}
			return new StandardEnvironment();
		}

		private ResourceLoader deduceResourceLoader(@Nullable BeanDefinitionRegistry source) {
			if (source instanceof ResourceLoader) {
				return (ResourceLoader) source;
			}
			return new DefaultResourceLoader();
		}

		@Nullable
		private ClassLoader deduceClassLoader(@Nullable ResourceLoader resourceLoader,
				@Nullable ConfigurableListableBeanFactory beanFactory) {

			if (resourceLoader != null) {
				ClassLoader classLoader = resourceLoader.getClassLoader();
				if (classLoader != null) {
					return classLoader;
				}
			}
			if (beanFactory != null) {
				return beanFactory.getBeanClassLoader();
			}
			return ClassUtils.getDefaultClassLoader();
		}

		@Override
		public BeanDefinitionRegistry getRegistry() {
			Assert.state(this.registry != null, "No BeanDefinitionRegistry available");
			return this.registry;
		}

		@Override
		@Nullable
		public ConfigurableListableBeanFactory getBeanFactory() {
			return this.beanFactory;
		}

		@Override
		public Environment getEnvironment() {
			return this.environment;
		}

		@Override
		public ResourceLoader getResourceLoader() {
			return this.resourceLoader;
		}

		@Override
		@Nullable
		public ClassLoader getClassLoader() {
			return this.classLoader;
		}
	}

}
