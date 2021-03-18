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

import org.springframework.beans.factory.parsing.Problem;
import org.springframework.beans.factory.parsing.ProblemReporter;
import org.springframework.core.type.MethodMetadata;

/**
 * Represents a {@link Configuration @Configuration} class method marked with the
 * {@link Bean @Bean} annotation.
 *
 * @author Chris Beams
 * @author Juergen Hoeller
 * @since 3.0
 * @see ConfigurationClass
 * @see ConfigurationClassParser
 * @see ConfigurationClassBeanDefinitionReader
 */
final class BeanMethod extends ConfigurationMethod {

	public BeanMethod(MethodMetadata metadata, ConfigurationClass configurationClass) {
		super(metadata, configurationClass);
	}

	@Override
	public void validate(ProblemReporter problemReporter) {
		if (getMetadata().isStatic()) {
			// 对于标注@Bean的静态方法，不需要强制校验，直接返回.
			return;
		}
		if (this.configurationClass.getMetadata().isAnnotated(Configuration.class.getName())) {
			// 不是静态 && 不是final && 不是private修饰的
			if (!getMetadata().isOverridable()) {
				// instance @Bean methods within @Configuration classes must be overridable to accommodate CGLIB

				// @Configuration类中标注有@Bean注解的方法，必须能够被重写，否则抛出异常，因为不能重写将无法正常使用CGLIB。
				problemReporter.error(new NonOverridableMethodError());
			}
		}
	}


	private class NonOverridableMethodError extends Problem {

		public NonOverridableMethodError() {
			super(String.format("@Bean method '%s' must not be private or final; change the method's modifiers to continue",
					getMetadata().getMethodName()), getResourceLocation());
		}
	}
}
