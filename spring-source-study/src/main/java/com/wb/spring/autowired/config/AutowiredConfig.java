package com.wb.spring.autowired.config;

import com.wb.spring.autowired.beans.Goods;
import com.wb.spring.autowired.beans.ShopCar;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

/**
 * Created by wangbin33 on 2020/3/8.
 */
@Configuration
@ComponentScan({"com.wb.spring.autowired"})
@EnableWebMvc
public class AutowiredConfig {
	@Bean
	@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
	public ShopCar shopCar() {
		ShopCar shopCar = new ShopCar();
		shopCar.setName("购物车！");
		shopCar.setGoods(goods());
		return shopCar;
	}
	@Bean
	@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
	public Goods goods() {
		Goods good = new Goods();
		good.setName("测试商品！");
		good.setPrice(1200D);
		return good;
	}
}
