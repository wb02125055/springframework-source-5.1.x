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

import org.springframework.beans.factory.BeanDefinitionStoreException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.lang.Nullable;
import org.springframework.util.StringValueResolver;

/**
 * Abstract base class for property resource configurers that resolve placeholders
 * in bean definition property values. Implementations <em>pull</em> values from a
 * properties file or other {@linkplain org.springframework.core.env.PropertySource
 * property source} into bean definitions.
 *
 * <p>The default placeholder syntax follows the Ant / Log4J / JSP EL style:
 *
 * <pre class="code">${...}</pre>
 *
 * Example XML bean definition:
 *
 * <pre class="code">
 * &lt;bean id="dataSource" class="org.springframework.jdbc.datasource.DriverManagerDataSource"/&gt;
 *   &lt;property name="driverClassName" value="${driver}"/&gt;
 *   &lt;property name="url" value="jdbc:${dbname}"/&gt;
 * &lt;/bean&gt;
 * </pre>
 *
 * Example properties file:
 *
 * <pre class="code">driver=com.mysql.jdbc.Driver
 * dbname=mysql:mydb</pre>
 *
 * Annotated bean definitions may take advantage of property replacement using
 * the {@link org.springframework.beans.factory.annotation.Value @Value} annotation:
 *
 * <pre class="code">@Value("${person.age}")</pre>
 *
 * Implementations check simple property values, lists, maps, props, and bean names
 * in bean references. Furthermore, placeholder values can also cross-reference
 * other placeholders, like:
 *
 * <pre class="code">rootPath=myrootdir
 * subPath=${rootPath}/subdir</pre>
 *
 * In contrast to {@link PropertyOverrideConfigurer}, subclasses of this type allow
 * filling in of explicit placeholders in bean definitions.
 *
 * <p>If a configurer cannot resolve a placeholder, a {@link BeanDefinitionStoreException}
 * will be thrown. If you want to check against multiple properties files, specify multiple
 * resources via the {@link #setLocations locations} property. You can also define multiple
 * configurers, each with its <em>own</em> placeholder syntax. Use {@link
 * #ignoreUnresolvablePlaceholders} to intentionally suppress throwing an exception if a
 * placeholder cannot be resolved.
 *
 * <p>Default property values can be defined globally for each configurer instance
 * via the {@link #setProperties properties} property, or on a property-by-property basis
 * using the default value separator which is {@code ":"} by default and
 * customizable via {@link #setValueSeparator(String)}.
 *
 * <p>Example XML property with default value:
 *
 * <pre class="code">
 *   <property name="url" value="jdbc:${dbname:defaultdb}"/>
 * </pre>
 *
 * @author Chris Beams
 * @author Juergen Hoeller
 * @since 3.1
 * @see ;
 * @see org.springframework.context.support.PropertySourcesPlaceholderConfigurer
 *
 * 用来处理xml中的占位符.
 */
public abstract class PlaceholderConfigurerSupport extends PropertyResourceConfigurer
		implements BeanNameAware, BeanFactoryAware {

	/**
	 * 默认的占位符前缀
	 */
	public static final String DEFAULT_PLACEHOLDER_PREFIX = "${";

	/**
	 * 默认的占位符后缀
	 */
	public static final String DEFAULT_PLACEHOLDER_SUFFIX = "}";

	/**
	 * 默认的占位符分隔符号
	 */
	public static final String DEFAULT_VALUE_SEPARATOR = ":";


	/** Defaults to {@value #DEFAULT_PLACEHOLDER_PREFIX}. */
	protected String placeholderPrefix = DEFAULT_PLACEHOLDER_PREFIX;

	/** Defaults to {@value #DEFAULT_PLACEHOLDER_SUFFIX}. */
	protected String placeholderSuffix = DEFAULT_PLACEHOLDER_SUFFIX;

	/** Defaults to {@value #DEFAULT_VALUE_SEPARATOR}. */
	@Nullable
	protected String valueSeparator = DEFAULT_VALUE_SEPARATOR;

	protected boolean trimValues = false;

	@Nullable
	protected String nullValue;

	protected boolean ignoreUnresolvablePlaceholders = false;

	@Nullable
	private String beanName;

	@Nullable
	private BeanFactory beanFactory;


	/**
	 * Set the prefix that a placeholder string starts with.
	 * The default is {@value #DEFAULT_PLACEHOLDER_PREFIX}.
	 */
	public void setPlaceholderPrefix(String placeholderPrefix) {
		this.placeholderPrefix = placeholderPrefix;
	}

	/**
	 * Set the suffix that a placeholder string ends with.
	 * The default is {@value #DEFAULT_PLACEHOLDER_SUFFIX}.
	 */
	public void setPlaceholderSuffix(String placeholderSuffix) {
		this.placeholderSuffix = placeholderSuffix;
	}

	/**
	 * Specify the separating character between the placeholder variable
	 * and the associated default value, or {@code null} if no such
	 * special character should be processed as a value separator.
	 * The default is {@value #DEFAULT_VALUE_SEPARATOR}.
	 */
	public void setValueSeparator(@Nullable String valueSeparator) {
		this.valueSeparator = valueSeparator;
	}

	/**
	 * Specify whether to trim resolved values before applying them,
	 * removing superfluous whitespace from the beginning and end.
	 * <p>Default is {@code false}.
	 * @since 4.3
	 */
	public void setTrimValues(boolean trimValues) {
		this.trimValues = trimValues;
	}

	/**
	 * Set a value that should be treated as {@code null} when resolved
	 * as a placeholder value: e.g. "" (empty String) or "null".
	 * <p>Note that this will only apply to full property values,
	 * not to parts of concatenated values.
	 * <p>By default, no such null value is defined. This means that
	 * there is no way to express {@code null} as a property value
	 * unless you explicitly map a corresponding value here.
	 */
	public void setNullValue(String nullValue) {
		this.nullValue = nullValue;
	}

	/**
	 * Set whether to ignore unresolvable placeholders.
	 * <p>Default is "false": An exception will be thrown if a placeholder fails
	 * to resolve. Switch this flag to "true" in order to preserve the placeholder
	 * String as-is in such a case, leaving it up to other placeholder configurers
	 * to resolve it.
	 */
	public void setIgnoreUnresolvablePlaceholders(boolean ignoreUnresolvablePlaceholders) {
		this.ignoreUnresolvablePlaceholders = ignoreUnresolvablePlaceholders;
	}

	/**
	 * Only necessary to check that we're not parsing our own bean definition,
	 * to avoid failing on unresolvable placeholders in properties file locations.
	 * The latter case can happen with placeholders for system properties in
	 * resource locations.
	 * @see #setLocations
	 * @see org.springframework.core.io.ResourceEditor
	 */
	@Override
	public void setBeanName(String beanName) {
		this.beanName = beanName;
	}

	/**
	 * Only necessary to check that we're not parsing our own bean definition,
	 * to avoid failing on unresolvable placeholders in properties file locations.
	 * The latter case can happen with placeholders for system properties in
	 * resource locations.
	 * @see #setLocations
	 * @see org.springframework.core.io.ResourceEditor
	 */
	@Override
	public void setBeanFactory(BeanFactory beanFactory) {
		this.beanFactory = beanFactory;
	}


	protected void doProcessProperties(ConfigurableListableBeanFactory beanFactoryToProcess,
			StringValueResolver valueResolver) {
		// valueResolver默认类型为：PlaceholderResolvingStringValueResolver
		BeanDefinitionVisitor visitor = new BeanDefinitionVisitor(valueResolver);
		// 从bean工厂中获取所有的bean定义的名称
		String[] beanNames = beanFactoryToProcess.getBeanDefinitionNames();
		for (String curName : beanNames) {
			// to avoid failing on unresolvable placeholders in properties file locations.
			/**
			 * 下面的判断等价于：if (!curName.equals(this.beanName) || !beanFactoryToProcess.equals(this.beanFactory))
			 *    表示：bean名称或者bean工厂只要有一个不相同，就执行解析操作.
			 *
			 * 执行到该步骤之后，由于PlaceholderConfigurerSupport实现了BeanNameAware接口，所以此处的beanName为PropertyPlaceholderConfigurer#0
			 * 需要判断当前从bean工厂中拿到的beanName和当前bean的名称是否相同，如果相同，则代表是一个占位符解析配置类，不需要进行解析，直接跳过。
			 * 如果是自定义的bean，此处会拿到自定义bean的名称，例如：user，然后去解析user这个bean中的属性占位符。从而避免了去解析非自定义的bean中
			 * 的属性占位符而导致解析失败。
			 */
			if (!(curName.equals(this.beanName) && beanFactoryToProcess.equals(this.beanFactory))) {
				/**
				 * 此时获取到的Bean定义中，如果存在着${user.name}占位符的属性，
				 *  则会以map格式保存在bean定义中，格式为: <name:${user.name}>，key为属性名称，value为原样占位符，
				 *  在下一步visitor.visitBeanDefinition中会解析占位符所表示的属性值
				 */
				BeanDefinition bd = beanFactoryToProcess.getBeanDefinition(curName);
				try {
					/** 解析父级bean，工厂bean，作用域，工厂方法等bean中的变量值 */
					visitor.visitBeanDefinition(bd);
				}
				catch (Exception ex) {
					throw new BeanDefinitionStoreException(bd.getResourceDescription(), curName, ex.getMessage(), ex);
				}
			}
		}

		// New in Spring 2.5: resolve placeholders in alias target names and aliases as well.
		// 使用当前的值解析器解析别名中占位符的值
		beanFactoryToProcess.resolveAliases(valueResolver);

		// New in Spring 3.0: resolve placeholders in embedded values such as annotation attributes.
		// 使用当前的值解析器去解析注解中占位符的值
		beanFactoryToProcess.addEmbeddedValueResolver(valueResolver);
	}

}
