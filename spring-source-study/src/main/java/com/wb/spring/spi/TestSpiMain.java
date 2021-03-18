package com.wb.spring.spi;

import java.util.ServiceLoader;

/**
 * Description: 测试SPI接口
 *
 * @author wangbin33
 * @date 2020/12/2 12:10
 */
public class TestSpiMain {
	public static void main(String[] args) {
		ServiceLoader<ServiceInitializer> initializers = ServiceLoader.load(ServiceInitializer.class);
		for (ServiceInitializer initializer : initializers) {
			initializer.init();
		}
	}
}
