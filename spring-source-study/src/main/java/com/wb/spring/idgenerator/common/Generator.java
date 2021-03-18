package com.wb.spring.idgenerator.common;

import java.util.Map;

/**
 * Description:
 *
 * @author wangbin33
 * @date 2020/8/8 9:18
 */
public interface Generator extends IdGenerator {

	SpecGenerator getDefaultSpecGenerator();

	Map<String, SpecGenerator> getSpecGeneratorMap();
}
