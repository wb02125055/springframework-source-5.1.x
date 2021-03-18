package com.wb.spring.ioc;

import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.env.Environment;
import org.springframework.core.type.AnnotatedTypeMetadata;

/**
 * Created by wangbin33 on 2020/1/31.
 */
public class LinuxConditional implements Condition {
	@Override
	public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
		Environment environment = context.getEnvironment();
		String osName = environment.getProperty("os.name");
		if (osName != null && osName.contains("Windows")) {
			return true;
		}
		return false;
	}
}
