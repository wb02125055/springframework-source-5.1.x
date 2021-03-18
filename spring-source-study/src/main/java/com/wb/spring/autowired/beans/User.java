package com.wb.spring.autowired.beans;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by wangbin33 on 2020/3/8.
 */
@Component
public class User {
	private Pen pen;
	public Pen getPen() {
		return this.pen;
	}
//	private Monkey monkey;

//	public User(Monkey monkey) {
//		this.monkey = monkey;
//	}

	public void setPen(@Autowired Pen pen) {
		this.pen = pen;
	}


//	public User(Pen pen) {
//		this.pen = pen;
//	}

//	public void setPen(@Autowired Pen pen) {
//		this.pen = pen;
//	}

	@Override
	public String toString() {
		return "User{" +
				"pen=" + pen +
				'}';
	}
}
