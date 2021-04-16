package com.wangbin.blog.autowire;

import com.wangbin.blog.autowire.config.AutoConfig;
import com.wangbin.blog.autowire.dao.TestDao;
import com.wangbin.blog.autowire.service.TestService;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * Description:
 *
 * @author wangbin33
 * @date 2021/4/16 14:31
 */
public class TestMain {

	public static void main(String[] args) {
		ApplicationContext acx = new AnnotationConfigApplicationContext(AutoConfig.class);

		TestService testService = acx.getBean(TestService.class);

//		System.out.println(testService);
		testService.printFlag();
	}
}
