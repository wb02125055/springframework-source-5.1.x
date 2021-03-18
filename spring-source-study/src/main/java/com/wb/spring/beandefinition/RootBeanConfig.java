package com.wb.spring.beandefinition;

import org.springframework.context.annotation.*;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * Created by wangbin33 on 2019/12/28.
 */
@Configuration
@ComponentScan(basePackages = "com.wb.spring.beandefinition")
@EnableAspectJAutoProxy
@EnableTransactionManagement
public class RootBeanConfig {

	@Bean
	@Lazy(value = true)
	public ChildPO po() {
		return new ChildPO();
	}
}
