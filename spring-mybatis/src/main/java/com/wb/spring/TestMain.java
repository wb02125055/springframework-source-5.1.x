package com.wb.spring;

import com.wb.spring.config.SpringMyBatisConfig;
import com.wb.spring.domain.User;
import com.wb.spring.mapper.UserMapper;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

/**
 * @author wangbin33
 * @date 2020/5/10 21:25
 */
public class TestMain {
	public static void main(String[] args) {
		ApplicationContext acx = new AnnotationConfigApplicationContext(SpringMyBatisConfig.class);
		UserMapper userMapper = acx.getBean(UserMapper.class);
		User user = userMapper.findUserById(1);
		System.out.println(user);

//		String a = new String("aaa");
		String a = new String(new char[] {'a','b','c'});



	}
}
