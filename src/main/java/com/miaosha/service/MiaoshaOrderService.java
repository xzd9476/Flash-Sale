package com.miaosha.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.miaosha.domain.MiaoshaOrder;
import com.miaosha.domain.MiaoshaUser;
import com.miaosha.domain.OrderInfo;
import com.miaosha.redis.MiaoshaOverKey;
import com.miaosha.vo.GoodsVo;

@Service
public class MiaoshaOrderService {
	@Autowired
	GoodsService goodsService;// 用来减少goods的库存

	@Autowired
	OrderService orderService;
	
	@Autowired
	RedisService redisService;

	@Transactional
	public OrderInfo miaosha(MiaoshaUser user, GoodsVo goods) {
		// 减少库存
		boolean success = goodsService.reduceStock(goods);
		if (success) {
			// 往order_info和miaosha_order写记录,存数据库&缓存（以加速判断是否重复秒杀）
			return orderService.createOrder(user, goods);
		}else{
			setGoodsOver(goods.getId());//标记商品已经被秒杀完了
			return null;
		}
	}


	public long getMiaoshaResult(Long userid, long goodsId) {
		MiaoshaOrder order=orderService.getMiaoshaOrderByUserIdGoodsId(userid, goodsId);
		if(order!=null){
			return order.getOrder_id();//秒杀成功
		}else{
			boolean isOver=getGoodsOver(goodsId);
			if(isOver){//若已卖完
				return -1;
			}else{//没有卖完，返回0，客户端继续轮询
				return 0;
			}
		}
	}

	private void setGoodsOver(Long goodsId) {
		redisService.set(MiaoshaOverKey.isGoodsOver, ""+goodsId, true);
	}
	
	private boolean getGoodsOver(long goodsId) {
		return redisService.exists(MiaoshaOverKey.isGoodsOver, ""+goodsId, true);
	}
}
