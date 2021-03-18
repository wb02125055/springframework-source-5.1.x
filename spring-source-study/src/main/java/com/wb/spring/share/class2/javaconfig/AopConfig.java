package com.wb.spring.share.class2.javaconfig;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

/**
 * Description: 注解式AOP配置
 * @author wangbin33
 */
@Configuration
@ComponentScan(basePackages = {"com.wb.spring.share.class2"})
@EnableAspectJAutoProxy
public class AopConfig {
}