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

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

/**
 * Bootstrap listener to start up and shut down Spring's root {@link WebApplicationContext}.
 * Simply delegates to {@link ContextLoader} as well as to {@link ContextCleanupListener}.
 *
 * <p>As of Spring 3.1, {@code ContextLoaderListener} supports injecting the root web
 * application context via the {@link #ContextLoaderListener(WebApplicationContext)}
 * constructor, allowing for programmatic configuration in Servlet 3.0+ environments.
 * See {@link org.springframework.web.WebApplicationInitializer} for usage examples.
 *
 * @author Juergen Hoeller
 * @author Chris Beams
 * @since 17.02.2003
 * @see #setContextInitializers
 * @see org.springframework.web.WebApplicationInitializer
 */
public class ContextLoaderListener extends ContextLoader implements ServletContextListener {

	/**
	 * 提供给xml配置版本使用的构造器。如果配置的是如下的xml，则调用该无参构造函数进行
	 *  <listener>
		 <listener-class>org.springframework.web.context.ContextLoaderListener</listener-class>
	    </listener>
		 <context-param>
			 <param-name>contextConfigLocation</param-name>
			 <param-value>/WEB-INF/root-context.xml</param-value>
		 </context-param>
	 */
	public ContextLoaderListener() {
	}

	/**
	 * 提供给注解配置版本使用的构造器
	 * 入参为Web应用上下文对象
	 * @param context
	 */
	public ContextLoaderListener(WebApplicationContext context) {
		super(context);
	}


	/**
	 * 初始化跟容器的web上下文对象
	 */
	@Override
	public void contextInitialized(ServletContextEvent event) {
		initWebApplicationContext(event.getServletContext());
	}


	/**
	 * Close the root web application context.
	 */
	@Override
	public void contextDestroyed(ServletContextEvent event) {
		closeWebApplicationContext(event.getServletContext());
		ContextCleanupListener.cleanupAttributes(event.getServletContext());
	}

}
