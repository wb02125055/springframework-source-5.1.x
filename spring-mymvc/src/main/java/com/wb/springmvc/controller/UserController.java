package com.wb.springmvc.controller;

import com.wb.springmvc.domain.User;
import org.springframework.lang.Nullable;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by wangbin33 on 2019/12/28.
 */
public class UserController extends AbstractController {
	@Nullable
	@Override
	protected ModelAndView handleRequestInternal(HttpServletRequest request, HttpServletResponse response) throws Exception {
		System.out.println("handleRequestInternal...");
		List<User> userList = new ArrayList<>();
		User user1 = new User("张三", 12);
		User user2 = new User("李四", 21);
		userList.add(user1);
		userList.add(user2);
		return new ModelAndView("userList", "users", userList);
	}
}
