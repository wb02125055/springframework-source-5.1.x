package com.wb.spring.designpattern.strategy.paystrategy;

/**
 * Created by wangbin33 on 2020/2/17.
 */
public class PayState {
	private int code;
	private Object data;
	private String msg;

	public PayState(int code, Object data, String msg) {
		this.code = code;
		this.data = data;
		this.msg = msg;
	}

	@Override
	public String toString() {
		return "PayState{" +
				"code=" + code +
				", data=" + data +
				", msg='" + msg + '\'' +
				'}';
	}
}
