package com.wb.springmvc.controller;

import org.springframework.stereotype.Component;
import org.springframework.web.HttpRequestHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by wangbin33 on 2020/1/5.
 *
 * 第二种声明Controller的方式。通过实现HttpRequestHandler接口的形式.
 * 一个类只能对应一个访问路径，但是可以直接通过父类HttpRequestHandler去调用子类实现的handleRequest方法，不需要额外通过访问路径去查找对应的处理器.
 */
@Component("/test")
public class TestController implements HttpRequestHandler {
	@Override
	public void handleRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.getWriter().write("testController...");
	}
}
