package com.wangbin.blog.autowire.dao.impl;

import com.wangbin.blog.autowire.dao.TestDao;
import org.springframework.stereotype.Repository;

/**
 * Description:
 *
 * @author wangbin33
 * @date 2021/4/16 15:22
 */
@Repository("testDao")
public class Test2DaoImpl implements TestDao {
	public Test2DaoImpl() {
		System.out.println("***********");
	}
	@Override
	public void printFlag() {
		System.out.println("2");
	}
}
