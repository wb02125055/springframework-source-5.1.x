package com.wb.spring.designpattern.template.jdbc;

import com.mchange.v2.c3p0.ComboPooledDataSource;

import java.util.List;

/**
 * Created by wangbin33 on 2020/2/17.
 */
public class MemberDaoTest {
	public static void main(String[] args) throws Exception {
		ComboPooledDataSource dataSource = new ComboPooledDataSource();
		dataSource.setDriverClass("com.mysql.jdbc.Driver");
		dataSource.setUser("root");
		dataSource.setPassword("root");
		dataSource.setJdbcUrl("jdbc:mysql://127.0.0.1:3306/test?useSSL=false");
		MemberDao memberDao = new MemberDao(dataSource);
		List<Member> list = (List<Member>) memberDao.selectAll();
		for (Member member : list) {
			System.out.println(member);
		}
	}
}
