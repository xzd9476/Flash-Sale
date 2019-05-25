package com.miaosha.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.miaosha.rabbitmq.MQSender;
import com.miaosha.result.Result;

@Controller
@RequestMapping("/demo")
public class DemoController {
//	@Autowired
//	MQSender sender;
//	
//	@RequestMapping("/mq")
//	@ResponseBody
//	public Result<String> mq(){
//		sender.send("hello mq");
//		return Result.success("hello world");
//	}

}
