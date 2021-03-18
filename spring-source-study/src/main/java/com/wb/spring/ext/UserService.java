package com.wb.spring.ext;

import org.springframework.context.ApplicationEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

/**
 * @author wangbin33
 * @date Created in 10:45 2019/12/8
 */
@Service
public class UserService {

	@EventListener(classes = {ApplicationEvent.class})
	public void listen(ApplicationEvent event) {
		System.out.println("UserService收到的事件：" + event);
	}
}
