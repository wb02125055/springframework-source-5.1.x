package com.wb.spring.finishBeanFactoryInitialization;

import com.wb.spring.finishBeanFactoryInitialization.domain.Person;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Created by wangbin33 on 2020/1/27.
 *
 * 如果直接获取bean，然后打印对应的属性，则会报异常：
 * 【Failed to convert property value of type 'java.lang.String' to required type 'java.util.Date' for property 'birthday'】
 *
 * 需要在Spring容器中配置对应的conversionService转换器，进行String->Date类型的转换.
 */
public class TestMain {
	public static void main(String[] args) {
		ApplicationContext acx = new ClassPathXmlApplicationContext("test4.xml");
		Person person = (Person) acx.getBean("person");
		System.out.println(person.name);
		System.out.println(person.birthday);
	}
}
