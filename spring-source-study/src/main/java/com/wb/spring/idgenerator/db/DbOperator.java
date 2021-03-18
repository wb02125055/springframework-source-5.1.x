package com.wb.spring.idgenerator.db;

/**
 * Description:
 *
 * @author wangbin33
 * @date 2020/8/8 9:41
 */
public interface DbOperator {

	/**
	 * 获取指定序列生成器上一次生成的序列ID
	 * @param name 序列生成器的名称
	 * @param backUpFlag 是否查询备份表
	 */
	long getPersistenceValue(String name, boolean backUpFlag);

	/**
	 * 更新序列生成器
	 * @param name 序列生成器的名称
	 * @param persistenceValue 序列生成器需要更新的值
	 */
	int updatePersistenceValue(String name, long persistenceValue);

	/**
	 * 创建序列生成器
	 * @param name 序列生成器的名称
	 * @param persistenceValue 序列生成器的值
	 */
	long newPersistenceValue(String name, long persistenceValue);

	/**
	 * 嗅探DB是否存活
	 */
	void snifferDb();
}
