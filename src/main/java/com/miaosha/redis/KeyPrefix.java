package com.miaosha.redis;

public interface KeyPrefix {
	int expireSeconds();
	String getPrefix();

}
