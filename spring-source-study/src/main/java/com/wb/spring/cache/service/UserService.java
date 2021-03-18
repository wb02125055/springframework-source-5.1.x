package com.wb.spring.cache.service;

import com.wb.spring.cache.domain.User;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * Description:
 *
 * @author wangbin33
 * @date 2021/2/9 9:27
 */
@Service
public class UserService {

	@Cacheable(value = "userCache", key = "#userName")
	public User getUserByName(String userName) {
		System.out.println("数据库查询..." + userName);
		return getFromDB(userName);
	}

	@CacheEvict(value = "userCache", key = "#user.userName")
	public void updateUser(User user) {
		System.out.println("数据库更新了..." + user);
		updateDB(user);
	}

	private void updateDB(User user) {
		System.out.println("更新数据..." + user);
	}

	private User getFromDB(String userName) {
		try {
			TimeUnit.SECONDS.sleep(1);
		} catch (InterruptedException e) {
			System.err.println("查询出异常了！" + e.getMessage());
		}
		return new User(userName, UUID.randomUUID().toString());
	}
}
