package com.wb.spring.beanInitializeTest.lookup.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackages = "com.wb.spring.beanInitializeTest.lookup.beans")
public class LookUpConfig {
}
