package com.wb.springmvc.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.stereotype.Controller;

/**
 * Created by wangbin33 on 2020/1/2.
 * 根容器，根据注解排除所有标注有@Controller注解的类.
 */
@ComponentScan(value = "com.wb.springmvc",
		excludeFilters = {@ComponentScan.Filter(type = FilterType.ANNOTATION, classes = {Controller.class})})
public class RootConfig {

}
