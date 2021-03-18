package com.wb.spring.propertyvalue.domain;

import org.springframework.beans.factory.annotation.Value;

import javax.annotation.PostConstruct;

/**
 * Created by wangbin33 on 2020/2/24.
 */
public class Person {

//	@PostConstruct
//	public void runTask() {
//		System.out.println("******.............");
//		try {
//			Thread.sleep(300000L);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//	}

	@PostConstruct
	public void initTask() {
		System.out.println("******");
		new Thread(()-> {
			try {
				Thread.sleep(300000L);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}).start();
		System.out.println("init finished...");
	}

	/**
	 * 给属性赋值的方法：
	 * (1)xml配置文件;
	 * (2)@Value中直接写变量的值
	 * (3)@Value注解中使用SpEL表达式
	 * (4)@Value中引入properties配置文件中的值（properties文件中的值会被解析完成之后放入到当前容器运行的环境中）
	 */
	@Value("#{1+2}")
	private Integer id;

	@Value("wb")
	private String name;

	/**
	 * 使用@Value取properties中的值，需要将properties文件导入
	 * (1)在xml中通过<context:property-placeholder location="classpath:db.properties"/>标签引入
	 * (2)使用@PropertySource或者@PropertySources注解引入配置文件中的值
	 */
	@Value("${person.nick:bw}")
	private String nickName;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getNickName() {
		return nickName;
	}

	public void setNickName(String nickName) {
		this.nickName = nickName;
	}

	@Override
	public String toString() {
		return "Person{" +
				"id=" + id +
				", name='" + name + '\'' +
				", nickName='" + nickName + '\'' +
				'}';
	}
}
