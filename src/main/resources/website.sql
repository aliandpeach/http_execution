/*
Navicat MySQL Data Transfer

Source Server         : localhost
Source Server Version : 50724
Source Host           : localhost:3306
Source Database       : website

Target Server Type    : MYSQL
Target Server Version : 50724
File Encoding         : 65001

Date: 2019-11-23 08:59:41
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
-- Records of download_host
-- ----------------------------
INSERT INTO `download_host` VALUES ('1', 'e7JIi1VkhFkEgt5RCMkhpJwAmknlvd53/Oy76hf0x2u4gYVZqkT7471LIsrGc/rg/zRupbrImHpHxLcQcahJmmC6nho9TdM4tyL2jayoLrkUAa4rR2s2ijvrpzUVthw6IS7PXdkDTRRqLQm93+xjpvuNYH8KHKS7IgcdUDCqyR/LmDPryx0S70ScFs76rx2I3Cw/Hb/qNUCeV0AhdhzMSilyCnJsgb1Q8AnPFkaGBlsIHycnB4LFv5B7XGc+CxxVo/Djq7Wcd5llAG3VFLPYtvBkVviDKDwtqgv7VSs/1aMjylc6IYcEVupPS3Xk3dwrqTsPM1oo4njnZE12fcsO1Q==');
INSERT INTO `download_host` VALUES ('2', 'D4VAitSbz5fwBd8M4oiyQ7mjzEczKGX2hSw1UNPRd1aDMc+DseD9Kl8dZUmSdD52gYbUFPAxUdBlWl218WfbXBRegnTKcLT379L1h/daewLHmDBLbi5IE1G3QvOpFpRiwymy19ABZFRKlaFKEGNeFiFHpY1lg638i+zkH1Bj00lT5D3Xo59SCfPAiuykTUBnVd76zN2k0gmp245Qit9GW7T+UX3kYubso1D50spp6ZmiwDWyKBaHf1M69LmyXCQUzXYy7nz+zRn967tOonG5V9DVWXUs+TLJ+xwqgcaSWZfSWDvNs+BWBdmTPCU9tA+RGFu4N2XUFDlqzEkeKTutbg==');
INSERT INTO `download_host` VALUES ('3', 'KRQUSU1W6QYiAz8m6IybQePLE02wBdUFSnPAhFg6cqNpPF8iNuqgLrPTFyd4e3KSMxxXbRebGBHGEXWdn4NdWLJnPvEoWaPdeyWFqpatU5eDXl9p7ed/TcxVJTkEJWobNcGQ/fFLhoR0ME+BfAuqSNNmr+cKsFuNx1sDUhLPoGkwZ1OxKWZrnPw1Xojc52UWP11M6DrlHk+00JreykdSVb0qxDSQDhrLNRZyecjV9pqMPYOwZxHf/Qmqt40dQgzSKSH2Ld8eLWbzW2aucJGUaHfpMZDRYMoizngLOE9QPlSa3wEoQbL4pSqWNEWnQyTKO2TF6JQv9xW1Q68wBYXQRw==');

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
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

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

-- ----------------------------
-- Records of download_type
-- ----------------------------
INSERT INTO `download_type` VALUES ('LATEST', 'Yo9rLZDyo1hOQfOdPDX21StGcCdRos26gdEg0E3SEmsUYc7tK/tix5k4wTk0Q89RDJmzT9y6pyXUsXCRSfEPQpOlTY6C1y9Od0utyMUV1fDM4eCrXmOKV9HGH66uQXckRdQsf/jqiq8hIl0vdJHi6uJFoYmWZgu6RiFxLG81JL5sAewzJ9WvXGY6KVXwf18K6sfq8reffAXMU9u3ErYmlJ3yOEhSfoVlKBvQ2YBO3aCGple9zBUHpavpu/5vOXZggX2287W5M5tLyTXVVBOUtSSELRfKscJx4jbdW9K2VUcknNlnf1K45zGEGEE7WP2pYy2rtSpF4fgPnZBAJp192Q==', 'Yo9rLZDyo1hOQfOdPDX21StGcCdRos26gdEg0E3SEmsUYc7tK/tix5k4wTk0Q89RDJmzT9y6pyXUsXCRSfEPQpOlTY6C1y9Od0utyMUV1fDM4eCrXmOKV9HGH66uQXckRdQsf/jqiq8hIl0vdJHi6uJFoYmWZgu6RiFxLG81JL5sAewzJ9WvXGY6KVXwf18K6sfq8reffAXMU9u3ErYmlJ3yOEhSfoVlKBvQ2YBO3aCGple9zBUHpavpu/5vOXZggX2287W5M5tLyTXVVBOUtSSELRfKscJx4jbdW9K2VUcknNlnf1K45zGEGEE7WP2pYy2rtSpF4fgPnZBAJp192Q==');
INSERT INTO `download_type` VALUES ('POP', 'RU6wow7ZyffXXZlzH3q8U/QtKAXDABSM8pgU0efomoHFhpyuyKbTM4lT8+mTu9qWYUrauc6ctXH3TNK+Uu3gnVrXmq0yvjhFY87CNrIJTnps2sFB6wKABgNnAR6CE90661rGwDoTk4fQf2aDXUvyzZghbvpk5w7NTQT4Ew1DXLC5GkB6Hd3w8XzK3FcWMCUUuDzH8TP2a7MhOpxtoFmmgMHCp7kOyITNkZNZ6KnkQnROkzVfd5LtAoVidz1sU6+UpQHTXDFDpIzedqBWamuQRuD/gXbkg+fHx7DD7E4gZhpXQ2NoIqZTF2vc8LyDlsYjkc81j06ovbjayfiC5kDrbQ==', 'RU6wow7ZyffXXZlzH3q8U/QtKAXDABSM8pgU0efomoHFhpyuyKbTM4lT8+mTu9qWYUrauc6ctXH3TNK+Uu3gnVrXmq0yvjhFY87CNrIJTnps2sFB6wKABgNnAR6CE90661rGwDoTk4fQf2aDXUvyzZghbvpk5w7NTQT4Ew1DXLC5GkB6Hd3w8XzK3FcWMCUUuDzH8TP2a7MhOpxtoFmmgMHCp7kOyITNkZNZ6KnkQnROkzVfd5LtAoVidz1sU6+UpQHTXDFDpIzedqBWamuQRuD/gXbkg+fHx7DD7E4gZhpXQ2NoIqZTF2vc8LyDlsYjkc81j06ovbjayfiC5kDrbQ==');
INSERT INTO `download_type` VALUES ('TOP', 'SlDsj47w8f3ov4cDxouBF4WTrHmNgcSMHvVkbh7zZ7cdIpDyUOYsmN6Rku6Xsf2VvZYPwRkTyzXLgRHpuYA4g/+kIaQCh9gX8HR2Xs5Cp8xhGvExEhHGWS9rqEjgqum87qIaXPEgbfgK/vJYFATlTxK8bpSu09g3I1QBVv1teiLwxElspb3/9YyBIRrGjLSTeBdB6fWbACERIVQDLy7NKCwCXX8Vl0og2p2UeqnypnKVjb/Sp2cRuspbe2ZZ+Q1G/iMIIdByJw/DgpQ8AJkmiwxWdd4TTjP72loqJpgddXZFq7dYhtiPxI8J3dKVc22eHgRXPiii2n2eeC5B6kSkjw==', 'SlDsj47w8f3ov4cDxouBF4WTrHmNgcSMHvVkbh7zZ7cdIpDyUOYsmN6Rku6Xsf2VvZYPwRkTyzXLgRHpuYA4g/+kIaQCh9gX8HR2Xs5Cp8xhGvExEhHGWS9rqEjgqum87qIaXPEgbfgK/vJYFATlTxK8bpSu09g3I1QBVv1teiLwxElspb3/9YyBIRrGjLSTeBdB6fWbACERIVQDLy7NKCwCXX8Vl0og2p2UeqnypnKVjb/Sp2cRuspbe2ZZ+Q1G/iMIIdByJw/DgpQ8AJkmiwxWdd4TTjP72loqJpgddXZFq7dYhtiPxI8J3dKVc22eHgRXPiii2n2eeC5B6kSkjw==');
