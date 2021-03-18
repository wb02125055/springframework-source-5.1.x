package com.wb.spring.designpattern.strategy.paystrategy;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by wangbin33 on 2020/2/17.
 */
public class PayStrategy {
	public static final String ALI_PAY = "AliPay";
	public static final String JD_PAY = "JDPay";
	public static final String UNION_PAY = "UnionPay";
	public static final String WECHAT_PAY = "WechatPay";
	public static final String DEFAULT_PAY = "AliPay";

	private static final Map<String, IPayment> PAY_STRATEGY_MAP = new HashMap<>();

	static {
		PAY_STRATEGY_MAP.put(ALI_PAY, new AliPay());
		PAY_STRATEGY_MAP.put(JD_PAY, new JDPay());
		PAY_STRATEGY_MAP.put(UNION_PAY, new UnionPay());
		PAY_STRATEGY_MAP.put(WECHAT_PAY, new WechatPay());
		PAY_STRATEGY_MAP.put(DEFAULT_PAY, new AliPay());
	}

	public static IPayment get(String payKey) {
		if (!PAY_STRATEGY_MAP.containsKey(payKey)) {
			return PAY_STRATEGY_MAP.get(DEFAULT_PAY);
		}
		return PAY_STRATEGY_MAP.get(payKey);
	}
}
