package com.wb.spring.finishBeanFactoryInitialization.config;

import org.springframework.core.convert.converter.Converter;
import org.springframework.lang.Nullable;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by wangbin33 on 2020/1/27.
 *
 * 自定义转换器，将String类型的属性值转换为Date类型
 */
public class MyConverter implements Converter<String, Date> {
	@Nullable
	@Override
	public Date convert(String source) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		try {
			return sdf.parse(source);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
}
