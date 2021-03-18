package com.wb.spring.share.class1.javaconfig.config;

import com.wb.spring.share.class1.domain.HotDog;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * Description:
 *
 * @author wangbin33
 * @date 2020/11/17 9:47
 */
@Configuration
@ComponentScan(basePackages = {"com.wb.spring.share.class1"})
public class SimpleConfig {
	@Bean
	public HotDog hotDog() {
		HotDog hotDog = new HotDog();
		hotDog.setName("hotDog_Name");
		hotDog.setId(1L);
		return hotDog;
	}
}
