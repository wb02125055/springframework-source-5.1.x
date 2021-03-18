package com.wb.spring.share.class1.domain;

/**
 * Description:
 *
 * @author wangbin33
 * @date 2020/11/1 22:59
 */
public class HotDog {
	private Long id;
	private String name;

	public void createHotDog() {
		System.out.println("createHotDog......");
	}
	public void eatHotDog() {
		System.out.println("eatHotDog......");
	}

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


	@Override
	public String toString() {
		return "HotDog{" +
				"id=" + id +
				", name='" + name + '\'' +
				'}';
	}
}
