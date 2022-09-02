package com.wb.spring.schedule.javaconfig.component;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * created by IDEA
 *
 * @author wangbing
 * @date 2022.09.2022/9/2.22:14
 */
@Component
public class MyTask {

	@Scheduled(cron = "*/2 * * * * ?")
	public void runTask() {
		Date date = new Date();
		System.out.println("当前时间为：" + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date));
	}
}
