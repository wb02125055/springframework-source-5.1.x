package com.wb.spring.aop.test;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@Configuration
@ComponentScan("com.wb.spring.aop.test")
@EnableAspectJAutoProxy
public class AopConfig1 {
}
