package com.wb.spring.ioc.dao;

import com.wb.spring.ioc.domain.Sheep;
import org.springframework.stereotype.Repository;

/**
 * Created by wangbin33 on 2020/2/14.
 */
@Repository
public class SheepDao {

	public void insert(Sheep sheep) {
		System.out.println("UserDao insert..." + sheep);
	}
}
