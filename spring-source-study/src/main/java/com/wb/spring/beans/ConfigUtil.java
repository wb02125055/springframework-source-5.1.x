package com.wb.spring.beans;

/**
 * @author wangbin33
 * @date Created in 21:33 2019/11/14
 */
public class ConfigUtil {

	private static ConfigCenterClient client;

	public void setClient(ConfigCenterClient client) {
		ConfigUtil.client = client;
	}

	public static String getConfig(String name) {
		return client.getConfig(name);
	}
}
