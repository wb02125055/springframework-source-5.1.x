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

import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.annotation.AutowiredAnnotationBeanPostProcessor;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.context.event.DefaultEventListenerFactory;
import org.springframework.context.event.EventListenerMethodProcessor;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.annotation.AnnotationAwareOrderComparator;
import org.springframework.core.type.AnnotatedTypeMetadata;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.lang.Nullable;
import org.springframework.util.ClassUtils;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

/**
 * Utility class that allows for convenient registration of common
 * {@link org.springframework.beans.factory.config.BeanPostProcessor} and
 * {@link org.springframework.beans.factory.config.BeanFactoryPostProcessor}
 * definitions for annotation-based configuration. Also registers a common
 * {@link org.springframework.beans.factory.support.AutowireCandidateResolver}.
 *
 * @author Mark Fisher
 * @author Juergen Hoeller
 * @author Chris Beams
 * @author Phillip Webb
 * @author Stephane Nicoll
 * @since 2.5
 * @see ContextAnnotationAutowireCandidateResolver
 * @see ConfigurationClassPostProcessor
 * @see CommonAnnotationBeanPostProcessor
 * @see org.springframework.beans.factory.annotation.AutowiredAnnotationBeanPostProcessor
 * @see org.springframework.orm.jpa.support.PersistenceAnnotationBeanPostProcessor
 */
public abstract class AnnotationConfigUtils {

	/**
	 * ConfigurationClassPostProcessor后置处理器对应的bean名称.
	 * 该后置处理器继承的接口有：BeanDefinitionRegistryPostProcessor 和 BeanFactoryPostProcessor
	 */
	public static final String CONFIGURATION_ANNOTATION_PROCESSOR_BEAN_NAME =
			"org.springframework.context.annotation.internalConfigurationAnnotationProcessor";

	/**
	 * The bean name of the internally managed BeanNameGenerator for use when processing
	 * {@link Configuration} classes. Set by {@link AnnotationConfigApplicationContext}
	 * and {@code AnnotationConfigWebApplicationContext} during bootstrap in order to make
	 * any custom name generation strategy available to the underlying
	 * {@link ConfigurationClassPostProcessor}.
	 * @since 3.1.1
	 */
	public static final String CONFIGURATION_BEAN_NAME_GENERATOR =
			"org.springframework.context.annotation.internalConfigurationBeanNameGenerator";

	/**
	 * The bean name of the internally managed Autowired annotation processor.
	 */
	public static final String AUTOWIRED_ANNOTATION_PROCESSOR_BEAN_NAME =
			"org.springframework.context.annotation.internalAutowiredAnnotationProcessor";

	/**
	 * The bean name of the internally managed Required annotation processor.
	 * @deprecated as of 5.1, since no Required processor is registered by default anymore
	 */
	@Deprecated
	public static final String REQUIRED_ANNOTATION_PROCESSOR_BEAN_NAME =
			"org.springframework.context.annotation.internalRequiredAnnotationProcessor";

	/**
	 * The bean name of the internally managed JSR-250 annotation processor.
	 */
	public static final String COMMON_ANNOTATION_PROCESSOR_BEAN_NAME =
			"org.springframework.context.annotation.internalCommonAnnotationProcessor";

	/**
	 * The bean name of the internally managed JPA annotation processor.
	 */
	public static final String PERSISTENCE_ANNOTATION_PROCESSOR_BEAN_NAME =
			"org.springframework.context.annotation.internalPersistenceAnnotationProcessor";

	private static final String PERSISTENCE_ANNOTATION_PROCESSOR_CLASS_NAME =
			"org.springframework.orm.jpa.support.PersistenceAnnotationBeanPostProcessor";

	/**
	 * The bean name of the internally managed @EventListener annotation processor.
	 */
	public static final String EVENT_LISTENER_PROCESSOR_BEAN_NAME =
			"org.springframework.context.event.internalEventListenerProcessor";

	/**
	 * The bean name of the internally managed EventListenerFactory.
	 */
	public static final String EVENT_LISTENER_FACTORY_BEAN_NAME =
			"org.springframework.context.event.internalEventListenerFactory";

	private static final boolean jsr250Present;

	private static final boolean jpaPresent;

	static {
		ClassLoader classLoader = AnnotationConfigUtils.class.getClassLoader();
		jsr250Present = ClassUtils.isPresent("javax.annotation.Resource", classLoader);
		jpaPresent = ClassUtils.isPresent("javax.persistence.EntityManagerFactory", classLoader) &&
				ClassUtils.isPresent(PERSISTENCE_ANNOTATION_PROCESSOR_CLASS_NAME, classLoader);
	}


	/**
	 * Register all relevant annotation post processors in the given registry.
	 * @param registry the registry to operate on
	 *
	 *
	 * registry: AnnotationConfigApplicationContext
	 */
	public static void registerAnnotationConfigProcessors(BeanDefinitionRegistry registry) {
		// 向Bean定义注册中心注册注解配置形式的后置处理器
		registerAnnotationConfigProcessors(registry, null);
	}

	/**
	 * Register all relevant annotation post processors in the given registry.
	 * @param registry the registry to operate on
	 * @param source the configuration source element (already extracted)
	 * that this registration was triggered from. May be {@code null}.
	 * @return a Set of BeanDefinitionHolders, containing all bean definitions
	 * that have actually been registered by this call
	 *
	 * 为什么需要返回值？
	 *     在xml方式配置bean的时候，如果开启了注解驱动，需要在解析xml标签的时候得到这些默认的后置处理器，做下一步的操作.
	 *     @see ComponentScanBeanDefinitionParser
	 */
	public static Set<BeanDefinitionHolder> registerAnnotationConfigProcessors(
			BeanDefinitionRegistry registry, @Nullable Object source) {
		// 获取Bean工厂.默认为DefaultListableBeanFactory
		DefaultListableBeanFactory beanFactory = unwrapDefaultListableBeanFactory(registry);
		if (beanFactory != null) {
			if (!(beanFactory.getDependencyComparator() instanceof AnnotationAwareOrderComparator)) {
				// 设置默认的排序比较器，在执行Bean工厂的后置处理器时会用到，用来按照优先级来执行Bean工厂的后置处理器。
				// AnnotationAwareOrderComparator是OrderComparator的子类
				//  用来支持Spring的Ordered类、@Order注解和@Priority注解
				beanFactory.setDependencyComparator(AnnotationAwareOrderComparator.INSTANCE);
			}
			// beanFactory中的autowireCandidateResolver默认为：SimpleAutowireCandidateResolver
			if (!(beanFactory.getAutowireCandidateResolver() instanceof ContextAnnotationAutowireCandidateResolver)) {
				// 设置Bean工厂中bean自动注入时的候选解析器
				beanFactory.setAutowireCandidateResolver(new ContextAnnotationAutowireCandidateResolver());
			}
		}

		Set<BeanDefinitionHolder> beanDefs = new LinkedHashSet<>(8);

		// 判断Bean定义注册中心是否包含有名称为"org.springframework.context.annotation.internalConfigurationAnnotationProcessor"的Bean定义
		// 如果不包括，则创建类型为ConfigurationClassPostProcessor的Bean定义，并向Bean定义注册中心里面注册

		// 用途：ConfigurationClassPostProcessor后置处理器是用来处理@Configuration，@Import，@ImportResource和类内部的@Bean
		if (!registry.containsBeanDefinition(CONFIGURATION_ANNOTATION_PROCESSOR_BEAN_NAME)) {
			// 继承关系：
			// ConfigurationClassPostProcess --> BeanDefinitionRegistryPostProcessor --> BeanFactoryPostProcessor
			RootBeanDefinition def = new RootBeanDefinition(ConfigurationClassPostProcessor.class);
			def.setSource(source);
			// 将Bean定义放入到Bean定义注册中心的map中.
			beanDefs.add(registerPostProcessor(registry, def, CONFIGURATION_ANNOTATION_PROCESSOR_BEAN_NAME));
		}

		// 判断bean定义注册中心中是否包括名称为org.springframework.context.annotation.internalAutowiredAnnotationProcessor的Bean定义，
		// 如果不包括，向bean定义注册中心注册类型为AutowiredAnnotationBeanPostProcessor的Bean定义.

		// 用途：AutowiredAnnotationBeanPostProcessor后置处理器是用来处理@Autowired注解和@Value注解的
		if (!registry.containsBeanDefinition(AUTOWIRED_ANNOTATION_PROCESSOR_BEAN_NAME)) {
			// AutowireAnnotationBeanPostProcessor --> InstantiationAwareBeanPostProcessorAdapter
			// 		--> SmartInstantiationAwareBeanPostProcessor --> InstantiationAwareBeanPostProcessor --> BeanPostProcessor
			RootBeanDefinition def = new RootBeanDefinition(AutowiredAnnotationBeanPostProcessor.class);
			def.setSource(source);
			beanDefs.add(registerPostProcessor(registry, def, AUTOWIRED_ANNOTATION_PROCESSOR_BEAN_NAME));
		}

		// Check for JSR-250 support, and if present add the CommonAnnotationBeanPostProcessor.
		// 判断当前版本是否支持JSR-250
		// 然后判断bean定义注册中心中是否包括名称为org.springframework.context.annotation.internalCommonAnnotationProcessor的Bean定义，
		// 如果不包括，向bean定义注册中心注册类型为CommonAnnotationBeanPostProcessor的Bean定义.

		// 用途：CommonAnnotationBeanPostProcessor后置处理器提供对JSR-250规范注解的支持@javax.annotation.Resource、
		//    @javax.annotation.PostConstruct和@javax.annotation.PreDestroy等的支持。
		if (jsr250Present && !registry.containsBeanDefinition(COMMON_ANNOTATION_PROCESSOR_BEAN_NAME)) {
			// CommonAnnotationBeanPostProcessor --> InitDestroyAnnotationBeanPostProcessor(class) --> DestructionAwareBeanPostProcessor,MergedBeanDefinitionPostProcessor
			//			--> InstantiationAwareBeanPostProcessor --> BeanPostProcessor
			RootBeanDefinition def = new RootBeanDefinition(CommonAnnotationBeanPostProcessor.class);
			def.setSource(source);
			beanDefs.add(registerPostProcessor(registry, def, COMMON_ANNOTATION_PROCESSOR_BEAN_NAME));
		}

		// Check for JPA support, and if present add the PersistenceAnnotationBeanPostProcessor.
		// 判断是否支持JPA
		// 然后判断bean定义注册中心中是否包括名称为org.springframework.context.annotation.internalPersistenceAnnotationProcessor的Bean定义，
		// 如果不包括，向bean定义注册中心注册类型为PersistenceAnnotationBeanPostProcessor的Bean定义.

		//  用途：EventListenerMethodProcessor后置处理器提供@PersistenceContext的支持。
		if (jpaPresent && !registry.containsBeanDefinition(PERSISTENCE_ANNOTATION_PROCESSOR_BEAN_NAME)) {
			RootBeanDefinition def = new RootBeanDefinition();
			try {
				def.setBeanClass(ClassUtils.forName(PERSISTENCE_ANNOTATION_PROCESSOR_CLASS_NAME,
						AnnotationConfigUtils.class.getClassLoader()));
			}
			catch (ClassNotFoundException ex) {
				throw new IllegalStateException(
						"Cannot load optional framework class: " + PERSISTENCE_ANNOTATION_PROCESSOR_CLASS_NAME, ex);
			}
			def.setSource(source);
			beanDefs.add(registerPostProcessor(registry, def, PERSISTENCE_ANNOTATION_PROCESSOR_BEAN_NAME));
		}

		// 判断bean定义注册中心中是否包括名称为org.springframework.context.event.internalEventListenerProcessor的Bean定义，
		// 如果不包括，向bean定义注册中心注册类型为EventListenerMethodProcessor的Bean定义.
		if (!registry.containsBeanDefinition(EVENT_LISTENER_PROCESSOR_BEAN_NAME)) {
			RootBeanDefinition def = new RootBeanDefinition(EventListenerMethodProcessor.class);
			def.setSource(source);
			beanDefs.add(registerPostProcessor(registry, def, EVENT_LISTENER_PROCESSOR_BEAN_NAME));
		}

		// 判断bean定义注册中心中是否包括名称为org.springframework.context.event.internalEventListenerFactory的Bean定义，
		// 如果不包括，向bean定义注册中心注册类型为DefaultEventListenerFactory的Bean定义.

		// 用途：EventListenerMethodProcessor后置处理器提供对@EventListener注解的支持。
		//  @EventListener是在spring4.2之后出现的，可以在一个Bean的方法上使用@EventListener注解来自动注册一个ApplicationListener。
		if (!registry.containsBeanDefinition(EVENT_LISTENER_FACTORY_BEAN_NAME)) {
			RootBeanDefinition def = new RootBeanDefinition(DefaultEventListenerFactory.class);
			def.setSource(source);
			beanDefs.add(registerPostProcessor(registry, def, EVENT_LISTENER_FACTORY_BEAN_NAME));
		}

		return beanDefs;
	}

	/**
	 * 向bean定义注册中心注册Bean定义.
	 * @param registry bean定义注册中心
	 * @param definition bean定义
	 * @param beanName bean的名称
	 * @return bean定义的一个包装对象。BeanDefinitionHolder是专门用来保存bean定义的
	 */
	private static BeanDefinitionHolder registerPostProcessor(
			BeanDefinitionRegistry registry, RootBeanDefinition definition, String beanName) {
		// 表示这个Bean是一个内部使用的Bean.
		definition.setRole(BeanDefinition.ROLE_INFRASTRUCTURE);

		// 将bean定义注册到bean定义注册中心的map集合中。即：beanDefinitionMap中
		registry.registerBeanDefinition(beanName, definition);
		return new BeanDefinitionHolder(definition, beanName);
	}

	@Nullable
	private static DefaultListableBeanFactory unwrapDefaultListableBeanFactory(BeanDefinitionRegistry registry) {
		if (registry instanceof DefaultListableBeanFactory) {
			return (DefaultListableBeanFactory) registry;
		}
		// 注意：AnnotationConfigApplicationContext是直接继承自GenericApplicationContext
		else if (registry instanceof GenericApplicationContext) {
			return ((GenericApplicationContext) registry).getDefaultListableBeanFactory();
		}
		else {
			return null;
		}
	}

	public static void processCommonDefinitionAnnotations(AnnotatedBeanDefinition abd) {
		processCommonDefinitionAnnotations(abd, abd.getMetadata());
	}

	/**
	 * 解析公共注解上面的元数据信息并添加到bean定义中
	 * @param abd bean定义
	 * @param metadata 注解元数据信息
	 */
	static void processCommonDefinitionAnnotations(AnnotatedBeanDefinition abd, AnnotatedTypeMetadata metadata) {
		AnnotationAttributes lazy = attributesFor(metadata, Lazy.class);
		if (lazy != null) {
			abd.setLazyInit(lazy.getBoolean("value"));
		}
		else if (abd.getMetadata() != metadata) {
			lazy = attributesFor(abd.getMetadata(), Lazy.class);
			if (lazy != null) {
				abd.setLazyInit(lazy.getBoolean("value"));
			}
		}

		if (metadata.isAnnotated(Primary.class.getName())) {
			abd.setPrimary(true);
		}
		AnnotationAttributes dependsOn = attributesFor(metadata, DependsOn.class);
		if (dependsOn != null) {
			abd.setDependsOn(dependsOn.getStringArray("value"));
		}

		AnnotationAttributes role = attributesFor(metadata, Role.class);
		if (role != null) {
			abd.setRole(role.getNumber("value").intValue());
		}
		AnnotationAttributes description = attributesFor(metadata, Description.class);
		if (description != null) {
			abd.setDescription(description.getString("value"));
		}
	}

	static BeanDefinitionHolder applyScopedProxyMode(
			ScopeMetadata metadata, BeanDefinitionHolder definition, BeanDefinitionRegistry registry) {
		// 获取代理的创建模式
		ScopedProxyMode scopedProxyMode = metadata.getScopedProxyMode();
		// 获取作用域的代理模式，如果是NO，直接返回Bean定义
		if (scopedProxyMode.equals(ScopedProxyMode.NO)) {
			return definition;
		}
		// 是否使用动态代理的方式
		boolean proxyTargetClass = scopedProxyMode.equals(ScopedProxyMode.TARGET_CLASS);
		// 设置bean定义中生成bean的方式。是否通过代理生成
		return ScopedProxyCreator.createScopedProxy(definition, registry, proxyTargetClass);
	}

	@Nullable
	static AnnotationAttributes attributesFor(AnnotatedTypeMetadata metadata, Class<?> annotationClass) {
		return attributesFor(metadata, annotationClass.getName());
	}

	@Nullable
	static AnnotationAttributes attributesFor(AnnotatedTypeMetadata metadata, String annotationClassName) {
		// metadata默认实现类为：StandardAnnotationMetadata
		return AnnotationAttributes.fromMap(metadata.getAnnotationAttributes(annotationClassName, false));
	}

	static Set<AnnotationAttributes> attributesForRepeatable(AnnotationMetadata metadata,
			Class<?> containerClass, Class<?> annotationClass) {
		return attributesForRepeatable(metadata, containerClass.getName(), annotationClass.getName());
	}

	@SuppressWarnings("unchecked")
	static Set<AnnotationAttributes> attributesForRepeatable(
			AnnotationMetadata metadata, String containerClassName, String annotationClassName) {

		Set<AnnotationAttributes> result = new LinkedHashSet<>();

		// Direct annotation present?
		addAttributesIfNotNull(result, metadata.getAnnotationAttributes(annotationClassName, false));

		// Container annotation present?
		Map<String, Object> container = metadata.getAnnotationAttributes(containerClassName, false);
		if (container != null && container.containsKey("value")) {
			for (Map<String, Object> containedAttributes : (Map<String, Object>[]) container.get("value")) {
				addAttributesIfNotNull(result, containedAttributes);
			}
		}

		// Return merged result
		return Collections.unmodifiableSet(result);
	}

	private static void addAttributesIfNotNull(
			Set<AnnotationAttributes> result, @Nullable Map<String, Object> attributes) {

		if (attributes != null) {
			result.add(AnnotationAttributes.fromMap(attributes));
		}
	}

}
