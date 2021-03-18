package com.wb.spring.aop.exposeproxy;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

/**
 * Created by wangbin33 on 2020/2/15.
 */
@Configuration
@ComponentScan(basePackages = "com.wb.spring.aop.exposeproxy")
@EnableAspectJAutoProxy(exposeProxy = true)
public class StudentServiceConfig {
}
