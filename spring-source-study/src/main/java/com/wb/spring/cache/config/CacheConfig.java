package com.wb.spring.cache.config;

import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCache;
import org.springframework.cache.support.SimpleCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import java.util.HashSet;
import java.util.Set;

/**
 * Description: 缓存配置
 *
 * @author wangbin33
 * @date 2021/2/9 9:10
 */
@Configuration
@ComponentScan(basePackages = {"com.wb.spring.cache"})
@EnableCaching
public class CacheConfig {

	@Bean
	public CacheManager cacheManager() {
		SimpleCacheManager cacheManager = new SimpleCacheManager();
		cacheManager.setCaches(caches());
		return cacheManager;
	}

	@Bean
	public Set<Cache> caches() {
		Set<Cache> cacheSet = new HashSet<>();
		cacheSet.add(new ConcurrentMapCache("userCache"));
		return cacheSet;
	}
}
