package com.wb.spring.tx;

import com.wb.spring.tx.config.TxConfig;
import com.wb.spring.tx.domain.User;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.util.UUID;

/**
 * @author wangbin33
 * @date Created in 22:54 2019/10/6
 */
public class TestTxMain {
	public static void main(String[] args) {
		ApplicationContext acx = new AnnotationConfigApplicationContext(TxConfig.class);
		UserService userService = acx.getBean(UserService.class);

		String name = UUID.randomUUID().toString().replace("-", "");
		System.out.println("生成的name: " + name);

		User user = new User();
		user.setAge(22);
		user.setName(name);
		userService.insert(user);
	}
}
