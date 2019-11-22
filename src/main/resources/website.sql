/*
Navicat MySQL Data Transfer

Source Server         : localhost
Source Server Version : 50724
Source Host           : localhost:3306
Source Database       : website

Target Server Type    : MYSQL
Target Server Version : 50724
File Encoding         : 65001

Date: 2019-11-22 19:19:59
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for download_host
-- ----------------------------
DROP TABLE IF EXISTS `download_host`;
CREATE TABLE `download_host` (
  `pkid` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `host_name` varchar(2048) DEFAULT NULL,
  PRIMARY KEY (`pkid`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for download_scan
-- ----------------------------
DROP TABLE IF EXISTS `download_scan`;
CREATE TABLE `download_scan` (
  `pkid` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `url` varchar(1024) DEFAULT NULL,
  `file_hash` varchar(1024) DEFAULT NULL,
  `file_name` varchar(1024) DEFAULT NULL,
  `is_success` int(1) DEFAULT NULL,
  `v_type` varchar(255) DEFAULT NULL,
  `enter_url` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`pkid`)
) ENGINE=InnoDB AUTO_INCREMENT=49371 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for download_type
-- ----------------------------
DROP TABLE IF EXISTS `download_type`;
CREATE TABLE `download_type` (
  `download_type` varchar(255) NOT NULL,
  `download_original_url` varchar(2048) DEFAULT NULL,
  `download_latest_url` varchar(2048) DEFAULT NULL,
  PRIMARY KEY (`download_type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
