package com.wb.spring.designpattern.singleton;

/**
 * Description: 线程调试示例
 *
 * @author wangbin33
 * @date 2020/5/26 8:56
 */
public class ThreadDebug {
	public static void main(String[] args) {
		for (int i = 0 ; i < 2; i++) {
			new Thread(()->{
				LazySimpleSingleton t = LazySimpleSingleton.getInstance();
				System.out.println(t);
			}).start();
		}
	}
}
