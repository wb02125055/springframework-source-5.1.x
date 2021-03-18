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

import org.springframework.beans.factory.config.BeanDefinitionCustomizer;
import org.springframework.beans.factory.support.BeanNameGenerator;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

import java.util.function.Supplier;

/**
 * Standalone application context, accepting <em>component classes</em> as input &mdash;
 * in particular {@link Configuration @Configuration}-annotated classes, but also plain
 * {@link org.springframework.stereotype.Component @Component} types and JSR-330 compliant
 * classes using {@code javax.inject} annotations.
 *
 * <p>Allows for registering classes one by one using {@link #register(Class...)}
 * as well as for classpath scanning using {@link #scan(String...)}.
 *
 * <p>In case of multiple {@code @Configuration} classes, {@link Bean @Bean} methods
 * defined in later classes will override those defined in earlier classes. This can
 * be leveraged to deliberately override certain bean definitions via an extra
 * {@code @Configuration} class.
 *
 * <p>See {@link Configuration @Configuration}'s javadoc for usage examples.
 *
 * @author Juergen Hoeller
 * @author Chris Beams
 * @since 3.0
 * @see #register
 * @see #scan
 * @see AnnotatedBeanDefinitionReader
 * @see ClassPathBeanDefinitionScanner
 * @see org.springframework.context.support.GenericXmlApplicationContext
 */
public class AnnotationConfigApplicationContext
		extends GenericApplicationContext
		implements AnnotationConfigRegistry {

	/**
	 * 注解形式的Bean定义扫描器
	 */
	private final AnnotatedBeanDefinitionReader reader;
	/**
	 * classpath格式的Bean定义扫描器
	 */
	private final ClassPathBeanDefinitionScanner scanner;

	/**
	 * Create a new AnnotationConfigApplicationContext that needs to be populated
	 * through {@link #register} calls and then manually {@linkplain #refresh refreshed}.
	 */
	public AnnotationConfigApplicationContext() {
		/** 1.使用父类GenericApplicationContext创建默认的Bean工厂: DefaultListableBeanFactory */

		/**
		 * 2.初始化默认的Bean定义读取器，该步骤也会给容器中注册用来处理Spring注解的后置处理器.
		 *   创建conditionEvaluator，默认类型为：ConditionEvaluator
		 *
		 * 注意：该步骤中会向容器中注入以下几个比较重要的后置处理器：
		 * 	(1) ConfigurationClassPostProcessor(本质是一个BeanFactoryPostProcessor): 用来处理JavaConfig配置类的后置处理器，包括JavaConfig配置类中注解的解析及处理操作
		 * 	(2) AutowiredAnnotationBeanPostProcessor: 用来处理@Autowired和@Value注解
		 * 	(3) CommonAnnotationBeanPostProcessor: 提供对JSR-250规范注解的支持@javax.annotation.Resource、
		 * 	    @javax.annotation.PostConstruct 和 @javax.annotation.PreDestroy等的支持。
		 * 	(4) EventListenerMethodProcessor：提供对@PersistenceContext的支持
		 * 	(5) DefaultEventListenerFactory: 提供对@EventListener的支持
		 */
		this.reader = new AnnotatedBeanDefinitionReader(this);

		/**
		 * 3.初始化默认的Bean定义扫描器，在ClassPathBeanDefinitionScanner的构造中，会初始化配置文件解析器
		 *
		 *  初始化CandidateComponentsIndexLoader对象，用来按照索引加载bean定义
		 */
		this.scanner = new ClassPathBeanDefinitionScanner(this);
	}

	/**
	 * Create a new AnnotationConfigApplicationContext with the given DefaultListableBeanFactory.
	 * @param beanFactory the DefaultListableBeanFactory instance to use for this context
	 */
	public AnnotationConfigApplicationContext(DefaultListableBeanFactory beanFactory) {
		super(beanFactory);

		// 创建注解类型的bean定义读取器，用来给容器中注册一些Spring框架本身需要使用的内部Bean
		this.reader = new AnnotatedBeanDefinitionReader(this);

		// 创建bean定义扫描器，完成bean定义扫描的准备操作
		this.scanner = new ClassPathBeanDefinitionScanner(this);
	}

	/**
	 * Create a new AnnotationConfigApplicationContext, deriving bean definitions
	 * from the given component classes and automatically refreshing the context.
	 * @param componentClasses one or more component classes &mdash; for example,
	 * {@link Configuration @Configuration} classes
	 */
	public AnnotationConfigApplicationContext(Class<?>... componentClasses) {
		// 调用本类的构造函数，创建默认的Bean工厂DefaultListableBeanFactory
		// 		初始化内置的一些后置处理器【包括：用来解析常用注解@Resource，@Autowired，@Configuration等注解的后置处理器】

		// 除此之外，还会初始化Spring容器所需的环境信息对象Environment及资源加载器对象ResourceLoader，还会初始化配置文件解析器
		this();

		// 解析配置类并将配置类封装为一个bean定义注册到Bean定义注册中心
		register(componentClasses);

		// 执行刷新的容器操作
		refresh();
	}

	/**
	 * Create a new AnnotationConfigApplicationContext, scanning for components
	 * in the given packages, registering bean definitions for those components,
	 * and automatically refreshing the context.
	 * @param basePackages the packages to scan for component classes
	 */
	public AnnotationConfigApplicationContext(String... basePackages) {
		/**
		 * 注意：使用这种方式。AnnotationConfigUtils.registerAnnotationConfigProcessors(this.registry)方法会被调用两次，
		 * 		在this中会被调用一次，注册Spring中默认的四个后置处理器。
		 * 		然后在scan方法中，会再次调用该方法，只是调用的时候，bean定义注册中心中已经存在了这个bean定义，所以也不会重复注册.
		 */
		this();

		// 执行包扫描
		scan(basePackages);

		// 执行容器的刷新操作
		refresh();
	}


	/**
	 * Propagate the given custom {@code Environment} to the underlying
	 * {@link AnnotatedBeanDefinitionReader} and {@link ClassPathBeanDefinitionScanner}.
	 */
	@Override
	public void setEnvironment(ConfigurableEnvironment environment) {
		super.setEnvironment(environment);
		this.reader.setEnvironment(environment);
		this.scanner.setEnvironment(environment);
	}

	/**
	 * Provide a custom {@link BeanNameGenerator} for use with {@link AnnotatedBeanDefinitionReader}
	 * and/or {@link ClassPathBeanDefinitionScanner}, if any.
	 * <p>Default is {@link org.springframework.context.annotation.AnnotationBeanNameGenerator}.
	 * <p>Any call to this method must occur prior to calls to {@link #register(Class...)}
	 * and/or {@link #scan(String...)}.
	 * @see AnnotatedBeanDefinitionReader#setBeanNameGenerator
	 * @see ClassPathBeanDefinitionScanner#setBeanNameGenerator
	 */
	public void setBeanNameGenerator(BeanNameGenerator beanNameGenerator) {
		this.reader.setBeanNameGenerator(beanNameGenerator);
		this.scanner.setBeanNameGenerator(beanNameGenerator);
		getBeanFactory().registerSingleton(
				AnnotationConfigUtils.CONFIGURATION_BEAN_NAME_GENERATOR, beanNameGenerator);
	}

	/**
	 * Set the {@link ScopeMetadataResolver} to use for registered component classes.
	 * <p>The default is an {@link AnnotationScopeMetadataResolver}.
	 * <p>Any call to this method must occur prior to calls to {@link #register(Class...)}
	 * and/or {@link #scan(String...)}.
	 */
	public void setScopeMetadataResolver(ScopeMetadataResolver scopeMetadataResolver) {
		this.reader.setScopeMetadataResolver(scopeMetadataResolver);
		this.scanner.setScopeMetadataResolver(scopeMetadataResolver);
	}


	//---------------------------------------------------------------------
	// Implementation of AnnotationConfigRegistry
	//---------------------------------------------------------------------

	/**
	 * Register one or more component classes to be processed.
	 * <p>Note that {@link #refresh()} must be called in order for the context
	 * to fully process the new classes.
	 * @param componentClasses one or more component classes &mdash; for example,
	 * {@link Configuration @Configuration} classes
	 * @see #scan(String...)
	 * @see #refresh()
	 *
	 * 从接口中的实现方法：AnnotationConfigRegistry
	 */
	@Override
	public void register(Class<?>... componentClasses) {
		Assert.notEmpty(componentClasses, "At least one component class must be specified");
		// 将配置类解析为Bean定义，并注册到Bean定义注册中心.
		this.reader.register(componentClasses);
	}

	/**
	 * Perform a scan within the specified base packages.
	 * <p>Note that {@link #refresh()} must be called in order for the context
	 * to fully process the new classes.
	 * @param basePackages the packages to scan for component classes
	 * @see #register(Class...)
	 * @see #refresh()
	 *
	 * 从接口中的实现方法：AnnotationConfigRegistry
	 */
	@Override
	public void scan(String... basePackages) {
		Assert.notEmpty(basePackages, "At least one base package must be specified");
		// 扫描指定包路径下的所有组件
		this.scanner.scan(basePackages);
	}


	//---------------------------------------------------------------------
	// Convenient methods for registering individual beans
	//---------------------------------------------------------------------

	/**
	 * Register a bean from the given bean class, deriving its metadata from
	 * class-declared annotations, and optionally providing explicit constructor
	 * arguments for consideration in the autowiring process.
	 * <p>The bean name will be generated according to annotated component rules.
	 * @param beanClass the class of the bean
	 * @param constructorArguments argument values to be fed into Spring's
	 * constructor resolution algorithm, resolving either all arguments or just
	 * specific ones, with the rest to be resolved through regular autowiring
	 * (may be {@code null} or empty)
	 * @since 5.0
	 */
	public <T> void registerBean(Class<T> beanClass, Object... constructorArguments) {
		registerBean(null, beanClass, constructorArguments);
	}

	/**
	 * Register a bean from the given bean class, deriving its metadata from
	 * class-declared annotations, and optionally providing explicit constructor
	 * arguments for consideration in the autowiring process.
	 * @param beanName the name of the bean (may be {@code null})
	 * @param beanClass the class of the bean
	 * @param constructorArguments argument values to be fed into Spring's
	 * constructor resolution algorithm, resolving either all arguments or just
	 * specific ones, with the rest to be resolved through regular autowiring
	 * (may be {@code null} or empty)
	 * @since 5.0
	 */
	public <T> void registerBean(@Nullable String beanName, Class<T> beanClass, Object... constructorArguments) {
		this.reader.doRegisterBean(beanClass, null, beanName, null,
				bd -> {
					for (Object arg : constructorArguments) {
						bd.getConstructorArgumentValues().addGenericArgumentValue(arg);
					}
				});
	}

	@Override
	public <T> void registerBean(@Nullable String beanName, Class<T> beanClass, @Nullable Supplier<T> supplier,
			BeanDefinitionCustomizer... customizers) {

		this.reader.doRegisterBean(beanClass, supplier, beanName, null, customizers);
	}

}
