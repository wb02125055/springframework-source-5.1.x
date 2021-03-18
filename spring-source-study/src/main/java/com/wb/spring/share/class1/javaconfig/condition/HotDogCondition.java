package com.wb.spring.share.class1.javaconfig.condition;

import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.env.Environment;
import org.springframework.core.type.AnnotatedTypeMetadata;

/**
 * Description:
 *
 * @author wangbin33
 * @date 2020/11/1 23:01
 */
public class HotDogCondition implements Condition {
	@Override
	public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
		Environment environment = context.getEnvironment();
		// 获取系统变量
		String property = environment.getProperty("user.name");
		return null != property && property.equals("wangbin33");
	}
}
