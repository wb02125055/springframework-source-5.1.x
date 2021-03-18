package com.wb.spring.profile.config;

import com.mchange.v2.c3p0.ComboPooledDataSource;
import com.wb.spring.profile.beans.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.*;

import javax.sql.DataSource;

/**
 * Created by wangbin33 on 2020/3/8.
 *
 * Profile: 指定组件在哪个环境的情况下才能被注册到容器中。不指定，任何环境下都能注册这个组件.
 *
 * （1）写在某一个Bean上，加了环境表示的bean，只有这个环境被激活的时候，才能注册到容器中。默认为default环境.即：@Profile("default")
 *
 * （2）标注在配置类上，只有在指定的环境下，整个配置类中的所有配置才能生效，控制的是整个配置类的运行环境
 *
 * （3）没有标注环境表示的bean，则在任何环境下都会被加载
 */
@Configuration
@ComponentScan({"com.wb.spring.profile"})
@PropertySource({"classpath:db.properties"})
//@Profile("dev")
public class ProfileConfig {

	@Value("${db.user}")
	private String user;

	@Value("${db.password}")
	private String password;

	@Value("${db.driverClass}")
	private String driverClass;

	@Bean
//	@Profile("test")
	public User user() {
		return new User();
	}

	@Bean
//	@Profile("dev")
	public DataSource dataSourceDev() throws Exception {
		ComboPooledDataSource dataSource = new ComboPooledDataSource();
		setParams(dataSource, "jdbc:mysql://127.0.0.1:3306/db_dev");
		return dataSource;
	}

	@Bean
	@Profile("test")
	public DataSource dataSourceTest() throws Exception {
		ComboPooledDataSource dataSource = new ComboPooledDataSource();
		setParams(dataSource, "jdbc:mysql://127.0.0.1:3306/db_test");
		return dataSource;
	}

	@Bean
//	@Profile("prod")
	public DataSource dataSourceProd() throws Exception {
		ComboPooledDataSource dataSource = new ComboPooledDataSource();
		setParams(dataSource, "jdbc:mysql://127.0.0.1:3306/db_prod");
		return dataSource;
	}

	private void setParams(ComboPooledDataSource dataSource, String jdbcUrl) throws Exception {
		dataSource.setJdbcUrl(jdbcUrl);
		dataSource.setUser(user);
		dataSource.setPassword(password);
		dataSource.setDriverClass(driverClass);
	}
}
