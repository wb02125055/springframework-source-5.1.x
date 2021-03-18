/*
 * Copyright 2002-2017 the original author or authors.
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

package org.springframework.core.type;

import org.springframework.lang.Nullable;

/**
 * Interface that defines abstract metadata of a specific class,
 * in a form that does not require that class to be loaded yet.
 *
 * @author Juergen Hoeller
 * @since 2.5
 * @see StandardClassMetadata
 * @see org.springframework.core.type.classreading.MetadataReader#getClassMetadata()
 * @see AnnotationMetadata
 */
public interface ClassMetadata {

	/**
	 * Return the name of the underlying class.
	 *
	 * 获取当前bean定义的类名
	 */
	String getClassName();

	/**
	 * Return whether the underlying class represents an interface.
	 * 返回当前bean定义指定的类是否为一个接口
	 */
	boolean isInterface();

	/**
	 * Return whether the underlying class represents an annotation.
	 * @since 4.1
	 *
	 * 返回当前bean定义指定的类是否为一个注解
	 */
	boolean isAnnotation();

	/**
	 * Return whether the underlying class is marked as abstract.
	 *
	 * 返回当前bean定义指定的类是否为一个抽象类
	 */
	boolean isAbstract();

	/**
	 * Return whether the underlying class represents a concrete class,
	 * i.e. neither an interface nor an abstract class.
	 *
	 * 返回当前bean定义指定的类是否为一个具体的类 [具体类：既不是接口也不是抽象类]
	 */
	boolean isConcrete();

	/**
	 * Return whether the underlying class is marked as 'final'.
	 *
	 * 判断当前bean定义所代表的类是否被final关键字修饰的
	 */
	boolean isFinal();

	/**
	 * Determine whether the underlying class is independent, i.e. whether
	 * it is a top-level class or a nested class (static inner class) that
	 * can be constructed independently from an enclosing class.
	 *
	 * Java中的类分为5种：
	 *    java.lang.Class#getEnclosingClass() 方法中的注释
	 *
	 *  a) Top level classes
	 *  b) Nested classes (static member classes)
	 *  c) Inner classes (non-static member classes)
	 *  d) Local classes (named classes declared within a method)
	 *  e) Anonymous classes
	 *
	 * 示例：
	 * public class MyClassType {  				// 该类表示的是top-level class (顶级类)
	 *     public class InnerClass { 			// 该类表示的是inner class (非静态内部类)
	 *     }
	 *     public static class NestedClass {	// 该类表示的是nested class (静态内部类)
	 *     }
	 *     public static void main(String[] args) {
	 *         class LocalClass {				// 该类表示的是local class (方法内部定义的类)
	 *         }
	 *         new Thread(new Runnable() {		// 该类表示的是anonymous class (匿名内部类)
	 *             @Override
	 *             public void run() {
	 *                 System.out.println("thread run...");
	 *             }
	 *         }).start;
	 *     }
	 * }
	 *
	 * 判断当前bean定义所代表的类是否为一个独立的类。[独立的类：顶级类，非静态内部类，静态内部类]
	 */
	boolean isIndependent();

	/**
	 * Return whether the underlying class is declared within an enclosing
	 * class (i.e. the underlying class is an inner/nested class or a
	 * local class within a method).
	 * <p>If this method returns {@code false}, then the underlying
	 * class is a top-level class.
	 *
	 * 判断bean定义所表示的class是否为一个封闭的类。[封闭的类：静态内类类，非静态内部类，方法内部的本地类]
	 */
	boolean hasEnclosingClass();

	/**
	 * Return the name of the enclosing class of the underlying class,
	 * or {@code null} if the underlying class is a top-level class.
	 *
	 * 获取一个封闭类[静态内部类，非静态内部类，方法内部的类]的类名。
	 * 如果当前类为top-class。则会返回null
	 */
	@Nullable
	String getEnclosingClassName();

	/**
	 * Return whether the underlying class has a super class.
	 *
	 * 返回bean定义所表示的class是否存在着父类
	 */
	boolean hasSuperClass();

	/**
	 * Return the name of the super class of the underlying class,
	 * or {@code null} if there is no super class defined.
	 *
	 * 返回bean定义所表示的class的父类类名。
	 * 如果没有父类，将返回null
	 */
	@Nullable
	String getSuperClassName();

	/**
	 * Return the names of all interfaces that the underlying class
	 * implements, or an empty array if there are none.
	 *
	 * 返回bean定义所表示的class实现的所有接口名称。
	 * 如果未实现任何接口，将会返回空数组。
	 */
	String[] getInterfaceNames();

	/**
	 * Return the names of all classes declared as members of the class represented by
	 * this ClassMetadata object. This includes public, protected, default (package)
	 * access, and private classes and interfaces declared by the class, but excludes
	 * inherited classes and interfaces. An empty array is returned if no member classes
	 * or interfaces exist.
	 * @since 3.1
	 *
	 *  返回bean定义所表示的class的成员类的类名称。包括：私有内部类(private class)，
	 *   共有内部类(public class)，受保护的内部类(protected class)，默认内部(不加修饰符的)还有内部接口
	 *
	 *   如果没有成员类或者成员接口，将会返回空数组。
	 */
	String[] getMemberClassNames();

}
