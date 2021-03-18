package com.wb.spring.postprocessor.processor;

import com.wb.spring.postprocessor.domain.UserService;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

/**
 * Created by wangbin33 on 2020/1/16.
 */
@Component
public class PostComponent {

	@Resource
	private UserService userService;

	@PostConstruct
	public void init() {
		userService.save();
		System.out.println("PostComponent... PostConstruct...");
	}
}
