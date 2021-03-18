package com.wb.spring.postprocessor;

import com.wb.spring.postprocessor.config.BeanPostProcessorConfig;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * Created by wangbin33 on 2020/1/8.
 */
public class TestMain {
	public static void main(String[] args) {
		ConfigurableApplicationContext acx = new AnnotationConfigApplicationContext(BeanPostProcessorConfig.class);
		acx.close();
	}
}
