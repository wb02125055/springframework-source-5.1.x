package org.springframework.mock.sourcestudy;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author wangbin33
 * @date Created in 14:45 2019/10/2
 */
@Configuration
public class MainConfig {

	@Bean
	public Color color() {
		Color color = new Color();
		color.setName("color1");
		color.setAge(12);
		return color;
	}
}