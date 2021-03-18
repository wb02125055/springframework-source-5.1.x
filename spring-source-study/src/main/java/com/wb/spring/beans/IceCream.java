package com.wb.spring.beans;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

/**
 * @author wangbin33
 * @date Created in 12:37 2019/11/30
 */
@Component
//@Creamy
//@Ice
@Qualifier("iceCream")
public class IceCream implements Dessert {
	@Override
	public String sayName() {
		return "iceCream.";
	}
}