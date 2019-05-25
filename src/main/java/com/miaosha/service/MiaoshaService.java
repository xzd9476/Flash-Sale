package com.miaosha.service;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.miaosha.dao.MiaoshaUserDao;
import com.miaosha.domain.MiaoshaUser;
import com.miaosha.exception.GlobalException;
import com.miaosha.redis.MiaoshaUserKey;
import com.miaosha.result.CodeMsg;
import com.miaosha.util.MD5Util;
import com.miaosha.util.UUIDUtil;
import com.miaosha.vo.LoginVo;

@Service
public class MiaoshaService {

	public static final String COOKI_NAME_TOKEN = "token";

	@Autowired
	private MiaoshaUserDao miaoshaUserDao;

	@Autowired
	RedisService redisService;

	public MiaoshaUser getById(long id) {
		//读缓存
		MiaoshaUser user=redisService.get(MiaoshaUserKey.getById, ""+id, MiaoshaUser.class);
		if(user!=null){
			return user;
		}
		//取数据库
		user=miaoshaUserDao.getById(id);
		if(user!=null){
			redisService.set(MiaoshaUserKey.getById, ""+id, user);
		}
		return user;
	}
	
	
	public boolean updatePassword(String token,long id,String formPass) {
		//取user
		MiaoshaUser user=getById(id);
		if(user==null){
			throw new GlobalException(CodeMsg.MOBILE_NOTEXITS);
		}
		//更新数据库
		MiaoshaUser toBeUpdate=new MiaoshaUser();
		toBeUpdate.setId(id);
		toBeUpdate.setPassword(MD5Util.formPassToDBPass(formPass, user.getSalt()));
		miaoshaUserDao.update(toBeUpdate);
		//处理缓存（token、user）
		redisService.delete(MiaoshaUserKey.getById,""+id);
		user.setPassword(toBeUpdate.getPassword());
		redisService.set(MiaoshaUserKey.token,token,user);
		return true;
	}
	
	

	public boolean login(HttpServletResponse response, LoginVo loginVo) {
		// TODO Auto-generated method stub
		if (loginVo == null) {
			throw new GlobalException(CodeMsg.SERVER_ERROR);
		}
		String mobile = loginVo.getMobile();
		String formPass = loginVo.getPassword();
		MiaoshaUser user = getById(Long.parseLong(mobile));
		if (user == null)
			throw new GlobalException(CodeMsg.MOBILE_NOTEXITS);
		// 验证密码
		String dbPass = user.getPassword();
		String dbSalt = user.getSalt();
		String calcPass = MD5Util.formPassToDBPass(formPass, dbSalt);
		if (!calcPass.equals(dbPass)) {
			throw new GlobalException(CodeMsg.PASSWORD_ERROR);
		}
		String token = UUIDUtil.uuid();
		// 生成cookie
		addCookie(response,token, user);
		return true;
	}

	/*
	 * redis中存储token-value
	 * 生成cookie对象token-value保存到浏览器根目录setPath("/")
	 */
	private void addCookie(HttpServletResponse response,String token,MiaoshaUser user) {
		redisService.set(MiaoshaUserKey.token, token, user);
		Cookie cookie = new Cookie(COOKI_NAME_TOKEN, token);
		cookie.setMaxAge(MiaoshaUserKey.token.expireSeconds());
		cookie.setPath("/");
		response.addCookie(cookie);
	}

	public MiaoshaUser getByToken(HttpServletResponse response,String token) {
		if (StringUtils.isEmpty(token))
			return null;
		MiaoshaUser user = redisService.get(MiaoshaUserKey.token, token, MiaoshaUser.class);
		// 延长过期时间(覆盖之前的token，cokkie也重新保存)
		// 生成cookie
		if(user!=null){
			addCookie(response, token,user);
		}
		
		return user;
	}

}
