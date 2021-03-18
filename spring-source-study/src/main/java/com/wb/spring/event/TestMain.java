package com.wb.spring.event;

import com.wb.spring.event.config.EventConfig;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * Description:
 *
 * @author wangbin33
 * @date 2020/5/5 20:56
 */
public class TestMain {
	public static void main(String[] args) {
		AnnotationConfigApplicationContext acx = new AnnotationConfigApplicationContext();
		acx.register(EventConfig.class);
		acx.refresh();
		acx.publishEvent(new TestEvent(acx, "测试..."));
		acx.close();
	}
}
