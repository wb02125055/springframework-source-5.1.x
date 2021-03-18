package com.wb.spring.componentregister;

import com.wb.spring.beans.ConfigCenterClient;
import com.wb.spring.beans.ConfigUtil;
import com.wb.spring.componentregister.config.BeanRegisterConfig;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.beans.Introspector;

/**
 * Created by wangbin33 on 2020/1/10.
 */
public class TestMain {
	public static void main(String[] args) {
		AnnotationConfigApplicationContext acx = new AnnotationConfigApplicationContext(BeanRegisterConfig.class);


		String[] beanNames = acx.getBeanDefinitionNames();
		for (String beanName : beanNames) {
			System.out.println(beanName);
		}

//		for (int i = 0; i < 3; i++) {
//			Sheep bean = acx.getBean(Sheep.class);
//			System.out.println(bean);
//		}
//		ConfigCenterClient configCenterClient = (ConfigCenterClient) acx.getBean("configClient");
//		System.out.println("appId: " + configCenterClient.getAppId() + ", appName: " + configCenterClient.getAppName());
		acx.close();


//		String className = "MyJdbc";
//		String className1 = "SEtName";
//		String result = Introspector.decapitalize(className);
//		String setResult = Introspector.decapitalize(className1);
//		System.out.println(result);
//		System.out.println(setResult);
	}
}
