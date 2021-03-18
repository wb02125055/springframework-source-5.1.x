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

package org.springframework.transaction.annotation;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Role;
import org.springframework.transaction.config.TransactionManagementConfigUtils;
import org.springframework.transaction.interceptor.BeanFactoryTransactionAttributeSourceAdvisor;
import org.springframework.transaction.interceptor.TransactionAttributeSource;
import org.springframework.transaction.interceptor.TransactionInterceptor;

/**
 * {@code @Configuration} class that registers the Spring infrastructure beans
 * necessary to enable proxy-based annotation-driven transaction management.
 *
 * @author Chris Beams
 * @since 3.1
 * @see EnableTransactionManagement
 * @see TransactionManagementConfigurationSelector
 */
@Configuration
public class ProxyTransactionManagementConfiguration extends AbstractTransactionManagementConfiguration {

	/**
	 * 给容器中注册事务增强器.
	 * @return 容器中的事务增强器
	 */
	@Bean(name = TransactionManagementConfigUtils.TRANSACTION_ADVISOR_BEAN_NAME)
	@Role(BeanDefinition.ROLE_INFRASTRUCTURE)
	public BeanFactoryTransactionAttributeSourceAdvisor transactionAdvisor() {
		BeanFactoryTransactionAttributeSourceAdvisor advisor = new BeanFactoryTransactionAttributeSourceAdvisor();

		// 将@Transactional注解中的元注解信息解析出来放入到事务增强器中.
		advisor.setTransactionAttributeSource(transactionAttributeSource());

		// 将增强之后的方法拦截器链放入到事务增强器中，在执行目标方法时，会执行方法拦截器中的invoke方法
		// 在invoke方法中通过在代理对象执行目标方法前后来开启、关闭、或者回滚事务.
		advisor.setAdvice(transactionInterceptor());
		if (this.enableTx != null) {
			advisor.setOrder(this.enableTx.<Integer>getNumber("order"));
		}
		return advisor;
	}

	/**
	 * 将声明式事务注解@Transactional中的属性解析为一个Bean，并注入到Spring容器中
	 * 方法中包括了对@Transactional注解中属性的解析操作.
	 * @return 事务注解@Transactional中的注解元数据
	 */
	@Bean
	@Role(BeanDefinition.ROLE_INFRASTRUCTURE)
	public TransactionAttributeSource transactionAttributeSource() {
		// 解析注解中的属性
		return new AnnotationTransactionAttributeSource();
	}

	/**
	 * 创建事务方法对应的方法拦截器
	 * @return 事务方法对应的拦截器
	 */
	@Bean
	@Role(BeanDefinition.ROLE_INFRASTRUCTURE)
	public TransactionInterceptor transactionInterceptor() {
		TransactionInterceptor interceptor = new TransactionInterceptor();

		// 设置事务注解的元注解信息到方法拦截器中
		interceptor.setTransactionAttributeSource(transactionAttributeSource());

		// 如果配置了事务管理器，则设置事务管理器到方法拦截器中.
		if (this.txManager != null) {
			interceptor.setTransactionManager(this.txManager);
		}
		return interceptor;
	}

}
