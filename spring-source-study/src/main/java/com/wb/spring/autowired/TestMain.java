package com.wb.spring.autowired;

import com.wb.spring.autowired.config.AutowiredConfig;
import com.wb.spring.autowired.dao.UserDao;
import com.wb.spring.autowired.service.UserService;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * Created by wangbin33 on 2020/3/3.
 */
public class TestMain {
	public static void main(String[] args) {
		ApplicationContext acx = new AnnotationConfigApplicationContext(AutowiredConfig.class);

//		UserDao userDao = acx.getBean(UserDao.class);
//		System.out.println(userDao);

		UserService userService = acx.getBean(UserService.class);
		userService.printUserDao();
	}
}
