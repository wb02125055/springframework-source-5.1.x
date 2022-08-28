package com.wb.spring.multipleBeanName;

/**
 * created by IDEA
 *
 * @author wangbing
 * @date 2022.08.2022/8/28.17:21
 */
public class TestBean {
	private String beanName;
	private Integer beanNo;

	public String getBeanName() {
		return beanName;
	}

	public void setBeanName(String beanName) {
		this.beanName = beanName;
	}

	public Integer getBeanNo() {
		return beanNo;
	}

	public void setBeanNo(Integer beanNo) {
		this.beanNo = beanNo;
	}

	@Override
	public String toString() {
		return "TestBean{" +
				"beanName='" + beanName + '\'' +
				", beanNo=" + beanNo +
				'}';
	}
}
