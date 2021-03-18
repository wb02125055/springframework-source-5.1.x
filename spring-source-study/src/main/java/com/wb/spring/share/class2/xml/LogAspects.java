package com.wb.spring.share.class2.xml;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;

import java.lang.reflect.Method;
import java.util.Arrays;

/**
 * @author wangbin33
 */
public class LogAspects {

	public Object doAround(ProceedingJoinPoint pjp) throws Throwable {
		// 获取被增强的目标对象，然后获取目标对象的class
		Class<?> targetClass = pjp.getTarget().getClass();
		// 方法名称
		String methodName = pjp.getSignature().getName();
		// 目标方法的参数类型
		Class[] parameterTypes = ((MethodSignature) pjp.getSignature()).getParameterTypes();
		// 目标方法的入参
		Object[] args = pjp.getArgs();
		System.out.println("执行Around，方法入参为：" + Arrays.toString(args));
		try {
			// 目标方法
			Method method = targetClass.getMethod(methodName, parameterTypes);
			System.out.println("开始执行目标方法，方法为：" + method);
			Object o = pjp.proceed();
			System.out.println("执行完毕目标方法，Around...");
			return o;
		} catch (Throwable e) {
			System.err.println("执行Around异常..." + e);
			throw e;
		}
	}

	public void logStart(JoinPoint joinPoint) {
		Object[] args = joinPoint.getArgs();
		System.out.println(joinPoint.getSignature().getName() + "运行Before... 参数为：" + Arrays.asList(args));
	}

	public void logEnd(JoinPoint joinPoint) {
		System.out.println(joinPoint.getSignature().getName() + "运行After...");
	}

	public void logReturn(JoinPoint joinPoint, Object result) {
		System.out.println(joinPoint.getSignature().getName() + "运行AfterReturning... 正常返回，结果为：" + result);
	}

	public void logException(JoinPoint joinPoint, Exception exception) {
		System.out.println(joinPoint.getSignature().getName() + "运行AfterThrowing... 异常信息：" + exception);
	}
}
