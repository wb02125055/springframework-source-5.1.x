package com.wb.spring.beans;

import org.springframework.stereotype.Component;

/**
 * @author wangbin33
 * @date Created in 12:44 2019/11/30
 */
@Component
public class Popsicle implements Dessert {
	@Override
	public String sayName() {
		return "popsicle";
	}
}
