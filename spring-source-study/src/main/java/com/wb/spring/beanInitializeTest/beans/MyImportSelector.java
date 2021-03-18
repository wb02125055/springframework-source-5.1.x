package com.wb.spring.beanInitializeTest.beans;

import org.springframework.context.annotation.ImportSelector;
import org.springframework.core.type.AnnotationMetadata;

/**
 * @author wangbin33
 * @date Created in 23:11 2019/11/30
 */
public class MyImportSelector implements ImportSelector {
	@Override
	public String[] selectImports(AnnotationMetadata importingClassMetadata) {
		return new String[] {"com.wb.spring.beanInitializeTest.beans.InstE"};
	}
}
