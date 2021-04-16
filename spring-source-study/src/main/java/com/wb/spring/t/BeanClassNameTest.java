package com.wb.spring.t;

import org.springframework.web.servlet.handler.BeanNameUrlHandlerMapping;

/**
 * Description:
 *
 * @author wangbin33
 * @date 2021/4/4 15:39
 */
public class BeanClassNameTest {
	public static void main(String[] args) {
		String className = BeanNameUrlHandlerMapping.class.getName();
		String simpleName = BeanNameUrlHandlerMapping.class.getSimpleName();
		System.out.println("className: " + className);
		System.out.println("simpleName: " + simpleName);
	}
}
