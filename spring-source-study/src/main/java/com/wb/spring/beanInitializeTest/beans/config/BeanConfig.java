package com.wb.spring.beanInitializeTest.beans.config;

import com.wb.spring.beanInitializeTest.beans.MyImportSelector;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * @author wangbin33
 * @date Created in 10:57 2019/11/30
 */
@Configuration
@ComponentScan(basePackages = "com.wb.spring.beanInitializeTest")
//@Import(value = {InstC.class}) // 注入普通的组件
//@Import(value = {MyImportBeanDefinitionRegistrar.class}) // 导入的组件实现了ImportBeanDefinitionRegistrar接口
@Import(value = {MyImportSelector.class})
public class BeanConfig {

//	@Bean
//	@Lazy // Spring容器启动的时候不会初始化，第一次获取Bean的时候才会去初始化.
//	public UserBean userBean() {
//		UserBean user = new UserBean();
//		user.setUuid(UUID.randomUUID().toString());
//		System.out.println("properties set finished...");
//		return user;
//	}
}
