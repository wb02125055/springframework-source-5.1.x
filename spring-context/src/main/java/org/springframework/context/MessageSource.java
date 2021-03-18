/*
 * Copyright 2002-2019 the original author or authors.
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

package org.springframework.context;

import java.util.Locale;

import org.springframework.lang.Nullable;

/**
 * Strategy interface for resolving messages, with support for the parameterization
 * and internationalization of such messages.
 *
 * <p>Spring provides two out-of-the-box implementations for production:
 * <ul>
 * <li>{@link org.springframework.context.support.ResourceBundleMessageSource}: built
 * on top of the standard {@link java.util.ResourceBundle}, sharing its limitations.
 * <li>{@link org.springframework.context.support.ReloadableResourceBundleMessageSource}:
 * highly configurable, in particular with respect to reloading message definitions.
 * </ul>
 *
 * @author Rod Johnson
 * @author Juergen Hoeller
 * @see org.springframework.context.support.ResourceBundleMessageSource
 * @see org.springframework.context.support.ReloadableResourceBundleMessageSource
 *
 * 按照区域信息获取国际化配置文件中的某个值
 *
 * 国际化使用.
 */
public interface MessageSource {

	/**
	 * 根据区域Locale获取国际化配置信息
	 *
	 * @param code: 消息编码
	 * @param args: 消息编码对应的实参内容
	 * @param defaultMessage: 默认消息内容
	 * @param locale 区域信息
	 */
	@Nullable
	String getMessage(String code, @Nullable Object[] args, @Nullable String defaultMessage, Locale locale);

	/**
	 * 获取国际化信息
	 */
	String getMessage(String code, @Nullable Object[] args, Locale locale) throws NoSuchMessageException;

	/**
	 * 获取国际化信息
	 *
	 * @param resolvable 自定义的消息解析规则。为一个接口，可以自定义消息的编码，解析规则，以及默认的国际化信息内容
	 * @param locale 国际化区域信息.
	 *
	 * @see MessageSourceResolvable#getCodes()
	 * @see MessageSourceResolvable#getArguments()
	 * @see MessageSourceResolvable#getDefaultMessage()
	 * @see java.text.MessageFormat
	 */
	String getMessage(MessageSourceResolvable resolvable, Locale locale) throws NoSuchMessageException;

}
