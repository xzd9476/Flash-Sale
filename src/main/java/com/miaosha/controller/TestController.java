package com.miaosha.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.miaosha.domain.MiaoshaUser;
import com.miaosha.result.Result;

@Controller
@RequestMapping("/yace")
public class TestController {
	
	@RequestMapping("/info")
	@ResponseBody
	public Result<MiaoshaUser> info(Model model,MiaoshaUser user){
		return Result.success(user); 
	}

}
