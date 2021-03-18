package com.wb.spring.autowired;

import com.wb.spring.autowired.beans.InstWorkBean;
import com.wb.spring.autowired.config.AutowiredConfig;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * Description:
 *
 * @author wangbin33
 * @date 2020/6/24 18:20
 */
public class TestMain2 {
	public static void main(String[] args) throws Exception {

		ApplicationContext acx = new AnnotationConfigApplicationContext(AutowiredConfig.class);

//		InstWorkBean bean = acx.getBean(InstWorkBean.class);
//
//		System.out.println(bean);

		InstWorkBean.class.newInstance().run();

		int a = 1;
//		bean.run();
	}
}
