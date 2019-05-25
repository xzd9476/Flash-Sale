/*
Navicat MySQL Data Transfer

Source Server         : localhost_3306
Source Server Version : 50520
Source Host           : localhost:3306
Source Database       : miaosha

Target Server Type    : MYSQL
Target Server Version : 50520
File Encoding         : 65001

Date: 2019-05-25 15:06:32
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for `goods`
-- ----------------------------
DROP TABLE IF EXISTS `goods`;
CREATE TABLE `goods` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '商品ID',
  `goods_name` varchar(16) DEFAULT NULL COMMENT '商品名称',
  `goods_title` varchar(64) DEFAULT NULL COMMENT '商品标题',
  `goods_img` varchar(64) DEFAULT NULL COMMENT '商品图片',
  `goods_detail` longtext COMMENT '商品详情介绍',
  `goods_price` decimal(10,2) DEFAULT '0.00' COMMENT '商品单价',
  `goods_stock` int(11) DEFAULT '0' COMMENT '商品库存，-1表示无限制',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of goods
-- ----------------------------
INSERT INTO `goods` VALUES ('1', 'iphoneX', 'Apple iPhone X (A1865) 64GB银色移动联通电信4G手机', '/img/iphonex.png', 'Apple iPhone X (A1865) 64GB银色移动联通电信4G', '8575.00', '101000');
INSERT INTO `goods` VALUES ('2', '华为Meta9', '华为Mate 94GB+32GB版月光银移动联通电信4G手机双卡双待', '/img/meta10.png', '华为Mate 9 4GB+32GB版月光银移动联通电信4G手机双卡双待', '3212.00', '-1');

-- ----------------------------
-- Table structure for `miaosha_goods`
-- ----------------------------
DROP TABLE IF EXISTS `miaosha_goods`;
CREATE TABLE `miaosha_goods` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `goods_id` bigint(20) DEFAULT NULL,
  `miaosha_price` decimal(10,2) DEFAULT '0.00',
  `stock_count` int(11) DEFAULT NULL,
  `start_date` datetime DEFAULT NULL,
  `end_date` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of miaosha_goods
-- ----------------------------
INSERT INTO `miaosha_goods` VALUES ('1', '1', '0.01', '8', '2019-05-24 18:28:00', '2019-05-25 00:00:00');
INSERT INTO `miaosha_goods` VALUES ('2', '2', '0.01', '7', '2019-05-23 00:00:00', '2019-05-26 11:17:00');

-- ----------------------------
-- Table structure for `miaosha_order`
-- ----------------------------
DROP TABLE IF EXISTS `miaosha_order`;
CREATE TABLE `miaosha_order` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `user_id` bigint(20) DEFAULT NULL,
  `order_id` bigint(20) DEFAULT NULL,
  `goods_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `u_uid_gid` (`user_id`,`goods_id`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of miaosha_order
-- ----------------------------
INSERT INTO `miaosha_order` VALUES ('6', '13787686684', '6', '2');

-- ----------------------------
-- Table structure for `miaosha_user`
-- ----------------------------
DROP TABLE IF EXISTS `miaosha_user`;
CREATE TABLE `miaosha_user` (
  `id` bigint(20) NOT NULL COMMENT '用户id，手机号码',
  `nickname` varchar(20) NOT NULL,
  `password` varchar(32) DEFAULT NULL COMMENT 'MD5（pass明文+固定salt） + salt',
  `salt` varchar(10) DEFAULT NULL,
  `head` varchar(128) DEFAULT NULL COMMENT '头像，云存储的id',
  `register_date` datetime DEFAULT NULL COMMENT '注册时间',
  `last_login_date` datetime DEFAULT NULL COMMENT '上次登录时间',
  `login_count` int(11) DEFAULT '0' COMMENT '登录次数',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of miaosha_user
-- ----------------------------
INSERT INTO `miaosha_user` VALUES ('13787686684', 'shawzhidong', 'b7797cce01b4b131b433b6acf4add449', '1a2b3c4d', null, '2019-05-21 10:44:48', '2019-05-22 10:44:54', '1');
INSERT INTO `miaosha_user` VALUES ('13787686688', '二号', 'b7797cce01b4b131b433b6acf4add449', '1a2b3c4d', null, '2019-05-21 18:32:13', '2019-05-22 18:32:18', '1');

-- ----------------------------
-- Table structure for `order_info`
-- ----------------------------
DROP TABLE IF EXISTS `order_info`;
CREATE TABLE `order_info` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `user_id` bigint(20) DEFAULT NULL,
  `goods_id` bigint(20) DEFAULT NULL,
  `delivery_addr_id` bigint(20) DEFAULT NULL COMMENT '收货地址',
  `goods_name` varchar(16) DEFAULT NULL,
  `goods_count` int(11) DEFAULT '0',
  `goods_price` decimal(10,2) DEFAULT '0.00',
  `order_channel` tinyint(4) DEFAULT '0' COMMENT '1pc,2安卓,3苹果',
  `status` tinyint(4) DEFAULT '0' COMMENT '订单状态',
  `create_date` datetime DEFAULT NULL,
  `pay_date` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of order_info
-- ----------------------------
INSERT INTO `order_info` VALUES ('2', '13787686684', '1', null, 'iphoneX', '1', '0.01', '1', '0', '2019-05-23 18:07:10', null);
INSERT INTO `order_info` VALUES ('3', '13787686684', '2', null, '华为Meta9', '1', '0.01', '1', '0', '2019-05-23 18:14:16', null);
INSERT INTO `order_info` VALUES ('4', '13787686684', '1', null, 'iphoneX', '1', '0.01', '1', '0', '2019-05-24 19:33:06', null);
INSERT INTO `order_info` VALUES ('5', '13787686684', '2', null, '华为Meta9', '1', '0.01', '1', '0', '2019-05-24 20:06:08', null);
INSERT INTO `order_info` VALUES ('6', '13787686684', '2', null, '华为Meta9', '1', '0.01', '1', '0', '2019-05-25 14:13:49', null);
