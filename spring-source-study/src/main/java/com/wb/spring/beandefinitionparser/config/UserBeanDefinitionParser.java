package com.wb.spring.beandefinitionparser.config;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.beans.factory.xml.BeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.util.StringUtils;
import org.w3c.dom.Element;

/**
 * Created by wangbin33 on 2020/1/9.
 *
 * 标签属性解析器
 */
// 继承自AbstractSingleBeanDefinitionParser也可以实现.
public class UserBeanDefinitionParser implements BeanDefinitionParser {

	private final Class<?> beanClass;

	public UserBeanDefinitionParser(Class<?> beanClass) {
		this.beanClass = beanClass;
	}

	@Override
	public BeanDefinition parse(Element element, ParserContext parserContext) {

		// 创建bean定义
		RootBeanDefinition beanDefinition = new RootBeanDefinition();
		// 设置bean定义对应的class类
		beanDefinition.setBeanClass(beanClass);

		String id = element.getAttribute("id");
		String name = element.getAttribute("name");
		String sex = element.getAttribute("sex");
		String email = element.getAttribute("email");
		// 此处可以加一些自定义的校验逻辑，校验必填属性的值及类型或者格式等
		if (null == id || "".equals(id.trim())) {
			throw new RuntimeException("id不能为空！");
		}
		// 注册bean定义到bean定义注册中心
		parserContext.getRegistry().registerBeanDefinition(id, beanDefinition);

		if (StringUtils.hasText(id)) {
			beanDefinition.getPropertyValues().addPropertyValue("id", id);
		}
		if (StringUtils.hasText(name)) {
			beanDefinition.getPropertyValues().addPropertyValue("name", name);
		}
		if (StringUtils.hasText(sex)) {
			beanDefinition.getPropertyValues().addPropertyValue("sex", sex);
		}
		if (StringUtils.hasText(email)) {
			beanDefinition.getPropertyValues().addPropertyValue("email", email);
		}
		return beanDefinition;
	}

//	@Nullable
//	@Override
//	protected Class<?> getBeanClass(Element element) {
//		return User.class;
//	}
//
//	@Override
//	protected void doParse(Element element, BeanDefinitionBuilder builder) {
//		String id = element.getAttribute("id");
//		String name = element.getAttribute("name");
//		String sex = element.getAttribute("sex");
//		String email = element.getAttribute("email");
//
//		if (StringUtils.hasText(id)) {
//			builder.addPropertyValue("id", id);
//		}
//		if (StringUtils.hasText(name)) {
//			builder.addPropertyValue("name", name);
//		}
//		if (StringUtils.hasText(sex)) {
//			builder.addPropertyValue("sex", sex);
//		}
//		if (StringUtils.hasText(email)) {
//			builder.addPropertyValue("email", email);
//		}
//	}
}
