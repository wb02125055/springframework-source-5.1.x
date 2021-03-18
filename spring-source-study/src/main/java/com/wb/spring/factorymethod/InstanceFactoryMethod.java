package com.wb.spring.factorymethod;

/**
 * Description:
 *
 * @author wangbin33
 * @date 2020/11/20 22:18
 */
public class InstanceFactoryMethod {
	public OldDog createOldDog(String name) {
		return new OldDog(name);
	}
	public OldDog createOldDog(Long id, String name) {
		return new OldDog(id, name);
	}
}
