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

package org.springframework.web.servlet.support;

import org.springframework.lang.Nullable;
import org.springframework.util.ObjectUtils;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;

/**
 * {@link org.springframework.web.WebApplicationInitializer WebApplicationInitializer}
 * to register a {@code DispatcherServlet} and use Java-based Spring configuration.
 *
 * <p>Implementations are required to implement:
 * <ul>
 * <li>{@link #getRootConfigClasses()} -- for "root" application context (non-web
 * infrastructure) configuration.
 * <li>{@link #getServletConfigClasses()} -- for {@code DispatcherServlet}
 * application context (Spring MVC infrastructure) configuration.
 * </ul>
 *
 * <p>If an application context hierarchy is not required, applications may
 * return all configuration via {@link #getRootConfigClasses()} and return
 * {@code null} from {@link #getServletConfigClasses()}.
 *
 * @author Arjen Poutsma
 * @author Chris Beams
 * @since 3.2
 *
 * 用来创建Spring父容器和Spring子容器
 */
public abstract class AbstractAnnotationConfigDispatcherServletInitializer
		extends AbstractDispatcherServletInitializer {

	/**
	 * 通过在父类中调用子类的方法，进行根容器的创建
	 */
	@Override
	@Nullable
	protected WebApplicationContext createRootApplicationContext() {
		// 获取IOC父容器的配置类
		Class<?>[] configClasses = getRootConfigClasses();
		// 如果获取到的IOC父容器的配置类不为空
		if (!ObjectUtils.isEmpty(configClasses)) {
			// 创建IOC根容器
			AnnotationConfigWebApplicationContext context = new AnnotationConfigWebApplicationContext();
			// 把配置类加入到SpringWeb的配置类集合中
			context.register(configClasses);
			return context;
		}
		else {
			return null;
		}
	}

	/**
	 * 创建SpringMVC的子容器
	 * @return 返回子容器.
	 */
	@Override
	protected WebApplicationContext createServletApplicationContext() {
		// 创建注解版的Web应用上下文对象
		AnnotationConfigWebApplicationContext context = new AnnotationConfigWebApplicationContext();
		// 获取子容器的配置类
		//  此处为一个抽象方法，需要子类继承AbstractAnnotationConfigDispatcherServletInitializer类然后返回具体的子容器配置类.【模板设计模式】
		Class<?>[] configClasses = getServletConfigClasses();
		if (!ObjectUtils.isEmpty(configClasses)) {
			// 将配置类添加到web组件的集合列表中
			context.register(configClasses);
		}
		return context;
	}

	/**
	 * 获取根容器的配置类
	 * 		由具体的子类去实现.
	 */
	@Nullable
	protected abstract Class<?>[] getRootConfigClasses();

	/**
	 * 获取子容器的配置类
	 * 		由具体的子类去实现.
	 */
	@Nullable
	protected abstract Class<?>[] getServletConfigClasses();

}
