package com.wb.spring.beandefinitionparser.config;

import com.wb.spring.beandefinitionparser.domain.User;
import org.springframework.beans.factory.xml.NamespaceHandlerSupport;

/**
 * Created by wangbin33 on 2020/1/9.
 * 解析自定义标签
 */
public class MyTagNamespaceHandler extends NamespaceHandlerSupport {
	@Override
	public void init() {
		registerBeanDefinitionParser("user", new UserBeanDefinitionParser(User.class));
	}
}
