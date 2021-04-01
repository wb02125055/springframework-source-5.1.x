package com.wb.springmvc.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import org.springframework.stereotype.Controller;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.config.annotation.DefaultServletHandlerConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

/**
 * Created by wangbin33 on 2020/1/2.
 *
 * 应用的子容器，只扫描com.wb.springmvc目录下类标注@Controller注解的Class
 */
@Configuration
@ComponentScan(value = "com.wb.springmvc",
		includeFilters = {@ComponentScan.Filter(
				type = FilterType.ANNOTATION,
				classes = {Controller.class})},
		useDefaultFilters = false)
public class AppConfig extends WebMvcConfigurationSupport {
	/**
	 * 配置视图解析器对应的Bean，也就是spring-mvc.xml中经常配置的那一坨
	 */
	@Bean
	public ViewResolver viewResolver() {
		InternalResourceViewResolver resolver = new InternalResourceViewResolver();
		resolver.setPrefix("/WEB-INF/views/");
		resolver.setSuffix(".jsp");
		resolver.setExposeContextBeansAsAttributes(true);
		return resolver;
	}
	/**
	 * 启用spring mvc 的注解
	 */
	public void configureDefaultServletHandling(DefaultServletHandlerConfigurer configurer) {
		configurer.enable();
	}
}
