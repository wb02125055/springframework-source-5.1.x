package com.wb.spring.propertyeditor.domain;

/**
 * Description:
 *
 * @author wangbin33
 * @date 2020/10/20 16:52
 */
public class Student {
	private String stuName;
	private Teacher teacher;

	public String getStuName() {
		return stuName;
	}

	public void setStuName(String stuName) {
		this.stuName = stuName;
	}

	public Teacher getTeacher() {
		return teacher;
	}

	public void setTeacher(Teacher teacher) {
		this.teacher = teacher;
	}

	@Override
	public String toString() {
		return "Student{" +
				"stuName='" + stuName + '\'' +
				", teacher=" + teacher +
				'}';
	}
}
