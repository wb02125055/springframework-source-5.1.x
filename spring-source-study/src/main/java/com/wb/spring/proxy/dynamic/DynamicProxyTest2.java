package com.wb.spring.proxy.dynamic;

import com.wb.spring.proxy.staticproxy.IOrderService;
import com.wb.spring.proxy.staticproxy.Order;
import com.wb.spring.proxy.staticproxy.OrderService;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by wangbin33 on 2020/2/8.
 */
public class DynamicProxyTest2 {
	public static void main(String[] args) {
		try {
			Order order = new Order();

			SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
			Date date = sdf.parse("2018/02/01");
			order.setCreateTime(date.getTime());

			IOrderService orderService = (IOrderService) new OrderServiceDynamicProxy()
					.getInstance(new OrderService());
			orderService.createOrder(order);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
