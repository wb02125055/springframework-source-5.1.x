package com.wb.spring.designpattern.strategy.paystrategy;

/**
 * Created by wangbin33 on 2020/2/17.
 */
public abstract class IPayment {
	public abstract String getName();

	public abstract double queryBalance(String uid);

	public PayState pay(String uid, double amount) {
		if (queryBalance(uid) < amount) {
			return new PayState(500, "支付失败", "余额不足");
		}
		return new PayState(200, "支付成功", "支付金额：" + amount);
	}

}
