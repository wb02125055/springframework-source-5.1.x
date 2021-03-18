package com.wb.spring.beandefinitionparser.domain;

/**
 * Created by wangbin33 on 2020/1/9.
 */
public class User {
	private String id;
	private String name;
	private String sex;
	private String email;
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getSex() {
		return sex;
	}
	public void setSex(String sex) {
		this.sex = sex;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	@Override
	public String toString() {
		return "User{" +
				"id='" + id + '\'' +
				", name='" + name + '\'' +
				", sex='" + sex + '\'' +
				", email='" + email + '\'' +
				'}';
	}
}
