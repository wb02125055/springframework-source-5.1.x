package com.wb.spring.jdbc;

import com.wb.spring.jdbc.config.JdbcConfig;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;
import java.util.List;
import java.util.Map;

/**
 * @author wangbin33
 * @date Created in 15:47 2019/11/24
 */
public class TestMain {

	private static JdbcTemplate jdbcTemplate;

	public static void main(String[] args) {

		ApplicationContext acx = new AnnotationConfigApplicationContext(JdbcConfig.class);

		DataSource dataSource = (DataSource) acx.getBean("dataSource");

		jdbcTemplate = new JdbcTemplate(dataSource);
		// 默认值为true，一旦设置为false，出现SQL警告信息时就会被转化为SQLWarningException异常抛出.
		jdbcTemplate.setIgnoreWarnings(true);
		// 设置JdbcTemplate的最大处理行数，表示的是ResultSet结果集中最多可以包括的行数，
		// 如果数据库中总共有1000条数据，设置为500，就只能取出500行数据，防止结果集过大，导致服务器OOM，和limit类似
		// 默认值为-1，表示使用JDBC驱动Driver中的默认配置
		jdbcTemplate.setMaxRows(-1);
		// 设置Result结果集在构建时，每次从结果集中取出多少条，防止内存不足.
		jdbcTemplate.setFetchSize(-1);
		// 查询超时时间.
		jdbcTemplate.setQueryTimeout(3000);

		String sql = "select * from t_activity";

		List<Map<String, Object>> userList = jdbcTemplate.queryForList(sql);
		for (Map<String, Object> dataMap : userList) {
			System.out.println(dataMap);
		}
	}
}
