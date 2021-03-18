package com.wb.spring.componentregister.config;

import com.wb.spring.componentregister.component.MyInstatiationAwareBeanPostProcessor;
import com.wb.spring.componentregister.component.WindowsConditional;
import com.wb.spring.componentregister.domain.Sheep;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.*;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * Created by wangbin33 on 2020/1/10.
 *
 * 组件注册
 */
@Configuration
@ComponentScan({"com.wb.spring.componentregister"})
@Import({MyInstatiationAwareBeanPostProcessor.class})
@ImportResource(value = "classpath:beans.xml")
@PropertySource(value = "classpath:public.properties", encoding = "UTF-8")
@EnableAspectJAutoProxy
@EnableTransactionManagement
@EnableScheduling
@EnableAsync
public class BeanRegisterConfig {

	@Value("${name:wb}")
	private String name;

	@Bean(initMethod = "init", destroyMethod = "destroy")
	@Scope(value = AnnotatedBeanDefinition.SCOPE_PROTOTYPE)
	@Lazy(value = false)
	@Conditional({WindowsConditional.class})
	// 根据条件判断是否需要像容器中加入Sheep类型的Bean.
	public Sheep sheep() {
		System.out.println("name:" + name);
		return new Sheep();
	}
}
