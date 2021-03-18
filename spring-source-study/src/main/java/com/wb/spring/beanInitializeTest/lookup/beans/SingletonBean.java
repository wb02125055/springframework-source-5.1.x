package com.wb.spring.beanInitializeTest.lookup.beans;

import org.springframework.beans.factory.annotation.Lookup;
import org.springframework.stereotype.Component;

@Component
public abstract class SingletonBean {

	/**
	@Autowired
	private ApplicationContext acx;

	public void printBean() {
		PrototypeBean prototypeBean =
				this.acx.getBean("prototypeBean", PrototypeBean.class);
		System.out.println(prototypeBean);
	}
	*/

	public void printBean() {
		PrototypeBean prototypeBean = methodInject();
		System.out.println(prototypeBean);
	}
	@Lookup(value = "prototypeBean")
	protected abstract PrototypeBean methodInject();
}
