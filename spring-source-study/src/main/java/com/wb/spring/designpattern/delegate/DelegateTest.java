package com.wb.spring.designpattern.delegate;

/**
 * Created by wangbin33 on 2020/2/17.
 *
 * 例如：Spring源码中的BeanDefinitionParserDelegate类，就利用了委派设计模式，
 *      根据不同类型委派不同逻辑去解析BeanDefinition
 */
public class DelegateTest {
	public static void main(String[] args) {
		new Boss().command("登陆", new Leader());
	}
}
