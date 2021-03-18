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

package org.springframework.beans.factory.support;

import org.apache.commons.logging.Log;
import org.springframework.beans.*;
import org.springframework.beans.factory.*;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.beans.factory.config.ConstructorArgumentValues;
import org.springframework.beans.factory.config.ConstructorArgumentValues.ValueHolder;
import org.springframework.beans.factory.config.DependencyDescriptor;
import org.springframework.core.*;
import org.springframework.lang.Nullable;
import org.springframework.util.*;

import java.beans.ConstructorProperties;
import java.lang.reflect.*;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.*;

/**
 * Delegate for resolving constructors and factory methods.
 * Performs constructor resolution through argument matching.
 *
 * @author Juergen Hoeller
 * @author Rob Harrop
 * @author Mark Fisher
 * @author Costin Leau
 * @author Sebastien Deleuze
 * @since 2.0
 * @see #autowireConstructor
 * @see #instantiateUsingFactoryMethod
 * @see AbstractAutowireCapableBeanFactory
 */
class ConstructorResolver {

	private static final Object[] EMPTY_ARGS = new Object[0];

	private static final NamedThreadLocal<InjectionPoint> currentInjectionPoint =
			new NamedThreadLocal<>("Current injection point");

	private final AbstractAutowireCapableBeanFactory beanFactory;

	private final Log logger;


	/**
	 * Create a new ConstructorResolver for the given factory and instantiation strategy.
	 * @param beanFactory the BeanFactory to work with
	 */
	public ConstructorResolver(AbstractAutowireCapableBeanFactory beanFactory) {
		this.beanFactory = beanFactory;
		this.logger = beanFactory.getLogger();
	}


	/**
	 * "autowire constructor" (with constructor arguments by type) behavior.
	 * Also applied if explicit constructor argument values are specified,
	 * matching all remaining arguments with beans from the bean factory.
	 * <p>This corresponds to constructor injection: In this mode, a Spring
	 * bean factory is able to host components that expect constructor-based
	 * dependency resolution.
	 * @param beanName the name of the bean
	 * @param mbd the merged bean definition for the bean
	 * @param chosenCtors chosen candidate constructors (or {@code null} if none)
	 * @param explicitArgs argument values passed in programmatically via the getBean method,
	 * or {@code null} if none (-> use constructor argument values from bean definition)
	 * @return a BeanWrapper for the new instance
	 */
	public BeanWrapper autowireConstructor(String beanName, RootBeanDefinition mbd,
			@Nullable Constructor<?>[] chosenCtors, @Nullable Object[] explicitArgs) {

		BeanWrapperImpl bw = new BeanWrapperImpl();
		this.beanFactory.initBeanWrapper(bw);

		// spring对这个bean进行实例化使用的构造函数
		Constructor<?> constructorToUse = null;
		// spring执行构造函数时使用的参数封装类
		ArgumentsHolder argsHolderToUse = null;
		// 参与构造函数实例化过程的参数
		Object[] argsToUse = null;

		// 如果传入参数的话，就直接使用传入的参数
		if (explicitArgs != null) {
			argsToUse = explicitArgs;
		}
		else {
			// 声明需要解析的参数值数组
			Object[] argsToResolve = null;
			synchronized (mbd.constructorArgumentLock) {
				// 获取BeanDefinition中已经解析完成的构造函数
				constructorToUse = (Constructor<?>) mbd.resolvedConstructorOrFactoryMethod;
				// BeanDefinition中存在构造函数并且存在构造函数的参数，赋值进行使用
				if (constructorToUse != null && mbd.constructorArgumentsResolved) {
					// Found a cached constructor...
					// 从缓存中找到了构造器，那么继续从缓存中寻找缓存的构造器参数
					argsToUse = mbd.resolvedConstructorArguments;
					if (argsToUse == null) {
						// 没有缓存的参数，就需要获取配置文件中配置的参数
						argsToResolve = mbd.preparedConstructorArguments;
					}
				}
			}

			// 如果缓存中没有缓存的参数的话，即argsToResolve不为空，就需要解析配置的参数
			if (argsToResolve != null) {
				// 解析参数类型，比如将配置的String类型转换为list、boolean等类型
				argsToUse = resolvePreparedArguments(beanName, mbd, bw, constructorToUse, argsToResolve, true);
			}
		}

		if (constructorToUse == null || argsToUse == null) {
			// Take specified constructors, if any.
			// 如果传入的构造器数组不为空，就使用传入的过后早期参数，否则通过反射获取class中定义的构造器
			Constructor<?>[] candidates = chosenCtors;
			if (candidates == null) {
				// 从bean定义中获取对应的class类型
				Class<?> beanClass = mbd.getBeanClass();
				try {
					// getDeclaredConstructor: 返回所有的构造函数，包括(public protected default private修饰的所有构造器)
					// getConstructor: 返回public修饰的构造器
					candidates = (mbd.isNonPublicAccessAllowed() ?
							beanClass.getDeclaredConstructors() : beanClass.getConstructors());
				}
				catch (Throwable ex) {
					throw new BeanCreationException(mbd.getResourceDescription(), beanName,
							"Resolution of declared constructors on bean Class [" + beanClass.getName() +
							"] from ClassLoader [" + beanClass.getClassLoader() + "] failed", ex);
				}
			}

			// 如果candidateList只有一个元素 && 没有传入构造函数值 && mbd也没有构造函数参数值
			if (candidates.length == 1 && explicitArgs == null && !mbd.hasConstructorArgumentValues()) {
				Constructor<?> uniqueCandidate = candidates[0];
				if (uniqueCandidate.getParameterCount() == 0) {
					//使用mdb的构造函数字段的通用锁【{@link RootBeanDefinition#constructorArgumentLock}】进行加锁以保证线程安全
					synchronized (mbd.constructorArgumentLock) {
						//让mbd缓存已解析的构造函数或工厂方法
						mbd.resolvedConstructorOrFactoryMethod = uniqueCandidate;
						//让mbd标记构造函数参数已解析
						mbd.constructorArgumentsResolved = true;
						//让mbd缓存完全解析的构造函数参数
						mbd.resolvedConstructorArguments = EMPTY_ARGS;
					}
					// 实例化
					bw.setBeanInstance(instantiate(beanName, mbd, uniqueCandidate, EMPTY_ARGS));
					return bw;
				}
			}

			// Need to resolve the constructor.
			// 自动装配标识，以下有一种情况成立则为true，
			// 1、传进来构造函数，证明spring根据之前代码的判断，知道应该用哪个构造函数，
			// 2、BeanDefinition中设置为构造函数注入模型
			boolean autowiring = (chosenCtors != null ||
					mbd.getResolvedAutowireMode() == AutowireCapableBeanFactory.AUTOWIRE_CONSTRUCTOR);
			// 定义一个用于存放解析后的构造函数参数值的ConstructorArgumentValues对象
			ConstructorArgumentValues resolvedValues = null;

			// 构造函数的最小参数个数
			int minNrOfArgs;
			// 如果传入了参与构造函数实例化的参数值，那么参数的数量即为最小参数个数
			if (explicitArgs != null) {
				// minNrOfArgs引用explitArgs的数组长度
				minNrOfArgs = explicitArgs.length;
			}
			else {
				// 提取配置文件中的配置的构造函数参数
				ConstructorArgumentValues cargs = mbd.getConstructorArgumentValues();
				// 用于承载解析后的构造函数参数的值
				resolvedValues = new ConstructorArgumentValues();
				// 能解析到的参数个数
				minNrOfArgs = resolveConstructorArguments(beanName, mbd, bw, cargs, resolvedValues);
			}

			// 对候选的构造函数进行排序，先是访问权限后是参数个数
			// public权限参数数量由多到少
			AutowireUtils.sortConstructors(candidates);
			// 定义一个差异变量，变量的大小决定着构造函数是否能够被使用
			int minTypeDiffWeight = Integer.MAX_VALUE;
			// 不明确的构造函数集合，正常情况下差异值不可能相同
			Set<Constructor<?>> ambiguousConstructors = null;
			LinkedList<UnsatisfiedDependencyException> causes = null;

			// 循环候选的构造函数
			for (Constructor<?> candidate : candidates) {
				Class<?>[] paramTypes = candidate.getParameterTypes();

				// 如果已经找到选用的构造函数或者需要的参数个数小于当前的构造函数参数个数则终止，前面已经经过了排序操作
				if (constructorToUse != null && argsToUse != null && argsToUse.length > paramTypes.length) {
					// Already found greedy constructor that can be satisfied ->
					// do not look any further, there are only less greedy constructors left.
					break;
				}
				//如果本构造函数的参数列表数量小于要求的最小数量，则遍历下一个
				if (paramTypes.length < minNrOfArgs) {
					continue;
				}

				// 存放构造函数解析完成的参数列表
				ArgumentsHolder argsHolder;

				// 获取参数列表的类型
				if (resolvedValues != null) {
					try {
						// 获取构造函数上的ConstructorProperties注解中的参数
						String[] paramNames = ConstructorPropertiesChecker.evaluate(candidate, paramTypes.length);
						// 如果没有上面的注解，则获取构造函数参数列表中属性的名称
						if (paramNames == null) {
							ParameterNameDiscoverer pnd = this.beanFactory.getParameterNameDiscoverer();
							if (pnd != null) {
								paramNames = pnd.getParameterNames(candidate);
							}
						}
						// 根据名称和数据类型创建参数持有者
						argsHolder = createArgumentArray(beanName, mbd, resolvedValues, bw, paramTypes, paramNames,
								getUserDeclaredConstructor(candidate), autowiring, candidates.length == 1);
					}
					catch (UnsatisfiedDependencyException ex) {
						if (logger.isTraceEnabled()) {
							logger.trace("Ignoring constructor [" + candidate + "] of bean '" + beanName + "': " + ex);
						}
						// Swallow and try next constructor.
						if (causes == null) {
							causes = new LinkedList<>();
						}
						causes.add(ex);
						continue;
					}
				}
				// 不存在构造函数参数列表需要解析的参数
				else {
					// Explicit arguments given -> arguments length must match exactly.
					// 如果参数列表的数量与传入进来的参数数量不相等，继续遍历，否则构造参数列表封装对象
					if (paramTypes.length != explicitArgs.length) {
						continue;
					}
					// 构造函数没有参数的情况
					argsHolder = new ArgumentsHolder(explicitArgs);
				}

				// 计算差异量，根据要参与构造函数的参数列表和本构造函数的参数列表进行计算
				int typeDiffWeight = (mbd.isLenientConstructorResolution() ?
						argsHolder.getTypeDifferenceWeight(paramTypes) : argsHolder.getAssignabilityWeight(paramTypes));
				// Choose this constructor if it represents the closest match.
				// 本次的构造函数差异值小于上一个构造函数，则进行构造函数更换
				if (typeDiffWeight < minTypeDiffWeight) {
					// 将确定使用的构造函数设置为本构造
					constructorToUse = candidate;
					// 更换使用的构造函数参数封装类
					argsHolderToUse = argsHolder;
					// 更换参与构造函数实例化的参数
					argsToUse = argsHolder.arguments;
					// 差异值更换
					minTypeDiffWeight = typeDiffWeight;
					// 不明确的构造函数列表清空为null
					ambiguousConstructors = null;
				}
				// 差异值相等，则表明构造函数不正常，放入异常集合
				else if (constructorToUse != null && typeDiffWeight == minTypeDiffWeight) {
					if (ambiguousConstructors == null) {
						ambiguousConstructors = new LinkedHashSet<>();
						ambiguousConstructors.add(constructorToUse);
					}
					ambiguousConstructors.add(candidate);
				}
			}

			// 以下两种情况会抛异常: 1、没有确定使用的构造函数; 2、存在模糊的构造函数并且不允许存在模糊的构造函数
			if (constructorToUse == null) {
				if (causes != null) {
					UnsatisfiedDependencyException ex = causes.removeLast();
					for (Exception cause : causes) {
						this.beanFactory.onSuppressedException(cause);
					}
					throw ex;
				}
				throw new BeanCreationException(mbd.getResourceDescription(), beanName,
						"Could not resolve matching constructor " +
						"(hint: specify index/type/name arguments for simple parameters to avoid type ambiguities)");
			}
			else if (ambiguousConstructors != null && !mbd.isLenientConstructorResolution()) {
				throw new BeanCreationException(mbd.getResourceDescription(), beanName,
						"Ambiguous constructor matches found in bean '" + beanName + "' " +
						"(hint: specify index/type/name arguments for simple parameters to avoid type ambiguities): " +
						ambiguousConstructors);
			}
			/**
			 * 没有传入参与构造函数参数列表的参数时，对构造函数缓存到BeanDefinition中
			 * 	1、缓存BeanDefinition进行实例化时使用的构造函数
			 * 	2、缓存BeanDefinition代表的Bean的构造函数已解析完标识
			 * 	3、缓存参与构造函数参数列表值的参数列表
			 */
			if (explicitArgs == null && argsHolderToUse != null) {
				argsHolderToUse.storeCache(mbd, constructorToUse);
			}
		}

		Assert.state(argsToUse != null, "Unresolved constructor arguments");
		// 实例化
		bw.setBeanInstance(instantiate(beanName, mbd, constructorToUse, argsToUse));
		// 返回
		return bw;
	}

	private Object instantiate(
			String beanName, RootBeanDefinition mbd, Constructor<?> constructorToUse, Object[] argsToUse) {

		try {
			InstantiationStrategy strategy = this.beanFactory.getInstantiationStrategy();
			if (System.getSecurityManager() != null) {
				return AccessController.doPrivileged((PrivilegedAction<Object>) () ->
						strategy.instantiate(mbd, beanName, this.beanFactory, constructorToUse, argsToUse),
						this.beanFactory.getAccessControlContext());
			}
			else {
				return strategy.instantiate(mbd, beanName, this.beanFactory, constructorToUse, argsToUse);
			}
		}
		catch (Throwable ex) {
			throw new BeanCreationException(mbd.getResourceDescription(), beanName,
					"Bean instantiation via constructor failed", ex);
		}
	}

	/**
	 * Resolve the factory method in the specified bean definition, if possible.
	 * {@link RootBeanDefinition#getResolvedFactoryMethod()} can be checked for the result.
	 * @param mbd the bean definition to check
	 */
	public void resolveFactoryMethodIfPossible(RootBeanDefinition mbd) {
		Class<?> factoryClass;
		boolean isStatic;
		if (mbd.getFactoryBeanName() != null) {
			factoryClass = this.beanFactory.getType(mbd.getFactoryBeanName());
			isStatic = false;
		}
		else {
			factoryClass = mbd.getBeanClass();
			isStatic = true;
		}
		Assert.state(factoryClass != null, "Unresolvable factory class");
		factoryClass = ClassUtils.getUserClass(factoryClass);

		Method[] candidates = getCandidateMethods(factoryClass, mbd);
		Method uniqueCandidate = null;
		for (Method candidate : candidates) {
			if (Modifier.isStatic(candidate.getModifiers()) == isStatic && mbd.isFactoryMethod(candidate)) {
				if (uniqueCandidate == null) {
					uniqueCandidate = candidate;
				}
				else if (!Arrays.equals(uniqueCandidate.getParameterTypes(), candidate.getParameterTypes())) {
					uniqueCandidate = null;
					break;
				}
			}
		}
		mbd.factoryMethodToIntrospect = uniqueCandidate;
	}

	/**
	 * Retrieve all candidate methods for the given class, considering
	 * the {@link RootBeanDefinition#isNonPublicAccessAllowed()} flag.
	 * Called as the starting point for factory method determination.
	 */
	private Method[] getCandidateMethods(Class<?> factoryClass, RootBeanDefinition mbd) {
		if (System.getSecurityManager() != null) {
			return AccessController.doPrivileged((PrivilegedAction<Method[]>) () ->
					(mbd.isNonPublicAccessAllowed() ?
						ReflectionUtils.getAllDeclaredMethods(factoryClass) : factoryClass.getMethods()));
		}
		else {
			return (mbd.isNonPublicAccessAllowed() ?
					ReflectionUtils.getAllDeclaredMethods(factoryClass) : factoryClass.getMethods());
		}
	}

	/**
	 * Instantiate the bean using a named factory method. The method may be static, if the
	 * bean definition parameter specifies a class, rather than a "factory-bean", or
	 * an instance variable on a factory object itself configured using Dependency Injection.
	 * <p>Implementation requires iterating over the static or instance methods with the
	 * name specified in the RootBeanDefinition (the method may be overloaded) and trying
	 * to match with the parameters. We don't have the types attached to constructor args,
	 * so trial and error is the only way to go here. The explicitArgs array may contain
	 * argument values passed in programmatically via the corresponding getBean method.
	 * @param beanName the name of the bean
	 * @param mbd the merged bean definition for the bean
	 * @param explicitArgs argument values passed in programmatically via the getBean
	 * method, or {@code null} if none (-> use constructor argument values from bean definition)
	 * @return a BeanWrapper for the new instance
	 */
	public BeanWrapper instantiateUsingFactoryMethod(
			String beanName, RootBeanDefinition mbd, @Nullable Object[] explicitArgs) {

		BeanWrapperImpl bw = new BeanWrapperImpl();
		this.beanFactory.initBeanWrapper(bw);

		// 工厂对象
		Object factoryBean;
		// 工厂类
		Class<?> factoryClass;
		// 是否为静态工厂方法
		boolean isStatic;

		// 获取工厂bean名称，如果工厂bean名称不为空，表示以实例工厂的方式创建实例对象
		String factoryBeanName = mbd.getFactoryBeanName();
		if (factoryBeanName != null) {
			if (factoryBeanName.equals(beanName)) {
				throw new BeanDefinitionStoreException(mbd.getResourceDescription(), beanName,
						"factory-bean reference points back to the same bean definition");
			}
			factoryBean = this.beanFactory.getBean(factoryBeanName);
			if (mbd.isSingleton() && this.beanFactory.containsSingleton(beanName)) {
				throw new ImplicitlyAppearedSingletonException();
			}
			factoryClass = factoryBean.getClass();
			// 标识非静态的工厂方法
			isStatic = false;
		}
		else {
			// It's a static factory method on the bean class.
			if (!mbd.hasBeanClass()) {
				throw new BeanDefinitionStoreException(mbd.getResourceDescription(), beanName,
						"bean definition declares neither a bean class nor a factory-bean reference");
			}
			factoryBean = null;
			factoryClass = mbd.getBeanClass();
			// 标识为静态工厂方法
			isStatic = true;
		}

		Method factoryMethodToUse = null;
		ArgumentsHolder argsHolderToUse = null;
		Object[] argsToUse = null;

		if (explicitArgs != null) {
			argsToUse = explicitArgs;
		}
		else {
			Object[] argsToResolve = null;
			synchronized (mbd.constructorArgumentLock) {
				factoryMethodToUse = (Method) mbd.resolvedConstructorOrFactoryMethod;
				if (factoryMethodToUse != null && mbd.constructorArgumentsResolved) {
					// Found a cached factory method...
					argsToUse = mbd.resolvedConstructorArguments;
					if (argsToUse == null) {
						argsToResolve = mbd.preparedConstructorArguments;
					}
				}
			}
			if (argsToResolve != null) {
				argsToUse = resolvePreparedArguments(beanName, mbd, bw, factoryMethodToUse, argsToResolve, true);
			}
		}

		if (factoryMethodToUse == null || argsToUse == null) {
			// Need to determine the factory method...
			// Try all methods with this name to see if they match the given arguments.
			factoryClass = ClassUtils.getUserClass(factoryClass);
			// 使用javaConfig的方式，则当前的factoryClass就是javaConfig对应的配置类.
			Method[] rawCandidates = getCandidateMethods(factoryClass, mbd);
			List<Method> candidateList = new ArrayList<>();
			for (Method candidate : rawCandidates) {
				if (Modifier.isStatic(candidate.getModifiers()) == isStatic && mbd.isFactoryMethod(candidate)) {
					candidateList.add(candidate);
				}
			}
			// 如果只有一个候选的工厂方法
			if (candidateList.size() == 1 && explicitArgs == null && !mbd.hasConstructorArgumentValues()) {
				Method uniqueCandidate = candidateList.get(0);
				// 没有参数.
				if (uniqueCandidate.getParameterCount() == 0) {
					mbd.factoryMethodToIntrospect = uniqueCandidate;
					synchronized (mbd.constructorArgumentLock) {
						mbd.resolvedConstructorOrFactoryMethod = uniqueCandidate;
						mbd.constructorArgumentsResolved = true;
						mbd.resolvedConstructorArguments = EMPTY_ARGS;
					}
					// 在普通的配置类中配置Bean，则会在此处直接通过反射调用标注@Bean注解的方法创建bean对象.
					bw.setBeanInstance(instantiate(beanName, mbd, factoryBean, uniqueCandidate, EMPTY_ARGS));
					return bw;
				}
			}

			Method[] candidates = candidateList.toArray(new Method[0]);
			AutowireUtils.sortFactoryMethods(candidates);

			ConstructorArgumentValues resolvedValues = null;
			boolean autowiring = (mbd.getResolvedAutowireMode() == AutowireCapableBeanFactory.AUTOWIRE_CONSTRUCTOR);
			int minTypeDiffWeight = Integer.MAX_VALUE;
			Set<Method> ambiguousFactoryMethods = null;

			int minNrOfArgs;
			if (explicitArgs != null) {
				minNrOfArgs = explicitArgs.length;
			}
			else {
				// We don't have arguments passed in programmatically, so we need to resolve the
				// arguments specified in the constructor arguments held in the bean definition.
				if (mbd.hasConstructorArgumentValues()) {
					ConstructorArgumentValues cargs = mbd.getConstructorArgumentValues();
					resolvedValues = new ConstructorArgumentValues();
					minNrOfArgs = resolveConstructorArguments(beanName, mbd, bw, cargs, resolvedValues);
				}
				else {
					minNrOfArgs = 0;
				}
			}

			LinkedList<UnsatisfiedDependencyException> causes = null;

			for (Method candidate : candidates) {
				Class<?>[] paramTypes = candidate.getParameterTypes();

				if (paramTypes.length >= minNrOfArgs) {
					ArgumentsHolder argsHolder;

					if (explicitArgs != null) {
						// Explicit arguments given -> arguments length must match exactly.
						if (paramTypes.length != explicitArgs.length) {
							continue;
						}
						argsHolder = new ArgumentsHolder(explicitArgs);
					}
					else {
						// Resolved constructor arguments: type conversion and/or autowiring necessary.
						try {
							String[] paramNames = null;
							ParameterNameDiscoverer pnd = this.beanFactory.getParameterNameDiscoverer();
							if (pnd != null) {
								paramNames = pnd.getParameterNames(candidate);
							}
							// 创建参数数组。因为在工厂方法中，可能存在着多个方法名和参数个数都一样但是参数类型不同的方法
							argsHolder = createArgumentArray(beanName, mbd, resolvedValues, bw,
									paramTypes, paramNames, candidate, autowiring, candidates.length == 1);
						}
						catch (UnsatisfiedDependencyException ex) {
							if (logger.isTraceEnabled()) {
								logger.trace("Ignoring factory method [" + candidate + "] of bean '" + beanName + "': " + ex);
							}
							// Swallow and try next overloaded factory method.
							if (causes == null) {
								causes = new LinkedList<>();
							}
							causes.add(ex);
							continue;
						}
					}

					int typeDiffWeight = (mbd.isLenientConstructorResolution() ?
							argsHolder.getTypeDifferenceWeight(paramTypes) : argsHolder.getAssignabilityWeight(paramTypes));
					// Choose this factory method if it represents the closest match.
					if (typeDiffWeight < minTypeDiffWeight) {
						factoryMethodToUse = candidate;
						argsHolderToUse = argsHolder;
						argsToUse = argsHolder.arguments;
						minTypeDiffWeight = typeDiffWeight;
						ambiguousFactoryMethods = null;
					}
					// Find out about ambiguity: In case of the same type difference weight
					// for methods with the same number of parameters, collect such candidates
					// and eventually raise an ambiguity exception.
					// However, only perform that check in non-lenient constructor resolution mode,
					// and explicitly ignore overridden methods (with the same parameter signature).
					else if (factoryMethodToUse != null && typeDiffWeight == minTypeDiffWeight &&
							!mbd.isLenientConstructorResolution() &&
							paramTypes.length == factoryMethodToUse.getParameterCount() &&
							!Arrays.equals(paramTypes, factoryMethodToUse.getParameterTypes())) {
						if (ambiguousFactoryMethods == null) {
							ambiguousFactoryMethods = new LinkedHashSet<>();
							ambiguousFactoryMethods.add(factoryMethodToUse);
						}
						ambiguousFactoryMethods.add(candidate);
					}
				}
			}

			if (factoryMethodToUse == null) {
				if (causes != null) {
					UnsatisfiedDependencyException ex = causes.removeLast();
					for (Exception cause : causes) {
						this.beanFactory.onSuppressedException(cause);
					}
					throw ex;
				}
				List<String> argTypes = new ArrayList<>(minNrOfArgs);
				if (explicitArgs != null) {
					for (Object arg : explicitArgs) {
						argTypes.add(arg != null ? arg.getClass().getSimpleName() : "null");
					}
				}
				else if (resolvedValues != null) {
					Set<ValueHolder> valueHolders = new LinkedHashSet<>(resolvedValues.getArgumentCount());
					valueHolders.addAll(resolvedValues.getIndexedArgumentValues().values());
					valueHolders.addAll(resolvedValues.getGenericArgumentValues());
					for (ValueHolder value : valueHolders) {
						String argType = (value.getType() != null ? ClassUtils.getShortName(value.getType()) :
								(value.getValue() != null ? value.getValue().getClass().getSimpleName() : "null"));
						argTypes.add(argType);
					}
				}
				String argDesc = StringUtils.collectionToCommaDelimitedString(argTypes);
				throw new BeanCreationException(mbd.getResourceDescription(), beanName,
						"No matching factory method found: " +
						(mbd.getFactoryBeanName() != null ?
							"factory bean '" + mbd.getFactoryBeanName() + "'; " : "") +
						"factory method '" + mbd.getFactoryMethodName() + "(" + argDesc + ")'. " +
						"Check that a method with the specified name " +
						(minNrOfArgs > 0 ? "and arguments " : "") +
						"exists and that it is " +
						(isStatic ? "static" : "non-static") + ".");
			}
			else if (void.class == factoryMethodToUse.getReturnType()) {
				throw new BeanCreationException(mbd.getResourceDescription(), beanName,
						"Invalid factory method '" + mbd.getFactoryMethodName() +
						"': needs to have a non-void return type!");
			}
			else if (ambiguousFactoryMethods != null) {
				throw new BeanCreationException(mbd.getResourceDescription(), beanName,
						"Ambiguous factory method matches found in bean '" + beanName + "' " +
						"(hint: specify index/type/name arguments for simple parameters to avoid type ambiguities): " +
						ambiguousFactoryMethods);
			}

			if (explicitArgs == null && argsHolderToUse != null) {
				mbd.factoryMethodToIntrospect = factoryMethodToUse;
				argsHolderToUse.storeCache(mbd, factoryMethodToUse);
			}
		}

		Assert.state(argsToUse != null, "Unresolved factory method arguments");
		// 通过反射创建具体的bean对象
		bw.setBeanInstance(instantiate(beanName, mbd, factoryBean, factoryMethodToUse, argsToUse));
		return bw;
	}

	private Object instantiate(String beanName, RootBeanDefinition mbd,
			@Nullable Object factoryBean, Method factoryMethod, Object[] args) {

		try {
			if (System.getSecurityManager() != null) {
				return AccessController.doPrivileged((PrivilegedAction<Object>) () ->
						this.beanFactory.getInstantiationStrategy().instantiate(
								mbd, beanName, this.beanFactory, factoryBean, factoryMethod, args),
						this.beanFactory.getAccessControlContext());
			}
			else {
				return this.beanFactory.getInstantiationStrategy().instantiate(
						mbd, beanName, this.beanFactory, factoryBean, factoryMethod, args);
			}
		}
		catch (Throwable ex) {
			throw new BeanCreationException(mbd.getResourceDescription(), beanName,
					"Bean instantiation via factory method failed", ex);
		}
	}

	/**
	 * Resolve the constructor arguments for this bean into the resolvedValues object.
	 * This may involve looking up other beans.
	 * <p>This method is also used for handling invocations of static factory methods.
	 */
	private int resolveConstructorArguments(String beanName, RootBeanDefinition mbd, BeanWrapper bw,
			ConstructorArgumentValues cargs, ConstructorArgumentValues resolvedValues) {

		TypeConverter customConverter = this.beanFactory.getCustomTypeConverter();
		TypeConverter converter = (customConverter != null ? customConverter : bw);
		BeanDefinitionValueResolver valueResolver =
				new BeanDefinitionValueResolver(this.beanFactory, beanName, mbd, converter);

		int minNrOfArgs = cargs.getArgumentCount();

		for (Map.Entry<Integer, ConstructorArgumentValues.ValueHolder> entry : cargs.getIndexedArgumentValues().entrySet()) {
			int index = entry.getKey();
			if (index < 0) {
				throw new BeanCreationException(mbd.getResourceDescription(), beanName,
						"Invalid constructor argument index: " + index);
			}
			if (index > minNrOfArgs) {
				minNrOfArgs = index + 1;
			}
			ConstructorArgumentValues.ValueHolder valueHolder = entry.getValue();
			if (valueHolder.isConverted()) {
				resolvedValues.addIndexedArgumentValue(index, valueHolder);
			}
			else {
				Object resolvedValue =
						valueResolver.resolveValueIfNecessary("constructor argument", valueHolder.getValue());
				ConstructorArgumentValues.ValueHolder resolvedValueHolder =
						new ConstructorArgumentValues.ValueHolder(resolvedValue, valueHolder.getType(), valueHolder.getName());
				resolvedValueHolder.setSource(valueHolder);
				resolvedValues.addIndexedArgumentValue(index, resolvedValueHolder);
			}
		}

		for (ConstructorArgumentValues.ValueHolder valueHolder : cargs.getGenericArgumentValues()) {
			if (valueHolder.isConverted()) {
				resolvedValues.addGenericArgumentValue(valueHolder);
			}
			else {
				Object resolvedValue =
						valueResolver.resolveValueIfNecessary("constructor argument", valueHolder.getValue());
				ConstructorArgumentValues.ValueHolder resolvedValueHolder = new ConstructorArgumentValues.ValueHolder(
						resolvedValue, valueHolder.getType(), valueHolder.getName());
				resolvedValueHolder.setSource(valueHolder);
				resolvedValues.addGenericArgumentValue(resolvedValueHolder);
			}
		}

		return minNrOfArgs;
	}

	/**
	 * Create an array of arguments to invoke a constructor or factory method,
	 * given the resolved constructor argument values.
	 */
	private ArgumentsHolder createArgumentArray(
			String beanName, RootBeanDefinition mbd, @Nullable ConstructorArgumentValues resolvedValues,
			BeanWrapper bw, Class<?>[] paramTypes, @Nullable String[] paramNames, Executable executable,
			boolean autowiring, boolean fallback) throws UnsatisfiedDependencyException {

		TypeConverter customConverter = this.beanFactory.getCustomTypeConverter();
		TypeConverter converter = (customConverter != null ? customConverter : bw);

		ArgumentsHolder args = new ArgumentsHolder(paramTypes.length);
		Set<ConstructorArgumentValues.ValueHolder> usedValueHolders = new HashSet<>(paramTypes.length);
		Set<String> autowiredBeanNames = new LinkedHashSet<>(4);

		for (int paramIndex = 0; paramIndex < paramTypes.length; paramIndex++) {
			Class<?> paramType = paramTypes[paramIndex];
			String paramName = (paramNames != null ? paramNames[paramIndex] : "");
			// Try to find matching constructor argument value, either indexed or generic.
			ConstructorArgumentValues.ValueHolder valueHolder = null;
			if (resolvedValues != null) {
				valueHolder = resolvedValues.getArgumentValue(paramIndex, paramType, paramName, usedValueHolders);
				// If we couldn't find a direct match and are not supposed to autowire,
				// let's try the next generic, untyped argument value as fallback:
				// it could match after type conversion (for example, String -> int).
				if (valueHolder == null && (!autowiring || paramTypes.length == resolvedValues.getArgumentCount())) {
					valueHolder = resolvedValues.getGenericArgumentValue(null, null, usedValueHolders);
				}
			}
			if (valueHolder != null) {
				// We found a potential match - let's give it a try.
				// Do not consider the same value definition multiple times!
				usedValueHolders.add(valueHolder);
				Object originalValue = valueHolder.getValue();
				Object convertedValue;
				if (valueHolder.isConverted()) {
					convertedValue = valueHolder.getConvertedValue();
					args.preparedArguments[paramIndex] = convertedValue;
				}
				else {
					MethodParameter methodParam = MethodParameter.forExecutable(executable, paramIndex);
					try {
						convertedValue = converter.convertIfNecessary(originalValue, paramType, methodParam);
					}
					catch (TypeMismatchException ex) {
						throw new UnsatisfiedDependencyException(
								mbd.getResourceDescription(), beanName, new InjectionPoint(methodParam),
								"Could not convert argument value of type [" +
										ObjectUtils.nullSafeClassName(valueHolder.getValue()) +
										"] to required type [" + paramType.getName() + "]: " + ex.getMessage());
					}
					Object sourceHolder = valueHolder.getSource();
					if (sourceHolder instanceof ConstructorArgumentValues.ValueHolder) {
						Object sourceValue = ((ConstructorArgumentValues.ValueHolder) sourceHolder).getValue();
						args.resolveNecessary = true;
						args.preparedArguments[paramIndex] = sourceValue;
					}
				}
				args.arguments[paramIndex] = convertedValue;
				args.rawArguments[paramIndex] = originalValue;
			}
			else {
				MethodParameter methodParam = MethodParameter.forExecutable(executable, paramIndex);
				// No explicit match found: we're either supposed to autowire or
				// have to fail creating an argument array for the given constructor.
				if (!autowiring) {
					throw new UnsatisfiedDependencyException(
							mbd.getResourceDescription(), beanName, new InjectionPoint(methodParam),
							"Ambiguous argument values for parameter of type [" + paramType.getName() +
							"] - did you specify the correct bean references as arguments?");
				}
				try {
					Object autowiredArgument = resolveAutowiredArgument(
							methodParam, beanName, autowiredBeanNames, converter, fallback);
					args.rawArguments[paramIndex] = autowiredArgument;
					args.arguments[paramIndex] = autowiredArgument;
					args.preparedArguments[paramIndex] = new AutowiredArgumentMarker();
					args.resolveNecessary = true;
				}
				catch (BeansException ex) {
					throw new UnsatisfiedDependencyException(
							mbd.getResourceDescription(), beanName, new InjectionPoint(methodParam), ex);
				}
			}
		}

		for (String autowiredBeanName : autowiredBeanNames) {
			this.beanFactory.registerDependentBean(autowiredBeanName, beanName);
			if (logger.isDebugEnabled()) {
				logger.debug("Autowiring by type from bean name '" + beanName +
						"' via " + (executable instanceof Constructor ? "constructor" : "factory method") +
						" to bean named '" + autowiredBeanName + "'");
			}
		}

		return args;
	}

	/**
	 * Resolve the prepared arguments stored in the given bean definition.
	 */
	private Object[] resolvePreparedArguments(String beanName, RootBeanDefinition mbd, BeanWrapper bw,
			Executable executable, Object[] argsToResolve, boolean fallback) {

		TypeConverter customConverter = this.beanFactory.getCustomTypeConverter();
		TypeConverter converter = (customConverter != null ? customConverter : bw);
		BeanDefinitionValueResolver valueResolver =
				new BeanDefinitionValueResolver(this.beanFactory, beanName, mbd, converter);
		Class<?>[] paramTypes = executable.getParameterTypes();

		Object[] resolvedArgs = new Object[argsToResolve.length];
		for (int argIndex = 0; argIndex < argsToResolve.length; argIndex++) {
			Object argValue = argsToResolve[argIndex];
			MethodParameter methodParam = MethodParameter.forExecutable(executable, argIndex);
			GenericTypeResolver.resolveParameterType(methodParam, executable.getDeclaringClass());
			if (argValue instanceof AutowiredArgumentMarker) {
				argValue = resolveAutowiredArgument(methodParam, beanName, null, converter, fallback);
			}
			else if (argValue instanceof BeanMetadataElement) {
				argValue = valueResolver.resolveValueIfNecessary("constructor argument", argValue);
			}
			else if (argValue instanceof String) {
				argValue = this.beanFactory.evaluateBeanDefinitionString((String) argValue, mbd);
			}
			Class<?> paramType = paramTypes[argIndex];
			try {
				resolvedArgs[argIndex] = converter.convertIfNecessary(argValue, paramType, methodParam);
			}
			catch (TypeMismatchException ex) {
				throw new UnsatisfiedDependencyException(
						mbd.getResourceDescription(), beanName, new InjectionPoint(methodParam),
						"Could not convert argument value of type [" + ObjectUtils.nullSafeClassName(argValue) +
						"] to required type [" + paramType.getName() + "]: " + ex.getMessage());
			}
		}
		return resolvedArgs;
	}

	protected Constructor<?> getUserDeclaredConstructor(Constructor<?> constructor) {
		Class<?> declaringClass = constructor.getDeclaringClass();
		Class<?> userClass = ClassUtils.getUserClass(declaringClass);
		if (userClass != declaringClass) {
			try {
				return userClass.getDeclaredConstructor(constructor.getParameterTypes());
			}
			catch (NoSuchMethodException ex) {
				// No equivalent constructor on user class (superclass)...
				// Let's proceed with the given constructor as we usually would.
			}
		}
		return constructor;
	}

	/**
	 * Template method for resolving the specified argument which is supposed to be autowired.
	 */
	@Nullable
	protected Object resolveAutowiredArgument(MethodParameter param, String beanName,
			@Nullable Set<String> autowiredBeanNames, TypeConverter typeConverter, boolean fallback) {

		Class<?> paramType = param.getParameterType();
		if (InjectionPoint.class.isAssignableFrom(paramType)) {
			InjectionPoint injectionPoint = currentInjectionPoint.get();
			if (injectionPoint == null) {
				throw new IllegalStateException("No current InjectionPoint available for " + param);
			}
			return injectionPoint;
		}
		try {
			return this.beanFactory.resolveDependency(
					new DependencyDescriptor(param, true), beanName, autowiredBeanNames, typeConverter);
		}
		catch (NoUniqueBeanDefinitionException ex) {
			throw ex;
		}
		catch (NoSuchBeanDefinitionException ex) {
			if (fallback) {
				// Single constructor or factory method -> let's return an empty array/collection
				// for e.g. a vararg or a non-null List/Set/Map parameter.
				if (paramType.isArray()) {
					return Array.newInstance(paramType.getComponentType(), 0);
				}
				else if (CollectionFactory.isApproximableCollectionType(paramType)) {
					return CollectionFactory.createCollection(paramType, 0);
				}
				else if (CollectionFactory.isApproximableMapType(paramType)) {
					return CollectionFactory.createMap(paramType, 0);
				}
			}
			throw ex;
		}
	}

	static InjectionPoint setCurrentInjectionPoint(@Nullable InjectionPoint injectionPoint) {
		InjectionPoint old = currentInjectionPoint.get();
		if (injectionPoint != null) {
			currentInjectionPoint.set(injectionPoint);
		}
		else {
			currentInjectionPoint.remove();
		}
		return old;
	}


	/**
	 * Private inner class for holding argument combinations.
	 */
	private static class ArgumentsHolder {

		public final Object[] rawArguments;

		public final Object[] arguments;

		public final Object[] preparedArguments;

		public boolean resolveNecessary = false;

		public ArgumentsHolder(int size) {
			this.rawArguments = new Object[size];
			this.arguments = new Object[size];
			this.preparedArguments = new Object[size];
		}

		public ArgumentsHolder(Object[] args) {
			this.rawArguments = args;
			this.arguments = args;
			this.preparedArguments = args;
		}

		public int getTypeDifferenceWeight(Class<?>[] paramTypes) {
			// If valid arguments found, determine type difference weight.
			// Try type difference weight on both the converted arguments and
			// the raw arguments. If the raw weight is better, use it.
			// Decrease raw weight by 1024 to prefer it over equal converted weight.
			int typeDiffWeight = MethodInvoker.getTypeDifferenceWeight(paramTypes, this.arguments);
			int rawTypeDiffWeight = MethodInvoker.getTypeDifferenceWeight(paramTypes, this.rawArguments) - 1024;
			return (rawTypeDiffWeight < typeDiffWeight ? rawTypeDiffWeight : typeDiffWeight);
		}

		public int getAssignabilityWeight(Class<?>[] paramTypes) {
			for (int i = 0; i < paramTypes.length; i++) {
				if (!ClassUtils.isAssignableValue(paramTypes[i], this.arguments[i])) {
					return Integer.MAX_VALUE;
				}
			}
			for (int i = 0; i < paramTypes.length; i++) {
				if (!ClassUtils.isAssignableValue(paramTypes[i], this.rawArguments[i])) {
					return Integer.MAX_VALUE - 512;
				}
			}
			return Integer.MAX_VALUE - 1024;
		}

		public void storeCache(RootBeanDefinition mbd, Executable constructorOrFactoryMethod) {
			synchronized (mbd.constructorArgumentLock) {
				mbd.resolvedConstructorOrFactoryMethod = constructorOrFactoryMethod;
				mbd.constructorArgumentsResolved = true;
				if (this.resolveNecessary) {
					mbd.preparedConstructorArguments = this.preparedArguments;
				}
				else {
					mbd.resolvedConstructorArguments = this.arguments;
				}
			}
		}
	}


	/**
	 * Marker for autowired arguments in a cached argument array.
 	 */
	private static class AutowiredArgumentMarker {
	}


	/**
	 * Delegate for checking Java 6's {@link ConstructorProperties} annotation.
	 */
	private static class ConstructorPropertiesChecker {

		@Nullable
		public static String[] evaluate(Constructor<?> candidate, int paramCount) {
			ConstructorProperties cp = candidate.getAnnotation(ConstructorProperties.class);
			if (cp != null) {
				String[] names = cp.value();
				if (names.length != paramCount) {
					throw new IllegalStateException("Constructor annotated with @ConstructorProperties but not " +
							"corresponding to actual number of parameters (" + paramCount + "): " + candidate);
				}
				return names;
			}
			else {
				return null;
			}
		}
	}

}
