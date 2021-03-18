package com.wb.spring.ioc.service;

import com.wb.spring.ioc.dao.SheepDao;
import com.wb.spring.ioc.domain.Sheep;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author wangbin33
 * @date Created in 16:04 2019/11/24
 */
@Service("sheepService")
public class SheepServiceImpl implements SheepService {

	@Resource
	private SheepDao userDao;

	@Override
	public void save(Sheep sheep) {
		System.out.println("保存了sheep..." + sheep);
	}
}
