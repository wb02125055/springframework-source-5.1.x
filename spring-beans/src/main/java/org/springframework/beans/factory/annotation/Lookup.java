/*
 * Copyright 2002-2015 the original author or authors.
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

package org.springframework.beans.factory.annotation;

import java.lang.annotation.*;

/**
 * An annotation that indicates 'lookup' methods, to be overridden by the container
 * to redirect them back to the {@link org.springframework.beans.factory.BeanFactory}
 * for a {@code getBean} call. This is essentially an annotation-based version of the
 * XML {@code lookup-method} attribute, resulting in the same runtime arrangement.
 *
 * <p>The resolution of the target bean can either be based on the return type
 * ({@code getBean(Class)}) or on a suggested bean name ({@code getBean(String)}),
 * in both cases passing the method's arguments to the {@code getBean} call
 * for applying them as target factory method arguments or constructor arguments.
 *
 * <p>Such lookup methods can have default (stub) implementations that will simply
 * get replaced by the container, or they can be declared as abstract - for the
 * container to fill them in at runtime. In both cases, the container will generate
 * runtime subclasses of the method's containing class via CGLIB, which is why such
 * lookup methods can only work on beans that the container instantiates through
 * regular constructors: i.e. lookup methods cannot get replaced on beans returned
 * from factory methods where we cannot dynamically provide a subclass for them.
 *
 * <p><b>Concrete limitations in typical Spring configuration scenarios:</b>
 * When used with component scanning or any other mechanism that filters out abstract
 * beans, provide stub implementations of your lookup methods to be able to declare
 * them as concrete classes. And please remember that lookup methods won't work on
 * beans returned from {@code @Bean} methods in configuration classes; you'll have
 * to resort to {@code @Inject Provider<TargetBean>} or the like instead.
 *
 * @author Juergen Hoeller
 * @since 4.1
 * @see org.springframework.beans.factory.BeanFactory#getBean(Class, Object...)
 * @see org.springframework.beans.factory.BeanFactory#getBean(String, Object...)
 *
 * 常用来解决在单实例类型的Bean中引入多实例Bean的场景，通常标注在方法上。通过@Lookup注解指定一个多实例的Bean，每次在单实例Bean中
 *  获取到的多实例Bean都会是不同的bean。
 *
 * 如果直接使用Spring的bean注入，默认单实例Bean只初始化一次，第一次依赖这个多实例bean的时候，会创建一次。后面由于在此获取Spring不会再去
 *  重新创建单例Bean，所以多实例bean也不会重新进行注入，导致了在单实例中获取到的bean其实还和单实例一样，是同一个bean。
 *
 * @Lookup的用法：
 *   <public|protected> [abstract] <return-type> theMethodName(NO Arguments);
 *
 *
 * 示例：
 * 1. PrototypeBean类：
 * @Component
 * @Scope(value = "prototype")
 * public class PrototypeBean {
 * }
 *
 * 2. SingletonBean类：
 * @Component
 * public abstract class SingletonBean {
 * 		public void printBean() {
 * 			PrototypeBean prototypeBan = methodInject();
 * 			System.out.println(prototypeBean);
 * 		}
 * 		@Lookup(value = "prototypeBean")
 * 		protected abstract PrototypeBean methodInject();
 * 	}
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Lookup {
	/**
	 * This annotation attribute may suggest a target bean name to look up.
	 * If not specified, the target bean will be resolved based on the
	 * annotated method's return type declaration.
	 */
	String value() default "";
}
