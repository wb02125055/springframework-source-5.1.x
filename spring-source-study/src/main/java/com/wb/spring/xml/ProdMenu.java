package com.wb.spring.xml;

/**
 * Description:
 *
 * @author wangbin33
 * @date 2020/8/6 14:16
 */
public class ProdMenu {
	private String name;
	private Integer id;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	@Override
	public String toString() {
		return "ProdMenu{" +
				"name='" + name + '\'' +
				", id=" + id +
				'}';
	}
}
