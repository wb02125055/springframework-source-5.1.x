package com.wb.springmvc.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Description:
 *
 * @author wangbin33
 * @date 2021/3/7 19:20
 */
@Controller
@RequestMapping("/test")
public class TestController {
	@RequestMapping("/_invoke")
	@ResponseBody
	public String test() {
		return "invoke";
	}
}
