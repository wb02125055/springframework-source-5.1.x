package com.wb.spring.beans;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * @author wangbin33
 * @date Created in 21:38 2019/11/14
 */
public class BeansTest {
	public static void main(String[] args) {
//		ClassPathXmlApplicationContext acx = new ClassPathXmlApplicationContext("beans.xml");
//		AnnotationConfigApplicationContext acx = new AnnotationConfigApplicationContext(BeanConfig.class);
////		acx.getBean(Person.class);
//		Person person = (Person) acx.getBean("person");
//		Person person1 = (Person) acx.getBean("person");
//		System.out.println(person);
//		System.out.println(person1);

		AnnotationConfigApplicationContext acx1 = new MyApplicationContext();
		acx1.register(BeanConfig.class);
		acx1.refresh();
//		Object person = acx1.getBean("person");
//		System.out.println(person);
//		acx.close();


//		ApplicationContext acx = new AnnotationConfigApplicationContext(BeanConfig.class);

//		Object obj1 = acx.getBean("iceCream");
//		Object obj2 = acx.getBean("ice");
//		Object obj3 = acx.getBean("creamy");
//
//		System.out.println(obj1);
//		System.out.println(obj2);
//		System.out.println(obj3);

//		Object person = acx.getBean("person");
//		System.out.println(person);


//		Object obj = acx.getBean("colorFactoryBean");
//		Object obj1 = acx.getBean("&colorFactoryBean");
//
//		Class clazz = obj.getClass();
//		Class clazz1 = obj1.getClass();
//
//
//		System.out.println("colorFactoryBean的类型为：" + clazz);
//		System.out.println("colorFactoryBean的类型为：" + clazz1);
	}
}
