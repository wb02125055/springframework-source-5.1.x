package com.wb.spring.autowired.beans;

import com.wb.spring.autowired.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Description:
 *
 * @author wangbin33
 * @date 2020/6/25 10:45
 */
public class InstWorkBean extends AbstractBean {

	@Autowired
	private UserService userService;

	@Override
	public void run() {
		userService.printUserDao();
	}
}
