package com.wb.spring.designpattern.strategy.promotionstrategy;

/**
 * Created by wangbin33 on 2020/2/17.
 */
public class CouponStrategy implements PromotionStrategy {
	@Override
	public void doPromotion() {
		System.out.println("领取优惠券.");
	}
}
