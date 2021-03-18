package com.wb.spring.beandefinition;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.ChildBeanDefinition;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * Created by wangbin33 on 2019/12/28.
 */
public class TestMain {
	public static void main(String[] args) {

		Object obj = new Object();

		AnnotationConfigApplicationContext acx = new AnnotationConfigApplicationContext();

		// 自定义RootBeanDefinition，并注册到Spring容器中
		RootBeanDefinition rootBeanDefinition = new RootBeanDefinition();
		rootBeanDefinition.getPropertyValues().add("name", "王兵");
		rootBeanDefinition.setBeanClass(AdminPO.class);
		rootBeanDefinition.setResourceDescription("初始化Bean失败！");
		rootBeanDefinition.setDescription("这是一个RootBeanDefinition.");
		// 设置Bean的角色
		rootBeanDefinition.setRole(BeanDefinition.ROLE_INFRASTRUCTURE);
		// 设置当前的Bean是否为抽象的
		rootBeanDefinition.setAbstract(false);
//		rootBeanDefinition.setSource(obj);
//		rootBeanDefinition.setAttribute("singleton", BeanDefinition.SCOPE_PROTOTYPE);

//		System.out.println(rootBeanDefinition.getScope());


		// 自定义ChildBeanDefinition，并注册到Spring容器中，bean1表示childBeanDefinition的父bean名称为bean1
		ChildBeanDefinition childBeanDefinition = new ChildBeanDefinition("bean1");
		childBeanDefinition.getPropertyValues().add("age", 12);
		childBeanDefinition.setBeanClass(AdminPO.class);


		acx.registerBeanDefinition("bean1", rootBeanDefinition);
		acx.registerBeanDefinition("bean2", childBeanDefinition);


		childBeanDefinition.setAttribute("wb", "wangbing");
		System.out.println("wb的值：" + childBeanDefinition.getAttribute("wb"));


		System.out.println("source: " + rootBeanDefinition.getSource());

		acx.refresh();

		System.out.println("bean1: " + acx.getBean("bean1"));
		System.out.println("bean2: " + acx.getBean("bean2"));
	}
}
