package org.springframework.mock.sourcestudy;

/**
 * @author wangbin33
 * @date Created in 15:27 2019/10/2
 */
public class Person {
	public Person() {}
	public Person(String name) {
		this.name = name;
	}
	private String name;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return "Person{" +
				"name='" + name + '\'' +
				'}';
	}
}
