package com.wb.spring.designpattern.adapter.dc220demo;

/**
 * Created by wangbin33 on 2020/2/17.
 *
 * 适配器设计模式.
 */
public class ObjectAdapterTest {
	public static void main(String[] args) {
		DC5 dc5 = new PowerAdapter(new AC220());
		dc5.outputDC5V();
	}
}
