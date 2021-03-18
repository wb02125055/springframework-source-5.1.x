/*
 * Copyright 2002-2017 the original author or authors.
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

import org.springframework.beans.factory.BeanDefinitionStoreException;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.lang.Nullable;

/**
 * Simple interface for bean definition readers.
 * Specifies load methods with Resource and String location parameters.
 *
 * <p>Concrete bean definition readers can of course add additional
 * load and register methods for bean definitions, specific to
 * their bean definition format.
 *
 * <p>Note that a bean definition reader does not have to implement
 * this interface. It only serves as suggestion for bean definition
 * readers that want to follow standard naming conventions.
 *
 * @author Juergen Hoeller
 * @since 1.1
 * @see org.springframework.core.io.Resource
 *
 * 用来进行Bean的加载
 *
 * 有一个抽象实现类，AbstractBeanDefinitionReader，里面实现了一些简单的方法，比较核心的复杂方法是通过子类
 * PropertiesBeanDefinitionReader，GroovyBeanDefinitionReader，XmlBeanDefinitionReader进行实现.
 */
public interface BeanDefinitionReader {

	/**
	 * Return the bean factory to register the bean definitions with.
	 * <p>The factory is exposed through the BeanDefinitionRegistry interface,
	 * encapsulating the methods that are relevant for bean definition handling.
	 *
	 * 得到Bean定义的register
	 */
	BeanDefinitionRegistry getRegistry();

	/**
	 * Return the resource loader to use for resource locations.
	 * Can be checked for the <b>ResourcePatternResolver</b> interface and cast
	 * accordingly, for loading multiple resources for a given resource pattern.
	 * <p>A {@code null} return value suggests that absolute resource loading
	 * is not available for this bean definition reader.
	 * <p>This is mainly meant to be used for importing further resources
	 * from within a bean definition resource, for example via the "import"
	 * tag in XML bean definitions. It is recommended, however, to apply
	 * such imports relative to the defining resource; only explicit full
	 * resource locations will trigger absolute resource loading.
	 * <p>There is also a {@code loadBeanDefinitions(String)} method available,
	 * for loading bean definitions from a resource location (or location pattern).
	 * This is a convenience to avoid explicit ResourceLoader handling.
	 * @see #loadBeanDefinitions(String)
	 * @see org.springframework.core.io.support.ResourcePatternResolver
	 *
	 * 获取用于加载资源的 ResourceLoader
	 */
	@Nullable
	ResourceLoader getResourceLoader();

	/**
	 * Return the class loader to use for bean classes.
	 * <p>{@code null} suggests to not load bean classes eagerly
	 * but rather to just register bean definitions with class names,
	 * with the corresponding Classes to be resolved later (or never).
	 *
	 * 获取Bean的类加载器
	 */
	@Nullable
	ClassLoader getBeanClassLoader();

	/**
	 * Return the BeanNameGenerator to use for anonymous beans
	 * (without explicit bean name specified).
	 *
	 * 获取Bean名称生成器，如果没有指定bean名称，就会调用该方法生成
	 */
	BeanNameGenerator getBeanNameGenerator();


	/**
	 * Load bean definitions from the specified resource.
	 * @param resource the resource descriptor
	 * @return the number of bean definitions found
	 * @throws BeanDefinitionStoreException in case of loading or parsing errors
	 *
	 * 根据一个资源描述加载bean定义，并返回加载到的bean定义的个数
	 */
	int loadBeanDefinitions(Resource resource) throws BeanDefinitionStoreException;

	/**
	 * Load bean definitions from the specified resources.
	 * @param resources the resource descriptors
	 * @return the number of bean definitions found
	 * @throws BeanDefinitionStoreException in case of loading or parsing errors
	 *
	 * 根据多个资源描述加载bean定义，并返回加载到的bean定义的个数
	 */
	int loadBeanDefinitions(Resource... resources) throws BeanDefinitionStoreException;

	/**
	 * Load bean definitions from the specified resource location.
	 * <p>The location can also be a location pattern, provided that the
	 * ResourceLoader of this bean definition reader is a ResourcePatternResolver.
	 * @param location the resource location, to be loaded with the ResourceLoader
	 * (or ResourcePatternResolver) of this bean definition reader
	 * @return the number of bean definitions found
	 * @throws BeanDefinitionStoreException in case of loading or parsing errors
	 * @see #getResourceLoader()
	 * @see #loadBeanDefinitions(org.springframework.core.io.Resource)
	 * @see #loadBeanDefinitions(org.springframework.core.io.Resource[])
	 *
	 * 根据某个配置文件的位置加载并解析封装bean定义。返回加载到的bean定义的数量
	 */
	int loadBeanDefinitions(String location) throws BeanDefinitionStoreException;

	/**
	 * Load bean definitions from the specified resource locations.
	 * @param locations the resource locations, to be loaded with the ResourceLoader
	 * (or ResourcePatternResolver) of this bean definition reader
	 * @return the number of bean definitions found
	 * @throws BeanDefinitionStoreException in case of loading or parsing errors
	 *
	 * 根据多个配置文件进行bean定义的解析，并封装为bean定义。返回加载到的bean定义的数量
	 */
	int loadBeanDefinitions(String... locations) throws BeanDefinitionStoreException;

}
