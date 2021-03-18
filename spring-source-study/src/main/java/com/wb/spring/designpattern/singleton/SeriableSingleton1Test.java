package com.wb.spring.designpattern.singleton;

import java.io.*;

/**
 * Created by wangbin33 on 2020/1/1.
 *
 * 测试使用序列化破坏单例模式
 *
 * 原因：ObjectOutputStream
 *  ---> readObject0
 *     --->readOrdinaryObject
 *     	 --->desc.isInstantiable
 *
 */
public class SeriableSingleton1Test {
	public static void main(String[] args) {
		SeriableSingleton1 s1 = null;
		SeriableSingleton1 s2 = SeriableSingleton1.getInstance();

		try {
			FileOutputStream fos = new FileOutputStream("D:/seriableData.obj");
			ObjectOutputStream oos = new ObjectOutputStream(fos);
			oos.writeObject(s2);
			oos.flush();
			oos.close();

			FileInputStream fis = new FileInputStream("D:/seriableData.obj");
			ObjectInputStream ois = new ObjectInputStream(fis);
			s1 = (SeriableSingleton1) ois.readObject();
			ois.close();

			System.out.println(s1);
			System.out.println(s2);
			System.out.println(s1 == s2);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
