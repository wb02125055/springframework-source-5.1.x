package com.wangbin.blog.autowire.dao.impl;

import com.wangbin.blog.autowire.dao.TestDao;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;

/**
 * Description:
 *
 * @author wangbin33
 * @date 2021/4/16 15:20
 */
//@Repository
//@Primary
public class Test1DaoImpl implements TestDao {
	@Override
	public void printFlag() {
		System.out.println("1");
	}
}
