package com.wb.spring.designpattern.template.simpledemo;

/**
 * Created by wangbin33 on 2020/2/17.
 */
public abstract class NetworkCourse {
	/**
	 * 创建课程.
	 */
	protected final void createCourse() {
		this.postPreResource();

		this.createPPT();

		this.liveVideo();

		this.postNote();

		this.postSource();

		if (needHomework()) {
			checkHomeWork();
		}
	}

	abstract void checkHomeWork();

	protected boolean needHomework() {
		return false;
	}

	final void postSource() {
		System.out.println("提交源代码");
	}
	final void postNote() {
		System.out.println("提交课件和笔记");
	}
	final void liveVideo() {
		System.out.println("直播授课");
	}
	final void createPPT() {
		System.out.println("创建备课PPT");
	}
	final void postPreResource() {
		System.out.println("分发预习资料");
	}
}
