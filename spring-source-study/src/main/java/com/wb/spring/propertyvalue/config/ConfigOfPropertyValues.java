package com.wb.spring.propertyvalue.config;

import com.wb.spring.propertyvalue.domain.Person;
import org.springframework.context.annotation.*;

/**
 * Created by wangbin33 on 2020/2/24.
 */
// PropertySource将外部的配置文件中的key/value解析出来之后，会放入到当前容器运行的环境变量中.
@PropertySource(value = {"classpath:public.properties"},encoding = "UTF-8")
//@PropertySources({@PropertySource(value = {}),@PropertySource(value = {})})
@Configuration
@ComponentScan(basePackages = "com.wb.spring.propertyvalue")

public class ConfigOfPropertyValues {

	@Bean
	public Person person() {
		return new Person();
	}
}
