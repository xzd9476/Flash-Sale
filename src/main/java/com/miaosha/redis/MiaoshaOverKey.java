package com.miaosha.redis;

public class MiaoshaOverKey extends BasePrefix{
	private MiaoshaOverKey(String prefix){
		super(prefix);
	}
	
	public static MiaoshaOverKey isGoodsOver =new MiaoshaOverKey("go");

}
