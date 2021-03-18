package com.wb.springmvc.controller;

import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by wangbin33 on 2020/1/5.
 *
 * 该种方式可以直接通过父类调用子类重写的方法即可，但是一个类只能对应一个访问路径
 */
@Component("/student")
public class BeanNameController implements Controller {
	@Nullable
	@Override
	public ModelAndView handleRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
		return null;
	}
}
