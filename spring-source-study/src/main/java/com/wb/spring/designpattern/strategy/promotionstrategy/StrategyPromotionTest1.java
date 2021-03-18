package com.wb.spring.designpattern.strategy.promotionstrategy;

/**
 * Created by wangbin33 on 2020/2/17.
 *
 * 测试使用策略工厂创建促销活动
 */
public class StrategyPromotionTest1 {
	public static void main(String[] args) {
		String promotionKey = "COUPON";
		// 获取策略实例
		PromotionStrategy promotionStrategy = PromotionStrategyFactory.getPromotionStrategy(promotionKey);
		// 执行活动创建
		promotionStrategy.doPromotion();
	}
}
