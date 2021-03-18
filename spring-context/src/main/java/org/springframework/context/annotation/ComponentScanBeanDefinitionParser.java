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

import java.lang.annotation.Annotation;
import java.util.Set;
import java.util.regex.Pattern;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.parsing.BeanComponentDefinition;
import org.springframework.beans.factory.parsing.CompositeComponentDefinition;
import org.springframework.beans.factory.support.BeanNameGenerator;
import org.springframework.beans.factory.xml.BeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.beans.factory.xml.XmlReaderContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.core.type.filter.AspectJTypeFilter;
import org.springframework.core.type.filter.AssignableTypeFilter;
import org.springframework.core.type.filter.RegexPatternTypeFilter;
import org.springframework.core.type.filter.TypeFilter;
import org.springframework.lang.Nullable;
import org.springframework.util.ClassUtils;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;

/**
 * Parser for the {@code <context:component-scan/>} element.
 *
 * @author Mark Fisher
 * @author Ramnivas Laddad
 * @author Juergen Hoeller
 * @since 2.5
 */
public class ComponentScanBeanDefinitionParser implements BeanDefinitionParser {
	/**
	 * 扫描的包名
	 */
	private static final String BASE_PACKAGE_ATTRIBUTE = "base-package";
	/**
	 * 资源匹配规则
	 */
	private static final String RESOURCE_PATTERN_ATTRIBUTE = "resource-pattern";
	/**
	 * 是否使用默认的filter，用来保存被过滤掉的bean和被包含的bean
	 */
	private static final String USE_DEFAULT_FILTERS_ATTRIBUTE = "use-default-filters";
	/**
	 * 开启注解驱动，会给容器中导入默认的后置处理器
	 */
	private static final String ANNOTATION_CONFIG_ATTRIBUTE = "annotation-config";
	/**
	 * Bean名称生成器
	 */
	private static final String NAME_GENERATOR_ATTRIBUTE = "name-generator";
	/**
	 * @Scope 注解解析器
	 */
	private static final String SCOPE_RESOLVER_ATTRIBUTE = "scope-resolver";
	/**
	 * scope注解中的代理bean创建方式。主要包括INTERFACE和TARGET_CLASS
	 * INTERFACE: 基于接口的jdk动态代理
	 * TARGET_CLASS: 基于cglib的动态代理
	 */
	private static final String SCOPED_PROXY_ATTRIBUTE = "scoped-proxy";
	/**
	 * 保存不包含的bean
	 */
	private static final String EXCLUDE_FILTER_ELEMENT = "exclude-filter";
	/**
	 * 保存包含的bean
	 */
	private static final String INCLUDE_FILTER_ELEMENT = "include-filter";
	/**
	 * exclude和include的过滤规则类型，是按照注解还是正则
	 */
	private static final String FILTER_TYPE_ATTRIBUTE = "type";
	/**
	 * 过滤bean时的条件表达式
	 */
	private static final String FILTER_EXPRESSION_ATTRIBUTE = "expression";


	@Override
	@Nullable
	public BeanDefinition parse(Element element, ParserContext parserContext) {
		// 获取<context:component-scan base-package=""/> 中base-package的属性值
		String basePackage = element.getAttribute(BASE_PACKAGE_ATTRIBUTE);

		// 这个属性值可能是一个占位符，也可能是一个或者多个逗号隔开的包路径，所以调用resolvePlaceholders进行占位符的解析
		basePackage = parserContext.getReaderContext().getEnvironment().resolvePlaceholders(basePackage);

		// 解析完成之后，将其转为数组。base-package可以使用分隔符隔开指定多个包扫描路径(,; \t\n)
		String[] basePackages = StringUtils.tokenizeToStringArray(basePackage,
				ConfigurableApplicationContext.CONFIG_LOCATION_DELIMITERS);

		// 创建bean定义扫描器Scanner
		ClassPathBeanDefinitionScanner scanner = configureScanner(parserContext, element);

		/**
		 * 使用类路径扫描器扫描指定包下的bean定义，将扫描到的bean定义注册到BeanDefinitionRegistry中，
		 *  然后将其封装为一个BeanDefinitionHolder集合返回
		 */
		Set<BeanDefinitionHolder> beanDefinitions = scanner.doScan(basePackages);

		// 注册一些后置处理器组件，例如：ContextAnnotationAutowireCandidateResolver或者ConfigurationClassPostProcesor等
		//   和new AnnotationConfigApplicationContext(config.class) 中注册的组件相同。用来在某些时机解析一些带特定注解的bean
		registerComponents(parserContext.getReaderContext(), beanDefinitions, element);

		return null;
	}

	protected ClassPathBeanDefinitionScanner configureScanner(ParserContext parserContext, Element element) {
		boolean useDefaultFilters = true;

		// <component-scan use-default-filters=""/> 是否包含use-default-filters属性.
		// 如果是要排除某些注解，则该值用默认的true即可；如果是包含某些注解，即：include-filters，则use-default-filters的值应该设置为false
		if (element.hasAttribute(USE_DEFAULT_FILTERS_ATTRIBUTE)) {
			useDefaultFilters = Boolean.parseBoolean(element.getAttribute(USE_DEFAULT_FILTERS_ATTRIBUTE));
		}

		// Delegate bean definition registration to scanner class.
		// 创建一个ClassPathBeanDefinitionScanner类型的Bean定义扫描器
		ClassPathBeanDefinitionScanner scanner = createScanner(parserContext.getReaderContext(), useDefaultFilters);

		// 设置beanDefinitionDefaults，这个属性是一个BeanDefinitionDefaults类型的对象，用来保存bean的简单默认属性.
		scanner.setBeanDefinitionDefaults(parserContext.getDelegate().getBeanDefinitionDefaults());

		// 设置自动装配时需要被作为候选bean的匹配规则，对应的是<beans default-autowire-candidates=""/>中的default-autowire-candidates属性
		// 例如，default-autowire-candidates="student*"，则bean名称以student开头的bean才会被作为候选bean，其他不以student开头的将不会被自动装配
		scanner.setAutowireCandidatePatterns(parserContext.getDelegate().getAutowireCandidatePatterns());

		// 设置<context:component-scan result-pattern=""/>中result-pattern的属性值.
		if (element.hasAttribute(RESOURCE_PATTERN_ATTRIBUTE)) {
			scanner.setResourcePattern(element.getAttribute(RESOURCE_PATTERN_ATTRIBUTE));
		}

		// 解析bean名称的生成器，即：name-generator属性的值，需要指定bean名称生成器类的全限定名。
		//  这个类需要实现BeanNameGenerator接口.
		try {
			parseBeanNameGenerator(element, scanner);
		}
		catch (Exception ex) {
			parserContext.getReaderContext().error(ex.getMessage(), parserContext.extractSource(element), ex.getCause());
		}
		// 解析scope-resolver属性或者scoped-proxy属性
		try {
			parseScope(element, scanner);
		}
		catch (Exception ex) {
			parserContext.getReaderContext().error(ex.getMessage(), parserContext.extractSource(element), ex.getCause());
		}

		/**
		 * 解析
		 * <context:component-scan>
		 *     <context:exclude-filter />
		 *     <context:include-filter />
		 * </context:component-scan>
		 *
		 * 中的 exclude-filter 和 include-filter 属性的值，并将其转换为typeFilter过滤器，放入到扫描器中
		 */
		parseTypeFilters(element, scanner, parserContext);

		return scanner;
	}

	protected ClassPathBeanDefinitionScanner createScanner(XmlReaderContext readerContext, boolean useDefaultFilters) {
		// 创建类路径下的组件扫描
		return new ClassPathBeanDefinitionScanner(readerContext.getRegistry(), useDefaultFilters,
				readerContext.getEnvironment(), readerContext.getResourceLoader());
	}

	protected void registerComponents(
			XmlReaderContext readerContext, Set<BeanDefinitionHolder> beanDefinitions, Element element) {

		Object source = readerContext.extractSource(element);

		// 这是一个组件的集合，将某个包下扫描到的组件全部保存在这个集合中。包括了组件的名称，描述，bean定义，引用等.
		CompositeComponentDefinition compositeDef = new CompositeComponentDefinition(element.getTagName(), source);

		for (BeanDefinitionHolder beanDefHolder : beanDefinitions) {
			compositeDef.addNestedComponent(new BeanComponentDefinition(beanDefHolder));
		}

		// <context:component-scan annotation-config="true" />
		boolean annotationConfig = true;

		// 解析annotation-config属性的值，默认为true。即：默认就会注册注解驱动相关的后置处理器.
		if (element.hasAttribute(ANNOTATION_CONFIG_ATTRIBUTE)) {
			annotationConfig = Boolean.parseBoolean(element.getAttribute(ANNOTATION_CONFIG_ATTRIBUTE));
		}

		// 开启注解驱动，xml中添加配置：<context:annotation-config /> 即可支持.
		if (annotationConfig) {
			// 给容器中注册默认的Spring后置处理器，包括用来处理各种注解及解析配置类的后置处理器.
			Set<BeanDefinitionHolder> processorDefinitions =
					AnnotationConfigUtils.registerAnnotationConfigProcessors(readerContext.getRegistry(), source);
			for (BeanDefinitionHolder processorDefinition : processorDefinitions) {
				compositeDef.addNestedComponent(new BeanComponentDefinition(processorDefinition));
			}
		}
		// 触发组件注册事件
		readerContext.fireComponentRegistered(compositeDef);
	}

	protected void parseBeanNameGenerator(Element element, ClassPathBeanDefinitionScanner scanner) {
		if (element.hasAttribute(NAME_GENERATOR_ATTRIBUTE)) {
			// 创建bean名称生成器.
			BeanNameGenerator beanNameGenerator = (BeanNameGenerator) instantiateUserDefinedStrategy(
					element.getAttribute(NAME_GENERATOR_ATTRIBUTE), BeanNameGenerator.class,
					scanner.getResourceLoader().getClassLoader());
			scanner.setBeanNameGenerator(beanNameGenerator);
		}
	}

	protected void parseScope(Element element, ClassPathBeanDefinitionScanner scanner) {
		// 解析<context:component-scan /> 标签中的scope-resolver属性或者scope-proxy属性
		if (element.hasAttribute(SCOPE_RESOLVER_ATTRIBUTE)) {
			// scope-resolver和scope-proxy不能同时存在，否则直接抛异常.
			if (element.hasAttribute(SCOPED_PROXY_ATTRIBUTE)) {
				throw new IllegalArgumentException(
						"Cannot define both 'scope-resolver' and 'scoped-proxy' on <component-scan> tag");
			}
			// scope-resolver是用来解析bean作用域的自定义解析器，需要配置类的全限定名，而且这个类需要实现ScopeMetadataResolver接口.
			ScopeMetadataResolver scopeMetadataResolver = (ScopeMetadataResolver) instantiateUserDefinedStrategy(
					element.getAttribute(SCOPE_RESOLVER_ATTRIBUTE), ScopeMetadataResolver.class,
					scanner.getResourceLoader().getClassLoader());
			scanner.setScopeMetadataResolver(scopeMetadataResolver);
		}

		// 解析scope-proxy属性
		// 这个属性一个典型的用法就是在一个单例bean中创建session类型bean的场景。
		/**
		 * // 购物车对应的bean。这个bean不适合使用单例的，也不适合使用原型的，更不适合使用request的，最合适的就是session类型的
		 * class ShoppingCatBean {
		 *
		 * }
		 * // 用户对应的bean
		 * class UserService {
		 * 		@Autowired
		 * 		private ShoppingCatBean shoppingCatBean;
		 * }
		 *
		 * 如果在用户bean中注入一个购物车bean，用户bean生成的时候，可能还没有会话，这个时候，shoppingBean是没法完全生成的。
		 * 	所以只会生成一个和shoppingCatBean拥有相同方法的一个代理对象，并暴露出来，注入到UserService中。
		 *
		 * 当使用到购物车这个bean的时候，Spring会通过生成的代理对象去调用真实bean的方法。这个属性就是指定，生成这个代理对象时所使用的方式
		 */
		if (element.hasAttribute(SCOPED_PROXY_ATTRIBUTE)) {
			String mode = element.getAttribute(SCOPED_PROXY_ATTRIBUTE);
			if ("targetClass".equals(mode)) {
				// 使用cglib生成代理
				scanner.setScopedProxyMode(ScopedProxyMode.TARGET_CLASS);
			}
			else if ("interfaces".equals(mode)) {
				// 使用接口生成代理
				scanner.setScopedProxyMode(ScopedProxyMode.INTERFACES);
			}
			else if ("no".equals(mode)) {
				// 不生成代理。通常不会使用.
				scanner.setScopedProxyMode(ScopedProxyMode.NO);
			}
			else {
				throw new IllegalArgumentException("scoped-proxy only supports 'no', 'interfaces' and 'targetClass'");
			}
		}
	}

	protected void parseTypeFilters(Element element, ClassPathBeanDefinitionScanner scanner, ParserContext parserContext) {
		// Parse exclude and include filter elements.
		ClassLoader classLoader = scanner.getResourceLoader().getClassLoader();
		NodeList nodeList = element.getChildNodes();
		for (int i = 0; i < nodeList.getLength(); i++) {
			Node node = nodeList.item(i);
			// 判断节点是否需要解析。w3c dom4j
			if (node.getNodeType() == Node.ELEMENT_NODE) {
				// 获取节点名称
				String localName = parserContext.getDelegate().getLocalName(node);
				try {
					// include-filter属性
					if (INCLUDE_FILTER_ELEMENT.equals(localName)) {
						TypeFilter typeFilter = createTypeFilter((Element) node, classLoader, parserContext);
						scanner.addIncludeFilter(typeFilter);
					}
					// exclude-filter属性
					else if (EXCLUDE_FILTER_ELEMENT.equals(localName)) {
						TypeFilter typeFilter = createTypeFilter((Element) node, classLoader, parserContext);
						scanner.addExcludeFilter(typeFilter);
					}
				}
				catch (ClassNotFoundException ex) {
					parserContext.getReaderContext().warning(
							"Ignoring non-present type filter class: " + ex, parserContext.extractSource(element));
				}
				catch (Exception ex) {
					parserContext.getReaderContext().error(
							ex.getMessage(), parserContext.extractSource(element), ex.getCause());
				}
			}
		}
	}

	@SuppressWarnings("unchecked")
	protected TypeFilter createTypeFilter(Element element, @Nullable ClassLoader classLoader,
			ParserContext parserContext) throws ClassNotFoundException {

		// 获取过滤规则的类型，annotation, assignable, aspectj, regex, custom
		String filterType = element.getAttribute(FILTER_TYPE_ATTRIBUTE);

		// 获取过滤规则表达式
		String expression = element.getAttribute(FILTER_EXPRESSION_ATTRIBUTE);

		// 过滤规则如果使用的是占位符的方式，则解析占位符，得到真实的过滤规则表达式.
		expression = parserContext.getReaderContext().getEnvironment().resolvePlaceholders(expression);

		if ("annotation".equals(filterType)) {
			return new AnnotationTypeFilter((Class<Annotation>) ClassUtils.forName(expression, classLoader));
		}
		else if ("assignable".equals(filterType)) {
			return new AssignableTypeFilter(ClassUtils.forName(expression, classLoader));
		}
		else if ("aspectj".equals(filterType)) {
			return new AspectJTypeFilter(expression, classLoader);
		}
		// 正则匹配
		else if ("regex".equals(filterType)) {
			return new RegexPatternTypeFilter(Pattern.compile(expression));
		}
		else if ("custom".equals(filterType)) {
			Class<?> filterClass = ClassUtils.forName(expression, classLoader);
			if (!TypeFilter.class.isAssignableFrom(filterClass)) {
				throw new IllegalArgumentException(
						"Class is not assignable to [" + TypeFilter.class.getName() + "]: " + expression);
			}
			return (TypeFilter) BeanUtils.instantiateClass(filterClass);
		}
		else {
			throw new IllegalArgumentException("Unsupported filter type: " + filterType);
		}
	}

	@SuppressWarnings("unchecked")
	private Object instantiateUserDefinedStrategy(
			String className, Class<?> strategyType, @Nullable ClassLoader classLoader) {

		Object result;
		try {
			// 通过构造器反射创建实例
			result = ReflectionUtils.accessibleConstructor(ClassUtils.forName(className, classLoader)).newInstance();
		}
		catch (ClassNotFoundException ex) {
			throw new IllegalArgumentException("Class [" + className + "] for strategy [" +
					strategyType.getName() + "] not found", ex);
		}
		catch (Throwable ex) {
			throw new IllegalArgumentException("Unable to instantiate class [" + className + "] for strategy [" +
					strategyType.getName() + "]: a zero-argument constructor is required", ex);
		}

		if (!strategyType.isAssignableFrom(result.getClass())) {
			throw new IllegalArgumentException("Provided class name must be an implementation of " + strategyType);
		}
		return result;
	}

}
