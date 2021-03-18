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

package org.springframework.context.annotation;

import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanNameGenerator;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;

import java.beans.Introspector;
import java.util.Map;
import java.util.Set;

/**
 * {@link org.springframework.beans.factory.support.BeanNameGenerator}
 * implementation for bean classes annotated with the
 * {@link org.springframework.stereotype.Component @Component} annotation
 * or with another annotation that is itself annotated with
 * {@link org.springframework.stereotype.Component @Component} as a
 * meta-annotation. For example, Spring's stereotype annotations (such as
 * {@link org.springframework.stereotype.Repository @Repository}) are
 * themselves annotated with
 * {@link org.springframework.stereotype.Component @Component}.
 *
 * <p>Also supports Java EE 6's {@link javax.annotation.ManagedBean} and
 * JSR-330's {@link javax.inject.Named} annotations, if available. Note that
 * Spring component annotations always override such standard annotations.
 *
 * <p>If the annotation's value doesn't indicate a bean name, an appropriate
 * name will be built based on the short name of the class (with the first
 * letter lower-cased). For example:
 *
 * <pre class="code">com.xyz.FooServiceImpl -&gt; fooServiceImpl</pre>
 *
 * @author Juergen Hoeller
 * @author Mark Fisher
 * @since 2.5
 * @see org.springframework.stereotype.Component#value()
 * @see org.springframework.stereotype.Repository#value()
 * @see org.springframework.stereotype.Service#value()
 * @see org.springframework.stereotype.Controller#value()
 * @see javax.inject.Named#value()
 */
public class AnnotationBeanNameGenerator implements BeanNameGenerator {

	/**
	 * 标注组件的类全限定名
	 */
	private static final String COMPONENT_ANNOTATION_CLASSNAME = "org.springframework.stereotype.Component";


	@Override
	public String generateBeanName(BeanDefinition definition, BeanDefinitionRegistry registry) {
		if (definition instanceof AnnotatedBeanDefinition) {
			// 如果是通过@Component/@Service/@Repository/@Controller注解标注的组件，而且指定了bean的名称，则直接使用指定的名称
			String beanName = determineBeanNameFromAnnotation((AnnotatedBeanDefinition) definition);
			if (StringUtils.hasText(beanName)) {
				// Explicit bean name found.
				return beanName;
			}
		}
		// 如果是其他注解标注的bean，则生成默认的bean的名称，通常为类的简单名称首字母小写，即：Introspector.decapitalize(class.getSimpleName())
		return buildDefaultBeanName(definition, registry);
	}

	/**
	 * Derive a bean name from one of the annotations on the class.
	 * @param annotatedDef the annotation-aware bean definition
	 * @return the bean name, or {@code null} if none is found
	 */
	@Nullable
	protected String determineBeanNameFromAnnotation(AnnotatedBeanDefinition annotatedDef) {
		AnnotationMetadata amd = annotatedDef.getMetadata();
		// 获取配置类上标注的所有注解
		Set<String> types = amd.getAnnotationTypes();
		String beanName = null;
		for (String type : types) {
			// 解析出注解中对应的属性及名称.
			AnnotationAttributes attributes = AnnotationConfigUtils.attributesFor(amd, type);
			// 如果是bean组件是通过@Component/@Service/@Repository/@Controller注解标注的，则bean的名称会使用@Component("componentName")中的名称，即：componentName
			if (attributes != null &&
					isStereotypeWithNameValue(type, amd.getMetaAnnotationTypes(type), attributes)) {
				Object value = attributes.get("value");
				if (value instanceof String) {
					String strVal = (String) value;
					if (StringUtils.hasLength(strVal)) {
						if (beanName != null && !strVal.equals(beanName)) {
							throw new IllegalStateException("Stereotype annotations suggest inconsistent " +
									"component names: '" + beanName + "' versus '" + strVal + "'");
						}
						beanName = strVal;
					}
				}
			}
		}
		return beanName;
	}

	/**
	 * Check whether the given annotation is a stereotype that is allowed
	 * to suggest a component name through its annotation {@code value()}.
	 * @param annotationType the name of the annotation class to check
	 * @param metaAnnotationTypes the names of meta-annotations on the given annotation
	 * @param attributes the map of attributes for the given annotation
	 * @return whether the annotation qualifies as a stereotype with component name
	 *
	 * 注解的全限定名是否为 "org.springframework.stereotype.Component"
	 */
	protected boolean isStereotypeWithNameValue(String annotationType,
			Set<String> metaAnnotationTypes, @Nullable Map<String, Object> attributes) {

		boolean isStereotype = annotationType.equals(COMPONENT_ANNOTATION_CLASSNAME) ||
				metaAnnotationTypes.contains(COMPONENT_ANNOTATION_CLASSNAME) ||
				annotationType.equals("javax.annotation.ManagedBean") ||
				annotationType.equals("javax.inject.Named");

		return (isStereotype && attributes != null && attributes.containsKey("value"));
	}

	/**
	 * Derive a default bean name from the given bean definition.
	 * <p>The default implementation delegates to {@link #buildDefaultBeanName(BeanDefinition)}.
	 * @param definition the bean definition to build a bean name for
	 * @param registry the registry that the given bean definition is being registered with
	 * @return the default bean name (never {@code null})
	 */
	protected String buildDefaultBeanName(BeanDefinition definition, BeanDefinitionRegistry registry) {
		return buildDefaultBeanName(definition);
	}

	/**
	 * Derive a default bean name from the given bean definition.
	 * <p>The default implementation simply builds a decapitalized version
	 * of the short class name: e.g. "mypackage.MyJdbcDao" -> "myJdbcDao".
	 * <p>Note that inner classes will thus have names of the form
	 * "outerClassName.InnerClassName", which because of the period in the
	 * name may be an issue if you are autowiring by name.
	 * @param definition the bean definition to build a bean name for
	 * @return the default bean name (never {@code null})
	 */
	protected String buildDefaultBeanName(BeanDefinition definition) {
		// bean定义对应的class名称.
		String beanClassName = definition.getBeanClassName();
		Assert.state(beanClassName != null, "No bean class name set");

		// beanClassName: com.wb.spring.async.config.AsyncConfig  shortClassName: asyncConfig
		String shortClassName = ClassUtils.getShortName(beanClassName);

		// 使用JDK的内省机制，格式化资源名称。格式化的规则：
		/**
		 * (1) 如果字符串为null或者长度为0，直接返回原字符串
		 * (2) 如果字符串的第一个字符和第二个字符都为大写，直接返回原字符串
		 * (3) 否则将第一个字符改为小写，然后返回.
		 *
		 * MyJDBC ---> myJDBC
		 * SEtName ---> SEtName
		 * setName ---> setName
		 */
		return Introspector.decapitalize(shortClassName);
	}

}
