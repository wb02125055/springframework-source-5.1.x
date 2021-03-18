package com.wb.spring.aop.exposeproxy;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Service;

/**
 * Created by wangbin33 on 2020/2/15.
 */
@Service
@Aspect
public class StudentInterceptor {
	@Before("execution(* com.wb.spring.aop.exposeproxy..*.*(..))")
	public void before() {
		System.out.println("StudentInterceptor invoke ...");
	}
}
