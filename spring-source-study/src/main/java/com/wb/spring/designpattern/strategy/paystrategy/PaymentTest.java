package com.wb.spring.designpattern.strategy.paystrategy;

/**
 * Created by wangbin33 on 2020/2/17.
 */
public class PaymentTest {
	public static void main(String[] args) {
		// 创建订单
		Order order = new Order("1", "2020021719020000", 324.55);

		// 支付
		System.out.println(order.pay(PayStrategy.ALI_PAY));
	}
}
