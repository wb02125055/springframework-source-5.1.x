package com.wb.spring.designpattern.strategy.paystrategy;

/**
 * Created by wangbin33 on 2020/2/17.
 */
public class UnionPay extends IPayment {
	@Override
	public String getName() {
		return "银联支付";
	}

	@Override
	public double queryBalance(String uid) {
		return 120;
	}
}
