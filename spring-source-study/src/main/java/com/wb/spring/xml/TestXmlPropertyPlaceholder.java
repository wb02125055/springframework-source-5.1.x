package com.wb.spring.xml;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.xml.XmlBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.core.io.ClassPathResource;

/**
 * Description:
 *
 * 在xml中的import标签中，如果使用了占位符。项目启动的时候要正常，有如下几种方案：
 * （1）将占位符对应的key值，放置到pom.xml中，在使用maven编译期间，将其动态替换
 * （2）通过设置JVM启动参数，使用-Dkey=value的方式，在加载xml之前，把key对应的值加入到系统环境变量中
 * （3）通过继承ClassPathXmlApplicationContext类，并重写initPropertySources方法，将占位符中使用的key放入到系统环境变量对应的map中
 *
 * 最终目的还是需要保证在xml加载之前能够获取到key对应的value值。使用普通的properties中配置key的方式，会存在加载xml的时候properties中的
 * 值还没有解析，所以无法正常替换掉占位符对应的key，导致提示：can't resolve property placeholder of name "menu.env" 类似的错误
 *
 * @author wangbin33
 * @date 2020/8/6 14:27
 */
public class TestXmlPropertyPlaceholder {
	public static void main(String[] args) {
//		ApplicationContext acx = new MyXmlApplicationContext("spring-all.xml");
		ApplicationContext acx = new ClassPathXmlApplicationContext("spring-${user.name}.xml");
		Object menu = acx.getBean("menu");
		Object otherProcessor = acx.getBean("otherProcessor");
		System.out.println(menu);
		System.out.println(otherProcessor);

//		BeanFactory beanFactory = new XmlBeanFactory(new ClassPathResource("spring-all.xml"));
//		Object result = beanFactory.getBean("testBean");
//		System.out.println(result);
	}
}
