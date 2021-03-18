package com.wb.spring.populate_test;

/**
 * Description:
 *
 * @author wangbin33
 * @date 2020/12/30 9:25
 */
public class PService {

	private PDao pDao;

	public void printDao() {
		System.out.println("pDao: " + pDao);
	}

	public void setpDao(PDao pDao) {
		this.pDao = pDao;
	}
}
