package com.wb.springmvc.config;

import org.springframework.lang.Nullable;
import org.springframework.web.servlet.support.AbstractAnnotationConfigDispatcherServletInitializer;

import javax.servlet.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by wangbin33 on 2020/1/2.
 *
 * 应用启动初始化器
 */
public class WebAppInitializer extends AbstractAnnotationConfigDispatcherServletInitializer {
	/**
	 * 获取父容器配置类，在容器初始化的时候会回调该方法加载配置类
	 */
	@Nullable
	@Override
	protected Class<?>[] getRootConfigClasses() {
		return new Class<?>[] {RootConfig.class};
	}
	/**
	 * 获取子容器配置类，在容器初始化的时候会回调该方法加载配置类
	 */
	@Nullable
	@Override
	protected Class<?>[] getServletConfigClasses() {
		return new Class<?>[] {AppConfig.class};
	}
	/**
	 * 获取所有的uri映射，完成handlerMapping
	 */
	@Override
	protected String[] getServletMappings() {
		return new String[] {"/"};
	}


	/**
	 * 表示是否支持Servlet的异步请求处理
	 * 	默认为true.表示支持异步，如果不支持异步，返回为false即可.
	 */
	@Override
	protected boolean isAsyncSupported() {
		return false;
	}

	/**
	 * 获取自定义的过滤器.
	 */
//	@Override
//	protected Filter[] getServletFilters() {
//		List<Filter> filters = new ArrayList<>();
//		Filter filter = new Filter() {
//			@Override
//			public void init(FilterConfig filterConfig) {
//				System.out.println("filterConfig invoke ...");
//			}
//
//			@Override
//			public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) {
//				System.out.println("doFilter... invoke...");
//			}
//
//			@Override
//			public void destroy() {
//				System.out.println("servlet destroy...");
//			}
//		};
//		filters.add(filter);
//		return filters.toArray(new Filter[0]);
//	}

	/**
	 * 获取Servlet的名称
	 * 		默认为dispatcher
	 */
	@Override
	protected String getServletName() {
		return "myDispatcher";
	}
}
