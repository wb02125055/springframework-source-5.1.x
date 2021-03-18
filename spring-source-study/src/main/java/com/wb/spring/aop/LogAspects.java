package com.wb.spring.aop;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.Arrays;

/**
 * @author wangbin33
 * @date Created in 18:02 2019/10/6
 *
 * // 当调用不符合环绕通知规则的时候，执行顺序为：
 * Before -> After -> AfterReturning (如果有异常，则AfterThrowing)
 *
 * // 当调用符合环绕通知规则的时候，执行顺序为：
 * Around Before -> Before -> 目标方法 -> Around After -> After -> AfterReturning (如果有异常，则AfterThrowing)
 */
@Aspect
@Component
public class LogAspects {
	@Pointcut("execution(public int com.wb.spring.aop.MathCalculator.*(..))")
//	@Order(1) // 定义多个切点时，可以使用Order指定切面的执行顺序，Order中的数字越小，表示优先级越高，越先被执行
	public void pointCut() {}

	@Around("pointCut()")
	public Object doAround(ProceedingJoinPoint pjp) throws Throwable {
		// 获取被增强的目标对象，然后获取目标对象的class
		Class<?> targetClass = pjp.getTarget().getClass();
		System.out.println("执行Around，被增强的目标类为：" + targetClass);
		// 方法名称
		String methodName = pjp.getSignature().getName();
		System.out.println("执行Around，目标方法名称为：" + methodName);
		// 目标方法的参数类型
		Class[] parameterTypes = ((MethodSignature) pjp.getSignature()).getParameterTypes();
		// 目标方法的入参
		Object[] args = pjp.getArgs();
		System.out.println("执行Around，方法入参为：" + Arrays.toString(args));
		try {
			// 目标方法
			Method method = targetClass.getMethod(methodName, parameterTypes);
			System.out.println("执行Around，方法为：" + method);
//			int i = 1 / 0;
			Object o = pjp.proceed();
			System.out.println("执行完毕Around...");
			return o;
		} catch (Throwable e) {
			System.err.println("执行Around异常..." + e);
			throw e;
		}
	}

	@Before("pointCut()")
	public void logStart(JoinPoint joinPoint) {
		Object[] args = joinPoint.getArgs();
		System.out.println(joinPoint.getSignature().getName() + "运行Before... 参数为：" + Arrays.asList(args));
	}

	@After("pointCut()")
	public void logEnd(JoinPoint joinPoint) {
		System.out.println(joinPoint.getSignature().getName() + "运行After...");
	}

	@AfterReturning(value = "pointCut()", returning = "result")
	public void logReturn(JoinPoint joinPoint, Object result) {
		System.out.println(joinPoint.getSignature().getName() + "运行AfterReturning... 正常返回，结果为：" + result);
	}

	@AfterThrowing(value = "pointCut()", throwing = "exception")
	public void logException(JoinPoint joinPoint, Exception exception) {
		System.out.println(joinPoint.getSignature().getName() + "运行AfterThrowing... 异常信息：" + exception);
	}
}