package com.wb.springmvc.dao;

import org.springframework.stereotype.Repository;

/**
 * Created by wangbin33 on 2020/1/3.
 *
 * dao层
 */
@Repository
public class UserDao {
	public void findUser() {
		System.out.println("Find User from DB...");
	}
}
