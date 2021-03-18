package com.wb.spring.proxy.staticproxy;

/**
 * Created by wangbin33 on 2020/2/8.
 */
public interface IOrderService {
	/**
	 * 创建订单
	 * @param order 订单类
	 * @return 是否创建成功
	 */
	int createOrder(Order order);
}
