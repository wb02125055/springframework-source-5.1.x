package com.wb.spring.schedule.javaconfig.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * created by IDEA
 *
 * @author wangbing
 * @date 2022.09.2022/9/2.22:12
 */
@Configuration
@ComponentScan(basePackages = "com.wb.spring.schedule.javaconfig")
@EnableScheduling
public class ScheduleConfig {
}
