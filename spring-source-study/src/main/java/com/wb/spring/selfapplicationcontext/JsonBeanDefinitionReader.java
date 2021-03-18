package com.wb.spring.selfapplicationcontext;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.BeanDefinitionStoreException;
import org.springframework.beans.factory.config.ConstructorArgumentValues;
import org.springframework.beans.factory.config.RuntimeBeanReference;
import org.springframework.beans.factory.support.AbstractBeanDefinitionReader;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanNameGenerator;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.context.annotation.AnnotationBeanNameGenerator;
import org.springframework.core.NamedThreadLocal;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.EncodedResource;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StreamUtils;

import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Description: json格式的bean定义读取器
 *
 * @author wangbin33
 * @date 2020/11/20 15:46
 */
public class JsonBeanDefinitionReader extends AbstractBeanDefinitionReader {

	private final ThreadLocal<Set<EncodedResource>> resourcesCurrentlyBeingLoaded =
			new NamedThreadLocal<>("Json bean definition resources currently being loaded.");

	/**
	 * 构造函数中完成该类中一些成员变量的初始化
	 */
	protected JsonBeanDefinitionReader(BeanDefinitionRegistry registry) {
		super(registry);
	}

	@Override
	public int loadBeanDefinitions(Resource resource) throws BeanDefinitionStoreException {
		return loadBeanDefinitions(new EncodedResource(resource));
	}

	public int loadBeanDefinitions(EncodedResource encodedResource) {
		Set<EncodedResource> currentResources = this.resourcesCurrentlyBeingLoaded.get();
		if (null == currentResources) {
			currentResources = new HashSet<>(4);
			this.resourcesCurrentlyBeingLoaded.set(currentResources);
		}
		if (!currentResources.add(encodedResource)) {
			// 重复加载资源
			throw new BeanDefinitionStoreException("Detected cyclic loading of " +
					encodedResource + " - check your import definitions!");
		}
		String json = "";
		try (InputStream inputStream = encodedResource.getResource().getInputStream()) {
			json = StreamUtils.copyToString(inputStream, StandardCharsets.UTF_8);
		} catch (Exception e) {
			e.printStackTrace();
			return 0;
		} finally {
			currentResources.remove(encodedResource);
			if (currentResources.isEmpty()) {
				this.resourcesCurrentlyBeingLoaded.remove();
			}
		}
		List<GenericBeanDefinition> beanDefinitionList = JSON.parseArray(json, GenericBeanDefinition.class);
		if (CollectionUtils.isEmpty(beanDefinitionList)) {
			return 0;
		}
		for (GenericBeanDefinition beanDefinition : beanDefinitionList) {
			Class<?> clazz = null;
			try {
				clazz = Thread.currentThread().getContextClassLoader().loadClass(beanDefinition.getBeanClassName());
			} catch (Exception e) {

			}
			beanDefinition.setBeanClass(clazz);

			ConstructorArgumentValues constructorArgumentValues = beanDefinition.getConstructorArgumentValues();
			if (constructorArgumentValues.isEmpty()) {
				continue;
			}
			Map<Integer, ConstructorArgumentValues.ValueHolder> map = constructorArgumentValues.getIndexedArgumentValues();
			if (CollectionUtils.isEmpty(map)) {
				continue;
			}
			for (ConstructorArgumentValues.ValueHolder valueHolder : map.values()) {
				Object value = valueHolder.getValue();
				if (value instanceof JSONObject) {
					JSONObject jsonObject = (JSONObject) value;
					RuntimeBeanReference reference = jsonObject.toJavaObject(RuntimeBeanReference.class);
					valueHolder.setValue(reference);
				}
			}
			setBeanNameGenerator(new AnnotationBeanNameGenerator());
			BeanNameGenerator beanNameGenerator = getBeanNameGenerator();
			BeanDefinitionRegistry registry = getRegistry();
			for (GenericBeanDefinition genericBeanDefinition : beanDefinitionList) {
				String beanName = beanNameGenerator.generateBeanName(genericBeanDefinition, registry);
				registry.registerBeanDefinition(beanName, genericBeanDefinition);
			}
		}
		return beanDefinitionList.size();
	}
}
