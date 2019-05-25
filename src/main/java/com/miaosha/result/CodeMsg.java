package com.miaosha.result;

public class CodeMsg {
	private int code;
	private String msg;
	public static CodeMsg SUCCESS=new CodeMsg(0,"success");
	public static CodeMsg SERVER_ERROR=new CodeMsg(500100, "服务端异常");
	public static CodeMsg BIND_ERROR=new CodeMsg(500101, "参数校验异常：%s");
	
	public static CodeMsg SESSION_ERROR=new CodeMsg(500210, "Session不存在或者已经失效");
	public static CodeMsg PAWWROED_EMPTY=new CodeMsg(500211, "登录密码不能空");
	public static CodeMsg MOBILE_EMPTY=new CodeMsg(500212, "手机号不能空");
	public static CodeMsg MOBILE_ERROR=new CodeMsg(500213, "手机号格式错误");
	public static CodeMsg MOBILE_NOTEXITS=new CodeMsg(500214, "手机号不存在");
	public static CodeMsg PASSWORD_ERROR=new CodeMsg(500215, "密码错误");
	
	
	public static CodeMsg ORDER_NOT_EXIST=new CodeMsg(500400, "订单不存在");
	//秒杀的错误信息
	public static CodeMsg MIAO_SHA_OVER=new CodeMsg(500500, "商品已经秒杀完毕");
	public static CodeMsg REPEATE_MIAOSHA=new CodeMsg(500500, "不能重复秒杀");
	
	private CodeMsg(int code,String msg){
		this.code=code;
		this.msg=msg;
	}
	public int getCode() {
		return code;
	}
	public void setCode(int code) {
		this.code = code;
	}
	public String getMsg() {
		return msg;
	}
	public void setMsg(String msg) {
		this.msg = msg;
	}
	
	//可返回带参数的异常
	public CodeMsg fillArgs(Object...args){
		int code=this.code;
		String message=String.format(this.msg, args);
		return new CodeMsg(code, message);
	}
	

}
