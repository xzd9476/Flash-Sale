package com.miaosha.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.miaosha.redis.KeyPrefix;
import com.miaosha.redis.RedisConfig;

import net.bytebuddy.build.ToStringPlugin.Enhance.Prefix;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

@Service
public class RedisService{
	@Autowired
	JedisPool jedisPool;

	/*
	 * 获取
	 */
	public <T> T get(KeyPrefix prefix, String key, Class<T> clazz) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			// 生成真正的key
			String realKey = prefix.getPrefix() + key;
			String str = jedis.get(realKey);
			T t = stringToBean(str, clazz);
			return t;
		} finally {
			retunToPool(jedis);
		}
	}
	
	/*
	 * 新增键值对
	 */
	public <T> boolean set(KeyPrefix prefix, String key, T value) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			String str = beanToString(value);
			if (str == null || str.length() <= 0) {
				return false;
			}
			// 生成真正的key
			String realKey = prefix.getPrefix() + key;
			int seconds = prefix.expireSeconds();
			if (seconds <= 0) {// 永不过期
				jedis.set(realKey, str);
			} else {
				jedis.setex(realKey, seconds, str);
			}

			return true;
		} finally {
			retunToPool(jedis);
		}
	}
	
	/*
	 * 删除
	 */
	public boolean delete(KeyPrefix prefix, String key) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			// 生成真正的key
			String realKey = prefix.getPrefix() + key;
			long ret= jedis.del(realKey);
			return ret > 0;
		} finally {
			retunToPool(jedis);
		}
	}
	
	

	/*
	 * 判断是否存在
	 */
	public <T> boolean exists(KeyPrefix prefix, String key, T value) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			// 生成真正的key
			String realKey = prefix.getPrefix() + key;
			return jedis.exists(realKey);
		} finally {
			retunToPool(jedis);
		}
	}

	/*
	 * 原子性 +1
	 */
	public <T> Long incr(KeyPrefix prefix, String key) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			// 生成真正的key
			String realKey = prefix.getPrefix() + key;
			return jedis.incr(realKey);
		} finally {
			retunToPool(jedis);
		}
	}
	
	/*
	 * 原子性 -1
	 */
	public <T> Long dec(KeyPrefix prefix, String key) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			// 生成真正的key
			String realKey = prefix.getPrefix() + key;
			return jedis.decr(realKey);
		} finally {
			retunToPool(jedis);
		}
	}

	public static <T> String beanToString(T value) {
		// TODO Auto-generated method stub
		if (value == null) {
			return null;
		}
		Class<?> clazz = value.getClass();
		if (clazz == int.class || clazz == Integer.class) {
			return "" + value;
		} else if (clazz == String.class) {
			return (String) value;
		} else if (clazz == long.class || clazz == Long.class) {
			return "" + value;
		} else {
			return JSON.toJSONString(value);
		}

	}
	@SuppressWarnings("unchecked")
	public static <T> T stringToBean(String str, Class<T> clazz) {
		// TODO Auto-generated method stub
		if (str == null || str.length() <= 0 || clazz == null) {
			return null;
		}
		if (clazz == int.class || clazz == Integer.class) {
			return (T) Integer.valueOf(str);
		} else if (clazz == String.class) {
			return (T) str;
		} else if (clazz == long.class || clazz == Long.class) {
			return (T) Long.valueOf(str);
		} else {
			return JSON.toJavaObject(JSON.parseObject(str), clazz);
		}
	}

	private void retunToPool(Jedis jedis) {
		// TODO Auto-generated method stub
		if (jedis != null) {
			jedis.close();
		}

	}

}
