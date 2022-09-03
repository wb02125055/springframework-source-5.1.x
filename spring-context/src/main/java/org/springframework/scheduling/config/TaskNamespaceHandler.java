/*
 * Copyright 2002-2012 the original author or authors.
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

package org.springframework.scheduling.config;

import org.springframework.beans.factory.xml.NamespaceHandlerSupport;

/**
 * {@code NamespaceHandler} for the 'task' namespace.
 *
 * @author Mark Fisher
 * @since 3.0
 */
public class TaskNamespaceHandler extends NamespaceHandlerSupport {

	@Override
	public void init() {
		// 解析<task:annotation-driven executor="executor" /> 标签
		this.registerBeanDefinitionParser("annotation-driven", new AnnotationDrivenBeanDefinitionParser());

		// 解析<task:executor id="executor" pool-size="5" rejection-policy="ABORT" keep-alive="60" queue-capacity="1000" /> 标签
		this.registerBeanDefinitionParser("executor", new ExecutorBeanDefinitionParser());

		this.registerBeanDefinitionParser("scheduled-tasks", new ScheduledTasksBeanDefinitionParser());

		this.registerBeanDefinitionParser("scheduler", new SchedulerBeanDefinitionParser());
	}

}
