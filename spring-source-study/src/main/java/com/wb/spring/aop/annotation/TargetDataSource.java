package com.wb.spring.aop.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by wangbin33 on 2020/1/14.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface TargetDataSource {
	/**
	 * @return
	 */
	String value() default "master";
}
