package com.wb.spring.event;

import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;

/**
 * Description:
 *
 * @author wangbin33
 * @date 2020/5/5 20:56
 */
public class TestEvent extends ApplicationEvent {
	private String text;
	public TestEvent(Object source, String text) {
		super(source);
		this.text = text;
	}
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}

	@Override
	public String toString() {
		return "TestEvent{" +
				"text='" + text + '\'' +
				'}';
	}
}
