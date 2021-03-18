package com.wb.spring.autowired.service;

import com.wb.spring.autowired.dao.UserDao;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * Created by wangbin33 on 2020/3/3.
 */
@Service
public class UserService {

//	@Qualifier("userDao")
//	@Autowired
	@Resource(name = "userDao")
//	@Inject
	private UserDao userDao;
	public void printUserDao() {
		System.out.println(userDao.hashCode());
		System.out.println(userDao);
	}
}
