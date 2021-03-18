package com.wb.spring.share.class2.xml;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Description: 通过XML方式实现AOP
 * @author wangbin33
 */
public class TestMainWithXml {
	public static void main(String[] args) {
		String configLocation = "aop/aop_config.xml";
		ApplicationContext acx = new ClassPathXmlApplicationContext(configLocation);
		MathCalc1 mathCalc1 = (MathCalc1) acx.getBean("mathCalc1");
		int result = mathCalc1.div(2, 0);
		System.out.println(result);
	}
}
