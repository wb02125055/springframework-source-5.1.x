package com.wb.spring.beancopytest;

import org.springframework.beans.BeanUtils;

import java.util.Arrays;

/**
 * Created by wangbin33 on 2020/3/9.
 */
public class Test {
	public static void main(String[] args) {
		InstA a = new InstA();
		a.setName("1111111111111111111111111111111111111222222222222222222222222");
		a.setTools(Arrays.asList("bbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbcccccccccc"));
		InstB b = new InstB();
		long start = System.currentTimeMillis();
		for (int i = 0 ; i < 10000000; i++) {
			BeanUtil.copy(a, b);
		}
		System.out.println("tooken: " + (System.currentTimeMillis() - start) + " ms.");
		InstB c = new InstB();
		long start1 = System.currentTimeMillis();
		for (int i = 0 ; i < 10000000; i++) {
			BeanUtils.copyProperties(a, c);
		}
		System.out.println("tooken1: " + (System.currentTimeMillis() - start1) + " ms.");
	}
}
