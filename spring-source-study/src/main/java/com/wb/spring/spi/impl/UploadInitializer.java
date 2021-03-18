package com.wb.spring.spi.impl;

import com.wb.spring.spi.ServiceInitializer;

/**
 * Description:
 *
 * @author wangbin33
 * @date 2020/12/2 12:09
 */
public class UploadInitializer implements ServiceInitializer {
	@Override
	public void init() {
		System.out.println("上传功能初始化！");
	}
}
