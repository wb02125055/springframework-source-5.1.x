package com.wb.spring.share.class1.javaconfig;

import com.wb.spring.share.class1.javaconfig.config.ComponentScanConfig;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * Description:
 *
 * @author wangbin33
 * @date 2020/11/1 21:07
 */
public class TestMain {
	public static void main(String[] args) {
		ConfigurableApplicationContext acx = new AnnotationConfigApplicationContext(ComponentScanConfig.class);
		String[] beanDefinitionNames = acx.getBeanDefinitionNames();
		for (String beanDefinitionName : beanDefinitionNames) {
			System.out.println(beanDefinitionName);
		}
		acx.close();
	}
}
