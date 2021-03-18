package com.wb.spring.idgenerator.db;

import com.wb.spring.idgenerator.log.IdGeneratorLogger;
import javafx.collections.ArrayChangeListener;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Description:
 *
 * @author wangbin33
 * @date 2020/8/8 9:23
 */
public abstract class AbstractDbOperator implements DbOperator {

	private String getSql() {
		return "select id from " + getTableName() + " where name = ?";
	}

	private String newSql() {
		return "insert into " + getTableName() + " (id,name) values(?,?)";
	}

	private String updateSql() {
		return "update " + getTableName() + " set id = ? where name = ? and id = ?";
	}

	private String getBackupSql() {
		return "select id from " + (getTableName() + "_backup") + " where name = ?";
	}

	private String getSnifferSql() {
		return "select count(1) from " + getTableName() + "_backup";
	}

	@Override
	public int updatePersistenceValue(String name, long value) {
		long updateValue = value + getBlockSize();
		try {
			IdGeneratorLogger.log.info("updatePersistenceValue name: {}, value: {}, updateValue: {}", name, value, updateValue);
			return getJdbcTemplate().update(updateSql(), updateValue, name, value);
		} catch (Exception e) {
			throw new RuntimeException("updatePersistenceValue error. name: "
					+ name + ", value:" + name + ", updateValue:" + updateValue);
		}
	}
	@Override
	public long getPersistenceValue(String name, boolean backUpFlag) {
		try {
			if (backUpFlag) {
				Long r = getJdbcTemplate().queryForObject(getBackupSql(), new Object[] {name}, Long.class);
				return null == r ? 0 : r;
			}
			Long r = getJdbcTemplate().queryForObject(getSql(), new Object[] {name}, Long.class);
			return null == r  ? 0 : r;
		} catch (Exception e) {
			IdGeneratorLogger.log.error("getPersistenceValue error. name: {}, backUpFlag: {}", name, backUpFlag, e);
			System.err.println(e.getMessage());
			throw new RuntimeException("getPersistenceValue error! name: " + name + ", backUpFlag: " + backUpFlag);
		}
	}
	@Override
	public long newPersistenceValue(String name, long persistenceValue) {
		try {
			getJdbcTemplate().update(newSql(), persistenceValue, name);
			return persistenceValue;
		} catch (Exception e) {
			throw new RuntimeException("newPersistenceValue error! name: " + name + ", persistenceValue: " + persistenceValue);
		}
	}
	@Override
	public void snifferDb() {
		getJdbcTemplate().query(getSnifferSql(), (rs, rowNum) -> rs.getInt(1));
	}

	protected abstract JdbcTemplate getJdbcTemplate();

	protected abstract long getBlockSize();

	protected abstract String getTableName();
}
