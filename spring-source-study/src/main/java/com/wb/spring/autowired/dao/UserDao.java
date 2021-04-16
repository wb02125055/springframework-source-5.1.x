package com.wb.spring.autowired.dao;

/**
 * Created by wangbin33 on 2020/3/3.
 */
//@Component("userDao")
public class UserDao {

	private String flag = "1";

	public String getFlag() {
		return flag;
	}
	public void setFlag(String flag) {
		this.flag = flag;
	}
	@Override
	public String toString() {
		return "UserDao{" +
				"flag='" + flag + '\'' +
				'}';
	}
}
