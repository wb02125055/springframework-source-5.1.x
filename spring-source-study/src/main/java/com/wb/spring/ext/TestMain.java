package com.wb.spring.ext;

import com.wb.spring.ext.config.ExtConfig;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * @author wangbin33
 * @date Created in 12:16 2019/12/7
 */
public class TestMain {

	public static void main(String[] args) {
		AnnotationConfigApplicationContext acx = new AnnotationConfigApplicationContext();
		acx.register(ExtConfig.class);
		acx.refresh();
		acx.publishEvent(new ApplicationEvent(new String("我发布的测试事件...")) {});
		// 发布一个自定义事件.
		// 容器关闭
		acx.close();
	}
}