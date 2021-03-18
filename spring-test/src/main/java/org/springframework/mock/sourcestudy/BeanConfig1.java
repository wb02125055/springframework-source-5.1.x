package org.springframework.mock.sourcestudy;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author wangbin33
 * @date Created in 15:27 2019/10/2
 */
@Configuration
public class BeanConfig1 {

	@Value("${test.name:wangbing1}")
	private String name;

	@Bean
	public Person person() {
		System.out.println("name : " + name);
		return new Person("wangbing");
	}
}
