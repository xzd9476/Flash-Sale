package com.miaosha.rabbitmq;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.miaosha.domain.MiaoshaOrder;
import com.miaosha.domain.MiaoshaUser;
import com.miaosha.service.GoodsService;
import com.miaosha.service.MiaoshaOrderService;
import com.miaosha.service.MiaoshaService;
import com.miaosha.service.OrderService;
import com.miaosha.service.RedisService;
import com.miaosha.vo.GoodsVo;

@Service
public class MQReceiver {
	private Logger log = LoggerFactory.getLogger(MQReceiver.class);
	@Autowired
	private GoodsService goodsService;

	@Autowired
	private OrderService orderService;

	@Autowired
	private MiaoshaOrderService miaoshaOrderService;
	

	@RabbitListener(queues = MQConfig.MIAOSHA_QUEUE)
	public void receive(String message) {
		log.info("receive message;" + message);
		// 从队列中拿到user、goodsid
		MiaoshaMessage mm = RedisService.stringToBean(message, MiaoshaMessage.class);
		MiaoshaUser user = mm.getUser();
		long goodsId = mm.getGoodsId();
		// 开始下订单减库存
		// 判断商品有没有库存 ，从数据库中查找真实的库存，因为队列中的数据量已经很少了，所以可以直接访问数据库
		GoodsVo goods = goodsService.getGoodsVoByGoodsId(goodsId);
		int stock = goods.getStock_count();
		if (stock <= 0) {// 库存小于0，跳转失败页面 return
			return;
		}
		// 判断是否已经秒杀过了(),走缓存 MiaoshaOrder
		MiaoshaOrder order = orderService.getMiaoshaOrderByUserIdGoodsId(user.getId(), goodsId);
		if (order != null) {// 若miaoshaorder为！null也就是秒杀过，跳转失败页面：已经参与过
			return ;
		}
		//减库存 下订单 写入秒杀订单
		miaoshaOrderService.miaosha(user, goods);

	}

	// @RabbitListener(queues=MQConfig.QUEUE)//监听queue队列的消息
	// public void receive(String message){//收到queue中的string类型的消息时
	// log.info("receive message:"+message);
	// }

}
