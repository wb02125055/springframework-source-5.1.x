package com.wb.spring.async.service;

/**
 * Description:
 *
 * @author wangbin33
 * @date 2020/8/10 18:34
 */
public interface AsyncService {
	/**
	 * 调用第三方http接口合成用户图像
	 */
	void asyncGenerateImg();
}
