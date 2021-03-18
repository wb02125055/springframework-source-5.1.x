package com.wb.spring.supplier;

import com.alibaba.fastjson.JSONObject;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Description:
 *
 * @author wangbin33
 * @date 2020/11/20 8:48
 */
public class TestMain {
	public static void main(String[] args) {

		ApplicationContext acx  = new ClassPathXmlApplicationContext("classpath:supplier/supplier.xml");
		UserObj bean = acx.getBean(UserObj.class);
		System.out.println(bean.getName());

		UserObj obj = new UserObj("wangbing");
		System.out.println(JSONObject.toJSONString(obj));
	}
}
