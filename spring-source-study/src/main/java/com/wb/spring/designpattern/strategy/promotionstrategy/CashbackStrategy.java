package com.wb.spring.designpattern.strategy.promotionstrategy;

/**
 * Created by wangbin33 on 2020/2/17.
 */
public class CashbackStrategy implements PromotionStrategy {
	@Override
	public void doPromotion() {
		System.out.println("返现促销.");
	}
}
