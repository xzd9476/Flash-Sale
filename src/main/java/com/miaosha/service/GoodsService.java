package com.miaosha.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.miaosha.dao.GoodsDao;
import com.miaosha.domain.MiaoshaGoods;
import com.miaosha.vo.GoodsVo;

@Service
public class GoodsService {
	
	@Autowired
	private GoodsDao goodsDao;
	
	public List<GoodsVo> listGoodsVo(){
		return goodsDao.listGoodsVo();
	}

	public  GoodsVo getGoodsVoByGoodsId(long goodsId) {
		// TODO Auto-generated method stub
		return goodsDao.getGoodsVoByGoodsId(goodsId);
	}

	public boolean reduceStock(GoodsVo goods) {
		MiaoshaGoods g=new MiaoshaGoods();
		g.setGoods_id(goods.getId());
		int ret=goodsDao.reduceStock(g);
		return ret > 0;
	}

}
