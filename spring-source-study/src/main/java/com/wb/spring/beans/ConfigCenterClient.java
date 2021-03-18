package com.wb.spring.beans;

/**
 * @author wangbin33
 * @date Created in 21:33 2019/11/14
 */
public class ConfigCenterClient {

	private String appName;

	private String appId;

	public String getAppName() {
		return appName;
	}

	public void setAppName(String appName) {
		this.appName = appName;
	}

	public String getAppId() {
		return appId;
	}

	public void setAppId(String appId) {
		this.appId = appId;
	}

	public String getConfig(String name) {
		System.out.println("appName: " + appName + ", appId: " + appId);
		return name;
	}

	@Override
	public String toString() {
		return "ConfigCenterClient{" +
				"appName='" + appName + '\'' +
				", appId='" + appId + '\'' +
				'}';
	}
}
