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

package org.springframework.context.support;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.*;
import org.springframework.core.OrderComparator;
import org.springframework.core.Ordered;
import org.springframework.core.PriorityOrdered;
import org.springframework.lang.Nullable;

import java.util.*;

/**
 * Delegate for AbstractApplicationContext's post-processor handling.
 *
 * @author Juergen Hoeller
 * @since 4.0
 */
final class PostProcessorRegistrationDelegate {

	private PostProcessorRegistrationDelegate() {
	}


	public static void invokeBeanFactoryPostProcessors(ConfigurableListableBeanFactory beanFactory,
			List<BeanFactoryPostProcessor> beanFactoryPostProcessors) {

		/** 用来记录已经执行过的后置处理器 */
		Set<String> processedBeans = new HashSet<>();
		/**
		 * 判断beanFactory的类型是否为BeanDefinitionRegistry
		 * 此处beanFactory的类型为：DefaultListableBeanFactory，而DefaultListableBeanFactory是实现了BeanDefinitionRegistry接口的，
		 * 所以此处判断里面的结果为true.
		 *
		 * BeanDefinitionRegistry接口中定义了操作bean定义的常用方法。如：注册bean定义，移除bean定义，获取bean定义的数量，判断是否包含指定的bean定义等...
		 */
		if (beanFactory instanceof BeanDefinitionRegistry) {
			BeanDefinitionRegistry registry = (BeanDefinitionRegistry) beanFactory;

			// 用于存放普通的bean工厂的后置处理器
			List<BeanFactoryPostProcessor> regularPostProcessors = new ArrayList<>();

			/**
			 * 用于存放类型为BeanDefinitionRegistryPostProcessor的bean工厂后置处理器。BeanDefinitionRegistry是bean定义的注册中心。用来存放所有的bean定义.
			 * BeanDefinitionRegistryPostProcessor继承自BeanFactoryPostProcessor
			 */
			List<BeanDefinitionRegistryPostProcessor> registryProcessors = new ArrayList<>();

			/**
			 * 遍历所有的BeanFactoryPostProcessor，将普通的BeanFactoryPostProcessor和BeanDefinitionRegistryPostProcessor区分开.
			 */
			for (BeanFactoryPostProcessor postProcessor : beanFactoryPostProcessors) {
				if (postProcessor instanceof BeanDefinitionRegistryPostProcessor) {
					BeanDefinitionRegistryPostProcessor registryProcessor = (BeanDefinitionRegistryPostProcessor) postProcessor;
					// 直接执行BeanDefinitionRegistryPostProcessor接口的postProcessBeanDefinitionRegistry方法.
					registryProcessor.postProcessBeanDefinitionRegistry(registry);
					// 添加到registryProcessors中，用于最后执行postProcessBeanFactory方法
					registryProcessors.add(registryProcessor);
				} else {
					// 普通BeanFactoryPostProcessor，添加到regularPostProcessors中，用于最后执行postProcessBeanFactory方法.
					regularPostProcessors.add(postProcessor);
				}
			}

			// Do not initialize FactoryBeans here: We need to leave all regular beans
			// uninitialized to let the bean factory post-processors apply to them!
			// Separate between BeanDefinitionRegistryPostProcessors that implement
			// PriorityOrdered, Ordered, and the rest.

			// 保存本次将要执行的BeanDefinitionRegistryPostProcessor.
			List<BeanDefinitionRegistryPostProcessor> currentRegistryProcessors = new ArrayList<>();

			// 下面的for循环中是找出所有实现了PriorityOrdered接口的BeanDefinitionRegistryPostProcessor实现类
			// 首先根据类型找出所有实现了BeanDefinitionRegistryPostProcessor接口的Bean的名称，然后依次循环遍历，判断是否实现了PriorityOrdered接口.
			String[] postProcessorNames =
					beanFactory.getBeanNamesForType(BeanDefinitionRegistryPostProcessor.class, true, false);
			for (String ppName : postProcessorNames) {
				if (beanFactory.isTypeMatch(ppName, PriorityOrdered.class)) {
					// 获取ppName对应的Bean实例，并添加到当前要执行的currentRegistryProcessors中

					// 此处会去创建容器中默认后置处理器对应的单实例bean
					// 此处的beanFactory.getBean会根据Bean的类型去创建Bean工厂后置处理器对象，有Bean的创建过程。
					currentRegistryProcessors.add(beanFactory.getBean(ppName, BeanDefinitionRegistryPostProcessor.class));
					// 把将要执行的bean的名称加入到processedBeans中，后续后判断是否已经执行。避免重复执行.
					processedBeans.add(ppName);
				}
			}

			// 根据是否实现了PriorityOrder，Order接口以及具体的order数值来排序.
			sortPostProcessors(currentRegistryProcessors, beanFactory);
			// 添加到registryProcessors，最后用于执行postProcessBeanFactory方法
			registryProcessors.addAll(currentRegistryProcessors);


			// 遍历当前要执行的所有BeanDefinitionRegistryPostProcessor，执行其postProcessBeanDefinitionRegistry方法.
			// 此处有一个重要的Bean定义注册中心的后置处理器：ConfigurationClassPostProcessor，配置类解析、条件注册的回调、方法及配置类的校验等操作
			invokeBeanDefinitionRegistryPostProcessors(currentRegistryProcessors, registry);
			// 执行完毕之后，清空currentRegistryProcessors
			currentRegistryProcessors.clear();

			// 查找所有实现了BeanDefinitionRegistryPostProcessor接口的实现类
			// 重复查找是因为上面执行完了所有的BeanDefinitionRegistryPostProcessor类之后，可能又新增了其他的BeanDefinitionRegistryPostProcessor。
			//    比如：有一个自定义的BeanDefinitionRegistryPostProcessor中实现了PriorityOrdered接口之后，同时实现了BeanDefinitionRegistryPostProcessor接口.
			postProcessorNames = beanFactory.getBeanNamesForType(BeanDefinitionRegistryPostProcessor.class, true, false);
			for (String ppName : postProcessorNames) {
				// 校验是否实现了Ordered接口，并且之前未执行过
				// 使用上述执行完毕之后记录的Set集合判断是否执行过.
				if (!processedBeans.contains(ppName) && beanFactory.isTypeMatch(ppName, Ordered.class)) {
					currentRegistryProcessors.add(beanFactory.getBean(ppName, BeanDefinitionRegistryPostProcessor.class));
					// 执行过之后，将其加入到已经执行过的集合中.
					processedBeans.add(ppName);
				}
			}
			// 排序
			sortPostProcessors(currentRegistryProcessors, beanFactory);
			registryProcessors.addAll(currentRegistryProcessors);
			// 遍历所有的BeanDefinitionRegistryPostProcessor接口的实现类，
			//  并执行BeanDefinitionRegistryPostProcessor的postProcessBeanDefinitionRegistry方法
			invokeBeanDefinitionRegistryPostProcessors(currentRegistryProcessors, registry);
			// 清空
			currentRegistryProcessors.clear();

			// Finally, invoke all other BeanDefinitionRegistryPostProcessors until no further ones appear.
			// 最后，遍历其他除了实现Ordered接口和PriorityOrdered接口的BeanDefinitionRegistryPostProcessor实现类，
			//  并执行其postProcessBeanDefinitionRegistry方法
			boolean reiterate = true;
			while (reiterate) {
				reiterate = false;
				postProcessorNames = beanFactory.getBeanNamesForType(BeanDefinitionRegistryPostProcessor.class, true, false);
				for (String ppName : postProcessorNames) {
					// 过滤掉已经执行过的.
					if (!processedBeans.contains(ppName)) {
						currentRegistryProcessors.add(beanFactory.getBean(ppName, BeanDefinitionRegistryPostProcessor.class));
						processedBeans.add(ppName);
						// 如果有BeanDefinitionRegistryPostProcessor被执行，则有可能产生新的BeanDefinitionRegistryPostProcessor
						// 所以需要再次循环查找一次.
						reiterate = true;
					}
				}
				sortPostProcessors(currentRegistryProcessors, beanFactory);
				registryProcessors.addAll(currentRegistryProcessors);
				// 依次执行BeanDefinitionRegistryPostProcessor的postProcessBeanDefinitionRegistry()方法
				// 执行时机是加载bean定义之前
				invokeBeanDefinitionRegistryPostProcessors(currentRegistryProcessors, registry);
				currentRegistryProcessors.clear();
			}

			// 执行所有BeanDefinitionRegistryPostProcessor接口的postProcessBeanFactory方法
			// 加载bean定义之后.但是实例化bean实例之前
			invokeBeanFactoryPostProcessors(registryProcessors, beanFactory);

			// 执行所有普通的BeanFactoryPostProcessor的postProcessBeanFactory方法。这些BeanFactoryPostProcessor是从方法传进来的.
			invokeBeanFactoryPostProcessors(regularPostProcessors, beanFactory);
		}

		else {
			// Invoke factory processors registered with the context instance.
			invokeBeanFactoryPostProcessors(beanFactoryPostProcessors, beanFactory);
		}


		// 至此，上面的过程已经处理完了入参beanFactoryPostProcessors和容器中所有的BeanDefinitionRegistryPostProcessor
		///////////////////////////////////////////////////////////////////////////////////////////////////////


		// 下面开始处理容器中的所有 BeanFactoryPostProcessor
		// Do not initialize FactoryBeans here: We need to leave all regular beans
		// uninitialized to let the bean factory post-processors apply to them!
		// 查找容器中所有实现了BeanFactoryPostProcessor接口的实现类
		String[] postProcessorNames =
				beanFactory.getBeanNamesForType(BeanFactoryPostProcessor.class, true, false);

		// Separate between BeanFactoryPostProcessors that implement PriorityOrdered,
		// Ordered, and the rest.
		// 用于存放所有实现了PriorityOrdered接口的BeanFactoryPostProcessor
		List<BeanFactoryPostProcessor> priorityOrderedPostProcessors = new ArrayList<>();
		// 用于存放实现了Ordered接口的BeanFactoryPostProcessor的beanName
		List<String> orderedPostProcessorNames = new ArrayList<>();
		// 用于实现了普通未实现Ordered接口的BeanFactoryPostProcessor的beanName
		List<String> nonOrderedPostProcessorNames = new ArrayList<>();
		// 循环所有的BeanFactoryPostProcessors
		for (String ppName : postProcessorNames) {
			// 如果前面已经执行过，直接跳过
			if (processedBeans.contains(ppName)) {
				// skip - already processed in first phase above
			}
			// 如果实现了PriorityOrdered接口，则获取bean并加入到priorityOrderedPostProcessors
			else if (beanFactory.isTypeMatch(ppName, PriorityOrdered.class)) {
				priorityOrderedPostProcessors.add(beanFactory.getBean(ppName, BeanFactoryPostProcessor.class));
			}
			// 如果是实现了Ordered接口的BeanFactoryPostProcessor，直接将其beanName放入到orderedPostProcessorNames
			else if (beanFactory.isTypeMatch(ppName, Ordered.class)) {
				orderedPostProcessorNames.add(ppName);
			}
			// 其他普通bean的名称放入到nonOrderedPostProcessorNames
			else {
				nonOrderedPostProcessorNames.add(ppName);
			}
		}

		// 对实现了PriorityOrdered接口的BeanFactoryPostProcessor进行排序
		sortPostProcessors(priorityOrderedPostProcessors, beanFactory);
		// 调用所有实现了PriorityOrdered接口的BeanFactoryPostProcessor的postProcessBeanFactory方法
		invokeBeanFactoryPostProcessors(priorityOrderedPostProcessors, beanFactory);

		// 获取所有实现了Ordered接口的BeanFactoryPostProcessor，并获取Bean实例，添加到orderedPostProcessors中，准备执行
		List<BeanFactoryPostProcessor> orderedPostProcessors = new ArrayList<>();
		for (String postProcessorName : orderedPostProcessorNames) {
			orderedPostProcessors.add(beanFactory.getBean(postProcessorName, BeanFactoryPostProcessor.class));
		}

		// 对orderedPostProcessors排序
		sortPostProcessors(orderedPostProcessors, beanFactory);
		// 调用实现了Ordered接口的BeanFactoryPostProcessor的postProcessBeanFactory方法
		invokeBeanFactoryPostProcessors(orderedPostProcessors, beanFactory);

		// 获取所有普通的未实现Ordered接口和PriorityOrdered接口的BeanFactoryPostProcessor对应的Bean实例，并添加到nonOrderedPostProcessors
		List<BeanFactoryPostProcessor> nonOrderedPostProcessors = new ArrayList<>();
		for (String postProcessorName : nonOrderedPostProcessorNames) {
			nonOrderedPostProcessors.add(beanFactory.getBean(postProcessorName, BeanFactoryPostProcessor.class));
		}

		// 执行剩余未实现Ordered接口和PriorityOrdered接口的BeanFactoryPostProcessor的postProcessBeanFactory方法.
		invokeBeanFactoryPostProcessors(nonOrderedPostProcessors, beanFactory);

		// Clear cached merged bean definitions since the post-processors might have
		// modified the original metadata, e.g. replacing placeholders in values...
		// 删除元数据（bean名称，bean定义等?）缓存，因为在执行后置处理器的过程中，原始的元数据可能已经被修改，例如：属性值中的占位符信息
		beanFactory.clearMetadataCache();
	}

	public static void registerBeanPostProcessors(
			ConfigurableListableBeanFactory beanFactory, AbstractApplicationContext applicationContext) {

		// 根据类型获取所有BeanPostProcessor的BeanName
		String[] postProcessorNames = beanFactory.getBeanNamesForType(BeanPostProcessor.class, true, false);

		// Register BeanPostProcessorChecker that logs an info message
		// a bean is created during BeanPostProcessor instantiation, i.e. when
		// a bean is not eligible for getting processed by all BeanPostProcessors.
		// 注册一个BeanPostProcessorChecker，用于记录Bean在BeanPostProcessor实例化时的信息
		// 此处+1的操作是因为下面给beanFactory中加入了一个BeanPostProcessorChecker后置处理器.
		int beanProcessorTargetCount = beanFactory.getBeanPostProcessorCount() + 1 + postProcessorNames.length;
		beanFactory.addBeanPostProcessor(new BeanPostProcessorChecker(beanFactory, beanProcessorTargetCount));

		// 存放实现了PriorityOrdered接口和未实现PriorityOrdered接口的BeanPostProcessor
		List<BeanPostProcessor> priorityOrderedPostProcessors = new ArrayList<>();
		// 用于存放Spring内部的BeanPostProcessor处理器
		List<BeanPostProcessor> internalPostProcessors = new ArrayList<>();
		// 存放实现了Ordered接口和未实现Ordered接口的BeanPostProcessor的beanName.
		List<String> orderedPostProcessorNames = new ArrayList<>();
		// 存放未实现任何排序接口的BeanPostProcessor的beanName
		List<String> nonOrderedPostProcessorNames = new ArrayList<>();

		// 对所有的BeanPostProcessor的beanName进行分类
		for (String ppName : postProcessorNames) {
			// 实现了PriorityOrdered接口的BeanPostProcessor的beanName
			if (beanFactory.isTypeMatch(ppName, PriorityOrdered.class)) {
				BeanPostProcessor pp = beanFactory.getBean(ppName, BeanPostProcessor.class);
				priorityOrderedPostProcessors.add(pp);
				if (pp instanceof MergedBeanDefinitionPostProcessor) {
					// 实现了MergedBeanDefinitionPostProcessor的BeanPostProcessor
					internalPostProcessors.add(pp);
				}
			}
			// 实现了Ordered接口的BeanPostProcessor的beanName
			else if (beanFactory.isTypeMatch(ppName, Ordered.class)) {
				orderedPostProcessorNames.add(ppName);
			}
			// 未实现PriorityOrdered接口和Ordered接口的BeanPostProcessor的beanName.
			else {
				nonOrderedPostProcessorNames.add(ppName);
			}
		}

		// First, register the BeanPostProcessors that implement PriorityOrdered.
		// 优先注册实现了PriorityOrdered接口的BeanPostProcessor.
		sortPostProcessors(priorityOrderedPostProcessors, beanFactory);
		registerBeanPostProcessors(beanFactory, priorityOrderedPostProcessors);

		// Next, register the BeanPostProcessors that implement Ordered.
		// 保存实现了Ordered接口的BeanPostProcessor.
		List<BeanPostProcessor> orderedPostProcessors = new ArrayList<>();
		for (String ppName : orderedPostProcessorNames) {
			BeanPostProcessor pp = beanFactory.getBean(ppName, BeanPostProcessor.class);
			orderedPostProcessors.add(pp);
			if (pp instanceof MergedBeanDefinitionPostProcessor) {
				internalPostProcessors.add(pp);
			}
		}
		// 注册实现了Ordered接口的BeanPostProcessor.
		sortPostProcessors(orderedPostProcessors, beanFactory);
		registerBeanPostProcessors(beanFactory, orderedPostProcessors);

		// Now, register all regular BeanPostProcessors.
		// 注册普通的BeanPostProcessor(既没有实现PriorityOrdered接口，也没有实现Ordered接口.)
		List<BeanPostProcessor> nonOrderedPostProcessors = new ArrayList<>();
		for (String ppName : nonOrderedPostProcessorNames) {
			BeanPostProcessor pp = beanFactory.getBean(ppName, BeanPostProcessor.class);
			nonOrderedPostProcessors.add(pp);
			if (pp instanceof MergedBeanDefinitionPostProcessor) {
				internalPostProcessors.add(pp);
			}
		}
		registerBeanPostProcessors(beanFactory, nonOrderedPostProcessors);


		// Finally, re-register all internal BeanPostProcessors.
		// 最后注册所有实现了MergedBeanDefinitionPostProcessor接口的BeanPostProcessor
		sortPostProcessors(internalPostProcessors, beanFactory);
		registerBeanPostProcessors(beanFactory, internalPostProcessors);

		// Re-register post-processor for detecting inner beans as ApplicationListeners,
		// moving it to the end of the processor chain (for picking up proxies etc).
		// 添加ApplicationListenerDetector的BeanPostProcessor。
		//   重复添加一次该beanPostProcessor是为了将该beanPostProcessor移动到beanPostProcessor对应的list的最后
		//	 上述处理bean的过程中，可能存在一些内部bean也是ApplicationListenerDetector类型，所以需要在添加一次
		beanFactory.addBeanPostProcessor(new ApplicationListenerDetector(applicationContext));
	}

	private static void sortPostProcessors(List<?> postProcessors, ConfigurableListableBeanFactory beanFactory) {
		Comparator<Object> comparatorToUse = null;
		if (beanFactory instanceof DefaultListableBeanFactory) {
			// 获取设置的比较器
			comparatorToUse = ((DefaultListableBeanFactory) beanFactory).getDependencyComparator();
		}
		if (comparatorToUse == null) {
			// 如果未设置比较器，则使用默认的比较器
			comparatorToUse = OrderComparator.INSTANCE;
		}
		// 使用比较器对postProcessors进行排序
		postProcessors.sort(comparatorToUse);
	}

	/**
	 * Invoke the given BeanDefinitionRegistryPostProcessor beans.
	 */
	private static void invokeBeanDefinitionRegistryPostProcessors(
			Collection<? extends BeanDefinitionRegistryPostProcessor> postProcessors, BeanDefinitionRegistry registry) {

		// 遍历所有实现了BeanDefinitionRegistryPostProcessor接口的实现类，并依次调用这些实现类的postProcessBeanDefinitionRegistry方法.
		// 用于解析配置类的bean定义后置处理器为：ConfigurationClassPostProcessor
		for (BeanDefinitionRegistryPostProcessor postProcessor : postProcessors) {
			postProcessor.postProcessBeanDefinitionRegistry(registry);
		}
	}

	/**
	 * Invoke the given BeanFactoryPostProcessor beans.
	 */
	private static void invokeBeanFactoryPostProcessors(
			Collection<? extends BeanFactoryPostProcessor> postProcessors, ConfigurableListableBeanFactory beanFactory) {

		// 遍历所有实现了BeanFactoryPostProcessor接口的实现类，并调用实现类的postProcessBeanFactory方法.
		for (BeanFactoryPostProcessor postProcessor : postProcessors) {
			postProcessor.postProcessBeanFactory(beanFactory);
		}
	}

	/**
	 * Register the given BeanPostProcessor beans.
	 */
	private static void registerBeanPostProcessors(
			ConfigurableListableBeanFactory beanFactory, List<BeanPostProcessor> postProcessors) {

		for (BeanPostProcessor postProcessor : postProcessors) {
			beanFactory.addBeanPostProcessor(postProcessor);
		}
	}


	/**
	 * BeanPostProcessor that logs an info message when a bean is created during
	 * BeanPostProcessor instantiation, i.e. when a bean is not eligible for
	 * getting processed by all BeanPostProcessors.
	 */
	private static final class BeanPostProcessorChecker implements BeanPostProcessor {

		private static final Log logger = LogFactory.getLog(BeanPostProcessorChecker.class);

		private final ConfigurableListableBeanFactory beanFactory;

		private final int beanPostProcessorTargetCount;

		public BeanPostProcessorChecker(ConfigurableListableBeanFactory beanFactory, int beanPostProcessorTargetCount) {
			this.beanFactory = beanFactory;
			this.beanPostProcessorTargetCount = beanPostProcessorTargetCount;
		}

		@Override
		public Object postProcessBeforeInitialization(Object bean, String beanName) {
			return bean;
		}

		@Override
		public Object postProcessAfterInitialization(Object bean, String beanName) {
			// BeanFactoryPostProcessor类型的不检测，其他类型根据则通过isInfrastructureBean检测当前的bean定义是否为Spring内部使用的bean.
			if (!(bean instanceof BeanPostProcessor)
					&& !isInfrastructureBean(beanName)
					&& this.beanFactory.getBeanPostProcessorCount() < this.beanPostProcessorTargetCount) {
				if (logger.isInfoEnabled()) {
					// 打印当前不符合条件的bean名称。例如：对于自动代理来说是不合格的.
					logger.info("Bean '" + beanName + "' of type [" + bean.getClass().getName() +
							"] is not eligible for getting processed by all BeanPostProcessors " +
							"(for example: not eligible for auto-proxying)");
				}
			}
			return bean;
		}

		private boolean isInfrastructureBean(@Nullable String beanName) {
			if (beanName != null && this.beanFactory.containsBeanDefinition(beanName)) {
				BeanDefinition bd = this.beanFactory.getBeanDefinition(beanName);
				// 是否为Spring内部使用的Bean
				return (bd.getRole() == RootBeanDefinition.ROLE_INFRASTRUCTURE);
			}
			return false;
		}
	}

}
