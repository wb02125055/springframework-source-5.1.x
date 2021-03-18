package com.wb.spring.beaninit;

import com.wb.spring.beaninit.config.BeanConfig1;
import com.wb.spring.beaninit.domain.A;
import com.wb.spring.beaninit.domain.B;
import com.wb.spring.beaninit.domain.User;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.util.StringTokenizer;

/**
 * @author wangbin33
 * @date 2020/4/26 20:16
 *
 * 循环依赖解决：
 * 	(1) 使用set方法注入，这也是spring推荐的方式
 * 	(2) 如果使用构造器进行注入，可以通过在构造器的参数上对需要注入的属性添加@Lazy注解
 *      @Autowired
 *      public A(@Lazy B b) {
 * 			this.b = b;
 * 		}
 */
public class TestMain {
	public static void main(String[] args) {

		AnnotationConfigApplicationContext acx = new AnnotationConfigApplicationContext();
//		// 注册配置类
		acx.register(BeanConfig1.class);
//		// 刷新容器
		acx.refresh();
//		Object a = acx.getBean(A.class);
//		Object b = acx.getBean(B.class);
//		System.out.println(a);
//		System.out.println(b);


//		String property = System.getProperty("user.name");
//		System.out.println(property);


//		String reqUrl = "/123123/aafsadfsa/1231/2134213.xlsx?sign=12312313125132eqda123213";
//		String fileNameAndSign = reqUrl.substring(reqUrl.lastIndexOf("/"));
//		System.out.println("fileNameAndSign: " + fileNameAndSign);
//		String fileName = fileNameAndSign.substring(1, fileNameAndSign.indexOf(".", 1)) + ".xlsx";
//		System.out.println("fileName: " + fileName);
//		System.out.println(fileNameAndSign);

		String str = "1,2,3,4,5,6,7,8,9,10";
		StringTokenizer tokenizer = new StringTokenizer(str, ",");
		while (tokenizer.hasMoreElements()) {
//			String s = tokenizer.nextToken();
			Object o = tokenizer.nextElement();
//			System.out.println(s);
			System.out.println(o);
		}
	}
}
