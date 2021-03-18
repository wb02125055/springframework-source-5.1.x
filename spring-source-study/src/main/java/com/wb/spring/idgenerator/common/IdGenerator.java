package com.wb.spring.idgenerator.common;

/**
 * Description:
 *
 * @author wangbin33
 * @date 2020/8/8 9:19
 */
public interface IdGenerator {
	/**
	 * 根据名称生成主键ID
	 * @param name 名称
	 */
	long get(String name);
}
