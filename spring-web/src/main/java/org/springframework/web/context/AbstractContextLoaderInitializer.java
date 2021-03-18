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

package org.springframework.web.context;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.springframework.context.ApplicationContextInitializer;
import org.springframework.lang.Nullable;
import org.springframework.web.WebApplicationInitializer;

/**
 * Convenient base class for {@link WebApplicationInitializer} implementations
 * that register a {@link ContextLoaderListener} in the servlet context.
 *
 * <p>The only method required to be implemented by subclasses is
 * {@link #createRootApplicationContext()}, which gets invoked from
 * {@link #registerContextLoaderListener(ServletContext)}.
 *
 * @author Arjen Poutsma
 * @author Chris Beams
 * @author Juergen Hoeller
 * @since 3.2
 *
 * 用来注册ContextLoaderListener
 */
public abstract class AbstractContextLoaderInitializer implements WebApplicationInitializer {

	/** Logger available to subclasses. */
	protected final Log logger = LogFactory.getLog(getClass());


	@Override
	public void onStartup(ServletContext servletContext) throws ServletException {
		// 直接调用registerContextLoaderListener方法，注册上下文监听器对象.
		registerContextLoaderListener(servletContext);
	}

	/**
	 * 注册上下文监听器对象，类似于如下的xml配置：
	 *
	 * 	<listener>
	 		<listener-class>org.springframework.web.context.ContextLoaderListener</listener-class>
	 	</listener>
	 */
	protected void registerContextLoaderListener(ServletContext servletContext) {
		// 创建根应用上下文对象。 createRootApplicationContext方法在本类中不实现，留给子类实现。该种设计模式为模板设计模式.
		// 模板设计模式在Spring中大量应用，父类中只会实现共性的逻辑，特殊的处理逻辑标记为抽象方法，全部留给子类去进行实现.
		// createRootApplicationContext在此处的实现子类为：AbstractAnnotationConfigDispatcherServletInitializer，返回的是AnnotationConfigWebApplicationContext
		WebApplicationContext rootAppContext = createRootApplicationContext();

		// 如果创建的根应用上下文不为null
		if (rootAppContext != null) {
			// 创建ContextLoaderListener，并且在ContextLoaderListener中注入AnnotationConfigWebApplicationContext
			ContextLoaderListener listener = new ContextLoaderListener(rootAppContext);
			listener.setContextInitializers(getRootApplicationContextInitializers());
			servletContext.addListener(listener);
		}
		else {
			logger.debug("No ContextLoaderListener registered, as " +
					"createRootApplicationContext() did not return an application context");
		}
	}

	/**
	 * 创建根应用上下文对象，由子类AbstractAnnotationConfigDispatcherServletInitializer去实现
	 * @return
	 */
	@Nullable
	protected abstract WebApplicationContext createRootApplicationContext();

	/**
	 * Specify application context initializers to be applied to the root application
	 * context that the {@code ContextLoaderListener} is being created with.
	 * @since 4.2
	 * @see #createRootApplicationContext()
	 * @see ContextLoaderListener#setContextInitializers
	 */
	@Nullable
	protected ApplicationContextInitializer<?>[] getRootApplicationContextInitializers() {
		return null;
	}

}
