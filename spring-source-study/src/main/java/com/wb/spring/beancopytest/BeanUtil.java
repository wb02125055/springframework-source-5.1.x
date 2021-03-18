package com.wb.spring.beancopytest;

import org.springframework.cglib.beans.BeanCopier;

import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by wangbin33 on 2020/3/9.
 * 测试BeanCopier的性能.
 */
public class BeanUtil {
	/**
	 * 存放BeanCopier
	 */
	private static final ConcurrentHashMap<String, BeanCopier> BEAN_COPIERS = new ConcurrentHashMap<>();
	/**
	 * 将 srcObj copy至 targetObj
	 * BeanCopier 拷贝 应用拷贝字节码技术 spring 的beanUtils.copy 应用 反射技术
	 * @param srcObj srcObj
	 * @param targetObj targetObj
	 */
	public static void copy(Object srcObj, Object targetObj) {
		String key = genKey(srcObj.getClass(), targetObj.getClass());
		BeanCopier beanCopier;
		if (!BEAN_COPIERS.containsKey(key)) {
			beanCopier = BeanCopier.create(srcObj.getClass(), targetObj.getClass(), false);
			BEAN_COPIERS.put(key, beanCopier);
		} else {
			beanCopier = BEAN_COPIERS.get(key);
		}
		beanCopier.copy(srcObj, targetObj, null);
	}
	/**
	 * 获取缓存中 beanCopier 的key
	 *
	 * @param srcClass srcClass
	 * @param destClass destClass
	 */
	private static String genKey(Class<?> srcClass, Class<?> destClass) {
		return srcClass.getName() + "-" + destClass.getName();
	}
}
