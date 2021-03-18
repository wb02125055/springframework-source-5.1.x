package com.wb.spring.idgenerator.test;

import com.wb.spring.idgenerator.handle.IdSpecHandle;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * Description:
 *
 * @author wangbin33
 * @date 2020/8/8 10:31
 */
public class TestMain {
	public static void main(String[] args) {
		ApplicationContext acx = new AnnotationConfigApplicationContext(IdGeneratorConfig.class);

		IdSpecHandle idSpecHandle = acx.getBean(IdSpecHandle.class);
		for (int i = 0 ; i < 1000; i++) {
			long id = idSpecHandle.get("test");
			System.out.println("id: " + id);
		}
	}
}
