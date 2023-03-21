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

package org.springframework.context.support;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.CachedIntrospectionResults;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.support.ResourceEditorRegistrar;
import org.springframework.context.*;
import org.springframework.context.event.*;
import org.springframework.context.expression.StandardBeanExpressionResolver;
import org.springframework.context.weaving.LoadTimeWeaverAware;
import org.springframework.context.weaving.LoadTimeWeaverAwareProcessor;
import org.springframework.core.ResolvableType;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.Environment;
import org.springframework.core.env.StandardEnvironment;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;
import org.springframework.util.ReflectionUtils;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Abstract implementation of the {@link org.springframework.context.ApplicationContext}
 * interface. Doesn't mandate the type of storage used for configuration; simply
 * implements common context functionality. Uses the Template Method design pattern,
 * requiring concrete subclasses to implement abstract methods.
 *
 * <p>In contrast to a plain BeanFactory, an ApplicationContext is supposed
 * to detect special beans defined in its internal bean factory:
 * Therefore, this class automatically registers
 * {@link org.springframework.beans.factory.config.BeanFactoryPostProcessor BeanFactoryPostProcessors},
 * {@link org.springframework.beans.factory.config.BeanPostProcessor BeanPostProcessors},
 * and {@link org.springframework.context.ApplicationListener ApplicationListeners}
 * which are defined as beans in the context.
 *
 * <p>A {@link org.springframework.context.MessageSource} may also be supplied
 * as a bean in the context, with the name "messageSource"; otherwise, message
 * resolution is delegated to the parent context. Furthermore, a multicaster
 * for application events can be supplied as an "applicationEventMulticaster" bean
 * of type {@link org.springframework.context.event.ApplicationEventMulticaster}
 * in the context; otherwise, a default multicaster of type
 * {@link org.springframework.context.event.SimpleApplicationEventMulticaster} will be used.
 *
 * <p>Implements resource loading by extending
 * {@link org.springframework.core.io.DefaultResourceLoader}.
 * Consequently treats non-URL resource paths as class path resources
 * (supporting full class path resource names that include the package path,
 * e.g. "mypackage/myresource.dat"), unless the {@link #getResourceByPath}
 * method is overridden in a subclass.
 *
 * @author Rod Johnson
 * @author Juergen Hoeller
 * @author Mark Fisher
 * @author Stephane Nicoll
 * @since January 21, 2001
 * @see #refreshBeanFactory
 * @see #getBeanFactory
 * @see org.springframework.beans.factory.config.BeanFactoryPostProcessor
 * @see org.springframework.beans.factory.config.BeanPostProcessor
 * @see org.springframework.context.event.ApplicationEventMulticaster
 * @see org.springframework.context.ApplicationListener
 * @see org.springframework.context.MessageSource
 */
public abstract class AbstractApplicationContext
		extends DefaultResourceLoader
		implements ConfigurableApplicationContext {
	/**
	 * 消息源Bean在Bean工厂中所对应的名称.
	 */
	public static final String MESSAGE_SOURCE_BEAN_NAME = "messageSource";
	/**
	 * 生命周期处理器Bean在Bean工厂中对应的名称，如果未手动指定生命周期处理器，将会使用默认的：DefaultLifecycleProcessor
	 */
	public static final String LIFECYCLE_PROCESSOR_BEAN_NAME = "lifecycleProcessor";
	/**
	 * Name of the ApplicationEventMulticaster bean in the factory.
	 * If none is supplied, a default SimpleApplicationEventMulticaster is used.
	 * @see org.springframework.context.event.ApplicationEventMulticaster
	 * @see org.springframework.context.event.SimpleApplicationEventMulticaster
	 *
	 * 应用事件多播器Bean在Bean工厂中对应的名称，默认为：SimpleApplicationEventMulticaster
	 */
	public static final String APPLICATION_EVENT_MULTICASTER_BEAN_NAME = "applicationEventMulticaster";
	static {
		// Eagerly load the ContextClosedEvent class to avoid weird classloader issues
		// on application shutdown in WebLogic 8.1. (Reported by Dustin Woods.)
		ContextClosedEvent.class.getName();
	}
	/**
	 * 用来记录日志，继承自AbstractApplicationContext也可以使用该对象进行日志记录
	 */
	protected final Log logger = LogFactory.getLog(getClass());
	/**
	 * Spring上下文的唯一ID标识
	 */
	private String id = ObjectUtils.identityToString(this);
	/**
	 * Spring上下文的名称。和ID相同
	 */
	private String displayName = ObjectUtils.identityToString(this);
	/**
	 * 当前上下文的一个父级上下文。是Spring上下文的一个顶层接口.
	 * public interface ApplicationContext extends
	 * 		EnvironmentCapable,
	 * 		ListableBeanFactory,
	 * 		HierarchicalBeanFactory,
	 *		MessageSource,
	 *		ApplicationEventPublisher,
	 *		ResourcePatternResolver {
	 *		 	...
	 *		}
	 */
	@Nullable
	private ApplicationContext parent;
	/**
	 * 创建Spring上下文时需要使用的环境信息
	 *
	 * 包括环境是否激活【可用来设置生产和测试环境】、系统properties变量信息【Map<Object,Object> map】等
	 */
	@Nullable
	private ConfigurableEnvironment environment;
	/**
	 * 作用：保存所有Bean工厂的后置处理器
	 *
	 * 在refresh()方法的第5步invokeBeanFactoryPostProcessors执行bean工厂的后置处理器时会使用。
	 * 获取该bean工厂后置处理器集合中的所有后置处理器，然后依次调用bean工厂的postProcessBeanFactory方法
	 *
	 * BeanFactoryPostProcessor接口中只有一个方法postProcessBeanFactory，是一个单独的接口，不存在父接口
	 */
	private final List<BeanFactoryPostProcessor> beanFactoryPostProcessors = new ArrayList<>();

	/**
	 * 记录Spring上下文的启动时间
	 */
	private long startupDate;

	/**
	 * 记录Spring上下文当前是否处于激活状态
	 */
	private final AtomicBoolean active = new AtomicBoolean();

	/**
	 * 记录Spring上下文当前是否处于关闭状态
	 */
	private final AtomicBoolean closed = new AtomicBoolean();
	/**
	 * 用于Spring容器启动刷新和销毁。
	 * 在执行refresh()，doClose()和close()方法时使用的同步锁对象
	 */
	private final Object startupShutdownMonitor = new Object();
	/** Reference to the JVM shutdown hook, if registered. */
	@Nullable
	private Thread shutdownHook;
	/**
	 * 资源文件解析器。是一个用来解析文件资源的策略接口
	 */
	private final ResourcePatternResolver resourcePatternResolver;
	/**
	 * 用来管理Spring容器中Bean生命周期的处理器，包括：onRefresh方法和onClose方法
	 * 该接口继承自LifeCycle接口，LifeCycle接口中包括的方法有：start，stop，isRunning
	 */
	@Nullable
	private LifecycleProcessor lifecycleProcessor;
	/**
	 * 用来接收实现了MessageSource接口的实现类。在Spring和SpringMVC整合之后，实现国际化功能时使用。
	 */
	@Nullable
	private MessageSource messageSource;
	/**
	 * 应用事件派发器接口，用来派发容器中发布的事件.
	 */
	@Nullable
	private ApplicationEventMulticaster applicationEventMulticaster;

	/**
	 * 用来保存容器中保存的监听器
	 */
	private final Set<ApplicationListener<?>> applicationListeners = new LinkedHashSet<>();

	/**
	 * 用来保存容器中的早期监听器
	 * 早期：在容器刷新之前【refresh方法执行前】注册的
	 */
	@Nullable
	private Set<ApplicationListener<?>> earlyApplicationListeners;

	/**
	 * 用来保存容器中的早期事件
	 * 早期：在事件派发器执行之前发布的事件
	 */
	@Nullable
	private Set<ApplicationEvent> earlyApplicationEvents;
	/**
	 * Create a new AbstractApplicationContext with no parent.
	 */
	public AbstractApplicationContext() {
		// 初始化一个默认的资源文件解析器对象，默认的类型为：PathMatchingResourcePatternResolver
		this.resourcePatternResolver = getResourcePatternResolver();
	}

	/**
	 * Create a new AbstractApplicationContext with the given parent context.
	 * @param parent the parent context
	 */
	public AbstractApplicationContext(@Nullable ApplicationContext parent) {
		this();
		setParent(parent);
	}


	//---------------------------------------------------------------------
	// Implementation of ApplicationContext interface
	// 以下的方法是实现自ApplicationContext接口
	//---------------------------------------------------------------------
	/**
	 * Set the unique id of this application context.
	 * <p>Default is the object id of the context instance, or the name
	 * of the context bean if the context is itself defined as a bean.
	 * @param id the unique id of the context
	 */
	@Override
	public void setId(String id) {
		this.id = id;
	}

	@Override
	public String getId() {
		return this.id;
	}

	@Override
	public String getApplicationName() {
		return "";
	}

	/**
	 * Set a friendly name for this context.
	 * Typically done during initialization of concrete context implementations.
	 * <p>Default is the object id of the context instance.
	 */
	public void setDisplayName(String displayName) {
		Assert.hasLength(displayName, "Display name must not be empty");
		this.displayName = displayName;
	}

	/**
	 * Return a friendly name for this context.
	 * @return a display name for this context (never {@code null})
	 */
	@Override
	public String getDisplayName() {
		return this.displayName;
	}

	/**
	 * Return the parent context, or {@code null} if there is no parent
	 * (that is, this context is the root of the context hierarchy).
	 */
	@Override
	@Nullable
	public ApplicationContext getParent() {
		return this.parent;
	}

	/**
	 * Set the {@code Environment} for this application context.
	 * <p>Default value is determined by {@link #createEnvironment()}. Replacing the
	 * default with this method is one option but configuration through {@link
	 * #getEnvironment()} should also be considered. In either case, such modifications
	 * should be performed <em>before</em> {@link #refresh()}.
	 * @see org.springframework.context.support.AbstractApplicationContext#createEnvironment
	 */
	@Override
	public void setEnvironment(ConfigurableEnvironment environment) {
		this.environment = environment;
	}

	/**
	 * Return the {@code Environment} for this application context in configurable
	 * form, allowing for further customization.
	 * <p>If none specified, a default environment will be initialized via
	 * {@link #createEnvironment()}.
	 */
	@Override
	public ConfigurableEnvironment getEnvironment() {
		if (this.environment == null) {
			// StandardEnvironment
			this.environment = createEnvironment();
		}
		return this.environment;
	}

	/**
	 * Create and return a new {@link StandardEnvironment}.
	 * <p>Subclasses may override this method in order to supply
	 * a custom {@link ConfigurableEnvironment} implementation.
	 */
	protected ConfigurableEnvironment createEnvironment() {
		return new StandardEnvironment();
	}

	/**
	 * Return this context's internal bean factory as AutowireCapableBeanFactory,
	 * if already available.
	 * @see #getBeanFactory()
	 */
	@Override
	public AutowireCapableBeanFactory getAutowireCapableBeanFactory() throws IllegalStateException {
		return getBeanFactory();
	}

	/**
	 * Return the timestamp (ms) when this context was first loaded.
	 */
	@Override
	public long getStartupDate() {
		return this.startupDate;
	}

	/**
	 * Publish the given event to all listeners.
	 * <p>Note: Listeners get initialized after the MessageSource, to be able
	 * to access it within listener implementations. Thus, MessageSource
	 * implementations cannot publish events.
	 * @param event the event to publish (may be application-specific or a
	 * standard framework event)
	 */
	@Override
	public void publishEvent(ApplicationEvent event) {
		publishEvent(event, null);
	}

	/**
	 * Publish the given event to all listeners.
	 * <p>Note: Listeners get initialized after the MessageSource, to be able
	 * to access it within listener implementations. Thus, MessageSource
	 * implementations cannot publish events.
	 * @param event the event to publish (may be an {@link ApplicationEvent}
	 * or a payload object to be turned into a {@link PayloadApplicationEvent})
	 */
	@Override
	public void publishEvent(Object event) {
		publishEvent(event, null);
	}

	/**
	 * Publish the given event to all listeners.
	 * @param event the event to publish (may be an {@link ApplicationEvent}
	 * or a payload object to be turned into a {@link PayloadApplicationEvent})
	 * @param eventType the resolved event type, if known
	 * @since 4.2
	 */
	protected void publishEvent(Object event, @Nullable ResolvableType eventType) {
		Assert.notNull(event, "Event must not be null");

		// Decorate event as an ApplicationEvent if necessary
		ApplicationEvent applicationEvent;
		if (event instanceof ApplicationEvent) {
			applicationEvent = (ApplicationEvent) event;
		}
		else {
			// 非ApplicationEvent类型，则转换为时间类型.
			applicationEvent = new PayloadApplicationEvent<>(this, event);
			if (eventType == null) {
				eventType = ((PayloadApplicationEvent<?>) applicationEvent).getResolvableType();
			}
		}

		// Multicast right now if possible - or lazily once the multicaster is initialized
		// 如果可能，就立刻进行多播或者一旦初始化就进入懒惰状态.
		if (this.earlyApplicationEvents != null) {
			this.earlyApplicationEvents.add(applicationEvent);
		}
		else {
			// 获取事件的多播器(派发器)
			// 然后调用SimpleApplicationEventMulticaster类的multicastEvent方法派发事件
			// applicationEventMulticaster是在refresh的第8步中进行初始化的.
			getApplicationEventMulticaster().multicastEvent(applicationEvent, eventType);
		}

		// Publish event via parent context as well...
		// 如果存在父容器，那么父容器也发布事件
		if (this.parent != null) {
			if (this.parent instanceof AbstractApplicationContext) {
				((AbstractApplicationContext) this.parent).publishEvent(event, eventType);
			}
			else {
				this.parent.publishEvent(event);
			}
		}
	}

	/**
	 * Return the internal ApplicationEventMulticaster used by the context.
	 * @return the internal ApplicationEventMulticaster (never {@code null})
	 * @throws IllegalStateException if the context has not been initialized yet
	 */
	ApplicationEventMulticaster getApplicationEventMulticaster() throws IllegalStateException {
		if (this.applicationEventMulticaster == null) {
			throw new IllegalStateException("ApplicationEventMulticaster not initialized - " +
					"call 'refresh' before multicasting events via the context: " + this);
		}
		return this.applicationEventMulticaster;
	}

	/**
	 * Return the internal LifecycleProcessor used by the context.
	 * @return the internal LifecycleProcessor (never {@code null})
	 * @throws IllegalStateException if the context has not been initialized yet
	 */
	LifecycleProcessor getLifecycleProcessor() throws IllegalStateException {
		if (this.lifecycleProcessor == null) {
			throw new IllegalStateException("LifecycleProcessor not initialized - " +
					"call 'refresh' before invoking lifecycle methods via the context: " + this);
		}
		return this.lifecycleProcessor;
	}

	/**
	 * Return the ResourcePatternResolver to use for resolving location patterns
	 * into Resource instances. Default is a
	 * {@link org.springframework.core.io.support.PathMatchingResourcePatternResolver},
	 * supporting Ant-style location patterns.
	 * <p>Can be overridden in subclasses, for extended resolution strategies,
	 * for example in a web environment.
	 * <p><b>Do not call this when needing to resolve a location pattern.</b>
	 * Call the context's {@code getResources} method instead, which
	 * will delegate to the ResourcePatternResolver.
	 * @return the ResourcePatternResolver for this context
	 * @see #getResources
	 * @see org.springframework.core.io.support.PathMatchingResourcePatternResolver
	 */
	protected ResourcePatternResolver getResourcePatternResolver() {
		return new PathMatchingResourcePatternResolver(this);
	}


	//---------------------------------------------------------------------
	// 下面的方法都是实现自ConfigurableApplicationContext接口
	//---------------------------------------------------------------------
	/**
	 * Set the parent of this application context.
	 * <p>The parent {@linkplain ApplicationContext#getEnvironment() environment} is
	 * {@linkplain ConfigurableEnvironment#merge(ConfigurableEnvironment) merged} with
	 * this (child) application context environment if the parent is non-{@code null} and
	 * its environment is an instance of {@link ConfigurableEnvironment}.
	 * @see ConfigurableEnvironment#merge(ConfigurableEnvironment)
	 */
	@Override
	public void setParent(@Nullable ApplicationContext parent) {
		this.parent = parent;
		if (parent != null) {
			Environment parentEnvironment = parent.getEnvironment();
			if (parentEnvironment instanceof ConfigurableEnvironment) {
				getEnvironment().merge((ConfigurableEnvironment) parentEnvironment);
			}
		}
	}

	@Override
	public void addBeanFactoryPostProcessor(BeanFactoryPostProcessor postProcessor) {
		Assert.notNull(postProcessor, "BeanFactoryPostProcessor must not be null");
		this.beanFactoryPostProcessors.add(postProcessor);
	}

	/**
	 * Return the list of BeanFactoryPostProcessors that will get applied
	 * to the internal BeanFactory.
	 */
	public List<BeanFactoryPostProcessor> getBeanFactoryPostProcessors() {
		return this.beanFactoryPostProcessors;
	}

	@Override
	public void addApplicationListener(ApplicationListener<?> listener) {
		Assert.notNull(listener, "ApplicationListener must not be null");
		if (this.applicationEventMulticaster != null) {
			this.applicationEventMulticaster.addApplicationListener(listener);
		}
		this.applicationListeners.add(listener);
	}

	/**
	 * Return the list of statically specified ApplicationListeners.
	 */
	public Collection<ApplicationListener<?>> getApplicationListeners() {
		return this.applicationListeners;
	}

	@Override
	public void refresh() throws BeansException, IllegalStateException {
		synchronized (this.startupShutdownMonitor) {
			/**
			 * 1.准备上下文的刷新工作，记录bean容器的启动时间，容器活跃状态
			 *    验证系统中一些属性和属性值的设置等.
			 *    使用LinkedHashSet初始化earlyApplicationListeners和earlyApplicationEvents
			 */
			prepareRefresh();
			/**
			 * 2.获取Bean工厂，期间会做解析和加载bean定义的一些列工作.生成BeanDefinition对象.
			 * 此处返回的beanFactory的真实类型为：DefaultListableBeanFactory
			 *
			 *
			 * 自定义的xsd约束文件也会在该步骤进行解析，通过实现BeanDefinitionParser接口，并实现parse方法
			 * 解析自定义标签时通过实现NamespaceHandlerSupport接口，并实现init方法进行实现
			 */
			ConfigurableListableBeanFactory beanFactory = obtainFreshBeanFactory();
			/**
			 * 3.bean工厂的初始化准备工作，设置bean工厂的一些属性
			 * 比如：创建bean工厂时，需要忽略哪些接口，需要注册哪些bean，需要设置哪些Bean的后置处理器等.
			 *
			 * 例如常用的：ApplicationContextAwareBeanPostProcessor, ApplicationListenerDetector
			 *
			 * 此外，注册一些和环境相关的bean单实例bean.
			 */
			prepareBeanFactory(beanFactory);

			try {
				/**
				 * 4.Bean定义加载完毕之后实现，目前方法为空实现，留给开发人员进行自定义扩展。
				 * 	  和BeanFactoryPostProcessor中的方法postProcessBeanFactory相同
				 *
				 * 该方法在Bean定义加载完毕之后，Bean实例化之前会执行
				 * 比如在BeanFactory加载完所有的Bean定义之后，想要修改某个bean的定义信息，可以通过重写这个方法实现.
				 * 比如：在xml中配置了<bean id="user"><property name="name" value="wb"></property></bean>
				 * 如果想在不修改配置文件的情况下修改name的值，可以使用如下的方法：
				 * class MyApplicationContext extends ClassPathXmlApplicationContext{
						 public MyApplicationContext(String s){
						 	super(s);
						 }
						 @Override
						 protected void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) {
							 BeanDefinition beanDefinition = beanFactory.getBeanDefinition("user");
							 PropertyValue propertyValue=new PropertyValue("name", "www.so.com");
							 beanDefinition.getPropertyValues().addPropertyValue(propertyValue);
						 }
				 */
				postProcessBeanFactory(beanFactory);

				/**
				 * 5.执行beanFactory的后置处理器
				 *
				 * 先执行BeanDefinitionRegistryPostProcessor接口的实现类的postProcessBeanDefinitionRegistry方法，
				 *   执行过程中，也是先执行实现了优先级接口PriorityOrdered的BeanDefinitionRegistryPostProcessor的postProcessBeanDefinitionRegistry方法
				 *   然后执行实现了Ordered接口的...
				 *   最后执行未实现PriorityOrdered接口和Ordered接口的...
				 *
				 * 然后执行BeanFactoryPostProcessor接口的实现类的postProcessBeanFactory方法
				 *   执行过程中，也是先执行实现了优先级接口PriorityOrdered的BeanFactoryPostProcessor的postProcessBeanFactory方法
				 *   然后执行实现了Ordered接口的...
				 *   最后执行未实现PriorityOrdered接口和Ordered接口的...
				 *
				 *   其中也涉及到了排序过程
				 *
				 *
				 *  配置类中的Selector类型的组件和@Component,@ComponentScan中的元数据信息也会在该步骤中进行解析
				 *    还包括执行条件注解@Condition的回调逻辑
				 *
				 *
				 *  ImportBeanDefinitionRegistrar对应的registerBeanDefinitions方法也会在该步骤中调用，给容器中注册自定义的组件.
				 */
				invokeBeanFactoryPostProcessors(beanFactory);

				/**
				 * 6.注册所有bean的后置处理器.用来拦截Bean的创建
				 *
				 * 注册所有实现了BeanPostProcessor接口的后置处理器
				 *   执行过程中，也是先执行实现了优先级接口PriorityOrdered接口的BeanPostProcessor的addBeanPostProcessor方法
				 *   然后执行实现了Ordered接口的...
				 *   最后执行未实现PriorityOrdered接口和Ordered接口的...
				 *
				 *   其中也涉及到了排序过程
				 */
				registerBeanPostProcessors(beanFactory);

				/**
				 * 7.初始化消息源
				 * 用来做国际化，消息绑定，消息解析等功能
				 * 一般在SpringMVC中会使用到.
				 */
				initMessageSource();

				/**
				 * 8.初始化事件派发器，用来发布事件
				 * 	如果容器中有类型为ApplicationEventMulticaster的派发器组件，则直接获取使用
				 * 	如果容器中没有，则默认创建一个类型为SimpleApplicationEventMulticaster的派发器，供容器派发事件使用
				 */
				initApplicationEventMulticaster();

				/**
				 * 9.用来初始化一些特殊的Bean，目前默认是空方法，未实现，可以通过继承AbstractApplicationContext类，
				 *   然后覆写该方法进行自定义特殊bean的初始化.
				 *
				 * 比如：AbstractRefreshableWebApplicationContext中onRefresh方法用来初始化主题能力.
				 *
				 * SpringBoot也是在该步骤中启动内嵌Tomcat容器的
				 */
				onRefresh();

				/**
				 * 10.注册监听器
				 * 将监听器绑定到广播器上，将监听器对应的beanName绑定到到第8步初始化的事件派发器中，
				 *   如果之前有发布的事件，则直接通过事件派发器将事件派发出去.
				 */
				registerListeners();

				/**
				 * 11.初始化所有剩余的单实例Bean(没有使用懒加载的Bean).整个Spring IOC的核心.
				 *
				 * 包括执行@PostConstruct标注的方法.
				 *
				 * 注意：SpringMVC的父子容器创建Bean的过程：
				 *   SpringMVC中，存在着父容器和子容器。当父容器启动之后，会通过该方法将所有的Dao和Service对应的Bean创建出来，保存到beanFactory的单例缓存容器中
				 *   当子容器启动之后，也会通过该方法将所有的Controller，viewResolver，HandlerMapping对应的Bean创建出来，然后放入到beanFactory的单例缓存容器中.
				 */
				finishBeanFactoryInitialization(beanFactory);

				/** 12.发布事件。例如容器中的刷新事件:ContextRefreshedEvent就是在这一步中发布. SpringCloud在该步骤中会启动web服务 */
				finishRefresh();
			}
			catch (BeansException ex) {
				if (logger.isWarnEnabled()) {
					logger.warn("Exception encountered during context initialization - " +
							"cancelling refresh attempt: " + ex);
				}
				// Destroy already created singletons to avoid dangling resources.
				// 清空单实例bean对应的map及缓存
				destroyBeans();

				// 设置容器的活跃状态为false
				cancelRefresh(ex);
				// Propagate exception to caller.
				throw ex;
			}

			finally {
				// Reset common introspection caches in Spring's core, since we
				// might not ever need metadata for singleton beans anymore...
				resetCommonCaches();
			}
		}
	}

	/**
	 * Prepare this context for refreshing, setting its startup date and
	 * active flag as well as performing any initialization of property sources.
	 */
	protected void prepareRefresh() {
		// 记录容器的启动时间.
		this.startupDate = System.currentTimeMillis();
		// 记录容器未关闭
		this.closed.set(false);
		// 记录容器状态为激活状态
		this.active.set(true);

		if (logger.isDebugEnabled()) {
			if (logger.isTraceEnabled()) {
				logger.trace("Refreshing " + this);
			}
			else {
				logger.debug("Refreshing " + getDisplayName());
			}
		}

		// Initialize any placeholder property sources in the context environment.
		/**
		 * 如果需要在验证系统属性之前，给系统中设置一些默认值。可以通过继承AbstractApplicationContext类，并重写该方法实现。
		 * Spring留给开发人员的一个扩展点.
		 *
		 * 例如子类在方法中设置环境属性中必须需要的变量：getEnvironment().setRequiredProperties("myProp");
		 */
		initPropertySources();

		// Validate that all properties marked as required are resolvable:
		// see ConfigurablePropertyResolver#setRequiredProperties
		/**
		 * 作用：校验设置的必须属性是否能够在系统环境中找到对应的值
		 *
		 * 如果在initPropertySources方法中使用getEnvironment().setRequiredProperties(String... keys)设置了必须的属性，而通过this.getProperty(key)
		 * 没有从系统环境中获取到属性的值，则会抛出MissingRequiredPropertiesException异常
		 */
		getEnvironment().validateRequiredProperties();

		// Store pre-refresh ApplicationListeners...
		if (this.earlyApplicationListeners == null) {
			/** 在SpringBoot中会有大量的初始化监听器，用于初始化使用 */
			this.earlyApplicationListeners = new LinkedHashSet<>(this.applicationListeners);
		}
		else {
			// Reset local application listeners to pre-refresh state.
			this.applicationListeners.clear();
			this.applicationListeners.addAll(this.earlyApplicationListeners);
		}

		// Allow for the collection of early ApplicationEvents,
		// to be published once the multicaster is available...
		/** 定义早期的应用事件 */
		this.earlyApplicationEvents = new LinkedHashSet<>();
	}

	/**
	 * <p>Replace any stub property sources with actual instances.
	 * @see org.springframework.core.env.PropertySource.StubPropertySource
	 */
	protected void initPropertySources() {
		// For subclasses: do nothing by default.
	}

	/**
	 * Tell the subclass to refresh the internal bean factory.
	 * @return the fresh BeanFactory instance
	 * @see #refreshBeanFactory()
	 * @see #getBeanFactory()
	 */
	protected ConfigurableListableBeanFactory obtainFreshBeanFactory() {
		/**
		 * 刷新bean工厂，判断bean工厂是否已经存在，如果存在需要进行销毁和关闭
		 *  默认调用的是AbstractRefreshableApplicationContext的refreshBeanFactory方法.
		 *  刷新Bean工厂时会进行bean定义的加载操作。
		 */
		refreshBeanFactory();
		// 这个方法是本类中定义的一个抽象方法，是一个模板方法，具体的实现在子类中
		return getBeanFactory();
	}

	/**
	 * Configure the factory's standard context characteristics,
	 * such as the context's ClassLoader and post-processors.
	 * @param beanFactory the BeanFactory to configure
	 */
	protected void prepareBeanFactory(ConfigurableListableBeanFactory beanFactory) {
		// Tell the internal bean factory to use the context's class loader etc.
		beanFactory.setBeanClassLoader(getClassLoader());

		// 设置SPEL表达式解析器，用来支持Spring的SPEL表达式
		beanFactory.setBeanExpressionResolver(new StandardBeanExpressionResolver(beanFactory.getBeanClassLoader()));

		// 添加属性编辑注册器。例如一个字符串类型的地址需要转换为一个Address对象，可以使用该功能.
		// 可参考示例：spring-source-study模块下的com.wb.spring.propertyeditor包下的示例程序
		beanFactory.addPropertyEditorRegistrar(new ResourceEditorRegistrar(this, getEnvironment()));

		// 添加bean的后置处理器。此处添加的是Spring自己的后置处理器，用来回调bean所实现的aware接口中的方法.
		beanFactory.addBeanPostProcessor(new ApplicationContextAwareProcessor(this));

		// 下面的ignoreDependencyInterface是用来设置bean工厂中需要忽略的接口
		// 可以通过实现EnvironmentAware接口来获取到当前的环境信息Environment。
		/**
		 * 如果将EnvironmentAware接口添加到ignoreDependencyInterface中，则在使用的地方通过@Autowired将会无法正常注入
		*   而是需要通过setEnvironment方法进行注入，下面的其他接口都类似.
		*/
		beanFactory.ignoreDependencyInterface(EnvironmentAware.class);
		// 可以通过实现EmbeddedValueResolverAware接口来获取String类型值的解析器
		beanFactory.ignoreDependencyInterface(EmbeddedValueResolverAware.class);
		// 资源加载器，例如使用：@Autowired ResourceLoaderAware aware; 将不会被注入
		beanFactory.ignoreDependencyInterface(ResourceLoaderAware.class);
		// 事件发布器
		beanFactory.ignoreDependencyInterface(ApplicationEventPublisherAware.class);
		// 消息资源
		beanFactory.ignoreDependencyInterface(MessageSourceAware.class);
		// 应用的上下文信息
		beanFactory.ignoreDependencyInterface(ApplicationContextAware.class);

		// 注册一些可以自动装配的接口。 当类型为dependencyType时， 注入autowiredValue。为了解决一个类型有多个子类实现时，优先注入那个子类实现的问题。
		// 例如下面第一个，当注入类型为BeanFactory时，注入的值为beanFactory，默认为DefaultListableBeanFactory
		beanFactory.registerResolvableDependency(BeanFactory.class, beanFactory);
		// 当注入类型为ResourceLoader时，注入的值为ApplicationContext
		beanFactory.registerResolvableDependency(ResourceLoader.class, this);
		// 当注入类型为ApplicationEventPublisher时，注入的值为ApplicationContext
		beanFactory.registerResolvableDependency(ApplicationEventPublisher.class, this);
		// 当注入的类型为ApplicationContext时，注入的值为ApplicationContext.
		beanFactory.registerResolvableDependency(ApplicationContext.class, this);

		// 增加一个bean的后置处理器，ApplicationListenerDetector
		// Register early post-processor for detecting inner beans as ApplicationListeners.
		beanFactory.addBeanPostProcessor(new ApplicationListenerDetector(this));

		// Detect a LoadTimeWeaver and prepare for weaving, if found.
		// 如果bean工厂中存在着名称为loadTimeWeaver的bean定义，则给bean工厂中加入LoaderTimeWeaverAwareProcessor后置处理器
		// 补充：1、增加对AspectJ的支持，在Java中织入分为3种：(1) 编译期织入; (2) 类加载期织入; (3) 运行期织入。编译期织入指的是在java编译期，
		//         采用特殊的编译器，将切面织入到java类中；类加载期织入则指的是通过特殊的类加载器，在字节码加载到JVM时，织入切面；运行期织入则是
		//         通过采用cglib或者jdk进行切面的织入。
		//      2、aspectJ中提供了两种方式：
		//         (1) 通过特殊编译器，在编译期，将aspectJ语言编写的切面类织入到java类中；
		//         (2) 类加载期织入，就是通过下面的LoadTimeWeaving
		if (beanFactory.containsBean(LOAD_TIME_WEAVER_BEAN_NAME)) {
			beanFactory.addBeanPostProcessor(new LoadTimeWeaverAwareProcessor(beanFactory));
			// Set a temporary ClassLoader for type matching.
			beanFactory.setTempClassLoader(new ContextTypeMatchClassLoader(beanFactory.getBeanClassLoader()));
		}

		// 给容器中注册一些与运行环境相关的单实例Bean
		if (!beanFactory.containsLocalBean(ENVIRONMENT_BEAN_NAME)) {
			// beanName: environment，直接将new出来的Spring内部对象放入到Spring的单实例缓存池中.
			beanFactory.registerSingleton(ENVIRONMENT_BEAN_NAME, getEnvironment());
		}

		if (!beanFactory.containsLocalBean(SYSTEM_PROPERTIES_BEAN_NAME)) {
			// beanName: systemProperties   方法：System.getProperties();
			beanFactory.registerSingleton(SYSTEM_PROPERTIES_BEAN_NAME, getEnvironment().getSystemProperties());
		}

		if (!beanFactory.containsLocalBean(SYSTEM_ENVIRONMENT_BEAN_NAME)) {
			// beanName: systemEnvironment,   方法：System.getEnv();
			beanFactory.registerSingleton(SYSTEM_ENVIRONMENT_BEAN_NAME, getEnvironment().getSystemEnvironment());
		}
	}

	/**
	 * Modify the application context's internal bean factory after its standard
	 * initialization. All bean definitions will have been loaded, but no beans
	 * will have been instantiated yet. This allows for registering special
	 * BeanPostProcessors etc in certain ApplicationContext implementations.
	 * @param beanFactory the bean factory used by the application context
	 */
	protected void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) {
	}

	/**
	 * Instantiate and invoke all registered BeanFactoryPostProcessor beans,
	 * respecting explicit order if given.
	 * <p>Must be called before singleton instantiation.
	 */
	// beanFactory的真实类型为 DefaultListableBeanFactory
	protected void invokeBeanFactoryPostProcessors(ConfigurableListableBeanFactory beanFactory) {
		// 执行Bean工厂的所有后置处理器
		PostProcessorRegistrationDelegate.invokeBeanFactoryPostProcessors(beanFactory, getBeanFactoryPostProcessors());

		// Detect a LoadTimeWeaver and prepare for weaving, if found in the meantime
		// (e.g. through an @Bean method registered by ConfigurationClassPostProcessor)
		if (beanFactory.getTempClassLoader() == null && beanFactory.containsBean(LOAD_TIME_WEAVER_BEAN_NAME)) {
			beanFactory.addBeanPostProcessor(new LoadTimeWeaverAwareProcessor(beanFactory));
			beanFactory.setTempClassLoader(new ContextTypeMatchClassLoader(beanFactory.getBeanClassLoader()));
		}
	}

	/**
	 * Instantiate and register all BeanPostProcessor beans,
	 * respecting explicit order if given.
	 * <p>Must be called before any instantiation of application beans.
	 */
	protected void registerBeanPostProcessors(ConfigurableListableBeanFactory beanFactory) {
		PostProcessorRegistrationDelegate.registerBeanPostProcessors(beanFactory, this);
	}

	/**
	 * Initialize the MessageSource.
	 * Use parent's if none defined in this context.
	 * 初始化MessageSource消息源，如果beanFactory中不存在此Bean，则采用默认的配置并设置父类的MessageSource.
	 */
	protected void initMessageSource() {
		// 获取bean工厂
		ConfigurableListableBeanFactory beanFactory = getBeanFactory();
		// 判断是否在bean工厂中定义了id为messageSource的bean对象
		if (beanFactory.containsLocalBean(MESSAGE_SOURCE_BEAN_NAME)) {
			// 如果有id为messageSource，而且类型是MessageSource的组件，直接赋值给messageSource属性
			this.messageSource = beanFactory.getBean(MESSAGE_SOURCE_BEAN_NAME, MessageSource.class);
			// Make MessageSource aware of parent MessageSource.
			if (this.parent != null && this.messageSource instanceof HierarchicalMessageSource) {
				HierarchicalMessageSource hms = (HierarchicalMessageSource) this.messageSource;
				if (hms.getParentMessageSource() == null) {
					// Only set parent context as parent MessageSource if no parent MessageSource
					// registered already.
					hms.setParentMessageSource(getInternalParentMessageSource());
				}
			}
			if (logger.isTraceEnabled()) {
				logger.trace("Using MessageSource [" + this.messageSource + "]");
			}
		}
		else {
			// Use empty MessageSource to be able to accept getMessage calls.
			// 如果没有，则创建一个DelegatingMessageSource类型的messageSource对象
			DelegatingMessageSource dms = new DelegatingMessageSource();
			dms.setParentMessageSource(getInternalParentMessageSource());
			this.messageSource = dms;
			// 将messageSource注册到容器中，在获取国际化配置文件中的某个值时，可以直接通过注入MessageSource，
			// 调用其getMessage(String code, @Nullable Object[] args, @Nullable String defaultMessage, Locale locale)方法来获取.
			beanFactory.registerSingleton(MESSAGE_SOURCE_BEAN_NAME, this.messageSource);
			if (logger.isTraceEnabled()) {
				logger.trace("No '" + MESSAGE_SOURCE_BEAN_NAME + "' bean, using [" + this.messageSource + "]");
			}
		}
	}

	/**
	 * Initialize the ApplicationEventMulticaster.
	 * Uses SimpleApplicationEventMulticaster if none defined in the context.
	 * @see org.springframework.context.event.SimpleApplicationEventMulticaster
	 *
	 * 事件监听器的作用是用来发布事件使用的.
	 */
	protected void initApplicationEventMulticaster() {
		// 获取Bean工厂
		ConfigurableListableBeanFactory beanFactory = getBeanFactory();
		// 判断当前的bean工厂中有没有id为applicationEventMulticaster的事件派发器
		if (beanFactory.containsLocalBean(APPLICATION_EVENT_MULTICASTER_BEAN_NAME)) {
			// 如果有，则直接获取到ApplicationEventMulticaster类型的事件多播器，并赋值给applicationEventMulticaster.
			this.applicationEventMulticaster =
					beanFactory.getBean(APPLICATION_EVENT_MULTICASTER_BEAN_NAME, ApplicationEventMulticaster.class);
			if (logger.isTraceEnabled()) {
				logger.trace("Using ApplicationEventMulticaster [" + this.applicationEventMulticaster + "]");
			}
		}
		else {
			// 如果没有则创建一个类型为SimpleApplicationEventMulticaster事件监听器，
			this.applicationEventMulticaster = new SimpleApplicationEventMulticaster(beanFactory);
			// 将创建的事件多播器注册到容器中，在其他组件需要派发事件时，直接获取这个applicationEventMulticaster
			beanFactory.registerSingleton(APPLICATION_EVENT_MULTICASTER_BEAN_NAME, this.applicationEventMulticaster);
			if (logger.isTraceEnabled()) {
				logger.trace("No '" + APPLICATION_EVENT_MULTICASTER_BEAN_NAME + "' bean, using " +
						"[" + this.applicationEventMulticaster.getClass().getSimpleName() + "]");
			}
		}
	}

	/**
	 * Initialize the LifecycleProcessor.
	 * Uses DefaultLifecycleProcessor if none defined in the context.
	 * @see org.springframework.context.support.DefaultLifecycleProcessor
	 */
	protected void initLifecycleProcessor() {
		ConfigurableListableBeanFactory beanFactory = getBeanFactory();
		// 如果当前的beanFactory中存在名称为lifecycleProcessor的声明周期处理器bean，则直接赋值
		if (beanFactory.containsLocalBean(LIFECYCLE_PROCESSOR_BEAN_NAME)) {
			this.lifecycleProcessor =
					beanFactory.getBean(LIFECYCLE_PROCESSOR_BEAN_NAME, LifecycleProcessor.class);
			if (logger.isTraceEnabled()) {
				logger.trace("Using LifecycleProcessor [" + this.lifecycleProcessor + "]");
			}
		}
		else {
			// 否则使用DefaultLifecycleProcessor作为默认的声明周期处理器.
			DefaultLifecycleProcessor defaultProcessor = new DefaultLifecycleProcessor();
			defaultProcessor.setBeanFactory(beanFactory);
			this.lifecycleProcessor = defaultProcessor;
			beanFactory.registerSingleton(LIFECYCLE_PROCESSOR_BEAN_NAME, this.lifecycleProcessor);
			if (logger.isTraceEnabled()) {
				logger.trace("No '" + LIFECYCLE_PROCESSOR_BEAN_NAME + "' bean, using " +
						"[" + this.lifecycleProcessor.getClass().getSimpleName() + "]");
			}
		}
	}

	/**
	 * Template method which can be overridden to add context-specific refresh work.
	 * Called on initialization of special beans, before instantiation of singletons.
	 * <p>This implementation is empty.
	 * @throws BeansException in case of errors
	 * @see #refresh()
	 */
	protected void onRefresh() throws BeansException {
		// For subclasses: do nothing by default.
	}

	/**
	 * Add beans that implement ApplicationListener as listeners.
	 * Doesn't affect other listeners, which can be added without being beans.
	 */
	// Spring在初始化广播器之后，并没有给其绑定Listener。在该方法中进行了绑定.
	protected void registerListeners() {
		// Register statically specified listeners first.
		// 将注册的监听器绑定到广播器
		for (ApplicationListener<?> listener : getApplicationListeners()) {
			getApplicationEventMulticaster().addApplicationListener(listener);
		}
		// Do not initialize FactoryBeans here: We need to leave all regular beans
		// uninitialized to let post-processors apply to them!
		// 根据类型获取listener监听器的名称
		String[] listenerBeanNames = getBeanNamesForType(ApplicationListener.class, true, false);
		for (String listenerBeanName : listenerBeanNames) {
			// 将事件监听器注册到派发器中，以后使用的时候，就可以直接通过事件派发器执行.
			getApplicationEventMulticaster().addApplicationListenerBean(listenerBeanName);
		}
		// Publish early application events now that we finally have a multicaster...
		// 如果之前存在着应用事件，则直接发布.比如如下自定义监听器的示例，会在此进行发布：
		/**
		 *   // 1.先自定义事件
			 public class TestEvent extends ApplicationEvent{
				 private User user;
				 public TestEvent(Object source,User user) {
					 this.user=user;
				 }
				 public User getUser() {
					return user;
				 }
				 public void setUser(User user) {
					this.user = user;
				 }
			 }
		 	 // 2.自定义监听器
			 public class TestListener implements ApplicationListener<TestEvent>{
				 public void onApplicationEvent(TestEvent event) {
					 User user = event.getUser();
					 System.out.println(user.getEmail());
				 }
			 }

		 	// 3.配置监听器
		 	<bean id="testListener" class="com.wb.listener.TestListener"></bean>

		 	// 4.发布事件
			 MyApplicationContext context=new MyApplicationContext("classpath:applicationContext.xml");
			 User user=new User();
			 user.setEmail("1111");
			 context.publishEvent(new TestEvent("", user));
		 */


		/**
		 * 派发之前产生的事件。
		 * earlyApplicationEvent是在prepareRefresh中声明的.
		 */
		Set<ApplicationEvent> earlyEventsToProcess = this.earlyApplicationEvents;
		this.earlyApplicationEvents = null;
		if (earlyEventsToProcess != null) {
			// earlyApplicationEvents如果不为空的话，则进行事件的派发
			for (ApplicationEvent earlyEvent : earlyEventsToProcess) {
				// 调用时间派发器进行事件的派发操作
				getApplicationEventMulticaster().multicastEvent(earlyEvent);
			}
		}
	}

	/**
	 * Finish the initialization of this context's bean factory,
	 * initializing all remaining singleton beans.
	 */
	protected void finishBeanFactoryInitialization(ConfigurableListableBeanFactory beanFactory) {
		// 1. 初始化此上下文的转换服务，用来自定义将Spring中的某个Bean的属性从一个类型转换到另外一个类型.
		//    判断Bean工厂中是否存在名称为conversionService的转换服务bean，如果存在而且类型为ConversionService，则获取该Bean实例，并将其设置到BeanFactory中
		/**
		 * 例如：
		 * (1)有如下的javaBean:
		 * public class Person {
		 *     public String name;
		 *     public Date birthday;
		 *     ...
		 * }
		 * (2)有如下的xml配置：
		 * <bean name="person" class="com.wb.test.Person">
		 *     <property name="name" value="wangbing"/>
		 *     <property name="birthday" value="1999-03-03"/>
		 * </bean>
		 * (3)有如下的测试类：
		 * ApplicationContext acx = new ClasspathXmlApplicationContext("test.xml");
		 * Person person = (Person) acx.getBean("person");
		 * System.out.println(person.name);
		 * System.out.println(person.birthday); // 改行会报错，提示字符串类型不能转换为日期类型
		 *
		 * (4)可以通过定义如下名称的bean，将某种类型的属性值转换为另外一种类型.
		 * <bean name="conversionService" class="com.wb.test.MyConversionService" />
		 * public class MyConversionService implements ConversionService {
		 *    // 实现是否能转换以及具体转换的方法。
		 *	  public boolean canConvert(Class<?> sourceType, Class<?> targetType) {}
		 *	  public boolean canConvert(TypeDescriptor sourceType, TypeDescriptor targetType) {}
		 *    public <T> T convert(Object source, Class<T> targetType) {}
		 *    // 可以在该方法中实现转换逻辑。如果源类型sourceType是String类型的话，将其转换为Date类型返回。
		 *    public Object convert(Object source, TypeDescriptor sourceType, TypeDescriptor targetType) {}
		 * }
		 */
		// 2.在Spring中，如果需要配置自定义的转换器，还可以直接利用Spring提供的ConversionServiceFactoryBean来完成。自己只需要实现具体的转换逻辑即可
		/**
		 * (1)配置conversionService对应的工厂Bean：
		 * <bean id="conversionService" class="org.springframework.context.support.ConversionServiceFactoryBean">
		 *     <property name="converters">
		 *			<bean class="com.wb.test.MyConverter"/>
		 *     </property>
		 * </bean>
		 * (2)然后自己去实现MyConverter即可：
		 *  public class MyConverter implements Converter<String,Date> {
		 *		@Override
		 *		public Date convert(String source) {
		 *			DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		 *			try {
		 *				return format.parse((String) source);
		 *			} catch (ParseException e) {
		 *				e.printStackTrace();
		 *			}
		 *			return null;
		 *		}
		 *	}
		 */
		if (beanFactory.containsBean(CONVERSION_SERVICE_BEAN_NAME)
				&& beanFactory.isTypeMatch(CONVERSION_SERVICE_BEAN_NAME, ConversionService.class)) {
			beanFactory.setConversionService(beanFactory.getBean(CONVERSION_SERVICE_BEAN_NAME, ConversionService.class));
		}
		/**
		 * 如果beanFactory之前没有注册嵌入值解析器，则注册默认的嵌入值解析器，
		 *  主要用于注解属性值的解析例如：@Value("${app.name}")。
		 */
		// 值解析器设置的地方：在调用invokeBeanfactoryPostProcessor方法的时候，通过PropertySourcesPlaceholderConfigurer的后置处理方法设置进去的
		if (!beanFactory.hasEmbeddedValueResolver()) {
			// 调用resolvePlaceholders方法解析strVal对应的值
			beanFactory.addEmbeddedValueResolver(strVal -> getEnvironment().resolvePlaceholders(strVal));
		}
		/**
		 * 初始化所有实现了LoadTimeWeaverAware接口的子类，用于类在加载进入jvm之前，动态增强类
		 *  这特别适用于Spring的JPA支持，其中load-time weaving加载织入对JPA类转换非常必要
		 */
		String[] weaverAwareNames = beanFactory.getBeanNamesForType(LoadTimeWeaverAware.class, false, false);
		for (String weaverAwareName : weaverAwareNames) {
			getBean(weaverAwareName);
		}

		// 停止使用临时的类加载器.
		beanFactory.setTempClassLoader(null);

		// 缓存(冻结)所有的BeanName（注册的bean定义不会被修改或进一步做处理了，因为下面马上要创建Bean的实例对象了）
		beanFactory.freezeConfiguration();

		// 初始化所有的单实例Bean，包括创建单实例bean的全部过程
		beanFactory.preInstantiateSingletons();
	}

	/**
	 * Finish the refresh of this context, invoking the LifecycleProcessor's
	 * onRefresh() method and publishing the
	 * {@link org.springframework.context.event.ContextRefreshedEvent}.
	 */
	protected void finishRefresh() {
		// Clear context-level resource caches (such as ASM metadata from scanning).
		// 清空资源缓存.
		clearResourceCaches();

		// Initialize lifecycle processor for this context.
		// 初始化声明周期处理器，用于处理Bean的生命周期.
		initLifecycleProcessor();

		// Propagate refresh to lifecycle processor first.
		// 使用声明周期处理器传播刷新事件
		getLifecycleProcessor().onRefresh();

		// Publish the final event.
		// 在Context中发布刷新事件
		publishEvent(new ContextRefreshedEvent(this));

		// Participate in LiveBeansView MBean, if active.
		// 将本Context注册到ListBeansView中。包括注册Spring ApplicationContext对应的MBean
		LiveBeansView.registerApplicationContext(this);
	}

	/**
	 * Cancel this context's refresh attempt, resetting the {@code active} flag
	 * after an exception got thrown.
	 * @param ex the exception that led to the cancellation
	 */
	protected void cancelRefresh(BeansException ex) {
		this.active.set(false);
	}

	/**
	 * Reset Spring's common reflection metadata caches, in particular the
	 * {@link ReflectionUtils}, {@link AnnotationUtils}, {@link ResolvableType}
	 * and {@link CachedIntrospectionResults} caches.
	 * @since 4.2
	 * @see ReflectionUtils#clearCache()
	 * @see AnnotationUtils#clearCache()
	 * @see ResolvableType#clearCache()
	 * @see CachedIntrospectionResults#clearClassLoader(ClassLoader)
	 */
	protected void resetCommonCaches() {
		ReflectionUtils.clearCache();
		AnnotationUtils.clearCache();
		ResolvableType.clearCache();
		CachedIntrospectionResults.clearClassLoader(getClassLoader());
	}


	/**
	 * Register a shutdown hook with the JVM runtime, closing this context
	 * on JVM shutdown unless it has already been closed at that time.
	 * <p>Delegates to {@code doClose()} for the actual closing procedure.
	 * @see Runtime#addShutdownHook
	 * @see #close()
	 * @see #doClose()
	 */
	@Override
	public void registerShutdownHook() {
		if (this.shutdownHook == null) {
			// No shutdown hook registered yet.
			this.shutdownHook = new Thread(() -> {
				synchronized (startupShutdownMonitor) {
					// 执行关闭方法，会销毁容器及容器中的所有单实例bean
					doClose();
				}
			});
			// 添加容器关闭时的钩子函数.
			Runtime.getRuntime().addShutdownHook(this.shutdownHook);
		}
	}

	/**
	 * Callback for destruction of this instance, originally attached
	 * to a {@code DisposableBean} implementation (not anymore in 5.0).
	 * <p>The {@link #close()} method is the native way to shut down
	 * an ApplicationContext, which this method simply delegates to.
	 * @deprecated as of Spring Framework 5.0, in favor of {@link #close()}
	 */
	@Deprecated
	public void destroy() {
		close();
	}

	/**
	 * Close this application context, destroying all beans in its bean factory.
	 * <p>Delegates to {@code doClose()} for the actual closing procedure.
	 * Also removes a JVM shutdown hook, if registered, as it's not needed anymore.
	 * @see #doClose()
	 * @see #registerShutdownHook()
	 *
	 * 关闭容器时执行。acx.close();
	 */
	@Override
	public void close() {
		// 获取锁
		synchronized (this.startupShutdownMonitor) {
			// 调用doClose方法关闭容器
			doClose();
			// If we registered a JVM shutdown hook, we don't need it anymore now:
			// We've already explicitly closed the context.
			if (this.shutdownHook != null) {
				try {
					Runtime.getRuntime().removeShutdownHook(this.shutdownHook);
				}
				catch (IllegalStateException ex) {
					// ignore - VM is already shutting down
				}
			}
		}
	}

	/**
	 * Actually performs context closing: publishes a ContextClosedEvent and
	 * destroys the singletons in the bean factory of this application context.
	 * <p>Called by both {@code close()} and a JVM shutdown hook, if any.
	 * @see org.springframework.context.event.ContextClosedEvent
	 * @see #destroyBeans()
	 * @see #close()
	 * @see #registerShutdownHook()
	 */
	protected void doClose() {
		// Check whether an actual close attempt is necessary...
		if (this.active.get() && this.closed.compareAndSet(false, true)) {
			if (logger.isDebugEnabled()) {
				logger.debug("Closing " + this);
			}

			// 取消Spring上下文MBean的注册
			LiveBeansView.unregisterApplicationContext(this);

			try {
				// 发布容器关闭事件
				publishEvent(new ContextClosedEvent(this));
			}
			catch (Throwable ex) {
				logger.warn("Exception thrown from ApplicationListener handling ContextClosedEvent", ex);
			}

			// Stop all Lifecycle beans, to avoid delays during individual destruction.
			if (this.lifecycleProcessor != null) {
				try {
					this.lifecycleProcessor.onClose();
				}
				catch (Throwable ex) {
					logger.warn("Exception thrown from LifecycleProcessor on context close", ex);
				}
			}

			// Destroy all cached singletons in the context's BeanFactory.
			// 销毁容器中的所有单实例bean，包括清空bean的缓存.
			destroyBeans();

			// Close the state of this context itself.
			// 关闭bean工厂，设置bean工厂的序列化ID为null
			closeBeanFactory();

			// Let subclasses do some final clean-up if they wish...
			// 留给开发人员的一个扩展点。用于容器关闭时做一些定制化处理.
			onClose();

			// Reset local application listeners to pre-refresh state.
			if (this.earlyApplicationListeners != null) {
				this.applicationListeners.clear();
				this.applicationListeners.addAll(this.earlyApplicationListeners);
			}

			// Switch to inactive.
			this.active.set(false);
		}
	}

	/**
	 * Template method for destroying all beans that this context manages.
	 * The default implementation destroy all cached singletons in this context,
	 * invoking {@code DisposableBean.destroy()} and/or the specified
	 * "destroy-method".
	 * <p>Can be overridden to add context-specific bean destruction steps
	 * right before or right after standard singleton destruction,
	 * while the context's BeanFactory is still active.
	 * @see #getBeanFactory()
	 * @see org.springframework.beans.factory.config.ConfigurableBeanFactory#destroySingletons()
	 */
	protected void destroyBeans() {
		getBeanFactory().destroySingletons();
	}

	/**
	 * Template method which can be overridden to add context-specific shutdown work.
	 * The default implementation is empty.
	 * <p>Called at the end of {@link #doClose}'s shutdown procedure, after
	 * this context's BeanFactory has been closed. If custom shutdown logic
	 * needs to execute while the BeanFactory is still active, override
	 * the {@link #destroyBeans()} method instead.
	 */
	protected void onClose() {
		// For subclasses: do nothing by default.
	}

	@Override
	public boolean isActive() {
		return this.active.get();
	}

	/**
	 * Assert that this context's BeanFactory is currently active,
	 * throwing an {@link IllegalStateException} if it isn't.
	 * <p>Invoked by all {@link BeanFactory} delegation methods that depend
	 * on an active context, i.e. in particular all bean accessor methods.
	 * <p>The default implementation checks the {@link #isActive() 'active'} status
	 * of this context overall. May be overridden for more specific checks, or for a
	 * no-op if {@link #getBeanFactory()} itself throws an exception in such a case.
	 *
	 * 判断bean工厂是否已经被关闭.
	 */
	protected void assertBeanFactoryActive() {
		// 判断容器是否处于激活状态
		if (!this.active.get()) {
			// 判断容器是否已经关闭
			if (this.closed.get()) {
				throw new IllegalStateException(getDisplayName() + " has been closed already");
			}
			// 容器处于未刷新状态
			else {
				throw new IllegalStateException(getDisplayName() + " has not been refreshed yet");
			}
		}
	}

	//---------------------------------------------------------------------
	// 下面的方法实现自BeanFactory接口
	//---------------------------------------------------------------------
	@Override
	public Object getBean(String name) throws BeansException {
		assertBeanFactoryActive();
		return getBeanFactory().getBean(name);
	}
	@Override
	public <T> T getBean(String name, Class<T> requiredType) throws BeansException {
		assertBeanFactoryActive();
		return getBeanFactory().getBean(name, requiredType);
	}
	@Override
	public Object getBean(String name, Object... args) throws BeansException {
		assertBeanFactoryActive();
		return getBeanFactory().getBean(name, args);
	}
	@Override
	public <T> T getBean(Class<T> requiredType) throws BeansException {
		assertBeanFactoryActive();
		return getBeanFactory().getBean(requiredType);
	}
	@Override
	public <T> T getBean(Class<T> requiredType, Object... args) throws BeansException {
		assertBeanFactoryActive();
		return getBeanFactory().getBean(requiredType, args);
	}
	@Override
	public <T> ObjectProvider<T> getBeanProvider(Class<T> requiredType) {
		assertBeanFactoryActive();
		return getBeanFactory().getBeanProvider(requiredType);
	}
	@Override
	public <T> ObjectProvider<T> getBeanProvider(ResolvableType requiredType) {
		assertBeanFactoryActive();
		return getBeanFactory().getBeanProvider(requiredType);
	}
	@Override
	public boolean containsBean(String name) {
		return getBeanFactory().containsBean(name);
	}
	@Override
	public boolean isSingleton(String name) throws NoSuchBeanDefinitionException {
		assertBeanFactoryActive();
		return getBeanFactory().isSingleton(name);
	}
	@Override
	public boolean isPrototype(String name) throws NoSuchBeanDefinitionException {
		assertBeanFactoryActive();
		return getBeanFactory().isPrototype(name);
	}

	@Override
	public boolean isTypeMatch(String name, ResolvableType typeToMatch) throws NoSuchBeanDefinitionException {
		assertBeanFactoryActive();
		return getBeanFactory().isTypeMatch(name, typeToMatch);
	}

	@Override
	public boolean isTypeMatch(String name, Class<?> typeToMatch) throws NoSuchBeanDefinitionException {
		assertBeanFactoryActive();
		return getBeanFactory().isTypeMatch(name, typeToMatch);
	}

	@Override
	@Nullable
	public Class<?> getType(String name) throws NoSuchBeanDefinitionException {
		assertBeanFactoryActive();
		return getBeanFactory().getType(name);
	}

	@Override
	public String[] getAliases(String name) {
		return getBeanFactory().getAliases(name);
	}


	//---------------------------------------------------------------------
	// 下面的所有方法实现自ListableBeanFactory接口
	//---------------------------------------------------------------------

	@Override
	public boolean containsBeanDefinition(String beanName) {
		return getBeanFactory().containsBeanDefinition(beanName);
	}

	@Override
	public int getBeanDefinitionCount() {
		return getBeanFactory().getBeanDefinitionCount();
	}

	@Override
	public String[] getBeanDefinitionNames() {
		return getBeanFactory().getBeanDefinitionNames();
	}

	@Override
	public String[] getBeanNamesForType(ResolvableType type) {
		assertBeanFactoryActive();
		return getBeanFactory().getBeanNamesForType(type);
	}

	@Override
	public String[] getBeanNamesForType(@Nullable Class<?> type) {
		assertBeanFactoryActive();
		return getBeanFactory().getBeanNamesForType(type);
	}

	@Override
	public String[] getBeanNamesForType(@Nullable Class<?> type, boolean includeNonSingletons, boolean allowEagerInit) {
		assertBeanFactoryActive();
		return getBeanFactory().getBeanNamesForType(type, includeNonSingletons, allowEagerInit);
	}

	@Override
	public <T> Map<String, T> getBeansOfType(@Nullable Class<T> type) throws BeansException {
		assertBeanFactoryActive();
		return getBeanFactory().getBeansOfType(type);
	}

	@Override
	public <T> Map<String, T> getBeansOfType(@Nullable Class<T> type, boolean includeNonSingletons, boolean allowEagerInit)
			throws BeansException {

		assertBeanFactoryActive();
		return getBeanFactory().getBeansOfType(type, includeNonSingletons, allowEagerInit);
	}

	@Override
	public String[] getBeanNamesForAnnotation(Class<? extends Annotation> annotationType) {
		assertBeanFactoryActive();
		return getBeanFactory().getBeanNamesForAnnotation(annotationType);
	}

	@Override
	public Map<String, Object> getBeansWithAnnotation(Class<? extends Annotation> annotationType)
			throws BeansException {

		assertBeanFactoryActive();
		return getBeanFactory().getBeansWithAnnotation(annotationType);
	}

	@Override
	@Nullable
	public <A extends Annotation> A findAnnotationOnBean(String beanName, Class<A> annotationType)
			throws NoSuchBeanDefinitionException {

		assertBeanFactoryActive();
		return getBeanFactory().findAnnotationOnBean(beanName, annotationType);
	}


	//---------------------------------------------------------------------
	// 下面的方法实现自HierarchicalBeanFactory接口
	//---------------------------------------------------------------------
	@Override
	@Nullable
	public BeanFactory getParentBeanFactory() {
		return getParent();
	}

	@Override
	public boolean containsLocalBean(String name) {
		return getBeanFactory().containsLocalBean(name);
	}

	/**
	 * Return the internal bean factory of the parent context if it implements
	 * ConfigurableApplicationContext; else, return the parent context itself.
	 * @see org.springframework.context.ConfigurableApplicationContext#getBeanFactory
	 */
	@Nullable
	protected BeanFactory getInternalParentBeanFactory() {
		return (getParent() instanceof ConfigurableApplicationContext ?
				((ConfigurableApplicationContext) getParent()).getBeanFactory() : getParent());
	}


	//---------------------------------------------------------------------
	// Implementation of MessageSource interface
	//---------------------------------------------------------------------
	@Override
	public String getMessage(String code, @Nullable Object[] args, @Nullable String defaultMessage, Locale locale) {
		return getMessageSource().getMessage(code, args, defaultMessage, locale);
	}

	@Override
	public String getMessage(String code, @Nullable Object[] args, Locale locale) throws NoSuchMessageException {
		return getMessageSource().getMessage(code, args, locale);
	}

	@Override
	public String getMessage(MessageSourceResolvable resolvable, Locale locale) throws NoSuchMessageException {
		return getMessageSource().getMessage(resolvable, locale);
	}

	/**
	 * Return the internal MessageSource used by the context.
	 * @return the internal MessageSource (never {@code null})
	 * @throws IllegalStateException if the context has not been initialized yet
	 */
	private MessageSource getMessageSource() throws IllegalStateException {
		if (this.messageSource == null) {
			throw new IllegalStateException("MessageSource not initialized - " +
					"call 'refresh' before accessing messages via the context: " + this);
		}
		return this.messageSource;
	}

	/**
	 * Return the internal message source of the parent context if it is an
	 * AbstractApplicationContext too; else, return the parent context itself.
	 */
	@Nullable
	protected MessageSource getInternalParentMessageSource() {
		return (getParent() instanceof AbstractApplicationContext ?
				((AbstractApplicationContext) getParent()).messageSource : getParent());
	}


	//---------------------------------------------------------------------
	// Implementation of ResourcePatternResolver interface
	//---------------------------------------------------------------------

	@Override
	public Resource[] getResources(String locationPattern) throws IOException {
		return this.resourcePatternResolver.getResources(locationPattern);
	}


	//---------------------------------------------------------------------
	// Implementation of Lifecycle interface
	//---------------------------------------------------------------------

	@Override
	public void start() {
		getLifecycleProcessor().start();
		publishEvent(new ContextStartedEvent(this));
	}

	@Override
	public void stop() {
		getLifecycleProcessor().stop();
		publishEvent(new ContextStoppedEvent(this));
	}

	@Override
	public boolean isRunning() {
		return (this.lifecycleProcessor != null && this.lifecycleProcessor.isRunning());
	}


	//---------------------------------------------------------------------
	// Abstract methods that must be implemented by subclasses
	//---------------------------------------------------------------------
	/**
	 * Subclasses must implement this method to perform the actual configuration load.
	 * The method is invoked by {@link #refresh()} before any other initialization work.
	 * <p>A subclass will either create a new bean factory and hold a reference to it,
	 * or return a single BeanFactory instance that it holds. In the latter case, it will
	 * usually throw an IllegalStateException if refreshing the context more than once.
	 * @throws BeansException if initialization of the bean factory failed
	 * @throws IllegalStateException if already initialized and multiple refresh
	 * attempts are not supported
	 *
	 * 默认实现类为GenericConfigApplicationContext
	 */
	protected abstract void refreshBeanFactory() throws BeansException, IllegalStateException;

	/**
	 * Subclasses must implement this method to release their internal bean factory.
	 * This method gets invoked by {@link #close()} after all other shutdown work.
	 * <p>Should never throw an exception but rather log shutdown failures.
	 *
	 * 默认实现类为AbstractRefreshableApplicationContext
	 */
	protected abstract void closeBeanFactory();

	/**
	 * Subclasses must return their internal bean factory here. They should implement the
	 * lookup efficiently, so that it can be called repeatedly without a performance penalty.
	 * <p>Note: Subclasses should check whether the context is still active before
	 * returning the internal bean factory. The internal factory should generally be
	 * considered unavailable once the context has been closed.
	 * @return this application context's internal bean factory (never {@code null})
	 * @throws IllegalStateException if the context does not hold an internal bean factory yet
	 * (usually if {@link #refresh()} has never been called) or if the context has been
	 * closed already
	 * @see #refreshBeanFactory()
	 * @see #closeBeanFactory()
	 *
	 * 默认实现类为GenericApplicationContext
	 */
	@Override
	public abstract ConfigurableListableBeanFactory getBeanFactory() throws IllegalStateException;
	/**
	 * Return information about this context.
	 */
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder(getDisplayName());
		sb.append(", started on ").append(new Date(getStartupDate()));
		ApplicationContext parent = getParent();
		if (parent != null) {
			sb.append(", parent: ").append(parent.getDisplayName());
		}
		return sb.toString();
	}

}
