package com.wb.spring.tx;

import com.wb.spring.tx.domain.User;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;

/**
 * @author wangbin33
 * @date Created in 23:16 2019/10/6
 */
@Repository
public class UserDao {

	@Resource
	private JdbcTemplate jdbcTemplate;

	public void insert(User user) {
		String sql = "insert into t_user(age,name) values(?,?)";
		jdbcTemplate.update(sql, user.getAge(), user.getName());
	}
}
