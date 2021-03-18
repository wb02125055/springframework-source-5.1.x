package com.wb.spring.designpattern.delegate;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by wangbin33 on 2020/2/17.
 */
public class MyDispatcherServlet extends HttpServlet {
	@Override
	protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		doDispatch(req,resp);
	}

	/**
	 * 根据uri将请求分派给不同的方法处理
	 * @param request 请求
	 * @param response 响应
	 */
	private void doDispatch(HttpServletRequest request, HttpServletResponse response) {
		String uri = request.getRequestURI();
		if ("deleteUser".equals(uri)) {
			// deleteUser
		} else if ("saveUser".equals(uri)) {
			// saveUser
		} else if ("findUserById".equals(uri)) {
			// findUserById
		}
	}
}
