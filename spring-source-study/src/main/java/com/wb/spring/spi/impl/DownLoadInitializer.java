package com.wb.spring.spi.impl;

import com.wb.spring.spi.ServiceInitializer;

/**
 * Description:
 *
 * @author wangbin33
 * @date 2020/12/2 12:09
 */
public class DownLoadInitializer implements ServiceInitializer {
	@Override
	public void init() {
		System.out.println("下载功能初始化！");
	}
}
