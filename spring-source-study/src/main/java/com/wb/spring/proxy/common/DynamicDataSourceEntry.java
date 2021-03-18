package com.wb.spring.proxy.common;

/**
 * Created by wangbin33 on 2020/2/8.
 */
public class DynamicDataSourceEntry {
	private static final String DEFAULT_SOURCE = null;
	private static final ThreadLocal<String> local = new ThreadLocal<>();

	private DynamicDataSourceEntry() {}

	public static void clear() {
		local.remove();
	}

	public static String get() {
		return local.get();
	}

	public static void restore() {
		local.set(DEFAULT_SOURCE);
	}

	public static void set(String source) {
		local.set(source);
	}

	public static void set(int year) {
		local.set("DB_" + year);
	}
}
