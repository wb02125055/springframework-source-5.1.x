package com.wb.spring.idgenerator.handle;

import com.wb.spring.idgenerator.common.Generator;
import com.wb.spring.idgenerator.common.SpecGenerator;

import java.util.Map;

/**
 * Description:
 *
 * @author wangbin33
 * @date 2020/8/8 9:52
 */
public class IdHandle implements Generator {
	/**
	 * 特殊序列生成器对象
	 */
	private SpecGenerator defaultSpecGenerator;
	/**
	 * 序列生成器集合
	 */
	private Map<String, SpecGenerator> specGeneratorMap;

	public void setDefaultSpecGenerator(SpecGenerator defaultSpecGenerator) {
		this.defaultSpecGenerator = defaultSpecGenerator;
	}

	public void setSpecGeneratorMap(Map<String, SpecGenerator> specGeneratorMap) {
		this.specGeneratorMap = specGeneratorMap;
	}

	@Override
	public SpecGenerator getDefaultSpecGenerator() {
		return this.defaultSpecGenerator;
	}

	@Override
	public Map<String, SpecGenerator> getSpecGeneratorMap() {
		return this.specGeneratorMap;
	}

	@Override
	public long get(String name) {
		SpecGenerator specGenerator = null;
		if (null != specGeneratorMap) {
			specGenerator = specGeneratorMap.get(name);
		}
		if (null == specGenerator) {
			return getDefaultSpecGenerator().get(name);
		}
		return specGenerator.get(name);
	}
}
