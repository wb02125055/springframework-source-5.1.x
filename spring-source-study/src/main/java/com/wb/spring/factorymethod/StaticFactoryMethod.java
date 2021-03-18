package com.wb.spring.factorymethod;

/**
 * Description:
 *
 * @author wangbin33
 * @date 2020/11/20 22:17
 */
public class StaticFactoryMethod {
	public static OldDog createOldDog(String name) {
		return new OldDog(name);
	}
	public static OldDog createOldDog(Long id, String name) {
		return new OldDog(id, name);
	}
}
