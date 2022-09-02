package com.wb.spring.schedule.javaconfig;

import com.wb.spring.schedule.javaconfig.config.ScheduleConfig;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * created by IDEA
 *
 * @author wangbing
 * @date 2022.09.2022/9/2.22:12
 */
public class TestMain {
	public static void main(String[] args) {
		ApplicationContext acx = new AnnotationConfigApplicationContext(ScheduleConfig.class);
	}
}
