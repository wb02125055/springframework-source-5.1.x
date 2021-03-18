package com.wb.spring.t;

import org.springframework.beans.factory.FactoryBean;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * @author wangbin33
 * @date 2020/11/14 10:11
 */
@Component("pigFactory")
public class PigFactoryBean implements FactoryBean<Pig> {
	@Override
	public Pig getObject() {
		Pig pig = new Pig();
		pig.setName(UUID.randomUUID().toString());
		return pig;
	}
	@Override
	public Class<?> getObjectType() {
		return Pig.class;
	}
	@Override
	public boolean isSingleton() {
		// 如果返回false，表示每次通过factoryBean去创建bean实例的时候都需要重新调用getObject方法进行创建
		return true;
	}
}
