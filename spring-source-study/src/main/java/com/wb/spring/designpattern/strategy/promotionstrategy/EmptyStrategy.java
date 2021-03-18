package com.wb.spring.designpattern.strategy.promotionstrategy;

/**
 * Created by wangbin33 on 2020/2/17.
 */
public class EmptyStrategy implements PromotionStrategy {
	@Override
	public void doPromotion() {
		System.out.println("无促销活动.");
	}
}
