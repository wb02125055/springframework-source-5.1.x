package com.wb.spring.share.class1.javaconfig.condition;

import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.env.Environment;
import org.springframework.core.type.AnnotatedTypeMetadata;

/**
 * Description:
 *
 * @author wangbin33
 * @date 2020/11/1 22:56
 */
public class WindowsCondition implements Condition {
	@Override
	public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
		Environment environment = context.getEnvironment();
		String property = environment.getProperty("os.name");
		return property != null && property.toLowerCase().contains("windows");
	}
}
