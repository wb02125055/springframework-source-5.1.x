package com.wb.spring.designpattern.singleton;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * Created by wangbin33 on 2020/1/1.
 */
public class SeriableSingletonTest {
	public static void main(String[] args) {
		SeriableSingleton s1 = null;
		SeriableSingleton s2 = SeriableSingleton.getInstance();

		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream("D:/SeriableSingleton.obj");
			ObjectOutputStream oos = new ObjectOutputStream(fos);
			oos.writeObject(s2);
			oos.flush();
			oos.close();

			FileInputStream fis = new FileInputStream("D:/SeriableSingleton.obj");
			ObjectInputStream ois = new ObjectInputStream(fis);
			s1 = (SeriableSingleton) ois.readObject();
			ois.close();

			System.out.println(s1);
			System.out.println(s2);
			System.out.println(s1 == s2);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
