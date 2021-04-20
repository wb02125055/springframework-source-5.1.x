package com.wangbin.blog.bridge;

/**
 * Description: HpPrinter
 *
 * @author wangbin33
 * @date 2021/4/20 17:12
 */
public class HpPrinter implements Printer<String> {
	@Override
	public void print(String s) {
		System.out.println("print content: " + s);
	}
}
