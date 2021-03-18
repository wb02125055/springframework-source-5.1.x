package com.wb.springmvc.controller;

import com.wb.springmvc.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.Resource;

/**
 * Created by wangbin33 on 2020/1/2.
 *
 * 测试controller
 */
@Controller
@RequestMapping("/user")
public class UserController {

	@Resource
	private UserService userService;

	@RequestMapping("/list")
	public String list() {
		userService.listUsers();
		return "listUser";
	}
}
