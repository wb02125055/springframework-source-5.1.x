package com.wb.spring.designpattern.delegate;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by wangbin33 on 2020/2/17.
 */
public class Leader implements IEmployee {

	private Map<String, IEmployee> targets = new HashMap<>();

	public Leader() {
		this.targets.put("加密", new EmployeeA());
		this.targets.put("登陆", new EmployeeB());
	}

	@Override
	public void doing(String command) {
		this.targets.get(command).doing(command);
	}
}
