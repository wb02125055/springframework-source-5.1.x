package com.wb.spring.share.class2.javaconfig;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * Description: 通过Annotation配置方式实现AOP
 * @author wangbin33
 */
public class TestMainWithAnnotation {
	public static void main(String[] args) {
		ApplicationContext acx = new AnnotationConfigApplicationContext(AopConfig.class);
		MathCalc mathCalc = acx.getBean(MathCalc.class);
		int result = mathCalc.div(2, 1);
		System.out.println("result: " + result);
	}
}
