package com.wb.spring.autowired.config;

import com.wb.spring.autowired.beans.Goods;
import com.wb.spring.autowired.beans.ShopCar;
import com.wb.spring.autowired.dao.UserDao;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.*;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

/**
 * Created by wangbin33 on 2020/3/8.
 */
@Configuration
@ComponentScan({"com.wb.spring.autowired"})
//@EnableWebMvc
public class AutowiredConfig {
	@Bean("userDao")
	public UserDao userDao() {
		System.out.println("userDao1 bean init...");
		UserDao userDao = new UserDao();
		userDao.setFlag("2");
		return userDao;
	}
	@Bean("userDao")
	@Primary
	public UserDao userDao1() {
		System.out.println("userDao2 bean init...");
		UserDao userDao = new UserDao();
		userDao.setFlag("1");
		return userDao;
	}
//	@Bean
//	@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
//	public ShopCar shopCar() {
//		ShopCar shopCar = new ShopCar();
//		shopCar.setName("购物车！");
//		shopCar.setGoods(goods());
//		return shopCar;
//	}
//	@Bean
//	@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
//	public Goods goods() {
//		Goods good = new Goods();
//		good.setName("测试商品！");
//		good.setPrice(1200D);
//		return good;
//	}
}
