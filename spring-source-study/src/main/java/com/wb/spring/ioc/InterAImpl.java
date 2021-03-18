package com.wb.spring.ioc;

/**
 * @author wangbin33
 * @date Created in 22:39 2019/11/24
 */
public class InterAImpl implements InterA {
	@Override
	public String say(String msg) {
		return name + msg;
	}
}
