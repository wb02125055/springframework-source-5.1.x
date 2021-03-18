package com.wb.spring.beandefinition;

import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.annotation.AnnotatedGenericBeanDefinition;

import java.util.Set;

/**
 * Created by wangbin33 on 2019/12/28.
 */
public class AnnotatedBeanDefinitionDemo {
	/**
	 * 在基于注解驱动的Spring应用着，它使用得非常的多。因为获取注解信息非常的方便
	 * @param args
	 */
	public static void main(String[] args) {
		AnnotatedBeanDefinition beanDefinition = new AnnotatedGenericBeanDefinition(RootBeanConfig.class);

		Set<String> typeList = beanDefinition.getMetadata().getAnnotationTypes();
		System.out.println(beanDefinition.getScope());
		System.out.println(beanDefinition.isSingleton());
		System.out.println(beanDefinition.getBeanClassName());

		System.out.println("-------------------------------");

		for (String type : typeList) {
			System.out.println(type);
		}
		/**
		 * 输出：

		 org.springframework.context.annotation.Configuration
		 org.springframework.context.annotation.ComponentScan
		 org.springframework.context.annotation.EnableAspectJAutoProxy
		 org.springframework.transaction.annotation.EnableTransactionManagement

		 */
	}
}
