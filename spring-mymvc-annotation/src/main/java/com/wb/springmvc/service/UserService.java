package com.wb.springmvc.service;

import com.wb.springmvc.dao.UserDao;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * Created by wangbin33 on 2020/1/2.
 *
 * user service
 */
@Service
public class UserService {

	@Resource
	private UserDao userDao;

	public void listUsers() {
		System.out.println("UserService.listUsers...");
		userDao.findUser();
	}
}
