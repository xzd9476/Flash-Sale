package com.miaosha.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.thymeleaf.context.IWebContext;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.spring5.view.ThymeleafViewResolver;

import com.miaosha.domain.MiaoshaUser;
import com.miaosha.redis.GoodsKey;
import com.miaosha.result.Result;
import com.miaosha.service.GoodsService;
import com.miaosha.service.MiaoshaService;
import com.miaosha.service.RedisService;
import com.miaosha.vo.GoodsDetailVo;
import com.miaosha.vo.GoodsVo;

@Controller
@RequestMapping("/goods")
public class GoodsController {
	@Autowired
	private MiaoshaService userService;

	@Autowired
	private GoodsService goodsService;

	@Autowired
	private RedisService redisSrvice;

	@Autowired
	ThymeleafViewResolver thymeleafViewResolver;// 用于手动渲染的thymeleaf框架

	@Autowired
	ApplicationContext applicationContext;

	// 页面静态化
	@RequestMapping(value = "/to_list", produces = "text/html")
	@ResponseBody
	public String toList(HttpServletRequest request, HttpServletResponse response, Model model, MiaoshaUser user) {
		model.addAttribute("user", user);
		// 取缓存
		String html = redisSrvice.get(GoodsKey.getGoodsList, "", String.class);
		if (!StringUtils.isEmpty(html)) {
			return html;
		}
		// 查询商品
		List<GoodsVo> goodsList = goodsService.listGoodsVo();
		model.addAttribute("goodsList", goodsList);
		// return "goods_list";
		IWebContext ctx = new WebContext(request, response, request.getServletContext(), request.getLocale(),
				model.asMap());
		// 手动渲染

		html = thymeleafViewResolver.getTemplateEngine().process("goods_list", ctx);
		if (!StringUtils.isEmpty(html)) {
			// html是页面源码，写入到redis
			redisSrvice.set(GoodsKey.getGoodsList, "", html);
		}
		return html;
	}

	@RequestMapping(value = "/to_detail2/{goodsId}", produces = "text/html")
	@ResponseBody
	public String detail2(HttpServletRequest request, HttpServletResponse response, Model model, MiaoshaUser user,
			@PathVariable("goodsId") long goodsId) {
		model.addAttribute("user", user);

		// 取缓存
		String html = redisSrvice.get(GoodsKey.getGoodsDetail, "" + goodsId, String.class);
		if (!StringUtils.isEmpty(html)) {
			return html;
		}
		// 手动渲染
		GoodsVo goods = goodsService.getGoodsVoByGoodsId(goodsId);
		model.addAttribute("goods", goods);

		long startAt = goods.getStart_date().getTime();
		long endAt = goods.getEnd_date().getTime();
		long now = System.currentTimeMillis();

		int miaoshaStatus = 0;//
		int remainSeconds = 0;// 还有多少秒开始秒杀
		int actionSeconds = 0;
		if (now < startAt) {// 秒杀未开始，倒计时
			miaoshaStatus = 0;
			remainSeconds = (int) ((startAt - now) / 1000);
			actionSeconds = (int) ((endAt = startAt) / 1000);
		} else if (now > endAt) {// 秒杀结束
			miaoshaStatus = 2;
			remainSeconds = -1;
			actionSeconds = 0;
		} else {// 秒杀进行中
			miaoshaStatus = 1;
			remainSeconds = 0;
			actionSeconds = (int) ((endAt = now) / 1000);
		}

		model.addAttribute("miaoshaStatus", miaoshaStatus);
		model.addAttribute("remainSeconds", remainSeconds);//
		model.addAttribute("actionSeconds", actionSeconds);//

		IWebContext ctx = new WebContext(request, response, request.getServletContext(), request.getLocale(),
				model.asMap());
		// 手动渲染

		html = thymeleafViewResolver.getTemplateEngine().process("goods_detail", ctx);
		if (!StringUtils.isEmpty(html)) {
			// html是页面源码，写入到redis
			redisSrvice.set(GoodsKey.getGoodsDetail, "" + goodsId, html);
		}
		return html;

		// return "goods_detail";
	}
	
	@RequestMapping(value = "/detail/{goodsId}")//页面是html，动态数据通过接口从服务端获取
	@ResponseBody
	public Result<GoodsDetailVo> detail(HttpServletRequest request, HttpServletResponse response, Model model, MiaoshaUser user,
			@PathVariable("goodsId") long goodsId) {
		// 手动渲染
		GoodsVo goods = goodsService.getGoodsVoByGoodsId(goodsId);
		
		long startAt = goods.getStart_date().getTime();
		long endAt = goods.getEnd_date().getTime();
		long now = System.currentTimeMillis();
		
		int miaoshaStatus = 0;//
		int remainSeconds = 0;// 还有多少秒开始秒杀
		if (now < startAt) {// 秒杀未开始，倒计时
			miaoshaStatus = 0;
			remainSeconds = (int) ((startAt - now) / 1000);
		} else if (now > endAt) {// 秒杀结束
			miaoshaStatus = 2;
			remainSeconds = -1;
		} else {// 秒杀进行中
			miaoshaStatus = 1;
			remainSeconds = 0;
		}
		GoodsDetailVo vo=new GoodsDetailVo();
		vo.setGoods(goods);
		vo.setUser(user);
		vo.setRemainSeconds(remainSeconds);
		vo.setRemainSeconds(remainSeconds);
		vo.setMiaoshaStatus(miaoshaStatus);
		return Result.success(vo);
		
		// return "goods_detail";
	}

	// @RequestMapping("/to_list")
	// public String toList(Model model, MiaoshaUser user
	// // HttpServletResponse response,
	// // @CookieValue(value=MiaoshaService.COOKI_NAME_TOKEN,required=false)
	// // String cookieToken,
	// // @RequestParam(value=MiaoshaService.COOKI_NAME_TOKEN,required=false)
	// // String paramToken
	// ) {
	// // if(StringUtils.isEmpty(cookieToken) &&
	// // StringUtils.isEmpty(paramToken)){
	// // return "login";
	// // }
	// // String token=StringUtils.isEmpty(paramToken)?cookieToken:paramToken;
	// // MiaoshaUser user=userService.getByToken(response,token);
	// model.addAttribute("user", user);
	// return "goods_list";
	// }

}
