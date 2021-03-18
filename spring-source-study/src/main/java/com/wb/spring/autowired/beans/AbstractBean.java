package com.wb.spring.autowired.beans;

import org.springframework.context.ApplicationContext;

/**
 * Description: 通过ApplicationContext提供的api进行bean的注入.
 *
 * @author wangbin33
 * @date 2020/6/25 10:43
 */
public abstract class AbstractBean {

	public AbstractBean() {
		injectBean();
	}

	private void injectBean() {
		ApplicationContext acx = ApplicationContextUtils.getContext();
		// this指的是某一个AbstractBean的子类对象.
		acx.getAutowireCapableBeanFactory().autowireBean(this);
	}

	protected abstract void run();
}
