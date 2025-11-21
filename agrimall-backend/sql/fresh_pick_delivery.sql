/*
 Navicat Premium Dump SQL

 Source Server         : MySQL80
 Source Server Type    : MySQL
 Source Server Version : 80034 (8.0.34)
 Source Host           : localhost:3306
 Source Schema         : fresh_pick_delivery

 Target Server Type    : MySQL
 Target Server Version : 80034 (8.0.34)
 File Encoding         : 65001

 Date: 21/11/2025 02:35:33
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for bms_admin
-- ----------------------------
DROP TABLE IF EXISTS `bms_admin`;
CREATE TABLE `bms_admin`  (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '管理员ID',
  `role_id` int NOT NULL DEFAULT 0 COMMENT '管理员对应角色ID；默认0：无',
  `head_pic` varchar(256) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT 'common/default_img.jpg' COMMENT '管理员头像',
  `password` varchar(16) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL DEFAULT '123456' COMMENT '管理员密码',
  `name` varchar(16) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL COMMENT '管理员姓名',
  `sex` int NULL DEFAULT 3 COMMENT '管理员性别：1：男；2：女；3：未知',
  `address` varchar(128) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL COMMENT '管理员地址',
  `mobile` bigint NOT NULL COMMENT '管理员电话',
  `state` int NOT NULL DEFAULT 1 COMMENT '管理员状态：1：启用；2：冻结',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '管理员创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '管理员更新时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 24 CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of bms_admin
-- ----------------------------
INSERT INTO `bms_admin` VALUES (1, 1, '20251109/1605253582033.jpg', '123456', 'root', 1, '北京市朝阳区', 13800138001, 1, '2024-01-01 10:00:00', '2025-11-13 13:42:34');
INSERT INTO `bms_admin` VALUES (2, 2, '20251109/1605253582033.jpg', '123456', 'xqx', 2, '上海市浦东新区', 13800138002, 1, '2024-01-01 10:00:00', '2025-11-14 02:05:28');
INSERT INTO `bms_admin` VALUES (3, 2, '20251109/1605253582033.jpg', '123456', '王运营', 1, '广州市天河区', 13800138003, 1, '2024-01-01 10:00:00', '2025-11-13 13:42:36');
INSERT INTO `bms_admin` VALUES (4, 2, '20251109/1605253582033.jpg', '123456', '赵客服', 2, '深圳市南山区', 13800138004, 1, '2024-01-01 10:00:00', '2025-11-13 13:42:44');

-- ----------------------------
-- Table structure for bms_announcement
-- ----------------------------
DROP TABLE IF EXISTS `bms_announcement`;
CREATE TABLE `bms_announcement`  (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '公告ID',
  `content` varchar(256) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL COMMENT '公告内容',
  `admin_id` int NOT NULL COMMENT '公告发布所属管理员',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '公告创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '公告更新时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 11 CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of bms_announcement
-- ----------------------------
INSERT INTO `bms_announcement` VALUES (1, '新春佳节将至，本店将于2月10日至2月17日放假，2月18日恢复正常营业。', 1, '2024-01-15 10:00:00', '2024-01-15 10:00:00');
INSERT INTO `bms_announcement` VALUES (2, '为庆祝店铺开业三周年，全场商品8折优惠，活动时间：1月20日-1月31日。', 2, '2024-01-18 10:00:00', '2024-01-18 10:00:00');
INSERT INTO `bms_announcement` VALUES (3, '系统将于今晚23:00-24:00进行维护升级，期间可能无法正常下单，敬请谅解。', 1, '2024-01-20 10:00:00', '2024-01-20 10:00:00');
INSERT INTO `bms_announcement` VALUES (10, '大家好', 1, '2025-11-14 03:38:46', '2025-11-14 03:38:46');

-- ----------------------------
-- Table structure for bms_attachment
-- ----------------------------
DROP TABLE IF EXISTS `bms_attachment`;
CREATE TABLE `bms_attachment`  (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '附件ID',
  `sender_id` int NOT NULL COMMENT '发件人ID',
  `url` varchar(256) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL COMMENT '附件保存路径',
  `name` varchar(256) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL COMMENT '附件名称',
  `size` decimal(10, 2) NOT NULL DEFAULT 0.00 COMMENT '附件大小：单位为KB；默认为0.00',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '附件创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '附件更新时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of bms_attachment
-- ----------------------------
INSERT INTO `bms_attachment` VALUES (1, 1, 'attachment/sales_report.pdf', '2024年1月销售报告.pdf', 2560.50, '2024-01-25 10:00:00', '2024-01-25 10:00:00');
INSERT INTO `bms_attachment` VALUES (2, 2, 'attachment/product_list.xlsx', '新产品清单.xlsx', 1280.25, '2024-01-26 10:00:00', '2024-01-26 10:00:00');
INSERT INTO `bms_attachment` VALUES (3, 1, 'attachment/meeting_minutes.doc', '月度会议纪要.doc', 890.75, '2024-01-27 10:00:00', '2024-01-27 10:00:00');

-- ----------------------------
-- Table structure for bms_authority
-- ----------------------------
DROP TABLE IF EXISTS `bms_authority`;
CREATE TABLE `bms_authority`  (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '权限ID',
  `menu_id` int NOT NULL COMMENT '权限对应的菜单ID',
  `role_id` int NOT NULL COMMENT '权限对应的角色ID',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '权限创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '权限更改时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 589 CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of bms_authority
-- ----------------------------
INSERT INTO `bms_authority` VALUES (441, 1, 1, '2024-11-10 10:33:11', '2024-11-10 10:33:11');
INSERT INTO `bms_authority` VALUES (442, 2, 1, '2024-11-10 10:33:11', '2024-11-10 10:33:11');
INSERT INTO `bms_authority` VALUES (443, 3, 1, '2024-11-10 10:33:11', '2024-11-10 10:33:11');
INSERT INTO `bms_authority` VALUES (444, 4, 1, '2024-11-10 10:33:11', '2024-11-10 10:33:11');
INSERT INTO `bms_authority` VALUES (445, 10, 1, '2024-11-10 10:33:11', '2024-11-10 10:33:11');
INSERT INTO `bms_authority` VALUES (446, 14, 1, '2024-11-10 10:33:11', '2024-11-10 10:33:11');
INSERT INTO `bms_authority` VALUES (447, 11, 1, '2024-11-10 10:33:11', '2024-11-10 10:33:11');
INSERT INTO `bms_authority` VALUES (448, 15, 1, '2024-11-10 10:33:11', '2024-11-10 10:33:11');
INSERT INTO `bms_authority` VALUES (449, 16, 1, '2024-11-10 10:33:11', '2024-11-10 10:33:11');
INSERT INTO `bms_authority` VALUES (450, 17, 1, '2024-11-10 10:33:11', '2024-11-10 10:33:11');
INSERT INTO `bms_authority` VALUES (451, 18, 1, '2024-11-10 10:33:11', '2024-11-10 10:33:11');
INSERT INTO `bms_authority` VALUES (452, 25, 1, '2024-11-10 10:33:11', '2024-11-10 10:33:11');
INSERT INTO `bms_authority` VALUES (453, 26, 1, '2024-11-10 10:33:11', '2024-11-10 10:33:11');
INSERT INTO `bms_authority` VALUES (454, 81, 1, '2024-11-10 10:33:11', '2024-11-10 10:33:11');
INSERT INTO `bms_authority` VALUES (455, 82, 1, '2024-11-10 10:33:11', '2024-11-10 10:33:11');
INSERT INTO `bms_authority` VALUES (456, 83, 1, '2024-11-10 10:33:11', '2024-11-10 10:33:11');
INSERT INTO `bms_authority` VALUES (457, 88, 1, '2024-11-10 10:33:11', '2024-11-10 10:33:11');
INSERT INTO `bms_authority` VALUES (458, 89, 1, '2024-11-10 10:33:11', '2024-11-10 10:33:11');
INSERT INTO `bms_authority` VALUES (459, 90, 1, '2024-11-10 10:33:11', '2024-11-10 10:33:11');
INSERT INTO `bms_authority` VALUES (460, 95, 1, '2024-11-10 10:33:11', '2024-11-10 10:33:11');
INSERT INTO `bms_authority` VALUES (461, 96, 1, '2024-11-10 10:33:11', '2024-11-10 10:33:11');
INSERT INTO `bms_authority` VALUES (462, 91, 1, '2024-11-10 10:33:11', '2024-11-10 10:33:11');
INSERT INTO `bms_authority` VALUES (463, 97, 1, '2024-11-10 10:33:11', '2024-11-10 10:33:11');
INSERT INTO `bms_authority` VALUES (464, 98, 1, '2024-11-10 10:33:11', '2024-11-10 10:33:11');
INSERT INTO `bms_authority` VALUES (465, 108, 1, '2024-11-10 10:33:11', '2024-11-10 10:33:11');
INSERT INTO `bms_authority` VALUES (466, 109, 1, '2024-11-10 10:33:11', '2024-11-10 10:33:11');
INSERT INTO `bms_authority` VALUES (467, 110, 1, '2024-11-10 10:33:11', '2024-11-10 10:33:11');
INSERT INTO `bms_authority` VALUES (468, 111, 1, '2024-11-10 10:33:11', '2024-11-10 10:33:11');
INSERT INTO `bms_authority` VALUES (469, 112, 1, '2024-11-10 10:33:11', '2024-11-10 10:33:11');
INSERT INTO `bms_authority` VALUES (470, 113, 1, '2024-11-10 10:33:11', '2024-11-10 10:33:11');
INSERT INTO `bms_authority` VALUES (471, 115, 1, '2024-11-10 10:33:11', '2024-11-10 10:33:11');
INSERT INTO `bms_authority` VALUES (472, 116, 1, '2024-11-10 10:33:11', '2024-11-10 10:33:11');
INSERT INTO `bms_authority` VALUES (473, 117, 1, '2024-11-10 10:33:11', '2024-11-10 10:33:11');
INSERT INTO `bms_authority` VALUES (474, 114, 1, '2024-11-10 10:33:11', '2024-11-10 10:33:11');
INSERT INTO `bms_authority` VALUES (475, 118, 1, '2024-11-10 10:33:11', '2024-11-10 10:33:11');
INSERT INTO `bms_authority` VALUES (476, 119, 1, '2024-11-10 10:33:11', '2024-11-10 10:33:11');
INSERT INTO `bms_authority` VALUES (477, 120, 1, '2024-11-10 10:33:11', '2024-11-10 10:33:11');
INSERT INTO `bms_authority` VALUES (478, 121, 1, '2024-11-10 10:33:11', '2024-11-10 10:33:11');
INSERT INTO `bms_authority` VALUES (479, 122, 1, '2024-11-10 10:33:11', '2024-11-10 10:33:11');
INSERT INTO `bms_authority` VALUES (480, 125, 1, '2024-11-10 10:33:11', '2024-11-10 10:33:11');
INSERT INTO `bms_authority` VALUES (481, 126, 1, '2024-11-10 10:33:11', '2024-11-10 10:33:11');
INSERT INTO `bms_authority` VALUES (482, 123, 1, '2024-11-10 10:33:11', '2024-11-10 10:33:11');
INSERT INTO `bms_authority` VALUES (483, 124, 1, '2024-11-10 10:33:11', '2024-11-10 10:33:11');
INSERT INTO `bms_authority` VALUES (484, 127, 1, '2024-11-10 10:33:11', '2024-11-10 10:33:11');
INSERT INTO `bms_authority` VALUES (485, 128, 1, '2024-11-10 10:33:11', '2024-11-10 10:33:11');
INSERT INTO `bms_authority` VALUES (486, 129, 1, '2024-11-10 10:33:11', '2024-11-10 10:33:11');
INSERT INTO `bms_authority` VALUES (487, 130, 1, '2024-11-10 10:33:11', '2024-11-10 10:33:11');
INSERT INTO `bms_authority` VALUES (488, 131, 1, '2024-11-10 10:33:11', '2024-11-10 10:33:11');
INSERT INTO `bms_authority` VALUES (509, 88, 5, '2024-11-11 22:22:03', '2024-11-11 22:22:03');
INSERT INTO `bms_authority` VALUES (510, 89, 5, '2024-11-11 22:22:03', '2024-11-11 22:22:03');
INSERT INTO `bms_authority` VALUES (511, 90, 5, '2024-11-11 22:22:03', '2024-11-11 22:22:03');
INSERT INTO `bms_authority` VALUES (512, 95, 5, '2024-11-11 22:22:03', '2024-11-11 22:22:03');
INSERT INTO `bms_authority` VALUES (513, 96, 5, '2024-11-11 22:22:03', '2024-11-11 22:22:03');
INSERT INTO `bms_authority` VALUES (514, 91, 5, '2024-11-11 22:22:03', '2024-11-11 22:22:03');
INSERT INTO `bms_authority` VALUES (515, 97, 5, '2024-11-11 22:22:03', '2024-11-11 22:22:03');
INSERT INTO `bms_authority` VALUES (516, 98, 5, '2024-11-11 22:22:03', '2024-11-11 22:22:03');
INSERT INTO `bms_authority` VALUES (517, 112, 5, '2024-11-11 22:22:03', '2024-11-11 22:22:03');
INSERT INTO `bms_authority` VALUES (518, 114, 5, '2024-11-11 22:22:03', '2024-11-11 22:22:03');
INSERT INTO `bms_authority` VALUES (519, 120, 5, '2024-11-11 22:22:03', '2024-11-11 22:22:03');
INSERT INTO `bms_authority` VALUES (520, 119, 5, '2024-11-11 22:22:03', '2024-11-11 22:22:03');
INSERT INTO `bms_authority` VALUES (521, 118, 5, '2024-11-11 22:22:03', '2024-11-11 22:22:03');
INSERT INTO `bms_authority` VALUES (522, 113, 5, '2024-11-11 22:22:03', '2024-11-11 22:22:03');
INSERT INTO `bms_authority` VALUES (523, 117, 5, '2024-11-11 22:22:03', '2024-11-11 22:22:03');
INSERT INTO `bms_authority` VALUES (524, 116, 5, '2024-11-11 22:22:03', '2024-11-11 22:22:03');
INSERT INTO `bms_authority` VALUES (525, 115, 5, '2024-11-11 22:22:03', '2024-11-11 22:22:03');
INSERT INTO `bms_authority` VALUES (526, 127, 5, '2024-11-11 22:22:03', '2024-11-11 22:22:03');
INSERT INTO `bms_authority` VALUES (527, 128, 5, '2024-11-11 22:22:03', '2024-11-11 22:22:03');
INSERT INTO `bms_authority` VALUES (528, 131, 5, '2024-11-11 22:22:03', '2024-11-11 22:22:03');
INSERT INTO `bms_authority` VALUES (529, 130, 5, '2024-11-11 22:22:03', '2024-11-11 22:22:03');
INSERT INTO `bms_authority` VALUES (530, 129, 5, '2024-11-11 22:22:03', '2024-11-11 22:22:03');
INSERT INTO `bms_authority` VALUES (567, 127, 2, '2025-11-14 02:06:59', '2025-11-14 02:06:59');
INSERT INTO `bms_authority` VALUES (568, 128, 2, '2025-11-14 02:06:59', '2025-11-14 02:06:59');
INSERT INTO `bms_authority` VALUES (569, 131, 2, '2025-11-14 02:06:59', '2025-11-14 02:06:59');
INSERT INTO `bms_authority` VALUES (570, 130, 2, '2025-11-14 02:06:59', '2025-11-14 02:06:59');
INSERT INTO `bms_authority` VALUES (571, 129, 2, '2025-11-14 02:06:59', '2025-11-14 02:06:59');
INSERT INTO `bms_authority` VALUES (572, 112, 2, '2025-11-14 02:06:59', '2025-11-14 02:06:59');
INSERT INTO `bms_authority` VALUES (573, 114, 2, '2025-11-14 02:06:59', '2025-11-14 02:06:59');
INSERT INTO `bms_authority` VALUES (574, 120, 2, '2025-11-14 02:06:59', '2025-11-14 02:06:59');
INSERT INTO `bms_authority` VALUES (575, 119, 2, '2025-11-14 02:06:59', '2025-11-14 02:06:59');
INSERT INTO `bms_authority` VALUES (576, 118, 2, '2025-11-14 02:06:59', '2025-11-14 02:06:59');
INSERT INTO `bms_authority` VALUES (577, 113, 2, '2025-11-14 02:06:59', '2025-11-14 02:06:59');
INSERT INTO `bms_authority` VALUES (578, 117, 2, '2025-11-14 02:06:59', '2025-11-14 02:06:59');
INSERT INTO `bms_authority` VALUES (579, 116, 2, '2025-11-14 02:06:59', '2025-11-14 02:06:59');
INSERT INTO `bms_authority` VALUES (580, 115, 2, '2025-11-14 02:06:59', '2025-11-14 02:06:59');
INSERT INTO `bms_authority` VALUES (581, 88, 2, '2025-11-14 02:06:59', '2025-11-14 02:06:59');
INSERT INTO `bms_authority` VALUES (582, 91, 2, '2025-11-14 02:06:59', '2025-11-14 02:06:59');
INSERT INTO `bms_authority` VALUES (583, 98, 2, '2025-11-14 02:06:59', '2025-11-14 02:06:59');
INSERT INTO `bms_authority` VALUES (584, 97, 2, '2025-11-14 02:06:59', '2025-11-14 02:06:59');
INSERT INTO `bms_authority` VALUES (585, 90, 2, '2025-11-14 02:06:59', '2025-11-14 02:06:59');
INSERT INTO `bms_authority` VALUES (586, 96, 2, '2025-11-14 02:06:59', '2025-11-14 02:06:59');
INSERT INTO `bms_authority` VALUES (587, 95, 2, '2025-11-14 02:06:59', '2025-11-14 02:06:59');
INSERT INTO `bms_authority` VALUES (588, 89, 2, '2025-11-14 02:06:59', '2025-11-14 02:06:59');

-- ----------------------------
-- Table structure for bms_mail
-- ----------------------------
DROP TABLE IF EXISTS `bms_mail`;
CREATE TABLE `bms_mail`  (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '邮件ID',
  `sender_id` int NOT NULL COMMENT '邮件发件人ID',
  `receiver_id` int NOT NULL COMMENT '邮件收件人ID',
  `title` varchar(64) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL COMMENT '邮件主题',
  `content` longtext CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL COMMENT '邮件正文',
  `attachment_one` int NULL DEFAULT NULL COMMENT '邮件的附件1',
  `attachment_two` int NULL DEFAULT NULL COMMENT '邮件的附件2',
  `attachment_three` int NULL DEFAULT NULL COMMENT '邮件的附件3',
  `delete_state` int NOT NULL DEFAULT 1 COMMENT '邮件删除状态：1:双方均未删除  2：发送者删除；3：接收者删除',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '邮件创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '邮件更新时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 10 CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of bms_mail
-- ----------------------------
INSERT INTO `bms_mail` VALUES (1, 1, 2, '关于本月销售情况的汇报', '李管理员：\n\n附件是本月销售报告，请查收并分析相关数据。如有问题请及时反馈。\n\n张超管', 1, NULL, NULL, 1, '2024-01-25 10:00:00', '2024-01-25 10:00:00');
INSERT INTO `bms_mail` VALUES (2, 2, 1, '新产品上线计划', '张超管：\n\n新产品清单已整理完成，请审阅附件。计划下周开始陆续上线。\n\n李普管', 2, NULL, NULL, 1, '2024-01-26 10:00:00', '2024-01-26 10:00:00');
INSERT INTO `bms_mail` VALUES (3, 1, 2, '月度工作会议安排', '李管理员：\n\n附件是本月工作会议的纪要，请按照会议要求安排相关工作。\n\n张超管', 3, NULL, NULL, 1, '2024-01-27 10:00:00', '2024-01-27 10:00:00');
INSERT INTO `bms_mail` VALUES (4, 1, 3, '测试', '111', NULL, NULL, NULL, 1, '2025-11-14 01:58:47', '2025-11-14 01:58:47');
INSERT INTO `bms_mail` VALUES (5, 1, 4, '测试', '111', NULL, NULL, NULL, 1, '2025-11-14 01:58:47', '2025-11-14 01:58:47');
INSERT INTO `bms_mail` VALUES (6, 1, 2, '测试', '111', NULL, NULL, NULL, 1, '2025-11-14 01:58:47', '2025-11-14 01:58:47');
INSERT INTO `bms_mail` VALUES (7, 1, 3, '测试', '111', NULL, NULL, NULL, 1, '2025-11-14 02:00:03', '2025-11-14 02:00:03');
INSERT INTO `bms_mail` VALUES (8, 1, 2, '测试', '111', NULL, NULL, NULL, 1, '2025-11-14 02:00:03', '2025-11-14 02:00:03');
INSERT INTO `bms_mail` VALUES (9, 1, 4, '测试', '111', NULL, NULL, NULL, 1, '2025-11-14 02:00:03', '2025-11-14 02:00:03');

-- ----------------------------
-- Table structure for bms_menu
-- ----------------------------
DROP TABLE IF EXISTS `bms_menu`;
CREATE TABLE `bms_menu`  (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '菜单ID',
  `parent_id` int NOT NULL DEFAULT 0 COMMENT '上级菜单的ID：默认为0',
  `name` varchar(32) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL COMMENT '菜单名称',
  `url` varchar(256) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL COMMENT '菜单路径',
  `sort` int NOT NULL DEFAULT 0 COMMENT '菜单排序：默认为0，值越大则在同级别越优先显示',
  `icon` varchar(64) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL COMMENT '菜单图标',
  `state` int NOT NULL DEFAULT 1 COMMENT '菜单状态：1:开启；2：停用',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '菜单创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '菜单更新时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 132 CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of bms_menu
-- ----------------------------
INSERT INTO `bms_menu` VALUES (1, 0, '菜单管理', NULL, 0, '&#xe6b4;', 1, '2020-07-31 15:10:59', '2020-08-02 17:16:55');
INSERT INTO `bms_menu` VALUES (2, 1, '菜单列表', '/admin/menu/index', 0, '&#xe6a2;', 1, '2020-07-31 15:11:13', '2020-08-02 13:38:43');
INSERT INTO `bms_menu` VALUES (3, 2, '添加', 'xadmin.open(\'菜单添加\',\'/admin/menu/add\',700,500);', 2, '&#xe6b9;', 1, '2020-07-31 15:12:33', '2020-08-04 10:30:41');
INSERT INTO `bms_menu` VALUES (4, 2, '编辑', 'openEdit();', 1, '&#xe69e;', 1, '2020-07-31 20:19:47', '2020-08-02 14:00:59');
INSERT INTO `bms_menu` VALUES (10, 2, '删除', 'deleteMenu();', 0, '&#xe69d;', 1, '2020-08-02 12:53:19', '2020-08-02 16:35:33');
INSERT INTO `bms_menu` VALUES (11, 0, '管理员管理', NULL, 0, '&#xe726;', 1, '2020-08-02 13:09:32', '2020-08-02 16:35:22');
INSERT INTO `bms_menu` VALUES (14, 2, '添加按钮', 'openAddButton();', 0, '&#xe6b9;', 1, '2020-08-02 13:47:20', '2020-08-02 14:01:07');
INSERT INTO `bms_menu` VALUES (15, 11, '管理员列表', '/admin/admin/index', 0, '&#xe6a2;', 1, '2020-08-02 14:03:08', '2020-08-03 13:27:51');
INSERT INTO `bms_menu` VALUES (16, 15, '添加', 'xadmin.open(\'管理员添加\',\'/admin/admin/add\',700,550);', 0, '&#xe6b9;', 1, '2020-08-02 17:31:25', '2020-08-04 10:30:57');
INSERT INTO `bms_menu` VALUES (17, 15, '编辑', 'openEdit();', 0, '&#xe69e;', 1, '2020-08-02 17:32:45', '2020-08-02 17:32:45');
INSERT INTO `bms_menu` VALUES (18, 15, '删除', 'deleteAdmin();', 0, '&#xe69d;', 1, '2020-08-02 17:33:45', '2020-08-02 17:33:45');
INSERT INTO `bms_menu` VALUES (25, 0, '角色管理', NULL, 0, '&#xe6a0;', 1, '2020-08-03 16:56:34', '2020-08-03 16:56:34');
INSERT INTO `bms_menu` VALUES (26, 25, '角色列表', '/admin/role/index', 0, '&#xe6a2;', 1, '2020-08-03 16:57:23', '2020-08-03 16:57:23');
INSERT INTO `bms_menu` VALUES (81, 26, '添加', 'xadmin.open(\'角色添加\',\'/admin/role/add\',550,350);', 0, '&#xe6b9;', 1, '2020-08-04 10:24:55', '2020-08-04 10:38:58');
INSERT INTO `bms_menu` VALUES (82, 26, '编辑', 'openEdit();', 0, '&#xe69e;', 1, '2020-08-04 10:25:24', '2020-08-04 10:26:42');
INSERT INTO `bms_menu` VALUES (83, 26, '删除', 'deleteRole();', 0, '&#xe69d;', 1, '2020-08-04 10:25:46', '2020-08-04 10:27:35');
INSERT INTO `bms_menu` VALUES (88, 0, '邮件管理', NULL, 0, '&#xe69f;', 1, '2020-08-04 20:38:54', '2025-11-14 03:18:24');
INSERT INTO `bms_menu` VALUES (89, 88, '邮件管理', '/admin/mails', 0, '&#xe71d;', 1, '2020-08-04 20:40:22', '2025-11-14 03:27:41');
INSERT INTO `bms_menu` VALUES (108, 0, '公告管理', NULL, 0, '&#xe6bc;', 1, '2020-08-07 14:22:32', '2020-08-07 14:36:49');
INSERT INTO `bms_menu` VALUES (109, 108, '公告列表', '/admin/announcement/index', 0, '&#xe6a2;', 1, '2020-08-07 14:24:20', '2020-08-07 14:37:23');
INSERT INTO `bms_menu` VALUES (110, 109, '添加', 'xadmin.open(\'公告添加\',\'/admin/announcement/add\',550,300);', 0, '&#xe6b9;', 1, '2020-08-07 14:25:49', '2020-08-07 16:01:02');
INSERT INTO `bms_menu` VALUES (111, 109, '删除', 'deleteAnnouncement();', 0, '&#xe69d;', 1, '2020-08-07 14:27:11', '2020-08-07 14:27:11');
INSERT INTO `bms_menu` VALUES (112, 0, '商品管理', NULL, 0, '&#xe6f6;', 1, '2020-11-12 09:19:59', '2020-11-12 09:21:33');
INSERT INTO `bms_menu` VALUES (113, 112, '商品种类列表', '/admin/product_category/index', 0, '&#xe6a2;', 1, '2020-11-12 09:22:01', '2020-11-12 10:00:07');
INSERT INTO `bms_menu` VALUES (114, 112, '商品列表', '/admin/product/index', 0, '&#xe6a2;', 1, '2020-11-12 09:25:18', '2020-11-12 15:48:28');
INSERT INTO `bms_menu` VALUES (115, 113, '添加', 'xadmin.open(\'商品种类添加\',\'/admin/product_category/add\',550,200);', 0, '&#xe6b9;', 1, '2020-11-12 10:04:15', '2020-11-12 11:55:10');
INSERT INTO `bms_menu` VALUES (116, 113, '编辑', 'openEdit();', 0, '&#xe69e;', 1, '2020-11-12 10:05:22', '2020-11-12 10:05:22');
INSERT INTO `bms_menu` VALUES (117, 113, '删除', 'deleteProductCategory();', 0, '&#xe69d;', 1, '2020-11-12 10:06:39', '2020-11-12 10:06:39');
INSERT INTO `bms_menu` VALUES (118, 114, '添加', 'xadmin.open(\'商品添加\',\'/admin/product/add\',700,550);', 0, '&#xe6b9;', 1, '2020-11-12 15:49:17', '2020-11-12 17:22:20');
INSERT INTO `bms_menu` VALUES (119, 114, '编辑', 'openEdit();', 0, '&#xe69e;', 1, '2020-11-12 15:50:10', '2020-11-12 15:50:10');
INSERT INTO `bms_menu` VALUES (120, 114, '删除', 'deleteProduct();', 0, '&#xe69d;', 1, '2020-11-12 15:50:36', '2020-11-12 15:50:36');
INSERT INTO `bms_menu` VALUES (121, 0, '用户管理', NULL, 0, '&#xe6b8;', 1, '2020-11-18 20:21:08', '2020-11-18 20:21:08');
INSERT INTO `bms_menu` VALUES (122, 121, '用户列表', '/admin/user/index', 0, '&#xe6a2;', 1, '2020-11-18 20:21:50', '2020-11-18 20:22:02');
INSERT INTO `bms_menu` VALUES (123, 121, '评论列表', '/admin/user/comment', 0, '&#xe69b;', 1, '2020-11-18 20:24:23', '2020-11-18 20:24:23');
INSERT INTO `bms_menu` VALUES (124, 123, '删除', 'deleteComment();', 0, '&#xe69d;', 1, '2020-11-18 20:24:48', '2020-11-18 20:26:56');
INSERT INTO `bms_menu` VALUES (125, 122, '修改密码', 'openEdit();', 0, '&#xe69e;', 1, '2020-11-18 20:26:44', '2020-11-18 20:26:44');
INSERT INTO `bms_menu` VALUES (126, 122, '删除', 'deleteUser();', 0, '&#xe69d;', 1, '2020-11-18 20:27:49', '2020-11-18 20:27:49');
INSERT INTO `bms_menu` VALUES (127, 0, '订单管理', NULL, 0, '&#xe702;', 1, '2020-11-19 10:17:13', '2020-11-19 10:17:25');
INSERT INTO `bms_menu` VALUES (128, 127, '订单列表', '/admin/order/index', 0, '&#xe6a2;', 1, '2020-11-19 10:17:50', '2020-11-19 10:17:50');
INSERT INTO `bms_menu` VALUES (129, 128, '修改订单状态', 'openEditState();', 0, '&#xe69e;', 1, '2020-11-19 10:18:18', '2020-11-19 13:05:39');
INSERT INTO `bms_menu` VALUES (130, 128, '删除订单', 'deleteOrder();', 0, '&#xe69d;', 1, '2020-11-19 10:18:50', '2020-11-19 10:18:50');
INSERT INTO `bms_menu` VALUES (131, 128, '查看订单详情', 'viewOrder();', 0, '&#xe6e6;', 1, '2020-11-19 10:29:59', '2020-11-19 11:02:58');

-- ----------------------------
-- Table structure for bms_role
-- ----------------------------
DROP TABLE IF EXISTS `bms_role`;
CREATE TABLE `bms_role`  (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '角色ID',
  `name` varchar(32) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL COMMENT '角色名称',
  `description` varchar(128) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL COMMENT '角色描述',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '角色创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '角色更新时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 8 CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of bms_role
-- ----------------------------
INSERT INTO `bms_role` VALUES (1, '超级管理员', '系统最高权限管理员，拥有所有权限', '2024-01-01 10:00:00', '2024-01-01 10:00:00');
INSERT INTO `bms_role` VALUES (2, '普通管理员', '普通权限管理员，负责日常运营管理', '2024-01-01 10:00:00', '2024-01-01 10:00:00');

-- ----------------------------
-- Table structure for mall_address
-- ----------------------------
DROP TABLE IF EXISTS `mall_address`;
CREATE TABLE `mall_address`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '地址id',
  `receiver_name` varchar(8) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL COMMENT '收货人姓名',
  `receiver_address` varchar(64) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL COMMENT '收货人地址',
  `receiver_phone` varchar(11) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL COMMENT '收货人手机号码',
  `user_id` bigint NOT NULL COMMENT '地址对应的用户id',
  `first_selected` int NOT NULL DEFAULT 0 COMMENT '该地址是否为首先地址  0：不是；1：是',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '地址创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '地址更新时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 12 CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of mall_address
-- ----------------------------
INSERT INTO `mall_address` VALUES (1, '张三', '北京市市辖区朝阳区朝阳区建国门外大街1号', '13800138001', 1, 0, '2024-01-01 10:00:00', '2025-11-13 22:58:00');
INSERT INTO `mall_address` VALUES (2, '李四', '上海市浦东新区陆家嘴金融中心', '13800138002', 2, 1, '2024-01-01 10:00:00', '2024-01-01 10:00:00');
INSERT INTO `mall_address` VALUES (3, '王五', '广州市天河区珠江新城', '13800138003', 3, 1, '2024-01-01 10:00:00', '2024-01-01 10:00:00');
INSERT INTO `mall_address` VALUES (4, '赵六', '深圳市南山区科技园', '13800138004', 4, 1, '2024-01-01 10:00:00', '2024-01-01 10:00:00');
INSERT INTO `mall_address` VALUES (5, '钱七', '杭州市西湖区文三路', '13800138005', 5, 1, '2024-01-01 10:00:00', '2024-01-01 10:00:00');
INSERT INTO `mall_address` VALUES (7, '张某人', '广东省肇庆市怀集县蓝钟镇', '18307582475', 1, 1, '2025-11-13 22:44:58', '2025-11-13 22:45:01');


-- ----------------------------
-- Table structure for mall_collect
-- ----------------------------
DROP TABLE IF EXISTS `mall_collect`;
CREATE TABLE `mall_collect`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '收藏id',
  `user_id` bigint NOT NULL COMMENT '收藏对应的用户id',
  `product_id` bigint NOT NULL COMMENT '收藏对应的商品id',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '收藏创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '收藏更新时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 21 CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of mall_collect
-- ----------------------------
INSERT INTO `mall_collect` VALUES (2, 1, 5, '2024-01-03 10:00:00', '2024-01-03 10:00:00');
INSERT INTO `mall_collect` VALUES (3, 2, 3, '2024-01-04 10:00:00', '2024-01-04 10:00:00');
INSERT INTO `mall_collect` VALUES (4, 2, 8, '2024-01-05 10:00:00', '2024-01-05 10:00:00');
INSERT INTO `mall_collect` VALUES (5, 3, 12, '2024-01-06 10:00:00', '2024-01-06 10:00:00');
INSERT INTO `mall_collect` VALUES (14, 1, 2, '2025-11-13 16:59:42', '2025-11-13 16:59:42');
INSERT INTO `mall_collect` VALUES (15, 1, 55, '2025-11-13 17:00:40', '2025-11-13 17:00:40');
INSERT INTO `mall_collect` VALUES (16, 1, 164, '2025-11-13 23:17:52', '2025-11-13 23:17:52');
INSERT INTO `mall_collect` VALUES (17, 51, 3, '2025-11-14 07:14:04', '2025-11-14 07:14:04');
INSERT INTO `mall_collect` VALUES (18, 51, 6, '2025-11-14 07:14:08', '2025-11-14 07:14:08');
INSERT INTO `mall_collect` VALUES (19, 51, 7, '2025-11-14 07:14:11', '2025-11-14 07:14:11');
INSERT INTO `mall_collect` VALUES (20, 51, 158, '2025-11-14 07:14:17', '2025-11-14 07:14:17');

-- ----------------------------
-- Table structure for mall_comment
-- ----------------------------
DROP TABLE IF EXISTS `mall_comment`;
CREATE TABLE `mall_comment`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '评论id',
  `product_id` bigint NOT NULL COMMENT '评论对应的商品id',
  `user_id` bigint NOT NULL COMMENT '评论对应的用户id',
  `content` varchar(128) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL COMMENT '评论内容',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '评论创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '评论更新时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 16 CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of mall_comment
-- ----------------------------
INSERT INTO `mall_comment` VALUES (1, 1, 1, '苹果很新鲜，味道很甜，下次还会购买！', '2024-01-03 10:00:00', '2024-01-03 10:00:00');
INSERT INTO `mall_comment` VALUES (2, 1, 2, '包装很好，没有磕碰，很满意的一次购物', '2024-01-04 10:00:00', '2024-01-04 10:00:00');
INSERT INTO `mall_comment` VALUES (3, 5, 3, '橙子很甜，水分很足，家里人都很喜欢', '2024-01-05 10:00:00', '2024-01-05 10:00:00');
INSERT INTO `mall_comment` VALUES (4, 8, 4, '梨子很脆，价格实惠，性价比高', '2024-01-06 10:00:00', '2024-01-06 10:00:00');
INSERT INTO `mall_comment` VALUES (5, 12, 5, '香蕉很新鲜，熟度刚好，值得推荐', '2024-01-07 10:00:00', '2024-01-07 10:00:00');
INSERT INTO `mall_comment` VALUES (15, 3, 51, '这个水果好好吃', '2025-11-14 07:13:35', '2025-11-14 07:13:35');

-- ----------------------------
-- Table structure for mall_order
-- ----------------------------
DROP TABLE IF EXISTS `mall_order`;
CREATE TABLE `mall_order`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '订单id',
  `order_no` bigint NOT NULL COMMENT '订单流水号',
  `user_id` bigint NOT NULL COMMENT '订单所属用户id',
  `state` int NOT NULL DEFAULT 0 COMMENT '订单状态  0：未支付；1：已支付，待发货；2：已取消；3：已送达，待签收；4：已签收；5：已发货',
  `total_price` decimal(10, 2) NOT NULL COMMENT '订单总价',
  `address_id` bigint NOT NULL DEFAULT 0 COMMENT '订单对应的配送地址id',
  `remark` varchar(128) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL COMMENT '订单留言',
  `is_deleted` int NOT NULL DEFAULT 0 COMMENT '用户是否删除订单 0：未删除；1：已删除 ',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '订单创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '订单更新时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 50 CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of mall_order
-- ----------------------------
INSERT INTO `mall_order` VALUES (2, 202401020001, 2, 3, 125.80, 2, '下午配送', 0, '2024-01-02 10:00:00', '2024-01-06 10:00:00');
INSERT INTO `mall_order` VALUES (3, 202401030001, 3, 1, 89.60, 3, '周末配送', 0, '2024-01-03 10:00:00', '2024-01-07 10:00:00');
INSERT INTO `mall_order` VALUES (4, 202401040001, 4, 5, 156.30, 4, '需要发票', 0, '2024-01-04 10:00:00', '2024-01-08 10:00:00');
INSERT INTO `mall_order` VALUES (5, 202401050001, 5, 0, 78.90, 5, NULL, 0, '2024-01-05 10:00:00', '2024-01-05 10:00:00');
INSERT INTO `mall_order` VALUES (19, 1186455945530847232, 1, 5, 91.60, 1, '', 0, '2025-11-13 21:13:26', '2025-11-13 23:30:28');
INSERT INTO `mall_order` VALUES (20, 1186457257001627648, 1, 5, 12.80, 1, '', 0, '2025-11-13 21:18:39', '2025-11-13 22:19:25');
INSERT INTO `mall_order` VALUES (22, 1186460085644439552, 1, 5, 2.50, 1, '', 0, '2025-11-13 21:29:53', '2025-11-13 22:19:20');
INSERT INTO `mall_order` VALUES (23, 1186469636569575424, 1, 2, 45.00, 0, NULL, 0, '2025-11-13 22:07:50', '2025-11-13 22:14:28');
INSERT INTO `mall_order` VALUES (24, 1186471698007076864, 1, 3, 45.00, 1, '', 0, '2025-11-13 22:16:02', '2025-11-14 01:08:58');
INSERT INTO `mall_order` VALUES (25, 1186482535174254592, 1, 1, 68.00, 7, '', 0, '2025-11-13 22:59:05', '2025-11-13 22:59:08');
INSERT INTO `mall_order` VALUES (26, 1186487403481935872, 1, 1, 54.00, 0, NULL, 0, '2025-11-13 23:18:26', '2025-11-13 23:18:59');
INSERT INTO `mall_order` VALUES (27, 1186488565589356544, 51, 1, 1.80, 8, '', 0, '2025-11-13 23:23:03', '2025-11-13 23:23:05');
INSERT INTO `mall_order` VALUES (31, 1186498708938960896, 51, 5, 6.50, 0, NULL, 0, '2025-11-14 00:03:21', '2025-11-14 00:04:03');
INSERT INTO `mall_order` VALUES (33, 1186504781439709184, 51, 2, 84.00, 0, NULL, 0, '2025-11-14 00:27:29', '2025-11-14 00:44:44');
INSERT INTO `mall_order` VALUES (34, 1186507719608381440, 51, 2, 84.00, 0, NULL, 0, '2025-11-14 00:39:10', '2025-11-14 00:44:39');
INSERT INTO `mall_order` VALUES (35, 1186508939320373248, 51, 2, 56.00, 0, NULL, 0, '2025-11-14 00:44:01', '2025-11-14 00:44:34');
INSERT INTO `mall_order` VALUES (36, 1186509012192210944, 51, 2, 6.50, 0, NULL, 0, '2025-11-14 00:44:18', '2025-11-14 00:44:28');
INSERT INTO `mall_order` VALUES (37, 1186509256200040448, 51, 0, 25.00, 0, NULL, 0, '2025-11-14 00:45:16', '2025-11-14 00:45:16');
INSERT INTO `mall_order` VALUES (38, 1186510078501728256, 51, 2, 25.00, 0, NULL, 0, '2025-11-14 00:48:32', '2025-11-14 00:48:42');
INSERT INTO `mall_order` VALUES (39, 1186510170973548544, 51, 2, 25.00, 0, NULL, 0, '2025-11-14 00:48:54', '2025-11-14 01:08:44');
INSERT INTO `mall_order` VALUES (40, 1186510451056586752, 51, 2, 25.00, 0, NULL, 0, '2025-11-14 00:50:01', '2025-11-14 00:50:18');
INSERT INTO `mall_order` VALUES (41, 1186510580891267072, 51, 2, 32.00, 0, NULL, 0, '2025-11-14 00:50:32', '2025-11-14 01:02:44');
INSERT INTO `mall_order` VALUES (42, 1186510619831185408, 51, 1, 32.00, 10, '', 0, '2025-11-14 00:50:41', '2025-11-14 00:50:42');
INSERT INTO `mall_order` VALUES (43, 1186511901354635264, 51, 4, 4.50, 0, NULL, 0, '2025-11-14 00:55:47', '2025-11-14 01:11:31');
INSERT INTO `mall_order` VALUES (44, 1186513701394722816, 51, 2, 3.80, 0, NULL, 0, '2025-11-14 01:02:56', '2025-11-14 01:06:48');
INSERT INTO `mall_order` VALUES (45, 1186514623046889472, 51, 2, 3.80, 0, NULL, 0, '2025-11-14 01:06:36', '2025-11-14 01:06:45');
INSERT INTO `mall_order` VALUES (46, 1186533236030058496, 51, 5, 4.50, 10, '', 0, '2025-11-14 02:20:33', '2025-11-14 02:21:11');
INSERT INTO `mall_order` VALUES (48, 1188333247910916096, 52, 1, 12.80, 0, NULL, 0, '2025-11-19 01:33:10', '2025-11-19 01:33:48');

-- ----------------------------
-- Table structure for mall_order_item
-- ----------------------------
DROP TABLE IF EXISTS `mall_order_item`;
CREATE TABLE `mall_order_item`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '订单详情id',
  `order_id` bigint NOT NULL COMMENT '订单详情对应的订单id',
  `product_id` bigint NOT NULL COMMENT '订单详情对应的商品id',
  `product_name` varchar(16) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL COMMENT '商品名称',
  `product_pic` varchar(256) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL COMMENT '商品图片',
  `product_price` decimal(10, 2) NOT NULL COMMENT '商品价格',
  `quantity` int NOT NULL COMMENT '商品购买数量',
  `total_price` decimal(10, 2) NOT NULL COMMENT '商品小计',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '订单详情创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '订单详情更新时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 63 CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of mall_order_item
-- ----------------------------
INSERT INTO `mall_order_item` VALUES (2, 1, 2, '海南香蕉', 'fruit/banana.jpg', 6.50, 3, 19.50, '2024-01-01 10:00:00', '2024-01-01 10:00:00');
INSERT INTO `mall_order_item` VALUES (3, 1, 5, '赣南脐橙', 'fruit/orange.jpg', 15.80, 1, 15.80, '2024-01-01 10:00:00', '2024-01-01 10:00:00');
INSERT INTO `mall_order_item` VALUES (4, 2, 3, '新疆哈密瓜', 'fruit/hami_melon.jpg', 25.00, 2, 50.00, '2024-01-02 10:00:00', '2024-01-02 10:00:00');
INSERT INTO `mall_order_item` VALUES (5, 2, 8, '砀山酥梨', 'fruit/pear.jpg', 9.80, 4, 39.20, '2024-01-02 10:00:00', '2024-01-02 10:00:00');
INSERT INTO `mall_order_item` VALUES (30, 19, 10, '泰国龙眼', '20251112/1605253581867.jpg', 32.00, 1, 32.00, '2025-11-13 21:13:26', '2025-11-13 21:13:26');
INSERT INTO `mall_order_item` VALUES (31, 19, 151, '新疆核桃', '20251112/1605253582008.jpg', 45.00, 1, 45.00, '2025-11-13 21:13:26', '2025-11-13 21:13:26');
INSERT INTO `mall_order_item` VALUES (32, 19, 56, '大白菜', '20251112/1605253581913.jpg', 1.80, 1, 1.80, '2025-11-13 21:13:26', '2025-11-13 21:13:26');
INSERT INTO `mall_order_item` VALUES (33, 19, 1, '红富士苹果', '20251112/1605253581858.jpg', 12.80, 1, 12.80, '2025-11-13 21:13:26', '2025-11-13 21:13:26');
INSERT INTO `mall_order_item` VALUES (34, 20, 1, '红富士苹果', '20251112/1605253581858.jpg', 12.80, 1, 12.80, '2025-11-13 21:18:39', '2025-11-13 21:18:39');
INSERT INTO `mall_order_item` VALUES (35, 21, 55, '土豆', '20251112/1605253581912.jpg', 2.50, 1, 2.50, '2025-11-13 21:24:28', '2025-11-13 21:24:28');
INSERT INTO `mall_order_item` VALUES (36, 22, 55, '土豆', '20251112/1605253581912.jpg', 2.50, 1, 2.50, '2025-11-13 21:29:53', '2025-11-13 21:29:53');
INSERT INTO `mall_order_item` VALUES (37, 23, 151, '新疆核桃', '20251112/1605253582008.jpg', 45.00, 1, 45.00, '2025-11-13 22:07:50', '2025-11-13 22:07:50');
INSERT INTO `mall_order_item` VALUES (38, 24, 151, '新疆核桃', '20251112/1605253582008.jpg', 45.00, 1, 45.00, '2025-11-13 22:16:02', '2025-11-13 22:16:02');
INSERT INTO `mall_order_item` VALUES (39, 25, 101, '东北大米', '20251112/1605253581958.jpg', 68.00, 1, 68.00, '2025-11-13 22:59:05', '2025-11-13 22:59:05');
INSERT INTO `mall_order_item` VALUES (40, 26, 164, '瓜子', '20251112/1605253582021.jpg', 18.00, 3, 54.00, '2025-11-13 23:18:26', '2025-11-13 23:18:26');
INSERT INTO `mall_order_item` VALUES (41, 27, 56, '大白菜', '20251112/1605253581913.jpg', 1.80, 1, 1.80, '2025-11-13 23:23:03', '2025-11-13 23:23:03');
INSERT INTO `mall_order_item` VALUES (42, 28, 4, '巨峰葡萄', '20251112/1605253581861.jpg', 18.50, 2, 37.00, '2025-11-13 23:48:43', '2025-11-13 23:48:43');
INSERT INTO `mall_order_item` VALUES (43, 29, 55, '土豆', '20251112/1605253581912.jpg', 2.50, 1, 2.50, '2025-11-13 23:49:54', '2025-11-13 23:49:54');
INSERT INTO `mall_order_item` VALUES (44, 30, 3, '新疆哈密瓜', '20251112/1605253581860.jpg', 25.00, 1, 25.00, '2025-11-13 23:55:25', '2025-11-13 23:55:25');
INSERT INTO `mall_order_item` VALUES (45, 31, 2, '海南香蕉', '20251112/1605253581859.jpg', 6.50, 1, 6.50, '2025-11-14 00:03:21', '2025-11-14 00:03:21');
INSERT INTO `mall_order_item` VALUES (46, 32, 107, '黑豆', '20251112/1605253581964.jpg', 28.00, 1, 28.00, '2025-11-14 00:14:09', '2025-11-14 00:14:09');
INSERT INTO `mall_order_item` VALUES (47, 33, 107, '黑豆', '20251112/1605253581964.jpg', 28.00, 3, 84.00, '2025-11-14 00:27:29', '2025-11-14 00:27:29');
INSERT INTO `mall_order_item` VALUES (48, 34, 107, '黑豆', '20251112/1605253581964.jpg', 28.00, 3, 84.00, '2025-11-14 00:39:10', '2025-11-14 00:39:10');
INSERT INTO `mall_order_item` VALUES (49, 35, 107, '黑豆', '20251112/1605253581964.jpg', 28.00, 2, 56.00, '2025-11-14 00:44:01', '2025-11-14 00:44:01');
INSERT INTO `mall_order_item` VALUES (50, 36, 2, '海南香蕉', '20251112/1605253581859.jpg', 6.50, 1, 6.50, '2025-11-14 00:44:18', '2025-11-14 00:44:18');
INSERT INTO `mall_order_item` VALUES (51, 37, 3, '新疆哈密瓜', '20251112/1605253581860.jpg', 25.00, 1, 25.00, '2025-11-14 00:45:16', '2025-11-14 00:45:16');
INSERT INTO `mall_order_item` VALUES (52, 38, 3, '新疆哈密瓜', '20251112/1605253581860.jpg', 25.00, 1, 25.00, '2025-11-14 00:48:32', '2025-11-14 00:48:32');
INSERT INTO `mall_order_item` VALUES (53, 39, 3, '新疆哈密瓜', '20251112/1605253581860.jpg', 25.00, 1, 25.00, '2025-11-14 00:48:54', '2025-11-14 00:48:54');
INSERT INTO `mall_order_item` VALUES (54, 40, 3, '新疆哈密瓜', '20251112/1605253581860.jpg', 25.00, 1, 25.00, '2025-11-14 00:50:01', '2025-11-14 00:50:01');
INSERT INTO `mall_order_item` VALUES (55, 41, 10, '泰国龙眼', '20251112/1605253581867.jpg', 32.00, 1, 32.00, '2025-11-14 00:50:32', '2025-11-14 00:50:32');
INSERT INTO `mall_order_item` VALUES (56, 42, 10, '泰国龙眼', '20251112/1605253581867.jpg', 32.00, 1, 32.00, '2025-11-14 00:50:41', '2025-11-14 00:50:41');
INSERT INTO `mall_order_item` VALUES (57, 43, 53, '新鲜黄瓜', '20251112/1605253581910.jpg', 4.50, 1, 4.50, '2025-11-14 00:55:47', '2025-11-14 00:55:47');
INSERT INTO `mall_order_item` VALUES (58, 44, 54, '胡萝卜', '20251112/1605253581911.jpg', 3.80, 1, 3.80, '2025-11-14 01:02:56', '2025-11-14 01:02:56');
INSERT INTO `mall_order_item` VALUES (59, 45, 54, '胡萝卜', '20251112/1605253581911.jpg', 3.80, 1, 3.80, '2025-11-14 01:06:36', '2025-11-14 01:06:36');
INSERT INTO `mall_order_item` VALUES (60, 46, 53, '新鲜黄瓜', '20251112/1605253581910.jpg', 4.50, 1, 4.50, '2025-11-14 02:20:33', '2025-11-14 02:20:33');
INSERT INTO `mall_order_item` VALUES (61, 47, 3, '新疆哈密瓜', '20251112/1605253581860.jpg', 25.00, 1, 25.00, '2025-11-18 15:46:02', '2025-11-18 15:46:02');
INSERT INTO `mall_order_item` VALUES (62, 48, 1, '红富士苹果', '20251112/1605253581858.jpg', 12.80, 1, 12.80, '2025-11-19 01:33:10', '2025-11-19 01:33:10');

-- ----------------------------
-- Table structure for mall_product
-- ----------------------------
DROP TABLE IF EXISTS `mall_product`;
CREATE TABLE `mall_product`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '商品id',
  `product_name` varchar(16) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL COMMENT '商品的名称',
  `info` varchar(32) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL COMMENT '商品的详情',
  `product_pic` varchar(256) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL DEFAULT 'common/product_img.png' COMMENT '商品的图片',
  `price` decimal(10, 2) NOT NULL COMMENT '商品的价格',
  `stock` int NOT NULL COMMENT '商品的库存',
  `sell_num` int NOT NULL DEFAULT 0 COMMENT '商品的销售数量',
  `comment_num` int NOT NULL DEFAULT 0 COMMENT '商品的评论数量',
  `category_id` bigint NOT NULL COMMENT '商品所属的商品种类id',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '商品创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '商品更新时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 201 CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of mall_product
-- ----------------------------
INSERT INTO `mall_product` VALUES (1, '红富士苹果', '山东烟台红富士，清脆香甜', '20251112/1605253581858.jpg', 12.80, 498, 236, 45, 1, '2025-01-01 10:00:00', '2025-11-13 21:18:39');
INSERT INTO `mall_product` VALUES (2, '海南香蕉', '海南香蕉，香甜软糯', '20251112/1605253581859.jpg', 6.50, 800, 567, 89, 1, '2025-01-01 10:00:00', '2025-11-13 02:00:00');
INSERT INTO `mall_product` VALUES (3, '新疆哈密瓜', '新疆特产，香甜多汁', '20251112/1605253581860.jpg', 25.00, 300, 123, 24, 1, '2025-01-01 10:00:00', '2025-11-14 07:13:35');
INSERT INTO `mall_product` VALUES (4, '巨峰葡萄', '新鲜巨峰葡萄，酸甜可口', '20251112/1605253581861.jpg', 18.50, 400, 345, 67, 1, '2025-01-01 10:00:00', '2025-11-13 02:00:00');
INSERT INTO `mall_product` VALUES (5, '赣南脐橙', '江西赣南脐橙，汁多味甜', '20251112/1605253581862.jpg', 15.80, 600, 456, 79, 1, '2025-01-01 10:00:00', '2025-11-13 20:08:36');
INSERT INTO `mall_product` VALUES (6, '海南芒果', '海南贵妃芒，香甜浓郁', '20251112/1605253581863.jpg', 22.00, 350, 278, 56, 1, '2025-01-01 10:00:00', '2025-11-13 02:00:00');
INSERT INTO `mall_product` VALUES (7, '麒麟西瓜', '海南麒麟西瓜，皮薄多汁', '20251112/1605253581864.jpg', 8.50, 200, 189, 34, 1, '2025-01-01 10:00:00', '2025-11-13 02:00:00');
INSERT INTO `mall_product` VALUES (8, '砀山酥梨', '安徽砀山酥梨，清脆多汁', '20251112/1605253581865.jpg', 9.80, 450, 312, 45, 1, '2025-01-01 10:00:00', '2025-11-13 02:00:00');
INSERT INTO `mall_product` VALUES (9, '水蜜桃', '无锡阳山水蜜桃，香甜软糯', '20251112/1605253581866.jpg', 28.00, 250, 156, 29, 1, '2025-01-01 10:00:00', '2025-11-13 02:00:00');
INSERT INTO `mall_product` VALUES (10, '泰国龙眼', '进口泰国龙眼，肉厚核小', '20251112/1605253581867.jpg', 32.00, 178, 100, 18, 1, '2025-01-01 10:00:00', '2025-11-14 00:50:42');
INSERT INTO `mall_product` VALUES (11, '草莓', '丹东草莓，鲜红饱满', '20251112/1605253581868.jpg', 35.00, 150, 267, 42, 1, '2025-01-01 10:00:00', '2025-11-13 02:00:00');
INSERT INTO `mall_product` VALUES (12, '樱桃', '大连樱桃，甜脆多汁', '20251112/1605253581869.jpg', 68.00, 100, 89, 15, 1, '2025-01-01 10:00:00', '2025-11-13 02:00:00');
INSERT INTO `mall_product` VALUES (13, '菠萝', '海南菠萝，香甜不涩', '20251112/1605253581870.jpg', 12.50, 300, 178, 28, 1, '2025-01-01 10:00:00', '2025-11-13 02:00:00');
INSERT INTO `mall_product` VALUES (14, '榴莲', '泰国金枕榴莲，浓郁香甜', '20251112/1605253581871.jpg', 128.00, 80, 45, 9, 1, '2025-01-01 10:00:00', '2025-11-13 02:00:00');
INSERT INTO `mall_product` VALUES (15, '山竹', '泰国山竹，果肉白皙', '20251112/1605253581872.jpg', 45.00, 120, 67, 12, 1, '2025-01-01 10:00:00', '2025-11-13 02:00:00');
INSERT INTO `mall_product` VALUES (16, '火龙果', '越南红心火龙果，清甜多汁', '20251112/1605253581873.jpg', 18.00, 280, 156, 25, 1, '2025-01-01 10:00:00', '2025-11-13 02:00:00');
INSERT INTO `mall_product` VALUES (17, '猕猴桃', '陕西猕猴桃，酸甜可口', '20251112/1605253581874.jpg', 15.50, 320, 234, 38, 1, '2025-01-01 10:00:00', '2025-11-13 02:00:00');
INSERT INTO `mall_product` VALUES (18, '百香果', '广西百香果，香气浓郁', '20251112/1605253581875.jpg', 22.80, 200, 112, 19, 1, '2025-01-01 10:00:00', '2025-11-13 02:00:00');
INSERT INTO `mall_product` VALUES (19, '柠檬', '安岳柠檬，酸爽清香', '20251112/1605253581876.jpg', 8.80, 400, 189, 31, 1, '2025-01-01 10:00:00', '2025-11-13 02:00:00');
INSERT INTO `mall_product` VALUES (20, '石榴', '会理石榴，籽粒饱满', '20251112/1605253581877.jpg', 16.50, 250, 134, 22, 1, '2025-01-01 10:00:00', '2025-11-13 02:00:00');
INSERT INTO `mall_product` VALUES (21, '蓝莓', '大兴安岭蓝莓，营养丰富', '20251112/1605253581878.jpg', 42.00, 180, 98, 16, 1, '2025-01-01 10:00:00', '2025-11-13 02:00:00');
INSERT INTO `mall_product` VALUES (22, '椰子', '海南椰子，汁多肉厚', '20251112/1605253581879.jpg', 15.00, 150, 78, 13, 1, '2025-01-01 10:00:00', '2025-11-13 02:00:00');
INSERT INTO `mall_product` VALUES (23, '枇杷', '苏州枇杷，果肉细腻', '20251112/1605253581880.jpg', 28.50, 120, 56, 10, 1, '2025-01-01 10:00:00', '2025-11-13 02:00:00');
INSERT INTO `mall_product` VALUES (24, '杏子', '新疆杏子，香甜多汁', '20251112/1605253581881.jpg', 18.80, 200, 89, 14, 1, '2025-01-01 10:00:00', '2025-11-13 02:00:00');
INSERT INTO `mall_product` VALUES (25, '李子', '三华李，酸甜爽口', '20251112/1605253581882.jpg', 12.80, 280, 145, 23, 1, '2025-01-01 10:00:00', '2025-11-13 02:00:00');
INSERT INTO `mall_product` VALUES (26, '杨梅', '浙江杨梅，酸甜多汁', '20251112/1605253581883.jpg', 35.00, 100, 67, 11, 1, '2025-01-01 10:00:00', '2025-11-13 02:00:00');
INSERT INTO `mall_product` VALUES (27, '荔枝', '广东荔枝，肉厚核小', '20251112/1605253581884.jpg', 32.50, 180, 112, 18, 1, '2025-01-01 10:00:00', '2025-11-13 02:00:00');
INSERT INTO `mall_product` VALUES (28, '桂圆', '福建桂圆，清甜爽口', '20251112/1605253581885.jpg', 28.00, 220, 134, 21, 1, '2025-01-01 10:00:00', '2025-11-13 02:00:00');
INSERT INTO `mall_product` VALUES (29, '桑葚', '有机桑葚，营养丰富', '20251112/1605253581886.jpg', 25.00, 150, 89, 14, 1, '2025-01-01 10:00:00', '2025-11-13 02:00:00');
INSERT INTO `mall_product` VALUES (30, '山楂', '山东山楂，酸甜开胃', '20251112/1605253581887.jpg', 9.50, 350, 178, 27, 1, '2025-01-01 10:00:00', '2025-11-13 02:00:00');
INSERT INTO `mall_product` VALUES (31, '木瓜', '海南木瓜，香甜软糯', '20251112/1605253581888.jpg', 12.00, 200, 112, 18, 1, '2025-01-01 10:00:00', '2025-11-13 02:00:00');
INSERT INTO `mall_product` VALUES (32, '杨桃', '台湾杨桃，清甜多汁', '20251112/1605253581889.jpg', 18.50, 120, 56, 9, 1, '2025-01-01 10:00:00', '2025-11-13 02:00:00');
INSERT INTO `mall_product` VALUES (33, '莲雾', '海南莲雾，清脆爽口', '20251112/1605253581890.jpg', 32.00, 100, 45, 7, 1, '2025-01-01 10:00:00', '2025-11-13 02:00:00');
INSERT INTO `mall_product` VALUES (34, '番石榴', '台湾番石榴，营养丰富', '20251112/1605253581891.jpg', 15.80, 180, 78, 12, 1, '2025-01-01 10:00:00', '2025-11-13 02:00:00');
INSERT INTO `mall_product` VALUES (35, '黄桃', '炎陵黄桃，香甜多汁', '20251112/1605253581892.jpg', 28.50, 150, 89, 14, 1, '2025-01-01 10:00:00', '2025-11-13 02:00:00');
INSERT INTO `mall_product` VALUES (36, '油桃', '山东油桃，脆甜可口', '20251112/1605253581893.jpg', 16.80, 250, 134, 21, 1, '2025-01-01 10:00:00', '2025-11-13 02:00:00');
INSERT INTO `mall_product` VALUES (37, '冬枣', '沾化冬枣，脆甜多汁', '20251112/1605253581894.jpg', 22.00, 200, 112, 17, 1, '2025-01-01 10:00:00', '2025-11-13 02:00:00');
INSERT INTO `mall_product` VALUES (38, '柿子', '陕西柿子，软糯香甜', '20251112/1605253581895.jpg', 12.50, 180, 89, 14, 1, '2025-01-01 10:00:00', '2025-11-13 02:00:00');
INSERT INTO `mall_product` VALUES (39, '无花果', '威海无花果，软糯香甜', '20251112/1605253581896.jpg', 35.00, 120, 67, 11, 1, '2025-01-01 10:00:00', '2025-11-13 02:00:00');
INSERT INTO `mall_product` VALUES (40, '人参果', '甘肃人参果，清甜多汁', '20251112/1605253581897.jpg', 28.00, 150, 78, 12, 1, '2025-01-01 10:00:00', '2025-11-13 02:00:00');
INSERT INTO `mall_product` VALUES (41, '芭乐', '台湾芭乐，清脆爽口', '20251112/1605253581898.jpg', 18.50, 200, 89, 14, 1, '2025-01-01 10:00:00', '2025-11-13 02:00:00');
INSERT INTO `mall_product` VALUES (42, '红毛丹', '泰国红毛丹，酸甜可口', '20251112/1605253581899.jpg', 42.00, 100, 45, 7, 1, '2025-01-01 10:00:00', '2025-11-13 02:00:00');
INSERT INTO `mall_product` VALUES (43, '释迦', '台湾释迦，香甜软糯', '20251112/1605253581900.jpg', 58.00, 80, 34, 6, 1, '2025-01-01 10:00:00', '2025-11-13 02:00:00');
INSERT INTO `mall_product` VALUES (44, '牛油果', '墨西哥牛油果，营养丰富', '20251112/1605253581901.jpg', 25.00, 150, 67, 10, 1, '2025-01-01 10:00:00', '2025-11-13 02:00:00');
INSERT INTO `mall_product` VALUES (45, '西梅', '智利西梅，酸甜可口', '20251112/1605253581902.jpg', 32.50, 120, 56, 9, 1, '2025-01-01 10:00:00', '2025-11-13 02:00:00');
INSERT INTO `mall_product` VALUES (46, '车厘子', '智利车厘子，个大味甜', '20251112/1605253581903.jpg', 88.00, 60, 23, 4, 1, '2025-01-01 10:00:00', '2025-11-13 02:00:00');
INSERT INTO `mall_product` VALUES (47, '青提', '新疆青提，脆甜无籽', '20251112/1605253581904.jpg', 28.00, 180, 89, 14, 1, '2025-01-01 10:00:00', '2025-11-13 02:00:00');
INSERT INTO `mall_product` VALUES (48, '红提', '美国红提，甜脆多汁', '20251112/1605253581905.jpg', 32.00, 150, 78, 12, 1, '2025-01-01 10:00:00', '2025-11-13 02:00:00');
INSERT INTO `mall_product` VALUES (49, '黑布林', '美国黑布林，酸甜可口', '20251112/1605253581906.jpg', 18.50, 200, 89, 14, 1, '2025-01-01 10:00:00', '2025-11-13 02:00:00');
INSERT INTO `mall_product` VALUES (50, '金桔', '广西金桔，皮薄多汁', '20251112/1605253581907.jpg', 15.80, 250, 112, 18, 1, '2025-01-01 10:00:00', '2025-11-13 02:00:00');
INSERT INTO `mall_product` VALUES (51, '有机西兰花', '有机种植，营养丰富', '20251112/1605253581908.jpg', 12.50, 300, 145, 28, 2, '2025-01-01 10:00:00', '2025-11-13 02:00:00');
INSERT INTO `mall_product` VALUES (52, '本地西红柿', '自然成熟，酸甜可口', '20251112/1605253581909.jpg', 6.80, 600, 278, 45, 2, '2025-01-01 10:00:00', '2025-11-13 02:00:00');
INSERT INTO `mall_product` VALUES (53, '新鲜黄瓜', '清脆爽口，现摘现发', '20251112/1605253581910.jpg', 4.50, 799, 457, 67, 2, '2025-01-01 10:00:00', '2025-11-14 02:20:35');
INSERT INTO `mall_product` VALUES (54, '胡萝卜', '农家种植，香甜脆嫩', '20251112/1605253581911.jpg', 3.80, 700, 389, 56, 2, '2025-01-01 10:00:00', '2025-11-13 02:00:00');
INSERT INTO `mall_product` VALUES (55, '土豆', '黄心土豆，粉糯香甜', '20251112/1605253581912.jpg', 2.50, 999, 679, 89, 2, '2025-01-01 10:00:00', '2025-11-13 21:30:03');
INSERT INTO `mall_product` VALUES (56, '大白菜', '山东大白菜，鲜嫩多汁', '20251112/1605253581913.jpg', 1.80, 1198, 791, 78, 2, '2025-01-01 10:00:00', '2025-11-13 23:23:05');
INSERT INTO `mall_product` VALUES (57, '菠菜', '新鲜菠菜，营养丰富', '20251112/1605253581914.jpg', 5.50, 400, 234, 34, 2, '2025-01-01 10:00:00', '2025-11-13 02:00:00');
INSERT INTO `mall_product` VALUES (58, '青椒', '薄皮青椒，微辣爽口', '20251112/1605253581915.jpg', 7.20, 500, 312, 45, 2, '2025-01-01 10:00:00', '2025-11-13 02:00:00');
INSERT INTO `mall_product` VALUES (59, '茄子', '紫皮茄子，肉质细嫩', '20251112/1605253581916.jpg', 6.00, 450, 267, 38, 2, '2025-01-01 10:00:00', '2025-11-13 02:00:00');
INSERT INTO `mall_product` VALUES (60, '芹菜', '西芹，清脆爽口', '20251112/1605253581917.jpg', 4.80, 350, 189, 29, 2, '2025-01-01 10:00:00', '2025-11-13 02:00:00');
INSERT INTO `mall_product` VALUES (61, '韭菜', '本地韭菜，香味浓郁', '20251112/1605253581918.jpg', 5.20, 300, 156, 23, 2, '2025-01-01 10:00:00', '2025-11-13 02:00:00');
INSERT INTO `mall_product` VALUES (62, '蒜苔', '新鲜蒜苔，脆嫩可口', '20251112/1605253581919.jpg', 8.50, 250, 134, 19, 2, '2025-01-01 10:00:00', '2025-11-13 02:00:00');
INSERT INTO `mall_product` VALUES (63, '豆角', '长豆角，鲜嫩无筋', '20251112/1605253581920.jpg', 7.80, 320, 178, 26, 2, '2025-01-01 10:00:00', '2025-11-13 02:00:00');
INSERT INTO `mall_product` VALUES (64, '荷兰豆', '清脆荷兰豆，营养丰富', '20251112/1605253581921.jpg', 12.00, 200, 112, 16, 2, '2025-01-01 10:00:00', '2025-11-13 02:00:00');
INSERT INTO `mall_product` VALUES (65, '西兰苔', '新品种，口感清脆', '20251112/1605253581922.jpg', 15.50, 150, 78, 11, 2, '2025-01-01 10:00:00', '2025-11-13 02:00:00');
INSERT INTO `mall_product` VALUES (66, '芦笋', '新鲜芦笋，嫩绿可口', '20251112/1605253581923.jpg', 18.00, 120, 67, 9, 2, '2025-01-01 10:00:00', '2025-11-13 02:00:00');
INSERT INTO `mall_product` VALUES (67, '秋葵', '绿色秋葵，营养丰富', '20251112/1605253581924.jpg', 9.80, 280, 145, 21, 2, '2025-01-01 10:00:00', '2025-11-13 02:00:00');
INSERT INTO `mall_product` VALUES (68, '苦瓜', '新鲜苦瓜，清热降火', '20251112/1605253581925.jpg', 6.50, 350, 189, 28, 2, '2025-01-01 10:00:00', '2025-11-13 02:00:00');
INSERT INTO `mall_product` VALUES (69, '丝瓜', '本地丝瓜，鲜嫩多汁', '20251112/1605253581926.jpg', 5.80, 300, 156, 23, 2, '2025-01-01 10:00:00', '2025-11-13 02:00:00');
INSERT INTO `mall_product` VALUES (70, '冬瓜', '黑皮冬瓜，肉质厚实', '20251112/1605253581927.jpg', 3.20, 400, 223, 32, 2, '2025-01-01 10:00:00', '2025-11-13 02:00:00');
INSERT INTO `mall_product` VALUES (71, '南瓜', '贝贝南瓜，香甜粉糯', '20251112/1605253581928.jpg', 4.50, 350, 189, 27, 2, '2025-01-01 10:00:00', '2025-11-13 02:00:00');
INSERT INTO `mall_product` VALUES (72, '西葫芦', '嫩西葫芦，清炒佳品', '20251112/1605253581929.jpg', 5.20, 320, 167, 24, 2, '2025-01-01 10:00:00', '2025-11-13 02:00:00');
INSERT INTO `mall_product` VALUES (73, '洋葱', '紫皮洋葱，辛辣香甜', '20251112/1605253581930.jpg', 3.80, 500, 278, 38, 2, '2025-01-01 10:00:00', '2025-11-13 02:00:00');
INSERT INTO `mall_product` VALUES (74, '大蒜', '独头蒜，香味浓郁', '20251112/1605253581931.jpg', 8.50, 400, 223, 31, 2, '2025-01-01 10:00:00', '2025-11-13 02:00:00');
INSERT INTO `mall_product` VALUES (75, '生姜', '小黄姜，辛辣浓郁', '20251112/1605253581932.jpg', 12.00, 300, 156, 22, 2, '2025-01-01 10:00:00', '2025-11-13 02:00:00');
INSERT INTO `mall_product` VALUES (76, '大葱', '山东大葱，葱白鲜嫩', '20251112/1605253581933.jpg', 4.20, 450, 245, 34, 2, '2025-01-01 10:00:00', '2025-11-13 02:00:00');
INSERT INTO `mall_product` VALUES (77, '小葱', '香葱，调味佳品', '20251112/1605253581934.jpg', 3.50, 600, 334, 45, 2, '2025-01-01 10:00:00', '2025-11-13 02:00:00');
INSERT INTO `mall_product` VALUES (78, '香菜', '新鲜香菜，香气浓郁', '20251112/1605253581935.jpg', 6.80, 350, 189, 26, 2, '2025-01-01 10:00:00', '2025-11-13 02:00:00');
INSERT INTO `mall_product` VALUES (79, '生菜', '球生菜，清脆爽口', '20251112/1605253581936.jpg', 4.80, 400, 223, 31, 2, '2025-01-01 10:00:00', '2025-11-13 02:00:00');
INSERT INTO `mall_product` VALUES (80, '油麦菜', '嫩油麦菜，清炒火锅', '20251112/1605253581937.jpg', 5.20, 350, 178, 25, 2, '2025-01-01 10:00:00', '2025-11-13 02:00:00');
INSERT INTO `mall_product` VALUES (81, '空心菜', '时令空心菜，鲜嫩爽口', '20251112/1605253581938.jpg', 4.50, 300, 156, 22, 2, '2025-01-01 10:00:00', '2025-11-13 02:00:00');
INSERT INTO `mall_product` VALUES (82, '苋菜', '红苋菜，营养丰富', '20251112/1605253581939.jpg', 6.20, 250, 134, 18, 2, '2025-01-01 10:00:00', '2025-11-13 02:00:00');
INSERT INTO `mall_product` VALUES (83, '芥蓝', '广东芥蓝，清脆爽口', '20251112/1605253581940.jpg', 8.50, 200, 112, 15, 2, '2025-01-01 10:00:00', '2025-11-13 02:00:00');
INSERT INTO `mall_product` VALUES (84, '菜心', '嫩菜心，清甜可口', '20251112/1605253581941.jpg', 7.80, 280, 145, 20, 2, '2025-01-01 10:00:00', '2025-11-13 02:00:00');
INSERT INTO `mall_product` VALUES (85, '娃娃菜', '迷你白菜，鲜嫩多汁', '20251112/1605253581942.jpg', 6.50, 350, 189, 26, 2, '2025-01-01 10:00:00', '2025-11-13 02:00:00');
INSERT INTO `mall_product` VALUES (86, '紫甘蓝', '紫色甘蓝，营养丰富', '20251112/1605253581943.jpg', 7.20, 250, 134, 18, 2, '2025-01-01 10:00:00', '2025-11-13 02:00:00');
INSERT INTO `mall_product` VALUES (87, '玉米', '甜玉米，香甜软糯', '20251112/1605253581944.jpg', 4.80, 400, 223, 31, 2, '2025-01-01 10:00:00', '2025-11-13 02:00:00');
INSERT INTO `mall_product` VALUES (88, '豌豆', '新鲜豌豆，清甜可口', '20251112/1605253581945.jpg', 12.50, 200, 112, 16, 2, '2025-01-01 10:00:00', '2025-11-13 02:00:00');
INSERT INTO `mall_product` VALUES (89, '毛豆', '带壳毛豆，鲜嫩饱满', '20251112/1605253581946.jpg', 8.80, 300, 156, 22, 2, '2025-01-01 10:00:00', '2025-11-13 02:00:00');
INSERT INTO `mall_product` VALUES (90, '山药', '铁棍山药，粉糯香甜', '20251112/1605253581947.jpg', 15.00, 250, 134, 19, 2, '2025-01-01 10:00:00', '2025-11-13 02:00:00');
INSERT INTO `mall_product` VALUES (91, '莲藕', '九孔莲藕，清脆爽口', '20251112/1605253581948.jpg', 12.80, 200, 112, 16, 2, '2025-01-01 10:00:00', '2025-11-13 02:00:00');
INSERT INTO `mall_product` VALUES (92, '芋头', '荔浦芋头，粉糯香甜', '20251112/1605253581949.jpg', 9.50, 280, 145, 21, 2, '2025-01-01 10:00:00', '2025-11-13 02:00:00');
INSERT INTO `mall_product` VALUES (93, '马蹄', '新鲜马蹄，清甜爽口', '20251112/1605253581950.jpg', 11.00, 220, 118, 17, 2, '2025-01-01 10:00:00', '2025-11-13 02:00:00');
INSERT INTO `mall_product` VALUES (94, '菱角', '新鲜菱角，粉糯香甜', '20251112/1605253581951.jpg', 13.50, 150, 78, 11, 2, '2025-01-01 10:00:00', '2025-11-13 02:00:00');
INSERT INTO `mall_product` VALUES (95, '茭白', '新鲜茭白，嫩滑爽口', '20251112/1605253581952.jpg', 10.80, 180, 89, 13, 2, '2025-01-01 10:00:00', '2025-11-13 02:00:00');
INSERT INTO `mall_product` VALUES (96, '竹笋', '春笋，鲜嫩爽口', '20251112/1605253581953.jpg', 16.00, 120, 67, 9, 2, '2025-01-01 10:00:00', '2025-11-13 02:00:00');
INSERT INTO `mall_product` VALUES (97, '芦蒿', '野生芦蒿，香气独特', '20251112/1605253581954.jpg', 14.50, 100, 56, 8, 2, '2025-01-01 10:00:00', '2025-11-13 02:00:00');
INSERT INTO `mall_product` VALUES (98, '蕨菜', '山野蕨菜，鲜嫩爽口', '20251112/1605253581955.jpg', 18.00, 80, 45, 6, 2, '2025-01-01 10:00:00', '2025-11-13 02:00:00');
INSERT INTO `mall_product` VALUES (99, '马齿苋', '野生马齿苋，营养丰富', '20251112/1605253581956.jpg', 9.80, 150, 78, 11, 2, '2025-01-01 10:00:00', '2025-11-13 02:00:00');
INSERT INTO `mall_product` VALUES (100, '红薯叶', '嫩红薯叶，清炒佳品', '20251112/1605253581957.jpg', 5.50, 200, 112, 16, 2, '2025-01-01 10:00:00', '2025-11-13 02:00:00');
INSERT INTO `mall_product` VALUES (101, '东北大米', '五常大米，香糯可口', '20251112/1605253581958.jpg', 68.00, 199, 157, 23, 3, '2025-01-01 10:00:00', '2025-11-13 22:59:08');
INSERT INTO `mall_product` VALUES (102, '优质小米', '山西小米，营养丰富', '20251112/1605253581959.jpg', 25.00, 150, 98, 15, 3, '2025-01-01 10:00:00', '2025-11-13 02:00:00');
INSERT INTO `mall_product` VALUES (103, '黑米', '有机黑米，补血养颜', '20251112/1605253581960.jpg', 32.00, 180, 67, 12, 3, '2025-01-01 10:00:00', '2025-11-13 02:00:00');
INSERT INTO `mall_product` VALUES (104, '红豆', '赤小豆，颗粒饱满', '20251112/1605253581961.jpg', 18.50, 250, 145, 21, 3, '2025-01-01 10:00:00', '2025-11-13 02:00:00');
INSERT INTO `mall_product` VALUES (105, '绿豆', '优质绿豆，清凉解暑', '20251112/1605253581962.jpg', 16.80, 300, 178, 26, 3, '2025-01-01 10:00:00', '2025-11-13 02:00:00');
INSERT INTO `mall_product` VALUES (106, '黄豆', '非转基因黄豆', '20251112/1605253581963.jpg', 12.50, 400, 234, 34, 3, '2025-01-01 10:00:00', '2025-11-13 02:00:00');
INSERT INTO `mall_product` VALUES (107, '黑豆', '绿心黑豆，营养价值高', '20251112/1605253581964.jpg', 28.00, 120, 89, 14, 3, '2025-01-01 10:00:00', '2025-11-13 02:00:00');
INSERT INTO `mall_product` VALUES (108, '燕麦', '即食燕麦，健康早餐', '20251112/1605253581965.jpg', 35.00, 180, 112, 18, 3, '2025-01-01 10:00:00', '2025-11-13 02:00:00');
INSERT INTO `mall_product` VALUES (109, '玉米碴', '粗粮玉米碴，营养丰富', '20251112/1605253581966.jpg', 15.00, 220, 134, 19, 3, '2025-01-01 10:00:00', '2025-11-13 02:00:00');
INSERT INTO `mall_product` VALUES (110, '薏米', '祛湿薏米，颗粒饱满', '20251112/1605253581967.jpg', 38.00, 100, 78, 11, 3, '2025-01-01 10:00:00', '2025-11-13 02:00:00');
INSERT INTO `mall_product` VALUES (111, '荞麦', '优质荞麦，健康粗粮', '20251112/1605253581968.jpg', 22.00, 150, 89, 13, 3, '2025-01-01 10:00:00', '2025-11-13 02:00:00');
INSERT INTO `mall_product` VALUES (112, '高粱米', '红高粱，营养丰富', '20251112/1605253581969.jpg', 18.50, 180, 98, 14, 3, '2025-01-01 10:00:00', '2025-11-13 02:00:00');
INSERT INTO `mall_product` VALUES (113, '糯米', '圆糯米，软糯香甜', '20251112/1605253581970.jpg', 28.00, 200, 112, 16, 3, '2025-01-01 10:00:00', '2025-11-13 02:00:00');
INSERT INTO `mall_product` VALUES (114, '糙米', '未精制糙米，营养全面', '20251112/1605253581971.jpg', 32.00, 150, 78, 11, 3, '2025-01-01 10:00:00', '2025-11-13 02:00:00');
INSERT INTO `mall_product` VALUES (115, '紫米', '墨江紫米，补血佳品', '20251112/1605253581972.jpg', 45.00, 120, 67, 10, 3, '2025-01-01 10:00:00', '2025-11-13 02:00:00');
INSERT INTO `mall_product` VALUES (116, '红米', '江西红米，营养丰富', '20251112/1605253581973.jpg', 38.00, 130, 72, 10, 3, '2025-01-01 10:00:00', '2025-11-13 02:00:00');
INSERT INTO `mall_product` VALUES (117, '青稞', '西藏青稞，高原特产', '20251112/1605253581974.jpg', 42.00, 100, 56, 8, 3, '2025-01-01 10:00:00', '2025-11-13 02:00:00');
INSERT INTO `mall_product` VALUES (118, '藜麦', '三色藜麦，营养全面', '20251112/1605253581975.jpg', 68.00, 80, 45, 7, 3, '2025-01-01 10:00:00', '2025-11-13 02:00:00');
INSERT INTO `mall_product` VALUES (119, '芝麻', '黑芝麻，乌发养颜', '20251112/1605253581976.jpg', 28.00, 200, 112, 16, 3, '2025-01-01 10:00:00', '2025-11-13 02:00:00');
INSERT INTO `mall_product` VALUES (120, '白芝麻', '优质白芝麻，香气浓郁', '20251112/1605253581977.jpg', 22.00, 220, 123, 18, 3, '2025-01-01 10:00:00', '2025-11-13 02:00:00');
INSERT INTO `mall_product` VALUES (121, '花生', '山东大花生，颗粒饱满', '20251112/1605253581978.jpg', 15.00, 300, 167, 24, 3, '2025-01-01 10:00:00', '2025-11-13 02:00:00');
INSERT INTO `mall_product` VALUES (122, '蚕豆', '新鲜蚕豆，软糯香甜', '20251112/1605253581979.jpg', 12.80, 250, 134, 19, 3, '2025-01-01 10:00:00', '2025-11-13 02:00:00');
INSERT INTO `mall_product` VALUES (123, '芸豆', '白芸豆，颗粒饱满', '20251112/1605253581980.jpg', 18.00, 180, 98, 14, 3, '2025-01-01 10:00:00', '2025-11-13 02:00:00');
INSERT INTO `mall_product` VALUES (124, '扁豆', '红扁豆，营养丰富', '20251112/1605253581981.jpg', 16.50, 200, 112, 16, 3, '2025-01-01 10:00:00', '2025-11-13 02:00:00');
INSERT INTO `mall_product` VALUES (125, '眉豆', '优质眉豆，炖汤佳品', '20251112/1605253581982.jpg', 14.80, 220, 118, 17, 3, '2025-01-01 10:00:00', '2025-11-13 02:00:00');
INSERT INTO `mall_product` VALUES (126, '鹰嘴豆', '新疆鹰嘴豆，营养丰富', '20251112/1605253581983.jpg', 32.00, 150, 78, 11, 3, '2025-01-01 10:00:00', '2025-11-13 02:00:00');
INSERT INTO `mall_product` VALUES (127, '莲子', '湘莲，去芯莲子', '20251112/1605253581984.jpg', 45.00, 120, 67, 10, 3, '2025-01-01 10:00:00', '2025-11-13 02:00:00');
INSERT INTO `mall_product` VALUES (128, '芡实', '新鲜芡实，健脾养胃', '20251112/1605253581985.jpg', 38.00, 100, 56, 8, 3, '2025-01-01 10:00:00', '2025-11-13 02:00:00');
INSERT INTO `mall_product` VALUES (129, '百合', '兰州百合，清甜软糯', '20251112/1605253581986.jpg', 52.00, 80, 45, 7, 3, '2025-01-01 10:00:00', '2025-11-13 02:00:00');
INSERT INTO `mall_product` VALUES (130, '银耳', '古田银耳，胶质丰富', '20251112/1605253581987.jpg', 28.00, 150, 78, 11, 3, '2025-01-01 10:00:00', '2025-11-13 02:00:00');
INSERT INTO `mall_product` VALUES (131, '黑木耳', '东北黑木耳，肉厚无根', '20251112/1605253581988.jpg', 35.00, 120, 67, 10, 3, '2025-01-01 10:00:00', '2025-11-13 02:00:00');
INSERT INTO `mall_product` VALUES (132, '香菇', '干香菇，香气浓郁', '20251112/1605253581989.jpg', 42.00, 100, 56, 8, 3, '2025-01-01 10:00:00', '2025-11-13 02:00:00');
INSERT INTO `mall_product` VALUES (133, '茶树菇', '干茶树菇，炖汤佳品', '20251112/1605253581990.jpg', 38.00, 110, 61, 9, 3, '2025-01-01 10:00:00', '2025-11-13 02:00:00');
INSERT INTO `mall_product` VALUES (134, '竹荪', '优质竹荪，营养丰富', '20251112/1605253581991.jpg', 68.00, 60, 34, 5, 3, '2025-01-01 10:00:00', '2025-11-13 02:00:00');
INSERT INTO `mall_product` VALUES (135, '猴头菇', '东北猴头菇，养胃佳品', '20251112/1605253581992.jpg', 58.00, 70, 39, 6, 3, '2025-01-01 10:00:00', '2025-11-13 02:00:00');
INSERT INTO `mall_product` VALUES (136, '紫菜', '头水紫菜，嫩滑爽口', '20251112/1605253581993.jpg', 18.00, 180, 98, 14, 3, '2025-01-01 10:00:00', '2025-11-13 02:00:00');
INSERT INTO `mall_product` VALUES (137, '海带', '优质海带，厚实有肉', '20251112/1605253581994.jpg', 12.50, 220, 118, 17, 3, '2025-01-01 10:00:00', '2025-11-13 02:00:00');
INSERT INTO `mall_product` VALUES (138, '裙带菜', '新鲜裙带菜，嫩滑爽口', '20251112/1605253581995.jpg', 15.80, 200, 112, 16, 3, '2025-01-01 10:00:00', '2025-11-13 02:00:00');
INSERT INTO `mall_product` VALUES (139, '石花菜', '野生石花菜，凉拌佳品', '20251112/1605253581996.jpg', 22.00, 150, 78, 11, 3, '2025-01-01 10:00:00', '2025-11-13 02:00:00');
INSERT INTO `mall_product` VALUES (140, '小麦', '优质小麦，颗粒饱满', '20251112/1605253581997.jpg', 8.50, 300, 167, 24, 3, '2025-01-01 10:00:00', '2025-11-13 02:00:00');
INSERT INTO `mall_product` VALUES (141, '大麦', '珍珠大麦，营养丰富', '20251112/1605253581998.jpg', 12.00, 250, 134, 19, 3, '2025-01-01 10:00:00', '2025-11-13 02:00:00');
INSERT INTO `mall_product` VALUES (142, '燕麦米', '整粒燕麦，健康粗粮', '20251112/1605253581999.jpg', 28.00, 180, 98, 14, 3, '2025-01-01 10:00:00', '2025-11-13 02:00:00');
INSERT INTO `mall_product` VALUES (143, '荞麦米', '去壳荞麦，营养易煮', '20251112/1605253582000.jpg', 32.00, 150, 78, 11, 3, '2025-01-01 10:00:00', '2025-11-13 02:00:00');
INSERT INTO `mall_product` VALUES (144, '黄小米', '沁州黄小米，米油丰富', '20251112/1605253582001.jpg', 35.00, 120, 67, 10, 3, '2025-01-01 10:00:00', '2025-11-13 02:00:00');
INSERT INTO `mall_product` VALUES (145, '血糯米', '云南血糯米，补血佳品', '20251112/1605253582002.jpg', 42.00, 100, 56, 8, 3, '2025-01-01 10:00:00', '2025-11-13 02:00:00');
INSERT INTO `mall_product` VALUES (146, '香米', '泰国香米，香气浓郁', '20251112/1605253582003.jpg', 58.00, 150, 78, 11, 3, '2025-01-01 10:00:00', '2025-11-13 02:00:00');
INSERT INTO `mall_product` VALUES (147, '珍珠米', '东北珍珠米，颗粒饱满', '20251112/1605253582004.jpg', 48.00, 180, 98, 14, 3, '2025-01-01 10:00:00', '2025-11-13 02:00:00');
INSERT INTO `mall_product` VALUES (148, '长粒香米', '吉林长粒香，米粒细长', '20251112/1605253582005.jpg', 52.00, 160, 84, 12, 3, '2025-01-01 10:00:00', '2025-11-13 02:00:00');
INSERT INTO `mall_product` VALUES (149, '有机大米', '有机认证，健康安全', '20251112/1605253582006.jpg', 78.00, 100, 56, 8, 3, '2025-01-01 10:00:00', '2025-11-13 02:00:00');
INSERT INTO `mall_product` VALUES (150, '胚芽米', '保留胚芽，营养丰富', '20251112/1605253582007.jpg', 65.00, 120, 67, 10, 3, '2025-01-01 10:00:00', '2025-11-13 02:00:00');
INSERT INTO `mall_product` VALUES (151, '新疆核桃', '纸皮核桃，易剥开', '20251112/1605253582008.jpg', 45.00, 148, 114, 18, 4, '2025-01-01 10:00:00', '2025-11-13 22:16:48');
INSERT INTO `mall_product` VALUES (152, '开心果', '美国开心果，颗粒饱满', '20251112/1605253582009.jpg', 68.00, 120, 89, 14, 4, '2025-01-01 10:00:00', '2025-11-13 02:00:00');
INSERT INTO `mall_product` VALUES (153, '巴旦木', '新疆巴旦木，香脆可口', '20251112/1605253582010.jpg', 52.00, 180, 134, 21, 4, '2025-01-01 10:00:00', '2025-11-13 02:00:00');
INSERT INTO `mall_product` VALUES (154, '夏威夷果', '澳洲进口，奶香味浓', '20251112/1605253582011.jpg', 78.00, 100, 67, 11, 4, '2025-01-01 10:00:00', '2025-11-13 02:00:00');
INSERT INTO `mall_product` VALUES (155, '腰果', '越南腰果，香酥可口', '20251112/1605253582012.jpg', 58.00, 160, 98, 15, 4, '2025-01-01 10:00:00', '2025-11-13 02:00:00');
INSERT INTO `mall_product` VALUES (156, '葡萄干', '新疆葡萄干，甜而不腻', '20251112/1605253582013.jpg', 28.00, 300, 245, 36, 4, '2025-01-01 10:00:00', '2025-11-13 02:00:00');
INSERT INTO `mall_product` VALUES (157, '红枣', '新疆和田枣，肉厚核小', '20251112/1605253582014.jpg', 35.00, 250, 189, 28, 4, '2025-01-01 10:00:00', '2025-11-13 02:00:00');
INSERT INTO `mall_product` VALUES (158, '枸杞', '宁夏枸杞，滋补养生', '20251112/1605253582015.jpg', 42.00, 180, 156, 23, 4, '2025-01-01 10:00:00', '2025-11-13 02:00:00');
INSERT INTO `mall_product` VALUES (159, '桂圆干', '福建桂圆，肉质饱满', '20251112/1605253582016.jpg', 38.00, 200, 134, 19, 4, '2025-01-01 10:00:00', '2025-11-13 02:00:00');
INSERT INTO `mall_product` VALUES (160, '无花果干', '土耳其无花果，软糯香甜', '20251112/1605253582017.jpg', 48.00, 150, 89, 13, 4, '2025-01-01 10:00:00', '2025-11-13 02:00:00');
INSERT INTO `mall_product` VALUES (161, '碧根果', '美国碧根果，香酥可口', '20251112/1605253582018.jpg', 65.00, 120, 78, 12, 4, '2025-01-01 10:00:00', '2025-11-13 02:00:00');
INSERT INTO `mall_product` VALUES (162, '松子', '东北松子，香气浓郁', '20251112/1605253582019.jpg', 88.00, 80, 45, 7, 4, '2025-01-01 10:00:00', '2025-11-13 02:00:00');
INSERT INTO `mall_product` VALUES (163, '榛子', '东北榛子，果仁饱满', '20251112/1605253582020.jpg', 55.00, 140, 89, 13, 4, '2025-01-01 10:00:00', '2025-11-13 02:00:00');
INSERT INTO `mall_product` VALUES (164, '瓜子', '内蒙古瓜子，颗粒饱满', '20251112/1605253582021.jpg', 18.00, 400, 278, 38, 4, '2025-01-01 10:00:00', '2025-11-13 02:00:00');
INSERT INTO `mall_product` VALUES (165, '南瓜子', '天然南瓜子，营养丰富', '20251112/1605253582022.jpg', 22.00, 300, 167, 24, 4, '2025-01-01 10:00:00', '2025-11-13 02:00:00');
INSERT INTO `mall_product` VALUES (166, '西瓜子', '大板西瓜子，香脆可口', '20251112/1605253582023.jpg', 25.00, 250, 134, 19, 4, '2025-01-01 10:00:00', '2025-11-13 02:00:00');
INSERT INTO `mall_product` VALUES (167, '杏仁', '美国大杏仁，香酥可口', '20251112/1605253582024.jpg', 48.00, 180, 112, 16, 4, '2025-01-01 10:00:00', '2025-11-13 02:00:00');
INSERT INTO `mall_product` VALUES (168, '鲍鱼果', '巴西鲍鱼果，营养丰富', '20251112/1605253582025.jpg', 72.00, 100, 56, 8, 4, '2025-01-01 10:00:00', '2025-11-13 02:00:00');
INSERT INTO `mall_product` VALUES (169, '香榧子', '诸暨香榧，香气独特', '20251112/1605253582026.jpg', 128.00, 60, 34, 5, 4, '2025-01-01 10:00:00', '2025-11-13 02:00:00');
INSERT INTO `mall_product` VALUES (170, '白果', '优质白果，炖汤佳品', '20251112/1605253582027.jpg', 35.00, 150, 78, 11, 4, '2025-01-01 10:00:00', '2025-11-13 02:00:00');
INSERT INTO `mall_product` VALUES (171, '板栗', '迁西板栗，香甜粉糯', '20251112/1605253582028.jpg', 28.00, 200, 112, 16, 4, '2025-01-01 10:00:00', '2025-11-13 02:00:00');
INSERT INTO `mall_product` VALUES (172, '莲子', '去芯莲子，清心安神', '20251112/1605253582029.jpg', 42.00, 180, 98, 14, 4, '2025-01-01 10:00:00', '2025-11-13 02:00:00');
INSERT INTO `mall_product` VALUES (173, '百合干', '兰州百合干，清甜软糯', '20251112/1605253582030.jpg', 58.00, 120, 67, 10, 4, '2025-01-01 10:00:00', '2025-11-13 02:00:00');
INSERT INTO `mall_product` VALUES (174, '银耳', '古田银耳，胶质丰富', '20251112/1605253582031.jpg', 32.00, 150, 78, 11, 4, '2025-01-01 10:00:00', '2025-11-13 02:00:00');
INSERT INTO `mall_product` VALUES (175, '黑木耳', '东北黑木耳，肉厚无根', '20251112/1605253582032.jpg', 38.00, 130, 72, 10, 4, '2025-01-01 10:00:00', '2025-11-13 02:00:00');
INSERT INTO `mall_product` VALUES (176, '香菇', '干香菇，香气浓郁', '20251112/1605253582033.jpg', 45.00, 110, 61, 9, 4, '2025-01-01 10:00:00', '2025-11-13 02:00:00');
INSERT INTO `mall_product` VALUES (177, '茶树菇', '干茶树菇，炖汤佳品', '20251112/1605253582034.jpg', 42.00, 120, 67, 10, 4, '2025-01-01 10:00:00', '2025-11-13 02:00:00');
INSERT INTO `mall_product` VALUES (178, '竹荪', '优质竹荪，营养丰富', '20251112/1605253582035.jpg', 75.00, 70, 39, 6, 4, '2025-01-01 10:00:00', '2025-11-13 02:00:00');
INSERT INTO `mall_product` VALUES (179, '猴头菇', '东北猴头菇，养胃佳品', '20251112/1605253582036.jpg', 62.00, 80, 45, 7, 4, '2025-01-01 10:00:00', '2025-11-13 02:00:00');
INSERT INTO `mall_product` VALUES (180, '紫菜', '头水紫菜，嫩滑爽口', '20251112/1605253582037.jpg', 22.00, 200, 112, 16, 4, '2025-01-01 10:00:00', '2025-11-13 02:00:00');
INSERT INTO `mall_product` VALUES (181, '海带', '优质海带，厚实有肉', '20251112/1605253582038.jpg', 15.00, 250, 134, 19, 4, '2025-01-01 10:00:00', '2025-11-13 02:00:00');
INSERT INTO `mall_product` VALUES (182, '裙带菜', '新鲜裙带菜，嫩滑爽口', '20251112/1605253582039.jpg', 18.00, 220, 118, 17, 4, '2025-01-01 10:00:00', '2025-11-13 02:00:00');
INSERT INTO `mall_product` VALUES (183, '石花菜', '野生石花菜，凉拌佳品', '20251112/1605253582040.jpg', 25.00, 160, 84, 12, 4, '2025-01-01 10:00:00', '2025-11-13 02:00:00');
INSERT INTO `mall_product` VALUES (184, '黄花菜', '优质黄花菜，炖汤佳品', '20251112/1605253582041.jpg', 28.00, 180, 98, 14, 4, '2025-01-01 10:00:00', '2025-11-13 02:00:00');
INSERT INTO `mall_product` VALUES (185, '笋干', '天目山笋干，嫩滑爽口', '20251112/1605253582042.jpg', 35.00, 150, 78, 11, 4, '2025-01-01 10:00:00', '2025-11-13 02:00:00');
INSERT INTO `mall_product` VALUES (186, '梅干菜', '绍兴梅干菜，香气浓郁', '20251112/1605253582043.jpg', 22.00, 200, 112, 16, 4, '2025-01-01 10:00:00', '2025-11-13 02:00:00');
INSERT INTO `mall_product` VALUES (187, '萝卜干', '萧山萝卜干，脆嫩可口', '20251112/1605253582044.jpg', 15.00, 250, 134, 19, 4, '2025-01-01 10:00:00', '2025-11-13 02:00:00');
INSERT INTO `mall_product` VALUES (188, '豆角干', '农家豆角干，炖肉佳品', '20251112/1605253582045.jpg', 18.00, 220, 118, 17, 4, '2025-01-01 10:00:00', '2025-11-13 02:00:00');
INSERT INTO `mall_product` VALUES (189, '茄子干', '江西茄子干，软糯香甜', '20251112/1605253582046.jpg', 20.00, 180, 98, 14, 4, '2025-01-01 10:00:00', '2025-11-13 02:00:00');
INSERT INTO `mall_product` VALUES (190, '南瓜干', '农家南瓜干，香甜软糯', '20251112/1605253582047.jpg', 16.00, 200, 112, 16, 4, '2025-01-01 10:00:00', '2025-11-13 02:00:00');
INSERT INTO `mall_product` VALUES (191, '红薯干', '天然红薯干，软糯香甜', '20251112/1605253582048.jpg', 22.00, 250, 134, 19, 4, '2025-01-01 10:00:00', '2025-11-13 02:00:00');
INSERT INTO `mall_product` VALUES (192, '芋头干', '荔浦芋头干，粉糯香甜', '20251112/1605253582049.jpg', 28.00, 150, 78, 11, 4, '2025-01-01 10:00:00', '2025-11-13 02:00:00');
INSERT INTO `mall_product` VALUES (193, '山楂干', '山东山楂干，酸甜开胃', '20251112/1605253582050.jpg', 18.00, 200, 112, 16, 4, '2025-01-01 10:00:00', '2025-11-13 02:00:00');
INSERT INTO `mall_product` VALUES (194, '苹果干', '烟台苹果干，香甜可口', '20251112/1605253582051.jpg', 25.00, 180, 98, 14, 4, '2025-01-01 10:00:00', '2025-11-13 02:00:00');
INSERT INTO `mall_product` VALUES (195, '梨干', '砀山梨干，清甜爽口', '20251112/1605253582052.jpg', 22.00, 160, 84, 12, 4, '2025-01-01 10:00:00', '2025-11-13 02:00:00');
INSERT INTO `mall_product` VALUES (196, '桃干', '阳山桃干，软糯香甜', '20251112/1605253582053.jpg', 28.00, 140, 78, 11, 4, '2025-01-01 10:00:00', '2025-11-13 02:00:00');
INSERT INTO `mall_product` VALUES (197, '杏干', '新疆杏干，酸甜可口', '20251112/1605253582054.jpg', 32.00, 120, 67, 10, 4, '2025-01-01 10:00:00', '2025-11-13 02:00:00');
INSERT INTO `mall_product` VALUES (198, '李子干', '三华李干，酸甜爽口', '20251112/1605253582055.jpg', 26.00, 150, 78, 11, 4, '2025-01-01 10:00:00', '2025-11-13 02:00:00');
INSERT INTO `mall_product` VALUES (199, '芒果干', '菲律宾芒果干，香甜浓郁', '20251112/1605253582056.jpg', 38.00, 130, 72, 10, 4, '2025-01-01 10:00:00', '2025-11-13 02:00:00');
INSERT INTO `mall_product` VALUES (200, '菠萝干', '海南菠萝干，香甜可口', '20251112/1605253582057.jpg', 28.00, 160, 84, 12, 4, '2025-01-01 10:00:00', '2025-11-13 02:00:00');

-- ----------------------------
-- Table structure for mall_product_category
-- ----------------------------
DROP TABLE IF EXISTS `mall_product_category`;
CREATE TABLE `mall_product_category`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '商品种类id',
  `category_name` varchar(8) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL COMMENT '商品种类名称',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '商品种类创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '商品种类更新时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 13 CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of mall_product_category
-- ----------------------------
INSERT INTO `mall_product_category` VALUES (1, '新鲜水果', '2024-01-01 10:00:00', '2024-01-01 10:00:00');
INSERT INTO `mall_product_category` VALUES (2, '时令蔬菜', '2024-01-01 10:00:00', '2024-01-01 10:00:00');
INSERT INTO `mall_product_category` VALUES (3, '五谷杂粮', '2024-01-01 10:00:00', '2024-01-01 10:00:00');
INSERT INTO `mall_product_category` VALUES (4, '干果坚果', '2024-01-01 10:00:00', '2025-11-17 15:11:20');

-- ----------------------------
-- Table structure for mall_user
-- ----------------------------
DROP TABLE IF EXISTS `mall_user`;
CREATE TABLE `mall_user`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '用户id',
  `username` varchar(8) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL COMMENT '用户名称',
  `password` varchar(16) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL COMMENT '用户密码',
  `email` varchar(64) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL COMMENT '用户电子邮箱',
  `head_pic` varchar(256) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL DEFAULT 'common/user_img.jpg' COMMENT '用户头像',
  `phone` varchar(11) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL COMMENT '用户手机号码',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '用户信息创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '用户信息更新时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 53 CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of mall_user
-- ----------------------------
INSERT INTO `mall_user` VALUES (1, '张三', '123456', 'zhangsan@email.com', '20251109/1605253582033.jpg', '13800138001', '2025-01-01 10:00:00', '2025-11-13 23:17:32');
INSERT INTO `mall_user` VALUES (2, '李四', '123456', 'lisi@email.com', '20251109/1605253582033.jpg', '13800138002', '2025-01-01 10:00:00', '2025-01-01 10:00:00');
INSERT INTO `mall_user` VALUES (3, '王五', '123456', 'wangwu@email.com', '20251109/1605253582033.jpg', '13800138003', '2025-01-01 10:00:00', '2025-01-01 10:00:00');
INSERT INTO `mall_user` VALUES (4, '赵六', '123456', 'zhaoliu@email.com', '20251109/1605253582033.jpg', '13800138004', '2025-01-01 10:00:00', '2025-01-01 10:00:00');
INSERT INTO `mall_user` VALUES (5, '钱七', '123456', 'qianqi@email.com', '20251109/1605253582033.jpg', '13800138005', '2025-01-01 10:00:00', '2025-01-01 10:00:00');
INSERT INTO `mall_user` VALUES (6, '孙八', '123456', 'sunba@email.com', '20251109/1605253582033.jpg', '13800138006', '2025-01-01 10:00:00', '2025-01-01 10:00:00');
INSERT INTO `mall_user` VALUES (7, '周九', '123456', 'zhoujiu@email.com', '20251109/1605253582033.jpg', '13800138007', '2025-01-01 10:00:00', '2025-01-01 10:00:00');
INSERT INTO `mall_user` VALUES (8, '吴十', '123456', 'wushi@email.com', '20251109/1605253582033.jpg', '13800138008', '2025-01-01 10:00:00', '2025-01-01 10:00:00');
INSERT INTO `mall_user` VALUES (9, '郑一', '123456', 'zhengyi@email.com', '20251109/1605253582033.jpg', '13800138009', '2025-01-01 10:00:00', '2025-01-01 10:00:00');
INSERT INTO `mall_user` VALUES (10, '王二', '123456', 'wanger@email.com', '20251109/1605253582033.jpg', '13800138010', '2025-01-01 10:00:00', '2025-01-01 10:00:00');
INSERT INTO `mall_user` VALUES (11, '李三', '123456', 'lisan@email.com', '20251109/1605253582033.jpg', '13800138011', '2025-01-01 10:00:00', '2025-01-01 10:00:00');
INSERT INTO `mall_user` VALUES (12, '张四', '123456', 'zhangsi@email.com', '20251109/1605253582033.jpg', '13800138012', '2025-01-01 10:00:00', '2025-01-01 10:00:00');
INSERT INTO `mall_user` VALUES (13, '刘五', '123456', 'liuwu@email.com', '20251109/1605253582033.jpg', '13800138013', '2025-01-01 10:00:00', '2025-01-01 10:00:00');
INSERT INTO `mall_user` VALUES (14, '陈六', '123456', 'chenliu@email.com', '20251109/1605253582033.jpg', '13800138014', '2025-01-01 10:00:00', '2025-01-01 10:00:00');
INSERT INTO `mall_user` VALUES (15, '杨七', '123456', 'yangqi@email.com', '20251109/1605253582033.jpg', '13800138015', '2025-01-01 10:00:00', '2025-01-01 10:00:00');
INSERT INTO `mall_user` VALUES (16, '黄八', '123456', 'huangba@email.com', '20251109/1605253582033.jpg', '13800138016', '2025-01-01 10:00:00', '2025-01-01 10:00:00');
INSERT INTO `mall_user` VALUES (17, '赵九', '123456', 'zhaojiu@email.com', '20251109/1605253582033.jpg', '13800138017', '2025-01-01 10:00:00', '2025-01-01 10:00:00');
INSERT INTO `mall_user` VALUES (18, '周十', '123456', 'zhoushi@email.com', '20251109/1605253582033.jpg', '13800138018', '2025-01-01 10:00:00', '2025-01-01 10:00:00');
INSERT INTO `mall_user` VALUES (19, '吴一', '123456', 'wuyi@email.com', '20251109/1605253582033.jpg', '13800138019', '2025-01-01 10:00:00', '2025-01-01 10:00:00');
INSERT INTO `mall_user` VALUES (20, '郑二', '123456', 'zhenger@email.com', '20251109/1605253582033.jpg', '13800138020', '2025-01-01 10:00:00', '2025-01-01 10:00:00');
INSERT INTO `mall_user` VALUES (21, '王三', '123456', 'wangsan@email.com', '20251109/1605253582033.jpg', '13800138021', '2025-01-01 10:00:00', '2025-01-01 10:00:00');
INSERT INTO `mall_user` VALUES (22, '李四', '123456', 'lisi2@email.com', '20251109/1605253582033.jpg', '13800138022', '2025-01-01 10:00:00', '2025-01-01 10:00:00');
INSERT INTO `mall_user` VALUES (23, '张五', '123456', 'zhangwu@email.com', '20251109/1605253582033.jpg', '13800138023', '2025-01-01 10:00:00', '2025-01-01 10:00:00');
INSERT INTO `mall_user` VALUES (24, '刘六', '123456', 'liuliu@email.com', '20251109/1605253582033.jpg', '13800138024', '2025-01-01 10:00:00', '2025-01-01 10:00:00');
INSERT INTO `mall_user` VALUES (25, '陈七', '123456', 'chenqi@email.com', '20251109/1605253582033.jpg', '13800138025', '2025-01-01 10:00:00', '2025-01-01 10:00:00');
INSERT INTO `mall_user` VALUES (26, '杨八', '123456', 'yangba@email.com', '20251109/1605253582033.jpg', '13800138026', '2025-01-01 10:00:00', '2025-01-01 10:00:00');
INSERT INTO `mall_user` VALUES (27, '黄九', '123456', 'huangjiu@email.com', '20251109/1605253582033.jpg', '13800138027', '2025-01-01 10:00:00', '2025-01-01 10:00:00');
INSERT INTO `mall_user` VALUES (28, '赵十', '123456', 'zhaoshi@email.com', '20251109/1605253582033.jpg', '13800138028', '2025-01-01 10:00:00', '2025-01-01 10:00:00');
INSERT INTO `mall_user` VALUES (29, '周一', '123456', 'zhouyi@email.com', '20251109/1605253582033.jpg', '13800138029', '2025-01-01 10:00:00', '2025-01-01 10:00:00');
INSERT INTO `mall_user` VALUES (30, '吴二', '123456', 'wuer@email.com', '20251109/1605253582033.jpg', '13800138030', '2025-01-01 10:00:00', '2025-01-01 10:00:00');
INSERT INTO `mall_user` VALUES (31, '郑三', '123456', 'zhengsan@email.com', '20251109/1605253582033.jpg', '13800138031', '2025-01-01 10:00:00', '2025-01-01 10:00:00');
INSERT INTO `mall_user` VALUES (32, '王四', '123456', 'wangsi@email.com', '20251109/1605253582033.jpg', '13800138032', '2025-01-01 10:00:00', '2025-01-01 10:00:00');
INSERT INTO `mall_user` VALUES (33, '李五', '123456', 'liwu@email.com', '20251109/1605253582033.jpg', '13800138033', '2025-01-01 10:00:00', '2025-01-01 10:00:00');
INSERT INTO `mall_user` VALUES (34, '张六', '123456', 'zhangliu@email.com', '20251109/1605253582033.jpg', '13800138034', '2025-01-01 10:00:00', '2025-01-01 10:00:00');
INSERT INTO `mall_user` VALUES (35, '刘七', '123456', 'liuqi@email.com', '20251109/1605253582033.jpg', '13800138035', '2025-01-01 10:00:00', '2025-01-01 10:00:00');
INSERT INTO `mall_user` VALUES (36, '陈八', '123456', 'chenba@email.com', '20251109/1605253582033.jpg', '13800138036', '2025-01-01 10:00:00', '2025-01-01 10:00:00');
INSERT INTO `mall_user` VALUES (37, '杨九', '123456', 'yangjiu@email.com', '20251109/1605253582033.jpg', '13800138037', '2025-01-01 10:00:00', '2025-01-01 10:00:00');
INSERT INTO `mall_user` VALUES (38, '黄十', '123456', 'huangshi@email.com', '20251109/1605253582033.jpg', '13800138038', '2025-01-01 10:00:00', '2025-01-01 10:00:00');
INSERT INTO `mall_user` VALUES (39, '赵一', '123456', 'zhaoyi@email.com', '20251109/1605253582033.jpg', '13800138039', '2025-01-01 10:00:00', '2025-01-01 10:00:00');
INSERT INTO `mall_user` VALUES (40, '周二', '123456', 'zhouer@email.com', '20251109/1605253582033.jpg', '13800138040', '2025-01-01 10:00:00', '2025-01-01 10:00:00');
INSERT INTO `mall_user` VALUES (41, '吴三', '123456', 'wusan@email.com', '20251109/1605253582033.jpg', '13800138041', '2025-01-01 10:00:00', '2025-01-01 10:00:00');
INSERT INTO `mall_user` VALUES (42, '郑四', '123456', 'zhengsi@email.com', '20251109/1605253582033.jpg', '13800138042', '2025-01-01 10:00:00', '2025-01-01 10:00:00');
INSERT INTO `mall_user` VALUES (43, '王五', '123456', 'wangwu2@email.com', '20251109/1605253582033.jpg', '13800138043', '2025-01-01 10:00:00', '2025-01-01 10:00:00');
INSERT INTO `mall_user` VALUES (44, '李六', '123456', 'liliu@email.com', '20251109/1605253582033.jpg', '13800138044', '2025-01-01 10:00:00', '2025-01-01 10:00:00');
INSERT INTO `mall_user` VALUES (45, '张七', '123456', 'zhangqi@email.com', '20251109/1605253582033.jpg', '13800138045', '2025-01-01 10:00:00', '2025-01-01 10:00:00');
INSERT INTO `mall_user` VALUES (46, '刘八', '123456', 'liuba@email.com', '20251109/1605253582033.jpg', '13800138046', '2025-01-01 10:00:00', '2025-01-01 10:00:00');
INSERT INTO `mall_user` VALUES (47, '陈九', '123456', 'chenjiu@email.com', '20251109/1605253582033.jpg', '13800138047', '2025-01-01 10:00:00', '2025-01-01 10:00:00');
INSERT INTO `mall_user` VALUES (48, '杨十', '123456', 'yangshi@email.com', '20251109/1605253582033.jpg', '13800138048', '2025-01-01 10:00:00', '2025-01-01 10:00:00');
INSERT INTO `mall_user` VALUES (49, '黄一', '123456', 'huangyi@email.com', '20251109/1605253582033.jpg', '13800138049', '2025-01-01 10:00:00', '2025-01-01 10:00:00');
INSERT INTO `mall_user` VALUES (50, '赵二', '123456', 'zhaoer@email.com', '20251109/1605253582033.jpg', '13800138050', '2025-01-01 10:00:00', '2025-01-01 10:00:00');


SET FOREIGN_KEY_CHECKS = 1;
