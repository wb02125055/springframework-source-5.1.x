package com.wb.spring.designpattern.strategy.promotionstrategy;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by wangbin33 on 2020/2/17.
 *
 * 策略工厂.
 */
public class PromotionStrategyFactory {

	private static final Map<String, PromotionStrategy> PROMOTION_STRATEGY_MAP = new HashMap<>();

	private static final PromotionStrategy NON_PROMOTION = new EmptyStrategy();

	static {
		PROMOTION_STRATEGY_MAP.put(PromotionKey.COUPON, new CouponStrategy());
		PROMOTION_STRATEGY_MAP.put(PromotionKey.CASHBACK, new CashbackStrategy());
		PROMOTION_STRATEGY_MAP.put(PromotionKey.GROUPBUY, new GroupbuyStrategy());
	}

	private interface PromotionKey {
		String COUPON = "COUPON";
		String CASHBACK = "CASHBACK";
		String GROUPBUY = "GROUPBUY";
	}

	public static final PromotionStrategy getPromotionStrategy(String promotionKey) {
		PromotionStrategy promotionStrategy = PROMOTION_STRATEGY_MAP.get(promotionKey);
		return null == promotionStrategy ? NON_PROMOTION : promotionStrategy;
	}
}
