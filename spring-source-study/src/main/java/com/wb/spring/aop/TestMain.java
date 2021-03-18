package com.wb.spring.aop;

import com.wb.spring.aop.config.AopConfig;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * @author wangbin33
 * @date Created in 18:48 2019/10/6
 */
public class TestMain {
	public static void main(String[] args) {
		ApplicationContext acx = new AnnotationConfigApplicationContext(AopConfig.class);

//		DataSourceSwitch dataSourceSwitch = acx.getBean(DataSourceSwitch.class);
//		dataSourceSwitch.doSwitch();

//		UserService bean = acx.getBean(UserService.class);
//		bean.say();

//		LogAspects bean = acx.getBean(LogAspects.class);
//		System.out.println(bean);

		MathCalculator mathCalculator = acx.getBean(MathCalculator.class);
		mathCalculator.div(2, 1);
//		mathCalculator.add(1,2);
		mathCalculator.sub();
//		TestController testController = acx.getBean(TestController.class);
//		testController.test("wangbing");
	}
}
