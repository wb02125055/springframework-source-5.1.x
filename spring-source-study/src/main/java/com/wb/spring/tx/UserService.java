package com.wb.spring.tx;

import com.wb.spring.tx.domain.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

/**
 * @author wangbin33
 * @date Created in 23:15 2019/10/6
 */
@Service
public class UserService {
	@Resource
	private UserDao userDao;

	@Transactional(propagation = Propagation.REQUIRED)
	public void m1() {
//		User user = new User();
//		user.setAge(12);
//		user.setId(90);
//		user.setName("m1");
//		userDao.insert(user);
		this.m2();
//		int i = 1 / 0;
	}
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public void m2() {
		User user = new User();
		user.setAge(13);
		user.setId(91);
		user.setName("m2");
		userDao.insert(user);
		int i = 1 / 0;
		System.out.println("m2 has been invoked.");
	}

//	@Transactional
	@Transactional(noRollbackFor = Exception.class)
	public void insert(User user) {
		userDao.insert(user);
//		int[] arr = new int[1];
//		arr[3] = 10;
		int a = 1 / 0;
		System.out.println("插入完成...");
	}
}
