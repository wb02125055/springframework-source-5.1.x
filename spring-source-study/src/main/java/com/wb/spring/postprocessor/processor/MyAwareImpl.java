package com.wb.spring.postprocessor.processor;

import org.springframework.stereotype.Component;

/**
 * Description:
 *
 * @author wangbin33
 * @date 2020/10/24 11:03
 */
@Component
public class MyAwareImpl implements MyAware {

	private String awareName;

	@Override
	public void setMy(String awareName) {
		this.awareName = awareName;
	}
}
