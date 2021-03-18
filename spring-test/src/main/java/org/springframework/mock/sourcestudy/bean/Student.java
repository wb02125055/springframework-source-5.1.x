package org.springframework.mock.sourcestudy.bean;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

/**
 * @author wangbin33
 * @date Created in 23:03 2019/10/2
 */
@Component
public class Student implements InitializingBean {
	@Override
	public void afterPropertiesSet() throws Exception {
		System.out.println("student afterPropertiesSet...");
	}
}
