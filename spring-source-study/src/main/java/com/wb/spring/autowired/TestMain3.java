package com.wb.spring.autowired;

import com.wb.spring.autowired.beans.Goods;
import com.wb.spring.autowired.beans.ShopCar;
import com.wb.spring.autowired.config.AutowiredConfig;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * Description:
 *
 * @author wangbin33
 * @date 2020/7/3 23:56
 */
public class TestMain3 {
	public static void main(String[] args) {
		ApplicationContext acx = new AnnotationConfigApplicationContext(AutowiredConfig.class);
		ShopCar shopCar1 = acx.getBean(ShopCar.class);
		ShopCar shopCar2 = acx.getBean(ShopCar.class);
		ShopCar shopCar3 = acx.getBean(ShopCar.class);

		System.out.println(shopCar1);
		System.out.println(shopCar2);
		System.out.println(shopCar3);


//		Goods goods1 = acx.getBean(Goods.class);
//		Goods goods2 = acx.getBean(Goods.class);
//		Goods goods3 = acx.getBean(Goods.class);
//
//		System.out.println(goods1);
//		System.out.println(goods2);
//		System.out.println(goods3);
	}
}
