package com.wb.spring.componentregister.component;

import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;

/**
 * Created by wangbin33 on 2020/1/10.
 *
 * 匹配windows系统，如果当前系统名称中包含Windows关键字，则注入带有该匹配条件的Bean.
 */
public class WindowsConditional implements Condition {
	@Override
	public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
		String osName = context.getEnvironment().getProperty("os.name");
		System.out.println(osName);
		return osName.toLowerCase().contains("Windows");
	}
}
