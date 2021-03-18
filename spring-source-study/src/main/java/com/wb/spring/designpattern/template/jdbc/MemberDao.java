package com.wb.spring.designpattern.template.jdbc;

import javax.sql.DataSource;
import java.util.List;

/**
 * Created by wangbin33 on 2020/2/17.
 */
public class MemberDao extends JdbcTemplate {
	public MemberDao(DataSource dataSource) {
		super(dataSource);
	}

	public List<?> selectAll() {
		String sql = "select * from t_member";
		return super.executeQuery(sql, (res, rowNum) -> {
			Member member = new Member();
			member.setUserName(res.getString("username"));
			member.setPassWord(res.getString("password"));
			member.setNickName(res.getString("nickname"));
			return member;
		}, null);
	}
}
