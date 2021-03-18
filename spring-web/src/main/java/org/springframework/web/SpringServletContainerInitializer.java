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

package org.springframework.web;

import java.lang.reflect.Modifier;
import java.util.LinkedList;
import java.util.List;
import java.util.ServiceLoader;
import java.util.Set;

import javax.servlet.ServletContainerInitializer;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.HandlesTypes;

import org.springframework.core.annotation.AnnotationAwareOrderComparator;
import org.springframework.lang.Nullable;
import org.springframework.util.ReflectionUtils;

/**
 * Spring 容器初始化器
 * @HandlesTypes 为tomcat中定义的注解.是在tomcat容器启动时用来加载注解里面指定的类的实现子类，可以传入多个
 *
 * WebApplicationInitializer的子接口，子类，子抽象类
 *   在Servlet容器启动时都会被以参数传入onStartup的webAppInitializerClasses参数中
 */
@HandlesTypes(value = {WebApplicationInitializer.class})
// 可以传入多个类型
//@HandlesTypes(value = {WebApplicationInitializer.class})
public class SpringServletContainerInitializer implements ServletContainerInitializer {

	/**
	 * 容器启动的时候会通过SPI机制调用该类的onStartUp方法，并且传入@HandlesTypes(WebApplicationInitializer.class)类型的所有子类作为入参.
	 * @param webAppInitializerClasses  WebApplicationInitializer类型的所有子类组成的集合
	 * @param servletContext 应用上下文
	 * @throws ServletException
	 */
	@Override
	public void onStartup(@Nullable Set<Class<?>> webAppInitializerClasses, ServletContext servletContext)
			throws ServletException {

		List<WebApplicationInitializer> initializers = new LinkedList<>();

		// webAppInitializerClasses表示传入的WebApplicationInitializer的子类
		if (webAppInitializerClasses != null) {
			for (Class<?> waiClass : webAppInitializerClasses) {
				// Be defensive: Some servlet containers provide us with invalid classes,
				// no matter what @HandlesTypes says...
				// 判断传入的类是否 （不是接口 && 不是抽象类 && 是WebApplicationInitializer的子类或者子接口）
				if (!waiClass.isInterface() && !Modifier.isAbstract(waiClass.getModifiers()) &&
						WebApplicationInitializer.class.isAssignableFrom(waiClass)) {
					try {
						// 通过反射调用创建传入的类的实例，并且加入到initializers中
						initializers.add((WebApplicationInitializer)
								ReflectionUtils.accessibleConstructor(waiClass).newInstance());
					}
					catch (Throwable ex) {
						throw new ServletException("Failed to instantiate WebApplicationInitializer class", ex);
					}
				}
			}
		}

		if (initializers.isEmpty()) {
			servletContext.log("No Spring WebApplicationInitializer types detected on classpath");
			return;
		}

		servletContext.log(initializers.size() + " Spring WebApplicationInitializers detected on classpath");
		AnnotationAwareOrderComparator.sort(initializers);
		for (WebApplicationInitializer initializer : initializers) {
			// 调用子类的onStartUp方法，子类即WebApplicationInitializer的子类，需要开发者自己实现
			// 这是留给开发者的一个扩展点，如果启动时需要tomcat去做一些初始化，可以通过实现WebApplicationInitializer接口
			//   然后实现onStartUp方法，tomcat启动时就会调用自定义的onStartUp方法.
			initializer.onStartup(servletContext);
		}
	}

}
