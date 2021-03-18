package com.wb.spring.proxy.staticproxy;

/**
 * Created by wangbin33 on 2020/2/8.
 */
public class OrderDao {
	public int insert(Order order) {
		System.out.println("OrderDao创建Order成功！订单信息：" + order);
		return 1;
	}
}
