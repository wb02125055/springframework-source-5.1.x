package com.wb.spring.async.service;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/**
 * Description:
 *
 * @author wangbin33
 * @date 2020/8/10 18:34
 */
@Service
//@Async
public class AsyncServiceImpl implements AsyncService {
//	@Override
//	public void asyncProcess() {
//		System.out.println(Thread.currentThread().getName() + ">>>>>>>>>>>>异步任务开始处理<<<<<<<<<<<<");
//		sleepSeconds(5);
//		System.out.println(Thread.currentThread().getName() + ">>>>>>>>>>>>异步任务处理结束<<<<<<<<<<<<");
//	}
//
//	@Override
//	public void asyncInsert() {
//		System.out.println(Thread.currentThread().getName() + ">>>>>>>>>>>>>异步写入日志信息开始<<<<<<<<<<<<");
//		sleepSeconds(20);
//		System.out.println(Thread.currentThread().getName() + ">>>>>>>>>>>>>异步写入日志信息结束<<<<<<<<<<<<");
//	}
//
//	@Override
//	public void asyncInsertLog1() {
//		System.out.println(Thread.currentThread().getName() + ">>>>>>>>>>>>>异步写入特殊日志信息开始<<<<<<<<<<<<");
//		sleepSeconds(10);
//		System.out.println(Thread.currentThread().getName() + ">>>>>>>>>>>>>异步写入特殊日志信息结束<<<<<<<<<<<<");
//	}


//	@Override
	public void createUser() {
		System.out.println("开始保存用户信息！");
	}

	@Override
	@Async
	public void asyncGenerateImg() {
		System.out.println("开始异步合成图片!!!");
		sleepSeconds(4);
		System.out.println("异步合成图片完毕!!!");
	}

//	@Override
	public void asyncInsertLog1() {

	}

	private void sleepSeconds(long second) {
		try {
			TimeUnit.SECONDS.sleep(second);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
