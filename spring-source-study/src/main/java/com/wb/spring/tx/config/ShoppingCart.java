package com.wb.spring.tx.config;

import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;
import org.springframework.web.context.WebApplicationContext;

/**
 * @author wangbin33
 * @date 2020/7/10 20:41
 */
@Component
@Scope(value = WebApplicationContext.SCOPE_SESSION,
		proxyMode = ScopedProxyMode.INTERFACES)
public class ShoppingCart {
	// ... 代码略
}
