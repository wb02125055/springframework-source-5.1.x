package com.wb.spring.ext.beans;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.config.DestructionAwareBeanPostProcessor;

import javax.annotation.PreDestroy;

/**
 * @author wangbin33
 * @date Created in 12:52 2019/12/7
 */
public class Blue {
	private String colorName;

	public String getColorName() {
		return colorName;
	}

	public void setColorName(String colorName) {
		this.colorName = colorName;
	}

	@Override
	public String toString() {
		return "Blue{" +
				"colorName='" + colorName + '\'' +
				'}';
	}

	@PreDestroy
	public void destroy1() {
		System.out.println("destroy1.");
	}
}
