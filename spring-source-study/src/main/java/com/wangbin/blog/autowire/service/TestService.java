package com.wangbin.blog.autowire.service;

import com.wangbin.blog.autowire.dao.TestDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Description:
 *
 * @author wangbin33
 * @date 2021/4/16 15:23
 */
@Service
public class TestService {
	@Autowired
	private TestDao testDao;
	public void printFlag() {
		testDao.printFlag();
	}
}
