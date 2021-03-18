package com.wb.spring.postprocessor.domain;

import org.springframework.stereotype.Service;

/**
 * Created by wangbin33 on 2020/2/12.
 */
@Service
public class UserService {
	public UserService() {
		System.out.println("UserService init...");
	}
	public void save() {
		System.out.println("UserService save..");
	}
}
