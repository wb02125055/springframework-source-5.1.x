package com.wb.spring.cache;

import com.wb.spring.cache.config.CacheConfig;
import com.wb.spring.cache.domain.User;
import com.wb.spring.cache.service.UserService;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.util.UUID;

/**
 * Description: 测试缓存
 *
 * @author wangbin33
 * @date 2021/2/9 9:10
 */
public class TestMain {
	public static void main(String[] args) {
		ApplicationContext acx = new AnnotationConfigApplicationContext(CacheConfig.class);
		UserService userService = acx.getBean(UserService.class);

		User user = userService.getUserByName("wangbin33");
		System.out.println(user);
		user = userService.getUserByName("wangbin33");
		System.out.println(user);
		user = userService.getUserByName("wangbin33");
		System.out.println(user);
		user = userService.getUserByName("wangbin33");
		System.out.println(user);
		String userId = UUID.randomUUID().toString();
		System.out.println("重新生成userId并更新，userId: " + userId);
		user.setUserId(userId);
		userService.updateUser(user);
		user = userService.getUserByName("wangbin33");
		System.out.println(user);
		user = userService.getUserByName("wangbin33");
		System.out.println(user);
	}
}
