package com.wb.spring.ioc.config;

import com.wb.spring.ioc.domain.Color;
import com.wb.spring.ioc.domain.Sheep;
import com.wb.spring.ioc.service.SheepService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import java.util.UUID;

/**
 * @author wangbin33
 * @date Created in 15:59 2019/11/24
 */
//@Configuration
//@ComponentScan(basePackages = "com.wb.spring.ioc.service")
public class IocConfig {

	@Autowired
	@Qualifier("sheepService")
	private SheepService sheepService;

	@Bean
	public Sheep sheep() {
		Sheep sheep = new Sheep();

		Color color = new Color();
		color.setName("RED");

		sheep.setColor(color);
		sheep.setId(1000L);
		sheep.setName(UUID.randomUUID().toString());

		sheepService.save(sheep);

		return sheep;
	}
}
