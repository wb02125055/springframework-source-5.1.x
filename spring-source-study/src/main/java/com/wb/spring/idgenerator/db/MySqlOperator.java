package com.wb.spring.idgenerator.db;

import org.springframework.jdbc.core.JdbcTemplate;

/**
 * Description:
 *
 * @author wangbin33
 * @date 2020/8/8 9:47
 */
public class MySqlOperator extends AbstractDbOperator {
	private JdbcTemplate jdbcTemplate;
	/** 表名称 */
	private String tableName;
	/** 序列块大小 */
	private int blockSize;

	/** 默认的序列生成器所在表名称 */
	private static final String DEFAULT_TABLE_NAME = "t_sequence";

	/** 默认块大小 */
	private static final int DEFAULT_BLOCK_SIZE = 5;

	public MySqlOperator() {}

	public MySqlOperator(JdbcTemplate jdbcTemplate, String tableName, int blockSize) {
		this.jdbcTemplate = jdbcTemplate;
		this.tableName = tableName;
		this.blockSize = blockSize;
	}

	protected JdbcTemplate getJdbcTemplate() {
		return this.jdbcTemplate;
	}

	@Override
	protected long getBlockSize() {
		return this.blockSize > 0 ? this.blockSize : DEFAULT_BLOCK_SIZE;
	}

	@Override
	protected String getTableName() {
		return (null == this.tableName || "".equals(this.tableName.trim())) ?
				DEFAULT_TABLE_NAME : this.tableName;
	}
}
