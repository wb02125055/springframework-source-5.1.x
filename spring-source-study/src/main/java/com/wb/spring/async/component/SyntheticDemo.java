package com.wb.spring.async.component;

/**
 * Description: synthetic的作用
 *
 * @author wangbin33
 * @date 2020/9/26 16:04
 */
public class SyntheticDemo {
	public static void main(String[] args) {
		SyntheticDemo.NestedClass n = new SyntheticDemo.NestedClass();
		System.out.println("result: " + n.highlyConfidential);
	}
	private static class NestedClass {
		private String highlyConfidential = "NestedClass...";
		private int highlyConfidentialInt = 42;
//		private Calendar highlyConfidentialCalendar = Calendar.getInstance();
		private boolean highlyConfidentialBoolean = true;
	}
}
