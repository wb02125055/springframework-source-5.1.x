package com.wb.spring.aop.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

/**
 * @author wangbin33
 * @date Created in 18:45 2019/10/6
 * 注解式AOP原理分析：【看容器中注册了什么组件，这个组件什么时候工作，这个组件的功能是什么？】
 * 	@EnableAspectJAutoProxy:
 * 	1.@EnableAspectJAutoProxy原理？
 * 	 	@Import(AspectJAutoProxyRegistrar.class): 给容器中导入AspectJAutoProxyRegistrar
 *         利用AspectJAutoProxyRegistrar自定义给容器中注册Bean.
 *		    org.springframework.aop.config.internalAutoProxyCreator=AnnotationAwareAspectJAutoProxyCreator
 *         给容器中注册一个AnnotationAwareAspectJAutoProxyCreator.
 *	2.AnnotationAwareAspectJAutoProxyCreator：
 *	(1)继承关系：
 *		AnnotationAwareAspectJAutoProxyCreator
 *			-> 	继承了AspectJAwareAdvisorAutoProxyCreator
 *				-> 继承了AbstractAdvisorAutoProxyCreator
 *					-> 继承了AbstractAutoProxyCreator
 *						-> 实现了(implements)SmartInstantiationAwareBeanPostProcessor(Bean的后置处理器)接口和BeanFactoryAware接口
 *					    关注该后置处理器(在Bean初始化前后执行)，自动装配BeanFactory.
 *	(2)分析后置处理器和Aware接口方法中的执行过程
 *		AbstractAutoProxyCreator.setBeanFactory 设置bean工厂.
 *		AbstractAutoProxyCreator.postProcessBeforeInstantiation 后置处理器逻辑.
 *
 *		AbstractAdvisorAutoProxyCreator.setBeanFactory() 重写了setBeanFactory，所以调用的应该是子类的setBeanFactory方法
 *			setBeanFactory方法中调用了 initBeanFactory()方法
 *
 * 		AspectJAwareAdvisorAutoProxyCreator中没有和aware接口和后置处理器相关的方法.
 *
 *		AnnotationAwareAspectJAutoProxyCreator.initBeanFactory() 该方法在AbstractAdvisorAutoProxyCreator类的setBeanFactory中调用了，
 *	 	  但是又被子类重写了，所以父类中调用initBeanFactory方法的时候，调用的是本类中的initBeanFactory方法.
 *
 *
 *	3.执行流程：
 *		(1)通过配置类，创建IOC容器
 *		(2)注册配置类，调用refresh方法
 *		(3)registerBeanPostProcessors()，注册Bean的后置处理器，来拦截bean的创建.
 *			(1)先获取IOC容器中已经定义的需要创建对象的BeanPostProcessor
 *			(2)给容器中添加其他的BeanPostProcessor
 *			(3)优先注册实现了PriorityOrdered接口的BeanPostProcessor
 *			(4)再给容器中注册实现了Ordered接口的BeanPostProcessor
 *			(5)最后注册没实现优先级接口的BeanPostProcessor
 *			(6)注册BeanPostProcessor，实际上就是创建BeanPostProcessor对象，并保存到容器中.
 *				创建名称为：org.springframework.aop.config.internalAutoProxyCreator类型为AnnotationAwareAspectJAutoProxyCreator的bean对象.
 *				(1)创建Bean的实例
 *				(2)populateBean(beanName, mbd, instanceWrapper)，被bean的属性赋值.
 *				(3)initializeBean(beanName, exposedObject, mbd)，初始化Bean.
 *					初始化Bean
 *					(1)invokeAwareMethods(beanName, bean)，处理Aware接口的方法回调
 *					(2)applyBeanPostProcessorsBeforeInitialization(wrappedBean, beanName)，应用后置处理器的postProcessBeforeInitialization(result, beanName)
 *					(3)invokeInitMethods()，执行自定义的初始化方法，例如：init-method或者destroy-method
 *					(4)applyBeanPostProcessorsAfterInitialization(wrappedBean, beanName)，应用后置处理器的postProcessAfterInitialization(result, beanName)
 *				(4)AnnotationAwareAspectJAutoProxyCreator创建成功，并创建了一个包装类aspectJAdvisorsBuilder
 *			(7)把BeanPostProcessor注册到BeanFactory中
 *				beanFactory.addBeanPostProcessor(postProcessor)
 *	//////////////////////以上是创建AnnotationAwareAspectJAutoProxyCreator的过程////////////////////////
 *		AnnotationAwareAspectJAutoProxyCreator =>继承了 InstantiationAwareBeanPostProcessor 接口.
 *		(4)finishBeanFactoryInitialization(beanFactory)，完成BeanFactory初始化，创建剩余的单实例Bean.
 *			(1)遍历获取容器中所有的Bean，依次创建对象getBean(beanName)
 *				getBean(beanName) -> doGetBean() -> getSingleton()
 *			(2)创建bean
 *				【AnnotationAwareAspectJAutoProxyCreator会在所有的Bean创建之前有一个拦截，因为它属于InstantiationAwareBeanPostProcessor类型的后置处理器，
 *					而该种处理器就是在Bean创建之前执行。会调用postProcessBeforeInstantiation方法】
 *
 *				(1)先从缓存中获取当前bean，如果能获取到，说明bean是之前被创建过的，直接使用；否则再去获取；
 *				   只要创建好的Bean，都会被缓存起来.
 *
 *				(2)createBean()，创建Bean对象。注意：AnnotationAwareAspectJAutoProxyCreator会在任何Bean创建之前返回Bean的实例
 *					【BeanPostProcessor】是在Bean实例创建完成，初始化前后调用的
 *					【InstantiationAwareBeanPostProcessor】是在创建Bean实例之前先尝试用后置处理器返回对象的.
 *					(1)resolveBeforeInstantiation(beanName, mbdToUse)，解析BeforeInstantiation
 *					   希望后置处理器在此可以创建一个代理对象；如果能返回代理对象就返回，如果不能则继续向下执行doCreateBean
 *					   (1)后置处理器先尝试返回对象
 *					   		// 拿到所有的后置处理器，如果是InstantiationAwareBeanPostProcessor，则执行后置处理器的postProcessBeforeInstantiation方法
 *					   		bean = applyBeanPostProcessorsBeforeInstantiation(targetType, beanName);
 *					   		//
 *					   		if (bean != null) {
								bean = applyBeanPostProcessorsAfterInitialization(bean, beanName);
							}
 *					(2)doCreateBean(beanName, mbdToUse, args)，真正去创建一个Bean实例，和上述步骤创建Bean实例的步骤相同.
 *
 *		AnnotationAwareAspectJAutoProxyCreator【InstantiationAwareBeanPostProcessor】的作用：
 *		(1)在每一个Bean创建之前，调用postProcessBeforeInstantiation方法
 *			关心MathCalculator和LogAspect的创建
 *			(1)先判断当前Bean是否在advisedBean中(advisedBeans中保存了所有需要增强的bean)
 *			(2)使用isInfrastructureClass(Class<?> beanClass)方法判断当前bean是否为基础类型的，
 *					即：是否实现了Advice，Pointcut，Advisor，AopInfrastructureBean接口。
 *				或者是否为切面:aspectJAdvisorFactory.isAspect(beanClass)，加了@Aspect注解的.
 *			(3)判断是否需要跳过.
 *				(1)获取候选的增强器(切面里面的通知方法)【List<Advisor> candidateAdvisors = findCandidateAdvisors()】
 *					每一个封装的通知方法的增强器是InstantiationModelAwarePointcutAdvisor，
 *					判断逻辑中判断每一个增强器是否为AspectJPointcutAdvisor类型的 (if (advisor instanceof AspectJPointcutAdvisor) )
 *				(2)返回false，跳过.
 *		(2)创建对象
 *			调用postProcessAfterInitialization方法：
 *				该方法中调用wrapIfNecessary(bean, beanName, cacheKey)方法
 *				(1)调用getAdvicesAndAdvisorsForBean(bean.getClass(), beanName, null)方法获取Bean的所有增强器
 *					(1)调用findAdvisorsThatCanApply方法找到所有候选的Bean增强器（找哪些通知方法需要切入当前Bean方法的）
 *					(2)获取到能在当前Bean中使用的增强器
 *					(3)给增强器排序
 *				(2)记录当前已经被增强的Bean到advisedBeans中
 *				(3)如果当前Bean需要增强，则创建当前Bean的代理对象.
 *					(1)获取所有的增强器（通知方法）
 *					(2)保存到proxyFactory中.
 *					(3)创建代理对象，Spring自动决定创建哪种代理.
 *						JdkDynamicAopProxy(config)，jdk动态代理
 *						ObjenesisCglibAopProxy(config)，cglib动态代理
 *				(4)给容器中返回当前组件使用Cglib增强之后的代理对象.
 *				(5)以后容器中获取到的就是组件的代理对象，执行目标方法的时候，代理对象就会执行通知方法的流程.
 * 		(3)目标方法执行
 *			容器中保存了组件的代理对象(cglib增强之后的对象)，这个对象里面保存了详细信息(比如：增强器，目标对象等)
 *			(1)CglibAopProxy.intercept方法；拦截目标方法的执行
 *			(2)根据ProxyFactory对象获取目标方法的拦截器链
 *				List<Object> chain = this.advised.getInterceptorsAndDynamicInterceptionAdvice(method, targetClass)
 *				(1)创建一个保存拦截器器链的List，其中的元素包括默认的ExposeInvocationInterceptor和其他自定义的增强器.
 *				(2)遍历所有的增强器，将其包装为一个Interceptor；通过registry.getInterceptors(advisor)方法包装
 *				(3)将增强器转为MethodInterceptor，并封装到List<MethodInterceptor>中
 *					如果是MethodInterceptor，直接加入到List中
 *					如果不是MethodInterceptor，使用AdvisorAdapter（增强器的适配器）将增强器转为MethodInterceptor
 *					将转换完成的MethodInterceptor集合返回.
 *			(3)如果没有拦截器链，直接执行目标方法
 *				拦截器链: 每一个通知方法又被包装为方法拦截器，利用MethodInterceptor机制
 *			(4)如果有拦截器链，把需要执行的目标对象，目标方法，拦截器链等信息传入创建一个CglibMethodInvocation，并调用proceed方法，得到返回值.
 *				Object retVal = new CglibMethodInvocation(proxy, target, method, args, targetClass, chain, methodProxy).proceed()
 *			(5)拦截器链的触发过程.
 *				(1)如果没有拦截器，直接执行目标方法。或者拦截器的索引和拦截器的长度减1相等（即到了最后一个拦截器）
 *				(2)链式获取每一个拦截器，拦截器执行invoke方法，每一个拦截器等待下一个拦截器执行完成以后再来执行；
 *					通过拦截器链的机制，保证通知方法与目标方法的执行顺序.
 *
 *
 * 	总结：
 * 		1.@EnableAspectJAutoProxy 开启AOP功能
 * 		2.@EnableAspectJAutoProxy会给容器中注册一个类型为AnnotationAwareAspectJAutoProxyCreator的组件
 * 		3.AnnotationAwareAspectJAutoProxyCreator是一个后置处理器
 * 		4.容器的创建流程:
 * 			(1)registerBeanPostProcessors会注册后置处理器，创建AnnotationAwareAspectJAutoProxyCreator对象
 * 			(2)finishBeanFactoryInitialization，初始化剩余的单实例Bean
 * 				(1)创建业务逻辑组件和切面组件
 * 				(2)AnnotationAwareAspectJAutoProxyCreator会拦截组件的创建过程
 * 				(3)在组建创建完成之后，会判断组件是否需要增强
 * 					如果是：切面中的包装方法，包装为增强器(Advisor)，给业务逻辑组件创建代理对象(默认是cglib，如果有接口，使用jdk)
 * 		5.执行目标方法:
 * 			(1)代理对象执行目标方法
 * 			(2)CglibAopProxy.intercept()
 * 				(1)得到目标方法的拦截器链(增强器包装为拦截器MethodInterceptor)
 * 				(2)利用拦截器链的链式机制，依次进入每一个拦截器进行执行
 * 				(3)执行效果：
 * 					正常执行：前置通知->目标方法->后置通知->返回通知
 *					异常执行：前置通知->目标方法->后置通知->异常通知
 */
@Configuration
@ComponentScan(basePackages = "com.wb.spring.aop")
// 开启AOP支持
@EnableAspectJAutoProxy
public class AopConfig {
}
