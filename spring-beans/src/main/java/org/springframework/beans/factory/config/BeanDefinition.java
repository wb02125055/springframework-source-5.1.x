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

package org.springframework.beans.factory.config;

import org.springframework.beans.BeanMetadataElement;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.core.AttributeAccessor;
import org.springframework.lang.Nullable;

/**
 * A BeanDefinition describes a bean instance, which has property values,
 * constructor argument values, and further information supplied by
 * concrete implementations.
 *
 * <p>This is just a minimal interface: The main intention is to allow a
 * {@link BeanFactoryPostProcessor} such as {@link PropertyPlaceholderConfigurer}
 * to introspect and modify property values and other bean metadata.
 *
 * @author Juergen Hoeller
 * @author Rob Harrop
 * @since 19.03.2004
 * @see ConfigurableListableBeanFactory#getBeanDefinition
 * @see org.springframework.beans.factory.support.RootBeanDefinition
 * @see org.springframework.beans.factory.support.ChildBeanDefinition
 */
public interface BeanDefinition extends AttributeAccessor, BeanMetadataElement {

	/**
	 * Bean的作用域标识，singleton
	 */
	String SCOPE_SINGLETON = ConfigurableBeanFactory.SCOPE_SINGLETON;

	/**
	 * Bean的作用域标识，prototype
	 */
	String SCOPE_PROTOTYPE = ConfigurableBeanFactory.SCOPE_PROTOTYPE;


	/**
	 * 用户自定义的bean。例如<bean class=""/>定义的bean或者使用@Bean标注的bean
	 *
	 * Role hint indicating that a {@code BeanDefinition} is a major part
	 * of the application. Typically corresponds to a user-defined bean.
	 */
	int ROLE_APPLICATION = 0;

	/**
	 * 用来支持复杂和比较大的bean定义
	 *
	 * Role hint indicating that a {@code BeanDefinition} is a supporting
	 * part of some larger configuration, typically an outer
	 * {@link org.springframework.beans.factory.parsing.ComponentDefinition}.
	 * {@code SUPPORT} beans are considered important enough to be aware
	 * of when looking more closely at a particular
	 * {@link org.springframework.beans.factory.parsing.ComponentDefinition},
	 * but not when looking at the overall configuration of an application.
	 */
	int ROLE_SUPPORT = 1;

	/**
	 * 和用户不相关的bean定义，表示spring内部使用的bean
	 * Role hint indicating that a {@code BeanDefinition} is providing an
	 * entirely background role and has no relevance to the end-user. This hint is
	 * used when registering beans that are completely part of the internal workings
	 * of a {@link org.springframework.beans.factory.parsing.ComponentDefinition}.
	 */
	int ROLE_INFRASTRUCTURE = 2;


	// Modifiable attributes

	/**
	 * Set the name of the parent definition of this bean definition, if any.
	 * 设置当前bean的父级bean名称
	 */
	void setParentName(@Nullable String parentName);

	/**
	 * Return the name of the parent definition of this bean definition, if any.
	 * 获取当前bean定义的父级bean定义名称
	 */
	@Nullable
	String getParentName();

	/**
	 * Specify the bean class name of this bean definition.
	 * <p>The class name can be modified during bean factory post-processing,
	 * typically replacing the original class name with a parsed variant of it.
	 * @see #setParentName
	 * @see #setFactoryBeanName
	 * @see #setFactoryMethodName
	 *
	 * 设置bean的类型
	 */
	void setBeanClassName(@Nullable String beanClassName);

	/**
	 * Return the current bean class name of this bean definition.
	 * <p>Note that this does not have to be the actual class name used at runtime, in
	 * case of a child definition overriding/inheriting the class name from its parent.
	 * Also, this may just be the class that a factory method is called on, or it may
	 * even be empty in case of a factory bean reference that a method is called on.
	 * Hence, do <i>not</i> consider this to be the definitive bean type at runtime but
	 * rather only use it for parsing purposes at the individual bean definition level.
	 * @see #getParentName()
	 * @see #getFactoryBeanName()
	 * @see #getFactoryMethodName()
	 *
	 * 获取bean的类型
	 */
	@Nullable
	String getBeanClassName();

	/**
	 * Override the target scope of this bean, specifying a new scope name.
	 * @see #SCOPE_SINGLETON
	 * @see #SCOPE_PROTOTYPE
	 *
	 * 设置bean的作用域
	 */
	void setScope(@Nullable String scope);

	/**
	 * 获取bean的作用域
	 */
	@Nullable
	String getScope();

	/**
	 * 设置bean是否使用使用懒加载
	 */
	void setLazyInit(boolean lazyInit);

	/**
	 * 获取当前bean是否为懒加载
	 */
	boolean isLazyInit();

	/**
	 * 设置当前bean依赖的其他bean的名称
	 * Spring在初始化当前bean时，就会首先去初始化其他bean
	 */
	void setDependsOn(@Nullable String... dependsOn);

	/**
	 * Return the bean names that this bean depends on.
	 * 获取当前bean依赖的其他bean的名称.
	 */
	@Nullable
	String[] getDependsOn();

	/**
	 * Set whether this bean is a candidate for getting autowired into some other bean.
	 * <p>Note that this flag is designed to only affect type-based autowiring.
	 * It does not affect explicit references by name, which will get resolved even
	 * if the specified bean is not marked as an autowire candidate. As a consequence,
	 * autowiring by name will nevertheless inject a bean if the name matches.
	 *
	 * 设置当前bean是否可以使用get方法作为一个候选bean装配到其他bean
	 */
	void setAutowireCandidate(boolean autowireCandidate);

	/**
	 * Return whether this bean is a candidate for getting autowired into some other bean.
	 *
	 * 返回当前bean是否为一个可以通过get方法作为注入其他bean中的候选bean
	 */
	boolean isAutowireCandidate();


	/**
	 * Set whether this bean is a primary autowire candidate.
	 * <p>If this value is {@code true} for exactly one bean among multiple
	 * matching candidates, it will serve as a tie-breaker.
	 *
	 * 设置当前bean是否为优先装配的bean。
	 * 当容器中存在着多个与目标类型匹配的bean时，使用primary可以设置当前bean为优先被装配的bean
	 */
	void setPrimary(boolean primary);

	/**
	 * Return whether this bean is a primary autowire candidate.
	 * 获取当前bean是否为一个可以被优先装配的bean
	 */
	boolean isPrimary();

	/**
	 * Specify the factory bean to use, if any.
	 * This the name of the bean to call the specified factory method on.
	 * @see #setFactoryMethodName
	 *
	 * 设置当前bean的工厂bean的名称
	 */
	void setFactoryBeanName(@Nullable String factoryBeanName);

	/**
	 * Return the factory bean name, if any.
	 *
	 * 获取当前bean的工厂bean的名称
	 */
	@Nullable
	String getFactoryBeanName();

	/**
	 * 获取当前bean的工厂方法的名称
	 */
	void setFactoryMethodName(@Nullable String factoryMethodName);

	/**
	 * Return a factory method, if any
	 *
	 * 获取当前bean的工厂方法的名称
	 */
	@Nullable
	String getFactoryMethodName();

	/**
	 * Return the constructor argument values for this bean.
	 * <p>The returned instance can be modified during bean factory post-processing.
	 * @return the ConstructorArgumentValues object (never {@code null})
	 *
	 * 获取当前bean的构造器参数对应的值
	 */
	ConstructorArgumentValues getConstructorArgumentValues();

	/**
	 * Return if there are constructor argument values defined for this bean.
	 * @since 5.0.2
	 *
	 * 判断当前bean中是否定义了构造器的参数值
	 */
	default boolean hasConstructorArgumentValues() {
		return !getConstructorArgumentValues().isEmpty();
	}

	/**
	 * Return the property values to be applied to a new instance of the bean.
	 * <p>The returned instance can be modified during bean factory post-processing.
	 * @return the MutablePropertyValues object (never {@code null})
	 *
	 * 获取当前bean的属性值， spring在获取bean实例时，需要将配置的属性值解析到PropertyValues中.
	 */
	MutablePropertyValues getPropertyValues();

	/**
	 * Return if there are property values values defined for this bean.
	 * @since 5.0.2
	 *
	 * 判断当前bean中是否包含了预先配置的属性值
	 */
	default boolean hasPropertyValues() {
		return !getPropertyValues().isEmpty();
	}

	/**
	 * Set the name of the initializer method.
	 * @since 5.1
	 *
	 * 设置bean的初始化方法
	 */
	void setInitMethodName(@Nullable String initMethodName);

	/**
	 * Return the name of the initializer method.
	 * @since 5.1
	 *
	 * 获取当前bean的初始化方法
	 */
	@Nullable
	String getInitMethodName();

	/**
	 * Set the name of the destroy method.
	 * @since 5.1
	 *
	 * 设置当前bean的销毁方法
	 */
	void setDestroyMethodName(@Nullable String destroyMethodName);

	/**
	 * Return the name of the destroy method.
	 * @since 5.1
	 *
	 * 获取当前bean的销毁方法
	 */
	@Nullable
	String getDestroyMethodName();

	/**
	 * Set the role hint for this {@code BeanDefinition}. The role hint
	 * provides the frameworks as well as tools with an indication of
	 * the role and importance of a particular {@code BeanDefinition}.
	 * @since 5.1
	 * @see #ROLE_APPLICATION
	 * @see #ROLE_SUPPORT
	 * @see #ROLE_INFRASTRUCTURE
	 *
	 * TODO 设置bean的角色？哪块会用到？
	 */
	void setRole(int role);

	/**
	 * Get the role hint for this {@code BeanDefinition}. The role hint
	 * provides the frameworks as well as tools with an indication of
	 * the role and importance of a particular {@code BeanDefinition}.
	 * @see #ROLE_APPLICATION
	 * @see #ROLE_SUPPORT
	 * @see #ROLE_INFRASTRUCTURE
	 *
	 * TODO 获取bean的角色？哪块会用到？
	 */
	int getRole();

	/**
	 * Set a human-readable description of this bean definition.
	 * @since 5.1
	 *
	 * 设置bean的描述信息
	 */
	void setDescription(@Nullable String description);

	/**
	 * Return a human-readable description of this bean definition.
	 *
	 * 获取bean的描述信息
	 */
	@Nullable
	String getDescription();


	// Read-only attributes

	/**
	 * Return whether this a <b>Singleton</b>, with a single, shared instance
	 * returned on all calls.
	 * @see #SCOPE_SINGLETON
	 *
	 * 获取bean是否为单例
	 */
	boolean isSingleton();

	/**
	 * Return whether this a <b>Prototype</b>, with an independent instance
	 * returned for each call.
	 * @since 3.0
	 * @see #SCOPE_PROTOTYPE
	 *
	 * 获取bean是否为多例
	 */
	boolean isPrototype();

	/**
	 * Return whether this bean is "abstract", that is, not meant to be instantiated.
	 *
	 * 获取bean是否为抽象的. 如果为抽象的，那么就不是需要被初始化的
	 */
	boolean isAbstract();

	/**
	 * Return a description of the resource that this bean definition
	 * came from (for the purpose of showing context in case of errors).
	 *
	 * 获取当前bean的资源描述，用于在出现错误时上下文信息.
	 */
	@Nullable
	String getResourceDescription();

	/**
	 * Return the originating BeanDefinition, or {@code null} if none.
	 * Allows for retrieving the decorated bean definition, if any.
	 * <p>Note that this method returns the immediate originator. Iterate through the
	 * originator chain to find the original BeanDefinition as defined by the user.
	 *
	 * 获取当前bean对应的原始的bean定义.
	 */
	@Nullable
	BeanDefinition getOriginatingBeanDefinition();

}
