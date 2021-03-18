package com.wb.spring.designpattern.strategy.promotionstrategy;

/**
 * Created by wangbin33 on 2020/2/17.
 *
 * 用来创建促销活动的类.
 */
public class PromotionActivity {
	/** 创建促销活动策略类 */
	private PromotionStrategy promotionStrategy;

	public PromotionActivity(PromotionStrategy promotionStrategy) {
		this.promotionStrategy = promotionStrategy;
	}

	public void execute() {
		promotionStrategy.doPromotion();
	}
}
