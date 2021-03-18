package com.wb.spring.ioc.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * Created by wangbin33 on 2020/2/14.
 */
@Configuration
@ComponentScan(basePackages = "com.wb.spring.ioc.dao")
public class DaoConfig {
}
