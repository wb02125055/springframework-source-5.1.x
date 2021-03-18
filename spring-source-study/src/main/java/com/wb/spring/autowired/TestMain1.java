package com.wb.spring.autowired;

import com.wb.spring.autowired.beans.Monkey;
import com.wb.spring.autowired.beans.Pen;
import com.wb.spring.autowired.beans.User;
import com.wb.spring.autowired.config.AutowiredConfig;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * Created by wangbin33 on 2020/3/8.
 */
public class TestMain1 {
	public static void main(String[] args) {
		ApplicationContext acx = new AnnotationConfigApplicationContext(AutowiredConfig.class);

		System.out.println(acx);
		Pen pen = acx.getBean(Pen.class);
		System.out.println(pen);

		User user = acx.getBean(User.class);
		System.out.println(user);
//
//		Monkey monkey = acx.getBean(Monkey.class);
//		System.out.println(monkey);
	}
}
