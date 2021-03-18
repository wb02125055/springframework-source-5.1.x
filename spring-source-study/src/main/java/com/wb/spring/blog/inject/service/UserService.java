package com.wb.spring.blog.inject.service;

import com.wb.spring.blog.inject.dao.UserDao;

/**
 * @author wangbin33
 */
public class UserService {
	private UserDao userDao;
	public void setUserDao(UserDao userDao) {
		this.userDao = userDao;
	}
	public void save() {
		userDao.save();
	}
}
