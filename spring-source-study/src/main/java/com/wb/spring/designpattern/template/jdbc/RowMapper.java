package com.wb.spring.designpattern.template.jdbc;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by wangbin33 on 2020/2/17.
 */
public interface RowMapper<T> {
	/**
	 * 对结果做映射
	 * @param res 结果集
	 * @param rowNum 行编号
	 * @return 结果集
	 */
	T mapRow(ResultSet res, int rowNum) throws SQLException;
}
