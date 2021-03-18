package com.wb.spring.ext.config;

import com.wb.spring.beans.Person;
import com.wb.spring.ext.beans.Blue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * @author wangbin33
 * @date Created in 12:16 2019/12/7
 * Spring的扩展
 *
 * 1.BeanPostProcessor: bean的后置处理器，在bean创建对象初始化前后进行拦截执行
 * 2.BeanFactoryPostProcessor: beanFactory的后置处理器，在BeanFactory标准初始化之后调用
 *    标准初始化：所有的bean定义都已经被加载了，但是还没有初始化bean实例，容器中不包含已经实例化的对象。
 *    执行时机：
 *    (1)ioc容器创建对象
 *    (2)invokeBeanFactoryPostProcessor(beanFactory)，执行BeanFactoryPostProcessor
 *    	 从容器中根据类型查找类型为BeanFactoryPostProcessor的组件，并执行他们的方法.
 *    	 在初始化其他组件之前执行postProcessBeanFactory方法
 *
 * 3.BeanDefinitionRegistryPostProcessor extends BeanFactoryPostProcessor
 * 		postProcessBeanDefinitionRegistry()
 * 		在所有bean定义信息将要被加载，bean定义加载之前，但是bean实例还未创建的时候执行
 *
 *		优先于BeanFactoryPostProcessor执行，
 *		可以使用BeanDefinitionRegistryPostProcessor在组件实例化之前给容器中再添加一些自定义组件
 *
 *   执行原理：
 *   	(1) ioc容器创建
 *   	(2) refresh刷新-> invokeBeanFactoryPostProcessors(beanFactory)
 *   	(3) 从容器中获取到所有的BeanDefinitionRegistryPostProcessor组件
 *   		a. 依次触发所有的postProcessBeanDefinitionRegistry()方法
 *   		b. 在依次触发postProcessBeanFactory()方法
 *		(4) 再从容器中找到BeanFactoryPostProcessor组件，然后依次触发postProcessBeanFactory()方法
 *
 * 4.ApplicationListener：监听容器中发布的事件，事件驱动模型开发.
 * 	自定义事件：
 * 		(1) 定义一个监听器监听某个事件(ApplicationEvent及其子类)
 * 		(2) 把监听器加入到容器
 * 		(3) 只要容器中有相关的事件发布，就可以监听到事件，例如容器的默认事件：
 * 					ContextRefreshedEvent: 容器刷新完成，所有bean都已经完全创建，会发布这个事件
 * 					ContextStartedEvent:
 * 					ContextClosedEvent: 容器关闭的时候，会发布
 * 					ContextStoppedEvent
 *
 */
@Configuration
@ComponentScan(basePackages = "com.wb.spring.ext")
public class ExtConfig {

	@Bean
	public Blue blue() {
		return new Blue();
	}

	@Bean
	public Person person() {
		return new Person();
	}
}
