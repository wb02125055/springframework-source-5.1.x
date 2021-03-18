package com.wb.spring.supplier;

/**
 * Description:
 *
 * @author wangbin33
 * @date 2020/11/20 8:46
 */
public class UserObjSupplier {
	public static UserObj createUser() {
		return new UserObj("wangbing");
	}
}
