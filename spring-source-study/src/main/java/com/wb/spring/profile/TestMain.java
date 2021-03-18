package com.wb.spring.profile;

import com.wb.spring.profile.beans.User;
import com.wb.spring.profile.config.ProfileConfig;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import javax.sql.DataSource;

/**
 * Created by wangbin33 on 2020/3/8.
 * 如何切换环境：
 * （1）方法1：使用命令行参数的方式：-Dspring.profiles.active=test 'test'为环境标识
 * （2）方法2：使用AnnotationConfigApplicationContext的无参构造，然后设置需要激活的环境
 */
public class TestMain {
	public static void main(String[] args) {

		ApplicationContext acx = new AnnotationConfigApplicationContext(ProfileConfig.class);
		// 1.创建上下文对象
//		AnnotationConfigApplicationContext acx = new AnnotationConfigApplicationContext();
		// 2.设置需要激活的环境表示，可以写多个.
//		acx.getEnvironment().setActiveProfiles("prod");
		// 3.注册配置类
//		acx.register(ProfileConfig.class);
		// 4.执行刷新
//		acx.refresh();

		String[] dataSources = acx.getBeanNamesForType(DataSource.class);
		for(String dataSource : dataSources) {
			System.out.println(dataSource);
		}

//		User user = acx.getBean(User.class);
//		System.out.println(user);
	}
}
