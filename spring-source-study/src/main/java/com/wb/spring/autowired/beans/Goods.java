package com.wb.spring.autowired.beans;

/**
 * Description:
 *
 * @author wangbin33
 * @date 2020/7/3 23:58
 */
public class Goods {
	private String name;
	private Double price;

	public Goods() {
		System.out.println("init -----");
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Double getPrice() {
		return price;
	}

	public void setPrice(Double price) {
		this.price = price;
	}

//	@Override
//	public String toString() {
//		return "Goods{" +
//				"name='" + name + '\'' +
//				", price=" + price +
//				'}';
//	}
}
