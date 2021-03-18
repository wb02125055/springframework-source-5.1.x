package com.wb.spring.designpattern.strategy.paystrategy;

/**
 * Created by wangbin33 on 2020/2/17.
 */
public class AliPay extends IPayment {
	@Override
	public String getName() {
		return "支付宝";
	}

	@Override
	public double queryBalance(String uid) {
		return 900;
	}
}
