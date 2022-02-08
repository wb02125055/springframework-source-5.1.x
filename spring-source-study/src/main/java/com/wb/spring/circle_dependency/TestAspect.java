package com.wb.spring.circle_dependency;

import com.alibaba.fastjson.JSONObject;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;

import java.util.Arrays;

/**
 *
 */
@Aspect
public class TestAspect {

	@Pointcut("execution(* com.wb.spring.circle_dependency.ClassA.*(..))")
	public void pointCut() {}

	@Around("pointCut()")
	public void run(ProceedingJoinPoint pjp) throws Throwable {
		System.out.println("执行Around，被增强的目标类为：" + pjp.getTarget().getClass());

		// 方法名称
		System.out.println("执行Around，目标方法名称为：" + pjp.getSignature().getName());

		// 目标方法的参数类型
		String[] parameterNames = ((MethodSignature) pjp.getSignature()).getParameterNames();
		System.out.println("参数名称：" + Arrays.toString(parameterNames));

		// 目标方法的入参
		Object[] args = pjp.getArgs();

		System.out.println(JSONObject.toJSONString(args));
	}
}
