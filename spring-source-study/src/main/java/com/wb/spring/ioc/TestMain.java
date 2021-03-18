package com.wb.spring.ioc;

import com.wb.spring.ioc.config.DaoConfig;
import com.wb.spring.ioc.config.ServiceConfig;
import com.wb.spring.ioc.domain.Color;
import com.wb.spring.ioc.domain.Sheep;
import com.wb.spring.ioc.service.SheepService;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.util.Optional;

/**
 * @author wangbin33
 * @date Created in 15:58 2019/11/24
 */
public class TestMain {

	public static void main(String[] args) throws Exception {


		String name = "";

		String myName = Optional.ofNullable(name).orElse("MyName");

		System.out.println(myName);


//		ApplicationContext acx = new AnnotationConfigApplicationContext(ServiceConfig.class, DaoConfig.class);
//
//		SheepService sheepService = (SheepService) acx.getBean("sheepService");
//
//		Sheep sheep = new Sheep();
//		sheep.setId(1L);
//		sheep.setName("sheep1");
//
//		Color color = new Color();
//		color.setName("红色");
//
//		sheep.setColor(color);
//
//		sheepService.save(sheep);
	}
}
