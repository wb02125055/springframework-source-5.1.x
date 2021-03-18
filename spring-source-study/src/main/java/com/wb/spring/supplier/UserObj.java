package com.wb.spring.supplier;

/**
 * Description:
 *
 * @author wangbin33
 * @date 2020/11/20 8:45
 */
public class UserObj {
	private String name;
	public UserObj() {}

	public UserObj(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return "UserObj{" +
				"name='" + name + '\'' +
				'}';
	}
}
