package com.wb.spring.spi.impl;

/**
 * Description:
 *
 * @author wangbin33
 * @date 2020/12/2 12:15
 */
public class SelfInitializer extends DownLoadInitializer {
	@Override
	public void init() {
		System.out.println("SelfInitializer...");
	}
}
