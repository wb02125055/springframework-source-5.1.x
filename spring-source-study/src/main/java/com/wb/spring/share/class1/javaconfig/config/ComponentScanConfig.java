package com.wb.spring.share.class1.javaconfig.config;


import com.wb.spring.share.class1.domain.HotDog;
import com.wb.spring.share.class1.javaconfig.condition.WindowsCondition;
import com.wb.spring.share.class1.javaconfig.ext.SelfBeanDefinitionRegistryPostProcessor;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.*;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Controller;

/**
 * Description:
 *
 * @author wangbin33
 * @date 2020/11/1 21:08
 */
@Configuration
@ComponentScan(value = "com.wb.spring.share.class1",
		excludeFilters = {@ComponentScan.Filter(type = FilterType.ANNOTATION, value = Controller.class)})
//@ComponentScans({@ComponentScan(basePackages = "")})
@Import(SelfBeanDefinitionRegistryPostProcessor.class)
@ImportResource(locations = {"classpath:beans.xml"})
@PropertySource(value = "classpath:public.properties", encoding = "UTF-8")
//@PropertySources(value = {@PropertySource(value = "classpath:module1.properties", encoding = "UTF-8"), @PropertySource(value = "classpath:module2.properties", encoding = "UTF-8")})
@Conditional(value = WindowsCondition.class)
public class ComponentScanConfig {

	// 内部配置类
	@Configuration
	@ComponentScan(value = "com.wb.spring.share.class1")
	class InnerClass {
	}

	@Bean(name = "hotDog", initMethod = "createHotDog", destroyMethod = "eatHotDog")
	@Scope(value = BeanDefinition.SCOPE_SINGLETON)
	@Conditional(value = WindowsCondition.class)
	@Order(value = 1)
	@Lazy(value = false)
	@Role(value = BeanDefinition.ROLE_INFRASTRUCTURE)
	@Description(value = "This is a test.")
	@Primary
	public HotDog hotDog() {
		HotDog hotDog = new HotDog();
		hotDog.setId(1L);
		hotDog.setName("HotDog001");
		return hotDog;
	}
}
