package com.wb.spring.beandefinition;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * Created by wangbin33 on 2019/12/28.
 */
public class RootBeanDefinitionDemo1 {
	public static void main(String[] args) {
		AnnotationConfigApplicationContext acx = new AnnotationConfigApplicationContext(RootBeanDefinitionConfig.class);

		// 使用GenericBeanDefinition给工厂中注入bean
		GenericBeanDefinition parentBean = new GenericBeanDefinition();
		parentBean.setBeanClass(ParentPO.class);
		parentBean.setBeanClassName(ParentPO.class.getName());
		parentBean.setScope(BeanDefinition.SCOPE_PROTOTYPE);

		GenericBeanDefinition childBean = new GenericBeanDefinition();
		childBean.setBeanClass(ChildPO.class);
		childBean.setParentName(parentBean.getBeanClassName());

		acx.registerBeanDefinition("parent", parentBean);
		acx.registerBeanDefinition("child", childBean);

		System.out.println(acx.getBeanDefinition("parent"));
		System.out.println(acx.getBeanDefinition("child"));
		System.out.println(acx.getBeanDefinition("child").getParentName());
	}
}
