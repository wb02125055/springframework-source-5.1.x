package com.wb.springmvc.vo;

/**
 * Created by wangbin33 on 2020/1/2.
 * 封装User信息
 */
public class UserVO {
	private Long id;
	private String name;
	public UserVO() {}
	public UserVO(Long id, String name) {
		this.id = id;
		this.name = name;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return "UserVO{" +
				"id=" + id +
				", name='" + name + '\'' +
				'}';
	}
}
