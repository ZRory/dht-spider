/*
SQLyog Ultimate v12.5.0 (64 bit)
MySQL - 5.6.44-log : Database - dht
*********************************************************************
*/

/*!40101 SET NAMES utf8 */;

/*!40101 SET SQL_MODE=''*/;

/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;
CREATE DATABASE /*!32312 IF NOT EXISTS*/`dht` /*!40100 DEFAULT CHARACTER SET utf8mb4 */;

USE `dht`;

/*Table structure for table `file` */

CREATE TABLE `file` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '文件主键id',
  `file_name` varchar(255) NOT NULL COMMENT '文件',
  `file_length` bigint(20) NOT NULL COMMENT '文件长度',
  `metadata_id` bigint(20) NOT NULL DEFAULT '0' COMMENT '父id',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `fk_mid` (`metadata_id`),
  CONSTRAINT `fk_mid` FOREIGN KEY (`metadata_id`) REFERENCES `metadata` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=18958158 DEFAULT CHARSET=utf8mb4;

/*Table structure for table `info_hash` */

CREATE TABLE `info_hash` (
  `info_hash` char(40) NOT NULL COMMENT 'infohash',
  `source_ip` varchar(20) NOT NULL,
  `source_port` int(5) NOT NULL,
  `state` int(2) NOT NULL,
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`info_hash`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

/*Table structure for table `metadata` */

CREATE TABLE `metadata` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键id',
  `name` varchar(255) NOT NULL COMMENT '名称',
  `announce` varchar(255) DEFAULT NULL COMMENT '通告者',
  `comment` varchar(1000) DEFAULT NULL COMMENT '备注',
  `created_by` varchar(255) DEFAULT NULL COMMENT '创建者',
  `creation_time` datetime DEFAULT NULL COMMENT '编成日期',
  `info_hash` char(40) NOT NULL COMMENT 'info_hash',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `update_time` datetime NOT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_info_hash` (`info_hash`)
) ENGINE=InnoDB AUTO_INCREMENT=682163 DEFAULT CHARSET=utf8mb4;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;
