package com.miaosha.service;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.miaosha.dao.OrderDao;
import com.miaosha.domain.MiaoshaOrder;
import com.miaosha.domain.MiaoshaUser;
import com.miaosha.domain.OrderInfo;
import com.miaosha.redis.OrderKey;
import com.miaosha.vo.GoodsVo;

@Service
public class OrderService {
	@Autowired
	OrderDao orderDao;
	
	@Autowired
	RedisService redisService;

	//通过userid和goodsid查询是否有这个订单
	public MiaoshaOrder getMiaoshaOrderByUserIdGoodsId(long userId, long goodsId) {
		//return orderDao.getMiaoshaOrderByUserIdGoodsId(userId,goodsId);
		return redisService.get(OrderKey.getMiaoshaOrderByUidGid, ""+userId+"_"+goodsId, MiaoshaOrder.class);
	}
	
	//往order_info和miaosha_order写记录
	@Transactional
	public OrderInfo createOrder(MiaoshaUser user, GoodsVo goods) {
		OrderInfo orderInfo=new OrderInfo();
		orderInfo.setCreate_date(new Date());
		orderInfo.setDelivery_addr_id(0L);
		orderInfo.setGoods_count(1);
		orderInfo.setGoods_id(goods.getId());
		orderInfo.setGoods_name(goods.getGoods_name());
		orderInfo.setGoods_price(goods.getMiaosha_price());
		orderInfo.setOrder_channel(1);
		orderInfo.setStatus(0);
		orderInfo.setUser_id(user.getId());
		orderDao.insert(orderInfo);
		
		MiaoshaOrder miaoshaOrder=new MiaoshaOrder();
		miaoshaOrder.setGoods_id(goods.getId());
		miaoshaOrder.setOrder_id(orderInfo.getId());
		miaoshaOrder.setUser_id(user.getId());
		orderDao.insertMiaoshaOrder(miaoshaOrder);
		
		//放入缓存
		redisService.set(OrderKey.getMiaoshaOrderByUidGid, ""+user.getId()+"_"+goods.getId(), miaoshaOrder);
		return orderInfo;
	}

	public OrderInfo getOrderById(long orderId) {
		return orderDao.getOrderById(orderId);
	}
}
