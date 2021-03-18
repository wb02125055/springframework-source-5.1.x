package com.wb.spring.designpattern.template.jdbc;

/**
 * Created by wangbin33 on 2020/2/17.
 */
public class Member {
	private String userName;
	private String passWord;
	private String nickName;

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getPassWord() {
		return passWord;
	}

	public void setPassWord(String passWord) {
		this.passWord = passWord;
	}

	public String getNickName() {
		return nickName;
	}

	public void setNickName(String nickName) {
		this.nickName = nickName;
	}

	@Override
	public String toString() {
		return "Member{" +
				"userName='" + userName + '\'' +
				", passWord='" + passWord + '\'' +
				", nickName='" + nickName + '\'' +
				'}';
	}
}
