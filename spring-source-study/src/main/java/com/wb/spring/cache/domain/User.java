package com.wb.spring.cache.domain;

/**
 * Description:
 *
 * @author wangbin33
 * @date 2021/2/9 9:27
 */
public class User {
	private String userName;
	private String userId;

	public User() {}

	public User(String userName, String userId) {
		this.userName = userName;
		this.userId = userId;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	@Override
	public String toString() {
		return "User{" +
				"userName='" + userName + '\'' +
				", userId='" + userId + '\'' +
				'}';
	}
}
