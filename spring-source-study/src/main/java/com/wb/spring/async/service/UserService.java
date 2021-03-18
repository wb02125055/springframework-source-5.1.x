package com.wb.spring.async.service;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

/**
 * @author wangbin33
 * @date 2020/8/10 18:36
 */
@Service
// @Async
public class UserService {

	@Resource
	private AsyncService asyncService;


//	@Autowired
//	private ContextAware contextAware;

	public void insertUser(String name, Integer age) {
//		System.out.println("aware:" + contextAware);
		System.out.println("调用userDao保存用户基本信息！name:" + name + ",age :" + age);
		asyncService.asyncGenerateImg();
		asyncFunc();
		System.out.println("保存用户信息完毕！");
	}


	@Async
	private void asyncFunc() {
		System.out.println("asyncFunc start...");
		try {
			TimeUnit.SECONDS.sleep(10);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		System.out.println("asyncFunc end...");
	}
}
