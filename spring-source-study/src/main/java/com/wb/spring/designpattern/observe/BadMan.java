package com.wb.spring.designpattern.observe;

import java.util.ArrayList;
import java.util.List;

/**
 * Description:
 *
 * @author wangbin33
 * @date 2020/11/7 15:57
 */
public class BadMan implements Observable {

	private List<Observer> observerList = new ArrayList<>();

	@Override
	public void registerObserver(Observer observer) {
		this.observerList.add(observer);
	}

	@Override
	public void notifyObserver(String thingName) {
		for (Observer observer : observerList) {
			observer.doThing(thingName);
		}
	}

	public void run() {
		System.out.println("罪犯开始逃跑了");
		this.notifyObserver("追击罪犯");
	}
	public void play() {
		System.out.println("罪犯在玩");
		this.notifyObserver("不用追击，监视就行");
	}
}
