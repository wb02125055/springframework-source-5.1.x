package com.wb.spring.aop.test;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Component
@Aspect
public class MyAspect {

	@Around("@args(com.wb.spring.aop.test.MyAnnotation)")
	public Object around(ProceedingJoinPoint pjp) throws Throwable {
		System.out.println("around invoke...");
		return pjp.proceed();
	}
}
