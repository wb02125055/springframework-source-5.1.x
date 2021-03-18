package com.wb.spring.event;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/**
 * Description:
 *
 * @author wangbin33
 * @date 2020/5/5 20:57
 */
@Component
public class TestComponent {
	@EventListener
	public void test(TestEvent testEvent) {
		System.out.println("testEvent was invoked..." + testEvent);
	}
}
