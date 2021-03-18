package com.wb.spring.autowired.beans;

/**
 * Description:
 *
 * @author wangbin33
 * @date 2020/7/3 23:58
 */
public class ShopCar {
	private String name;
	private Goods goods;

	public Goods getGoods() {
		return goods;
	}

	public void setGoods(Goods goods) {
		this.goods = goods;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return "ShopCar{" +
				"name='" + name + '\'' +
				", goods=" + goods +
				'}';
	}
}
