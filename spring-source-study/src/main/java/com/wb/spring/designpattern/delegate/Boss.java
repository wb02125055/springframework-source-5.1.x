package com.wb.spring.designpattern.delegate;

/**
 * Created by wangbin33 on 2020/2/17.
 */
public class Boss {
	/**
	 * boss下达任务
	 * @param command 任务
	 * @param leader leader
	 */
	public void command(String command, Leader leader) {
		leader.doing(command);
	}
}
