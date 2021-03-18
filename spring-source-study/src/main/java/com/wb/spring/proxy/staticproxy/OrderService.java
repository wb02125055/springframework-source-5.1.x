package com.wb.spring.proxy.staticproxy;

/**
 * Created by wangbin33 on 2020/2/8.
 */
public class OrderService implements IOrderService {

	private OrderDao orderDao;

	public OrderService() {
		orderDao = new OrderDao();
	}

	@Override
	public int createOrder(Order order) {
		System.out.println("OrderService 调用 orderDao 创建订单！");
		return orderDao.insert(order);
	}
}
