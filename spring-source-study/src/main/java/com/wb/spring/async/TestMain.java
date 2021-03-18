package com.wb.spring.async;

import com.wb.spring.async.config.AsyncConfig;
import com.wb.spring.async.service.UserService;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * Description: 测试异步
 * @author wangbin33
 * @date 2020/8/10 18:36
 */
public class TestMain {

	public static void main(String[] args) {

		ApplicationContext acx = new AnnotationConfigApplicationContext(AsyncConfig.class);

		UserService userService = acx.getBean(UserService.class);

		userService.insertUser("wangbin33", 24);
	}
}
