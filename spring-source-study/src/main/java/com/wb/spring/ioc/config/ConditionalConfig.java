package com.wb.spring.ioc.config;

import com.wb.spring.ioc.LinuxConditional;
import com.wb.spring.ioc.domain.LinuxCalc;
import com.wb.spring.postprocessor.processor.MyBeanPostProcessor;
import org.springframework.context.annotation.*;

/**
 * Created by wangbin33 on 2020/1/31.
 */
@Configuration
@ComponentScan(basePackages = "com.wb.spring.ioc")
@Import(value = MyBeanPostProcessor.class)
public class ConditionalConfig {

	@Bean("linuxCalc")
	@Conditional(value = {LinuxConditional.class})
	public LinuxCalc calc() {
		return new LinuxCalc();
	}
}
