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

package org.springframework.scheduling.config;

import java.util.concurrent.RejectedExecutionHandler;

import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.task.TaskExecutor;
import org.springframework.lang.Nullable;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.util.StringUtils;

/**
 * {@link FactoryBean} for creating {@link ThreadPoolTaskExecutor} instances,
 * primarily used behind the XML task namespace.
 *
 * @author Mark Fisher
 * @author Juergen Hoeller
 * @since 3.0
 */
public class TaskExecutorFactoryBean implements
		FactoryBean<TaskExecutor>, BeanNameAware, InitializingBean, DisposableBean {

	@Nullable
	private String poolSize;

	@Nullable
	private Integer queueCapacity;

	@Nullable
	private RejectedExecutionHandler rejectedExecutionHandler;

	@Nullable
	private Integer keepAliveSeconds;

	@Nullable
	private String beanName;

	@Nullable
	private ThreadPoolTaskExecutor target;


	public void setPoolSize(String poolSize) {
		this.poolSize = poolSize;
	}

	public void setQueueCapacity(int queueCapacity) {
		this.queueCapacity = queueCapacity;
	}

	public void setRejectedExecutionHandler(RejectedExecutionHandler rejectedExecutionHandler) {
		this.rejectedExecutionHandler = rejectedExecutionHandler;
	}

	public void setKeepAliveSeconds(int keepAliveSeconds) {
		this.keepAliveSeconds = keepAliveSeconds;
	}

	@Override
	public void setBeanName(String beanName) {
		// 在创建factoryBean的时候进行回调
		this.beanName = beanName;
	}


	@Override
	public void afterPropertiesSet() {
		// 在创建factoryBean的时候进行回调
		ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();

		// 设置线程池相关参数
		determinePoolSizeRange(executor);
		if (this.queueCapacity != null) {
			executor.setQueueCapacity(this.queueCapacity);
		}
		if (this.keepAliveSeconds != null) {
			executor.setKeepAliveSeconds(this.keepAliveSeconds);
		}
		if (this.rejectedExecutionHandler != null) {
			executor.setRejectedExecutionHandler(this.rejectedExecutionHandler);
		}
		if (this.beanName != null) {
			executor.setThreadNamePrefix(this.beanName + "-");
		}
		// 执行executor的afterPropertiesSet方法，初始化线程池
		executor.afterPropertiesSet();
		this.target = executor;
	}

	private void determinePoolSizeRange(ThreadPoolTaskExecutor executor) {
		// 如果设置了poolSize
		if (StringUtils.hasText(this.poolSize)) {
			try {
				int corePoolSize;
				int maxPoolSize;
				int separatorIndex = this.poolSize.indexOf('-');
				if (separatorIndex != -1) {
					// 获取核心线程数和最大线程数，通过xml配置文件中的pool-size="5-10"来获取，5表示核心线程数，10表示最大线程数
					corePoolSize = Integer.parseInt(this.poolSize.substring(0, separatorIndex));
					maxPoolSize = Integer.parseInt(this.poolSize.substring(separatorIndex + 1));

					// 如果核心线程数大于最大线程数，则抛出异常
					if (corePoolSize > maxPoolSize) {
						throw new IllegalArgumentException(
								"Lower bound of pool-size range must not exceed the upper bound");
					}
					// 如果没有设置线程池工作队列的容量
					if (this.queueCapacity == null) {
						// No queue-capacity provided, so unbounded
						if (corePoolSize == 0) {

							// 如果同时设置了核心线程数和最大线程数，而且核心线程数设置的值为0，则会自动将最大线程数设置和和线程数相同，而且允许核心线程超时

							// Actually set 'corePoolSize' to the upper bound of the range
							// but allow core threads to timeout...

							// 表示当线程池中没有任务时，将自动销毁线程池中所有的线程
							executor.setAllowCoreThreadTimeOut(true);
							corePoolSize = maxPoolSize;
						}
						else {
							// 如果同时设置了核心线程数和最大线程数，但是没有设置queue-capacity，则直接跑出异常
							// Non-zero lower bound implies a core-max size range...
							throw new IllegalArgumentException(
									"A non-zero lower bound for the size range requires a queue-capacity value");
						}
					}
				}
				else {
					// 如果设置的pool-size为一个具体的数字，则表示核心线程数和最大线程数相同
					int value = Integer.parseInt(this.poolSize);
					corePoolSize = value;
					maxPoolSize = value;
				}
				// 设置线程池的核心线程数和最大线程数
				executor.setCorePoolSize(corePoolSize);
				executor.setMaxPoolSize(maxPoolSize);
			}
			catch (NumberFormatException ex) {
				throw new IllegalArgumentException("Invalid pool-size value [" + this.poolSize + "]: only single " +
						"maximum integer (e.g. \"5\") and minimum-maximum range (e.g. \"3-5\") are supported", ex);
			}
		}
	}


	@Override
	@Nullable
	public TaskExecutor getObject() {
		return this.target;
	}

	@Override
	public Class<? extends TaskExecutor> getObjectType() {
		return (this.target != null ? this.target.getClass() : ThreadPoolTaskExecutor.class);
	}

	@Override
	public boolean isSingleton() {
		return true;
	}


	@Override
	public void destroy() {
		if (this.target != null) {
			this.target.destroy();
		}
	}

}
