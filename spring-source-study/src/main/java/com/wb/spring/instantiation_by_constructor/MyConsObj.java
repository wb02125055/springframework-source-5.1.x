package com.wb.spring.instantiation_by_constructor;

/**
 * Description:
 *
 * @author wangbin33
 * @date 2020/11/26 11:35
 */
public class MyConsObj {
	private Long id;
	private String name;

	public MyConsObj() {
	}

	public MyConsObj(Long id) {
		this.id = id;
	}

	public MyConsObj(Long id, String name) {
		this.id = id;
		this.name = name;
	}

	public MyConsObj(String name, Long id) {
		this.name = name;
		this.id = id;
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
		return "MyConsObj{" +
				"id=" + id +
				", name='" + name + '\'' +
				'}';
	}
}
