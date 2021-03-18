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

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Enables support for handling components marked with AspectJ's {@code @Aspect} annotation,
 * similar to functionality found in Spring's {@code <aop:aspectj-autoproxy>} XML element.
 * To be used on @{@link Configuration} classes as follows:
 *
 * <pre class="code">
 * &#064;Configuration
 * &#064;EnableAspectJAutoProxy
 * public class AppConfig {
 *
 *     &#064;Bean
 *     public FooService fooService() {
 *         return new FooService();
 *     }
 *
 *     &#064;Bean
 *     public MyAspect myAspect() {
 *         return new MyAspect();
 *     }
 * }</pre>
 *
 * Where {@code FooService} is a typical POJO component and {@code MyAspect} is an
 * {@code @Aspect}-style aspect:
 *
 * <pre class="code">
 * public class FooService {
 *
 *     // various methods
 * }</pre>
 *
 * <pre class="code">
 * &#064;Aspect
 * public class MyAspect {
 *
 *     &#064;Before("execution(* FooService+.*(..))")
 *     public void advice() {
 *         // advise FooService methods as appropriate
 *     }
 * }</pre>
 *
 * In the scenario above, {@code @EnableAspectJAutoProxy} ensures that {@code MyAspect}
 * will be properly processed and that {@code FooService} will be proxied mixing in the
 * advice that it contributes.
 *
 * <p>Users can control the type of proxy that gets created for {@code FooService} using
 * the {@link #proxyTargetClass()} attribute. The following enables CGLIB-style 'subclass'
 * proxies as opposed to the default interface-based JDK proxy approach.
 *
 * <pre class="code">
 * &#064;Configuration
 * &#064;EnableAspectJAutoProxy(proxyTargetClass=true)
 * public class AppConfig {
 *     // ...
 * }</pre>
 *
 * <p>Note that {@code @Aspect} beans may be component-scanned like any other.
 * Simply mark the aspect with both {@code @Aspect} and {@code @Component}:
 *
 * <pre class="code">
 * package com.foo;
 *
 * &#064;Component
 * public class FooService { ... }
 *
 * &#064;Aspect
 * &#064;Component
 * public class MyAspect { ... }</pre>
 *
 * Then use the @{@link ComponentScan} annotation to pick both up:
 *
 * <pre class="code">
 * &#064;Configuration
 * &#064;ComponentScan("com.foo")
 * &#064;EnableAspectJAutoProxy
 * public class AppConfig {
 *
 *     // no explicit &#064Bean definitions required
 * }</pre>
 *
 * <b>Note: {@code @EnableAspectJAutoProxy} applies to its local application context only,
 * allowing for selective proxying of beans at different levels.</b> Please redeclare
 * {@code @EnableAspectJAutoProxy} in each individual context, e.g. the common root web
 * application context and any separate {@code DispatcherServlet} application contexts,
 * if you need to apply its behavior at multiple levels.
 *
 * <p>This feature requires the presence of {@code aspectjweaver} on the classpath.
 * While that dependency is optional for {@code spring-aop} in general, it is required
 * for {@code @EnableAspectJAutoProxy} and its underlying facilities.
 *
 * @author Chris Beams
 * @author Juergen Hoeller
 * @since 3.1
 * @see org.aspectj.lang.annotation.Aspect
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
// 通过Import注解给容器中导入AspectJAutoProxyRegistrar组件.
@Import(AspectJAutoProxyRegistrar.class)
public @interface EnableAspectJAutoProxy {

	/**
	 * 用来指定是否使用CGLIB的方式来创建代理
	 * 默认值为false，表示使用基于接口的JDK动态代理方式来创建
	 */
	boolean proxyTargetClass() default false;
	/**
	 * 用来设置是否将代理类暴露到AopContext中。如果将代理暴露在AopContext中，代理类将会被保存在ThreadLocal中，
	 * 在需要使用代理的时候直接从ThreadLocal中获取。例如：
	 *
	 * public interface StudentService {
	 *     public void m1();
	 *     public void m2();
	 * }
	 * public StudentServiceImpl implements StudentService {
	 * 	   @Transactional(propagation = Propagation.REQUIRED)
	 *     public void m1() {
	 *         log.info("m1 invoke.");
	 *         this.m2();
	 *     }
	 *     @Transactional(propagation = Propagation.REQUIRED_NEW)
	 *     public void m2() {
	 *		   System.out.println("m2 invoke.");
	 *     }
	 * }
	 * 比如：在执行m1和m2之前需要做一些操作，现在需要加一个AOP拦截。如下：
	 * @Service
	 * @Aspect
	 * public class StudentServiceInterceptor {
	 *		@Before("execution(* com.wb.spring..*.*(..))")
	 *		public void before() {
	 *		 	System.out.println("before invoke method...");
	 *		}
	 * }
	 * 如果在不设置exposeProxy的情况下，m1中的方法调用m2由于是内部调用，在执行m1的时候会触发切面拦截方法的执行，
	 * 但是在执行m2方法时不会触发切面方法的执行，不会打印"before invoke method..."日志。
	 *
	 * 因为m1中调用m2，相当于是this.m2(); this指的就是StudentServiceImpl实现类，并不是代理类，所以切面未生效。
	 *
	 * 如果想使用代理：需要将exposeProxy设置为true。
	 *  在m1中通过((StudentService)AopContext.currentProxy()).m2()去调用，AopContext.currentProxy()就
	 *  是从ThreadLocal中获取出来的代理.
	 */
	boolean exposeProxy() default false;
}
