package com.wb.spring.designpattern.observe;

/**
 * Description: 被观察者
 *
 * @author wangbin33
 * @date 2020/11/7 15:55
 */
public interface Observable {

	void registerObserver(Observer observer);

	void notifyObserver(String thingName);
}
