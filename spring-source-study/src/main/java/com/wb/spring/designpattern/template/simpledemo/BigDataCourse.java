package com.wb.spring.designpattern.template.simpledemo;

/**
 * Created by wangbin33 on 2020/2/17.
 */
public class BigDataCourse extends NetworkCourse {

	private boolean needHomeworkFlag = false;

	public BigDataCourse(boolean needHomeworkFlag) {
		this.needHomeworkFlag = needHomeworkFlag;
	}

	@Override
	void checkHomeWork() {
		System.out.println("检查大数据的课后作业");
	}

	@Override
	protected boolean needHomework() {
		return this.needHomeworkFlag;
	}
}
