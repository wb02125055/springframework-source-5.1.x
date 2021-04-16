package com.wangbin.blog.autowire.config;

import com.wangbin.blog.autowire.dao.TestDao;
import com.wangbin.blog.autowire.dao.impl.Test1DaoImpl;
import com.wangbin.blog.autowire.dao.impl.Test2DaoImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * Description:
 *
 * @author wangbin33
 * @date 2021/4/16 14:32
 */
@Configuration
@ComponentScan(basePackages = "com.wangbin.blog.autowire")
public class AutoConfig {
	@Bean("testDao")
	public TestDao testDao() {
		TestDao dao = new Test1DaoImpl();
		System.out.println("-------------------");
		return dao;
	}

//	@Bean("testDao")
//	public TestDao testDao1() {
//		TestDao dao = new Test2DaoImpl();
//		return dao;
//	}
}
