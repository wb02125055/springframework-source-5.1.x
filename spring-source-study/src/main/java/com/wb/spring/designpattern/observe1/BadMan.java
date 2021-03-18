package com.wb.spring.designpattern.observe1;

import java.util.Observable;

/**
 * Description:
 *
 * @author wangbin33
 * @date 2020/11/7 16:21
 */
public class BadMan extends Observable {

	public void run() {
		System.out.println("罪犯准备逃跑了");
		this.setChanged();
		this.notifyObservers("开始拿枪追赶!");
	}
}
