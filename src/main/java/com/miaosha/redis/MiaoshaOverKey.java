package com.miaosha.redis;

public class MiaoshaOverKey extends BasePrefix{
	private MiaoshaOverKey(int expireSeconds,String prefix){
		super(expireSeconds,prefix);
	}
	
	public static MiaoshaOverKey isGoodsOver =new MiaoshaOverKey(0,"go");
	public static MiaoshaOverKey getMiaoshaPath =new MiaoshaOverKey(60,"mp");

}
