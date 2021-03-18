package com.wb.spring.beans;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

/**
 * @author wangbin33
 * @date Created in 12:36 2019/11/30
 */
@Component
@Qualifier(value = "cookies")
public class Cookies implements Dessert {
	@Override
	public String sayName() {
		return "cookies.";
	}
}
