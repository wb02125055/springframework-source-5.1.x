package com.wb.spring.ext;

import java.util.HashSet;
import java.util.Set;

/**
 * @author wangbin33
 * @date Created in 14:25 2019/12/10
 */
public class SetSystemOut {
	public static void main(String[] args) {
		Set<String> set = new HashSet<>();
		set.add("wangbing");
		set.add("bingwang");

		System.out.println("set:" + set);
	}
}
