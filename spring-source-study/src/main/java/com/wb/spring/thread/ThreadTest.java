package com.wb.spring.thread;

import java.util.concurrent.TimeUnit;

/**
 * Description:
 *
 * @author wangbin33
 * @date 2020/9/17 19:16
 */
public class ThreadTest {

	public static void main(String[] args) throws InterruptedException {
//		Test t = new Test();
//		t.start();

		Test1 t = new Test1();
		Thread th = new Thread(t);
		th.start();

		TimeUnit.SECONDS.sleep(2);
//		t.setFlag(false);

		t.change();
	}
}

class Test1 implements Runnable {
	private boolean flag = true;
	int c = 0;
	@Override
	public void run() {
		while (flag) {
			int a = 1;
			int b = 1;
			c = a + b;
		}
		System.out.println("c: " + c);
		System.out.println("end");
	}
	public void change() {
		this.flag = false;
	}
}

class Test extends Thread {
	private volatile boolean flag = true;
	public void change() {
		this.flag = false;
	}
	int c = 0;
	@Override
	public void run() {
		while (flag) {
			int a = 1;
			int b = 1;
			c = a + b;
		}
		System.out.println("c: " + c);
		System.out.println("end");
	}
}