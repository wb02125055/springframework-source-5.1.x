package com.wb.spring.beans;

import org.springframework.beans.factory.FactoryBean;
import org.springframework.lang.Nullable;

/**
 * Created by wangbin33 on 2020/2/15.
 */
public class ColorFactoryBean implements FactoryBean<Color> {
	@Nullable
	@Override
	public Color getObject() throws Exception {
		return new Color();
	}

	@Nullable
	@Override
	public Class<?> getObjectType() {
		return Color.class;
	}
	@Override
	public boolean isSingleton() {
		return true;
	}
}
