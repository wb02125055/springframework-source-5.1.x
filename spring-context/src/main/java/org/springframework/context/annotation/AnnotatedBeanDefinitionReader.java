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

import org.springframework.beans.factory.annotation.AnnotatedGenericBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionCustomizer;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.support.AutowireCandidateQualifier;
import org.springframework.beans.factory.support.BeanDefinitionReaderUtils;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanNameGenerator;
import org.springframework.core.env.Environment;
import org.springframework.core.env.EnvironmentCapable;
import org.springframework.core.env.StandardEnvironment;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

import java.lang.annotation.Annotation;
import java.util.function.Supplier;

/**
 * Convenient adapter for programmatic registration of bean classes.
 *
 * <p>This is an alternative to {@link ClassPathBeanDefinitionScanner}, applying
 * the same resolution of annotations but for explicitly registered classes only.
 *
 * @author Juergen Hoeller
 * @author Chris Beams
 * @author Sam Brannen
 * @author Phillip Webb
 * @since 3.0
 * @see AnnotationConfigApplicationContext#register
 */
public class AnnotatedBeanDefinitionReader {

	/**
	 * Bean定义注册中心接口定义.
	 *  提供注册Bean定义及获取Bean定义信息的方法
	 */
	private final BeanDefinitionRegistry registry;

	/**
	 * 用来生成Bean名称的生成器
	 */
	private BeanNameGenerator beanNameGenerator = new AnnotationBeanNameGenerator();

	/**
	 * 用来解析Bean作用域信息的解析器。根据给定的Bean定义信息，解析出来Bean的作用域信息.
	 */
	private ScopeMetadataResolver scopeMetadataResolver = new AnnotationScopeMetadataResolver();

	/**
	 * 用于处理条件注解
	 */
	private ConditionEvaluator conditionEvaluator;


	/**
	 * Create a new {@code AnnotatedBeanDefinitionReader} for the given registry.
	 * <p>If the registry is {@link EnvironmentCapable}, e.g. is an {@code ApplicationContext},
	 * the {@link Environment} will be inherited, otherwise a new
	 * {@link StandardEnvironment} will be created and used.
	 * @param registry the {@code BeanFactory} to load bean definitions into,
	 * in the form of a {@code BeanDefinitionRegistry}
	 * @see #AnnotatedBeanDefinitionReader(BeanDefinitionRegistry, Environment)
	 * @see #setEnvironment(Environment)
	 */
	public AnnotatedBeanDefinitionReader(BeanDefinitionRegistry registry) {
		// getOrCreateEnvironment默认返回的是StandardEnvironment
		this(registry, getOrCreateEnvironment(registry));
	}

	/**
	 * Create a new {@code AnnotatedBeanDefinitionReader} for the given registry,
	 * using the given {@link Environment}.
	 * @param registry the {@code BeanFactory} to load bean definitions into,
	 * in the form of a {@code BeanDefinitionRegistry}
	 * @param environment the {@code Environment} to use when evaluating bean definition
	 * profiles.
	 * @since 3.1
	 */
	public AnnotatedBeanDefinitionReader(BeanDefinitionRegistry registry, Environment environment) {
		Assert.notNull(registry, "BeanDefinitionRegistry must not be null");
		Assert.notNull(environment, "Environment must not be null");

		// 初始化BeanDefinitionRegistry
		this.registry = registry;

		// 初始化用于处理条件注解的对象，environment默认为StandardEnvironment
		/**
		 * 在创建ConditionEvaluator的时候，会去初始化ConditionContextImpl上下文。
		 * 里面会持有(registry,beanFactory,environment,resourceLoader,classLoader的引用)
		 */
		this.conditionEvaluator = new ConditionEvaluator(registry, environment, null);

		// 注册注解版的后置处理器.
		AnnotationConfigUtils.registerAnnotationConfigProcessors(this.registry);
	}


	/**
	 * Get the BeanDefinitionRegistry that this reader operates on.
	 */
	public final BeanDefinitionRegistry getRegistry() {
		return this.registry;
	}

	/**
	 * Set the {@code Environment} to use when evaluating whether
	 * {@link Conditional @Conditional}-annotated component classes should be registered.
	 * <p>The default is a {@link StandardEnvironment}.
	 * @see #registerBean(Class, String, Class...)
	 */
	public void setEnvironment(Environment environment) {
		this.conditionEvaluator = new ConditionEvaluator(this.registry, environment, null);
	}

	/**
	 * Set the {@code BeanNameGenerator} to use for detected bean classes.
	 * <p>The default is a {@link AnnotationBeanNameGenerator}.
	 */
	public void setBeanNameGenerator(@Nullable BeanNameGenerator beanNameGenerator) {
		this.beanNameGenerator = (beanNameGenerator != null ? beanNameGenerator : new AnnotationBeanNameGenerator());
	}

	/**
	 * Set the {@code ScopeMetadataResolver} to use for registered component classes.
	 * <p>The default is an {@link AnnotationScopeMetadataResolver}.
	 */
	public void setScopeMetadataResolver(@Nullable ScopeMetadataResolver scopeMetadataResolver) {
		this.scopeMetadataResolver =
				(scopeMetadataResolver != null ? scopeMetadataResolver : new AnnotationScopeMetadataResolver());
	}


	/**
	 * Register one or more component classes to be processed.
	 * <p>Calls to {@code register} are idempotent; adding the same
	 * component class more than once has no additional effect.
	 * @param componentClasses one or more component classes,
	 * e.g. {@link Configuration @Configuration} classes
	 */
	public void register(Class<?>... componentClasses) {
		// 配置类可以传入多个，依次注册传入的配置类.
		for (Class<?> componentClass : componentClasses) {
			registerBean(componentClass);
		}
	}

	/**
	 * Register a bean from the given bean class, deriving its metadata from
	 * class-declared annotations.
	 * @param beanClass the class of the bean
	 */
	public void registerBean(Class<?> beanClass) {
		doRegisterBean(beanClass, null, null, null);
	}

	/**
	 * Register a bean from the given bean class, deriving its metadata from
	 * class-declared annotations, using the given supplier for obtaining a new
	 * instance (possibly declared as a lambda expression or method reference).
	 * @param beanClass the class of the bean
	 * @param instanceSupplier a callback for creating an instance of the bean
	 * (may be {@code null})
	 * @since 5.0
	 */
	public <T> void registerBean(Class<T> beanClass, @Nullable Supplier<T> instanceSupplier) {
		doRegisterBean(beanClass, instanceSupplier, null, null);
	}

	/**
	 * Register a bean from the given bean class, deriving its metadata from
	 * class-declared annotations, using the given supplier for obtaining a new
	 * instance (possibly declared as a lambda expression or method reference).
	 * @param beanClass the class of the bean
	 * @param name an explicit name for the bean
	 * @param instanceSupplier a callback for creating an instance of the bean
	 * (may be {@code null})
	 * @since 5.0
	 */
	public <T> void registerBean(Class<T> beanClass, String name, @Nullable Supplier<T> instanceSupplier) {
		doRegisterBean(beanClass, instanceSupplier, name, null);
	}

	/**
	 * Register a bean from the given bean class, deriving its metadata from
	 * class-declared annotations.
	 * @param beanClass the class of the bean
	 * @param qualifiers specific qualifier annotations to consider,
	 * in addition to qualifiers at the bean class level
	 */
	@SuppressWarnings("unchecked")
	public void registerBean(Class<?> beanClass, Class<? extends Annotation>... qualifiers) {
		doRegisterBean(beanClass, null, null, qualifiers);
	}

	/**
	 * Register a bean from the given bean class, deriving its metadata from
	 * class-declared annotations.
	 * @param beanClass the class of the bean
	 * @param name an explicit name for the bean
	 * @param qualifiers specific qualifier annotations to consider,
	 * in addition to qualifiers at the bean class level
	 */
	@SuppressWarnings("unchecked")
	public void registerBean(Class<?> beanClass, String name, Class<? extends Annotation>... qualifiers) {
		doRegisterBean(beanClass, null, name, qualifiers);
	}

	/**
	 * Register a bean from the given bean class, deriving its metadata from
	 * class-declared annotations.
	 * @param beanClass the class of the bean
	 * @param instanceSupplier a callback for creating an instance of the bean
	 * (may be {@code null})
	 * @param name an explicit name for the bean
	 * @param qualifiers specific qualifier annotations to consider, if any,
	 * in addition to qualifiers at the bean class level
	 * @param definitionCustomizers one or more callbacks for customizing the
	 * factory's {@link BeanDefinition}, e.g. setting a lazy-init or primary flag
	 * @since 5.0
	 *
	 *
	 * doRegisterBean(class, null, null, null, null);
	 */
	<T> void doRegisterBean(Class<T> beanClass, @Nullable Supplier<T> instanceSupplier, @Nullable String name,
			@Nullable Class<? extends Annotation>[] qualifiers, BeanDefinitionCustomizer... definitionCustomizers) {

		// 实例化注解格式的Bean定义。其中包括设置bean的类型以及初始化用于获取类注解信息的元数据对象
		AnnotatedGenericBeanDefinition abd = new AnnotatedGenericBeanDefinition(beanClass);
		/**
		 * 根据类上面的注解元数据信息，判断注册过程是否需要跳过当前的bean，如果配置类上面未标注@Conditional注解，会返回false。不会跳过
		 *
		 * conditionEvaluator默认实现类为ConditionEvaluator
		 *
		 *
		 *
		 *
		 * abd.getMetadata获取到的MedataData类型为 StandardAnnotationMetaData
		*/
		// 如果当前配置类上没有标注@Conditional注解，返回false
		if (this.conditionEvaluator.shouldSkip(abd.getMetadata())) {
			return;
		}

		/***
		// 1.supplier 使用:
		Supplier<Integer> supplier = new Supplier<Integer>() {
			@Override
			public Integer get() {
				return new Random().nextInt();
			}
		};
		Integer result = supplier.get();

		// 2. lambda表达式形式
		supplier = () -> new Random().nextInt();
		result = supplier.get();
		*/

		// 给Bean定义中注册回调supplier回调方法
		abd.setInstanceSupplier(instanceSupplier);

		// 默认实现类为：AnnotationScopeMetadataResolver，解析@Scope注解的元信息
		ScopeMetadata scopeMetadata = this.scopeMetadataResolver.resolveScopeMetadata(abd);

		// 设置bean的作用域，未设置默认为'singleton'
		abd.setScope(scopeMetadata.getScopeName());

		// 获取Bean的名称。如果传入的name为null。则使用默认的Bean Name生成器AnnotationBeanNameGenerator生成bean的名称.根据内省操作获取的bean名称.
		String beanName = (name != null ? name : this.beanNameGenerator.generateBeanName(abd, this.registry));

		// 解析公共注解上面的元数据信息 例如：@DependsOn， @Lazy，@Primary，@DependsOn，@Description
		AnnotationConfigUtils.processCommonDefinitionAnnotations(abd);

		// 如果使用new AnnotationConfigApplicationContext(Class<?> clazz)创建的bean容器，
		//    则此处的qualifiers和下面的definitionCustomizers都默认为null
		if (qualifiers != null) {
			for (Class<? extends Annotation> qualifier : qualifiers) {
				if (Primary.class == qualifier) {
					abd.setPrimary(true);
				}
				else if (Lazy.class == qualifier) {
					abd.setLazyInit(true);
				}
				else {
					abd.addQualifier(new AutowireCandidateQualifier(qualifier));
				}
			}
		}
		for (BeanDefinitionCustomizer customizer : definitionCustomizers) {
			customizer.customize(abd);
		}

		// 用来保存Bean的定义。包括bean的名称，定义及别名信息
		BeanDefinitionHolder definitionHolder = new BeanDefinitionHolder(abd, beanName);

		// 设置bean定义的作用域模式，如果为ScopedProxyMode.NO，直接返回.
		definitionHolder = AnnotationConfigUtils.applyScopedProxyMode(scopeMetadata, definitionHolder, this.registry);

		// 真正完成配置类bean定义的注册。将bean定义注册到bean定义注册中心，即：BeanDefinitionRegistry中
		// this.registry即AnnotationConfigApplicationContext对象
		BeanDefinitionReaderUtils.registerBeanDefinition(definitionHolder, this.registry);
	}


	/**
	 * Get the Environment from the given registry if possible, otherwise return a new
	 * StandardEnvironment.
	 */
	private static Environment getOrCreateEnvironment(BeanDefinitionRegistry registry) {
		Assert.notNull(registry, "BeanDefinitionRegistry must not be null");
		// AnnotationConfigApplicationContext是EnvironmentCapable接口的实例，
		//   然后会调用AbstractApplicationContext的getEnvironment方法
		if (registry instanceof EnvironmentCapable) {
			return ((EnvironmentCapable) registry).getEnvironment();
		}
		return new StandardEnvironment();
	}

}
