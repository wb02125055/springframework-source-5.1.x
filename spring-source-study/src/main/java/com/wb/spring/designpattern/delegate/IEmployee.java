package com.wb.spring.designpattern.delegate;

/**
 * Created by wangbin33 on 2020/2/17.
 */
public interface IEmployee {
	/**
	 * 执行具体的工作
	 * @param command 任务
	 */
	void doing(String command);
}
