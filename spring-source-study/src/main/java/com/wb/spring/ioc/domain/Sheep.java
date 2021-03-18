package com.wb.spring.ioc.domain;

/**
 * @author wangbin33
 * @date Created in 15:59 2019/11/24
 */
public class Sheep {
	private Long id;
	private String name;
	private Color color;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Color getColor() {
		return color;
	}

	public void setColor(Color color) {
		this.color = color;
	}

	@Override
	public String toString() {
		return "Sheep{" +
				"id=" + id +
				", name='" + name + '\'' +
				", color=" + color +
				'}';
	}
}
