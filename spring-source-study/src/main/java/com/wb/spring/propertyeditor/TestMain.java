package com.wb.spring.propertyeditor;

import com.wb.spring.propertyeditor.domain.Student;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Description: propereditor使用
 *
 * @author wangbin33
 * @date 2020/10/20 16:48
 */
public class TestMain {
	public static void main(String[] args) {
		ConfigurableApplicationContext acx = new ClassPathXmlApplicationContext("selfEditor.xml");
		Student bean = acx.getBean(Student.class);
		System.out.println(bean);
		acx.close();
	}
}
