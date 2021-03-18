package org.springframework.mock.sourcestudy;

/**
 * @author wangbin33
 * @date Created in 14:44 2019/10/2
 */
public class Color {
	private String name;
	private Integer age;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getAge() {
		return age;
	}

	public void setAge(Integer age) {
		this.age = age;
	}

	@Override
	public String toString() {
		return "Color{" +
				"name='" + name + '\'' +
				", age=" + age +
				'}';
	}
}
