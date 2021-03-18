package com.wb.spring.blog.inject.factory;

import com.wb.spring.blog.inject.dao.UserDao;

/**
 * @author wangbin33
 */
public class UserInstanceFactory {
	public UserDao getUserDao() {
		return new UserDao();
	}
}
