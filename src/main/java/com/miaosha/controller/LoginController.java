package com.miaosha.controller;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.validator.internal.util.logging.LoggerFactory;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.miaosha.result.CodeMsg;
import com.miaosha.result.Result;
import com.miaosha.service.MiaoshaService;
import com.miaosha.util.ValidatorUtil;
import com.miaosha.vo.LoginVo;

@Controller
@RequestMapping("/login")
public class LoginController {
	private static Logger log=org.slf4j.LoggerFactory.getLogger(LoginController.class);
	
	@Autowired
	private MiaoshaService userService;
	
	@RequestMapping("/to_login")
	public String toLogin(){
		return "login";
	}
	
	/*
	 * 来自login.html的请求
	 */
	@RequestMapping("/do_login")
	@ResponseBody
	public Result<Boolean> doLogin(HttpServletResponse response,@Valid LoginVo loginVo){
		log.info(loginVo.toString());
		//若遇到错误，则在login中处理
		userService.login(response,loginVo);
		return Result.success(true);
	}

}
