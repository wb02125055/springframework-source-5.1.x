package com.wb.spring.componentregister.domain;

/**
 * Created by wangbin33 on 2020/1/10.
 */
public class Sheep {
	public Sheep() {
		System.out.println("Sheep Constructor is invoke...");
	}
	public void init() {
		System.out.println("init invoke...");
	}
	public void destroy() {
		System.out.println("destroy invoke...");
	}
}
