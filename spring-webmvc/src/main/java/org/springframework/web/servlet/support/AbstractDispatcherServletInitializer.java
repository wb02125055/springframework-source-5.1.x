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

import java.util.EnumSet;

import javax.servlet.DispatcherType;
import javax.servlet.Filter;
import javax.servlet.FilterRegistration;
import javax.servlet.FilterRegistration.Dynamic;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRegistration;

import org.springframework.context.ApplicationContextInitializer;
import org.springframework.core.Conventions;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;
import org.springframework.web.context.AbstractContextLoaderInitializer;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;
import org.springframework.web.servlet.FrameworkServlet;

/**
 * Base class for {@link org.springframework.web.WebApplicationInitializer}
 * implementations that register a {@link DispatcherServlet} in the servlet context.
 *
 * <p>Most applications should consider extending the Spring Java config subclass
 * {@link AbstractAnnotationConfigDispatcherServletInitializer}.
 *
 * @author Arjen Poutsma
 * @author Chris Beams
 * @author Rossen Stoyanchev
 * @author Juergen Hoeller
 * @author Stephane Nicoll
 * @since 3.2
 *
 * 用来注册DispatcherServlet
 */
public abstract class AbstractDispatcherServletInitializer extends AbstractContextLoaderInitializer {

	/**
	 * The default servlet name. Can be customized by overriding {@link #getServletName}.
	 */
	public static final String DEFAULT_SERVLET_NAME = "dispatcher";


	@Override
	public void onStartup(ServletContext servletContext) throws ServletException {
		// 实例化Spring Root上下文，将ContextLoaderListener监听器加入到ServletContext中
		super.onStartup(servletContext);
		// 注册DispatcherServlet 创建Spring web上下文对象
		registerDispatcherServlet(servletContext);
	}

	/**
	 * 注册DispatcherServlet 创建Spring web上下文对象
	 */
	protected void registerDispatcherServlet(ServletContext servletContext) {
		// 获取DispatcherServlet的名称 "dispatcher"
		String servletName = getServletName();
		Assert.hasLength(servletName, "getServletName() must not return null or empty");

		// 创建WebApplicationContext对象
		WebApplicationContext servletAppContext = createServletApplicationContext();
		Assert.notNull(servletAppContext, "createServletApplicationContext() must not return null");

		// 创建DispatcherServlet对象，因此Tomcat会对DispatcherServlet进行生命周期管理
		FrameworkServlet dispatcherServlet = createDispatcherServlet(servletAppContext);
		Assert.notNull(dispatcherServlet, "createDispatcherServlet(WebApplicationContext) must not return null");

		// 获取ServletApplicationContextInitializers对象，然后将其注册到DispatcherServlet中
		dispatcherServlet.setContextInitializers(getServletApplicationContextInitializers());

		// 将dispatcherServlet注册到Spring Web上下文中
		ServletRegistration.Dynamic registration = servletContext.addServlet(servletName, dispatcherServlet);
		if (registration == null) {
			throw new IllegalStateException("Failed to register servlet with name '" + servletName + "'. " +
					"Check if there is another servlet registered under the same name.");
		}

		// 设置dispatcherServlet的属性
		/**
		 * loadOnStartup具体作用如下：
		 1、load-on-startup元素标记容器是否在启动的时候就加载这个servlet(实例化并调用其init()方法)。
		 2、它的值必须是一个整数，表示servlet应该被载入的顺序
		 3、当值为0或者大于0时，表示容器在应用启动时就加载并初始化这个servlet；
		 4、当值小于0或者没有指定时，则表示容器在该servlet被选择时才会去加载。
		 5、正数的值越小，该servlet的优先级越高，应用启动时就越先加载。
		 6、当值相同时，容器就会自己选择顺序来加载。
		 */
		registration.setLoadOnStartup(1);

		// 添加Servlet的映射路径
		registration.addMapping(getServletMappings());

		// 设置servlet是否支持异步，默认支持异步方式
		registration.setAsyncSupported(isAsyncSupported());

		// 获取并注册自定义配置的过滤器
		Filter[] filters = getServletFilters();
		if (!ObjectUtils.isEmpty(filters)) {
			for (Filter filter : filters) {
				registerServletFilter(servletContext, filter);
			}
		}

		// 定制其他servlet的特有属性
		customizeRegistration(registration);
	}

	/**
	 * Return the name under which the {@link DispatcherServlet} will be registered.
	 * Defaults to {@link #DEFAULT_SERVLET_NAME}.
	 * @see #registerDispatcherServlet(ServletContext)
	 */
	protected String getServletName() {
		// dispatcher
		return DEFAULT_SERVLET_NAME;
	}

	/**
	 * Create a servlet application context to be provided to the {@code DispatcherServlet}.
	 * <p>The returned context is delegated to Spring's
	 * {@link DispatcherServlet#DispatcherServlet(WebApplicationContext)}. As such,
	 * it typically contains controllers, view resolvers, locale resolvers, and other
	 * web-related beans.
	 * @see #registerDispatcherServlet(ServletContext)
	 */
	protected abstract WebApplicationContext createServletApplicationContext();

	/**
	 * Create a {@link DispatcherServlet} (or other kind of {@link FrameworkServlet}-derived
	 * dispatcher) with the specified {@link WebApplicationContext}.
	 * <p>Note: This allows for any {@link FrameworkServlet} subclass as of 4.2.3.
	 * Previously, it insisted on returning a {@link DispatcherServlet} or subclass thereof.
	 */
	protected FrameworkServlet createDispatcherServlet(WebApplicationContext servletAppContext) {
		return new DispatcherServlet(servletAppContext);
	}

	/**
	 * Specify application context initializers to be applied to the servlet-specific
	 * application context that the {@code DispatcherServlet} is being created with.
	 * @since 4.2
	 * @see #createServletApplicationContext()
	 * @see DispatcherServlet#setContextInitializers
	 * @see #getRootApplicationContextInitializers()
	 */
	@Nullable
	protected ApplicationContextInitializer<?>[] getServletApplicationContextInitializers() {
		return null;
	}

	/**
	 * Specify the servlet mapping(s) for the {@code DispatcherServlet} &mdash;
	 * for example {@code "/"}, {@code "/app"}, etc.
	 * @see #registerDispatcherServlet(ServletContext)
	 */
	protected abstract String[] getServletMappings();

	/**
	 * Specify filters to add and map to the {@code DispatcherServlet}.
	 * @return an array of filters or {@code null}
	 * @see #registerServletFilter(ServletContext, Filter)
	 */
	@Nullable
	protected Filter[] getServletFilters() {
		return null;
	}

	/**
	 * Add the given filter to the ServletContext and map it to the
	 * {@code DispatcherServlet} as follows:
	 * <ul>
	 * <li>a default filter name is chosen based on its concrete type
	 * <li>the {@code asyncSupported} flag is set depending on the
	 * return value of {@link #isAsyncSupported() asyncSupported}
	 * <li>a filter mapping is created with dispatcher types {@code REQUEST},
	 * {@code FORWARD}, {@code INCLUDE}, and conditionally {@code ASYNC} depending
	 * on the return value of {@link #isAsyncSupported() asyncSupported}
	 * </ul>
	 * <p>If the above defaults are not suitable or insufficient, override this
	 * method and register filters directly with the {@code ServletContext}.
	 * @param servletContext the servlet context to register filters with
	 * @param filter the filter to be registered
	 * @return the filter registration
	 */
	protected FilterRegistration.Dynamic registerServletFilter(ServletContext servletContext, Filter filter) {
		// 获取过滤器名称
		String filterName = Conventions.getVariableName(filter);
		// 添加过滤器，使用的是servlet的api
		Dynamic registration = servletContext.addFilter(filterName, filter);

		if (registration == null) {
			int counter = 0;
			while (registration == null) {
				if (counter == 100) {
					throw new IllegalStateException("Failed to register filter with name '" + filterName + "'. " +
							"Check if there is another filter registered under the same name.");
				}
				registration = servletContext.addFilter(filterName + "#" + counter, filter);
				counter++;
			}
		}
		// 过滤器是否支持异步
		registration.setAsyncSupported(isAsyncSupported());
		registration.addMappingForServletNames(getDispatcherTypes(), false, getServletName());
		return registration;
	}

	/**
	 * 获取过滤器的转发模式
	 */
	private EnumSet<DispatcherType> getDispatcherTypes() {
		return (isAsyncSupported() ?
				EnumSet.of(DispatcherType.REQUEST, DispatcherType.FORWARD, DispatcherType.INCLUDE, DispatcherType.ASYNC) :
				EnumSet.of(DispatcherType.REQUEST, DispatcherType.FORWARD, DispatcherType.INCLUDE));
	}

	/**
	 * A single place to control the {@code asyncSupported} flag for the
	 * {@code DispatcherServlet} and all filters added via {@link #getServletFilters()}.
	 * <p>The default value is "true".
	 */
	protected boolean isAsyncSupported() {
		return true;
	}

	/**
	 * Optionally perform further registration customization once
	 * {@link #registerDispatcherServlet(ServletContext)} has completed.
	 * @param registration the {@code DispatcherServlet} registration to be customized
	 * @see #registerDispatcherServlet(ServletContext)
	 */
	protected void customizeRegistration(ServletRegistration.Dynamic registration) {
	}

}
