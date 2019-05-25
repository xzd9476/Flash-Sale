package com.miaosha.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.miaosha.domain.MiaoshaOrder;
import com.miaosha.domain.MiaoshaUser;
import com.miaosha.domain.OrderInfo;
import com.miaosha.rabbitmq.MQSender;
import com.miaosha.rabbitmq.MiaoshaMessage;
import com.miaosha.redis.GoodsKey;
import com.miaosha.result.CodeMsg;
import com.miaosha.result.Result;
import com.miaosha.service.GoodsService;
import com.miaosha.service.MiaoshaOrderService;
import com.miaosha.service.MiaoshaService;
import com.miaosha.service.OrderService;
import com.miaosha.service.RedisService;
import com.miaosha.vo.GoodsVo;

@Controller
@RequestMapping("/miaosha")
public class MiaoShaController implements InitializingBean {
	@Autowired
	private GoodsService goodsService;

	@Autowired
	private OrderService orderService;

	@Autowired
	private MiaoshaOrderService miaoshaOrderService;

	@Autowired
	private RedisService redisService;

	@Autowired
	MQSender sender;
	
	private Map<Long, Boolean> localOverMap=new HashMap<Long, Boolean>();

	@Override
	// 用于系统初始化,将每一个秒杀商品的stock存在redis
	public void afterPropertiesSet() throws Exception {
		List<GoodsVo> goodsList = goodsService.listGoodsVo();
		if (goodsList == null) {
			return;
		}
		for (GoodsVo goodsVo : goodsList) {// 遍历，每一件秒杀商品
											// key为goodsid，value为库存，存到redis
			redisService.set(GoodsKey.getMiaoshaGoodsStock, "" + goodsVo.getId(), goodsVo.getStock_count());
			localOverMap.put(goodsVo.getId(), false);//标记没有结束
		}
	}

	@RequestMapping(value = "/do_miaosha", method = RequestMethod.POST)
	@ResponseBody
	public Result<Integer> miaosha(Model model, MiaoshaUser user, @RequestParam("goodsId") long goodsId) {
		model.addAttribute("user", user);
		if (user == null) {
			return Result.error(CodeMsg.SESSION_ERROR);
		}
		
		boolean over=localOverMap.get(goodsId);
		if(over){
			return Result.error(CodeMsg.MIAO_SHA_OVER);
		}
		
		// 先将redis中的库存数-1，返回剩余量.redis的--操作是原子性的
		long stock = redisService.dec(GoodsKey.getMiaoshaGoodsStock, "" + goodsId);
		if (stock < 0) {
			//内存标记，减少redis访问
			localOverMap.put(goodsId, true);
			return Result.error(CodeMsg.MIAO_SHA_OVER);
		}

		// 判断是否已经秒杀过了(),走缓存
		MiaoshaOrder order = orderService.getMiaoshaOrderByUserIdGoodsId(user.getId(), goodsId);
		
		if (order != null) {// 若miaoshaorder为！null也就是秒杀过，跳转失败页面：已经参与过
			return Result.error(CodeMsg.REPEATE_MIAOSHA);
		}
		// 可以秒杀，入队，将下订单的后续操作交给队列消费者，异步。不会阻塞后面的请求
		MiaoshaMessage mm = new MiaoshaMessage();
		mm.setUser(user);
		mm.setGoodsId(goodsId);
		sender.sendMiaoshaMessage(mm);
		return Result.success(0);// 排队中
		/*
		 * //判断商品有没有库存 GoodsVo goods=goodsService.getGoodsVoByGoodsId(goodsId);
		 * int stock=goods.getStock_count(); if(stock<=0){//库存小于0，跳转失败页面 return
		 * Result.error(CodeMsg.MIAO_SHA_OVER); }
		 * 
		 * //判断是否已经秒杀过了(),走缓存 MiaoshaOrder
		 * order=orderService.getMiaoshaOrderByUserIdGoodsId(user.getId(),
		 * goodsId); if(order!=null){//若miaoshaorder为！null也就是秒杀过，跳转失败页面：已经参与过
		 * return Result.error(CodeMsg.REPEATE_MIAOSHA); }
		 * //开始秒杀：减库存、写入订单、写入秒杀订单，返回订单信息，跳转订单详细页 OrderInfo
		 * orderInfo=miaoshaOrderService.miaosha(user,goods); return
		 * Result.success(orderInfo);
		 */
	}

	/*
	 * orderId:成功 -1:秒杀失败 0：排队中
	 */
	@RequestMapping(value = "/result", method = RequestMethod.GET)
	@ResponseBody
	public Result<Long> miaoshaResult(Model model, MiaoshaUser user, @RequestParam("goodsId") long goodsId) {
		model.addAttribute("user", user);
		if (user == null) {
			return Result.error(CodeMsg.SESSION_ERROR);
		}
		long result = miaoshaOrderService.getMiaoshaResult(user.getId(), goodsId);
		return Result.success(result);
	}
}