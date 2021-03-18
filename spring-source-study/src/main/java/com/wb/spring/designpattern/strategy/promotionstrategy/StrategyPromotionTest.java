package com.wb.spring.designpattern.strategy.promotionstrategy;

/**
 * Created by wangbin33 on 2020/2/17.
 *
 * 策略测试类.
 */
public class StrategyPromotionTest {
	public static void main(String[] args) {
		PromotionActivity promotionActivity1 = new PromotionActivity(new CashbackStrategy());
		PromotionActivity promotionActivity2 = new PromotionActivity(new CouponStrategy());

		promotionActivity1.execute();
		promotionActivity2.execute();
	}
}
