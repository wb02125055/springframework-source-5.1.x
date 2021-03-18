package com.wb.spring.designpattern.template.jdbc;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by wangbin33 on 2020/2/17.
 *
 * AbstractList，AbstractMap，AbstractSet类就使用的是模板设计模式.
 *
 *
 * 模板设计模式优点：
 * （1）将相同的业务处理逻辑放到父类中，代码复用
 * （2）将不同的行为放到不同的子类中，提高扩展性
 * （3）复合开闭原则
 *
 * 模板设计模式缺点：
 * （1）子类数目增加
 * （2）如果父类中增加了一个抽象方法，则会导致所有的子类中都需要增加该方法
 * y
 */
public abstract class JdbcTemplate {
	private DataSource dataSource;

	public JdbcTemplate(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	public List<?> executeQuery(String sql, RowMapper<?> rowMapper, Object[] values) {
		try {
			// 创建连接
			Connection conn = this.createConnection();

			// 创建语句集
			PreparedStatement pstmt = this.createPrepareStatement(conn, sql);

			// 创建结果集
			ResultSet res = this.executeQuery(pstmt, values);

			// 映射结果集
			List<?> result = this.parseResultSet(res, rowMapper);

			// 关闭结果集
			this.closeResultSet(res);

			// 关闭语句集
			this.closeStatement(pstmt);

			// 关闭连接
			this.closeConnection(conn);

			return result;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	protected void closeConnection(Connection conn) throws Exception {
		conn.close();
	}

	protected void closeStatement(PreparedStatement pstmt) throws Exception {
		pstmt.close();
	}

	protected void closeResultSet(ResultSet res) throws Exception {
		res.close();
	}

	protected List<?> parseResultSet(ResultSet res, RowMapper<?> rowMapper) throws Exception {
		List<Object> list = new ArrayList<>();
		int rowNum = 1;
		while (res.next()) {
			list.add(rowMapper.mapRow(res, rowNum++));
		}
		return list;
	}

	protected ResultSet executeQuery(PreparedStatement pstmt, Object[] values) throws Exception {
		if (null != values) {
			for (int i = 0 ; i < values.length; i++) {
				pstmt.setObject(i, values[i]);
			}
		}
		return pstmt.executeQuery();
	}

	protected PreparedStatement createPrepareStatement(Connection conn, String sql) throws Exception {
		return conn.prepareStatement(sql);
	}

	protected Connection createConnection() throws Exception {
		return this.dataSource.getConnection();
	}
}
