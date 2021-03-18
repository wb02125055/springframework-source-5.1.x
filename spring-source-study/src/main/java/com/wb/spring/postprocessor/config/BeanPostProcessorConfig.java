package com.wb.spring.postprocessor.config;

import com.wb.spring.postprocessor.domain.Pig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * Created by wangbin33 on 2020/1/8.
 *
 * 后置处理器的配置类
 */
@Configuration
@ComponentScan(basePackages = "com.wb.spring.postprocessor")
public class BeanPostProcessorConfig {

	@Bean
	public Pig pig() {
		return new Pig();
	}
}
