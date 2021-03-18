package com.wb.spring.proxy.staticproxy;

import com.wb.spring.proxy.common.DynamicDataSourceEntry;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by wangbin33 on 2020/2/8.
 */
public class OrderServiceStaticProxy implements IOrderService {

	private SimpleDateFormat yearFormat = new SimpleDateFormat("yyyy");

	private IOrderService orderService;

	public OrderServiceStaticProxy(IOrderService orderService) {
		this.orderService = orderService;
	}

	@Override
	public int createOrder(Order order) {
		before();
		Long time = order.getCreateTime();
		Integer dbRouter = Integer.valueOf(yearFormat.format(new Date(time)));
		System.out.println("静态代理类自动分配到【DB_" + dbRouter + "】数据源处理数据");
		DynamicDataSourceEntry.set(dbRouter);
		orderService.createOrder(order);
		after();
		return 0;
	}
	private void before() {
		System.out.println("Proxy before method.");
	}
	private void after() {
		System.out.println("Proxy after method.");
	}
}
