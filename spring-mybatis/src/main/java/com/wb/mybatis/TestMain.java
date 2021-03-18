package com.wb.mybatis;

import com.wb.spring.domain.User;
import com.wb.spring.mapper.UserMapper;

/**
 * Description:
 *
 * @author wangbin33
 * @date 2020/5/10 23:15
 *
 * TODO 动态代理的底层源码实现？
 */
public class TestMain {
	public static void main(String[] args) {
		UserMapper userMapper = (UserMapper) MySqlSessionFactory.queryMapper(UserMapper.class);

		User user = userMapper.findUserById(1);

		System.out.println(user);
	}
}
