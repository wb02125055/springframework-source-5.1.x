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

package org.springframework.core;

import org.springframework.lang.Nullable;

/**
 * Interface defining a generic contract for attaching and accessing metadata
 * to/from arbitrary objects.
 *
 * @author Rob Harrop
 * @since 2.0
 *
 * 用来定义对对象中元数据访问的抽象接口
 */
public interface AttributeAccessor {

	/**
	 * Set the attribute defined by {@code name} to the supplied {@code value}.
	 * If {@code value} is {@code null}, the attribute is {@link #removeAttribute removed}.
	 * <p>In general, users should take care to prevent overlaps with other
	 * metadata attributes by using fully-qualified names, perhaps using
	 * class or package names as prefix.
	 * @param name the unique attribute key
	 * @param value the attribute value to be attached
	 *
	 * 设置属性的值，name-value，自定义bean中的属性值.
	 * 如果属性的值为空，该属性将会被移除。否则会被加入到attribute对应的LinkedHashMap中
	 */
	void setAttribute(String name, @Nullable Object value);

	/**
	 * Get the value of the attribute identified by {@code name}.
	 * Return {@code null} if the attribute doesn't exist.
	 * @param name the unique attribute key
	 * @return the current value of the attribute, if any
	 *
	 * 返回指定属性对应的值，如果属性不存在，则返回null
	 */
	@Nullable
	Object getAttribute(String name);

	/**
	 * Remove the attribute identified by {@code name} and return its value.
	 * Return {@code null} if no attribute under {@code name} is found.
	 * @param name the unique attribute key
	 * @return the last value of the attribute, if any
	 *
	 * 删除指定的name的属性，如果属性不存在，则返回null
	 */
	@Nullable
	Object removeAttribute(String name);

	/**
	 * Return {@code true} if the attribute identified by {@code name} exists.
	 * Otherwise return {@code false}.
	 * @param name the unique attribute key
	 *
	 * 判断指定的name属性是否存在，属性名称需要与设置的属性名完全相同。
	 */
	boolean hasAttribute(String name);

	/**
	 * Return the names of all attributes.
	 *
	 * 获取所有属性的名称.
	 */
	String[] attributeNames();

}
