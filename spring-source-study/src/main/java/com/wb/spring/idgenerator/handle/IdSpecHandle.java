package com.wb.spring.idgenerator.handle;

import com.google.common.base.Preconditions;
import com.wb.spring.idgenerator.db.DbOperator;
import com.wb.spring.idgenerator.db.MySqlOperator;
import com.wb.spring.idgenerator.log.IdGeneratorLogger;
import com.wb.spring.idgenerator.common.SpecGenerator;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

/**
 * Description:
 *
 * @author wangbin33
 * @date 2020/8/8 9:57
 */
public class IdSpecHandle implements SpecGenerator, InitializingBean {

	/** 数据源 */
	private DataSource dataSource;
	/** 每次块的大小 */
	private int blockSize;
	/** 序列生成器所在的表 */
	private String tableName;

	private long startValue = 0;

	private Map<String ,Step> stepMap = new HashMap<>();

	public DbOperator getDbOperator() {
		Preconditions.checkNotNull(dataSource, "dataSource is null.");
		return new MySqlOperator(new JdbcTemplate(dataSource), tableName, blockSize);
	}

	@Override
	public synchronized long get(String name) {
		Step step = stepMap.get(name);
		if (null == step) {
			IdGeneratorLogger.log.info("step is null. name: {}", name);
			step = new Step(startValue, startValue + blockSize);
			stepMap.put(name, step);
		} else {
			if (step.currentValue < step.endValue) {
				return step.incrementAndGet();
			}
		}
		for (int i = 0 ; i < blockSize; i++) {
			IdGeneratorLogger.log.info("getNextBlock, i: {}, name: {}", i, name);
			if (getNextBlock(name, step)) {
				return step.incrementAndGet();
			}
		}
		IdGeneratorLogger.log.error("No more value where get id, name: {}", name);
		throw new RuntimeException("No more value.");
	}

	private boolean getNextBlock(String name, Step step) {
		DbOperator dbOperator = getDbOperator();
		long currentValue = dbOperator.getPersistenceValue(name, false);
		long value = currentValue;
		if (value <= 0) {
			IdGeneratorLogger.log.info("get id by no backup table return empty result.now to get id from backup table.");
			value = dbOperator.getPersistenceValue(name, true);
		}
		if (value <= 0) {
			IdGeneratorLogger.log.info("getNextBlock <= 0,name: {}, value: {}", name ,value);
			try {
				value = dbOperator.newPersistenceValue(name, startValue);
			} catch (Exception e) {
				IdGeneratorLogger.log.error("getNextBlock error! name: {}, startValue: {}", name, startValue);
				value = getPersistenceValueWithBackup(name, dbOperator);
			}
		} else if (currentValue <= 0) {
			boolean insertSuccess = false;
			try {
				insertSuccess = dbOperator.newPersistenceValue(name, value + blockSize) > 0;
			} catch (Exception e) {
				IdGeneratorLogger.log.error("getNextBlock newPersistenceValue error. name: {}, value: {}",
						name, value + blockSize, e);
			}
			IdGeneratorLogger.log.info("getNextBlock newPersistenceValue, name: {}, value: {}", name, value + blockSize);
			if (insertSuccess) {
				step.setCurrentValue(value);
				step.setEndValue(value + blockSize);
			}
			return insertSuccess;
		}
		boolean updateRes = dbOperator.updatePersistenceValue(name, value) == 1;
		IdGeneratorLogger.log.info("getNextBlock after updatePersistenceValue, name: {}, value: {}, updateValue: {}, updateRes: {}",
				name, value, value + blockSize, updateRes);
		if (updateRes) {
			step.setCurrentValue(value);
			step.setEndValue(value + blockSize);
		}
		return updateRes;
	}

	private long getPersistenceValueWithBackup(String name, DbOperator dbOperator) {
		long value = dbOperator.getPersistenceValue(name, false);
		if (value <= 0) {
			value = dbOperator.getPersistenceValue(name, true);
		}
		return value;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		try {
			getDbOperator().snifferDb();
		} catch (Exception e) {
			IdGeneratorLogger.log.error("sniffer db error. check the configuration first.");
			throw new BeanCreationException(e.getMessage());
		}
	}

	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	public void setBlockSize(int blockSize) {
		this.blockSize = blockSize;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	static class Step {
		private long currentValue;
		private long endValue;
		Step(long currentValue, long endValue) {
			this.currentValue = currentValue;
			this.endValue = endValue;
		}
		public void setCurrentValue(long currentValue) {
			this.currentValue = currentValue;
		}
		public void setEndValue(long endValue) {
			this.endValue = endValue;
		}
		public long incrementAndGet() {
			return ++currentValue;
		}
	}
}
