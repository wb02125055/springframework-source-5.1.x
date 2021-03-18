package com.wb.spring.beandefinition;

/**
 * Created by wangbin33 on 2019/12/28.
 */
public class ParentPO {
	private String name;
	private Integer sex;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getSex() {
		return sex;
	}

	public void setSex(Integer sex) {
		this.sex = sex;
	}

	@Override
	public String toString() {
		return "ParentPO{" +
				"name='" + name + '\'' +
				", sex=" + sex +
				'}';
	}
}
