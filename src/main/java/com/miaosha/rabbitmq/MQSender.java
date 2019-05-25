package com.miaosha.rabbitmq;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.miaosha.service.RedisService;

@Service
public class MQSender {//消息生产者
	private Logger log=LoggerFactory.getLogger(MQReceiver.class);
	
	@Autowired
	AmqpTemplate amqpTemplate;
	
	public void sendMiaoshaMessage(MiaoshaMessage mm) {
		String msg=RedisService.beanToString(mm);
		log.info("send message"+msg);
		amqpTemplate.convertAndSend(MQConfig.MIAOSHA_QUEUE,msg);
	}
	
	
//	public void send(Object message){
//		String msg=RedisService.beanToString(message);
//		log.info("send message:"+msg);
//		amqpTemplate.convertAndSend(MQConfig.QUEUE,message);//将message放入到queue中
//		
//	}
	

}
