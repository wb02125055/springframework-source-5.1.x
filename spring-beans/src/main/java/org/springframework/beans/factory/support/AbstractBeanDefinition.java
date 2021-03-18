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

import java.lang.reflect.Constructor;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;

import org.springframework.beans.BeanMetadataAttributeAccessor;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConstructorArgumentValues;
import org.springframework.core.io.DescriptiveResource;
import org.springframework.core.io.Resource;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

/**
 * Base class for concrete, full-fledged {@link BeanDefinition} classes,
 * factoring out common properties of {@link GenericBeanDefinition},
 * {@link RootBeanDefinition}, and {@link ChildBeanDefinition}.
 *
 * <p>The autowire constants match the ones defined in the
 * {@link org.springframework.beans.factory.config.AutowireCapableBeanFactory}
 * interface.
 *
 * @author Rod Johnson
 * @author Juergen Hoeller
 * @author Rob Harrop
 * @author Mark Fisher
 * @see GenericBeanDefinition
 * @see RootBeanDefinition
 * @see ChildBeanDefinition
 */
@SuppressWarnings("serial")
public abstract class AbstractBeanDefinition extends BeanMetadataAttributeAccessor
		implements BeanDefinition, Cloneable {

	/**
	 * Constant for the default scope name: {@code ""}, equivalent to singleton
	 * status unless overridden from a parent bean definition (if applicable).
	 * Bean默认的scope
	 */
	public static final String SCOPE_DEFAULT = "";

	/**
	 * Constant that indicates no external autowiring at all.
	 * @see #setAutowireMode
	 * Bean的注入模式：默认的Bean注入模式，使用这种注入模式时，在依赖其他Bean时，需要使用注解@Autowired进行Bean的注入
	 */
	public static final int AUTOWIRE_NO = AutowireCapableBeanFactory.AUTOWIRE_NO;

	/**
	 * Constant that indicates autowiring bean properties by name.
	 * @see #setAutowireMode
	 * Bean的注入模式：按照Bean的名称进行注入
	 */
	public static final int AUTOWIRE_BY_NAME = AutowireCapableBeanFactory.AUTOWIRE_BY_NAME;

	/**
	 * Constant that indicates autowiring bean properties by type.
	 * @see #setAutowireMode
	 * Bean的注入模式：按照Bean的类型进行注入
	 */
	public static final int AUTOWIRE_BY_TYPE = AutowireCapableBeanFactory.AUTOWIRE_BY_TYPE;

	/**
	 * Constant that indicates autowiring a constructor.
	 * @see #setAutowireMode
	 * Bean的注入模式：按照构造函数进行注入
	 */
	public static final int AUTOWIRE_CONSTRUCTOR = AutowireCapableBeanFactory.AUTOWIRE_CONSTRUCTOR;

	/**
	 * Constant that indicates determining an appropriate autowire strategy
	 * through introspection of the bean class.
	 * @see #setAutowireMode
	 * @deprecated as of Spring 3.0: If you are using mixed autowiring strategies,
	 * use annotation-based autowiring for clearer demarcation of autowiring needs.
	 */
	@Deprecated
	public static final int AUTOWIRE_AUTODETECT = AutowireCapableBeanFactory.AUTOWIRE_AUTODETECT;

	/**
	 * Spring在进行自动装配时，使用的依赖检查模式。分为四种
	 * 自动装配时不进行依赖检查
	 */
	public static final int DEPENDENCY_CHECK_NONE = 0;

	/**
	 * 自动装配时只对依赖的对象类型做检查
	 */
	public static final int DEPENDENCY_CHECK_OBJECTS = 1;

	/**
	 * 自动装配时只对原始的简单类型做检查（包括：集合类，String，基本类型）
	 */
	public static final int DEPENDENCY_CHECK_SIMPLE = 2;

	/**
	 * 自动装配时对所有的类型都做检查，包括: 对象类型和简单类型.
	 */
	public static final int DEPENDENCY_CHECK_ALL = 3;

	/**
	 * Constant that indicates the container should attempt to infer the
	 * {@link #setDestroyMethodName destroy method name} for a bean as opposed to
	 * explicit specification of a method name. The value {@value} is specifically
	 * designed to include characters otherwise illegal in a method name, ensuring
	 * no possibility of collisions with legitimately named methods having the same
	 * name.
	 * <p>Currently, the method names detected during destroy method inference
	 * are "close" and "shutdown", if present on the specific bean class.
	 */
	public static final String INFER_METHOD = "(inferred)";


	/**
	 * Bean对应的class路径
	 *
	 * 使用volatile保证了beanClass的可见性和有序性。
	 */
	@Nullable
	private volatile Object beanClass;

	/**
	 * bean对应的作用域
	 * prototype
	 * singleton
	 * request
	 * session
	 */
	@Nullable
	private String scope = SCOPE_DEFAULT;

	/**
	 * 类是否为抽象类
	 */
	private boolean abstractFlag = false;

	/**
	 * 是否懒加载
	 */
	private boolean lazyInit = false;

	/**
	 * 自动注入模型0. 0表示不支持外部注入
	 */
	private int autowireMode = AUTOWIRE_NO;

	/**
	 * 默认的依赖检查模式，默认是不检查依赖。
	 */
	private int dependencyCheck = DEPENDENCY_CHECK_NONE;

	/**
	 * 用来控制Bean的初始化顺序。
	 * 比如Bean A依赖Bean B，而Bean B中需要初始化一些缓存，初始化缓存的时候又使用了单例模式，没有注入到Spring容器中。而A中需要使用该缓存，这个时候需要指定B应该先被初始化。
	 * 可以再Bean A中声明该属性，表示依赖的Bean，这样Spring在初始化A的时候，会先去初始化B。防止因为B未初始化而导致使用出现未知问题
	 */
	@Nullable
	private String[] dependsOn;

	/**
	 * 该变量表示Spring在依赖注入时，默认注入所有声明的Bean
	 *
	 * 有一种情况，需要设置某些Bean不参与注入：
	 *    当某一个接口有多个实现类时，这些实现类中如果有某些bean不想参与自动注入，可以通过在bean的定义中声明该属性的值为false，表示不参与自动注入.
	 */
	private boolean autowireCandidate = true;

	/**
	 * 当发现多个相同的Bean，比如一个接口有多个实现类，多个实现类对应的Bean就属于重复的Bean，这个时候直接使用会抛出bean重复定义异常。
	 * 需要在某一个Bean上面指定为primary，表示如果发现多个重复的Bean，会优先加载.
	 */
	private boolean primary = false;

	/**
	 * 用来保存Qualifiers，对应Bean的属性Qualifier
	 * 此处这个字段目前【永远】不会被赋值（除非我们手动调用对应方法为其赋值）
	 *
	 * 注意：该属性的值并不是使用 @Qualifier("a") 注解进行的赋值.
	 */
	private final Map<String, AutowireCandidateQualifier> qualifiers = new LinkedHashMap<>();

	/**
	 * 通过这个函数的逻辑初始化Bean
	 * 而不是构造函数或是工厂方法（相当于自己去实例化，而不是交给Bean工厂）
	 */
	@Nullable
	private Supplier<?> instanceSupplier;

	/**
	 * 允许访问非public的构造器和方法
	 */
	private boolean nonPublicAccessAllowed = true;

	/**
	 * 调用Bean的构造方法时，是否使用宽松匹配模式
	 */
	private boolean lenientConstructorResolution = true;

	/**
	 * 工厂Bean的名称，工厂Bean即用来创建Bean的工厂对应的Bean
	 */
	@Nullable
	private String factoryBeanName;

	/**
	 * 工厂方法的名称
	 */
	@Nullable
	private String factoryMethodName;

	/**
	 * 用来控制Spring在通过构造参数实例化对象时，调用哪个构造函数。会根据构造器的参数类型去适配合适的构造函数.
	 */
	@Nullable
	private ConstructorArgumentValues constructorArgumentValues;

	/**
	 * 表示的是可变的属性值。key-value类型，表示这些属性值在被赋值给Bean之前是可以修改的.
	 */
	@Nullable
	private MutablePropertyValues propertyValues;

	/**
	 * 记录哪些方法被覆写了
	 */
	private MethodOverrides methodOverrides = new MethodOverrides();

	/**
	 * 初始化方法的名称 init-method指定的方法名称
	 */
	@Nullable
	private String initMethodName;

	/**
	 * 销毁Bean方法的名称，destroy-method指定的方法名称
	 */
	@Nullable
	private String destroyMethodName;
	/**
	 * 是否执行init-method方法
	 */
	private boolean enforceInitMethod = true;
	/**
	 * 是否执行destroy-method方法
	 */
	private boolean enforceDestroyMethod = true;

	/**
	 * 是否是合成类（是不是应用自定义的，例如生成AOP代理时，会用到某些辅助类，这些辅助类不是应用自定义的，这个就是合成类）
	 * 创建AOP时候为true
	 */
	private boolean synthetic = false;

	/**
	 * Bean的角色，初始化时为ROLE_APPLICATION，表示默认为应用bean
	 * Spring中还存在着一些比较特殊的Bean，比如：基础设施Bean、支持Bean
	 */
	private int role = BeanDefinition.ROLE_APPLICATION;

	/**
	 * Bean定义的描述信息。可以写一些中文描述
	 */
	@Nullable
	private String description;

	/**
	 * Bean定义的资源.
	 */
	@Nullable
	private Resource resource;


	/**
	 * Create a new AbstractBeanDefinition with default settings.
	 */
	protected AbstractBeanDefinition() {
		this(null, null);
	}

	/**
	 * Create a new AbstractBeanDefinition with the given
	 * constructor argument values and property values.
	 */
	protected AbstractBeanDefinition(@Nullable ConstructorArgumentValues cargs, @Nullable MutablePropertyValues pvs) {
		this.constructorArgumentValues = cargs;
		this.propertyValues = pvs;
	}

	/**
	 * Create a new AbstractBeanDefinition as a deep copy of the given
	 * bean definition.
	 * @param original the original bean definition to copy from
	 *
	 * 在创建Bean定义之前，通过给定的Bean定义进行深拷贝
	 */
	protected AbstractBeanDefinition(BeanDefinition original) {
		// 设置父级Bean定义
		setParentName(original.getParentName());
		// 设置Bean的类型
		setBeanClassName(original.getBeanClassName());
		// 设置Bean的作用域
		setScope(original.getScope());
		// 设置Bean是否为抽象的
		setAbstract(original.isAbstract());
		// 设置Bean是否为懒加载
		setLazyInit(original.isLazyInit());
		// 设置Bean对应的工厂Bean名称
		setFactoryBeanName(original.getFactoryBeanName());
		// 设置Bean对应的工厂方法
		setFactoryMethodName(original.getFactoryMethodName());
		// 设置Bean的角色
		setRole(original.getRole());
		// 设置Bean的创建来源
		setSource(original.getSource());
		// 拷贝bean定义中的属性
		copyAttributesFrom(original);

		if (original instanceof AbstractBeanDefinition) {
			AbstractBeanDefinition originalAbd = (AbstractBeanDefinition) original;
			if (originalAbd.hasBeanClass()) { // bean定义中是否存在着beanClass
				setBeanClass(originalAbd.getBeanClass());
			}
			if (originalAbd.hasConstructorArgumentValues()) { // bean定义中是否存在着构造器参数
				setConstructorArgumentValues(new ConstructorArgumentValues(original.getConstructorArgumentValues()));
			}
			if (originalAbd.hasPropertyValues()) { // bean定义中是否存在着属性和值
				setPropertyValues(new MutablePropertyValues(original.getPropertyValues()));
			}
			if (originalAbd.hasMethodOverrides()) { // bean定义中是否存在着覆写的方法
				setMethodOverrides(new MethodOverrides(originalAbd.getMethodOverrides()));
			}
			setAutowireMode(originalAbd.getAutowireMode());
			setDependencyCheck(originalAbd.getDependencyCheck());
			setDependsOn(originalAbd.getDependsOn());
			setAutowireCandidate(originalAbd.isAutowireCandidate());
			setPrimary(originalAbd.isPrimary());
			copyQualifiersFrom(originalAbd);
			setInstanceSupplier(originalAbd.getInstanceSupplier());
			setNonPublicAccessAllowed(originalAbd.isNonPublicAccessAllowed());
			setLenientConstructorResolution(originalAbd.isLenientConstructorResolution());
			setInitMethodName(originalAbd.getInitMethodName());
			setEnforceInitMethod(originalAbd.isEnforceInitMethod());
			setDestroyMethodName(originalAbd.getDestroyMethodName());
			setEnforceDestroyMethod(originalAbd.isEnforceDestroyMethod());
			setSynthetic(originalAbd.isSynthetic());
			setResource(originalAbd.getResource());
		}
		else {
			setConstructorArgumentValues(new ConstructorArgumentValues(original.getConstructorArgumentValues()));
			setPropertyValues(new MutablePropertyValues(original.getPropertyValues()));
			setResourceDescription(original.getResourceDescription());
		}
	}


	/**
	 * Override settings in this bean definition (presumably a copied parent
	 * from a parent-child inheritance relationship) from the given bean
	 * definition (presumably the child).
	 * <ul>
	 * <li>Will override beanClass if specified in the given bean definition.
	 * <li>Will always take {@code abstract}, {@code scope},
	 * {@code lazyInit}, {@code autowireMode}, {@code dependencyCheck},
	 * and {@code dependsOn} from the given bean definition.
	 * <li>Will add {@code constructorArgumentValues}, {@code propertyValues},
	 * {@code methodOverrides} from the given bean definition to existing ones.
	 * <li>Will override {@code factoryBeanName}, {@code factoryMethodName},
	 * {@code initMethodName}, and {@code destroyMethodName} if specified
	 * in the given bean definition.
	 * </ul>
	 */
	public void overrideFrom(BeanDefinition other) {
		if (StringUtils.hasLength(other.getBeanClassName())) {
			setBeanClassName(other.getBeanClassName());
		}
		if (StringUtils.hasLength(other.getScope())) {
			setScope(other.getScope());
		}
		setAbstract(other.isAbstract());
		setLazyInit(other.isLazyInit());
		if (StringUtils.hasLength(other.getFactoryBeanName())) {
			setFactoryBeanName(other.getFactoryBeanName());
		}
		if (StringUtils.hasLength(other.getFactoryMethodName())) {
			setFactoryMethodName(other.getFactoryMethodName());
		}
		setRole(other.getRole());
		setSource(other.getSource());
		// 进行配置属性和值的拷贝
		copyAttributesFrom(other);

		if (other instanceof AbstractBeanDefinition) {
			AbstractBeanDefinition otherAbd = (AbstractBeanDefinition) other;
			if (otherAbd.hasBeanClass()) {
				setBeanClass(otherAbd.getBeanClass());
			}
			if (otherAbd.hasConstructorArgumentValues()) {
				getConstructorArgumentValues().addArgumentValues(other.getConstructorArgumentValues());
			}
			if (otherAbd.hasPropertyValues()) {
				getPropertyValues().addPropertyValues(other.getPropertyValues());
			}
			if (otherAbd.hasMethodOverrides()) {
				getMethodOverrides().addOverrides(otherAbd.getMethodOverrides());
			}
			setAutowireMode(otherAbd.getAutowireMode());
			setDependencyCheck(otherAbd.getDependencyCheck());
			setDependsOn(otherAbd.getDependsOn());
			setAutowireCandidate(otherAbd.isAutowireCandidate());
			setPrimary(otherAbd.isPrimary());
			copyQualifiersFrom(otherAbd);
			setInstanceSupplier(otherAbd.getInstanceSupplier());
			setNonPublicAccessAllowed(otherAbd.isNonPublicAccessAllowed());
			setLenientConstructorResolution(otherAbd.isLenientConstructorResolution());
			if (otherAbd.getInitMethodName() != null) {
				setInitMethodName(otherAbd.getInitMethodName());
				setEnforceInitMethod(otherAbd.isEnforceInitMethod());
			}
			if (otherAbd.getDestroyMethodName() != null) {
				setDestroyMethodName(otherAbd.getDestroyMethodName());
				setEnforceDestroyMethod(otherAbd.isEnforceDestroyMethod());
			}
			setSynthetic(otherAbd.isSynthetic());
			setResource(otherAbd.getResource());
		}
		else {
			getConstructorArgumentValues().addArgumentValues(other.getConstructorArgumentValues());
			getPropertyValues().addPropertyValues(other.getPropertyValues());
			setResourceDescription(other.getResourceDescription());
		}
	}

	/**
	 * Apply the provided default values to this bean.
	 * @param defaults the default settings to apply
	 * @since 2.5
	 */
	public void applyDefaults(BeanDefinitionDefaults defaults) {
		setLazyInit(defaults.isLazyInit());
		setAutowireMode(defaults.getAutowireMode());
		setDependencyCheck(defaults.getDependencyCheck());
		setInitMethodName(defaults.getInitMethodName());
		setEnforceInitMethod(false);
		setDestroyMethodName(defaults.getDestroyMethodName());
		setEnforceDestroyMethod(false);
	}


	/**
	 * Specify the bean class name of this bean definition.
	 */
	@Override
	public void setBeanClassName(@Nullable String beanClassName) {
		this.beanClass = beanClassName;
	}

	/**
	 * Return the current bean class name of this bean definition.
	 */
	@Override
	@Nullable
	public String getBeanClassName() {
		Object beanClassObject = this.beanClass;
		if (beanClassObject instanceof Class) {
			return ((Class<?>) beanClassObject).getName();
		}
		else {
			return (String) beanClassObject;
		}
	}

	/**
	 * Specify the class for this bean.
	 */
	public void setBeanClass(@Nullable Class<?> beanClass) {
		this.beanClass = beanClass;
	}

	/**
	 * Return the class of the wrapped bean (assuming it is resolved already).
	 * @return the bean class (never {@code null})
	 * @throws IllegalStateException if the bean definition does not define a bean class,
	 * or a specified bean class name has not been resolved into an actual Class yet
	 * @see #hasBeanClass()
	 * @see #setBeanClass(Class)
	 * @see #resolveBeanClass(ClassLoader)
	 */
	public Class<?> getBeanClass() throws IllegalStateException {
		Object beanClassObject = this.beanClass;
		if (beanClassObject == null) {
			throw new IllegalStateException("No bean class specified on bean definition");
		}
		if (!(beanClassObject instanceof Class)) {
			throw new IllegalStateException(
					"Bean class name [" + beanClassObject + "] has not been resolved into an actual Class");
		}
		return (Class<?>) beanClassObject;
	}

	/**
	 * Return whether this definition specifies a bean class.
	 * @see #getBeanClass()
	 * @see #setBeanClass(Class)
	 * @see #resolveBeanClass(ClassLoader)
	 */
	public boolean hasBeanClass() {
		return (this.beanClass instanceof Class);
	}

	/**
	 * Determine the class of the wrapped bean, resolving it from a
	 * specified class name if necessary. Will also reload a specified
	 * Class from its name when called with the bean class already resolved.
	 * @param classLoader the ClassLoader to use for resolving a (potential) class name
	 * @return the resolved bean class
	 * @throws ClassNotFoundException if the class name could be resolved
	 */
	@Nullable
	public Class<?> resolveBeanClass(@Nullable ClassLoader classLoader) throws ClassNotFoundException {
		String className = getBeanClassName();
		if (className == null) {
			return null;
		}
		Class<?> resolvedClass = ClassUtils.forName(className, classLoader);
		this.beanClass = resolvedClass;
		return resolvedClass;
	}

	/**
	 * Set the name of the target scope for the bean.
	 * <p>The default is singleton status, although this is only applied once
	 * a bean definition becomes active in the containing factory. A bean
	 * definition may eventually inherit its scope from a parent bean definition.
	 * For this reason, the default scope name is an empty string (i.e., {@code ""}),
	 * with singleton status being assumed until a resolved scope is set.
	 * @see #SCOPE_SINGLETON
	 * @see #SCOPE_PROTOTYPE
	 */
	@Override
	public void setScope(@Nullable String scope) {
		this.scope = scope;
	}

	/**
	 * Return the name of the target scope for the bean.
	 */
	@Override
	@Nullable
	public String getScope() {
		return this.scope;
	}

	/**
	 * Return whether this a <b>Singleton</b>, with a single shared instance
	 * returned from all calls.
	 * @see #SCOPE_SINGLETON
	 *
	 * 不设置Bean的实例创建方式，也是单例的Bean.
	 */
	@Override
	public boolean isSingleton() {
		return SCOPE_SINGLETON.equals(this.scope) || SCOPE_DEFAULT.equals(this.scope);
	}

	/**
	 * Return whether this a <b>Prototype</b>, with an independent instance
	 * returned for each call.
	 * @see #SCOPE_PROTOTYPE
	 */
	@Override
	public boolean isPrototype() {
		return SCOPE_PROTOTYPE.equals(this.scope);
	}

	/**
	 * Set if this bean is "abstract", i.e. not meant to be instantiated itself but
	 * rather just serving as parent for concrete child bean definitions.
	 * <p>Default is "false". Specify true to tell the bean factory to not try to
	 * instantiate that particular bean in any case.
	 */
	public void setAbstract(boolean abstractFlag) {
		this.abstractFlag = abstractFlag;
	}

	/**
	 * Return whether this bean is "abstract", i.e. not meant to be instantiated
	 * itself but rather just serving as parent for concrete child bean definitions.
	 */
	@Override
	public boolean isAbstract() {
		return this.abstractFlag;
	}

	/**
	 * Set whether this bean should be lazily initialized.
	 * <p>If {@code false}, the bean will get instantiated on startup by bean
	 * factories that perform eager initialization of singletons.
	 */
	@Override
	public void setLazyInit(boolean lazyInit) {
		this.lazyInit = lazyInit;
	}

	/**
	 * Return whether this bean should be lazily initialized, i.e. not
	 * eagerly instantiated on startup. Only applicable to a singleton bean.
	 * @return whether to apply lazy-init semantics ({@code false} by default)
	 */
	@Override
	public boolean isLazyInit() {
		return this.lazyInit;
	}

	/**
	 * Set the autowire mode. This determines whether any automagical detection
	 * and setting of bean references will happen. Default is AUTOWIRE_NO
	 * which means there won't be convention-based autowiring by name or type
	 * (however, there may still be explicit annotation-driven autowiring).
	 * @param autowireMode the autowire mode to set.
	 * Must be one of the constants defined in this class.
	 * @see #AUTOWIRE_NO
	 * @see #AUTOWIRE_BY_NAME
	 * @see #AUTOWIRE_BY_TYPE
	 * @see #AUTOWIRE_CONSTRUCTOR
	 * @see #AUTOWIRE_AUTODETECT
	 */
	public void setAutowireMode(int autowireMode) {
		this.autowireMode = autowireMode;
	}

	/**
	 * Return the autowire mode as specified in the bean definition.
	 */
	public int getAutowireMode() {
		return this.autowireMode;
	}

	/**
	 * Return the resolved autowire code,
	 * (resolving AUTOWIRE_AUTODETECT to AUTOWIRE_CONSTRUCTOR or AUTOWIRE_BY_TYPE).
	 * @see #AUTOWIRE_AUTODETECT
	 * @see #AUTOWIRE_CONSTRUCTOR
	 * @see #AUTOWIRE_BY_TYPE
	 *
	 * 获取自动注入模型.
	 */
	public int getResolvedAutowireMode() {
		// 如果设置了自动注入
		if (this.autowireMode == AUTOWIRE_AUTODETECT) {
			// Work out whether to apply setter autowiring or constructor autowiring.
			// If it has a no-arg constructor it's deemed[认为] to be setter autowiring,
			// otherwise we'll try constructor autowiring.
			Constructor<?>[] constructors = getBeanClass().getConstructors();
			for (Constructor<?> constructor : constructors) {
				// 如果当前bean中只存在着无参构造函数，则按照类型进行注入
				if (constructor.getParameterCount() == 0) {
					return AUTOWIRE_BY_TYPE;
				}
			}
			// 否则按照构造器进行注入
			return AUTOWIRE_CONSTRUCTOR;
		}
		else {
			// 如果设置了自动注入模型，则按照设置的模式注入
			return this.autowireMode;
		}
	}

	/**
	 * Set the dependency check code.
	 * @param dependencyCheck the code to set.
	 * Must be one of the four constants defined in this class.
	 * @see #DEPENDENCY_CHECK_NONE
	 * @see #DEPENDENCY_CHECK_OBJECTS
	 * @see #DEPENDENCY_CHECK_SIMPLE
	 * @see #DEPENDENCY_CHECK_ALL
	 */
	public void setDependencyCheck(int dependencyCheck) {
		this.dependencyCheck = dependencyCheck;
	}

	/**
	 * Return the dependency check code.
	 */
	public int getDependencyCheck() {
		return this.dependencyCheck;
	}

	/**
	 * Set the names of the beans that this bean depends on being initialized.
	 * The bean factory will guarantee that these beans get initialized first.
	 * <p>Note that dependencies are normally expressed through bean properties or
	 * constructor arguments. This property should just be necessary for other kinds
	 * of dependencies like statics (*ugh*) or database preparation on startup.
	 */
	@Override
	public void setDependsOn(@Nullable String... dependsOn) {
		this.dependsOn = dependsOn;
	}

	/**
	 * Return the bean names that this bean depends on.
	 */
	@Override
	@Nullable
	public String[] getDependsOn() {
		return this.dependsOn;
	}

	/**
	 * Set whether this bean is a candidate for getting autowired into some other bean.
	 * <p>Note that this flag is designed to only affect type-based autowiring.
	 * It does not affect explicit references by name, which will get resolved even
	 * if the specified bean is not marked as an autowire candidate. As a consequence,
	 * autowiring by name will nevertheless inject a bean if the name matches.
	 * @see #AUTOWIRE_BY_TYPE
	 * @see #AUTOWIRE_BY_NAME
	 */
	@Override
	public void setAutowireCandidate(boolean autowireCandidate) {
		this.autowireCandidate = autowireCandidate;
	}

	/**
	 * Return whether this bean is a candidate for getting autowired into some other bean.
	 */
	@Override
	public boolean isAutowireCandidate() {
		return this.autowireCandidate;
	}

	/**
	 * Set whether this bean is a primary autowire candidate.
	 * <p>If this value is {@code true} for exactly one bean among multiple
	 * matching candidates, it will serve as a tie-breaker.
	 */
	@Override
	public void setPrimary(boolean primary) {
		this.primary = primary;
	}

	/**
	 * Return whether this bean is a primary autowire candidate.
	 */
	@Override
	public boolean isPrimary() {
		return this.primary;
	}

	/**
	 * Register a qualifier to be used for autowire candidate resolution,
	 * keyed by the qualifier's type name.
	 * @see AutowireCandidateQualifier#getTypeName()
	 */
	public void addQualifier(AutowireCandidateQualifier qualifier) {
		this.qualifiers.put(qualifier.getTypeName(), qualifier);
	}

	/**
	 * Return whether this bean has the specified qualifier.
	 */
	public boolean hasQualifier(String typeName) {
		return this.qualifiers.containsKey(typeName);
	}

	/**
	 * Return the qualifier mapped to the provided type name.
	 */
	@Nullable
	public AutowireCandidateQualifier getQualifier(String typeName) {
		return this.qualifiers.get(typeName);
	}

	/**
	 * Return all registered qualifiers.
	 * @return the Set of {@link AutowireCandidateQualifier} objects.
	 */
	public Set<AutowireCandidateQualifier> getQualifiers() {
		return new LinkedHashSet<>(this.qualifiers.values());
	}

	/**
	 * Copy the qualifiers from the supplied AbstractBeanDefinition to this bean definition.
	 * @param source the AbstractBeanDefinition to copy from
	 */
	public void copyQualifiersFrom(AbstractBeanDefinition source) {
		Assert.notNull(source, "Source must not be null");
		this.qualifiers.putAll(source.qualifiers);
	}

	/**
	 * Specify a callback for creating an instance of the bean,
	 * as an alternative to a declaratively specified factory method.
	 * <p>If such a callback is set, it will override any other constructor
	 * or factory method metadata. However, bean property population and
	 * potential annotation-driven injection will still apply as usual.
	 * @since 5.0
	 * @see #setConstructorArgumentValues(ConstructorArgumentValues)
	 * @see #setPropertyValues(MutablePropertyValues)
	 */
	public void setInstanceSupplier(@Nullable Supplier<?> instanceSupplier) {
		this.instanceSupplier = instanceSupplier;
	}

	/**
	 * Return a callback for creating an instance of the bean, if any.
	 * @since 5.0
	 */
	@Nullable
	public Supplier<?> getInstanceSupplier() {
		return this.instanceSupplier;
	}

	/**
	 * Specify whether to allow access to non-public constructors and methods,
	 * for the case of externalized metadata pointing to those. The default is
	 * {@code true}; switch this to {@code false} for public access only.
	 * <p>This applies to constructor resolution, factory method resolution,
	 * and also init/destroy methods. Bean property accessors have to be public
	 * in any case and are not affected by this setting.
	 * <p>Note that annotation-driven configuration will still access non-public
	 * members as far as they have been annotated. This setting applies to
	 * externalized metadata in this bean definition only.
	 */
	public void setNonPublicAccessAllowed(boolean nonPublicAccessAllowed) {
		this.nonPublicAccessAllowed = nonPublicAccessAllowed;
	}

	/**
	 * 是否允许访问非公有构造函数和方法.
	 */
	public boolean isNonPublicAccessAllowed() {
		return this.nonPublicAccessAllowed;
	}

	/**
	 * Specify whether to resolve constructors in lenient mode ({@code true},
	 * which is the default) or to switch to strict resolution (throwing an exception
	 * in case of ambiguous constructors that all match when converting the arguments,
	 * whereas lenient mode would use the one with the 'closest' type matches).
	 */
	public void setLenientConstructorResolution(boolean lenientConstructorResolution) {
		this.lenientConstructorResolution = lenientConstructorResolution;
	}

	/**
	 * Return whether to resolve constructors in lenient mode or in strict mode.
	 */
	public boolean isLenientConstructorResolution() {
		return this.lenientConstructorResolution;
	}

	/**
	 * Specify the factory bean to use, if any.
	 * This the name of the bean to call the specified factory method on.
	 * @see #setFactoryMethodName
	 */
	@Override
	public void setFactoryBeanName(@Nullable String factoryBeanName) {
		this.factoryBeanName = factoryBeanName;
	}

	/**
	 * Return the factory bean name, if any.
	 */
	@Override
	@Nullable
	public String getFactoryBeanName() {
		return this.factoryBeanName;
	}

	/**
	 * Specify a factory method, if any. This method will be invoked with
	 * constructor arguments, or with no arguments if none are specified.
	 * The method will be invoked on the specified factory bean, if any,
	 * or otherwise as a static method on the local bean class.
	 * @see #setFactoryBeanName
	 * @see #setBeanClassName
	 */
	@Override
	public void setFactoryMethodName(@Nullable String factoryMethodName) {
		this.factoryMethodName = factoryMethodName;
	}

	/**
	 * Return a factory method, if any.
	 */
	@Override
	@Nullable
	public String getFactoryMethodName() {
		return this.factoryMethodName;
	}

	/**
	 * Specify constructor argument values for this bean.
	 */
	public void setConstructorArgumentValues(ConstructorArgumentValues constructorArgumentValues) {
		this.constructorArgumentValues = constructorArgumentValues;
	}

	/**
	 * Return constructor argument values for this bean (never {@code null}).
	 */
	@Override
	public ConstructorArgumentValues getConstructorArgumentValues() {
		if (this.constructorArgumentValues == null) {
			this.constructorArgumentValues = new ConstructorArgumentValues();
		}
		return this.constructorArgumentValues;
	}

	/**
	 * Return if there are constructor argument values defined for this bean.
	 */
	@Override
	public boolean hasConstructorArgumentValues() {
		return (this.constructorArgumentValues != null && !this.constructorArgumentValues.isEmpty());
	}

	/**
	 * Specify property values for this bean, if any.
	 */
	public void setPropertyValues(MutablePropertyValues propertyValues) {
		this.propertyValues = propertyValues;
	}

	/**
	 * Return property values for this bean (never {@code null}).
	 */
	@Override
	public MutablePropertyValues getPropertyValues() {
		if (this.propertyValues == null) {
			this.propertyValues = new MutablePropertyValues();
		}
		return this.propertyValues;
	}

	/**
	 * Return if there are property values values defined for this bean.
	 * @since 5.0.2
	 */
	@Override
	public boolean hasPropertyValues() {
		return (this.propertyValues != null && !this.propertyValues.isEmpty());
	}

	/**
	 * Specify method overrides for the bean, if any.
	 */
	public void setMethodOverrides(MethodOverrides methodOverrides) {
		this.methodOverrides = methodOverrides;
	}

	/**
	 * Return information about methods to be overridden by the IoC
	 * container. This will be empty if there are no method overrides.
	 * <p>Never returns {@code null}.
	 */
	public MethodOverrides getMethodOverrides() {
		return this.methodOverrides;
	}

	/**
	 * Return if there are method overrides defined for this bean.
	 * @since 5.0.2
	 */
	public boolean hasMethodOverrides() {
		return !this.methodOverrides.isEmpty();
	}

	/**
	 * Set the name of the initializer method.
	 * <p>The default is {@code null} in which case there is no initializer method.
	 */
	@Override
	public void setInitMethodName(@Nullable String initMethodName) {
		this.initMethodName = initMethodName;
	}

	/**
	 * Return the name of the initializer method.
	 */
	@Override
	@Nullable
	public String getInitMethodName() {
		return this.initMethodName;
	}

	/**
	 * Specify whether or not the configured init method is the default.
	 * <p>The default value is {@code false}.
	 * @see #setInitMethodName
	 */
	public void setEnforceInitMethod(boolean enforceInitMethod) {
		this.enforceInitMethod = enforceInitMethod;
	}

	/**
	 * Indicate whether the configured init method is the default.
	 * @see #getInitMethodName()
	 */
	public boolean isEnforceInitMethod() {
		return this.enforceInitMethod;
	}

	/**
	 * Set the name of the destroy method.
	 * <p>The default is {@code null} in which case there is no destroy method.
	 */
	@Override
	public void setDestroyMethodName(@Nullable String destroyMethodName) {
		this.destroyMethodName = destroyMethodName;
	}

	/**
	 * Return the name of the destroy method.
	 */
	@Override
	@Nullable
	public String getDestroyMethodName() {
		return this.destroyMethodName;
	}

	/**
	 * Specify whether or not the configured destroy method is the default.
	 * <p>The default value is {@code false}.
	 * @see #setDestroyMethodName
	 */
	public void setEnforceDestroyMethod(boolean enforceDestroyMethod) {
		this.enforceDestroyMethod = enforceDestroyMethod;
	}

	/**
	 * Indicate whether the configured destroy method is the default.
	 * @see #getDestroyMethodName
	 */
	public boolean isEnforceDestroyMethod() {
		return this.enforceDestroyMethod;
	}

	/**
	 * Set whether this bean definition is 'synthetic', that is, not defined
	 * by the application itself (for example, an infrastructure bean such
	 * as a helper for auto-proxying, created through {@code <aop:config>}).
	 */
	public void setSynthetic(boolean synthetic) {
		this.synthetic = synthetic;
	}

	/**
	 * Return whether this bean definition is 'synthetic', that is,
	 * not defined by the application itself.
	 */
	public boolean isSynthetic() {
		return this.synthetic;
	}

	/**
	 * Set the role hint for this {@code BeanDefinition}.
	 */
	@Override
	public void setRole(int role) {
		this.role = role;
	}

	/**
	 * Return the role hint for this {@code BeanDefinition}.
	 */
	@Override
	public int getRole() {
		return this.role;
	}

	/**
	 * Set a human-readable description of this bean definition.
	 */
	@Override
	public void setDescription(@Nullable String description) {
		this.description = description;
	}

	/**
	 * Return a human-readable description of this bean definition.
	 */
	@Override
	@Nullable
	public String getDescription() {
		return this.description;
	}

	/**
	 * Set the resource that this bean definition came from
	 * (for the purpose of showing context in case of errors).
	 */
	public void setResource(@Nullable Resource resource) {
		this.resource = resource;
	}

	/**
	 * Return the resource that this bean definition came from.
	 */
	@Nullable
	public Resource getResource() {
		return this.resource;
	}

	/**
	 * Set a description of the resource that this bean definition
	 * came from (for the purpose of showing context in case of errors).
	 */
	public void setResourceDescription(@Nullable String resourceDescription) {
		this.resource = (resourceDescription != null ? new DescriptiveResource(resourceDescription) : null);
	}

	/**
	 * Return a description of the resource that this bean definition
	 * came from (for the purpose of showing context in case of errors).
	 */
	@Override
	@Nullable
	public String getResourceDescription() {
		return (this.resource != null ? this.resource.getDescription() : null);
	}

	/**
	 * Set the originating (e.g. decorated) BeanDefinition, if any.
	 */
	public void setOriginatingBeanDefinition(BeanDefinition originatingBd) {
		this.resource = new BeanDefinitionResource(originatingBd);
	}

	/**
	 * Return the originating BeanDefinition, or {@code null} if none.
	 * Allows for retrieving the decorated bean definition, if any.
	 * <p>Note that this method returns the immediate originator. Iterate through the
	 * originator chain to find the original BeanDefinition as defined by the user.
	 */
	@Override
	@Nullable
	public BeanDefinition getOriginatingBeanDefinition() {
		return (this.resource instanceof BeanDefinitionResource ?
				((BeanDefinitionResource) this.resource).getBeanDefinition() : null);
	}

	/**
	 * Validate this bean definition.
	 * @throws BeanDefinitionValidationException in case of validation failure
	 */
	public void validate() throws BeanDefinitionValidationException {
		if (hasMethodOverrides() && getFactoryMethodName() != null) {
			throw new BeanDefinitionValidationException(
					"Cannot combine factory method with container-generated method overrides: " +
					"the factory method must create the concrete bean instance.");
		}
		if (hasBeanClass()) {
			prepareMethodOverrides();
		}
	}

	/**
	 * Validate and prepare the method overrides defined for this bean.
	 * Checks for existence of a method with the specified name.
	 * @throws BeanDefinitionValidationException in case of validation failure
	 */
	public void prepareMethodOverrides() throws BeanDefinitionValidationException {
		// Check that lookup methods exist and determine their overloaded status.
		if (hasMethodOverrides()) {
			// 循环所有重载的方法，对重载方法做一个override的标记
			getMethodOverrides().getOverrides().forEach(this::prepareMethodOverride);
		}
	}

	/**
	 * Validate and prepare the given method override.
	 * Checks for existence of a method with the specified name,
	 * marking it as not overloaded if none found.
	 * @param mo the MethodOverride object to validate
	 * @throws BeanDefinitionValidationException in case of validation failure
	 */
	protected void prepareMethodOverride(MethodOverride mo) throws BeanDefinitionValidationException {
		int count = ClassUtils.getMethodCountForName(getBeanClass(), mo.getMethodName());
		if (count == 0) {
			throw new BeanDefinitionValidationException(
					"Invalid method override: no method with name '" + mo.getMethodName() +
					"' on class [" + getBeanClassName() + "]");
		}
		else if (count == 1) {
			// Mark override as not overloaded, to avoid the overhead of arg type checking.
			// 标记方法没有重载，在后面调用方法的时候，直接根据方法名称和方法类型去调用。
			// 此处相当于一个预处理，在后面使用的时候直接调用方法，不用在判断调用的哪个重载方法，避免再次判断的开销
			mo.setOverloaded(false);
		}
	}


	/**
	 * Public declaration of Object's {@code clone()} method.
	 * Delegates to {@link #cloneBeanDefinition()}.
	 * @see Object#clone()
	 */
	@Override
	public Object clone() {
		return cloneBeanDefinition();
	}

	/**
	 * Clone this bean definition.
	 * To be implemented by concrete subclasses.
	 * @return the cloned bean definition object
	 */
	public abstract AbstractBeanDefinition cloneBeanDefinition();

	@Override
	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (!(other instanceof AbstractBeanDefinition)) {
			// 如果待比较的bean不是AbstractBeanDefinition的实例，直接返回false
			return false;
		}
		AbstractBeanDefinition that = (AbstractBeanDefinition) other;
		// 判断Bean的名称是否相同
		boolean rtn = ObjectUtils.nullSafeEquals(getBeanClassName(), that.getBeanClassName());
		// 判断Bean的作用域是否相同
		rtn &= ObjectUtils.nullSafeEquals(this.scope, that.scope);
		// 判断Bean是否都为抽象类
		rtn &= this.abstractFlag == that.abstractFlag;
		// 判断Bean是否都为懒加载
		rtn &= this.lazyInit == that.lazyInit;
		// 判断Bean是否都设置了自动注入模型
		rtn &= this.autowireMode == that.autowireMode;
		// 判断Bean是否都需要依赖检查
		rtn &= this.dependencyCheck == that.dependencyCheck;
		// 判断Bean依赖的其他Bean是否相同
		rtn &= Arrays.equals(this.dependsOn, that.dependsOn);
		// 判断Bean是否不参与其他的注入
		rtn &= this.autowireCandidate == that.autowireCandidate;
		rtn &= ObjectUtils.nullSafeEquals(this.qualifiers, that.qualifiers);
		rtn &= this.primary == that.primary;
		rtn &= this.nonPublicAccessAllowed == that.nonPublicAccessAllowed;
		rtn &= this.lenientConstructorResolution == that.lenientConstructorResolution;
		rtn &= ObjectUtils.nullSafeEquals(this.constructorArgumentValues, that.constructorArgumentValues);
		rtn &= ObjectUtils.nullSafeEquals(this.propertyValues, that.propertyValues);
		rtn &= ObjectUtils.nullSafeEquals(this.methodOverrides, that.methodOverrides);
		rtn &= ObjectUtils.nullSafeEquals(this.factoryBeanName, that.factoryBeanName);
		rtn &= ObjectUtils.nullSafeEquals(this.factoryMethodName, that.factoryMethodName);
		rtn &= ObjectUtils.nullSafeEquals(this.initMethodName, that.initMethodName);
		rtn &= this.enforceInitMethod == that.enforceInitMethod;
		rtn &= ObjectUtils.nullSafeEquals(this.destroyMethodName, that.destroyMethodName);
		rtn &= this.enforceDestroyMethod == that.enforceDestroyMethod;
		rtn &= this.synthetic == that.synthetic;
		rtn &= this.role == that.role;
		return rtn && super.equals(other);
	}

	@Override
	public int hashCode() {
		int hashCode = ObjectUtils.nullSafeHashCode(getBeanClassName());
		hashCode = 29 * hashCode + ObjectUtils.nullSafeHashCode(this.scope);
		hashCode = 29 * hashCode + ObjectUtils.nullSafeHashCode(this.constructorArgumentValues);
		hashCode = 29 * hashCode + ObjectUtils.nullSafeHashCode(this.propertyValues);
		hashCode = 29 * hashCode + ObjectUtils.nullSafeHashCode(this.factoryBeanName);
		hashCode = 29 * hashCode + ObjectUtils.nullSafeHashCode(this.factoryMethodName);
		hashCode = 29 * hashCode + super.hashCode();
		return hashCode;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("class [");
		sb.append(getBeanClassName()).append("]");
		sb.append("; scope=").append(this.scope);
		sb.append("; abstract=").append(this.abstractFlag);
		sb.append("; lazyInit=").append(this.lazyInit);
		sb.append("; autowireMode=").append(this.autowireMode);
		sb.append("; dependencyCheck=").append(this.dependencyCheck);
		sb.append("; autowireCandidate=").append(this.autowireCandidate);
		sb.append("; primary=").append(this.primary);
		sb.append("; factoryBeanName=").append(this.factoryBeanName);
		sb.append("; factoryMethodName=").append(this.factoryMethodName);
		sb.append("; initMethodName=").append(this.initMethodName);
		sb.append("; destroyMethodName=").append(this.destroyMethodName);
		if (this.resource != null) {
			sb.append("; defined in ").append(this.resource.getDescription());
		}
		return sb.toString();
	}

}
