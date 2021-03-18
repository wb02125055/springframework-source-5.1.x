package com.wb.spring.ext;

import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

/**
 * @author wangbin33
 * @date Created in 13:13 2019/12/7
 */
@Component
public class MyApplicationListener implements ApplicationListener<ApplicationEvent> {
	/**
	 * 当容器中发布事件之后，会触发该方法
	 * @param event the event to respond to
	 */
	@Override
	public void onApplicationEvent(ApplicationEvent event) {
		System.out.println("收到事件：" + event);
	}
}
