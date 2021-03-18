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

import org.springframework.beans.factory.*;
import org.springframework.beans.factory.config.SingletonBeanRegistry;
import org.springframework.core.SimpleAliasRegistry;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Generic registry for shared bean instances, implementing the
 * {@link org.springframework.beans.factory.config.SingletonBeanRegistry}.
 * Allows for registering singleton instances that should be shared
 * for all callers of the registry, to be obtained via bean name.
 *
 * <p>Also supports registration of
 * {@link org.springframework.beans.factory.DisposableBean} instances,
 * (which might or might not correspond to registered singletons),
 * to be destroyed on shutdown of the registry. Dependencies between
 * beans can be registered to enforce an appropriate shutdown order.
 *
 * <p>This class mainly serves as base class for
 * {@link org.springframework.beans.factory.BeanFactory} implementations,
 * factoring out the common management of singleton bean instances. Note that
 * the {@link org.springframework.beans.factory.config.ConfigurableBeanFactory}
 * interface extends the {@link SingletonBeanRegistry} interface.
 *
 * <p>Note that this class assumes neither a bean definition concept
 * nor a specific creation process for bean instances, in contrast to
 * {@link AbstractBeanFactory} and {@link DefaultListableBeanFactory}
 * (which inherit from it). Can alternatively also be used as a nested
 * helper to delegate to.
 *
 * @author Juergen Hoeller
 * @since 2.0
 * @see #registerSingleton
 * @see #registerDisposableBean
 * @see org.springframework.beans.factory.DisposableBean
 * @see org.springframework.beans.factory.config.ConfigurableBeanFactory
 */
public class DefaultSingletonBeanRegistry extends SimpleAliasRegistry implements SingletonBeanRegistry {
	/**
	 * Cache of singleton objects: bean name to bean instance.
	 * 一级缓存：单例对象的缓存，也被称作单例缓存池
	 */
	private final Map<String, Object> singletonObjects = new ConcurrentHashMap<>(256);
	/**
	 * Cache of early singleton objects: bean name to bean instance.
	 * 二级缓存：提前曝光的单例对象的缓存，用于检测循环引用
	 */
	private final Map<String, Object> earlySingletonObjects = new HashMap<>(16);
	/**
	 * Cache of singleton factories: bean name to ObjectFactory.
	 * 三级缓存：保存对单实例bean的包装对象
	 */
	private final Map<String, ObjectFactory<?>> singletonFactories = new HashMap<>(16);

	/**
	 * 按照注册顺序所保存的已经注册的单例对象的名称
	 */
	private final Set<String> registeredSingletons = new LinkedHashSet<>(256);

	/**
	 * 创建单实例Bean时，用于保证bean创建的唯一性。保存当前正在创建中的bean的名称.
	 */
	private final Set<String> singletonsCurrentlyInCreation =
			Collections.newSetFromMap(new ConcurrentHashMap<>(16));
	/**
	 * 创建单实例Bean时排除检查的beanName的集合
	 */
	private final Set<String> inCreationCheckExclusions =
			Collections.newSetFromMap(new ConcurrentHashMap<>(16));

	/** List of suppressed Exceptions, available for associating related causes. */
	@Nullable
	private Set<Exception> suppressedExceptions;

	/** Flag that indicates whether we're currently within destroySingletons. */
	private boolean singletonsCurrentlyInDestruction = false;

	/** Disposable bean instances: bean name to disposable instance. */
	private final Map<String, Object> disposableBeans = new LinkedHashMap<>();

	/** Map between containing bean names: bean name to Set of bean names that the bean contains. */
	private final Map<String, Set<String>> containedBeanMap = new ConcurrentHashMap<>(16);

	/**
	 * 用来保存某个bean所以来的其他bean集合。key: beanName，value: beanName所依赖的其他所有bean的名称
	 */
	private final Map<String, Set<String>> dependentBeanMap = new ConcurrentHashMap<>(64);

	/** Map between depending bean names: bean name to Set of bean names for the bean's dependencies. */
	private final Map<String, Set<String>> dependenciesForBeanMap = new ConcurrentHashMap<>(64);


	@Override
	public void registerSingleton(String beanName, Object singletonObject) throws IllegalStateException {
		Assert.notNull(beanName, "Bean name must not be null");
		Assert.notNull(singletonObject, "Singleton object must not be null");
		synchronized (this.singletonObjects) {
			Object oldObject = this.singletonObjects.get(beanName);
			if (oldObject != null) {
				throw new IllegalStateException("Could not register object [" + singletonObject +
						"] under bean name '" + beanName + "': there is already object [" + oldObject + "] bound");
			}
			addSingleton(beanName, singletonObject);
		}
	}

	/**
	 * Add the given singleton object to the singleton cache of this factory.
	 * <p>To be called for eager registration of singletons.
	 * @param beanName the name of the bean
	 * @param singletonObject the singleton object
	 */
	protected void addSingleton(String beanName, Object singletonObject) {
		synchronized (this.singletonObjects) {
			// 将创建好的单实例bean放入到单例缓存池中
			this.singletonObjects.put(beanName, singletonObject);

			// 从三级缓存中删除
			this.singletonFactories.remove(beanName);

			// 从二级缓存中删除(早期对象：已经实例化，但是未完成属性赋值的对象)
			this.earlySingletonObjects.remove(beanName);

			// 保存到已注册单实例Bean名称集合中
			this.registeredSingletons.add(beanName);
		}
	}

	/**
	 * Add the given singleton factory for building the specified singleton
	 * if necessary.
	 * <p>To be called for eager registration of singletons, e.g. to be able to
	 * resolve circular references.
	 * @param beanName the name of the bean
	 * @param singletonFactory the factory for the singleton object
	 */
	protected void addSingletonFactory(String beanName, ObjectFactory<?> singletonFactory) {
		Assert.notNull(singletonFactory, "Singleton factory must not be null");
		synchronized (this.singletonObjects) {
			// 如果当前的单实例缓存池中还没有beanName对应的单实例bean
			if (!this.singletonObjects.containsKey(beanName)) {
				// 将当前beanName对应的ObjectFactory放入到三级缓存singletonFactories中
				this.singletonFactories.put(beanName, singletonFactory);

				// 从早期的单例对象缓存中移除beanName对应的bean实例
				this.earlySingletonObjects.remove(beanName);

				// 将当前的beanName保存到已经注册的bean对应的Set集合中，标识其已经注册过
				this.registeredSingletons.add(beanName);
			}
		}
	}

	@Override
	@Nullable
	public Object getSingleton(String beanName) {
		// 从缓存中获取单实例Bean
		return getSingleton(beanName, true);
	}

	/**
	 * Return the (raw) singleton object registered under the given name.
	 * <p>Checks already instantiated singletons and also allows for an early
	 * reference to a currently created singleton (resolving a circular reference).
	 * @param beanName the name of the bean to look for
	 * @param allowEarlyReference whether early references should be created or not
	 * @return the registered singleton object, or {@code null} if none found
	 */
	////////////////////////////////////////////////////////////////////////////
	//   ******该段代码是 Spring 解决循环引用的核心代码******
	//
	//   解决循环引用逻辑：使用构造函数创建一个 “不完整” 的 bean 实例（之所以说不完整，是因为此时该 bean 实例还未初始化），
	//      并且提前曝光该 bean 实例的 ObjectFactory（提前曝光就是将 ObjectFactory 放到 singletonFactories 缓存），
	//      通过 ObjectFactory 我们可以拿到该 bean 实例的引用，如果出现循环引用，我们可以通过缓存中的 ObjectFactory 来拿到 bean 实例，
	//      从而避免出现循环引用导致的死循环。
	//
	//    这边通过缓存中的 ObjectFactory 拿到的 bean 实例虽然拿到的是 “不完整” 的 bean 实例，但是由于是单例，所以后续初始化完成后，
	//      该 bean 实例的引用地址并不会变，所以最终我们看到的还是完整 bean 实例。


	//	另外这个代码块中引进了4个重要缓存：
	//		singletonObjects 缓存：beanName -> 单例 bean 对象。
	//		earlySingletonObjects 缓存：beanName -> 单例 bean 对象，该缓存存放的是早期单例 bean 对象，可以理解成还未进行属性填充、初始化。
	//		singletonFactories 缓存：beanName -> ObjectFactory。
	//		singletonsCurrentlyInCreation 缓存：当前正在创建单例 bean 对象的 beanName 集合。
	//		singletonObjects、earlySingletonObjects、singletonFactories 在这边构成了一个类似于 “三级缓存” 的概念。
	////////////////////////////////////////////////////////////////////////////
	/**
	 * 注意：
	 * （1）通过 setter 注入方式产生的循环引用是可以通过以上方案解决的。
	 * （2）构造器注入方式产生的循环引用无法解决，因为无法实例化出 early singleton bean 实例。
	 * （3）非单例模式的循环引用也无法解决，因为 Spring 框架不会缓存非单例的 bean 实例。
	 */
	@Nullable
	protected Object getSingleton(String beanName, boolean allowEarlyReference) {
		// 根据beanName从单实例对象缓存中获取单例对象(singletonObjects为一个ConcurrentHashMap，就是用来保存所有的单实例Bean的,
		//   key:beanName value:beanInstance) 相当于一级缓存
		Object singletonObject = this.singletonObjects.get(beanName);
		// 如果缓存中不存在，而且beanName对应的单实例Bean正在创建中.
		if (singletonObject == null && isSingletonCurrentlyInCreation(beanName)) {
			// 加锁操作.
			synchronized (this.singletonObjects) {
				// 从早期单实例对象缓存中获取单例对象（之所以称为单实例早期对象，
				//   是因为earlySingletonObjects里面的对象都是通过提前曝光的ObjectFactory创建出来的，还未进行属性的填充）
				singletonObject = this.earlySingletonObjects.get(beanName);


				// 如果早期单实例对象缓存中没有，而且允许创建早期单实例对象引用
				if (singletonObject == null && allowEarlyReference) {
					// 则从单例工厂缓存中获取BeanName对应的单例工厂.
					ObjectFactory<?> singletonFactory = this.singletonFactories.get(beanName);
					if (singletonFactory != null) {
						// 如果存在着单例对象工厂，则通过工厂创建一个单例对象，
						// 调用的是：addSingletonFactory(beanName, () -> getEarlyBeanReference(beanName, mbd, bean))中的拉姆达表达式
						singletonObject = singletonFactory.getObject();
						// 将通过单例对象工厂创建的单例对象放入到早期单例对象缓存中，这个早期对象指的是一个空的未完成属性赋值和初始化的对象。
						this.earlySingletonObjects.put(beanName, singletonObject);
						// 移除该beanName对应的单例对象工厂，因为该单例工厂已经创建了一个实例对象，并且放入到earlySingletonObjects缓存中了，
						//  所以，后续通过beanName获取单例对象，可以通过earlySingletonObjects缓存获取到，不需要再用到该单例工厂.
						this.singletonFactories.remove(beanName);
					}
				}
			}
		}
		return singletonObject;
	}

	/**
	 * Return the (raw) singleton object registered under the given name,
	 * creating and registering a new one if none registered yet.
	 * @param beanName the name of the bean
	 * @param singletonFactory the ObjectFactory to lazily create the singleton
	 * with, if necessary
	 * @return the registered singleton object
	 */
	public Object getSingleton(String beanName, ObjectFactory<?> singletonFactory) {
		// 校验bean的名称，不能为空
		Assert.notNull(beanName, "Bean name must not be null");

		synchronized (this.singletonObjects) {

			// 首先从单例缓存池singletonObjects【Map<String,Object>】中尝试获取单实例bean
			Object singletonObject = this.singletonObjects.get(beanName);

			// 如果未获取到，则通过如下的过程去创建单实例bean
			if (singletonObject == null) {
				/** 如果当前bean工厂中的实例bean正在被销毁，则不允许执行bean的创建过程 */
				if (this.singletonsCurrentlyInDestruction) {
					throw new BeanCreationNotAllowedException(beanName,
							"Singleton bean creation not allowed while singletons of this factory are in destruction " +
							"(Do not request a bean from a BeanFactory in a destroy method implementation!)");
				}
				if (logger.isDebugEnabled()) {
					logger.debug("Creating shared instance of singleton bean '" + beanName + "'");
				}

				/** 创建单实例bean之前的检查，根据名称校验当前的单实例bean是否正在创建中. */
				beforeSingletonCreation(beanName);

				boolean newSingleton = false;
				/** 初始化用来保存异常信息的Set集合 */
				boolean recordSuppressedExceptions = (this.suppressedExceptions == null);
				if (recordSuppressedExceptions) {
					this.suppressedExceptions = new LinkedHashSet<>();
				}
				try {
					/** 回调ObjectFactory的getObject方法，进行单实例Bean的创建. */
					singletonObject = singletonFactory.getObject();
					/** 标注单实例bean创建成功 */
					newSingleton = true;
				}
				catch (IllegalStateException ex) {
					// Has the singleton object implicitly appeared in the meantime ->
					// if yes, proceed with it since the exception indicates that state.
					singletonObject = this.singletonObjects.get(beanName);
					if (singletonObject == null) {
						throw ex;
					}
				}
				catch (BeanCreationException ex) {
					if (recordSuppressedExceptions) {
						for (Exception suppressedException : this.suppressedExceptions) {
							ex.addRelatedCause(suppressedException);
						}
					}
					throw ex;
				}
				finally {
					if (recordSuppressedExceptions) {
						this.suppressedExceptions = null;
					}
					/** 创建完成之后，将bean名称从检查列表中删除. */
					afterSingletonCreation(beanName);
				}
				if (newSingleton) {
					/** 如果bean创建成功，将其加入单实例bean的map中 */
					addSingleton(beanName, singletonObject);
				}
			}
			return singletonObject;
		}
	}

	/**
	 * Register an Exception that happened to get suppressed during the creation of a
	 * singleton bean instance, e.g. a temporary circular reference resolution problem.
	 * @param ex the Exception to register
	 */
	protected void onSuppressedException(Exception ex) {
		synchronized (this.singletonObjects) {
			if (this.suppressedExceptions != null) {
				this.suppressedExceptions.add(ex);
			}
		}
	}

	/**
	 * Remove the bean with the given name from the singleton cache of this factory,
	 * to be able to clean up eager registration of a singleton if creation failed.
	 * @param beanName the name of the bean
	 * @see #getSingletonMutex()
	 */
	protected void removeSingleton(String beanName) {
		synchronized (this.singletonObjects) {
			this.singletonObjects.remove(beanName);
			this.singletonFactories.remove(beanName);
			this.earlySingletonObjects.remove(beanName);
			this.registeredSingletons.remove(beanName);
		}
	}

	@Override
	public boolean containsSingleton(String beanName) {
		return this.singletonObjects.containsKey(beanName);
	}

	@Override
	public String[] getSingletonNames() {
		synchronized (this.singletonObjects) {
			return StringUtils.toStringArray(this.registeredSingletons);
		}
	}

	@Override
	public int getSingletonCount() {
		synchronized (this.singletonObjects) {
			return this.registeredSingletons.size();
		}
	}


	public void setCurrentlyInCreation(String beanName, boolean inCreation) {
		Assert.notNull(beanName, "Bean name must not be null");
		if (!inCreation) {
			this.inCreationCheckExclusions.add(beanName);
		}
		else {
			this.inCreationCheckExclusions.remove(beanName);
		}
	}

	public boolean isCurrentlyInCreation(String beanName) {
		Assert.notNull(beanName, "Bean name must not be null");
		return (!this.inCreationCheckExclusions.contains(beanName) && isActuallyInCreation(beanName));
	}

	protected boolean isActuallyInCreation(String beanName) {
		return isSingletonCurrentlyInCreation(beanName);
	}

	/**
	 * Return whether the specified singleton bean is currently in creation
	 * (within the entire factory).
	 * @param beanName the name of the bean
	 */
	public boolean isSingletonCurrentlyInCreation(String beanName) {
		return this.singletonsCurrentlyInCreation.contains(beanName);
	}

	/**
	 * Callback before singleton creation.
	 * <p>The default implementation register the singleton as currently in creation.
	 * @param beanName the name of the singleton about to be created
	 * @see #isSingletonCurrentlyInCreation
	 */
	protected void beforeSingletonCreation(String beanName) {
		if (!this.inCreationCheckExclusions.contains(beanName) && !this.singletonsCurrentlyInCreation.add(beanName)) {
			throw new BeanCurrentlyInCreationException(beanName);
		}
	}

	/**
	 * Callback after singleton creation.
	 * <p>The default implementation marks the singleton as not in creation anymore.
	 * @param beanName the name of the singleton that has been created
	 * @see #isSingletonCurrentlyInCreation
	 */
	protected void afterSingletonCreation(String beanName) {
		if (!this.inCreationCheckExclusions.contains(beanName) && !this.singletonsCurrentlyInCreation.remove(beanName)) {
			throw new IllegalStateException("Singleton '" + beanName + "' isn't currently in creation");
		}
	}


	/**
	 * Add the given bean to the list of disposable beans in this registry.
	 * <p>Disposable beans usually correspond to registered singletons,
	 * matching the bean name but potentially being a different instance
	 * (for example, a DisposableBean adapter for a singleton that does not
	 * naturally implement Spring's DisposableBean interface).
	 * @param beanName the name of the bean
	 * @param bean the bean instance
	 */
	public void registerDisposableBean(String beanName, DisposableBean bean) {
		synchronized (this.disposableBeans) {
			this.disposableBeans.put(beanName, bean);
		}
	}

	/**
	 * Register a containment relationship between two beans,
	 * e.g. between an inner bean and its containing outer bean.
	 * <p>Also registers the containing bean as dependent on the contained bean
	 * in terms of destruction order.
	 * @param containedBeanName the name of the contained (inner) bean
	 * @param containingBeanName the name of the containing (outer) bean
	 * @see #registerDependentBean
	 */
	public void registerContainedBean(String containedBeanName, String containingBeanName) {
		synchronized (this.containedBeanMap) {
			Set<String> containedBeans =
					this.containedBeanMap.computeIfAbsent(containingBeanName, k -> new LinkedHashSet<>(8));
			if (!containedBeans.add(containedBeanName)) {
				return;
			}
		}
		registerDependentBean(containedBeanName, containingBeanName);
	}

	/**
	 * Register a dependent bean for the given bean,
	 * to be destroyed before the given bean is destroyed.
	 * @param beanName 被依赖的bean的名称，即：dependsOn中的bean的名称
	 * @param dependentBeanName 待创建的bean的名称，原始传入待创建的beanName
	 */
	public void registerDependentBean(String beanName, String dependentBeanName) {
		// 解析被依赖的bean的真正beanName
		String canonicalName = canonicalName(beanName);

		/**
		 * 比如将要创建test对应的bean:
		 *
		 * <bean name="test" class="com.wb.spring.test.ClassA" depends-on="book,ball,house"/>
		 * <bean name="book" class="com.wb.spring.test.Book" depends-on="paper"/>
		 */
		// 维修被依赖的bean对应的其他依赖bean集合。如上相当于解析出其他依赖book的beanName列表
		synchronized (this.dependentBeanMap) {
			// 将当前bean所依赖的beanName，放入到依赖的beanMap中。
			// dependentBeanMap： key:当前的bean的名称 value: Set<String>，表示的是当前bean所依赖的其他bean的名称.
			Set<String> dependentBeans =
					// 如果canonicalName的值为空，则将new LinkedHashSet(8)放入到dependentBeanMap中.
					this.dependentBeanMap.computeIfAbsent(canonicalName, k -> new LinkedHashSet<>(8));
			if (!dependentBeans.add(dependentBeanName)) {
				return;
			}
		}
		// 维修原始传入的bean所依赖的其他bean的集合。如上相当于解析出test所依赖的其他beanName集合.
		synchronized (this.dependenciesForBeanMap) {
			Set<String> dependenciesForBean =
					this.dependenciesForBeanMap.computeIfAbsent(dependentBeanName, k -> new LinkedHashSet<>(8));
			dependenciesForBean.add(canonicalName);
		}
	}

	/**
	 * 用来检测bean是否存在着循环依赖的关系
	 * @param beanName 将要创建的bean的名称
	 * @param dependentBeanName beanName所依赖的其他bean的名称
	 * @return
	 */
	protected boolean isDependent(String beanName, String dependentBeanName) {
		synchronized (this.dependentBeanMap) {
			return isDependent(beanName, dependentBeanName, null);
		}
	}

	/**
	 *
	 * @param beanName 将要创建的bean的名称
	 * @param dependentBeanName beanName所以来的其他bean的名称
	 * @param alreadySeen null
	 * @return
	 */
	private boolean isDependent(String beanName, String dependentBeanName, @Nullable Set<String> alreadySeen) {
		/** 递归的出口。如果已经监测过的集合中包括将要监测的beanName，直接返回无循环依赖 */
		if (alreadySeen != null && alreadySeen.contains(beanName)) {
			return false;
		}

		/** 解析将要创建的bean的真正名称，包括别名的解析 */
		String canonicalName = canonicalName(beanName);

		/**
		 * 从依赖关系中找出当前将要创建的bean所依赖的其他所有beanName的集合，例如下列的循环依赖关系：
		 * <bean name="classA" class="com.wb.spring.finishBeanFactoryInitialization.domain.ClassA" depends-on="classB"></bean>
		 * <bean name="classB" class="com.wb.spring.finishBeanFactoryInitialization.domain.ClassB" depends-on="classA"></bean>
		 * 会抛出循环依赖的异常：
		 *    throw new BeanCreationException(mbd.getResourceDescription(), beanName,"Circular depends-on relationship between '" + beanName + "' and '" + dep + "'");
		 * 从容器中获取classA对应的bean实例时，会解析到depends-on="classB"，然后会通过registerDependentBean方法将这个依赖关系维护到依赖关系的map集合中，集合如下：
		 *    Map<String,Set<String>> maps = [<classB,[classA]>]; 然后再通过紧跟着的getBean(dep)方法去获取classB的实例。这个时候，又会解析出depends-on="classA"，
		 * 然后根据classB去获取依赖关系，获取到的Set集合中有classA，然后判断到当前的classB所依赖的classA在该集合中，所以就会抛出循环依赖的异常.
		 *
		 */
		Set<String> dependentBeans = this.dependentBeanMap.get(canonicalName);

		// 如果当前的bean没有依赖其他任何bean，直接返回false，表示没有循环依赖关系.
		if (dependentBeans == null) {
			return false;
		}

		/** 监测被依赖的对象是否又依赖了原对象，即存在循环依赖 */
		if (dependentBeans.contains(dependentBeanName)) {
			return true;
		}

		/** 否则如果依赖层次比较深，则使用递归去依次监测依赖关系，判断是否有循环依赖 */
		for (String transitiveDependency : dependentBeans) {
			if (alreadySeen == null) {
				alreadySeen = new HashSet<>();
			}
			alreadySeen.add(beanName);
			if (isDependent(transitiveDependency, dependentBeanName, alreadySeen)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Determine whether a dependent bean has been registered for the given name.
	 * @param beanName the name of the bean to check
	 */
	protected boolean hasDependentBean(String beanName) {
		return this.dependentBeanMap.containsKey(beanName);
	}

	/**
	 * Return the names of all beans which depend on the specified bean, if any.
	 * @param beanName the name of the bean
	 * @return the array of dependent bean names, or an empty array if none
	 */
	public String[] getDependentBeans(String beanName) {
		Set<String> dependentBeans = this.dependentBeanMap.get(beanName);
		if (dependentBeans == null) {
			return new String[0];
		}
		synchronized (this.dependentBeanMap) {
			return StringUtils.toStringArray(dependentBeans);
		}
	}

	/**
	 * Return the names of all beans that the specified bean depends on, if any.
	 * @param beanName the name of the bean
	 * @return the array of names of beans which the bean depends on,
	 * or an empty array if none
	 */
	public String[] getDependenciesForBean(String beanName) {
		Set<String> dependenciesForBean = this.dependenciesForBeanMap.get(beanName);
		if (dependenciesForBean == null) {
			return new String[0];
		}
		synchronized (this.dependenciesForBeanMap) {
			return StringUtils.toStringArray(dependenciesForBean);
		}
	}

	public void destroySingletons() {
		if (logger.isTraceEnabled()) {
			logger.trace("Destroying singletons in " + this);
		}
		synchronized (this.singletonObjects) {
			this.singletonsCurrentlyInDestruction = true;
		}

		String[] disposableBeanNames;
		synchronized (this.disposableBeans) {
			disposableBeanNames = StringUtils.toStringArray(this.disposableBeans.keySet());
		}
		for (int i = disposableBeanNames.length - 1; i >= 0; i--) {
			destroySingleton(disposableBeanNames[i]);
		}

		this.containedBeanMap.clear();
		this.dependentBeanMap.clear();
		this.dependenciesForBeanMap.clear();

		clearSingletonCache();
	}

	/**
	 * Clear all cached singleton instances in this registry.
	 * @since 4.3.15
	 */
	protected void clearSingletonCache() {
		synchronized (this.singletonObjects) {
			this.singletonObjects.clear();
			this.singletonFactories.clear();
			this.earlySingletonObjects.clear();
			this.registeredSingletons.clear();
			this.singletonsCurrentlyInDestruction = false;
		}
	}

	/**
	 * Destroy the given bean. Delegates to {@code destroyBean}
	 * if a corresponding disposable bean instance is found.
	 * @param beanName the name of the bean
	 * @see #destroyBean
	 */
	public void destroySingleton(String beanName) {
		// Remove a registered singleton of the given name, if any.
		removeSingleton(beanName);

		// Destroy the corresponding DisposableBean instance.
		DisposableBean disposableBean;
		synchronized (this.disposableBeans) {
			disposableBean = (DisposableBean) this.disposableBeans.remove(beanName);
		}
		destroyBean(beanName, disposableBean);
	}

	/**
	 * Destroy the given bean. Must destroy beans that depend on the given
	 * bean before the bean itself. Should not throw any exceptions.
	 * @param beanName the name of the bean
	 * @param bean the bean instance to destroy
	 */
	protected void destroyBean(String beanName, @Nullable DisposableBean bean) {
		// Trigger destruction of dependent beans first...
		Set<String> dependencies;
		synchronized (this.dependentBeanMap) {
			// Within full synchronization in order to guarantee a disconnected Set
			dependencies = this.dependentBeanMap.remove(beanName);
		}
		if (dependencies != null) {
			if (logger.isTraceEnabled()) {
				logger.trace("Retrieved dependent beans for bean '" + beanName + "': " + dependencies);
			}
			for (String dependentBeanName : dependencies) {
				destroySingleton(dependentBeanName);
			}
		}

		// Actually destroy the bean now...
		if (bean != null) {
			try {
				bean.destroy();
			}
			catch (Throwable ex) {
				if (logger.isWarnEnabled()) {
					logger.warn("Destruction of bean with name '" + beanName + "' threw an exception", ex);
				}
			}
		}

		// Trigger destruction of contained beans...
		Set<String> containedBeans;
		synchronized (this.containedBeanMap) {
			// Within full synchronization in order to guarantee a disconnected Set
			containedBeans = this.containedBeanMap.remove(beanName);
		}
		if (containedBeans != null) {
			for (String containedBeanName : containedBeans) {
				destroySingleton(containedBeanName);
			}
		}

		// Remove destroyed bean from other beans' dependencies.
		synchronized (this.dependentBeanMap) {
			for (Iterator<Map.Entry<String, Set<String>>> it = this.dependentBeanMap.entrySet().iterator(); it.hasNext();) {
				Map.Entry<String, Set<String>> entry = it.next();
				Set<String> dependenciesToClean = entry.getValue();
				dependenciesToClean.remove(beanName);
				if (dependenciesToClean.isEmpty()) {
					it.remove();
				}
			}
		}

		// Remove destroyed bean's prepared dependency information.
		this.dependenciesForBeanMap.remove(beanName);
	}

	/**
	 * Exposes the singleton mutex to subclasses and external collaborators.
	 * <p>Subclasses should synchronize on the given Object if they perform
	 * any sort of extended singleton creation phase. In particular, subclasses
	 * should <i>not</i> have their own mutexes involved in singleton creation,
	 * to avoid the potential for deadlocks in lazy-init situations.
	 */
	public final Object getSingletonMutex() {
		return this.singletonObjects;
	}

}
