package com.miaosha.rabbitmq;

import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MQConfig {//定义一个队列，名字为queue
	public static final String MIAOSHA_QUEUE="miaosha.queue";
	public static final String QUEUE="queue";
//	public static final String TOPIC_QUEUE1="topic-queue1";
//	public static final String TOPIC_QUEUE2="topic-queue2";
//	public static final String TOPIC_EXCHANGE="topicEXCHANGE";

	/*
	 * 交换机模式：Direct
	 */
	@Bean
	public Queue queue(){
		return new Queue(MIAOSHA_QUEUE,true);//名字，持久化
	}
	
}
