package com.wb.spring.proxy.staticproxy;

/**
 * Created by wangbin33 on 2020/2/8.
 */
public class Order {
	private Object orderInfo;
	private Long createTime;
	private String id;

	public Object getOrderInfo() {
		return orderInfo;
	}

	public void setOrderInfo(Object orderInfo) {
		this.orderInfo = orderInfo;
	}

	public Long getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Long createTime) {
		this.createTime = createTime;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	@Override
	public String toString() {
		return "Order{" +
				"orderInfo=" + orderInfo +
				", createTime=" + createTime +
				", id='" + id + '\'' +
				'}';
	}
}
