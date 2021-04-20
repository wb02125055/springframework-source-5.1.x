package com.wangbin.blog.bridge;

import java.lang.reflect.Method;

/**
 * Description: 测试类
 *
 * @author wangbin33
 * @date 2021/4/20 17:36
 */
public class TestMain {
	public static void main(String[] args) throws Exception {
		Printer<String> printer = new HpPrinter();
		printer.print("Hello world");

		// 获取入参为Object类型的方法
		Method printMethod = HpPrinter.class.getMethod("print", Object.class);
		printMethod.invoke(printer, "a");
		System.out.println(printMethod.isBridge());

		// 获取入参为String类型的方法
		printMethod = HpPrinter.class.getMethod("print", String.class);
		printMethod.invoke(printer, "b");
		System.out.println(printMethod.isBridge());
	}
}
