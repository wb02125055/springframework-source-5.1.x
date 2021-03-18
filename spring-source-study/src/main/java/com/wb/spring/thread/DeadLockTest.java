package com.wb.spring.thread;

/**
 * Created by wangbin33 on 2020/2/29.
 */
public class DeadLockTest {
	public static void main(String[] args) {
		for (int i = 0 ; i < 10000; i++) {
			new Thread(new LockTask(1,2)).start();
			new Thread(new LockTask(2,1)).start();
		}
	}
}
class LockTask implements Runnable {

	private int a;
	private int b;

	public LockTask(int a, int b) {
		this.a = a;
		this.b = b;
	}

	@Override
	public void run() {
		synchronized (Integer.valueOf(a)) {
			synchronized (Integer.valueOf(b)) {
				System.out.println("----------------------");
			}
		}
	}
}
