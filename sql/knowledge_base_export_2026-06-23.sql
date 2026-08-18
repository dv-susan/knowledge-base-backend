-- MySQL dump 10.13  Distrib 8.0.21, for macos10.15 (x86_64)
--
-- Host: localhost    Database: kb_user
-- ------------------------------------------------------
-- Server version	8.0.21

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Current Database: `kb_user`
--

CREATE DATABASE /*!32312 IF NOT EXISTS*/ `kb_user` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci */ /*!80016 DEFAULT ENCRYPTION='N' */;

USE `kb_user`;

--
-- Temporary view structure for view `kb_document`
--

DROP TABLE IF EXISTS `kb_document`;
/*!50001 DROP VIEW IF EXISTS `kb_document`*/;
SET @saved_cs_client     = @@character_set_client;
/*!50503 SET character_set_client = utf8mb4 */;
/*!50001 CREATE VIEW `kb_document` AS SELECT 
 1 AS `id`,
 1 AS `title`,
 1 AS `author_id`,
 1 AS `author_name`,
 1 AS `category_id`,
 1 AS `status`,
 1 AS `view_count`,
 1 AS `like_count`,
 1 AS `favorite_count`,
 1 AS `comment_count`,
 1 AS `is_public`,
 1 AS `is_top`,
 1 AS `is_recommend`,
 1 AS `document_type`,
 1 AS `source`,
 1 AS `cover_image`,
 1 AS `summary`,
 1 AS `sort`,
 1 AS `allow_comment`,
 1 AS `publish_time`,
 1 AS `created_at`,
 1 AS `updated_at`,
 1 AS `create_by`,
 1 AS `update_by`,
 1 AS `deleted`*/;
SET character_set_client = @saved_cs_client;

--
-- Table structure for table `kb_permission`
--

DROP TABLE IF EXISTS `kb_permission`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `kb_permission` (
  `id` bigint NOT NULL COMMENT '权限ID（雪花算法）',
  `parent_id` bigint NOT NULL DEFAULT '0' COMMENT '父权限ID',
  `permission_name` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '权限名称',
  `permission_code` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '权限编码',
  `permission_type` tinyint NOT NULL COMMENT '权限类型：1-菜单，2-按钮，3-接口',
  `menu_url` varchar(200) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '菜单URL',
  `api_url` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '接口URL',
  `method` varchar(10) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '请求方法：GET,POST,PUT,DELETE',
  `icon` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '图标',
  `sort` int NOT NULL DEFAULT '0' COMMENT '排序号',
  `status` tinyint NOT NULL DEFAULT '1' COMMENT '状态：0-禁用，1-启用',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint NOT NULL DEFAULT '0' COMMENT '删除标识：0-未删除，1-已删除',
  `create_by` bigint DEFAULT NULL COMMENT '创建人',
  `update_by` bigint DEFAULT NULL COMMENT '更新人',
  PRIMARY KEY (`id`),
  KEY `idx_parent_id` (`parent_id`),
  KEY `idx_permission_type` (`permission_type`),
  KEY `idx_permission_code` (`permission_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='权限表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `kb_permission`
--

LOCK TABLES `kb_permission` WRITE;
/*!40000 ALTER TABLE `kb_permission` DISABLE KEYS */;
INSERT INTO `kb_permission` VALUES (3000000000000000001,0,'首页','dashboard',1,'/dashboard',NULL,NULL,'DashboardOutlined',1,1,'2026-06-19 10:24:37','2026-06-19 10:24:37',0,NULL,NULL),(3000000000000000002,0,'文档中心','document',1,'/documents',NULL,NULL,'FileTextOutlined',2,1,'2026-06-19 10:24:37','2026-06-19 10:24:37',0,NULL,NULL),(3000000000000000003,0,'知识图谱','graph',1,'/knowledge-graph',NULL,NULL,'NodeIndexOutlined',4,1,'2026-06-19 10:24:37','2026-06-19 10:24:37',0,NULL,NULL),(3000000000000000004,0,'AI助手','ai',1,'/ai',NULL,NULL,'RobotOutlined',6,1,'2026-06-19 10:24:37','2026-06-19 10:24:37',0,NULL,NULL),(3000000000000000005,0,'搜索','search',1,'/search',NULL,NULL,'SearchOutlined',5,1,'2026-06-19 10:24:37','2026-06-19 10:24:37',0,NULL,NULL),(3000000000000000006,0,'通知中心','notification',1,'/notifications',NULL,NULL,'BellOutlined',8,1,'2026-06-19 10:24:37','2026-06-19 10:24:37',0,NULL,NULL),(3000000000000000007,0,'个人中心','profile',1,'/profile',NULL,NULL,'UserOutlined',9,1,'2026-06-19 10:24:37','2026-06-19 10:24:37',0,NULL,NULL),(3000000000000000008,0,'系统管理','system',1,'/admin',NULL,NULL,'SettingOutlined',10,1,'2026-06-19 10:24:37','2026-06-19 10:24:37',0,NULL,NULL),(3000000000000000011,3000000000000000002,'文档列表','document:list',1,'/documents',NULL,NULL,NULL,1,1,'2026-06-19 10:24:37','2026-06-19 10:24:37',0,NULL,NULL),(3000000000000000012,3000000000000000002,'创建文档','document:create',2,NULL,NULL,NULL,NULL,2,1,'2026-06-19 10:24:37','2026-06-19 10:24:37',0,NULL,NULL),(3000000000000000013,3000000000000000002,'编辑文档','document:edit',2,NULL,NULL,NULL,NULL,3,1,'2026-06-19 10:24:37','2026-06-19 10:24:37',0,NULL,NULL),(3000000000000000014,3000000000000000002,'删除文档','document:delete',2,NULL,NULL,NULL,NULL,4,1,'2026-06-19 10:24:37','2026-06-19 10:24:37',0,NULL,NULL),(3000000000000000015,3000000000000000002,'文档审核','document:review',2,NULL,NULL,NULL,NULL,5,1,'2026-06-19 10:24:37','2026-06-19 10:24:37',0,NULL,NULL),(3000000000000000016,3000000000000000002,'文档分类','document:category',1,'/admin/categories',NULL,NULL,NULL,6,1,'2026-06-19 10:24:37','2026-06-19 10:24:37',0,NULL,NULL),(3000000000000000017,3000000000000000002,'文档标签','document:tag',1,'/admin/tags',NULL,NULL,NULL,7,1,'2026-06-19 10:24:37','2026-06-19 10:24:37',0,NULL,NULL),(3000000000000000018,3000000000000000016,'分类查询','document:category:query',3,NULL,NULL,NULL,NULL,1,1,'2026-06-19 10:24:37','2026-06-19 10:24:37',0,NULL,NULL),(3000000000000000021,3000000000000000008,'用户管理','system:user',1,'/admin/users',NULL,NULL,NULL,1,1,'2026-06-19 10:24:37','2026-06-19 10:24:37',0,NULL,NULL),(3000000000000000022,3000000000000000008,'角色管理','system:role',1,'/admin/roles',NULL,NULL,NULL,2,1,'2026-06-19 10:24:37','2026-06-19 10:24:37',0,NULL,NULL),(3000000000000000023,3000000000000000008,'权限管理','system:permission',1,'/admin/permissions',NULL,NULL,NULL,3,1,'2026-06-19 10:24:37','2026-06-19 10:24:37',0,NULL,NULL),(3000000000000000024,3000000000000000008,'团队管理','system:team',1,'/admin/teams',NULL,NULL,NULL,4,1,'2026-06-19 10:24:37','2026-06-19 10:24:37',0,NULL,NULL),(3000000000000000025,3000000000000000008,'数据统计','system:statistics',1,'/admin/statistics',NULL,NULL,NULL,5,1,'2026-06-19 10:24:37','2026-06-19 10:24:37',0,NULL,NULL),(3000000000000000026,3000000000000000008,'审核管理','system:review',1,'/admin/review',NULL,NULL,NULL,6,1,'2026-06-19 10:24:37','2026-06-19 10:24:37',0,NULL,NULL),(3000000000000000027,3000000000000000008,'系统设置','system:settings',1,'/admin/settings',NULL,NULL,NULL,7,1,'2026-06-19 10:24:37','2026-06-19 10:24:37',0,NULL,NULL),(3000000000000000031,0,'文档查询接口','api:document:query',3,NULL,'/api/document/**','GET',NULL,1,1,'2026-06-19 10:24:37','2026-06-19 10:24:37',0,NULL,NULL),(3000000000000000032,0,'文档创建接口','api:document:create',3,NULL,'/api/document','POST',NULL,2,1,'2026-06-19 10:24:37','2026-06-19 10:24:37',0,NULL,NULL),(3000000000000000033,0,'文档更新接口','api:document:update',3,NULL,'/api/document/**','PUT',NULL,3,1,'2026-06-19 10:24:37','2026-06-19 10:24:37',0,NULL,NULL),(3000000000000000034,0,'文档删除接口','api:document:delete',3,NULL,'/api/document/**','DELETE',NULL,4,1,'2026-06-19 10:24:37','2026-06-19 10:24:37',0,NULL,NULL),(3000000000000000035,0,'用户管理接口','api:user:manage',3,NULL,'/api/user/**','*',NULL,5,1,'2026-06-19 10:24:37','2026-06-19 10:24:37',0,NULL,NULL),(3000000000000000036,0,'角色管理接口','api:role:manage',3,NULL,'/api/role/**','*',NULL,6,1,'2026-06-19 10:24:37','2026-06-19 10:24:37',0,NULL,NULL),(3000000000000000046,0,'文件管理','file',1,'/files',NULL,NULL,'FolderOpenOutlined',3,1,'2026-06-19 10:24:37','2026-06-19 10:24:37',0,NULL,NULL),(3000000000000000047,0,'AI写作','ai-writing',1,'/ai-writing',NULL,NULL,'EditOutlined',7,1,'2026-06-19 10:24:37','2026-06-19 10:24:37',0,NULL,NULL),(3000000000000000048,3000000000000000046,'文件列表','file:list',1,'/files',NULL,NULL,NULL,1,1,'2026-06-19 10:24:37','2026-06-19 10:24:37',0,NULL,NULL),(3000000000000000049,3000000000000000046,'上传文件','file:upload',2,NULL,NULL,NULL,NULL,2,1,'2026-06-19 10:24:37','2026-06-19 10:24:37',0,NULL,NULL),(3000000000000000050,3000000000000000046,'删除文件','file:delete',2,NULL,NULL,NULL,NULL,3,1,'2026-06-19 10:24:37','2026-06-19 10:24:37',0,NULL,NULL),(3000000000000000051,3000000000000000008,'系统配置','system:config',1,'/admin/system-config',NULL,NULL,NULL,8,1,'2026-06-19 10:24:37','2026-06-19 10:24:37',0,NULL,NULL),(3000000000000000052,3000000000000000008,'字典管理','system:dictionary',1,'/admin/dictionary',NULL,NULL,NULL,9,1,'2026-06-19 10:24:37','2026-06-19 10:24:37',0,NULL,NULL),(3000000000000000053,3000000000000000008,'操作日志','system:operation-log',1,'/admin/operation-logs',NULL,NULL,NULL,10,1,'2026-06-19 10:24:37','2026-06-19 10:24:37',0,NULL,NULL),(3000000000000000054,3000000000000000008,'通知模板','system:notification-template',1,'/admin/notification-templates',NULL,NULL,NULL,11,1,'2026-06-19 10:24:37','2026-06-19 10:24:37',0,NULL,NULL);
/*!40000 ALTER TABLE `kb_permission` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `kb_role`
--

DROP TABLE IF EXISTS `kb_role`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `kb_role` (
  `id` bigint NOT NULL COMMENT '角色ID（雪花算法）',
  `role_name` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '角色名称',
  `role_code` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '角色编码',
  `description` varchar(200) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '角色描述',
  `sort` int NOT NULL DEFAULT '0' COMMENT '排序号',
  `status` tinyint NOT NULL DEFAULT '1' COMMENT '状态：0-禁用，1-启用',
  `remark` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '备注',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `create_by` bigint DEFAULT NULL COMMENT '创建人',
  `update_by` bigint DEFAULT NULL COMMENT '更新人',
  `deleted` tinyint NOT NULL DEFAULT '0' COMMENT '删除标识：0-未删除，1-已删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_role_code` (`role_code`,`deleted`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='角色表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `kb_role`
--

LOCK TABLES `kb_role` WRITE;
/*!40000 ALTER TABLE `kb_role` DISABLE KEYS */;
INSERT INTO `kb_role` VALUES (2000000000000000001,'超级管理员','ROLE_SUPER_ADMIN','拥有系统所有权限',1,1,NULL,'2026-06-19 10:24:37','2026-06-19 10:24:37',NULL,NULL,0),(2000000000000000002,'管理员','ROLE_ADMIN','拥有系统管理权限',2,1,NULL,'2026-06-19 10:24:37','2026-06-19 10:24:37',NULL,NULL,0),(2000000000000000003,'编辑','ROLE_EDITOR','可编辑和管理文档',3,1,NULL,'2026-06-19 10:24:37','2026-06-19 10:24:37',NULL,NULL,0),(2000000000000000004,'审核员','ROLE_REVIEWER','可审核文档',4,1,NULL,'2026-06-19 10:24:37','2026-06-19 10:24:37',NULL,NULL,0),(2000000000000000005,'普通用户','ROLE_USER','普通用户权限',5,1,NULL,'2026-06-19 10:24:37','2026-06-19 10:24:37',NULL,NULL,0),(2000000000000000006,'访客','ROLE_GUEST','只读访客权限',6,1,NULL,'2026-06-19 10:24:37','2026-06-19 10:24:37',NULL,NULL,0);
/*!40000 ALTER TABLE `kb_role` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `kb_role_permission`
--

DROP TABLE IF EXISTS `kb_role_permission`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `kb_role_permission` (
  `id` bigint NOT NULL COMMENT '主键ID',
  `role_id` bigint NOT NULL COMMENT '角色ID',
  `permission_id` bigint NOT NULL COMMENT '权限ID',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_role_permission` (`role_id`,`permission_id`),
  KEY `idx_permission_id` (`permission_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='角色权限关联表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `kb_role_permission`
--

LOCK TABLES `kb_role_permission` WRITE;
/*!40000 ALTER TABLE `kb_role_permission` DISABLE KEYS */;
INSERT INTO `kb_role_permission` VALUES (5000000000000000001,2000000000000000001,3000000000000000001,'2026-06-19 10:24:37'),(5000000000000000002,2000000000000000001,3000000000000000002,'2026-06-19 10:24:37'),(5000000000000000003,2000000000000000001,3000000000000000003,'2026-06-19 10:24:37'),(5000000000000000004,2000000000000000001,3000000000000000004,'2026-06-19 10:24:37'),(5000000000000000005,2000000000000000001,3000000000000000005,'2026-06-19 10:24:37'),(5000000000000000006,2000000000000000001,3000000000000000006,'2026-06-19 10:24:37'),(5000000000000000007,2000000000000000001,3000000000000000007,'2026-06-19 10:24:37'),(5000000000000000008,2000000000000000001,3000000000000000008,'2026-06-19 10:24:37'),(5000000000000000009,2000000000000000001,3000000000000000011,'2026-06-19 10:24:37'),(5000000000000000010,2000000000000000001,3000000000000000012,'2026-06-19 10:24:37'),(5000000000000000011,2000000000000000001,3000000000000000013,'2026-06-19 10:24:37'),(5000000000000000012,2000000000000000001,3000000000000000014,'2026-06-19 10:24:37'),(5000000000000000013,2000000000000000001,3000000000000000015,'2026-06-19 10:24:37'),(5000000000000000014,2000000000000000001,3000000000000000016,'2026-06-19 10:24:37'),(5000000000000000015,2000000000000000001,3000000000000000017,'2026-06-19 10:24:37'),(5000000000000000016,2000000000000000001,3000000000000000018,'2026-06-19 10:24:37'),(5000000000000000017,2000000000000000001,3000000000000000021,'2026-06-19 10:24:37'),(5000000000000000018,2000000000000000001,3000000000000000022,'2026-06-19 10:24:37'),(5000000000000000019,2000000000000000001,3000000000000000023,'2026-06-19 10:24:37'),(5000000000000000020,2000000000000000001,3000000000000000024,'2026-06-19 10:24:37'),(5000000000000000021,2000000000000000001,3000000000000000025,'2026-06-19 10:24:37'),(5000000000000000022,2000000000000000001,3000000000000000026,'2026-06-19 10:24:37'),(5000000000000000023,2000000000000000001,3000000000000000027,'2026-06-19 10:24:37'),(5000000000000000024,2000000000000000001,3000000000000000031,'2026-06-19 10:24:37'),(5000000000000000025,2000000000000000001,3000000000000000032,'2026-06-19 10:24:37'),(5000000000000000026,2000000000000000001,3000000000000000033,'2026-06-19 10:24:37'),(5000000000000000027,2000000000000000001,3000000000000000034,'2026-06-19 10:24:37'),(5000000000000000028,2000000000000000001,3000000000000000035,'2026-06-19 10:24:37'),(5000000000000000029,2000000000000000001,3000000000000000036,'2026-06-19 10:24:37'),(5000000000000000030,2000000000000000001,3000000000000000046,'2026-06-19 10:24:37'),(5000000000000000031,2000000000000000001,3000000000000000047,'2026-06-19 10:24:37'),(5000000000000000032,2000000000000000001,3000000000000000048,'2026-06-19 10:24:37'),(5000000000000000033,2000000000000000001,3000000000000000049,'2026-06-19 10:24:37'),(5000000000000000034,2000000000000000001,3000000000000000050,'2026-06-19 10:24:37'),(5000000000000000035,2000000000000000001,3000000000000000051,'2026-06-19 10:24:37'),(5000000000000000036,2000000000000000001,3000000000000000052,'2026-06-19 10:24:37'),(5000000000000000037,2000000000000000001,3000000000000000053,'2026-06-19 10:24:37'),(5000000000000000038,2000000000000000001,3000000000000000054,'2026-06-19 10:24:37'),(5000000000000000040,2000000000000000005,3000000000000000011,'2026-06-19 10:25:54'),(5000000000000000041,2000000000000000005,3000000000000000012,'2026-06-19 10:25:54'),(5000000000000000042,2000000000000000005,3000000000000000013,'2026-06-19 10:25:54'),(5000000000000000043,2000000000000000005,3000000000000000014,'2026-06-19 10:25:54');
/*!40000 ALTER TABLE `kb_role_permission` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `kb_team`
--

DROP TABLE IF EXISTS `kb_team`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `kb_team` (
  `id` bigint NOT NULL COMMENT '团队ID',
  `team_name` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '团队名称',
  `team_code` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '团队编码',
  `description` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '团队描述',
  `icon` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '团队图标标识',
  `leader_id` bigint DEFAULT NULL COMMENT '团队负责人ID',
  `parent_id` bigint NOT NULL DEFAULT '0' COMMENT '父团队ID',
  `sort` int NOT NULL DEFAULT '0' COMMENT '排序号',
  `level` int NOT NULL DEFAULT '0' COMMENT '层级',
  `path` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '团队路径',
  `member_count` int NOT NULL DEFAULT '0' COMMENT '成员数量',
  `doc_count` int NOT NULL DEFAULT '0' COMMENT '文档数量',
  `status` tinyint NOT NULL DEFAULT '1' COMMENT '状态：0-禁用，1-启用',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `create_by` bigint DEFAULT NULL COMMENT '创建人',
  `update_by` bigint DEFAULT NULL COMMENT '更新人',
  `deleted` tinyint NOT NULL DEFAULT '0' COMMENT '删除标识',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_team_code` (`team_code`,`deleted`),
  KEY `idx_leader_id` (`leader_id`),
  KEY `idx_parent_id` (`parent_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='团队表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `kb_team`
--

LOCK TABLES `kb_team` WRITE;
/*!40000 ALTER TABLE `kb_team` DISABLE KEYS */;
INSERT INTO `kb_team` VALUES (8000000000000000001,'技术中心','TECH_CENTER','负责公司所有技术研发工作','tech',1000000000000000004,0,1,0,NULL,0,0,1,'2026-06-19 10:24:37','2026-06-19 10:24:37',NULL,NULL,0),(8000000000000000002,'产品中心','PRODUCT_CENTER','负责产品规划和设计','product',1000000000000000005,0,2,0,NULL,0,0,1,'2026-06-19 10:24:37','2026-06-19 10:24:37',NULL,NULL,0),(8000000000000000003,'运营中心','OPS_CENTER','负责业务运营和市场推广','ops',1000000000000000007,0,3,0,NULL,0,0,1,'2026-06-19 10:24:37','2026-06-19 10:24:37',NULL,NULL,0),(8000000000000000004,'职能中心','ADMIN_CENTER','负责公司行政人事财务工作','admin',1000000000000000008,0,4,0,NULL,0,0,1,'2026-06-19 10:24:37','2026-06-19 10:24:37',NULL,NULL,0),(8000000000000000005,'后端开发组','BACKEND_TEAM','后端系统开发','backend',1000000000000000004,8000000000000000001,1,0,NULL,0,0,1,'2026-06-19 10:24:37','2026-06-19 10:24:37',NULL,NULL,0),(8000000000000000006,'前端开发组','FRONTEND_TEAM','前端系统开发','frontend',1000000000000000004,8000000000000000001,2,0,NULL,0,0,1,'2026-06-19 10:24:37','2026-06-19 10:24:37',NULL,NULL,0),(8000000000000000007,'测试组','QA_TEAM','质量保证和测试','qa',1000000000000000003,8000000000000000001,3,0,NULL,0,0,1,'2026-06-19 10:24:37','2026-06-19 10:24:37',NULL,NULL,0);
/*!40000 ALTER TABLE `kb_team` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `kb_team_member`
--

DROP TABLE IF EXISTS `kb_team_member`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `kb_team_member` (
  `id` bigint NOT NULL COMMENT '主键ID',
  `team_id` bigint NOT NULL COMMENT '团队ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `member_role` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'member' COMMENT '成员角色：leader-负责人，member-成员',
  `join_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '加入时间',
  `create_by` bigint DEFAULT NULL COMMENT '添加人',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_team_user` (`team_id`,`user_id`),
  KEY `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='团队成员表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `kb_team_member`
--

LOCK TABLES `kb_team_member` WRITE;
/*!40000 ALTER TABLE `kb_team_member` DISABLE KEYS */;
INSERT INTO `kb_team_member` VALUES (9000000000000000001,8000000000000000005,1000000000000000004,'leader','2026-06-19 10:24:37',NULL),(9000000000000000002,8000000000000000005,1000000000000000001,'member','2026-06-19 10:24:37',NULL),(9000000000000000003,8000000000000000006,1000000000000000006,'member','2026-06-19 10:24:37',NULL),(9000000000000000004,8000000000000000007,1000000000000000003,'leader','2026-06-19 10:24:37',NULL);
/*!40000 ALTER TABLE `kb_team_member` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `kb_user`
--

DROP TABLE IF EXISTS `kb_user`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `kb_user` (
  `id` bigint NOT NULL COMMENT '用户ID（雪花算法）',
  `username` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '用户名',
  `password` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '密码（BCrypt加密）',
  `email` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '邮箱',
  `email_verified` tinyint NOT NULL DEFAULT '0' COMMENT '邮箱是否已验证：0-未验证，1-已验证',
  `activation_token` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '账户激活令牌',
  `activation_token_expiry` datetime DEFAULT NULL COMMENT '激活令牌过期时间',
  `phone` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '手机号',
  `avatar` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '头像URL',
  `real_name` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '真实姓名',
  `department` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '部门',
  `position` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '职位',
  `remark` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '个人简介/备注',
  `status` tinyint NOT NULL DEFAULT '1' COMMENT '状态：0-禁用，1-启用',
  `last_login_time` datetime DEFAULT NULL COMMENT '最后登录时间',
  `last_login_ip` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '最后登录IP',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `create_by` bigint DEFAULT NULL COMMENT '创建人',
  `update_by` bigint DEFAULT NULL COMMENT '更新人',
  `deleted` tinyint NOT NULL DEFAULT '0' COMMENT '删除标识：0-未删除，1-已删除',
  `tenant_id` bigint DEFAULT NULL COMMENT '租户ID',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_username` (`username`,`deleted`,`tenant_id`),
  UNIQUE KEY `uk_email` (`email`,`deleted`,`tenant_id`),
  KEY `idx_department` (`department`),
  KEY `idx_status` (`status`),
  KEY `idx_create_time` (`created_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `kb_user`
--

LOCK TABLES `kb_user` WRITE;
/*!40000 ALTER TABLE `kb_user` DISABLE KEYS */;
INSERT INTO `kb_user` VALUES (326186554798247936,'zhoujielun','$2a$10$OxPN77uAH2PhC1mzzjCub.Alj7GSwB9w8BOCpgXGlr83gmekd8bZa','zhoujielun@163.com',0,NULL,NULL,NULL,NULL,'周杰伦','','',NULL,1,'2026-06-19 10:29:06',NULL,'2026-06-19 10:28:55','2026-06-19 10:29:06',1000000000000000001,NULL,0,NULL),(1000000000000000001,'admin','$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi','admin@company.com',0,NULL,NULL,NULL,'https://api.dicebear.com/7.x/avataaars/svg?seed=admin','系统管理员','技术部','系统架构师',NULL,1,'2026-06-23 16:55:27',NULL,'2026-06-19 10:24:37','2026-06-23 16:55:27',NULL,NULL,0,NULL),(1000000000000000002,'editor','$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi','editor@company.com',0,NULL,NULL,NULL,'https://api.dicebear.com/7.x/avataaars/svg?seed=editor','内容编辑','内容部','高级编辑',NULL,1,NULL,NULL,'2026-06-19 10:24:37','2026-06-19 10:24:37',NULL,NULL,0,NULL),(1000000000000000003,'tester','$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi','tester@company.com',0,NULL,NULL,NULL,'https://api.dicebear.com/7.x/avataaars/svg?seed=tester','测试人员','测试部','测试工程师',NULL,1,NULL,NULL,'2026-06-19 10:24:37','2026-06-19 10:24:37',NULL,NULL,0,NULL),(1000000000000000004,'developer','$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi','dev@company.com',0,NULL,NULL,NULL,'https://api.dicebear.com/7.x/avataaars/svg?seed=developer','开发工程师','研发部','高级工程师',NULL,1,NULL,NULL,'2026-06-19 10:24:37','2026-06-19 10:24:37',NULL,NULL,0,NULL),(1000000000000000005,'product','$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi','product@company.com',0,NULL,NULL,NULL,'https://api.dicebear.com/7.x/avataaars/svg?seed=product','产品经理','产品部','高级产品经理',NULL,1,NULL,NULL,'2026-06-19 10:24:37','2026-06-19 10:24:37',NULL,NULL,0,NULL),(1000000000000000006,'designer','$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi','designer@company.com',0,NULL,NULL,NULL,'https://api.dicebear.com/7.x/avataaars/svg?seed=designer','UI设计师','设计部','高级设计师',NULL,1,NULL,NULL,'2026-06-19 10:24:37','2026-06-19 10:24:37',NULL,NULL,0,NULL),(1000000000000000007,'sales','$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi','sales@company.com',0,NULL,NULL,NULL,'https://api.dicebear.com/7.x/avataaars/svg?seed=sales','销售经理','销售部','销售经理',NULL,1,NULL,NULL,'2026-06-19 10:24:37','2026-06-19 10:24:37',NULL,NULL,0,NULL),(1000000000000000008,'hr','$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi','hr@company.com',0,NULL,NULL,NULL,'https://api.dicebear.com/7.x/avataaars/svg?seed=hr','人事专员','人力资源部','人事专员',NULL,1,NULL,NULL,'2026-06-19 10:24:37','2026-06-19 10:24:37',NULL,NULL,0,NULL),(1000000000000000009,'finance','$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi','finance@company.com',0,NULL,NULL,NULL,'https://api.dicebear.com/7.x/avataaars/svg?seed=finance','财务主管','财务部','财务主管',NULL,1,NULL,NULL,'2026-06-19 10:24:37','2026-06-19 10:24:37',NULL,NULL,0,NULL),(1000000000000000010,'guest','$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi','guest@company.com',0,NULL,NULL,NULL,'https://api.dicebear.com/7.x/avataaars/svg?seed=guest','访客用户','外部','访客',NULL,1,NULL,NULL,'2026-06-19 10:24:37','2026-06-19 10:24:37',NULL,NULL,0,NULL);
/*!40000 ALTER TABLE `kb_user` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `kb_user_permission`
--

DROP TABLE IF EXISTS `kb_user_permission`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `kb_user_permission` (
  `id` bigint NOT NULL COMMENT '主键ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `permission_id` bigint NOT NULL COMMENT '权限ID',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_permission` (`user_id`,`permission_id`),
  KEY `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户权限关联表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `kb_user_permission`
--

LOCK TABLES `kb_user_permission` WRITE;
/*!40000 ALTER TABLE `kb_user_permission` DISABLE KEYS */;
/*!40000 ALTER TABLE `kb_user_permission` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `kb_user_role`
--

DROP TABLE IF EXISTS `kb_user_role`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `kb_user_role` (
  `id` bigint NOT NULL COMMENT '主键ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `role_id` bigint NOT NULL COMMENT '角色ID',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `create_by` bigint DEFAULT NULL COMMENT '创建人',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_role` (`user_id`,`role_id`),
  KEY `idx_role_id` (`role_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户角色关联表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `kb_user_role`
--

LOCK TABLES `kb_user_role` WRITE;
/*!40000 ALTER TABLE `kb_user_role` DISABLE KEYS */;
INSERT INTO `kb_user_role` VALUES (326186556496941056,326186554798247936,2000000000000000005,'2026-06-19 10:28:55',NULL),(4000000000000000001,1000000000000000001,2000000000000000001,'2026-06-19 10:24:37',1000000000000000001),(4000000000000000002,1000000000000000002,2000000000000000003,'2026-06-19 10:24:37',1000000000000000001),(4000000000000000003,1000000000000000003,2000000000000000005,'2026-06-19 10:24:37',1000000000000000001),(4000000000000000004,1000000000000000004,2000000000000000005,'2026-06-19 10:24:37',1000000000000000001),(4000000000000000005,1000000000000000005,2000000000000000005,'2026-06-19 10:24:37',1000000000000000001),(4000000000000000006,1000000000000000006,2000000000000000005,'2026-06-19 10:24:37',1000000000000000001),(4000000000000000007,1000000000000000007,2000000000000000005,'2026-06-19 10:24:37',1000000000000000001),(4000000000000000008,1000000000000000008,2000000000000000005,'2026-06-19 10:24:37',1000000000000000001),(4000000000000000009,1000000000000000009,2000000000000000005,'2026-06-19 10:24:37',1000000000000000001),(4000000000000000010,1000000000000000010,2000000000000000006,'2026-06-19 10:24:37',1000000000000000001);
/*!40000 ALTER TABLE `kb_user_role` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `tb_token_blacklist`
--

DROP TABLE IF EXISTS `tb_token_blacklist`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `tb_token_blacklist` (
  `id` bigint NOT NULL COMMENT '主键ID',
  `token_hash` varchar(64) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT 'Token哈希值',
  `expire_time` datetime NOT NULL COMMENT '过期时间',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_token_hash` (`token_hash`),
  KEY `idx_expire_time` (`expire_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='Token黑名单表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `tb_token_blacklist`
--

LOCK TABLES `tb_token_blacklist` WRITE;
/*!40000 ALTER TABLE `tb_token_blacklist` DISABLE KEYS */;
/*!40000 ALTER TABLE `tb_token_blacklist` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping routines for database 'kb_user'
--

--
-- Current Database: `kb_document`
--

CREATE DATABASE /*!32312 IF NOT EXISTS*/ `kb_document` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci */ /*!80016 DEFAULT ENCRYPTION='N' */;

USE `kb_document`;

--
-- Table structure for table `kb_category`
--

DROP TABLE IF EXISTS `kb_category`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `kb_category` (
  `id` bigint NOT NULL COMMENT '分类ID',
  `category_name` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '分类名称',
  `category_code` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '分类编码',
  `remark` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '备注',
  `parent_id` bigint NOT NULL DEFAULT '0' COMMENT '父分类ID',
  `category_icon` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT 'tech' COMMENT '分类图标标识',
  `description` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '分类描述',
  `sort` int NOT NULL DEFAULT '0' COMMENT '排序号',
  `document_count` int NOT NULL DEFAULT '0' COMMENT '文档数量',
  `status` tinyint NOT NULL DEFAULT '1' COMMENT '状态：0-禁用，1-启用',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `create_by` bigint DEFAULT NULL COMMENT '创建人',
  `update_by` bigint DEFAULT NULL COMMENT '更新人',
  `deleted` tinyint NOT NULL DEFAULT '0' COMMENT '删除标识',
  PRIMARY KEY (`id`),
  KEY `idx_parent_id` (`parent_id`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='文档分类表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `kb_category`
--

LOCK TABLES `kb_category` WRITE;
/*!40000 ALTER TABLE `kb_category` DISABLE KEYS */;
INSERT INTO `kb_category` VALUES (6000000000000000001,'技术文档',NULL,NULL,0,'tech','技术开发相关的文档资料',1,12,1,'2026-06-19 10:24:37','2026-06-23 16:02:49',NULL,NULL,0),(6000000000000000002,'产品文档',NULL,NULL,0,'product','产品设计、需求文档',2,2,1,'2026-06-19 10:24:37','2026-06-19 10:25:00',NULL,NULL,0),(6000000000000000003,'业务流程',NULL,NULL,0,'business','公司业务流程规范',3,1,1,'2026-06-19 10:24:37','2026-06-19 10:25:00',NULL,NULL,0),(6000000000000000004,'人力资源',NULL,NULL,0,'hr','人事制度和管理规范',4,1,1,'2026-06-19 10:24:37','2026-06-19 10:25:00',NULL,NULL,0),(6000000000000000005,'财务制度',NULL,NULL,0,'finance','财务管理制度和流程',5,1,1,'2026-06-19 10:24:37','2026-06-19 10:25:00',NULL,NULL,0),(6000000000000000006,'市场营销',NULL,NULL,0,'marketing','市场营销策略和方案',6,0,1,'2026-06-19 10:24:37','2026-06-19 10:24:37',NULL,NULL,0),(6000000000000000007,'合规法务',NULL,NULL,0,'legal','法律法规和合规要求',7,0,1,'2026-06-19 10:24:37','2026-06-19 10:24:37',NULL,NULL,0),(6000000000000000008,'培训资料',NULL,NULL,0,'training','员工培训和学习资料',8,0,1,'2026-06-19 10:24:37','2026-06-19 10:24:37',NULL,NULL,0),(6000000000000000011,'后端开发',NULL,NULL,6000000000000000001,'backend','后端技术栈开发文档',1,4,1,'2026-06-19 10:24:37','2026-06-23 15:52:25',NULL,NULL,0),(6000000000000000012,'前端开发',NULL,NULL,6000000000000000001,'frontend','前端技术栈开发文档',2,1,1,'2026-06-19 10:24:37','2026-06-19 10:25:00',NULL,NULL,0),(6000000000000000013,'数据库',NULL,NULL,6000000000000000001,'database','数据库设计和优化',3,1,1,'2026-06-19 10:24:37','2026-06-19 10:25:00',NULL,NULL,0),(6000000000000000014,'DevOps',NULL,NULL,6000000000000000001,'devops','运维部署和CI/CD',4,1,1,'2026-06-19 10:24:37','2026-06-19 10:25:00',NULL,NULL,0),(6000000000000000015,'架构设计',NULL,NULL,6000000000000000001,'architecture','系统架构设计文档',5,0,1,'2026-06-19 10:24:37','2026-06-19 10:24:37',NULL,NULL,0),(6000000000000000021,'产品需求',NULL,NULL,6000000000000000002,'requirement','产品需求文档PRD',1,1,1,'2026-06-19 10:24:37','2026-06-19 10:25:00',NULL,NULL,0),(6000000000000000022,'UI设计',NULL,NULL,6000000000000000002,'design','UI/UX设计规范',2,1,1,'2026-06-19 10:24:37','2026-06-19 10:25:00',NULL,NULL,0),(6000000000000000023,'产品规划',NULL,NULL,6000000000000000002,'planning','产品规划和路线图',3,0,1,'2026-06-19 10:24:37','2026-06-19 10:24:37',NULL,NULL,0),(6000000000000000024,'竞品分析',NULL,NULL,6000000000000000002,'competitive','竞品分析报告',4,0,1,'2026-06-19 10:24:37','2026-06-19 10:24:37',NULL,NULL,0);
/*!40000 ALTER TABLE `kb_category` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `kb_comment`
--

DROP TABLE IF EXISTS `kb_comment`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `kb_comment` (
  `id` bigint NOT NULL COMMENT '评论ID',
  `document_id` bigint NOT NULL COMMENT '文档ID',
  `parent_id` bigint DEFAULT NULL COMMENT '父评论ID',
  `root_id` bigint DEFAULT NULL COMMENT '根评论ID',
  `content` text COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '评论内容',
  `commenter_id` bigint NOT NULL COMMENT '评论人ID',
  `commenter_name` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '评论人姓名',
  `commenter_avatar` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '评论人头像',
  `reply_to_user_id` bigint DEFAULT NULL COMMENT '回复给谁（用户ID）',
  `reply_to_user_name` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '回复给谁（用户姓名）',
  `like_count` int NOT NULL DEFAULT '0' COMMENT '点赞数',
  `reply_count` int NOT NULL DEFAULT '0' COMMENT '回复数',
  `status` tinyint NOT NULL DEFAULT '1' COMMENT '状态：0-隐藏，1-正常',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint NOT NULL DEFAULT '0' COMMENT '删除标识',
  PRIMARY KEY (`id`),
  KEY `idx_document_id` (`document_id`),
  KEY `idx_parent_id` (`parent_id`),
  KEY `idx_root_id` (`root_id`),
  KEY `idx_commenter_id` (`commenter_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='文档评论表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `kb_comment`
--

LOCK TABLES `kb_comment` WRITE;
/*!40000 ALTER TABLE `kb_comment` DISABLE KEYS */;
INSERT INTO `kb_comment` VALUES (1200000000000000001,1000000000000000001,NULL,NULL,'这篇文章写得很详细，对我帮助很大！',1000000000000002,'editor',NULL,NULL,NULL,12,0,1,'2026-06-18 20:30:10','2026-06-18 20:30:10',0),(1200000000000000002,1000000000000000001,NULL,NULL,'补充一点：自动配置的原理可以再详细讲讲',1000000000000004,'developer',NULL,NULL,NULL,5,0,1,'2026-06-18 20:30:10','2026-06-18 20:30:10',0),(1200000000000000003,1000000000000000002,NULL,NULL,'TypeScript的类型定义很规范，学习了！',1000000000000003,'tester',NULL,NULL,NULL,8,0,1,'2026-06-18 20:30:10','2026-06-18 20:30:10',0),(1200000000000000004,1000000000000000002,NULL,NULL,'期待出下一期关于Hooks的文章',1000000000000002,'editor',NULL,NULL,NULL,3,0,1,'2026-06-18 20:30:10','2026-06-18 20:30:10',0),(1200000000000000005,1000000000000000003,NULL,NULL,'索引优化的技巧很实用，已经在项目中应用了',1000000000000005,'product',NULL,NULL,NULL,15,0,1,'2026-06-18 20:30:10','2026-06-18 20:30:10',0),(1200000000000000006,1000000000000000005,NULL,NULL,'PRD写得很清楚，产品逻辑很完整',1000000000000001,'admin',NULL,NULL,NULL,6,0,1,'2026-06-18 20:30:10','2026-06-18 20:30:10',0),(1200000000000000007,1000000000000000008,NULL,NULL,'入职指南很详细，帮助我快速熟悉了公司',1000000000000003,'tester',NULL,NULL,NULL,23,0,1,'2026-06-18 20:30:10','2026-06-18 20:30:10',0),(1200000000000000008,1000000000000000008,NULL,NULL,'建议补充一下远程办公的注意事项',1000000000000007,'sales',NULL,NULL,NULL,2,0,1,'2026-06-18 20:30:10','2026-06-18 20:30:10',0);
/*!40000 ALTER TABLE `kb_comment` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `kb_document`
--

DROP TABLE IF EXISTS `kb_document`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `kb_document` (
  `id` bigint NOT NULL COMMENT '文档ID',
  `title` varchar(200) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '文档标题',
  `content` longtext COLLATE utf8mb4_unicode_ci COMMENT '文档内容',
  `content_id` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '内容ID（文件内容关联）',
  `content_length` int DEFAULT NULL COMMENT '内容长度',
  `summary` text COLLATE utf8mb4_unicode_ci COMMENT '文档摘要',
  `category_id` bigint DEFAULT NULL COMMENT '分类ID',
  `team_id` bigint DEFAULT NULL COMMENT '团队空间ID',
  `author_id` bigint NOT NULL COMMENT '作者ID',
  `author_name` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '作者名称',
  `cover_image` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '封面图片',
  `tags` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '文档标签（逗号分隔）',
  `remark` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '文档备注',
  `status` tinyint NOT NULL DEFAULT '0' COMMENT '状态：0-草稿，1-已发布，2-已归档',
  `is_public` tinyint NOT NULL DEFAULT '1' COMMENT '是否公开：0-私有，1-公开',
  `is_top` tinyint NOT NULL DEFAULT '0' COMMENT '是否置顶：0-否，1-是',
  `is_recommend` tinyint NOT NULL DEFAULT '0' COMMENT '是否推荐：0-否，1-是',
  `allow_comment` tinyint NOT NULL DEFAULT '1' COMMENT '允许评论：0-否，1-是',
  `view_count` int NOT NULL DEFAULT '0' COMMENT '浏览次数',
  `like_count` int NOT NULL DEFAULT '0' COMMENT '点赞次数',
  `comment_count` int NOT NULL DEFAULT '0' COMMENT '评论次数',
  `favorite_count` int NOT NULL DEFAULT '0' COMMENT '收藏次数',
  `version` int NOT NULL DEFAULT '1' COMMENT '版本号',
  `word_count` int DEFAULT NULL COMMENT '字数统计',
  `document_type` tinyint DEFAULT '1' COMMENT '文档类型：1-文章，2-文件',
  `file_path` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '文件路径',
  `file_size` bigint DEFAULT NULL COMMENT '文件大小（字节）',
  `file_extension` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '文件扩展名',
  `mime_type` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT 'MIME类型',
  `source` tinyint DEFAULT '1' COMMENT '来源：1-原创，2-转载，3-翻译',
  `source_url` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '来源URL',
  `sort` int DEFAULT '0' COMMENT '排序',
  `publish_time` datetime DEFAULT NULL COMMENT '发布时间',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `create_by` bigint DEFAULT NULL COMMENT '创建人ID',
  `update_by` bigint DEFAULT NULL COMMENT '更新人ID',
  `deleted` tinyint NOT NULL DEFAULT '0' COMMENT '删除标识',
  `auto_save_dismissed` tinyint(1) DEFAULT '0' COMMENT '自动保存草稿已确认（0-未确认，1-用户已放弃恢复）',
  PRIMARY KEY (`id`),
  KEY `idx_title` (`title`(100)),
  KEY `idx_category_id` (`category_id`),
  KEY `idx_team_id` (`team_id`),
  KEY `idx_author_id` (`author_id`),
  KEY `idx_status` (`status`),
  KEY `idx_is_public` (`is_public`),
  KEY `idx_publish_time` (`publish_time`),
  KEY `idx_create_time` (`created_at`),
  FULLTEXT KEY `ft_content` (`title`,`summary`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='文档表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `kb_document`
--

LOCK TABLES `kb_document` WRITE;
/*!40000 ALTER TABLE `kb_document` DISABLE KEYS */;
INSERT INTO `kb_document` VALUES (327281558375501824,'JDK26有哪些新功能',NULL,'6a38a5341b6b877d115662d6',14,'',NULL,NULL,1000000000000000001,'admin',NULL,NULL,NULL,0,1,0,0,1,0,0,0,0,1,NULL,1,NULL,28,NULL,NULL,1,NULL,0,NULL,'2026-06-22 11:00:04','2026-06-22 18:19:56',1000000000000000001,1000000000000000001,0,1),(327281945643978752,'AgentScope2.0发布了',NULL,'6a38a5901b6b877d115662d7',18,'',NULL,NULL,1000000000000000001,'admin',NULL,NULL,NULL,0,1,0,0,1,0,0,0,0,1,NULL,1,NULL,24,NULL,NULL,1,NULL,0,NULL,'2026-06-22 11:01:36','2026-06-22 18:19:56',1000000000000000001,1000000000000000001,0,1),(327282048354095104,'AgentScope2.0发布了',NULL,'6a38a5b01b6b877d115662d8',14451,'',NULL,NULL,1000000000000000001,'admin',NULL,NULL,NULL,0,1,0,0,1,0,0,0,0,1,NULL,1,NULL,27499,NULL,NULL,1,NULL,0,NULL,'2026-06-22 11:02:01','2026-06-22 18:19:56',1000000000000000001,1000000000000000001,0,1),(327283159584608256,'AgentScope2.0发布了',NULL,'6a38a6b71b6b877d115662d9',14451,'',NULL,NULL,1000000000000000001,'admin',NULL,NULL,NULL,0,1,0,0,1,0,0,0,0,1,NULL,1,NULL,27499,NULL,NULL,1,NULL,0,NULL,'2026-06-22 11:06:26','2026-06-22 18:19:56',1000000000000000001,1000000000000000001,0,1),(327283237921624064,'AgentScope2.0发布了',NULL,'6a38a6c41b6b877d115662da',14663,'',6000000000000000001,NULL,1000000000000000001,'admin',NULL,'',NULL,1,0,0,0,1,2,0,0,0,1,NULL,1,NULL,27705,NULL,NULL,1,NULL,0,'2026-06-23 16:16:33','2026-06-22 11:06:45','2026-06-23 16:02:52',1000000000000000001,1000000000000000001,0,1),(327283313100328960,'AgentScope2.0发布了',NULL,'6a38a6d61b6b877d115662db',14663,'',6000000000000000001,8000000000000000001,1000000000000000001,'admin',NULL,'',NULL,1,1,0,0,1,2,0,0,0,1,NULL,1,NULL,27705,NULL,NULL,1,NULL,0,'2026-06-22 11:07:11','2026-06-22 11:07:02','2026-06-22 17:11:49',1000000000000000001,1000000000000000001,0,0),(327306488970350592,'全网爆火的Loop到底是什么？',NULL,'6a38bc6dba009160b90fe575',7567,'',6000000000000000001,8000000000000000001,1000000000000000001,'admin',NULL,'',NULL,1,0,0,0,1,5,0,0,0,1,NULL,1,NULL,16561,NULL,NULL,1,NULL,0,'2026-06-23 16:49:19','2026-06-22 12:39:08','2026-06-23 16:49:05',1000000000000000001,1000000000000000001,0,1),(327313369386323968,'测试',NULL,'6a38c2d4ba009160b90fe577',1,'',NULL,NULL,1000000000000000001,'admin',NULL,NULL,NULL,0,1,0,0,1,1,0,0,0,1,NULL,1,NULL,1,NULL,NULL,1,NULL,0,NULL,'2026-06-22 13:06:28','2026-06-22 18:18:46',1000000000000000001,1000000000000000001,1,0),(327352453118955520,'测试2',NULL,'6a38e73a4fd71a67c21708ab',7,'',NULL,NULL,1000000000000000001,'admin',NULL,NULL,NULL,0,1,0,0,1,2,0,0,0,1,NULL,1,NULL,17,NULL,NULL,1,NULL,0,NULL,'2026-06-22 15:41:47','2026-06-22 18:18:49',1000000000000000001,1000000000000000001,1,0),(327364749388025856,'PostgreSQL入门教程',NULL,'6a38f399b07c6b4633da3190',13227,'',NULL,NULL,1000000000000000001,'admin',NULL,NULL,NULL,0,1,0,0,1,0,0,0,0,1,NULL,1,NULL,20735,NULL,NULL,1,NULL,0,NULL,'2026-06-22 16:30:38','2026-06-22 18:18:59',1000000000000000001,1000000000000000001,1,0),(327366085009608704,'PostgreSQL入门教程',NULL,'6a38f3f6b07c6b4633da3197',13227,'',6000000000000000001,NULL,1000000000000000001,'admin',NULL,'',NULL,1,0,0,0,1,1,0,0,0,1,NULL,1,NULL,20735,NULL,NULL,1,NULL,0,'2026-06-22 17:57:30','2026-06-22 16:35:57','2026-06-22 17:57:20',1000000000000000001,1000000000000000001,0,0),(327392111123107840,'美团二面：高并发下如何保证接口幂等性？',NULL,'6a390c341856c4616b501e2e',5243,'',NULL,NULL,1000000000000000001,'admin',NULL,NULL,NULL,0,1,0,0,1,0,0,0,0,1,NULL,1,NULL,9497,NULL,NULL,1,NULL,0,NULL,'2026-06-22 18:19:22','2026-06-22 18:29:00',1000000000000000001,1000000000000000001,1,1),(327393448317554688,'美团二面：高并发下如何保证接口幂等性？',NULL,'6a390d693982334c296e446d',5243,'',6000000000000000001,8000000000000000002,1000000000000000001,'admin',NULL,'',NULL,1,1,0,0,1,1,0,0,0,1,NULL,1,NULL,9497,NULL,NULL,1,NULL,0,'2026-06-22 18:24:58','2026-06-22 18:24:41','2026-06-22 18:28:13',1000000000000000001,1000000000000000001,1,0),(327394591991009280,'美团二面：高并发下如何保证接口幂等性？',NULL,'6a390eab3982334c296e446f',5105,'',6000000000000000001,8000000000000000001,1000000000000000001,'admin',NULL,'',NULL,1,1,0,0,1,2,0,0,0,1,NULL,1,NULL,9359,NULL,NULL,1,NULL,0,'2026-06-22 18:33:22','2026-06-22 18:29:13','2026-06-22 18:30:28',1000000000000000001,1000000000000000001,0,0),(327395741289025536,'索引优化的10个高效技巧',NULL,'6a390f963982334c296e4471',6620,'',6000000000000000011,8000000000000000001,1000000000000000001,'admin',NULL,'',NULL,3,1,0,0,1,11,1,2,1,1,NULL,1,NULL,11436,NULL,NULL,1,NULL,0,'2026-06-23 16:16:27','2026-06-22 18:33:47','2026-06-23 16:39:00',1000000000000000001,1000000000000000001,0,0),(327396046936346624,'线上慢SQL导致CPU飙升，如何处理？',NULL,'6a390fd63982334c296e4473',3817,'',6000000000000000011,NULL,1000000000000000001,'admin',NULL,'',NULL,3,1,0,0,1,7,0,0,0,1,NULL,1,NULL,6899,NULL,NULL,1,NULL,0,'2026-06-23 16:38:12','2026-06-22 18:35:00','2026-06-23 16:45:32',1000000000000000001,1000000000000000001,0,1),(327405209401823232,'程序员最常用的10个AI提示词',NULL,'6a39185c3982334c296e4476',3527,'',6000000000000000011,8000000000000000001,1000000000000000001,'admin',NULL,'',NULL,3,1,0,0,1,9,0,0,0,1,NULL,1,NULL,7707,NULL,NULL,1,NULL,0,'2026-06-23 14:50:44','2026-06-22 19:11:25','2026-06-23 17:26:10',1000000000000000001,1000000000000000001,0,0),(1000000000000000001,'Spring Boot 3.x 快速入门指南',NULL,NULL,NULL,'Spring Boot 3.x完整入门教程，包含项目初始化、核心特性介绍和最佳实践。',6000000000000000011,NULL,1000000000000000004,'developer',NULL,NULL,NULL,1,1,0,0,1,1523,89,23,0,1,NULL,1,NULL,NULL,NULL,NULL,1,NULL,0,'2024-01-15 10:00:00','2026-06-19 10:24:37','2026-06-19 10:24:37',NULL,NULL,0,0),(1000000000000000002,'React 18 + TypeScript 最佳实践',NULL,NULL,NULL,'基于React 18和TypeScript的前端开发最佳实践，包含项目结构、核心概念和状态管理。',6000000000000000012,NULL,1000000000000000006,'designer',NULL,NULL,NULL,1,1,0,0,1,2187,156,45,0,1,NULL,1,NULL,NULL,NULL,NULL,1,NULL,0,'2024-02-10 14:30:00','2026-06-19 10:24:37','2026-06-19 10:24:37',NULL,NULL,0,0),(1000000000000000003,'MySQL 8.0 性能优化指南',NULL,NULL,NULL,'MySQL 8.0数据库性能优化完整指南，涵盖索引优化、查询优化和慢查询分析。',6000000000000000013,NULL,1000000000000000001,'admin',NULL,NULL,NULL,1,1,0,0,1,3425,235,67,1,1,NULL,1,NULL,NULL,NULL,NULL,1,NULL,0,'2024-01-28 09:15:00','2026-06-19 10:24:37','2026-06-23 16:17:09',NULL,NULL,0,0),(1000000000000000004,'Docker + Kubernetes 容器化部署',NULL,NULL,NULL,'基于Docker和Kubernetes的微服务容器化部署实践。',6000000000000000014,NULL,1000000000000000004,'developer',NULL,NULL,NULL,1,1,0,0,1,1876,98,19,0,1,NULL,1,NULL,NULL,NULL,NULL,1,NULL,0,'2024-03-05 16:20:00','2026-06-19 10:24:37','2026-06-19 10:24:37',NULL,NULL,0,0),(1000000000000000005,'企业知识库产品需求文档PRD',NULL,NULL,NULL,'完整的企业知识库产品需求文档，包含产品定位、目标用户和功能需求。',6000000000000000021,NULL,1000000000000000005,'product',NULL,NULL,NULL,1,1,0,0,1,987,45,12,0,1,NULL,1,NULL,NULL,NULL,NULL,1,NULL,0,'2024-02-01 10:00:00','2026-06-19 10:24:37','2026-06-19 10:24:37',NULL,NULL,0,0),(1000000000000000006,'UI设计规范 V2.0',NULL,NULL,NULL,'企业知识库UI设计规范，包含色彩系统、字体规范和组件规范。',6000000000000000022,NULL,1000000000000000006,'designer',NULL,NULL,NULL,1,1,0,0,1,654,34,8,0,1,NULL,1,NULL,NULL,NULL,NULL,1,NULL,0,'2024-02-15 14:00:00','2026-06-19 10:24:37','2026-06-19 10:24:37',NULL,NULL,0,0),(1000000000000000007,'文档审核流程规范',NULL,NULL,NULL,'文档审核流程的详细规范，包括流程步骤和审核标准。',6000000000000000003,NULL,1000000000000000002,'editor',NULL,NULL,NULL,1,1,0,0,1,1234,67,15,0,1,NULL,1,NULL,NULL,NULL,NULL,1,NULL,0,'2024-01-20 11:00:00','2026-06-19 10:24:37','2026-06-19 10:24:37',NULL,NULL,0,0),(1000000000000000008,'员工入职指南',NULL,NULL,NULL,'新员工入职指南，包含入职流程、常用系统和福利制度说明。',6000000000000000004,NULL,1000000000000000008,'hr',NULL,NULL,NULL,1,1,0,0,1,5678,234,56,0,1,NULL,1,NULL,NULL,NULL,NULL,1,NULL,0,'2024-01-01 09:00:00','2026-06-19 10:24:37','2026-06-19 10:24:37',NULL,NULL,0,0),(1000000000000000009,'报销流程说明',NULL,NULL,NULL,'公司费用报销流程的详细说明，包含报销原则、流程步骤和注意事项。',6000000000000000005,NULL,1000000000000000009,'finance',NULL,NULL,NULL,1,1,0,0,1,3456,123,34,0,1,NULL,1,NULL,NULL,NULL,NULL,1,NULL,0,'2024-01-10 14:00:00','2026-06-19 10:24:37','2026-06-19 10:24:37',NULL,NULL,0,0);
/*!40000 ALTER TABLE `kb_document` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `kb_document_access`
--

DROP TABLE IF EXISTS `kb_document_access`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `kb_document_access` (
  `id` bigint NOT NULL COMMENT '访问记录ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `document_id` bigint NOT NULL COMMENT '文档ID',
  `document_title` varchar(200) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '文档标题',
  `category_id` bigint DEFAULT NULL COMMENT '分类ID',
  `category_name` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '分类名称',
  `access_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '访问时间',
  `ip_address` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '访问IP地址',
  `user_agent` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '用户代理',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `created_by` bigint DEFAULT NULL COMMENT '创建人ID',
  `updated_by` bigint DEFAULT NULL COMMENT '更新人ID',
  `deleted` tinyint NOT NULL DEFAULT '0' COMMENT '删除标识',
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx_user_document` (`user_id`,`document_id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_access_time` (`access_time`),
  KEY `idx_document_id` (`document_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='文档访问记录表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `kb_document_access`
--

LOCK TABLES `kb_document_access` WRITE;
/*!40000 ALTER TABLE `kb_document_access` DISABLE KEYS */;
INSERT INTO `kb_document_access` VALUES (327375116176592896,1000000000000000001,327283313100328960,'AgentScope2.0发布了',NULL,NULL,'2026-06-22 17:11:50',NULL,NULL,'2026-06-22 17:11:50','2026-06-22 17:11:50',NULL,NULL,0),(327386570497855488,1000000000000000001,327366085009608704,'PostgreSQL入门教程',NULL,NULL,'2026-06-22 17:57:21',NULL,NULL,'2026-06-22 17:57:21','2026-06-22 17:57:21',NULL,NULL,0),(327394336033607680,1000000000000000001,327393448317554688,'美团二面：高并发下如何保证接口幂等性？',NULL,NULL,'2026-06-22 18:28:12',NULL,NULL,'2026-06-22 18:28:12','2026-06-22 18:28:12',NULL,NULL,0),(327394848581750784,1000000000000000001,327394591991009280,'美团二面：高并发下如何保证接口幂等性？',NULL,NULL,'2026-06-22 18:30:15',NULL,NULL,'2026-06-22 18:30:15','2026-06-22 18:30:15',NULL,NULL,0),(327720152017801216,1000000000000000001,327283237921624064,'AgentScope2.0发布了',NULL,NULL,'2026-06-23 16:02:53',NULL,NULL,'2026-06-23 16:02:53','2026-06-23 16:02:53',NULL,NULL,0),(327723742765649920,1000000000000000001,1000000000000000003,'MySQL 8.0 性能优化指南',NULL,NULL,'2026-06-23 16:17:09',NULL,NULL,'2026-06-23 16:17:09','2026-06-23 16:17:09',NULL,NULL,0),(327729244396457984,1000000000000000001,327395741289025536,'索引优化的10个高效技巧',NULL,NULL,'2026-06-23 16:39:01',NULL,NULL,'2026-06-23 16:39:01','2026-06-23 16:39:01',NULL,NULL,0),(327730887070453760,1000000000000000001,327396046936346624,'线上慢SQL导致CPU飙升，如何处理？',NULL,NULL,'2026-06-23 16:45:32',NULL,NULL,'2026-06-23 16:45:32','2026-06-23 16:45:32',NULL,NULL,0),(327731779735785472,1000000000000000001,327306488970350592,'全网爆火的Loop到底是什么？',NULL,NULL,'2026-06-23 16:49:05',NULL,NULL,'2026-06-23 16:49:05','2026-06-23 16:49:05',NULL,NULL,0),(327743606179893248,1000000000000000001,327405209401823232,'程序员最常用的10个AI提示词',NULL,NULL,'2026-06-23 17:36:05',NULL,NULL,'2026-06-23 17:36:05','2026-06-23 17:36:05',NULL,NULL,0);
/*!40000 ALTER TABLE `kb_document_access` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `kb_document_review`
--

DROP TABLE IF EXISTS `kb_document_review`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `kb_document_review` (
  `id` bigint NOT NULL COMMENT '审核ID',
  `document_id` bigint NOT NULL COMMENT '文档ID',
  `document_title` varchar(200) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '文档标题（冗余字段）',
  `submitter_id` bigint NOT NULL COMMENT '提交人ID',
  `submitter_name` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '提交人姓名（冗余字段）',
  `reviewer_id` bigint DEFAULT NULL COMMENT '审核人ID',
  `reviewer_name` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '审核人姓名（冗余字段）',
  `status` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'pending' COMMENT '状态',
  `comment` text COLLATE utf8mb4_unicode_ci COMMENT '审核意见',
  `submit_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '提交时间',
  `review_time` datetime DEFAULT NULL COMMENT '审核时间',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_document_id` (`document_id`),
  KEY `idx_submitter_id` (`submitter_id`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='文档审核表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `kb_document_review`
--

LOCK TABLES `kb_document_review` WRITE;
/*!40000 ALTER TABLE `kb_document_review` DISABLE KEYS */;
/*!40000 ALTER TABLE `kb_document_review` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `kb_document_share`
--

DROP TABLE IF EXISTS `kb_document_share`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `kb_document_share` (
  `id` bigint NOT NULL COMMENT '主键ID（雪花算法）',
  `share_id` varchar(32) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '分享ID（唯一标识）',
  `document_id` bigint NOT NULL COMMENT '文档ID',
  `title` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '分享标题',
  `share_type` tinyint DEFAULT '1' COMMENT '分享类型：1-公开链接，2-私信分享',
  `share_code` varchar(32) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '分享码',
  `expire_type` tinyint DEFAULT '1' COMMENT '有效期类型：1-永久，2-限时',
  `expire_time` datetime DEFAULT NULL COMMENT '过期时间',
  `access_limit` int DEFAULT '0' COMMENT '访问次数限制（0-不限制）',
  `access_count` int DEFAULT '0' COMMENT '已访问次数',
  `require_password` tinyint DEFAULT '0' COMMENT '是否需要密码：0-否，1-是',
  `password` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '访问密码（MD5加密）',
  `sharer_id` bigint DEFAULT NULL COMMENT '分享人ID',
  `sharer_name` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '分享人名称',
  `description` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '分享描述',
  `status` tinyint DEFAULT '0' COMMENT '状态：0-有效，1-已失效，2-已删除',
  `share_time` datetime DEFAULT NULL COMMENT '分享时间',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `create_by` bigint DEFAULT NULL COMMENT '创建人ID',
  `update_by` bigint DEFAULT NULL COMMENT '更新人ID',
  `deleted` tinyint DEFAULT '0' COMMENT '逻辑删除标记',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_share_id` (`share_id`),
  KEY `idx_document_id` (`document_id`),
  KEY `idx_sharer_id` (`sharer_id`),
  KEY `idx_status` (`status`),
  KEY `idx_share_time` (`share_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='文档分享表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `kb_document_share`
--

LOCK TABLES `kb_document_share` WRITE;
/*!40000 ALTER TABLE `kb_document_share` DISABLE KEYS */;
/*!40000 ALTER TABLE `kb_document_share` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `kb_document_tag`
--

DROP TABLE IF EXISTS `kb_document_tag`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `kb_document_tag` (
  `id` bigint NOT NULL COMMENT '主键ID',
  `document_id` bigint NOT NULL COMMENT '文档ID',
  `tag_id` bigint NOT NULL COMMENT '标签ID',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_document_tag` (`document_id`,`tag_id`),
  KEY `idx_tag_id` (`tag_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='文档标签关联表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `kb_document_tag`
--

LOCK TABLES `kb_document_tag` WRITE;
/*!40000 ALTER TABLE `kb_document_tag` DISABLE KEYS */;
INSERT INTO `kb_document_tag` VALUES (1100000000000000001,1000000000000000001,7000000000000000006,'2026-06-19 10:24:37'),(1100000000000000002,1000000000000000001,7000000000000000005,'2026-06-19 10:24:37'),(1100000000000000003,1000000000000000001,7000000000000000011,'2026-06-19 10:24:37'),(1100000000000000004,1000000000000000002,7000000000000000007,'2026-06-19 10:24:37'),(1100000000000000005,1000000000000000002,7000000000000000012,'2026-06-19 10:24:37'),(1100000000000000006,1000000000000000003,7000000000000000008,'2026-06-19 10:24:37'),(1100000000000000007,1000000000000000003,7000000000000000009,'2026-06-19 10:24:37'),(1100000000000000008,1000000000000000004,7000000000000000010,'2026-06-19 10:24:37'),(1100000000000000009,1000000000000000004,7000000000000000005,'2026-06-19 10:24:37');
/*!40000 ALTER TABLE `kb_document_tag` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `kb_document_version`
--

DROP TABLE IF EXISTS `kb_document_version`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `kb_document_version` (
  `id` bigint NOT NULL COMMENT '版本ID',
  `document_id` bigint NOT NULL COMMENT '文档ID',
  `version` int NOT NULL COMMENT '版本号',
  `title` varchar(200) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '文档标题',
  `content` longtext COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '文档内容',
  `change_log` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '变更说明',
  `author_id` bigint NOT NULL COMMENT '作者ID',
  `author_name` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '作者姓名',
  `is_current` tinyint NOT NULL DEFAULT '0' COMMENT '是否当前版本',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_doc_version` (`document_id`,`version`),
  KEY `idx_document_id` (`document_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='文档版本表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `kb_document_version`
--

LOCK TABLES `kb_document_version` WRITE;
/*!40000 ALTER TABLE `kb_document_version` DISABLE KEYS */;
/*!40000 ALTER TABLE `kb_document_version` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `kb_file_metadata`
--

DROP TABLE IF EXISTS `kb_file_metadata`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `kb_file_metadata` (
  `id` bigint NOT NULL COMMENT '文件ID',
  `file_name` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '文件名称',
  `original_file_name` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '文件原始名称',
  `file_extension` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '文件扩展名',
  `file_size` bigint DEFAULT NULL COMMENT '文件大小（字节）',
  `content_type` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '文件类型（MIME类型）',
  `storage_path` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '文件存储路径',
  `access_url` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '文件访问URL',
  `file_category` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '文件分类（image, document, video, audio, other）',
  `uploader_id` bigint DEFAULT NULL COMMENT '上传用户ID',
  `uploader_name` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '上传用户名称',
  `file_md5` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '文件MD5',
  `file_sha256` varchar(128) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '文件SHA256',
  `width` int DEFAULT NULL COMMENT '文件宽度（图片）',
  `height` int DEFAULT NULL COMMENT '文件高度（图片）',
  `thumbnail_url` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '缩略图URL',
  `is_public` tinyint DEFAULT '1' COMMENT '是否公开',
  `download_count` int DEFAULT '0' COMMENT '下载次数',
  `last_access_time` datetime DEFAULT NULL COMMENT '最后访问时间',
  `upload_status` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT 'completed' COMMENT '文件状态（uploading, completed, failed）',
  `error_message` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '错误信息',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `create_by` bigint DEFAULT NULL COMMENT '创建人',
  `update_by` bigint DEFAULT NULL COMMENT '更新人',
  `deleted` tinyint NOT NULL DEFAULT '0' COMMENT '删除标识',
  PRIMARY KEY (`id`),
  KEY `idx_uploader_id` (`uploader_id`),
  KEY `idx_file_category` (`file_category`),
  KEY `idx_created_at` (`created_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='文件元数据表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `kb_file_metadata`
--

LOCK TABLES `kb_file_metadata` WRITE;
/*!40000 ALTER TABLE `kb_file_metadata` DISABLE KEYS */;
INSERT INTO `kb_file_metadata` VALUES (327705310393405440,'未命名绘图-第 23 页.drawio.png','未命名绘图-第 23 页.drawio.png','png',6506368,'image/png','http://117.72.88.11:9091/knowledge-dev/2026/06/23/a9/a96be6052c94ae696f6e5887e2148524de269f5350ceecdb08e3b5d7708639ca.png','http://117.72.88.11:9091/knowledge-dev/2026/06/23/a9/a96be6052c94ae696f6e5887e2148524de269f5350ceecdb08e3b5d7708639ca.png','image',1000000000000000001,'admin','d42aa41034ace8b2f344c77dce30306e','a96be6052c94ae696f6e5887e2148524de269f5350ceecdb08e3b5d7708639ca',298,1940,NULL,0,1,NULL,'completed',NULL,'2026-06-23 15:03:57','2026-06-23 15:03:57',1000000000000000001,1000000000000000001,0);
/*!40000 ALTER TABLE `kb_file_metadata` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `kb_tag`
--

DROP TABLE IF EXISTS `kb_tag`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `kb_tag` (
  `id` bigint NOT NULL COMMENT '标签ID',
  `tag_name` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '标签名称',
  `tag_color` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT '#1890ff' COMMENT '标签颜色',
  `description` varchar(200) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '标签描述',
  `use_count` int NOT NULL DEFAULT '0' COMMENT '使用次数',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `create_by` bigint DEFAULT NULL COMMENT '创建人',
  `deleted` tinyint NOT NULL DEFAULT '0' COMMENT '删除标识',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_tag_name` (`tag_name`,`deleted`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='文档标签表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `kb_tag`
--

LOCK TABLES `kb_tag` WRITE;
/*!40000 ALTER TABLE `kb_tag` DISABLE KEYS */;
INSERT INTO `kb_tag` VALUES (7000000000000000001,'重要','#ff4d4f','重要文档标签',0,'2026-06-18 20:30:10','2026-06-18 20:30:10',NULL,0),(7000000000000000002,'置顶','#1890ff','置顶文档标签',0,'2026-06-18 20:30:10','2026-06-18 20:30:10',NULL,0),(7000000000000000003,'推荐','#52c41a','推荐文档标签',0,'2026-06-18 20:30:10','2026-06-18 20:30:10',NULL,0),(7000000000000000004,'草稿','#d9d9d9','草稿文档标签',0,'2026-06-18 20:30:10','2026-06-18 20:30:10',NULL,0),(7000000000000000005,'Java','#b07219','Java技术标签',0,'2026-06-18 20:30:10','2026-06-18 20:30:10',NULL,0),(7000000000000000006,'Spring Boot','#6db33f','Spring Boot标签',0,'2026-06-18 20:30:10','2026-06-18 20:30:10',NULL,0),(7000000000000000007,'React','#61dafb','React前端标签',0,'2026-06-18 20:30:10','2026-06-18 20:30:10',NULL,0),(7000000000000000008,'MySQL','#4479a1','MySQL数据库标签',0,'2026-06-18 20:30:10','2026-06-18 20:30:10',NULL,0),(7000000000000000009,'Redis','#dc382d','Redis缓存标签',0,'2026-06-18 20:30:10','2026-06-18 20:30:10',NULL,0),(7000000000000000010,'Docker','#2496ed','Docker容器标签',0,'2026-06-18 20:30:10','2026-06-18 20:30:10',NULL,0),(7000000000000000011,'架构','#722ed1','系统架构标签',0,'2026-06-18 20:30:10','2026-06-18 20:30:10',NULL,0),(7000000000000000012,'规范','#fa8c16','开发规范标签',0,'2026-06-18 20:30:10','2026-06-18 20:30:10',NULL,0);
/*!40000 ALTER TABLE `kb_tag` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `kb_user_favorite`
--

DROP TABLE IF EXISTS `kb_user_favorite`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `kb_user_favorite` (
  `id` bigint NOT NULL COMMENT '收藏ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `document_id` bigint NOT NULL COMMENT '文档ID',
  `document_title` varchar(200) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '文档标题',
  `document_category_id` bigint DEFAULT NULL COMMENT '文档分类ID',
  `favorite_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '收藏时间',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint NOT NULL DEFAULT '0' COMMENT '删除标识',
  `create_by` bigint DEFAULT NULL COMMENT '创建人',
  `update_by` bigint DEFAULT NULL COMMENT '更新人',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_document` (`user_id`,`document_id`,`deleted`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_document_id` (`document_id`),
  KEY `idx_favorite_time` (`favorite_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户收藏表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `kb_user_favorite`
--

LOCK TABLES `kb_user_favorite` WRITE;
/*!40000 ALTER TABLE `kb_user_favorite` DISABLE KEYS */;
INSERT INTO `kb_user_favorite` VALUES (327391878150492160,1000000000000000001,1000000000000000003,'MySQL 8.0 性能优化指南',6000000000000000013,'2026-06-22 18:18:26','2026-06-22 18:18:26','2026-06-22 18:18:26',0,1000000000000000001,NULL),(327701221408378880,1000000000000000001,327395741289025536,'索引优化的10个高效技巧',6000000000000000011,'2026-06-23 14:47:39','2026-06-23 14:47:40','2026-06-23 14:47:40',0,1000000000000000001,NULL);
/*!40000 ALTER TABLE `kb_user_favorite` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `tb_comment`
--

DROP TABLE IF EXISTS `tb_comment`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `tb_comment` (
  `id` bigint NOT NULL COMMENT '评论ID',
  `document_id` bigint NOT NULL COMMENT '文档ID',
  `content` text COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '评论内容',
  `user_id` bigint NOT NULL COMMENT '评论用户ID',
  `user_name` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '评论用户姓名',
  `user_avatar` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '评论用户头像',
  `parent_id` bigint NOT NULL DEFAULT '0' COMMENT '父评论ID',
  `reply_to_id` bigint DEFAULT NULL COMMENT '回复的评论ID',
  `reply_to_name` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '回复的用户名',
  `like_count` int NOT NULL DEFAULT '0' COMMENT '点赞数',
  `reply_count` int NOT NULL DEFAULT '0' COMMENT '回复数',
  `status` tinyint NOT NULL DEFAULT '1' COMMENT '状态：0-隐藏，1-显示',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `create_by` bigint DEFAULT NULL COMMENT '创建人',
  `update_by` bigint DEFAULT NULL COMMENT '更新人',
  `deleted` tinyint NOT NULL DEFAULT '0' COMMENT '删除标识',
  PRIMARY KEY (`id`),
  KEY `idx_document_id` (`document_id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_parent_id` (`parent_id`),
  KEY `idx_create_time` (`created_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='文档评论表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `tb_comment`
--

LOCK TABLES `tb_comment` WRITE;
/*!40000 ALTER TABLE `tb_comment` DISABLE KEYS */;
INSERT INTO `tb_comment` VALUES (327701177024253952,327395741289025536,'11',1000000000000000001,'admin','https://api.dicebear.com/7.x/avataaars/svg?seed=admin',0,NULL,NULL,0,1,1,'2026-06-23 14:47:29','2026-06-23 14:47:36',1000000000000000001,NULL,0),(327701206740897792,327395741289025536,'222',1000000000000000001,'admin','https://api.dicebear.com/7.x/avataaars/svg?seed=admin',327701177024253952,NULL,NULL,0,0,1,'2026-06-23 14:47:36','2026-06-23 14:47:36',1000000000000000001,NULL,0),(1200000000000000001,1000000000000000001,'这篇文章写得很详细，对我帮助很大！',1000000000000000002,'editor',NULL,0,NULL,NULL,12,0,1,'2026-06-19 10:24:37','2026-06-19 10:24:37',NULL,NULL,0),(1200000000000000002,1000000000000000001,'补充一点：自动配置的原理可以再详细讲讲',1000000000000000004,'developer',NULL,0,NULL,NULL,5,0,1,'2026-06-19 10:24:37','2026-06-19 10:24:37',NULL,NULL,0),(1200000000000000003,1000000000000000002,'TypeScript的类型定义很规范，学习了！',1000000000000000003,'tester',NULL,0,NULL,NULL,8,0,1,'2026-06-19 10:24:37','2026-06-19 10:24:37',NULL,NULL,0),(1200000000000000004,1000000000000000002,'期待出下一期关于Hooks的文章',1000000000000000002,'editor',NULL,0,NULL,NULL,3,0,1,'2026-06-19 10:24:37','2026-06-19 10:24:37',NULL,NULL,0),(1200000000000000005,1000000000000000003,'索引优化的技巧很实用，已经在项目中应用了',1000000000000000005,'product',NULL,0,NULL,NULL,15,0,1,'2026-06-19 10:24:37','2026-06-19 10:24:37',NULL,NULL,0),(1200000000000000006,1000000000000000005,'PRD写得很清楚，产品逻辑很完整',1000000000000000001,'admin',NULL,0,NULL,NULL,6,0,1,'2026-06-19 10:24:37','2026-06-19 10:24:37',NULL,NULL,0),(1200000000000000007,1000000000000000008,'入职指南很详细，帮助我快速熟悉了公司',1000000000000000003,'tester',NULL,0,NULL,NULL,23,0,1,'2026-06-19 10:24:37','2026-06-19 10:24:37',NULL,NULL,0),(1200000000000000008,1000000000000000008,'建议补充一下远程办公的注意事项',1000000000000000007,'sales',NULL,0,NULL,NULL,2,0,1,'2026-06-19 10:24:37','2026-06-19 10:24:37',NULL,NULL,0);
/*!40000 ALTER TABLE `tb_comment` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `tb_document_review`
--

DROP TABLE IF EXISTS `tb_document_review`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `tb_document_review` (
  `id` bigint NOT NULL COMMENT '审核ID',
  `document_id` bigint NOT NULL COMMENT '文档ID',
  `reviewer_id` bigint DEFAULT NULL COMMENT '审核人ID',
  `reviewer_name` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '审核人姓名',
  `review_result` int DEFAULT NULL COMMENT '审核结果：1-通过，2-驳回',
  `review_round` int DEFAULT '1' COMMENT '审核轮次',
  `review_comment` text COLLATE utf8mb4_unicode_ci COMMENT '审核意见',
  `before_status` int DEFAULT NULL COMMENT '审核前状态',
  `reviewed_at` datetime DEFAULT NULL COMMENT '审核时间',
  `review_level` int DEFAULT '1' COMMENT '审核级别',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_document_id` (`document_id`),
  KEY `idx_reviewer_id` (`reviewer_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='文档审核表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `tb_document_review`
--

LOCK TABLES `tb_document_review` WRITE;
/*!40000 ALTER TABLE `tb_document_review` DISABLE KEYS */;
INSERT INTO `tb_document_review` VALUES (327283315486887936,327283313100328960,1000000000000000001,'admin',1,1,NULL,0,'2026-06-22 11:07:11',1,'2026-06-22 11:07:03'),(327386554274287616,327366085009608704,1000000000000000001,'admin',1,1,NULL,0,'2026-06-22 17:57:30',1,'2026-06-22 17:57:17'),(327393466617303040,327393448317554688,1000000000000000001,'admin',1,1,NULL,0,'2026-06-22 18:24:58',1,'2026-06-22 18:24:45'),(327394904475045888,327394591991009280,1000000000000000001,'admin',1,1,NULL,0,'2026-06-22 18:33:22',1,'2026-06-22 18:30:28'),(327395870851076096,327395741289025536,1000000000000000001,'admin',1,1,NULL,0,'2026-06-22 18:34:33',1,'2026-06-22 18:34:18'),(327406738288545792,327405209401823232,1000000000000000001,'admin',1,1,NULL,0,'2026-06-23 14:50:44',1,'2026-06-22 19:17:29'),(327702491993083904,327395741289025536,1000000000000000001,'admin',1,2,NULL,1,'2026-06-23 14:56:40',1,'2026-06-23 14:52:43'),(327705605013901312,327395741289025536,1000000000000000001,'admin',1,3,NULL,1,'2026-06-23 15:12:28',1,'2026-06-23 15:05:05'),(327708822393196544,327395741289025536,1000000000000000001,'admin',1,4,NULL,1,'2026-06-23 15:51:27',1,'2026-06-23 15:17:52'),(327717626258264064,327396046936346624,1000000000000000001,'admin',1,1,NULL,0,'2026-06-23 15:53:25',1,'2026-06-23 15:52:51'),(327717927476400128,327306488970350592,1000000000000000001,'admin',1,1,NULL,0,'2026-06-23 16:16:38',1,'2026-06-23 15:54:03'),(327719942508122112,327306488970350592,1000000000000000001,'admin',1,2,NULL,0,'2026-06-23 16:16:36',1,'2026-06-23 16:02:03'),(327720138084323328,327283237921624064,1000000000000000001,'admin',1,1,NULL,0,'2026-06-23 16:16:33',1,'2026-06-23 16:02:50'),(327721532711374848,327396046936346624,1000000000000000001,'admin',1,2,NULL,1,'2026-06-23 16:16:30',1,'2026-06-23 16:08:22'),(327723370143682560,327395741289025536,1000000000000000001,'admin',1,5,NULL,1,'2026-06-23 16:16:27',1,'2026-06-23 16:15:40'),(327723804149288960,327396046936346624,1000000000000000001,'admin',1,3,NULL,1,'2026-06-23 16:38:11',1,'2026-06-23 16:17:24'),(327723994507776000,327306488970350592,1000000000000000001,'admin',1,3,NULL,1,'2026-06-23 16:38:07',1,'2026-06-23 16:18:09'),(327729228948836352,327395741289025536,NULL,NULL,NULL,6,NULL,1,NULL,1,'2026-06-23 16:38:57'),(327730654580183040,327405209401823232,NULL,NULL,NULL,2,NULL,1,NULL,1,'2026-06-23 16:44:37'),(327730871610249216,327396046936346624,NULL,NULL,NULL,4,NULL,1,NULL,1,'2026-06-23 16:45:29'),(327731760110637056,327306488970350592,1000000000000000001,'admin',1,4,NULL,1,'2026-06-23 16:49:19',1,'2026-06-23 16:49:01'),(327732840877920256,327405209401823232,NULL,NULL,NULL,3,NULL,0,NULL,1,'2026-06-23 16:53:18'),(327733166364299264,327405209401823232,NULL,NULL,NULL,4,NULL,0,NULL,1,'2026-06-23 16:54:36');
/*!40000 ALTER TABLE `tb_document_review` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `tb_document_version`
--

DROP TABLE IF EXISTS `tb_document_version`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `tb_document_version` (
  `id` bigint NOT NULL COMMENT '版本ID',
  `document_id` bigint NOT NULL COMMENT '文档ID',
  `version` int NOT NULL COMMENT '版本号',
  `title` varchar(200) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '文档标题',
  `content` longtext COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '文档内容',
  `summary` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '文档摘要',
  `change_description` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '版本变更说明',
  `change_size` bigint DEFAULT NULL COMMENT '变更大小(字节)',
  `operator_id` bigint DEFAULT NULL COMMENT '操作人ID',
  `operator_name` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '操作人姓名',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_document_id` (`document_id`),
  KEY `idx_operator_id` (`operator_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='文档版本表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `tb_document_version`
--

LOCK TABLES `tb_document_version` WRITE;
/*!40000 ALTER TABLE `tb_document_version` DISABLE KEYS */;
/*!40000 ALTER TABLE `tb_document_version` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `tb_like`
--

DROP TABLE IF EXISTS `tb_like`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `tb_like` (
  `id` bigint NOT NULL COMMENT '点赞ID',
  `target_id` bigint NOT NULL COMMENT '目标ID（文档或评论）',
  `target_type` tinyint NOT NULL COMMENT '目标类型：1-文档，2-评论',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_target_user_type` (`target_id`,`user_id`,`target_type`),
  KEY `idx_target_id` (`target_id`),
  KEY `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='点赞表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `tb_like`
--

LOCK TABLES `tb_like` WRITE;
/*!40000 ALTER TABLE `tb_like` DISABLE KEYS */;
INSERT INTO `tb_like` VALUES (327391882831335424,1000000000000000003,1,1000000000000000001,'2026-06-22 18:18:27'),(327701226521235456,327395741289025536,1,1000000000000000001,'2026-06-23 14:47:40');
/*!40000 ALTER TABLE `tb_like` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `tb_tag`
--

DROP TABLE IF EXISTS `tb_tag`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `tb_tag` (
  `id` bigint NOT NULL COMMENT '标签ID',
  `tag_name` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '标签名称',
  `tag_color` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT '#1890ff' COMMENT '标签颜色',
  `description` varchar(200) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '标签描述',
  `use_count` int NOT NULL DEFAULT '0' COMMENT '使用次数',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `create_by` bigint DEFAULT NULL COMMENT '创建人',
  `update_by` bigint DEFAULT NULL COMMENT '更新人',
  `deleted` tinyint NOT NULL DEFAULT '0' COMMENT '删除标识',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_tag_name` (`tag_name`,`deleted`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='文档标签表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `tb_tag`
--

LOCK TABLES `tb_tag` WRITE;
/*!40000 ALTER TABLE `tb_tag` DISABLE KEYS */;
INSERT INTO `tb_tag` VALUES (7000000000000000001,'重要','#ff4d4f','重要文档标签',0,'2026-06-19 10:24:37','2026-06-19 10:24:37',NULL,NULL,0),(7000000000000000002,'置顶','#1890ff','置顶文档标签',0,'2026-06-19 10:24:37','2026-06-19 10:24:37',NULL,NULL,0),(7000000000000000003,'推荐','#52c41a','推荐文档标签',0,'2026-06-19 10:24:37','2026-06-19 10:24:37',NULL,NULL,0),(7000000000000000004,'草稿','#d9d9d9','草稿文档标签',0,'2026-06-19 10:24:37','2026-06-19 10:24:37',NULL,NULL,0),(7000000000000000005,'Java','#b07219','Java技术标签',0,'2026-06-19 10:24:37','2026-06-19 10:24:37',NULL,NULL,0),(7000000000000000006,'Spring Boot','#6db33f','Spring Boot标签',0,'2026-06-19 10:24:37','2026-06-19 10:24:37',NULL,NULL,0),(7000000000000000007,'React','#61dafb','React前端标签',0,'2026-06-19 10:24:37','2026-06-19 10:24:37',NULL,NULL,0),(7000000000000000008,'MySQL','#4479a1','MySQL数据库标签',0,'2026-06-19 10:24:37','2026-06-19 10:24:37',NULL,NULL,0),(7000000000000000009,'Redis','#dc382d','Redis缓存标签',0,'2026-06-19 10:24:37','2026-06-19 10:24:37',NULL,NULL,0),(7000000000000000010,'Docker','#2496ed','Docker容器标签',0,'2026-06-19 10:24:37','2026-06-19 10:24:37',NULL,NULL,0),(7000000000000000011,'架构','#722ed1','系统架构标签',0,'2026-06-19 10:24:37','2026-06-19 10:24:37',NULL,NULL,0),(7000000000000000012,'规范','#fa8c16','开发规范标签',0,'2026-06-19 10:24:37','2026-06-19 10:24:37',NULL,NULL,0);
/*!40000 ALTER TABLE `tb_tag` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping routines for database 'kb_document'
--

--
-- Current Database: `kb_ai`
--

CREATE DATABASE /*!32312 IF NOT EXISTS*/ `kb_ai` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci */ /*!80016 DEFAULT ENCRYPTION='N' */;

USE `kb_ai`;

--
-- Table structure for table `ai_feedback`
--

DROP TABLE IF EXISTS `ai_feedback`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `ai_feedback` (
  `id` bigint NOT NULL COMMENT '反馈ID',
  `conversation_id` bigint NOT NULL COMMENT '对话ID',
  `message_id` bigint NOT NULL COMMENT '消息ID',
  `user_id` BIGINT NOT NULL COMMENT '用户ID',
  `feedback_type` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '反馈类型：like-点赞，dislike-点踩',
  `feedback_content` TEXT COMMENT '反馈内容',
  `rating` INT COMMENT '评分（1-5分）',
  `comment` text COLLATE utf8mb4_unicode_ci COMMENT '反馈意见',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `deleted` tinyint NOT NULL DEFAULT '0' COMMENT '删除标识',
  PRIMARY KEY (`id`),
  KEY `idx_conversation_id` (`conversation_id`),
  KEY `idx_message_id` (`message_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='AI反馈表';
/*!40101 SET character_set_client = @saved_cs_client */;



--
-- Dumping data for table `ai_feedback`
--

LOCK TABLES `ai_feedback` WRITE;
/*!40000 ALTER TABLE `ai_feedback` DISABLE KEYS */;
/*!40000 ALTER TABLE `ai_feedback` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `conversation`
--

DROP TABLE IF EXISTS `conversation`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `conversation` (
  `id` bigint NOT NULL COMMENT '对话ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `user_name` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '用户名称',
  `title` varchar(200) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '对话标题',
  `model` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'qwen' COMMENT '模型名称',
  `system_prompt` text COLLATE utf8mb4_unicode_ci COMMENT '系统提示词',
  `model_name` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT 'qwen' COMMENT 'AI模型名称',
  `message_count` int DEFAULT '0' COMMENT '消息数量',
  `tokens_used` int DEFAULT '0' COMMENT 'Token使用量',
  `status` tinyint DEFAULT '0' COMMENT '状态（0-进行中，1-已结束）',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint NOT NULL DEFAULT '0' COMMENT '删除标识',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_update_time` (`updated_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='AI对话表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `conversation`
--

LOCK TABLES `conversation` WRITE;
/*!40000 ALTER TABLE `conversation` DISABLE KEYS */;
INSERT INTO `conversation` VALUES (327699310567362560,1000000000000000001,NULL,'如何使用企业知识库？','qwen',NULL,'qwen',0,863,0,'2026-06-23 14:40:04','2026-06-23 14:40:21',0),(1600000000000000001,1000000000000000001,'admin','关于Spring Boot的讨论','qwen-turbo',NULL,'qwen-turbo',2,0,0,'2026-06-19 10:24:37','2026-06-23 14:38:28',0),(1600000000000000002,1000000000000000002,'editor','前端开发问题咨询','qwen-turbo',NULL,'qwen-turbo',2,0,0,'2026-06-19 10:24:37','2026-06-23 14:38:28',0),(1600000000000000003,1000000000000000004,'developer','数据库优化建议','qwen-turbo',NULL,'qwen-turbo',2,0,0,'2026-06-19 10:24:37','2026-06-23 14:38:28',0);
/*!40000 ALTER TABLE `conversation` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `kb_ai_conversation`
--

DROP TABLE IF EXISTS `kb_ai_conversation`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `kb_ai_conversation` (
  `id` bigint NOT NULL COMMENT '对话ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `user_name` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '用户姓名（冗余字段）',
  `title` varchar(200) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '对话标题',
  `model_name` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT 'qwen-turbo' COMMENT 'AI模型名称',
  `message_count` int NOT NULL DEFAULT '0' COMMENT '消息数量',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_update_time` (`updated_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='AI对话表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `kb_ai_conversation`
--

LOCK TABLES `kb_ai_conversation` WRITE;
/*!40000 ALTER TABLE `kb_ai_conversation` DISABLE KEYS */;
INSERT INTO `kb_ai_conversation` VALUES (1600000000000000001,1000000000000001,'admin','关于Spring Boot的讨论','qwen-turbo',2,'2026-06-18 20:30:11','2026-06-18 20:30:11'),(1600000000000000002,1000000000000002,'editor','前端开发问题咨询','qwen-turbo',2,'2026-06-18 20:30:11','2026-06-18 20:30:11'),(1600000000000000003,1000000000000004,'developer','数据库优化建议','qwen-turbo',2,'2026-06-18 20:30:11','2026-06-18 20:30:11');
/*!40000 ALTER TABLE `kb_ai_conversation` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `kb_ai_feedback`
--

DROP TABLE IF EXISTS `kb_ai_feedback`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `kb_ai_feedback` (
  `id` bigint NOT NULL COMMENT '反馈ID',
  `conversation_id` bigint NOT NULL COMMENT '对话ID',
  `message_id` bigint NOT NULL COMMENT '消息ID',
  `feedback_type` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '反馈类型：like/dislike',
  `comment` text COLLATE utf8mb4_unicode_ci COMMENT '反馈意见',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_conversation_id` (`conversation_id`),
  KEY `idx_message_id` (`message_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='AI反馈表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `kb_ai_feedback`
--

LOCK TABLES `kb_ai_feedback` WRITE;
/*!40000 ALTER TABLE `kb_ai_feedback` DISABLE KEYS */;
/*!40000 ALTER TABLE `kb_ai_feedback` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `kb_ai_message`
--

DROP TABLE IF EXISTS `kb_ai_message`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `kb_ai_message` (
  `id` bigint NOT NULL COMMENT '消息ID',
  `conversation_id` bigint NOT NULL COMMENT '对话ID',
  `role` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '角色：user/assistant/system',
  `content` longtext COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '消息内容',
  `tokens` int DEFAULT NULL COMMENT 'Token数量',
  `model_name` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '使用的模型',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_conversation_id` (`conversation_id`),
  KEY `idx_create_time` (`created_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='AI消息表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `kb_ai_message`
--

LOCK TABLES `kb_ai_message` WRITE;
/*!40000 ALTER TABLE `kb_ai_message` DISABLE KEYS */;
INSERT INTO `kb_ai_message` VALUES (1700000000000000001,1600000000000000001,'user','Spring Boot自动配置的原理是什么？',20,NULL,'2026-06-18 20:30:11'),(1700000000000000002,1600000000000000001,'assistant','Spring Boot的自动配置是通过条件注解(@ConditionalOnClass、@ConditionalOnMissingBean等)实现的。它会根据类路径中的jar包和已定义的Bean来决定是否加载某个配置...',150,NULL,'2026-06-18 20:30:11'),(1700000000000000003,1600000000000000002,'user','React 18的新特性有哪些？',18,NULL,'2026-06-18 20:30:11'),(1700000000000000004,1600000000000000002,'assistant','React 18的主要新特性包括：1. 并发渲染 2. 自动批处理 3. Transitions 4. Suspense改进...',120,NULL,'2026-06-18 20:30:11'),(1700000000000000005,1600000000000000003,'user','如何优化MySQL查询性能？',15,NULL,'2026-06-18 20:30:11'),(1700000000000000006,1600000000000000003,'assistant','MySQL查询优化可以从以下几个方面入手：1. 索引优化 2. 查询语句优化 3. 表结构优化 4. 参数调优...',135,NULL,'2026-06-18 20:30:11');
/*!40000 ALTER TABLE `kb_ai_message` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `message`
--

DROP TABLE IF EXISTS `message`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `message` (
  `id` bigint NOT NULL COMMENT '消息ID',
  `conversation_id` bigint NOT NULL COMMENT '对话ID',
  `role` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '角色：user-用户，assistant-助手，system-系统',
  `content` longtext COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '消息内容',
  `tokens` int DEFAULT NULL COMMENT 'Token数量',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `deleted` tinyint NOT NULL DEFAULT '0' COMMENT '删除标识',
  PRIMARY KEY (`id`),
  KEY `idx_conversation_id` (`conversation_id`),
  KEY `idx_create_time` (`created_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='AI消息表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `message`
--

LOCK TABLES `message` WRITE;
/*!40000 ALTER TABLE `message` DISABLE KEYS */;
INSERT INTO `message` VALUES (327699314262544384,327699310567362560,'user','如何使用企业知识库？',9,'2026-06-23 14:40:05',0),(327699382977826816,327699310567362560,'assistant','使用企业知识库可以显著提升工作效率、促进知识共享和减少重复劳动。以下是使用企业知识库的一般步骤和最佳实践：\n\n### 一、了解知识库的结构与内容\n1. **熟悉分类体系**：大多数企业知识库会按部门（如HR、IT、市场）、主题（如产品文档、操作手册、FAQ）或项目进行分类，先了解整体架构有助于快速定位信息。\n2. **查看使用指南**：许多知识库首页会提供“如何使用本知识库”或“新手引导”页面，建议优先阅读。\n\n### 二、高效检索信息\n1. **使用关键词搜索**：\n   - 输入具体问题关键词（如“报销流程”“客户合同模板”）。\n   - 利用高级搜索功能（如按作者、更新时间、文档类型筛选）。\n2. **浏览目录或标签**：如果不确定关键词，可通过导航菜单或标签云浏览相关内容。\n3. **查看热门/最新内容**：关注“热门文章”“最近更新”板块，获取高频或时效性强的信息。\n\n### 三、正确使用知识内容\n1. **验证信息时效性**：注意文档的最后更新日期，避免使用过时流程或政策。\n2. **遵循引用规范**：若需在报告或邮件中引用知识库内容，应注明来源链接或版本号。\n3. **反馈问题**：发现内容错误、缺失或难以理解时，及时通过“反馈”按钮或联系管理员修正。\n\n### 四、积极参与知识共建\n1. **贡献内容**：\n   - 将个人经验、项目总结、操作指南等整理成文档上传。\n   - 遵循企业规定的格式模板（如标题命名规则、元数据填写）。\n2. **维护更新**：\n   - 定期检查自己创建的内容是否需要更新。\n   - 对他人内容提出改进建议（如评论、协作编辑）。\n3. **参与评审**：部分企业设有知识审核机制，可申请成为领域专家参与内容质量把控。\n\n### 五、遵守安全与权限规则\n- **权限管理**：仅访问授权范围内的内容（如财务数据可能仅限财务部查看）。\n- **保密要求**：不得将内部知识库内容外传或用于非工作用途。\n- **合规操作**：遵循公司关于数据存储、分享和删除的规定。\n\n### 六、结合工具提升效率\n- **集成办公软件**：若知识库支持（如Confluence、Notion），可直接在Teams/钉钉/飞书中嵌入链接。\n- **设置收藏/订阅**：对常用页面收藏，或订阅更新通知，确保及时获取变更信息。\n\n---\n\n✅ **小贴士**：\n- 新员工入职时，建议花1–2小时系统浏览知识库核心模块。\n- 遇到重复性问题，先查知识库再提问，培养“自助式”工作习惯。\n- 管理者应定期推动知识沉淀，例如项目结束后强制归档复盘文档。\n\n通过主动使用和持续贡献，企业知识库才能真正成为组织的“智慧资产”。',854,'2026-06-23 14:40:21',0),(1700000000000000001,1600000000000000001,'user','Spring Boot自动配置的原理是什么？',20,'2026-06-19 10:24:37',0),(1700000000000000002,1600000000000000001,'assistant','Spring Boot的自动配置是通过条件注解(@ConditionalOnClass、@ConditionalOnMissingBean等)实现的。它会根据类路径中的jar包和已定义的Bean来决定是否加载某个配置...',150,'2026-06-19 10:24:37',0),(1700000000000000003,1600000000000000002,'user','React 18的新特性有哪些？',18,'2026-06-19 10:24:37',0),(1700000000000000004,1600000000000000002,'assistant','React 18的主要新特性包括：1. 并发渲染 2. 自动批处理 3. Transitions 4. Suspense改进...',120,'2026-06-19 10:24:37',0),(1700000000000000005,1600000000000000003,'user','如何优化MySQL查询性能？',15,'2026-06-19 10:24:37',0),(1700000000000000006,1600000000000000003,'assistant','MySQL查询优化可以从以下几个方面入手：1. 索引优化 2. 查询语句优化 3. 表结构优化 4. 参数调优...',135,'2026-06-19 10:24:37',0);
/*!40000 ALTER TABLE `message` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping routines for database 'kb_ai'
--

--
-- Current Database: `kb_search`
--

CREATE DATABASE /*!32312 IF NOT EXISTS*/ `kb_search` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci */ /*!80016 DEFAULT ENCRYPTION='N' */;

USE `kb_search`;

--
-- Table structure for table `kb_search_history`
--

DROP TABLE IF EXISTS `kb_search_history`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `kb_search_history` (
  `id` bigint NOT NULL COMMENT '搜索历史ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `keyword` varchar(200) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '搜索关键词',
  `search_count` int DEFAULT '0' COMMENT '搜索次数',
  `search_type` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'document' COMMENT '搜索类型：document-文档，user-用户',
  `result_count` int DEFAULT '0' COMMENT '结果数量',
  `search_params` json DEFAULT NULL COMMENT '搜索参数',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '搜索时间',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_keyword` (`keyword`(100)),
  KEY `idx_create_time` (`created_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='搜索历史表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `kb_search_history`
--

LOCK TABLES `kb_search_history` WRITE;
/*!40000 ALTER TABLE `kb_search_history` DISABLE KEYS */;
INSERT INTO `kb_search_history` VALUES (327704815390035968,1000000000000000001,'Spring Boot教程',2,'document',0,NULL,'2026-06-23 15:02:51');
/*!40000 ALTER TABLE `kb_search_history` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping routines for database 'kb_search'
--

--
-- Current Database: `kb_file`
--

CREATE DATABASE /*!32312 IF NOT EXISTS*/ `kb_file` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci */ /*!80016 DEFAULT ENCRYPTION='N' */;

USE `kb_file`;

--
-- Table structure for table `kb_file`
--

DROP TABLE IF EXISTS `kb_file`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `kb_file` (
  `id` bigint NOT NULL COMMENT '文件ID',
  `file_name` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '文件名',
  `original_name` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '原始文件名',
  `file_path` varchar(500) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '文件路径',
  `file_size` bigint NOT NULL COMMENT '文件大小（字节）',
  `file_type` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '文件类型',
  `mime_type` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT 'MIME类型',
  `file_extension` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '文件扩展名',
  `storage_type` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'local' COMMENT '存储类型',
  `upload_user_id` bigint NOT NULL COMMENT '上传用户ID',
  `upload_user_name` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '上传用户姓名（冗余字段）',
  `related_type` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '关联类型',
  `related_id` bigint DEFAULT NULL COMMENT '关联ID',
  `download_count` int NOT NULL DEFAULT '0' COMMENT '下载次数',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `deleted` tinyint NOT NULL DEFAULT '0' COMMENT '删除标识',
  PRIMARY KEY (`id`),
  KEY `idx_upload_user_id` (`upload_user_id`),
  KEY `idx_related` (`related_type`,`related_id`),
  KEY `idx_file_type` (`file_type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='文件信息表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `kb_file`
--

LOCK TABLES `kb_file` WRITE;
/*!40000 ALTER TABLE `kb_file` DISABLE KEYS */;
/*!40000 ALTER TABLE `kb_file` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `tb_file`
--

DROP TABLE IF EXISTS `tb_file`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `tb_file` (
  `id` bigint NOT NULL COMMENT '文件ID',
  `original_name` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '原始文件名',
  `stored_name` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '存储文件名',
  `file_path` varchar(500) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '文件路径',
  `file_size` bigint NOT NULL COMMENT '文件大小（字节）',
  `file_type` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '文件类型',
  `mime_type` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT 'MIME类型',
  `file_hash` varchar(128) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '文件哈希',
  `storage_type` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'local' COMMENT '存储类型：local-本地，oss-对象存储',
  `bucket_name` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '存储桶名称',
  `uploader_id` bigint NOT NULL COMMENT '上传者ID',
  `access_level` int NOT NULL DEFAULT '0' COMMENT '访问级别：0-私有，1-团队可见，2-公开',
  `download_count` int NOT NULL DEFAULT '0' COMMENT '下载次数',
  `status` int NOT NULL DEFAULT '1' COMMENT '状态：0-删除，1-正常',
  `deleted` tinyint NOT NULL DEFAULT '0' COMMENT '删除标识',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `create_by` bigint DEFAULT NULL COMMENT '创建人ID',
  `update_by` bigint DEFAULT NULL COMMENT '更新人ID',
  `duration` int DEFAULT NULL COMMENT '时长（秒），音视频文件专用',
  `resolution` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '分辨率',
  `bitrate` int DEFAULT NULL COMMENT '码率（kbps）',
  `transcode_status` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '转码状态：PENDING/PROCESSING/DONE/FAILED',
  `hls_path` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT 'HLS播放列表路径',
  `thumbnail_path` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '缩略图路径',
  PRIMARY KEY (`id`),
  KEY `idx_uploader_id` (`uploader_id`),
  KEY `idx_file_type` (`file_type`),
  KEY `idx_file_hash` (`file_hash`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='文件信息表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `tb_file`
--

LOCK TABLES `tb_file` WRITE;
/*!40000 ALTER TABLE `tb_file` DISABLE KEYS */;
INSERT INTO `tb_file` VALUES (327283176512819200,'1dd62061-0299-456e-a8b5-43f5a817db96.png','b763d39379140ba128abb57b00dd311a7264a8b34d5ffe5dce270686c0dd864f.png','2026/06/22/b7/b763d39379140ba128abb57b00dd311a7264a8b34d5ffe5dce270686c0dd864f.png',134596,'IMAGE','image/png','b763d39379140ba128abb57b00dd311a7264a8b34d5ffe5dce270686c0dd864f','RUSTFS',NULL,1,0,0,1,0,'2026-06-22 11:06:30','2026-06-22 11:06:30',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL),(327283177607532544,'1dd62061-0299-456e-a8b5-43f5a817db96.png','b763d39379140ba128abb57b00dd311a7264a8b34d5ffe5dce270686c0dd864f.png','2026/06/22/b7/b763d39379140ba128abb57b00dd311a7264a8b34d5ffe5dce270686c0dd864f.png',134596,'IMAGE','image/png','b763d39379140ba128abb57b00dd311a7264a8b34d5ffe5dce270686c0dd864f','RUSTFS',NULL,1,0,0,1,0,'2026-06-22 11:06:30','2026-06-22 11:06:30',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL),(327283179767599104,'5f7a3d15-9437-43e8-867f-3dbf2bd0a413.jpg','b592abf1eafde982ef687ec90e62ba301c7061a53a3a6c54d455bc30106bf55c.jpg','2026/06/22/b5/b592abf1eafde982ef687ec90e62ba301c7061a53a3a6c54d455bc30106bf55c.jpg',128701,'IMAGE','image/jpg','b592abf1eafde982ef687ec90e62ba301c7061a53a3a6c54d455bc30106bf55c','RUSTFS',NULL,1,0,0,1,0,'2026-06-22 11:06:31','2026-06-22 11:06:31',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL),(327283180732289024,'5f7a3d15-9437-43e8-867f-3dbf2bd0a413.jpg','b592abf1eafde982ef687ec90e62ba301c7061a53a3a6c54d455bc30106bf55c.jpg','2026/06/22/b5/b592abf1eafde982ef687ec90e62ba301c7061a53a3a6c54d455bc30106bf55c.jpg',128701,'IMAGE','image/jpg','b592abf1eafde982ef687ec90e62ba301c7061a53a3a6c54d455bc30106bf55c','RUSTFS',NULL,1,0,0,1,0,'2026-06-22 11:06:31','2026-06-22 11:06:31',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL),(327283181306908672,'e7dc868f-3068-4f27-9ed1-a8834d2ad2e8.png','dcc6c491bfb0ed78f18c3768ec51355447816f76416ec7745c25cc06b16b3f9a.png','2026/06/22/dc/dcc6c491bfb0ed78f18c3768ec51355447816f76416ec7745c25cc06b16b3f9a.png',63419,'IMAGE','image/png','dcc6c491bfb0ed78f18c3768ec51355447816f76416ec7745c25cc06b16b3f9a','RUSTFS',NULL,1,0,0,1,0,'2026-06-22 11:06:31','2026-06-22 11:06:31',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL),(327283183223705600,'39eee48c-73d6-4f24-a2d2-d89875182c03.jpg','538ad3a9d11275bc523fe383f3ac378b04e912a7d457b34c1b59a402d3a64618.jpg','2026/06/22/53/538ad3a9d11275bc523fe383f3ac378b04e912a7d457b34c1b59a402d3a64618.jpg',52364,'IMAGE','image/jpg','538ad3a9d11275bc523fe383f3ac378b04e912a7d457b34c1b59a402d3a64618','RUSTFS',NULL,1,0,0,1,0,'2026-06-22 11:06:32','2026-06-22 11:06:32',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL),(327283183907377152,'39eee48c-73d6-4f24-a2d2-d89875182c03.jpg','538ad3a9d11275bc523fe383f3ac378b04e912a7d457b34c1b59a402d3a64618.jpg','2026/06/22/53/538ad3a9d11275bc523fe383f3ac378b04e912a7d457b34c1b59a402d3a64618.jpg',52364,'IMAGE','image/jpg','538ad3a9d11275bc523fe383f3ac378b04e912a7d457b34c1b59a402d3a64618','RUSTFS',NULL,1,0,0,1,0,'2026-06-22 11:06:32','2026-06-22 11:06:32',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL),(327283184792375296,'1ab68e80-6a45-431d-a878-313ec74c031c.png','318b845cbcc715ea6c300c9613a78299c91d8344936d2fa056c3fcfd1a79ceca.png','2026/06/22/31/318b845cbcc715ea6c300c9613a78299c91d8344936d2fa056c3fcfd1a79ceca.png',65668,'IMAGE','image/png','318b845cbcc715ea6c300c9613a78299c91d8344936d2fa056c3fcfd1a79ceca','RUSTFS',NULL,1,0,0,1,0,'2026-06-22 11:06:32','2026-06-22 11:06:32',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL),(327283185958391808,'1ab68e80-6a45-431d-a878-313ec74c031c.png','318b845cbcc715ea6c300c9613a78299c91d8344936d2fa056c3fcfd1a79ceca.png','2026/06/22/31/318b845cbcc715ea6c300c9613a78299c91d8344936d2fa056c3fcfd1a79ceca.png',65668,'IMAGE','image/png','318b845cbcc715ea6c300c9613a78299c91d8344936d2fa056c3fcfd1a79ceca','RUSTFS',NULL,1,0,0,1,0,'2026-06-22 11:06:32','2026-06-22 11:06:32',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL),(327306189379604480,'bd11aee8-ffc1-4c39-bbec-56676ca131f0.png','50966659c0957d6f1d13e4b070cc0ca34584bf3a0ad21360e74b175f6110e014.png','2026/06/22/50/50966659c0957d6f1d13e4b070cc0ca34584bf3a0ad21360e74b175f6110e014.png',61827,'IMAGE','image/png','50966659c0957d6f1d13e4b070cc0ca34584bf3a0ad21360e74b175f6110e014','RUSTFS',NULL,1,0,0,1,0,'2026-06-22 12:37:57','2026-06-22 12:37:57',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL),(327306194257580032,'94d37e69-251f-4eaf-b5b6-b5c48cd0c0f4.png','e0e1e4bfc102f6bddbe2e8a33bfd2f142b013fb62315707ccf09643f26dc464f.png','2026/06/22/e0/e0e1e4bfc102f6bddbe2e8a33bfd2f142b013fb62315707ccf09643f26dc464f.png',19646,'IMAGE','image/png','e0e1e4bfc102f6bddbe2e8a33bfd2f142b013fb62315707ccf09643f26dc464f','RUSTFS',NULL,1,0,0,1,0,'2026-06-22 12:37:58','2026-06-22 12:37:58',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL),(327306196505726976,'9c6cb4bd-eca6-458e-8098-13b7cde64b6a.png','6766d7a00b25425fe3d9ec56952e13bdab3296eab991d57cbd4cacc48b59453b.png','2026/06/22/67/6766d7a00b25425fe3d9ec56952e13bdab3296eab991d57cbd4cacc48b59453b.png',25669,'IMAGE','image/png','6766d7a00b25425fe3d9ec56952e13bdab3296eab991d57cbd4cacc48b59453b','RUSTFS',NULL,1,0,0,1,0,'2026-06-22 12:37:58','2026-06-22 12:37:58',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL),(327306198602878976,'be92b3fd-f13a-47c8-a758-2cc174dc29d9.png','78a12912d3824743f5cbb3bf265cea6812f7abfef0b377c4069f090dc6e4e922.png','2026/06/22/78/78a12912d3824743f5cbb3bf265cea6812f7abfef0b377c4069f090dc6e4e922.png',25268,'IMAGE','image/png','78a12912d3824743f5cbb3bf265cea6812f7abfef0b377c4069f090dc6e4e922','RUSTFS',NULL,1,0,0,1,0,'2026-06-22 12:37:59','2026-06-22 12:37:59',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL),(327306201522114560,'efd68b18-24a8-4665-8b59-1d47a2e1fa8a.png','754b7bac1b112a3a6af805754c61d4afcd2264a82eb2cfd1d084c98ce8819f2d.png','2026/06/22/75/754b7bac1b112a3a6af805754c61d4afcd2264a82eb2cfd1d084c98ce8819f2d.png',25164,'IMAGE','image/png','754b7bac1b112a3a6af805754c61d4afcd2264a82eb2cfd1d084c98ce8819f2d','RUSTFS',NULL,1,0,0,1,0,'2026-06-22 12:37:59','2026-06-22 12:37:59',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL),(327306204479098880,'850b595e-aaf7-4530-a8f7-63e4fd46897b.png','e4fb6386ee1cc844db7fa92d608932125cbf4a01008e8c6d720d199ad63cbd3b.png','2026/06/22/e4/e4fb6386ee1cc844db7fa92d608932125cbf4a01008e8c6d720d199ad63cbd3b.png',181680,'IMAGE','image/png','e4fb6386ee1cc844db7fa92d608932125cbf4a01008e8c6d720d199ad63cbd3b','RUSTFS',NULL,1,0,0,1,0,'2026-06-22 12:38:00','2026-06-22 12:38:00',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL),(327364781327650816,'4b012f5e-7ff6-4191-8838-b3f4b4ca0544.png','89b6b63605a40b5a424b6239ddf58f3f3a826bfd13301dee4694331de4602c3b.png','2026/06/22/89/89b6b63605a40b5a424b6239ddf58f3f3a826bfd13301dee4694331de4602c3b.png',64296,'IMAGE','image/png','89b6b63605a40b5a424b6239ddf58f3f3a826bfd13301dee4694331de4602c3b','RUSTFS',NULL,1,0,0,1,0,'2026-06-22 16:30:46','2026-06-22 16:30:46',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL),(327364786419535872,'3236534f-0cd0-4267-8396-d7038ac929e8.png','95beeca900a2d46672ad54a926315808b3d5c55e6a02d7454ec3bf9084de0fec.png','2026/06/22/95/95beeca900a2d46672ad54a926315808b3d5c55e6a02d7454ec3bf9084de0fec.png',77552,'IMAGE','image/png','95beeca900a2d46672ad54a926315808b3d5c55e6a02d7454ec3bf9084de0fec','RUSTFS',NULL,1,0,0,1,0,'2026-06-22 16:30:47','2026-06-22 16:30:47',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL),(327364789347160064,'e04e6055-ccf3-4159-8b88-ddc0a5927bbd.png','6523a4a076c8ea4bdc4b69c9e0ed7fab6bf9fd9a2d326156ddc8596f2cd2a67c.png','2026/06/22/65/6523a4a076c8ea4bdc4b69c9e0ed7fab6bf9fd9a2d326156ddc8596f2cd2a67c.png',57995,'IMAGE','image/png','6523a4a076c8ea4bdc4b69c9e0ed7fab6bf9fd9a2d326156ddc8596f2cd2a67c','RUSTFS',NULL,1,0,0,1,0,'2026-06-22 16:30:48','2026-06-22 16:30:48',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL),(327392153653350400,'bab4b524-af4c-40e7-94f2-ec0a135a7412.jpg','3ad5c2ea4e4a8cde350753df739718ca4e8a728bc4c5619c1324953d2b0bec88.jpg','2026/06/22/3a/3ad5c2ea4e4a8cde350753df739718ca4e8a728bc4c5619c1324953d2b0bec88.jpg',39747,'IMAGE','image/jpg','3ad5c2ea4e4a8cde350753df739718ca4e8a728bc4c5619c1324953d2b0bec88','RUSTFS',NULL,1,0,0,1,0,'2026-06-22 18:19:32','2026-06-22 18:19:32',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL),(327393339685081088,'8fe720e2-2ccc-4169-9ba9-eeb56fd61533.png','71ad9ee0deedd37d20135786e71ace7530fa4f15b63af7e82d63070b462d4fb8.png','2026/06/22/71/71ad9ee0deedd37d20135786e71ace7530fa4f15b63af7e82d63070b462d4fb8.png',40087,'IMAGE','image/png','71ad9ee0deedd37d20135786e71ace7530fa4f15b63af7e82d63070b462d4fb8','RUSTFS',NULL,1,0,0,1,0,'2026-06-22 18:24:15','2026-06-22 18:24:15',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL),(327393342553985024,'8e74be3b-f4c4-4bf4-b115-59f61711ecff.png','bd29d133b4e2b8bf8b2ebb24d9bfa0949e296075668a46768f4639c05a7a7fa7.png','2026/06/22/bd/bd29d133b4e2b8bf8b2ebb24d9bfa0949e296075668a46768f4639c05a7a7fa7.png',29967,'IMAGE','image/png','bd29d133b4e2b8bf8b2ebb24d9bfa0949e296075668a46768f4639c05a7a7fa7','RUSTFS',NULL,1,0,0,1,0,'2026-06-22 18:24:16','2026-06-22 18:24:16',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL),(327393345829736448,'dea6c4db-7eaa-4652-b31b-9493381e8cf1.png','46f2eec10d91c2311916d8cad2782ecbea2147687daa1951c0b20fcd049f666f.png','2026/06/22/46/46f2eec10d91c2311916d8cad2782ecbea2147687daa1951c0b20fcd049f666f.png',39675,'IMAGE','image/png','46f2eec10d91c2311916d8cad2782ecbea2147687daa1951c0b20fcd049f666f','RUSTFS',NULL,1,0,0,1,0,'2026-06-22 18:24:16','2026-06-22 18:24:16',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL),(327395764730990592,'bead95db-34be-4ce2-8246-834d33cd1773.png','9f11d2e750c2d31e4cd407d31cdbb49687580a0caf106a9f51ef61276ad138f1.png','2026/06/22/9f/9f11d2e750c2d31e4cd407d31cdbb49687580a0caf106a9f51ef61276ad138f1.png',30746,'IMAGE','image/png','9f11d2e750c2d31e4cd407d31cdbb49687580a0caf106a9f51ef61276ad138f1','RUSTFS',NULL,1,0,0,1,0,'2026-06-22 18:33:53','2026-06-22 18:33:53',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL),(327395766790393856,'37edfd9a-876f-4aae-9b33-fb95efdfea40.png','c7a6cbea87512a2470f019bd1b41c0ed98a3dccbb967cf96ea62d12a857af054.png','2026/06/22/c7/c7a6cbea87512a2470f019bd1b41c0ed98a3dccbb967cf96ea62d12a857af054.png',37280,'IMAGE','image/png','c7a6cbea87512a2470f019bd1b41c0ed98a3dccbb967cf96ea62d12a857af054','RUSTFS',NULL,1,0,0,1,0,'2026-06-22 18:33:54','2026-06-22 18:33:54',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL),(327396050002382848,'b8e0a6f7-0b0d-49e2-b507-b23ec0f48563.png','02cd9f370c9bbf2dad567b601ec39518ead2feded29e490bee366a59e767b236.png','2026/06/22/02/02cd9f370c9bbf2dad567b601ec39518ead2feded29e490bee366a59e767b236.png',57151,'IMAGE','image/png','02cd9f370c9bbf2dad567b601ec39518ead2feded29e490bee366a59e767b236','RUSTFS',NULL,1,0,0,1,0,'2026-06-22 18:35:01','2026-06-22 18:35:01',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL),(327396052330221568,'03dac28b-674d-4419-a023-c3a135429c35.png','145f9d681114e732f4733e74163d8fb8e9ad68c3b904bef32a9a609a94e6a25b.png','2026/06/22/14/145f9d681114e732f4733e74163d8fb8e9ad68c3b904bef32a9a609a94e6a25b.png',72844,'IMAGE','image/png','145f9d681114e732f4733e74163d8fb8e9ad68c3b904bef32a9a609a94e6a25b','RUSTFS',NULL,1,0,0,1,0,'2026-06-22 18:35:02','2026-06-22 18:35:02',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL),(327396054196686848,'01185e3c-14db-43fe-b875-831cfbd3079b.png','381ca7f4ce7a7fd0d77dbe3809ac97a266066b1d0d5289bb89c10d2e181c27eb.png','2026/06/22/38/381ca7f4ce7a7fd0d77dbe3809ac97a266066b1d0d5289bb89c10d2e181c27eb.png',30814,'IMAGE','image/png','381ca7f4ce7a7fd0d77dbe3809ac97a266066b1d0d5289bb89c10d2e181c27eb','RUSTFS',NULL,1,0,0,1,0,'2026-06-22 18:35:02','2026-06-22 18:35:02',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL),(327396055853436928,'982bfebb-9392-4122-a519-6a1cfaeceb84.png','313c97e2a39cdd791662432ddc5200780c4f403af0af1c923562e715b177e7d7.png','2026/06/22/31/313c97e2a39cdd791662432ddc5200780c4f403af0af1c923562e715b177e7d7.png',18675,'IMAGE','image/png','313c97e2a39cdd791662432ddc5200780c4f403af0af1c923562e715b177e7d7','RUSTFS',NULL,1,0,0,1,0,'2026-06-22 18:35:02','2026-06-22 18:35:02',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL),(327705305569955840,'未命名绘图-第 23 页.drawio.png','a96be6052c94ae696f6e5887e2148524de269f5350ceecdb08e3b5d7708639ca.png','2026/06/23/a9/a96be6052c94ae696f6e5887e2148524de269f5350ceecdb08e3b5d7708639ca.png',6506368,'IMAGE','image/png','a96be6052c94ae696f6e5887e2148524de269f5350ceecdb08e3b5d7708639ca','RUSTFS',NULL,1,1,0,1,0,'2026-06-23 15:03:53','2026-06-23 15:03:53',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL);
/*!40000 ALTER TABLE `tb_file` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping routines for database 'kb_file'
--

--
-- Current Database: `kb_foundation`
--

CREATE DATABASE /*!32312 IF NOT EXISTS*/ `kb_foundation` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci */ /*!80016 DEFAULT ENCRYPTION='N' */;

USE `kb_foundation`;

--
-- Table structure for table `kb_dict`
--

DROP TABLE IF EXISTS `kb_dict`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `kb_dict` (
  `id` bigint NOT NULL COMMENT '字典ID',
  `dict_code` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '字典编码',
  `dict_name` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '字典名称',
  `dict_type` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '字典类型',
  `description` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '描述',
  `sort` int NOT NULL DEFAULT '0' COMMENT '排序',
  `status` tinyint NOT NULL DEFAULT '1' COMMENT '状态：0-禁用，1-启用',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `create_by` bigint DEFAULT NULL COMMENT '创建人',
  `update_by` bigint DEFAULT NULL COMMENT '更新人',
  `deleted` tinyint NOT NULL DEFAULT '0' COMMENT '删除标识',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_dict_code` (`dict_code`),
  KEY `idx_dict_type` (`dict_type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='字典表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `kb_dict`
--

LOCK TABLES `kb_dict` WRITE;
/*!40000 ALTER TABLE `kb_dict` DISABLE KEYS */;
INSERT INTO `kb_dict` VALUES (3000000000000000001,'document_status','文档状态','DOCUMENT','文档状态：草稿/待审核/已发布/已驳回',1,1,'2026-06-19 10:24:37','2026-06-19 10:24:37',NULL,NULL,0),(3000000000000000002,'notification_type','通知类型','SYSTEM','系统通知类型',2,1,'2026-06-19 10:24:37','2026-06-19 10:24:37',NULL,NULL,0),(3000000000000000003,'operation_type','操作类型','SYSTEM','系统操作类型',3,1,'2026-06-19 10:24:37','2026-06-19 10:24:37',NULL,NULL,0),(3000000000000000004,'file_type','文件类型','FILE','支持的文件类型',4,1,'2026-06-19 10:24:37','2026-06-19 10:24:37',NULL,NULL,0),(3000000000000000005,'user_type','用户类型','USER','用户类型分类',5,1,'2026-06-19 10:24:37','2026-06-19 10:24:37',NULL,NULL,0);
/*!40000 ALTER TABLE `kb_dict` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `kb_dict_data`
--

DROP TABLE IF EXISTS `kb_dict_data`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `kb_dict_data` (
  `id` bigint NOT NULL COMMENT '字典数据ID',
  `dict_id` bigint NOT NULL COMMENT '字典ID',
  `dict_code` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '字典编码（冗余）',
  `dict_label` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '字典标签',
  `dict_value` varchar(200) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '字典值',
  `dict_sort` int NOT NULL DEFAULT '0' COMMENT '排序',
  `css_class` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '样式类名',
  `is_default` tinyint NOT NULL DEFAULT '0' COMMENT '是否默认',
  `status` tinyint NOT NULL DEFAULT '1' COMMENT '状态',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `create_by` bigint DEFAULT NULL COMMENT '创建人',
  `update_by` bigint DEFAULT NULL COMMENT '更新人',
  `deleted` tinyint NOT NULL DEFAULT '0' COMMENT '删除标识',
  PRIMARY KEY (`id`),
  KEY `idx_dict_id` (`dict_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='字典数据表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `kb_dict_data`
--

LOCK TABLES `kb_dict_data` WRITE;
/*!40000 ALTER TABLE `kb_dict_data` DISABLE KEYS */;
INSERT INTO `kb_dict_data` VALUES (3100000000000000001,3000000000000000001,'document_status','草稿','0',1,'badge-gray',1,1,'2026-06-19 10:24:37','2026-06-19 10:24:37',NULL,NULL,0),(3100000000000000002,3000000000000000001,'document_status','待审核','1',2,'badge-yellow',0,1,'2026-06-19 10:24:37','2026-06-19 10:24:37',NULL,NULL,0),(3100000000000000003,3000000000000000001,'document_status','已发布','2',3,'badge-green',0,1,'2026-06-19 10:24:37','2026-06-19 10:24:37',NULL,NULL,0),(3100000000000000004,3000000000000000001,'document_status','已驳回','3',4,'badge-red',0,1,'2026-06-19 10:24:37','2026-06-19 10:24:37',NULL,NULL,0),(3200000000000000001,3000000000000000002,'notification_type','系统通知','system',1,'badge-blue',1,1,'2026-06-19 10:24:37','2026-06-19 10:24:37',NULL,NULL,0),(3200000000000000002,3000000000000000002,'notification_type','评论通知','comment',2,'badge-green',0,1,'2026-06-19 10:24:37','2026-06-19 10:24:37',NULL,NULL,0),(3200000000000000003,3000000000000000002,'notification_type','@提醒','mention',3,'badge-orange',0,1,'2026-06-19 10:24:37','2026-06-19 10:24:37',NULL,NULL,0),(3200000000000000004,3000000000000000002,'notification_type','审核通知','review',4,'badge-purple',0,1,'2026-06-19 10:24:37','2026-06-19 10:24:37',NULL,NULL,0),(3200000000000000005,3000000000000000002,'notification_type','点赞通知','like',5,'badge-pink',0,1,'2026-06-19 10:24:37','2026-06-19 10:24:37',NULL,NULL,0),(3300000000000000001,3000000000000000003,'operation_type','登录','LOGIN',1,NULL,0,1,'2026-06-19 10:24:37','2026-06-19 10:24:37',NULL,NULL,0),(3300000000000000002,3000000000000000003,'operation_type','登出','LOGOUT',2,NULL,0,1,'2026-06-19 10:24:37','2026-06-19 10:24:37',NULL,NULL,0),(3300000000000000003,3000000000000000003,'operation_type','创建','CREATE',3,NULL,0,1,'2026-06-19 10:24:37','2026-06-19 10:24:37',NULL,NULL,0),(3300000000000000004,3000000000000000003,'operation_type','更新','UPDATE',4,NULL,0,1,'2026-06-19 10:24:37','2026-06-19 10:24:37',NULL,NULL,0),(3300000000000000005,3000000000000000003,'operation_type','删除','DELETE',5,NULL,0,1,'2026-06-19 10:24:37','2026-06-19 10:24:37',NULL,NULL,0),(3300000000000000006,3000000000000000003,'operation_type','查询','QUERY',6,NULL,0,1,'2026-06-19 10:24:37','2026-06-19 10:24:37',NULL,NULL,0),(3300000000000000007,3000000000000000003,'operation_type','导出','EXPORT',7,NULL,0,1,'2026-06-19 10:24:37','2026-06-19 10:24:37',NULL,NULL,0),(3300000000000000008,3000000000000000003,'operation_type','导入','IMPORT',8,NULL,0,1,'2026-06-19 10:24:37','2026-06-19 10:24:37',NULL,NULL,0),(3400000000000000001,3000000000000000004,'file_type','PDF文档','pdf',1,'file-pdf',1,1,'2026-06-19 10:24:37','2026-06-19 10:24:37',NULL,NULL,0),(3400000000000000002,3000000000000000004,'file_type','Word文档','doc',2,'file-word',0,1,'2026-06-19 10:24:37','2026-06-19 10:24:37',NULL,NULL,0),(3400000000000000003,3000000000000000004,'file_type','Excel表格','xls',3,'file-excel',0,1,'2026-06-19 10:24:37','2026-06-19 10:24:37',NULL,NULL,0),(3400000000000000004,3000000000000000004,'file_type','PPT演示','ppt',4,'file-ppt',0,1,'2026-06-19 10:24:37','2026-06-19 10:24:37',NULL,NULL,0),(3400000000000000005,3000000000000000004,'file_type','图片','image',5,'file-image',0,1,'2026-06-19 10:24:37','2026-06-19 10:24:37',NULL,NULL,0),(3400000000000000006,3000000000000000004,'file_type','视频','video',6,'file-video',0,1,'2026-06-19 10:24:37','2026-06-19 10:24:37',NULL,NULL,0),(3400000000000000007,3000000000000000004,'file_type','文本','txt',7,'file-text',0,1,'2026-06-19 10:24:37','2026-06-19 10:24:37',NULL,NULL,0),(3400000000000000008,3000000000000000004,'file_type','Markdown','md',8,'file-markdown',0,1,'2026-06-19 10:24:37','2026-06-19 10:24:37',NULL,NULL,0),(3500000000000000001,3000000000000000005,'user_type','超级管理员','SUPER_ADMIN',1,'user-admin',0,1,'2026-06-19 10:24:37','2026-06-19 10:24:37',NULL,NULL,0),(3500000000000000002,3000000000000000005,'user_type','知识管理员','KNOWLEDGE_ADMIN',2,'user-manager',0,1,'2026-06-19 10:24:37','2026-06-19 10:24:37',NULL,NULL,0),(3500000000000000003,3000000000000000005,'user_type','内容管理员','CONTENT_ADMIN',3,'user-editor',0,1,'2026-06-19 10:24:37','2026-06-19 10:24:37',NULL,NULL,0),(3500000000000000004,3000000000000000005,'user_type','团队负责人','TEAM_LEADER',4,'user-leader',0,1,'2026-06-19 10:24:37','2026-06-19 10:24:37',NULL,NULL,0),(3500000000000000005,3000000000000000005,'user_type','贡献者','CONTRIBUTOR',5,'user-contributor',0,1,'2026-06-19 10:24:37','2026-06-19 10:24:37',NULL,NULL,0),(3500000000000000006,3000000000000000005,'user_type','普通用户','VIEWER',6,'user-viewer',1,1,'2026-06-19 10:24:37','2026-06-19 10:24:37',NULL,NULL,0);
/*!40000 ALTER TABLE `kb_dict_data` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `kb_notification`
--

DROP TABLE IF EXISTS `kb_notification`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `kb_notification` (
  `id` bigint NOT NULL COMMENT '通知ID',
  `user_id` bigint NOT NULL COMMENT '接收用户ID',
  `user_name` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '接收用户姓名',
  `notification_type` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '通知类型',
  `title` varchar(200) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '通知标题',
  `content` text COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '通知内容',
  `link` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '跳转链接',
  `related_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '关联类型',
  `related_id` bigint DEFAULT NULL COMMENT '关联ID',
  `is_read` tinyint NOT NULL DEFAULT '0' COMMENT '是否已读',
  `read_time` datetime DEFAULT NULL COMMENT '阅读时间',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `create_by` bigint DEFAULT NULL COMMENT '创建人ID',
  `update_by` bigint DEFAULT NULL COMMENT '更新人ID',
  `deleted` tinyint NOT NULL DEFAULT '0' COMMENT '逻辑删除标记',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_is_read` (`is_read`),
  KEY `idx_notification_type` (`notification_type`),
  KEY `idx_create_time` (`created_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='系统通知表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `kb_notification`
--

LOCK TABLES `kb_notification` WRITE;
/*!40000 ALTER TABLE `kb_notification` DISABLE KEYS */;
INSERT INTO `kb_notification` VALUES (327717300704776192,1000000000000000001,NULL,'review','文档审核通过','您的文档《索引优化的10个高效技巧》已通过审核，正式发布。','/review/documents/327395741289025536','document',327395741289025536,1,'2026-06-23 16:38:01','2026-06-23 15:51:33','2026-06-23 16:38:01',NULL,NULL,0),(327717769489551360,1000000000000000001,NULL,'review','文档审核通过','您的文档《线上慢SQL导致CPU飙升，如何处理？》已通过审核，正式发布。','/review/documents/327396046936346624','document',327396046936346624,1,'2026-06-23 16:38:01','2026-06-23 15:53:25','2026-06-23 16:38:01',NULL,NULL,0),(327723397578625024,1000000000000000001,NULL,'review','文档已提交审核','您的文档《索引优化的10个高效技巧》已提交审核，请等待审核员处理。','/review/documents/327395741289025536','document',327395741289025536,1,'2026-06-23 16:38:01','2026-06-23 16:15:47','2026-06-23 16:38:01',NULL,NULL,0),(327723418629836800,1000000000000000001,NULL,'review','新文档待审核','用户「admin」提交了文档《索引优化的10个高效技巧》，请及时审核。','/review/documents/327395741289025536','document',327395741289025536,1,'2026-06-23 16:38:01','2026-06-23 16:15:52','2026-06-23 16:38:01',NULL,NULL,0),(327723566630047744,1000000000000000001,NULL,'review','文档审核通过','您的文档《索引优化的10个高效技巧》已通过审核，正式发布。','/review/documents/327395741289025536','document',327395741289025536,1,'2026-06-23 16:38:01','2026-06-23 16:16:27','2026-06-23 16:38:01',NULL,NULL,0),(327723580014071808,1000000000000000001,NULL,'review','文档审核通过','您的文档《线上慢SQL导致CPU飙升，如何处理？》已通过审核，正式发布。','/review/documents/327396046936346624','document',327396046936346624,1,'2026-06-23 16:38:01','2026-06-23 16:16:30','2026-06-23 16:38:01',NULL,NULL,0),(327723592659898368,1000000000000000001,NULL,'review','文档审核通过','您的文档《AgentScope2.0发布了》已通过审核，正式发布。','/review/documents/327283237921624064','document',327283237921624064,1,'2026-06-23 16:38:01','2026-06-23 16:16:33','2026-06-23 16:38:01',NULL,NULL,0),(327723602520707072,1000000000000000001,NULL,'review','文档审核通过','您的文档《全网爆火的Loop到底是什么？》已通过审核，正式发布。','/review/documents/327306488970350592','document',327306488970350592,1,'2026-06-23 16:38:01','2026-06-23 16:16:36','2026-06-23 16:38:01',NULL,NULL,0),(327723614344450048,1000000000000000001,NULL,'review','文档审核通过','您的文档《全网爆火的Loop到底是什么？》已通过审核，正式发布。','/review/documents/327306488970350592','document',327306488970350592,1,'2026-06-23 16:38:01','2026-06-23 16:16:38','2026-06-23 16:38:01',NULL,NULL,0),(327723804786823168,1000000000000000001,NULL,'review','文档已提交审核','您的文档《线上慢SQL导致CPU飙升，如何处理？》已提交审核，请等待审核员处理。','/review/documents/327396046936346624','document',327396046936346624,1,'2026-06-23 16:38:01','2026-06-23 16:17:24','2026-06-23 16:38:01',NULL,NULL,0),(327723805348859904,1000000000000000001,NULL,'review','新文档待审核','用户「admin」提交了文档《线上慢SQL导致CPU飙升，如何处理？》，请及时审核。','/review/documents/327396046936346624','document',327396046936346624,1,'2026-06-23 16:38:01','2026-06-23 16:17:24','2026-06-23 16:38:01',NULL,NULL,0),(327723995292110848,1000000000000000001,NULL,'review','文档已提交审核','您的文档《全网爆火的Loop到底是什么？》已提交审核，请等待审核员处理。','/review/documents/327306488970350592','document',327306488970350592,1,'2026-06-23 16:38:01','2026-06-23 16:18:09','2026-06-23 16:38:01',NULL,NULL,0),(327723995791233024,1000000000000000001,NULL,'review','新文档待审核','用户「admin」提交了文档《全网爆火的Loop到底是什么？》，请及时审核。','/review/documents/327306488970350592','document',327306488970350592,1,'2026-06-23 16:38:01','2026-06-23 16:18:09','2026-06-23 16:38:01',NULL,NULL,0),(327729023109173248,1000000000000000001,NULL,'review','文档审核通过','您的文档《全网爆火的Loop到底是什么？》已通过审核，正式发布。','/review/documents/327306488970350592','document',327306488970350592,0,NULL,'2026-06-23 16:38:08','2026-06-23 16:38:08',NULL,NULL,0),(327729038133170176,1000000000000000001,NULL,'review','文档审核通过','您的文档《线上慢SQL导致CPU飙升，如何处理？》已通过审核，正式发布。','/review/documents/327396046936346624','document',327396046936346624,0,NULL,'2026-06-23 16:38:12','2026-06-23 16:38:12',NULL,NULL,0),(327729229515067392,1000000000000000001,NULL,'review','文档已提交审核','您的文档《索引优化的10个高效技巧》已提交审核，请等待审核员处理。','/review/documents/327395741289025536','document',327395741289025536,0,NULL,'2026-06-23 16:38:57','2026-06-23 16:38:57',NULL,NULL,0),(327729229938692096,1000000000000000001,NULL,'review','新文档待审核','用户「admin」提交了文档《索引优化的10个高效技巧》，请及时审核。','/review/documents/327395741289025536','document',327395741289025536,0,NULL,'2026-06-23 16:38:57','2026-06-23 16:38:57',NULL,NULL,0),(327730660745809920,1000000000000000001,NULL,'review','新文档待审核','用户「admin」提交了文档《程序员最常用的10个AI提示词》，请及时审核。','/review/documents/327405209401823232','document',327405209401823232,0,NULL,'2026-06-23 16:44:38','2026-06-23 16:44:38',NULL,NULL,0),(327730872080011264,1000000000000000001,NULL,'review','新文档待审核','用户「admin」提交了文档《线上慢SQL导致CPU飙升，如何处理？》，请及时审核。','/review/documents/327396046936346624','document',327396046936346624,0,NULL,'2026-06-23 16:45:29','2026-06-23 16:45:29',NULL,NULL,0),(327731763310891008,1000000000000000001,NULL,'review','新文档待审核','用户「admin」提交了文档《全网爆火的Loop到底是什么？》，请及时审核。','/review/documents/327306488970350592','document',327306488970350592,0,NULL,'2026-06-23 16:49:01','2026-06-23 16:49:01',NULL,NULL,0),(327731836539244544,1000000000000000001,NULL,'review','文档审核通过','您的文档《全网爆火的Loop到底是什么？》已通过审核，正式发布。','/review/documents/327306488970350592','document',327306488970350592,0,NULL,'2026-06-23 16:49:19','2026-06-23 16:49:19',NULL,NULL,0),(327732841595146240,1000000000000000001,NULL,'review','新文档待审核','用户「admin」提交了文档《程序员最常用的10个AI提示词》，请及时审核。','/review/documents/327405209401823232','document',327405209401823232,0,NULL,'2026-06-23 16:53:18','2026-06-23 16:53:18',NULL,NULL,0),(327733167748419584,1000000000000000001,NULL,'review','新文档待审核','用户「admin」提交了文档《程序员最常用的10个AI提示词》，请及时审核。','/review/documents/327405209401823232','document',327405209401823232,0,NULL,'2026-06-23 16:54:36','2026-06-23 16:54:36',NULL,NULL,0),(1500000000000000001,1000000000000000002,'editor','system','欢迎加入企业知识库','欢迎加入企业知识库系统，开始您的知识管理之旅！','/documents',NULL,NULL,0,NULL,'2026-06-19 10:24:37','2026-06-19 10:24:37',NULL,NULL,0),(1500000000000000002,1000000000000000004,'developer','comment','您的文档收到新评论','《Spring Boot 3.x 快速入门指南》收到新评论','/documents/1000000000000000001',NULL,NULL,0,NULL,'2026-06-19 10:24:37','2026-06-19 10:24:37',NULL,NULL,0),(1500000000000000003,1000000000000000005,'product','review','文档审核通过','您的《企业知识库产品需求文档PRD》已通过审核','/documents/1000000000000000005',NULL,NULL,1,NULL,'2026-06-19 10:24:37','2026-06-19 10:24:37',NULL,NULL,0),(1500000000000000004,1000000000000000001,'admin','mention','有人@了您','developer在《Docker + Kubernetes 容器化部署》中提到了您','/documents/1000000000000000004',NULL,NULL,1,'2026-06-23 16:38:01','2026-06-19 10:24:37','2026-06-23 16:38:01',NULL,NULL,0);
/*!40000 ALTER TABLE `kb_notification` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `kb_notification_template`
--

DROP TABLE IF EXISTS `kb_notification_template`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `kb_notification_template` (
  `id` bigint NOT NULL COMMENT '主键ID',
  `template_code` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '模板编码',
  `template_name` varchar(200) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '模板名称',
  `notification_type` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '通知类型：EMAIL/SMS/WECHAT/SYSTEM/BROWSER',
  `title` varchar(500) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '模板标题',
  `content` text COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '模板内容',
  `variables` varchar(1000) COLLATE utf8mb4_unicode_ci DEFAULT '[]' COMMENT '模板变量（JSON数组格式）',
  `description` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '模板描述',
  `is_active` tinyint(1) DEFAULT '1' COMMENT '是否启用：0-停用，1-启用',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `create_by` bigint DEFAULT NULL COMMENT '创建人',
  `update_by` bigint DEFAULT NULL COMMENT '更新人',
  `deleted` tinyint NOT NULL DEFAULT '0' COMMENT '删除标识',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_template_code` (`template_code`),
  KEY `idx_notification_type` (`notification_type`),
  KEY `idx_is_active` (`is_active`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='通知模板表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `kb_notification_template`
--

LOCK TABLES `kb_notification_template` WRITE;
/*!40000 ALTER TABLE `kb_notification_template` DISABLE KEYS */;
INSERT INTO `kb_notification_template` VALUES (1,'EMAIL_VERIFY_CODE','邮箱验证码','EMAIL','验证码 - {{systemName}}','尊敬的{{userName}}，您的验证码是：{{verifyCode}}，5分钟内有效。','[\"userName\",\"verifyCode\",\"systemName\"]','用于邮箱验证和找回密码场景',1,'2026-06-19 10:24:37','2026-06-19 10:24:37',NULL,NULL,0),(2,'DOCUMENT_APPROVED','文档审核通过','SYSTEM','您的文档《{{documentTitle}}》已通过审核','您提交的文档《{{documentTitle}}》已通过审核，感谢您的贡献！','[\"documentTitle\"]','文档审核通过时发送的通知',1,'2026-06-19 10:24:37','2026-06-19 10:24:37',NULL,NULL,0),(3,'DOCUMENT_REJECTED','文档审核驳回','SYSTEM','您的文档《{{documentTitle}}》需要修改','您提交的文档《{{documentTitle}}》未通过审核，原因：{{rejectReason}}。请修改后重新提交。','[\"documentTitle\",\"rejectReason\"]','文档审核驳回时发送的通知',1,'2026-06-19 10:24:37','2026-06-19 10:24:37',NULL,NULL,0),(4,'NEW_COMMENT','新评论通知','SYSTEM','您的文档收到新评论','{{commentUsername}} 评论了您的文档《{{documentTitle}}》：{{commentContent}}','[\"commentUsername\",\"documentTitle\",\"commentContent\"]','文档收到新评论时的通知',1,'2026-06-19 10:24:37','2026-06-19 10:24:37',NULL,NULL,0),(5,'DOCUMENT_LIKED','文档被点赞','SYSTEM','您的文档收到新的点赞','{{likeUsername}} 点赞了您的文档《{{documentTitle}}》','[\"likeUsername\",\"documentTitle\"]','文档被点赞时的通知',1,'2026-06-19 10:24:37','2026-06-19 10:24:37',NULL,NULL,0),(6,'WELCOME_MESSAGE','欢迎消息','SYSTEM','欢迎加入{{systemName}}','尊敬的{{userName}}，欢迎加入{{systemName}}！我们期待您的贡献。','[\"userName\",\"systemName\"]','用户注册后的欢迎消息',1,'2026-06-19 10:24:37','2026-06-19 10:24:37',NULL,NULL,0);
/*!40000 ALTER TABLE `kb_notification_template` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `kb_operation_log`
--

DROP TABLE IF EXISTS `kb_operation_log`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `kb_operation_log` (
  `id` bigint NOT NULL COMMENT '日志ID',
  `module` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '操作模块',
  `operation_type` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '操作类型',
  `operation_desc` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '操作描述',
  `request_method` varchar(10) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '请求方法',
  `request_url` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '请求URL',
  `request_params` text COLLATE utf8mb4_unicode_ci COMMENT '请求参数',
  `response_result` text COLLATE utf8mb4_unicode_ci COMMENT '响应结果',
  `user_id` bigint DEFAULT NULL COMMENT '操作用户ID',
  `username` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '操作用户名',
  `ip_address` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT 'IP地址',
  `location` varchar(200) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '地理位置',
  `user_agent` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '用户代理',
  `execute_time` int DEFAULT NULL COMMENT '执行时长（毫秒）',
  `status` tinyint NOT NULL DEFAULT '1' COMMENT '状态',
  `error_msg` text COLLATE utf8mb4_unicode_ci COMMENT '错误信息',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '操作时间',
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `create_by` bigint DEFAULT NULL COMMENT '创建人',
  `update_by` bigint DEFAULT NULL COMMENT '更新人',
  `deleted` tinyint NOT NULL DEFAULT '0' COMMENT '删除标识',
  PRIMARY KEY (`id`),
  KEY `idx_module` (`module`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_create_time` (`created_at`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='操作日志表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `kb_operation_log`
--

LOCK TABLES `kb_operation_log` WRITE;
/*!40000 ALTER TABLE `kb_operation_log` DISABLE KEYS */;
INSERT INTO `kb_operation_log` VALUES (326358782147956736,'文件管理','上传文件','上传文件','POST','/files/upload','[{\"originalFilename\":\"2a9a3e7367eb9073fc46db17708a86cc70c7477d77d61ec5d87012c7db93b378.png\",\"size\":77975,\"type\":\"MultipartFile\"},{\"accessLevel\":1,\"fileType\":\"document\"}]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21',1163,1,NULL,'2026-06-19 21:53:17','2026-06-19 21:53:17',NULL,NULL,0),(326360760353689600,'文件管理','上传文件','上传文件','POST','/files/upload','[{\"type\":\"MultipartFile\",\"originalFilename\":\"2a9a3e7367eb9073fc46db17708a86cc70c7477d77d61ec5d87012c7db93b378.png\",\"size\":77975},{\"accessLevel\":1,\"fileType\":\"document\"}]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21',767,1,NULL,'2026-06-19 22:01:08','2026-06-19 22:01:08',NULL,NULL,0),(326360994790117376,'文件管理','上传文件','上传文件','POST','/files/upload','[{\"type\":\"MultipartFile\",\"originalFilename\":\"7VdLrDmh0H.jpg\",\"size\":75054},{\"accessLevel\":1,\"fileType\":\"document\"}]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21',775,0,'Unable to execute HTTP request: Connect to 127.0.0.1:9091 [/127.0.0.1] failed: Connection refused','2026-06-19 22:02:04','2026-06-19 22:02:04',NULL,NULL,0),(326362776425598976,'文件管理','上传文件','上传文件','POST','/files/upload','[{\"size\":869486,\"originalFilename\":\"1.jpg\",\"type\":\"MultipartFile\"},{\"accessLevel\":1,\"fileType\":\"document\"}]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21',965,0,'Failed to upload file: The specified bucket does not exist (Service: S3, Status Code: 404, Request ID: null)','2026-06-19 22:09:09','2026-06-19 22:09:09',NULL,NULL,0),(326362928037105664,'文件管理','上传文件','上传文件','POST','/files/upload','[{\"size\":869486,\"originalFilename\":\"1.jpg\",\"type\":\"MultipartFile\"},{\"accessLevel\":1,\"fileType\":\"document\"}]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21',215,1,NULL,'2026-06-19 22:09:45','2026-06-19 22:09:45',NULL,NULL,0),(326906013661925376,'文件管理','下载文件','下载文件','GET','/files/download/1/**','[\"1\",\"[HttpServletResponse]\"]',NULL,NULL,NULL,'0:0:0:0:0:0:0:1',NULL,'Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/124.0.0.0 Safari/537.36 Edg/124.0.0.0',170,0,'文件不存在','2026-06-21 10:07:48','2026-06-21 10:07:48',NULL,NULL,0),(327282073498947584,'文件管理','URL转换','从URL转换图片','POST','/files/convert-url','[\"https://files.mdnice.com/user/5303/1dd62061-0299-456e-a8b5-43f5a817db96.png\"]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.6',8968,1,NULL,'2026-06-22 11:02:07','2026-06-22 11:02:07',NULL,NULL,0),(327282075268943872,'文件管理','URL转换','从URL转换图片','POST','/files/convert-url','[\"https://files.mdnice.com/user/5303/5f7a3d15-9437-43e8-867f-3dbf2bd0a413.jpg\"]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.6',279,1,NULL,'2026-06-22 11:02:07','2026-06-22 11:02:07',NULL,NULL,0),(327282077147992064,'文件管理','URL转换','从URL转换图片','POST','/files/convert-url','[\"https://files.mdnice.com/user/5303/5f7a3d15-9437-43e8-867f-3dbf2bd0a413.jpg\"]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.6',727,1,NULL,'2026-06-22 11:02:07','2026-06-22 11:02:07',NULL,NULL,0),(327282077718417408,'文件管理','URL转换','从URL转换图片','POST','/files/convert-url','[\"https://files.mdnice.com/user/5303/39eee48c-73d6-4f24-a2d2-d89875182c03.jpg\"]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.6',262,1,NULL,'2026-06-22 11:02:07','2026-06-22 11:02:07',NULL,NULL,0),(327282079459053568,'文件管理','URL转换','从URL转换图片','POST','/files/convert-url','[\"https://files.mdnice.com/user/5303/1ab68e80-6a45-431d-a878-313ec74c031c.png\"]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.6',293,1,NULL,'2026-06-22 11:02:08','2026-06-22 11:02:08',NULL,NULL,0),(327283178492530688,'文件管理','URL转换','从URL转换图片','POST','/files/convert-url','[\"https://files.mdnice.com/user/5303/1dd62061-0299-456e-a8b5-43f5a817db96.png\"]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.6',412,1,NULL,'2026-06-22 11:06:30','2026-06-22 11:06:30',NULL,NULL,0),(327283178563833856,'文件管理','URL转换','从URL转换图片','POST','/files/convert-url','[\"https://files.mdnice.com/user/5303/1dd62061-0299-456e-a8b5-43f5a817db96.png\"]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.6',3912,1,NULL,'2026-06-22 11:06:30','2026-06-22 11:06:30',NULL,NULL,0),(327283180006674432,'文件管理','URL转换','从URL转换图片','POST','/files/convert-url','[\"https://files.mdnice.com/user/5303/5f7a3d15-9437-43e8-867f-3dbf2bd0a413.jpg\"]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.6',320,1,NULL,'2026-06-22 11:06:30','2026-06-22 11:06:30',NULL,NULL,0),(327283180983947264,'文件管理','URL转换','从URL转换图片','POST','/files/convert-url','[\"https://files.mdnice.com/user/5303/5f7a3d15-9437-43e8-867f-3dbf2bd0a413.jpg\"]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.6',558,1,NULL,'2026-06-22 11:06:30','2026-06-22 11:06:30',NULL,NULL,0),(327283182045106176,'文件管理','URL转换','从URL转换图片','POST','/files/convert-url','[\"https://files.mdnice.com/user/5303/e7dc868f-3068-4f27-9ed1-a8834d2ad2e8.png\"]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.6',475,1,NULL,'2026-06-22 11:06:31','2026-06-22 11:06:31',NULL,NULL,0),(327283182372261888,'文件管理','URL转换','从URL转换图片','POST','/files/convert-url','[\"https://files.mdnice.com/user/5303/e7dc868f-3068-4f27-9ed1-a8834d2ad2e8.png\"]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.6',271,1,NULL,'2026-06-22 11:06:31','2026-06-22 11:06:31',NULL,NULL,0),(327283183483752448,'文件管理','URL转换','从URL转换图片','POST','/files/convert-url','[\"https://files.mdnice.com/user/5303/39eee48c-73d6-4f24-a2d2-d89875182c03.jpg\"]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.6',321,1,NULL,'2026-06-22 11:06:31','2026-06-22 11:06:31',NULL,NULL,0),(327283184163229696,'文件管理','URL转换','从URL转换图片','POST','/files/convert-url','[\"https://files.mdnice.com/user/5303/39eee48c-73d6-4f24-a2d2-d89875182c03.jpg\"]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.6',425,1,NULL,'2026-06-22 11:06:31','2026-06-22 11:06:31',NULL,NULL,0),(327283185027256320,'文件管理','URL转换','从URL转换图片','POST','/files/convert-url','[\"https://files.mdnice.com/user/5303/1ab68e80-6a45-431d-a878-313ec74c031c.png\"]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.6',365,1,NULL,'2026-06-22 11:06:31','2026-06-22 11:06:31',NULL,NULL,0),(327283186256187392,'文件管理','URL转换','从URL转换图片','POST','/files/convert-url','[\"https://files.mdnice.com/user/5303/1ab68e80-6a45-431d-a878-313ec74c031c.png\"]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.6',475,1,NULL,'2026-06-22 11:06:32','2026-06-22 11:06:32',NULL,NULL,0),(327283318599061504,'文档审核','提交审核','提交文档审核','POST','/review/submit/327283313100328960','[327283313100328960]',NULL,1000000000000000001,'admin','0:0:0:0:0:0:0:1',NULL,'Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/149.0.0.0 Safari/537.36',800,1,NULL,'2026-06-22 11:07:03','2026-06-22 11:07:03',NULL,NULL,0),(327283348458311680,'文档审核','审核操作','审核文档','POST','/review/tasks/327283315486887936/review','[327283315486887936,{\"status\":\"approved\"}]',NULL,1000000000000000001,'admin','0:0:0:0:0:0:0:1',NULL,'Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/149.0.0.0 Safari/537.36',85,1,NULL,'2026-06-22 11:07:10','2026-06-22 11:07:10',NULL,NULL,0),(327306190675644416,'文件管理','URL转换','从URL转换图片','POST','/files/convert-url','[\"https://files.mdnice.com/user/5303/bd11aee8-ffc1-4c39-bbec-56676ca131f0.png\"]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.6',4317,1,NULL,'2026-06-22 12:37:56','2026-06-22 12:37:56',NULL,NULL,0),(327306194593124352,'文件管理','URL转换','从URL转换图片','POST','/files/convert-url','[\"https://files.mdnice.com/user/5303/94d37e69-251f-4eaf-b5b6-b5c48cd0c0f4.png\"]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.6',529,1,NULL,'2026-06-22 12:37:57','2026-06-22 12:37:57',NULL,NULL,0),(327306196811911168,'文件管理','URL转换','从URL转换图片','POST','/files/convert-url','[\"https://files.mdnice.com/user/5303/9c6cb4bd-eca6-458e-8098-13b7cde64b6a.png\"]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.6',498,1,NULL,'2026-06-22 12:37:58','2026-06-22 12:37:58',NULL,NULL,0),(327306199269773312,'文件管理','URL转换','从URL转换图片','POST','/files/convert-url','[\"https://files.mdnice.com/user/5303/be92b3fd-f13a-47c8-a758-2cc174dc29d9.png\"]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.6',537,1,NULL,'2026-06-22 12:37:58','2026-06-22 12:37:58',NULL,NULL,0),(327306201815715840,'文件管理','URL转换','从URL转换图片','POST','/files/convert-url','[\"https://files.mdnice.com/user/5303/efd68b18-24a8-4665-8b59-1d47a2e1fa8a.png\"]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.6',566,1,NULL,'2026-06-22 12:37:59','2026-06-22 12:37:59',NULL,NULL,0),(327306204965638144,'文件管理','URL转换','从URL转换图片','POST','/files/convert-url','[\"https://files.mdnice.com/user/5303/850b595e-aaf7-4530-a8f7-63e4fd46897b.png\"]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.6',683,1,NULL,'2026-06-22 12:38:00','2026-06-22 12:38:00',NULL,NULL,0),(327306389313687552,'文件管理','URL转换','从URL转换图片','POST','/files/convert-url','[\"https://files.mdnice.com/user/5303/bd11aee8-ffc1-4c39-bbec-56676ca131f0.png\"]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.6',650,1,NULL,'2026-06-22 12:38:44','2026-06-22 12:38:44',NULL,NULL,0),(327306391394062336,'文件管理','URL转换','从URL转换图片','POST','/files/convert-url','[\"https://files.mdnice.com/user/5303/94d37e69-251f-4eaf-b5b6-b5c48cd0c0f4.png\"]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.6',498,1,NULL,'2026-06-22 12:38:44','2026-06-22 12:38:44',NULL,NULL,0),(327306392295837696,'文件管理','URL转换','从URL转换图片','POST','/files/convert-url','[\"https://files.mdnice.com/user/5303/9c6cb4bd-eca6-458e-8098-13b7cde64b6a.png\"]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.6',202,1,NULL,'2026-06-22 12:38:44','2026-06-22 12:38:44',NULL,NULL,0),(327306393713512448,'文件管理','URL转换','从URL转换图片','POST','/files/convert-url','[\"https://files.mdnice.com/user/5303/be92b3fd-f13a-47c8-a758-2cc174dc29d9.png\"]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.6',317,1,NULL,'2026-06-22 12:38:45','2026-06-22 12:38:45',NULL,NULL,0),(327306395856801792,'文件管理','URL转换','从URL转换图片','POST','/files/convert-url','[\"https://files.mdnice.com/user/5303/efd68b18-24a8-4665-8b59-1d47a2e1fa8a.png\"]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.6',188,1,NULL,'2026-06-22 12:38:45','2026-06-22 12:38:45',NULL,NULL,0),(327306398012674048,'文件管理','URL转换','从URL转换图片','POST','/files/convert-url','[\"https://files.mdnice.com/user/5303/850b595e-aaf7-4530-a8f7-63e4fd46897b.png\"]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.6',433,1,NULL,'2026-06-22 12:38:46','2026-06-22 12:38:46',NULL,NULL,0),(327364790186020864,'文件管理','URL转换','从URL转换图片','POST','/files/convert-url','[\"https://files.mdnice.com/user/5303/e04e6055-ccf3-4159-8b88-ddc0a5927bbd.png\"]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.6',695,1,NULL,'2026-06-22 16:30:48','2026-06-22 16:30:48',NULL,NULL,0),(327366105335205888,'文件管理','URL转换','从URL转换图片','POST','/files/convert-url','[\"https://files.mdnice.com/user/5303/e04e6055-ccf3-4159-8b88-ddc0a5927bbd.png\"]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.6',254,1,NULL,'2026-06-22 16:36:01','2026-06-22 16:36:01',NULL,NULL,0),(327382756432678912,'评论管理','创建评论','创建文档评论','POST','/comments','[{\"content\":\"111\",\"documentId\":1000000000000000003}]',NULL,1000000000000000001,'admin','0:0:0:0:0:0:0:1',NULL,'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/149.0.0.0 Safari/537.36 Edg/149.0.0.0',6,0,'\r\n### Error updating database.  Cause: java.sql.SQLSyntaxErrorException: Unknown column \'commenter_id\' in \'field list\'\r\n### The error may exist in com/knowledge/base/document/mapper/CommentMapper.java (best guess)\r\n### The error may involve com.knowledge.base.document.mapper.CommentMapper.insert-Inline\r\n### The error occurred while setting parameters\r\n### SQL: INSERT INTO tb_comment  ( id, document_id, parent_id,  content, commenter_id, commenter_name, commenter_avatar,   status, like_count, rep...','2026-06-22 17:42:11','2026-06-22 17:42:11',NULL,NULL,0),(327392155385597952,'文件管理','URL转换','从URL转换图片','POST','/files/convert-url','[\"https://files.mdnice.com/user/5303/bab4b524-af4c-40e7-94f2-ec0a135a7412.jpg\"]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.6',2303,1,NULL,'2026-06-22 18:19:32','2026-06-22 18:19:32',NULL,NULL,0),(327393340049985536,'文件管理','URL转换','从URL转换图片','POST','/files/convert-url','[\"https://files.mdnice.com/user/5303/8fe720e2-2ccc-4169-9ba9-eeb56fd61533.png\"]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.6',860,1,NULL,'2026-06-22 18:24:14','2026-06-22 18:24:14',NULL,NULL,0),(327393346265944064,'文件管理','URL转换','从URL转换图片','POST','/files/convert-url','[\"https://files.mdnice.com/user/5303/dea6c4db-7eaa-4652-b31b-9493381e8cf1.png\"]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.6',711,1,NULL,'2026-06-22 18:24:16','2026-06-22 18:24:16',NULL,NULL,0),(327393522019864576,'文档审核','审核操作','审核文档','POST','/review/tasks/327393466617303040/review','[327393466617303040,{\"status\":\"approved\"}]',NULL,1000000000000000001,'admin','0:0:0:0:0:0:0:1',NULL,'Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/149.0.0.0 Safari/537.36',103,1,NULL,'2026-06-22 18:24:58','2026-06-22 18:24:58',NULL,NULL,0),(327394605421170688,'文件管理','URL转换','从URL转换图片','POST','/files/convert-url','[\"https://files.mdnice.com/user/5303/8e74be3b-f4c4-4bf4-b115-59f61711ecff.png\"]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.6',157,1,NULL,'2026-06-22 18:29:16','2026-06-22 18:29:16',NULL,NULL,0),(327394796534632448,'文件管理','URL转换','从URL转换图片','POST','/files/convert-url','[\"https://files.mdnice.com/user/5303/8fe720e2-2ccc-4169-9ba9-eeb56fd61533.png\"]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.6',468,1,NULL,'2026-06-22 18:30:02','2026-06-22 18:30:02',NULL,NULL,0),(327394798849888256,'文件管理','URL转换','从URL转换图片','POST','/files/convert-url','[\"https://files.mdnice.com/user/5303/8fe720e2-2ccc-4169-9ba9-eeb56fd61533.png\"]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.6',242,1,NULL,'2026-06-22 18:30:02','2026-06-22 18:30:02',NULL,NULL,0),(327394799898464256,'文件管理','URL转换','从URL转换图片','POST','/files/convert-url','[\"https://files.mdnice.com/user/5303/8e74be3b-f4c4-4bf4-b115-59f61711ecff.png\"]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.6',162,1,NULL,'2026-06-22 18:30:02','2026-06-22 18:30:02',NULL,NULL,0),(327394902356922368,'文件管理','URL转换','从URL转换图片','POST','/files/convert-url','[\"https://files.mdnice.com/user/5303/8fe720e2-2ccc-4169-9ba9-eeb56fd61533.png\"]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.6',307,1,NULL,'2026-06-22 18:30:27','2026-06-22 18:30:27',NULL,NULL,0),(327394904005283840,'文件管理','URL转换','从URL转换图片','POST','/files/convert-url','[\"https://files.mdnice.com/user/5303/dea6c4db-7eaa-4652-b31b-9493381e8cf1.png\"]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.6',123,1,NULL,'2026-06-22 18:30:27','2026-06-22 18:30:27',NULL,NULL,0),(327395634418159616,'文档审核','审核操作','审核文档','POST','/review/tasks/327394904475045888/review','[327394904475045888,{\"status\":\"approved\"}]',NULL,1000000000000000001,'admin','0:0:0:0:0:0:0:1',NULL,'Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/149.0.0.0 Safari/537.36',67,1,NULL,'2026-06-22 18:33:21','2026-06-22 18:33:21',NULL,NULL,0),(327395767075606528,'文件管理','URL转换','从URL转换图片','POST','/files/convert-url','[\"https://files.mdnice.com/user/5303/37edfd9a-876f-4aae-9b33-fb95efdfea40.png\"]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.6',471,1,NULL,'2026-06-22 18:33:53','2026-06-22 18:33:53',NULL,NULL,0),(327395936068308992,'文档审核','审核操作','审核文档','POST','/review/tasks/327395870851076096/review','[327395870851076096,{\"status\":\"approved\"}]',NULL,1000000000000000001,'admin','0:0:0:0:0:0:0:1',NULL,'Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/149.0.0.0 Safari/537.36',381,1,NULL,'2026-06-22 18:34:33','2026-06-22 18:34:33',NULL,NULL,0),(327396052657377280,'文件管理','URL转换','从URL转换图片','POST','/files/convert-url','[\"https://files.mdnice.com/user/5303/03dac28b-674d-4419-a023-c3a135429c35.png\"]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.6',530,1,NULL,'2026-06-22 18:35:01','2026-06-22 18:35:01',NULL,NULL,0),(327396055194931200,'文件管理','URL转换','从URL转换图片','POST','/files/convert-url','[\"https://files.mdnice.com/user/5303/b8e0a6f7-0b0d-49e2-b507-b23ec0f48563.png\"]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.6',420,1,NULL,'2026-06-22 18:35:02','2026-06-22 18:35:02',NULL,NULL,0),(327396056079929344,'文件管理','URL转换','从URL转换图片','POST','/files/convert-url','[\"https://files.mdnice.com/user/5303/982bfebb-9392-4122-a519-6a1cfaeceb84.png\"]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.6',382,1,NULL,'2026-06-22 18:35:02','2026-06-22 18:35:02',NULL,NULL,0),(327396058109972480,'文件管理','URL转换','从URL转换图片','POST','/files/convert-url','[\"https://files.mdnice.com/user/5303/982bfebb-9392-4122-a519-6a1cfaeceb84.png\"]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.6',317,1,NULL,'2026-06-22 18:35:02','2026-06-22 18:35:02',NULL,NULL,0),(327468811437608960,'文档审核','审核操作','审核文档','POST','/review/tasks/327468471090810880/review','[327468471090810880,{\"status\":\"approved\"}]',NULL,1000000000000000001,'admin','0:0:0:0:0:0:0:1',NULL,'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/148.0.0.0 Safari/537.36',1296,1,NULL,'2026-06-22 23:24:08','2026-06-22 23:24:08',NULL,NULL,0),(327469655855861760,'文件管理','上传文件','上传文件','POST','/files/upload','[{\"type\":\"MultipartFile\",\"size\":129136,\"originalFilename\":\"pdf_img_p102_0.png\"},{\"accessLevel\":1,\"fileType\":\"document\"}]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.9',823,1,NULL,'2026-06-22 23:27:30','2026-06-22 23:27:30',NULL,NULL,0),(327469710679609344,'文件管理','上传文件','上传文件','POST','/files/upload','[{\"type\":\"MultipartFile\",\"size\":125565,\"originalFilename\":\"pdf_img_p104_1.png\"},{\"accessLevel\":1,\"fileType\":\"document\"}]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.9',1063,1,NULL,'2026-06-22 23:27:43','2026-06-22 23:27:43',NULL,NULL,0),(327469728350212096,'文件管理','上传文件','上传文件','POST','/files/upload','[{\"type\":\"MultipartFile\",\"size\":149179,\"originalFilename\":\"pdf_img_p107_0.png\"},{\"accessLevel\":1,\"fileType\":\"document\"}]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.9',1135,1,NULL,'2026-06-22 23:27:47','2026-06-22 23:27:47',NULL,NULL,0),(327469749120405504,'文件管理','上传文件','上传文件','POST','/files/upload','[{\"type\":\"MultipartFile\",\"size\":72557,\"originalFilename\":\"pdf_img_p110_0.png\"},{\"accessLevel\":1,\"fileType\":\"document\"}]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.9',1035,1,NULL,'2026-06-22 23:27:52','2026-06-22 23:27:52',NULL,NULL,0),(327469773095047168,'文件管理','上传文件','上传文件','POST','/files/upload','[{\"type\":\"MultipartFile\",\"size\":242439,\"originalFilename\":\"pdf_img_p112_1.png\"},{\"accessLevel\":1,\"fileType\":\"document\"}]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.9',1068,1,NULL,'2026-06-22 23:27:58','2026-06-22 23:27:58',NULL,NULL,0),(327469791126360064,'文件管理','上传文件','上传文件','POST','/files/upload','[{\"type\":\"MultipartFile\",\"size\":100399,\"originalFilename\":\"pdf_img_p115_0.png\"},{\"accessLevel\":1,\"fileType\":\"document\"}]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.9',600,1,NULL,'2026-06-22 23:28:02','2026-06-22 23:28:02',NULL,NULL,0),(327469807530283008,'文件管理','上传文件','上传文件','POST','/files/upload','[{\"type\":\"MultipartFile\",\"size\":143610,\"originalFilename\":\"pdf_img_p118_0.png\"},{\"accessLevel\":1,\"fileType\":\"document\"}]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.9',763,1,NULL,'2026-06-22 23:28:06','2026-06-22 23:28:06',NULL,NULL,0),(327469822961127424,'文件管理','上传文件','上传文件','POST','/files/upload','[{\"type\":\"MultipartFile\",\"size\":84255,\"originalFilename\":\"pdf_img_p120_1.png\"},{\"accessLevel\":1,\"fileType\":\"document\"}]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.9',621,1,NULL,'2026-06-22 23:28:09','2026-06-22 23:28:09',NULL,NULL,0),(327469837481807872,'文件管理','上传文件','上传文件','POST','/files/upload','[{\"type\":\"MultipartFile\",\"size\":106840,\"originalFilename\":\"pdf_img_p123_0.png\"},{\"accessLevel\":1,\"fileType\":\"document\"}]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.9',861,1,NULL,'2026-06-22 23:28:13','2026-06-22 23:28:13',NULL,NULL,0),(327469858361053184,'文件管理','上传文件','上传文件','POST','/files/upload','[{\"type\":\"MultipartFile\",\"size\":80708,\"originalFilename\":\"pdf_img_p126_0.png\"},{\"accessLevel\":1,\"fileType\":\"document\"}]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.9',880,1,NULL,'2026-06-22 23:28:18','2026-06-22 23:28:18',NULL,NULL,0),(327469879244492800,'文件管理','上传文件','上传文件','POST','/files/upload','[{\"type\":\"MultipartFile\",\"size\":109751,\"originalFilename\":\"pdf_img_p128_0.png\"},{\"accessLevel\":1,\"fileType\":\"document\"}]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.9',1041,1,NULL,'2026-06-22 23:28:23','2026-06-22 23:28:23',NULL,NULL,0),(327469894763417600,'文件管理','上传文件','上传文件','POST','/files/upload','[{\"type\":\"MultipartFile\",\"size\":158962,\"originalFilename\":\"pdf_img_p131_0.png\"},{\"accessLevel\":1,\"fileType\":\"document\"}]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.9',874,1,NULL,'2026-06-22 23:28:27','2026-06-22 23:28:27',NULL,NULL,0),(327469909074382848,'文件管理','上传文件','上传文件','POST','/files/upload','[{\"type\":\"MultipartFile\",\"size\":130985,\"originalFilename\":\"pdf_img_p133_1.png\"},{\"accessLevel\":1,\"fileType\":\"document\"}]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.9',1000,1,NULL,'2026-06-22 23:28:30','2026-06-22 23:28:30',NULL,NULL,0),(327469924102574080,'文件管理','上传文件','上传文件','POST','/files/upload','[{\"type\":\"MultipartFile\",\"size\":99340,\"originalFilename\":\"pdf_img_p136_1.png\"},{\"accessLevel\":1,\"fileType\":\"document\"}]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.9',679,1,NULL,'2026-06-22 23:28:34','2026-06-22 23:28:34',NULL,NULL,0),(327469944092626944,'文件管理','上传文件','上传文件','POST','/files/upload','[{\"type\":\"MultipartFile\",\"size\":75417,\"originalFilename\":\"pdf_img_p139_0.png\"},{\"accessLevel\":1,\"fileType\":\"document\"}]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.9',1994,1,NULL,'2026-06-22 23:28:38','2026-06-22 23:28:38',NULL,NULL,0),(327469960198754304,'文件管理','上传文件','上传文件','POST','/files/upload','[{\"type\":\"MultipartFile\",\"size\":201484,\"originalFilename\":\"pdf_img_p142_0.png\"},{\"accessLevel\":1,\"fileType\":\"document\"}]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.9',885,1,NULL,'2026-06-22 23:28:42','2026-06-22 23:28:42',NULL,NULL,0),(327469981023473664,'文件管理','上传文件','上传文件','POST','/files/upload','[{\"type\":\"MultipartFile\",\"size\":139244,\"originalFilename\":\"pdf_img_p145_0.png\"},{\"accessLevel\":1,\"fileType\":\"document\"}]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.9',500,1,NULL,'2026-06-22 23:28:47','2026-06-22 23:28:47',NULL,NULL,0),(327469999709097984,'文件管理','上传文件','上传文件','POST','/files/upload','[{\"type\":\"MultipartFile\",\"size\":200480,\"originalFilename\":\"pdf_img_p149_0.png\"},{\"accessLevel\":1,\"fileType\":\"document\"}]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.9',879,1,NULL,'2026-06-22 23:28:52','2026-06-22 23:28:52',NULL,NULL,0),(327470006680031232,'文件管理','上传文件','上传文件','POST','/files/upload','[{\"type\":\"MultipartFile\",\"size\":158228,\"originalFilename\":\"pdf_img_p101_0.png\"},{\"accessLevel\":1,\"fileType\":\"document\"}]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.9',299,1,NULL,'2026-06-22 23:28:53','2026-06-22 23:28:53',NULL,NULL,0),(327470013663547392,'文件管理','上传文件','上传文件','POST','/files/upload','[{\"type\":\"MultipartFile\",\"size\":83667,\"originalFilename\":\"pdf_img_p103_0.png\"},{\"accessLevel\":1,\"fileType\":\"document\"}]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.9',540,1,NULL,'2026-06-22 23:28:55','2026-06-22 23:28:55',NULL,NULL,0),(327470018705100800,'文件管理','上传文件','上传文件','POST','/files/upload','[{\"type\":\"MultipartFile\",\"size\":167894,\"originalFilename\":\"pdf_img_p153_0.png\"},{\"accessLevel\":1,\"fileType\":\"document\"}]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.9',808,1,NULL,'2026-06-22 23:28:56','2026-06-22 23:28:56',NULL,NULL,0),(327470022878433280,'文件管理','上传文件','上传文件','POST','/files/upload','[{\"type\":\"MultipartFile\",\"size\":123023,\"originalFilename\":\"pdf_img_p106_0.png\"},{\"accessLevel\":1,\"fileType\":\"document\"}]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.9',286,1,NULL,'2026-06-22 23:28:57','2026-06-22 23:28:57',NULL,NULL,0),(327470032093319168,'文件管理','上传文件','上传文件','POST','/files/upload','[{\"type\":\"MultipartFile\",\"size\":155670,\"originalFilename\":\"pdf_img_p108_0.png\"},{\"accessLevel\":1,\"fileType\":\"document\"}]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.9',268,1,NULL,'2026-06-22 23:28:59','2026-06-22 23:28:59',NULL,NULL,0),(327470039286550528,'文件管理','上传文件','上传文件','POST','/files/upload','[{\"type\":\"MultipartFile\",\"size\":241484,\"originalFilename\":\"pdf_img_p155_1.png\"},{\"accessLevel\":1,\"fileType\":\"document\"}]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.9',1252,1,NULL,'2026-06-22 23:29:01','2026-06-22 23:29:01',NULL,NULL,0),(327470043866730496,'文件管理','上传文件','上传文件','POST','/files/upload','[{\"type\":\"MultipartFile\",\"size\":115674,\"originalFilename\":\"pdf_img_p111_1.png\"},{\"accessLevel\":1,\"fileType\":\"document\"}]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.9',245,1,NULL,'2026-06-22 23:29:02','2026-06-22 23:29:02',NULL,NULL,0),(327470051886239744,'文件管理','上传文件','上传文件','POST','/files/upload','[{\"type\":\"MultipartFile\",\"size\":33665,\"originalFilename\":\"pdf_img_p113_0.png\"},{\"accessLevel\":1,\"fileType\":\"document\"}]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.9',808,1,NULL,'2026-06-22 23:29:04','2026-06-22 23:29:04',NULL,NULL,0),(327470058773286912,'文件管理','上传文件','上传文件','POST','/files/upload','[{\"type\":\"MultipartFile\",\"size\":267858,\"originalFilename\":\"pdf_img_p158_0.png\"},{\"accessLevel\":1,\"fileType\":\"document\"}]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.9',1457,1,NULL,'2026-06-22 23:29:06','2026-06-22 23:29:06',NULL,NULL,0),(327470067459690496,'文件管理','上传文件','上传文件','POST','/files/upload','[{\"type\":\"MultipartFile\",\"size\":155536,\"originalFilename\":\"pdf_img_p117_0.png\"},{\"accessLevel\":1,\"fileType\":\"document\"}]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.9',591,1,NULL,'2026-06-22 23:29:08','2026-06-22 23:29:08',NULL,NULL,0),(327470075634388992,'文件管理','上传文件','上传文件','POST','/files/upload','[{\"type\":\"MultipartFile\",\"size\":116484,\"originalFilename\":\"pdf_img_p119_0.png\"},{\"accessLevel\":1,\"fileType\":\"document\"}]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.9',765,1,NULL,'2026-06-22 23:29:10','2026-06-22 23:29:10',NULL,NULL,0),(327470084027191296,'文件管理','上传文件','上传文件','POST','/files/upload','[{\"type\":\"MultipartFile\",\"size\":84255,\"originalFilename\":\"pdf_img_p120_1.png\"},{\"accessLevel\":1,\"fileType\":\"document\"}]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.9',453,1,NULL,'2026-06-22 23:29:12','2026-06-22 23:29:12',NULL,NULL,0),(327470089681113088,'文件管理','上传文件','上传文件','POST','/files/upload','[{\"type\":\"MultipartFile\",\"size\":122305,\"originalFilename\":\"pdf_img_p122_1.png\"},{\"accessLevel\":1,\"fileType\":\"document\"}]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.9',667,1,NULL,'2026-06-22 23:29:13','2026-06-22 23:29:13',NULL,NULL,0),(327470094940770304,'文件管理','上传文件','上传文件','POST','/files/upload','[{\"type\":\"MultipartFile\",\"size\":122724,\"originalFilename\":\"pdf_img_p161_1.png\"},{\"accessLevel\":1,\"fileType\":\"document\"}]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.9',1216,1,NULL,'2026-06-22 23:29:14','2026-06-22 23:29:14',NULL,NULL,0),(327470103660728320,'文件管理','上传文件','上传文件','POST','/files/upload','[{\"type\":\"MultipartFile\",\"size\":160384,\"originalFilename\":\"pdf_img_p162_0.png\"},{\"accessLevel\":1,\"fileType\":\"document\"}]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.9',2059,1,NULL,'2026-06-22 23:29:16','2026-06-22 23:29:16',NULL,NULL,0),(327470111101423616,'文件管理','上传文件','上传文件','POST','/files/upload','[{\"type\":\"MultipartFile\",\"size\":69822,\"originalFilename\":\"pdf_img_p127_1.png\"},{\"accessLevel\":1,\"fileType\":\"document\"}]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.9',249,1,NULL,'2026-06-22 23:29:18','2026-06-22 23:29:18',NULL,NULL,0),(327470119913656320,'文件管理','上传文件','上传文件','POST','/files/upload','[{\"type\":\"MultipartFile\",\"size\":82772,\"originalFilename\":\"pdf_img_p164_1.png\"},{\"accessLevel\":1,\"fileType\":\"document\"}]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.9',1014,1,NULL,'2026-06-22 23:29:20','2026-06-22 23:29:20',NULL,NULL,0),(327470124229595136,'文件管理','上传文件','上传文件','POST','/files/upload','[{\"type\":\"MultipartFile\",\"size\":158962,\"originalFilename\":\"pdf_img_p131_0.png\"},{\"accessLevel\":1,\"fileType\":\"document\"}]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.9',236,1,NULL,'2026-06-22 23:29:21','2026-06-22 23:29:21',NULL,NULL,0),(327470131770953728,'文件管理','上传文件','上传文件','POST','/files/upload','[{\"type\":\"MultipartFile\",\"size\":152197,\"originalFilename\":\"pdf_img_p133_0.png\"},{\"accessLevel\":1,\"fileType\":\"document\"}]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.9',895,1,NULL,'2026-06-22 23:29:23','2026-06-22 23:29:23',NULL,NULL,0),(327470139677216768,'文件管理','上传文件','上传文件','POST','/files/upload','[{\"type\":\"MultipartFile\",\"size\":103488,\"originalFilename\":\"pdf_img_p135_0.png\"},{\"accessLevel\":1,\"fileType\":\"document\"}]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.9',594,1,NULL,'2026-06-22 23:29:25','2026-06-22 23:29:25',NULL,NULL,0),(327470145607962624,'文件管理','上传文件','上传文件','POST','/files/upload','[{\"type\":\"MultipartFile\",\"size\":169322,\"originalFilename\":\"pdf_img_p167_1.png\"},{\"accessLevel\":1,\"fileType\":\"document\"}]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.9',1243,1,NULL,'2026-06-22 23:29:26','2026-06-22 23:29:26',NULL,NULL,0),(327470157121327104,'文件管理','上传文件','上传文件','POST','/files/upload','[{\"type\":\"MultipartFile\",\"size\":130694,\"originalFilename\":\"pdf_img_p138_1.png\"},{\"accessLevel\":1,\"fileType\":\"document\"}]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.9',581,1,NULL,'2026-06-22 23:29:29','2026-06-22 23:29:29',NULL,NULL,0),(327470162955603968,'文件管理','上传文件','上传文件','POST','/files/upload','[{\"type\":\"MultipartFile\",\"size\":130810,\"originalFilename\":\"pdf_img_p140_1.png\"},{\"accessLevel\":1,\"fileType\":\"document\"}]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.9',258,1,NULL,'2026-06-22 23:29:30','2026-06-22 23:29:30',NULL,NULL,0),(327470173311340544,'文件管理','上传文件','上传文件','POST','/files/upload','[{\"type\":\"MultipartFile\",\"size\":226268,\"originalFilename\":\"pdf_img_p170_0.png\"},{\"accessLevel\":1,\"fileType\":\"document\"}]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.9',1574,1,NULL,'2026-06-22 23:29:33','2026-06-22 23:29:33',NULL,NULL,0),(327470178386448384,'文件管理','上传文件','上传文件','POST','/files/upload','[{\"type\":\"MultipartFile\",\"size\":221863,\"originalFilename\":\"pdf_img_p144_0.png\"},{\"accessLevel\":1,\"fileType\":\"document\"}]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.9',604,1,NULL,'2026-06-22 23:29:34','2026-06-22 23:29:34',NULL,NULL,0),(327470190487015424,'文件管理','上传文件','上传文件','POST','/files/upload','[{\"type\":\"MultipartFile\",\"size\":140763,\"originalFilename\":\"pdf_img_p147_0.png\"},{\"accessLevel\":1,\"fileType\":\"document\"}]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.9',608,1,NULL,'2026-06-22 23:29:37','2026-06-22 23:29:37',NULL,NULL,0),(327470195721506816,'文件管理','上传文件','上传文件','POST','/files/upload','[{\"type\":\"MultipartFile\",\"size\":63955,\"originalFilename\":\"pdf_img_p172_0.png\"},{\"accessLevel\":1,\"fileType\":\"document\"}]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.9',993,1,NULL,'2026-06-22 23:29:38','2026-06-22 23:29:38',NULL,NULL,0),(327470200503013376,'文件管理','上传文件','上传文件','POST','/files/upload','[{\"type\":\"MultipartFile\",\"size\":218674,\"originalFilename\":\"pdf_img_p151_1.png\"},{\"accessLevel\":1,\"fileType\":\"document\"}]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.9',292,1,NULL,'2026-06-22 23:29:39','2026-06-22 23:29:39',NULL,NULL,0),(327470208761597952,'文件管理','上传文件','上传文件','POST','/files/upload','[{\"type\":\"MultipartFile\",\"size\":233639,\"originalFilename\":\"pdf_img_p153_1.png\"},{\"accessLevel\":1,\"fileType\":\"document\"}]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.9',799,1,NULL,'2026-06-22 23:29:41','2026-06-22 23:29:41',NULL,NULL,0),(327470215891914752,'文件管理','上传文件','上传文件','POST','/files/upload','[{\"type\":\"MultipartFile\",\"size\":241484,\"originalFilename\":\"pdf_img_p155_1.png\"},{\"accessLevel\":1,\"fileType\":\"document\"}]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.9',595,1,NULL,'2026-06-22 23:29:43','2026-06-22 23:29:43',NULL,NULL,0),(327470221080268800,'文件管理','上传文件','上传文件','POST','/files/upload','[{\"type\":\"MultipartFile\",\"size\":318824,\"originalFilename\":\"pdf_img_p176_0.png\"},{\"accessLevel\":1,\"fileType\":\"document\"}]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.9',1117,1,NULL,'2026-06-22 23:29:44','2026-06-22 23:29:44',NULL,NULL,0),(327470228252528640,'文件管理','上传文件','上传文件','POST','/files/upload','[{\"type\":\"MultipartFile\",\"size\":433088,\"originalFilename\":\"pdf_img_p158_1.png\"},{\"accessLevel\":1,\"fileType\":\"document\"}]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.9',544,1,NULL,'2026-06-22 23:29:46','2026-06-22 23:29:46',NULL,NULL,0),(327470234011308032,'文件管理','上传文件','上传文件','POST','/files/upload','[{\"type\":\"MultipartFile\",\"size\":180958,\"originalFilename\":\"pdf_img_p177_1.png\"},{\"accessLevel\":1,\"fileType\":\"document\"}]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.9',1091,1,NULL,'2026-06-22 23:29:47','2026-06-22 23:29:47',NULL,NULL,0),(327470238658596864,'文件管理','上传文件','上传文件','POST','/files/upload','[{\"type\":\"MultipartFile\",\"size\":122724,\"originalFilename\":\"pdf_img_p161_1.png\"},{\"accessLevel\":1,\"fileType\":\"document\"}]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.9',227,1,NULL,'2026-06-22 23:29:48','2026-06-22 23:29:48',NULL,NULL,0),(327470249270185984,'文件管理','上传文件','上传文件','POST','/files/upload','[{\"type\":\"MultipartFile\",\"size\":154281,\"originalFilename\":\"pdf_img_p164_0.png\"},{\"accessLevel\":1,\"fileType\":\"document\"}]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.9',541,1,NULL,'2026-06-22 23:29:51','2026-06-22 23:29:51',NULL,NULL,0),(327470259252629504,'文件管理','上传文件','上传文件','POST','/files/upload','[{\"type\":\"MultipartFile\",\"size\":108099,\"originalFilename\":\"pdf_img_p180_0.png\"},{\"accessLevel\":1,\"fileType\":\"document\"}]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.9',1955,1,NULL,'2026-06-22 23:29:53','2026-06-22 23:29:53',NULL,NULL,0),(327470266085150720,'文件管理','上传文件','上传文件','POST','/files/upload','[{\"type\":\"MultipartFile\",\"size\":159609,\"originalFilename\":\"pdf_img_p180_1.png\"},{\"accessLevel\":1,\"fileType\":\"document\"}]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.9',1574,1,NULL,'2026-06-22 23:29:55','2026-06-22 23:29:55',NULL,NULL,0),(327470272049451008,'文件管理','上传文件','上传文件','POST','/files/upload','[{\"type\":\"MultipartFile\",\"size\":140069,\"originalFilename\":\"pdf_img_p168_1.png\"},{\"accessLevel\":1,\"fileType\":\"document\"}]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.9',273,1,NULL,'2026-06-22 23:29:56','2026-06-22 23:29:56',NULL,NULL,0),(327470283801890816,'文件管理','上传文件','上传文件','POST','/files/upload','[{\"type\":\"MultipartFile\",\"size\":128305,\"originalFilename\":\"pdf_img_p171_0.png\"},{\"accessLevel\":1,\"fileType\":\"document\"}]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.9',421,1,NULL,'2026-06-22 23:29:59','2026-06-22 23:29:59',NULL,NULL,0),(327470292094029824,'文件管理','上传文件','上传文件','POST','/files/upload','[{\"type\":\"MultipartFile\",\"size\":217035,\"originalFilename\":\"pdf_img_p173_0.png\"},{\"accessLevel\":1,\"fileType\":\"document\"}]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.9',599,1,NULL,'2026-06-22 23:30:01','2026-06-22 23:30:01',NULL,NULL,0),(327470301858369536,'文件管理','上传文件','上传文件','POST','/files/upload','[{\"type\":\"MultipartFile\",\"size\":119722,\"originalFilename\":\"pdf_img_p184_0.png\"},{\"accessLevel\":1,\"fileType\":\"document\"}]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.9',1272,1,NULL,'2026-06-22 23:30:04','2026-06-22 23:30:04',NULL,NULL,0),(327470308728639488,'文件管理','上传文件','上传文件','POST','/files/upload','[{\"type\":\"MultipartFile\",\"size\":109751,\"originalFilename\":\"pdf_img_p185_0.png\"},{\"accessLevel\":1,\"fileType\":\"document\"}]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.9',572,1,NULL,'2026-06-22 23:30:05','2026-06-22 23:30:05',NULL,NULL,0),(327470314021851136,'文件管理','上传文件','上传文件','POST','/files/upload','[{\"type\":\"MultipartFile\",\"size\":109674,\"originalFilename\":\"pdf_img_p186_0.png\"},{\"accessLevel\":1,\"fileType\":\"document\"}]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.9',291,1,NULL,'2026-06-22 23:30:06','2026-06-22 23:30:06',NULL,NULL,0),(327470321043116032,'文件管理','上传文件','上传文件','POST','/files/upload','[{\"type\":\"MultipartFile\",\"size\":140671,\"originalFilename\":\"pdf_img_p181_0.png\"},{\"accessLevel\":1,\"fileType\":\"document\"}]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.9',526,1,NULL,'2026-06-22 23:30:08','2026-06-22 23:30:08',NULL,NULL,0),(327470326575403008,'文件管理','上传文件','上传文件','POST','/files/upload','[{\"type\":\"MultipartFile\",\"size\":152491,\"originalFilename\":\"pdf_img_p182_1.png\"},{\"accessLevel\":1,\"fileType\":\"document\"}]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.9',294,1,NULL,'2026-06-22 23:30:09','2026-06-22 23:30:09',NULL,NULL,0),(327470331994443776,'文件管理','上传文件','上传文件','POST','/files/upload','[{\"type\":\"MultipartFile\",\"size\":106667,\"originalFilename\":\"pdf_img_p189_0.png\"},{\"accessLevel\":1,\"fileType\":\"document\"}]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.9',490,1,NULL,'2026-06-22 23:30:11','2026-06-22 23:30:11',NULL,NULL,0),(327470342165630976,'文件管理','上传文件','上传文件','POST','/files/upload','[{\"type\":\"MultipartFile\",\"size\":102299,\"originalFilename\":\"pdf_img_p187_0.png\"},{\"accessLevel\":1,\"fileType\":\"document\"}]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.9',586,1,NULL,'2026-06-22 23:30:13','2026-06-22 23:30:13',NULL,NULL,0),(327470354656268288,'文件管理','上传文件','上传文件','POST','/files/upload','[{\"type\":\"MultipartFile\",\"size\":98291,\"originalFilename\":\"pdf_img_p191_0.png\"},{\"accessLevel\":1,\"fileType\":\"document\"}]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.9',1685,1,NULL,'2026-06-22 23:30:16','2026-06-22 23:30:16',NULL,NULL,0),(327470361409097728,'文件管理','上传文件','上传文件','POST','/files/upload','[{\"type\":\"MultipartFile\",\"size\":228950,\"originalFilename\":\"pdf_img_p192_0.png\"},{\"accessLevel\":1,\"fileType\":\"document\"}]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.9',1518,1,NULL,'2026-06-22 23:30:18','2026-06-22 23:30:18',NULL,NULL,0),(327470369197920256,'文件管理','上传文件','上传文件','POST','/files/upload','[{\"type\":\"MultipartFile\",\"size\":228950,\"originalFilename\":\"pdf_img_p192_0.png\"},{\"accessLevel\":1,\"fileType\":\"document\"}]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.9',807,1,NULL,'2026-06-22 23:30:20','2026-06-22 23:30:20',NULL,NULL,0),(327470373501276160,'文件管理','上传文件','上传文件','POST','/files/upload','[{\"type\":\"MultipartFile\",\"size\":166253,\"originalFilename\":\"pdf_img_p193_0.png\"},{\"accessLevel\":1,\"fileType\":\"document\"}]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.9',945,1,NULL,'2026-06-22 23:30:21','2026-06-22 23:30:21',NULL,NULL,0),(327470382170902528,'文件管理','上传文件','上传文件','POST','/files/upload','[{\"type\":\"MultipartFile\",\"size\":133561,\"originalFilename\":\"pdf_img_p105_0.png\"},{\"accessLevel\":1,\"fileType\":\"document\"}]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.9',259,1,NULL,'2026-06-22 23:30:23','2026-06-22 23:30:23',NULL,NULL,0),(327470388613353472,'文件管理','上传文件','上传文件','POST','/files/upload','[{\"type\":\"MultipartFile\",\"size\":160872,\"originalFilename\":\"pdf_img_p194_1.png\"},{\"accessLevel\":1,\"fileType\":\"document\"}]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.9',1330,1,NULL,'2026-06-22 23:30:24','2026-06-22 23:30:24',NULL,NULL,0),(327470394518933504,'文件管理','上传文件','上传文件','POST','/files/upload','[{\"type\":\"MultipartFile\",\"size\":113851,\"originalFilename\":\"pdf_img_p109_0.png\"},{\"accessLevel\":1,\"fileType\":\"document\"}]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.9',865,1,NULL,'2026-06-22 23:30:26','2026-06-22 23:30:26',NULL,NULL,0),(327470401175293952,'文件管理','上传文件','上传文件','POST','/files/upload','[{\"type\":\"MultipartFile\",\"size\":126757,\"originalFilename\":\"pdf_img_p111_0.png\"},{\"accessLevel\":1,\"fileType\":\"document\"}]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.9',591,1,NULL,'2026-06-22 23:30:27','2026-06-22 23:30:27',NULL,NULL,0),(327470403649933312,'文件管理','上传文件','上传文件','POST','/files/upload','[{\"type\":\"MultipartFile\",\"size\":54492,\"originalFilename\":\"pdf_img_p112_0.png\"},{\"accessLevel\":1,\"fileType\":\"document\"}]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.9',257,1,NULL,'2026-06-22 23:30:28','2026-06-22 23:30:28',NULL,NULL,0),(327470410109161472,'文件管理','上传文件','上传文件','POST','/files/upload','[{\"type\":\"MultipartFile\",\"size\":33665,\"originalFilename\":\"pdf_img_p113_0.png\"},{\"accessLevel\":1,\"fileType\":\"document\"}]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.9',624,1,NULL,'2026-06-22 23:30:29','2026-06-22 23:30:29',NULL,NULL,0),(327470416048295936,'文件管理','上传文件','上传文件','POST','/files/upload','[{\"type\":\"MultipartFile\",\"size\":71268,\"originalFilename\":\"pdf_img_p114_0.png\"},{\"accessLevel\":1,\"fileType\":\"document\"}]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.9',1104,1,NULL,'2026-06-22 23:30:31','2026-06-22 23:30:31',NULL,NULL,0),(327470420615892992,'文件管理','上传文件','上传文件','POST','/files/upload','[{\"type\":\"MultipartFile\",\"size\":194523,\"originalFilename\":\"pdf_img_p116_0.png\"},{\"accessLevel\":1,\"fileType\":\"document\"}]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.9',292,1,NULL,'2026-06-22 23:30:32','2026-06-22 23:30:32',NULL,NULL,0),(327470429251964928,'文件管理','上传文件','上传文件','POST','/files/upload','[{\"type\":\"MultipartFile\",\"size\":91939,\"originalFilename\":\"pdf_img_p117_1.png\"},{\"accessLevel\":1,\"fileType\":\"document\"}]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.9',1025,1,NULL,'2026-06-22 23:30:34','2026-06-22 23:30:34',NULL,NULL,0),(327470432850677760,'文件管理','上传文件','上传文件','POST','/files/upload','[{\"type\":\"MultipartFile\",\"size\":116484,\"originalFilename\":\"pdf_img_p119_0.png\"},{\"accessLevel\":1,\"fileType\":\"document\"}]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.9',249,1,NULL,'2026-06-22 23:30:35','2026-06-22 23:30:35',NULL,NULL,0),(327470439771279360,'文件管理','上传文件','上传文件','POST','/files/upload','[{\"type\":\"MultipartFile\",\"size\":29003,\"originalFilename\":\"pdf_img_p120_0.png\"},{\"accessLevel\":1,\"fileType\":\"document\"}]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.9',899,1,NULL,'2026-06-22 23:30:36','2026-06-22 23:30:36',NULL,NULL,0),(327470445450366976,'文件管理','上传文件','上传文件','POST','/files/upload','[{\"type\":\"MultipartFile\",\"size\":88833,\"originalFilename\":\"pdf_img_p200_1.png\"},{\"accessLevel\":1,\"fileType\":\"document\"}]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.9',2025,1,NULL,'2026-06-22 23:30:38','2026-06-22 23:30:38',NULL,NULL,0),(327470450043129856,'文件管理','上传文件','上传文件','POST','/files/upload','[{\"type\":\"MultipartFile\",\"size\":122305,\"originalFilename\":\"pdf_img_p122_1.png\"},{\"accessLevel\":1,\"fileType\":\"document\"}]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.9',310,1,NULL,'2026-06-22 23:30:39','2026-06-22 23:30:39',NULL,NULL,0),(327470457051811840,'文件管理','上传文件','上传文件','POST','/files/upload','[{\"type\":\"MultipartFile\",\"size\":102238,\"originalFilename\":\"pdf_img_p124_0.png\"},{\"accessLevel\":1,\"fileType\":\"document\"}]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.9',792,1,NULL,'2026-06-22 23:30:41','2026-06-22 23:30:41',NULL,NULL,0),(327470461929787392,'文件管理','上传文件','上传文件','POST','/files/upload','[{\"type\":\"MultipartFile\",\"size\":137040,\"originalFilename\":\"pdf_img_p125_0.png\"},{\"accessLevel\":1,\"fileType\":\"document\"}]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.9',817,1,NULL,'2026-06-22 23:30:42','2026-06-22 23:30:42',NULL,NULL,0),(327470469638918144,'文件管理','上传文件','上传文件','POST','/files/upload','[{\"type\":\"MultipartFile\",\"size\":99149,\"originalFilename\":\"pdf_img_p126_1.png\"},{\"accessLevel\":1,\"fileType\":\"document\"}]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.9',541,1,NULL,'2026-06-22 23:30:44','2026-06-22 23:30:44',NULL,NULL,0),(327470474512699392,'文件管理','上传文件','上传文件','POST','/files/upload','[{\"type\":\"MultipartFile\",\"size\":69822,\"originalFilename\":\"pdf_img_p127_1.png\"},{\"accessLevel\":1,\"fileType\":\"document\"}]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.9',663,1,NULL,'2026-06-22 23:30:45','2026-06-22 23:30:45',NULL,NULL,0),(327470483194908672,'文件管理','上传文件','上传文件','POST','/files/upload','[{\"type\":\"MultipartFile\",\"size\":137186,\"originalFilename\":\"pdf_img_p129_0.png\"},{\"accessLevel\":1,\"fileType\":\"document\"}]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.9',1789,1,NULL,'2026-06-22 23:30:47','2026-06-22 23:30:47',NULL,NULL,0),(327470488332931072,'文件管理','上传文件','上传文件','POST','/files/upload','[{\"type\":\"MultipartFile\",\"size\":131332,\"originalFilename\":\"pdf_img_p204_1.png\"},{\"accessLevel\":1,\"fileType\":\"document\"}]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.9',1161,1,NULL,'2026-06-22 23:30:48','2026-06-22 23:30:48',NULL,NULL,0),(327470494238511104,'文件管理','上传文件','上传文件','POST','/files/upload','[{\"type\":\"MultipartFile\",\"size\":214805,\"originalFilename\":\"pdf_img_p205_0.png\"},{\"accessLevel\":1,\"fileType\":\"document\"}]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.9',1454,1,NULL,'2026-06-22 23:30:49','2026-06-22 23:30:49',NULL,NULL,0),(327470499779186688,'文件管理','上传文件','上传文件','POST','/files/upload','[{\"type\":\"MultipartFile\",\"size\":92066,\"originalFilename\":\"pdf_img_p206_0.png\"},{\"accessLevel\":1,\"fileType\":\"document\"}]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.9',1270,1,NULL,'2026-06-22 23:30:51','2026-06-22 23:30:51',NULL,NULL,0),(327470505198227456,'文件管理','上传文件','上传文件','POST','/files/upload','[{\"type\":\"MultipartFile\",\"size\":200378,\"originalFilename\":\"pdf_img_p206_1.png\"},{\"accessLevel\":1,\"fileType\":\"document\"}]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.9',1278,1,NULL,'2026-06-22 23:30:52','2026-06-22 23:30:52',NULL,NULL,0),(327470512500510720,'文件管理','上传文件','上传文件','POST','/files/upload','[{\"type\":\"MultipartFile\",\"size\":162045,\"originalFilename\":\"pdf_img_p207_0.png\"},{\"accessLevel\":1,\"fileType\":\"document\"}]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.9',1712,1,NULL,'2026-06-22 23:30:54','2026-06-22 23:30:54',NULL,NULL,0),(327470516703203328,'文件管理','上传文件','上传文件','POST','/files/upload','[{\"type\":\"MultipartFile\",\"size\":127778,\"originalFilename\":\"pdf_img_p138_0.png\"},{\"accessLevel\":1,\"fileType\":\"document\"}]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.9',269,1,NULL,'2026-06-22 23:30:55','2026-06-22 23:30:55',NULL,NULL,0),(327470520381607936,'文件管理','上传文件','上传文件','POST','/files/upload','[{\"type\":\"MultipartFile\",\"size\":131332,\"originalFilename\":\"pdf_img_p208_0.png\"},{\"accessLevel\":1,\"fileType\":\"document\"}]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.9',796,0,'Expected one result (or null) to be returned by selectOne(), but found: 2','2026-06-22 23:30:56','2026-06-22 23:30:56',NULL,NULL,0),(327470526069084160,'文件管理','上传文件','上传文件','POST','/files/upload','[{\"type\":\"MultipartFile\",\"size\":137985,\"originalFilename\":\"pdf_img_p140_0.png\"},{\"accessLevel\":1,\"fileType\":\"document\"}]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.9',975,1,NULL,'2026-06-22 23:30:57','2026-06-22 23:30:57',NULL,NULL,0),(327470533258121216,'文件管理','上传文件','上传文件','POST','/files/upload','[{\"type\":\"MultipartFile\",\"size\":87008,\"originalFilename\":\"pdf_img_p209_0.png\"},{\"accessLevel\":1,\"fileType\":\"document\"}]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.9',1847,1,NULL,'2026-06-22 23:30:59','2026-06-22 23:30:59',NULL,NULL,0),(327470539369222144,'文件管理','上传文件','上传文件','POST','/files/upload','[{\"type\":\"MultipartFile\",\"size\":162397,\"originalFilename\":\"pdf_img_p210_0.png\"},{\"accessLevel\":1,\"fileType\":\"document\"}]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.9',1087,1,NULL,'2026-06-22 23:31:00','2026-06-22 23:31:00',NULL,NULL,0),(327470542649167872,'文件管理','上传文件','上传文件','POST','/files/upload','[{\"type\":\"MultipartFile\",\"size\":124042,\"originalFilename\":\"pdf_img_p210_1.png\"},{\"accessLevel\":1,\"fileType\":\"document\"}]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.9',1157,1,NULL,'2026-06-22 23:31:01','2026-06-22 23:31:01',NULL,NULL,0),(327470547405508608,'文件管理','上传文件','上传文件','POST','/files/upload','[{\"type\":\"MultipartFile\",\"size\":138717,\"originalFilename\":\"pdf_img_p211_0.png\"},{\"accessLevel\":1,\"fileType\":\"document\"}]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.9',1111,1,NULL,'2026-06-22 23:31:02','2026-06-22 23:31:02',NULL,NULL,0),(327470553566941184,'文件管理','上传文件','上传文件','POST','/files/upload','[{\"type\":\"MultipartFile\",\"size\":134583,\"originalFilename\":\"pdf_img_p212_0.png\"},{\"accessLevel\":1,\"fileType\":\"document\"}]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.9',1440,1,NULL,'2026-06-22 23:31:04','2026-06-22 23:31:04',NULL,NULL,0),(327470558818209792,'文件管理','上传文件','上传文件','POST','/files/upload','[{\"type\":\"MultipartFile\",\"size\":111395,\"originalFilename\":\"pdf_img_p212_1.png\"},{\"accessLevel\":1,\"fileType\":\"document\"}]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.9',1234,1,NULL,'2026-06-22 23:31:05','2026-06-22 23:31:05',NULL,NULL,0),(327470566426677248,'文件管理','上传文件','上传文件','POST','/files/upload','[{\"type\":\"MultipartFile\",\"size\":152804,\"originalFilename\":\"pdf_img_p213_0.png\"},{\"accessLevel\":1,\"fileType\":\"document\"}]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.9',1680,1,NULL,'2026-06-22 23:31:07','2026-06-22 23:31:07',NULL,NULL,0),(327470571149463552,'文件管理','上传文件','上传文件','POST','/files/upload','[{\"type\":\"MultipartFile\",\"size\":236501,\"originalFilename\":\"pdf_img_p213_1.png\"},{\"accessLevel\":1,\"fileType\":\"document\"}]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.9',1151,1,NULL,'2026-06-22 23:31:08','2026-06-22 23:31:08',NULL,NULL,0),(327470577268953088,'文件管理','上传文件','上传文件','POST','/files/upload','[{\"type\":\"MultipartFile\",\"size\":326654,\"originalFilename\":\"pdf_img_p214_0.png\"},{\"accessLevel\":1,\"fileType\":\"document\"}]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.9',1411,1,NULL,'2026-06-22 23:31:09','2026-06-22 23:31:09',NULL,NULL,0),(327470584575430656,'文件管理','上传文件','上传文件','POST','/files/upload','[{\"type\":\"MultipartFile\",\"size\":333635,\"originalFilename\":\"pdf_img_p157_0.png\"},{\"accessLevel\":1,\"fileType\":\"document\"}]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.9',219,1,NULL,'2026-06-22 23:31:11','2026-06-22 23:31:11',NULL,NULL,0),(327470589478572032,'文件管理','上传文件','上传文件','POST','/files/upload','[{\"type\":\"MultipartFile\",\"size\":194112,\"originalFilename\":\"pdf_img_p215_0.png\"},{\"accessLevel\":1,\"fileType\":\"document\"}]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.9',1058,1,NULL,'2026-06-22 23:31:12','2026-06-22 23:31:12',NULL,NULL,0),(327470597066067968,'文件管理','上传文件','上传文件','POST','/files/upload','[{\"type\":\"MultipartFile\",\"size\":159842,\"originalFilename\":\"pdf_img_p215_1.png\"},{\"accessLevel\":1,\"fileType\":\"document\"}]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.9',1837,1,NULL,'2026-06-22 23:31:14','2026-06-22 23:31:14',NULL,NULL,0),(327470601772077056,'文件管理','上传文件','上传文件','POST','/files/upload','[{\"type\":\"MultipartFile\",\"size\":183499,\"originalFilename\":\"pdf_img_p160_1.png\"},{\"accessLevel\":1,\"fileType\":\"document\"}]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.9',248,1,NULL,'2026-06-22 23:31:15','2026-06-22 23:31:15',NULL,NULL,0),(327470606805241856,'文件管理','上传文件','上传文件','POST','/files/upload','[{\"type\":\"MultipartFile\",\"size\":122724,\"originalFilename\":\"pdf_img_p161_1.png\"},{\"accessLevel\":1,\"fileType\":\"document\"}]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.9',260,1,NULL,'2026-06-22 23:31:16','2026-06-22 23:31:16',NULL,NULL,0),(327470613239304192,'文件管理','上传文件','上传文件','POST','/files/upload','[{\"type\":\"MultipartFile\",\"size\":391673,\"originalFilename\":\"pdf_img_p163_0.png\"},{\"accessLevel\":1,\"fileType\":\"document\"}]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.9',621,1,NULL,'2026-06-22 23:31:18','2026-06-22 23:31:18',NULL,NULL,0),(327470617886593024,'文件管理','上传文件','上传文件','POST','/files/upload','[{\"type\":\"MultipartFile\",\"size\":82772,\"originalFilename\":\"pdf_img_p164_1.png\"},{\"accessLevel\":1,\"fileType\":\"document\"}]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.9',704,1,NULL,'2026-06-22 23:31:19','2026-06-22 23:31:19',NULL,NULL,0),(327470621674049536,'文件管理','上传文件','上传文件','POST','/files/upload','[{\"type\":\"MultipartFile\",\"size\":142191,\"originalFilename\":\"pdf_img_p165_1.png\"},{\"accessLevel\":1,\"fileType\":\"document\"}]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.9',243,1,NULL,'2026-06-22 23:31:20','2026-06-22 23:31:20',NULL,NULL,0),(327470627420246016,'文件管理','上传文件','上传文件','POST','/files/upload','[{\"type\":\"MultipartFile\",\"size\":148017,\"originalFilename\":\"pdf_img_p167_0.png\"},{\"accessLevel\":1,\"fileType\":\"document\"}]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.9',1025,1,NULL,'2026-06-22 23:31:21','2026-06-22 23:31:21',NULL,NULL,0),(327470630846992384,'文件管理','上传文件','上传文件','POST','/files/upload','[{\"type\":\"MultipartFile\",\"size\":113466,\"originalFilename\":\"pdf_img_p168_0.png\"},{\"accessLevel\":1,\"fileType\":\"document\"}]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.9',263,1,NULL,'2026-06-22 23:31:22','2026-06-22 23:31:22',NULL,NULL,0),(327470639428538368,'文件管理','上传文件','上传文件','POST','/files/upload','[{\"type\":\"MultipartFile\",\"size\":168466,\"originalFilename\":\"pdf_img_p169_0.png\"},{\"accessLevel\":1,\"fileType\":\"document\"}]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.9',836,1,NULL,'2026-06-22 23:31:24','2026-06-22 23:31:24',NULL,NULL,0),(327470644038078464,'文件管理','上传文件','上传文件','POST','/files/upload','[{\"type\":\"MultipartFile\",\"size\":127300,\"originalFilename\":\"pdf_img_p170_1.png\"},{\"accessLevel\":1,\"fileType\":\"document\"}]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.9',261,1,NULL,'2026-06-22 23:31:25','2026-06-22 23:31:25',NULL,NULL,0),(327470648295297024,'文件管理','上传文件','上传文件','POST','/files/upload','[{\"type\":\"MultipartFile\",\"size\":141175,\"originalFilename\":\"pdf_img_p171_1.png\"},{\"accessLevel\":1,\"fileType\":\"document\"}]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.9',410,1,NULL,'2026-06-22 23:31:26','2026-06-22 23:31:26',NULL,NULL,0),(327470654825828352,'文件管理','上传文件','上传文件','POST','/files/upload','[{\"type\":\"MultipartFile\",\"size\":154128,\"originalFilename\":\"pdf_img_p223_0.png\"},{\"accessLevel\":1,\"fileType\":\"document\"}]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.9',521,1,NULL,'2026-06-22 23:31:28','2026-06-22 23:31:28',NULL,NULL,0),(327470657392742400,'文件管理','上传文件','上传文件','POST','/files/upload','[{\"type\":\"MultipartFile\",\"size\":165331,\"originalFilename\":\"pdf_img_p174_0.png\"},{\"accessLevel\":1,\"fileType\":\"document\"}]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.9',260,1,NULL,'2026-06-22 23:31:28','2026-06-22 23:31:28',NULL,NULL,0),(327470665051541504,'文件管理','上传文件','上传文件','POST','/files/upload','[{\"type\":\"MultipartFile\",\"size\":318824,\"originalFilename\":\"pdf_img_p176_0.png\"},{\"accessLevel\":1,\"fileType\":\"document\"}]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.9',503,1,NULL,'2026-06-22 23:31:30','2026-06-22 23:31:30',NULL,NULL,0),(327470669933711360,'文件管理','上传文件','上传文件','POST','/files/upload','[{\"type\":\"MultipartFile\",\"size\":35513,\"originalFilename\":\"pdf_img_p177_0.png\"},{\"accessLevel\":1,\"fileType\":\"document\"}]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.9',312,1,NULL,'2026-06-22 23:31:31','2026-06-22 23:31:31',NULL,NULL,0),(327470677227606016,'文件管理','上传文件','上传文件','POST','/files/upload','[{\"type\":\"MultipartFile\",\"size\":188383,\"originalFilename\":\"pdf_img_p178_0.png\"},{\"accessLevel\":1,\"fileType\":\"document\"}]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.9',1032,1,NULL,'2026-06-22 23:31:33','2026-06-22 23:31:33',NULL,NULL,0),(327470682143330304,'文件管理','上传文件','上传文件','POST','/files/upload','[{\"type\":\"MultipartFile\",\"size\":269250,\"originalFilename\":\"pdf_img_p179_0.png\"},{\"accessLevel\":1,\"fileType\":\"document\"}]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.9',833,1,NULL,'2026-06-22 23:31:34','2026-06-22 23:31:34',NULL,NULL,0),(327470690590658560,'文件管理','上传文件','上传文件','POST','/files/upload','[{\"type\":\"MultipartFile\",\"size\":159609,\"originalFilename\":\"pdf_img_p180_1.png\"},{\"accessLevel\":1,\"fileType\":\"document\"}]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.9',1089,1,NULL,'2026-06-22 23:31:36','2026-06-22 23:31:36',NULL,NULL,0),(327470697876164608,'文件管理','上传文件','上传文件','POST','/files/upload','[{\"type\":\"MultipartFile\",\"size\":91043,\"originalFilename\":\"pdf_img_p181_1.png\"},{\"accessLevel\":1,\"fileType\":\"document\"}]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.9',608,1,NULL,'2026-06-22 23:31:38','2026-06-22 23:31:38',NULL,NULL,0),(327470703135821824,'文件管理','上传文件','上传文件','POST','/files/upload','[{\"type\":\"MultipartFile\",\"size\":152491,\"originalFilename\":\"pdf_img_p182_1.png\"},{\"accessLevel\":1,\"fileType\":\"document\"}]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.9',911,1,NULL,'2026-06-22 23:31:39','2026-06-22 23:31:39',NULL,NULL,0),(327470708089294848,'文件管理','上传文件','上传文件','POST','/files/upload','[{\"type\":\"MultipartFile\",\"size\":119722,\"originalFilename\":\"pdf_img_p184_0.png\"},{\"accessLevel\":1,\"fileType\":\"document\"}]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.9',241,1,NULL,'2026-06-22 23:31:40','2026-06-22 23:31:40',NULL,NULL,0),(327470711759310848,'文件管理','上传文件','上传文件','POST','/files/upload','[{\"type\":\"MultipartFile\",\"size\":109751,\"originalFilename\":\"pdf_img_p185_0.png\"},{\"accessLevel\":1,\"fileType\":\"document\"}]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.9',270,1,NULL,'2026-06-22 23:31:41','2026-06-22 23:31:41',NULL,NULL,0),(327470719917232128,'文件管理','上传文件','上传文件','POST','/files/upload','[{\"type\":\"MultipartFile\",\"size\":109674,\"originalFilename\":\"pdf_img_p186_0.png\"},{\"accessLevel\":1,\"fileType\":\"document\"}]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.9',1020,1,NULL,'2026-06-22 23:31:43','2026-06-22 23:31:43',NULL,NULL,0),(327470725592125440,'文件管理','上传文件','上传文件','POST','/files/upload','[{\"type\":\"MultipartFile\",\"size\":156401,\"originalFilename\":\"pdf_img_p187_1.png\"},{\"accessLevel\":1,\"fileType\":\"document\"}]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.9',333,1,NULL,'2026-06-22 23:31:45','2026-06-22 23:31:45',NULL,NULL,0),(327470733544525824,'文件管理','上传文件','上传文件','POST','/files/upload','[{\"type\":\"MultipartFile\",\"size\":125130,\"originalFilename\":\"pdf_img_p188_1.png\"},{\"accessLevel\":1,\"fileType\":\"document\"}]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.9',1013,1,NULL,'2026-06-22 23:31:46','2026-06-22 23:31:46',NULL,NULL,0),(327470743774433280,'文件管理','上传文件','上传文件','POST','/files/upload','[{\"type\":\"MultipartFile\",\"size\":98620,\"originalFilename\":\"pdf_img_p231_0.png\"},{\"accessLevel\":1,\"fileType\":\"document\"}]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.9',2541,1,NULL,'2026-06-22 23:31:49','2026-06-22 23:31:49',NULL,NULL,0),(327470747893239808,'文件管理','上传文件','上传文件','POST','/files/upload','[{\"type\":\"MultipartFile\",\"size\":104053,\"originalFilename\":\"pdf_img_p232_0.png\"},{\"accessLevel\":1,\"fileType\":\"document\"}]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.9',988,1,NULL,'2026-06-22 23:31:50','2026-06-22 23:31:50',NULL,NULL,0),(327470752339202048,'文件管理','上传文件','上传文件','POST','/files/upload','[{\"type\":\"MultipartFile\",\"size\":76570,\"originalFilename\":\"pdf_img_p232_1.png\"},{\"accessLevel\":1,\"fileType\":\"document\"}]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.9',1019,1,NULL,'2026-06-22 23:31:51','2026-06-22 23:31:51',NULL,NULL,0),(327470756667723776,'文件管理','上传文件','上传文件','POST','/files/upload','[{\"type\":\"MultipartFile\",\"size\":153905,\"originalFilename\":\"pdf_img_p233_0.png\"},{\"accessLevel\":1,\"fileType\":\"document\"}]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.9',968,1,NULL,'2026-06-22 23:31:52','2026-06-22 23:31:52',NULL,NULL,0),(327470795876077568,'文件管理','上传文件','上传文件','POST','/files/upload','[{\"type\":\"MultipartFile\",\"size\":267860,\"originalFilename\":\"pdf_img_p195_1.png\"},{\"accessLevel\":1,\"fileType\":\"document\"}]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.9',1283,0,'Expected one result (or null) to be returned by selectOne(), but found: 2','2026-06-22 23:32:01','2026-06-22 23:32:01',NULL,NULL,0),(327470804315017216,'文件管理','上传文件','上传文件','POST','/files/upload','[{\"type\":\"MultipartFile\",\"size\":115185,\"originalFilename\":\"pdf_img_p234_1.png\"},{\"accessLevel\":1,\"fileType\":\"document\"}]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.9',1800,1,NULL,'2026-06-22 23:32:03','2026-06-22 23:32:03',NULL,NULL,0),(327470851719041024,'文件管理','上传文件','上传文件','POST','/files/upload','[{\"type\":\"MultipartFile\",\"size\":150918,\"originalFilename\":\"pdf_img_p235_0.png\"},{\"accessLevel\":1,\"fileType\":\"document\"}]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.9',11238,0,'JDBC commit; Communications link failure\n\nThe last packet successfully received from the server was 10,021 milliseconds ago. The last packet sent successfully to the server was 10,021 milliseconds ago.','2026-06-22 23:32:15','2026-06-22 23:32:15',NULL,NULL,0),(327470881204998144,'文件管理','上传文件','上传文件','POST','/files/upload','[{\"type\":\"MultipartFile\",\"size\":204927,\"originalFilename\":\"pdf_img_p198_1.png\"},{\"accessLevel\":1,\"fileType\":\"document\"}]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.9',1066,0,'Expected one result (or null) to be returned by selectOne(), but found: 2','2026-06-22 23:32:22','2026-06-22 23:32:22',NULL,NULL,0),(327470886867308544,'文件管理','上传文件','上传文件','POST','/files/upload','[{\"type\":\"MultipartFile\",\"size\":120638,\"originalFilename\":\"pdf_img_p200_0.png\"},{\"accessLevel\":1,\"fileType\":\"document\"}]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.9',601,0,'Expected one result (or null) to be returned by selectOne(), but found: 2','2026-06-22 23:32:23','2026-06-22 23:32:23',NULL,NULL,0),(327470891212607488,'文件管理','上传文件','上传文件','POST','/files/upload','[{\"type\":\"MultipartFile\",\"size\":158561,\"originalFilename\":\"pdf_img_p201_0.png\"},{\"accessLevel\":1,\"fileType\":\"document\"}]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.9',661,0,'Expected one result (or null) to be returned by selectOne(), but found: 2','2026-06-22 23:32:24','2026-06-22 23:32:24',NULL,NULL,0),(327470896220606464,'文件管理','上传文件','上传文件','POST','/files/upload','[{\"type\":\"MultipartFile\",\"size\":172989,\"originalFilename\":\"pdf_img_p202_0.png\"},{\"accessLevel\":1,\"fileType\":\"document\"}]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.9',568,0,'Expected one result (or null) to be returned by selectOne(), but found: 2','2026-06-22 23:32:25','2026-06-22 23:32:25',NULL,NULL,0),(327470900498796544,'文件管理','上传文件','上传文件','POST','/files/upload','[{\"type\":\"MultipartFile\",\"size\":179861,\"originalFilename\":\"pdf_img_p238_1.png\"},{\"accessLevel\":1,\"fileType\":\"document\"}]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.9',1656,1,NULL,'2026-06-22 23:32:26','2026-06-22 23:32:26',NULL,NULL,0),(327470905309663232,'文件管理','上传文件','上传文件','POST','/files/upload','[{\"type\":\"MultipartFile\",\"size\":112568,\"originalFilename\":\"pdf_img_p239_0.png\"},{\"accessLevel\":1,\"fileType\":\"document\"}]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.9',1106,1,NULL,'2026-06-22 23:32:27','2026-06-22 23:32:27',NULL,NULL,0),(327470910414131200,'文件管理','上传文件','上传文件','POST','/files/upload','[{\"type\":\"MultipartFile\",\"size\":200378,\"originalFilename\":\"pdf_img_p206_1.png\"},{\"accessLevel\":1,\"fileType\":\"document\"}]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.9',844,0,'Expected one result (or null) to be returned by selectOne(), but found: 2','2026-06-22 23:32:29','2026-06-22 23:32:29',NULL,NULL,0),(327470914549714944,'文件管理','上传文件','上传文件','POST','/files/upload','[{\"type\":\"MultipartFile\",\"size\":172612,\"originalFilename\":\"pdf_img_p207_1.png\"},{\"accessLevel\":1,\"fileType\":\"document\"}]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.9',339,0,'Expected one result (or null) to be returned by selectOne(), but found: 2','2026-06-22 23:32:30','2026-06-22 23:32:30',NULL,NULL,0),(327470921692614656,'文件管理','上传文件','上传文件','POST','/files/upload','[{\"type\":\"MultipartFile\",\"size\":116541,\"originalFilename\":\"pdf_img_p241_0.png\"},{\"accessLevel\":1,\"fileType\":\"document\"}]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.9',601,1,NULL,'2026-06-22 23:32:31','2026-06-22 23:32:31',NULL,NULL,0),(327470923231924224,'文件管理','上传文件','上传文件','POST','/files/upload','[{\"type\":\"MultipartFile\",\"size\":87008,\"originalFilename\":\"pdf_img_p209_0.png\"},{\"accessLevel\":1,\"fileType\":\"document\"}]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.9',287,0,'Expected one result (or null) to be returned by selectOne(), but found: 2','2026-06-22 23:32:32','2026-06-22 23:32:32',NULL,NULL,0),(327470929389162496,'文件管理','上传文件','上传文件','POST','/files/upload','[{\"type\":\"MultipartFile\",\"size\":328796,\"originalFilename\":\"pdf_img_p242_1.png\"},{\"accessLevel\":1,\"fileType\":\"document\"}]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.9',1632,1,NULL,'2026-06-22 23:32:33','2026-06-22 23:32:33',NULL,NULL,0),(327470935282159616,'文件管理','上传文件','上传文件','POST','/files/upload','[{\"type\":\"MultipartFile\",\"size\":159534,\"originalFilename\":\"pdf_img_p243_0.png\"},{\"accessLevel\":1,\"fileType\":\"document\"}]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.9',1376,1,NULL,'2026-06-22 23:32:35','2026-06-22 23:32:35',NULL,NULL,0),(327470939904282624,'文件管理','上传文件','上传文件','POST','/files/upload','[{\"type\":\"MultipartFile\",\"size\":142880,\"originalFilename\":\"pdf_img_p101_1.png\"},{\"accessLevel\":1,\"fileType\":\"document\"}]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.9',1035,1,NULL,'2026-06-22 23:32:36','2026-06-22 23:32:36',NULL,NULL,0),(327470944031477760,'文件管理','上传文件','上传文件','POST','/files/upload','[{\"type\":\"MultipartFile\",\"size\":459507,\"originalFilename\":\"pdf_img_p244_0.png\"},{\"accessLevel\":1,\"fileType\":\"document\"}]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.9',1908,1,NULL,'2026-06-22 23:32:37','2026-06-22 23:32:37',NULL,NULL,0),(327470947512750080,'文件管理','上传文件','上传文件','POST','/files/upload','[{\"type\":\"MultipartFile\",\"size\":60355,\"originalFilename\":\"pdf_img_p103_1.png\"},{\"accessLevel\":1,\"fileType\":\"document\"}]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.9',296,1,NULL,'2026-06-22 23:32:38','2026-06-22 23:32:38',NULL,NULL,0),(327470950826250240,'文件管理','上传文件','上传文件','POST','/files/upload','[{\"type\":\"MultipartFile\",\"size\":132436,\"originalFilename\":\"pdf_img_p214_1.png\"},{\"accessLevel\":1,\"fileType\":\"document\"}]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.9',679,0,'Expected one result (or null) to be returned by selectOne(), but found: 2','2026-06-22 23:32:38','2026-06-22 23:32:38',NULL,NULL,0),(327470955012165632,'文件管理','上传文件','上传文件','POST','/files/upload','[{\"type\":\"MultipartFile\",\"size\":71555,\"originalFilename\":\"pdf_img_p245_0.png\"},{\"accessLevel\":1,\"fileType\":\"document\"}]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.9',1352,1,NULL,'2026-06-22 23:32:39','2026-06-22 23:32:39',NULL,NULL,0),(327470956643749888,'文件管理','上传文件','上传文件','POST','/files/upload','[{\"type\":\"MultipartFile\",\"size\":182605,\"originalFilename\":\"pdf_img_p216_0.png\"},{\"accessLevel\":1,\"fileType\":\"document\"}]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.9',261,0,'Expected one result (or null) to be returned by selectOne(), but found: 2','2026-06-22 23:32:40','2026-06-22 23:32:40',NULL,NULL,0),(327470959936278528,'文件管理','上传文件','上传文件','POST','/files/upload','[{\"type\":\"MultipartFile\",\"size\":155501,\"originalFilename\":\"pdf_img_p246_0.png\"},{\"accessLevel\":1,\"fileType\":\"document\"}]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.9',1068,1,NULL,'2026-06-22 23:32:40','2026-06-22 23:32:40',NULL,NULL,0),(327470964130582528,'文件管理','上传文件','上传文件','POST','/files/upload','[{\"type\":\"MultipartFile\",\"size\":74528,\"originalFilename\":\"pdf_img_p246_1.png\"},{\"accessLevel\":1,\"fileType\":\"document\"}]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.9',1205,1,NULL,'2026-06-22 23:32:41','2026-06-22 23:32:41',NULL,NULL,0),(327470969826447360,'文件管理','上传文件','上传文件','POST','/files/upload','[{\"type\":\"MultipartFile\",\"size\":207081,\"originalFilename\":\"pdf_img_p219_0.png\"},{\"accessLevel\":1,\"fileType\":\"document\"}]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.9',1213,0,'Expected one result (or null) to be returned by selectOne(), but found: 2','2026-06-22 23:32:43','2026-06-22 23:32:43',NULL,NULL,0),(327470972733100032,'文件管理','上传文件','上传文件','POST','/files/upload','[{\"type\":\"MultipartFile\",\"size\":197703,\"originalFilename\":\"pdf_img_p247_0.png\"},{\"accessLevel\":1,\"fileType\":\"document\"}]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.9',1738,1,NULL,'2026-06-22 23:32:44','2026-06-22 23:32:44',NULL,NULL,0),(327470977141313536,'文件管理','上传文件','上传文件','POST','/files/upload','[{\"type\":\"MultipartFile\",\"size\":242613,\"originalFilename\":\"pdf_img_p248_0.png\"},{\"accessLevel\":1,\"fileType\":\"document\"}]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.9',1189,1,NULL,'2026-06-22 23:32:45','2026-06-22 23:32:45',NULL,NULL,0),(327470981025239040,'文件管理','上传文件','上传文件','POST','/files/upload','[{\"type\":\"MultipartFile\",\"size\":115469,\"originalFilename\":\"pdf_img_p221_1.png\"},{\"accessLevel\":1,\"fileType\":\"document\"}]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.9',759,0,'Expected one result (or null) to be returned by selectOne(), but found: 2','2026-06-22 23:32:45','2026-06-22 23:32:45',NULL,NULL,0),(327470986096152576,'文件管理','上传文件','上传文件','POST','/files/upload','[{\"type\":\"MultipartFile\",\"size\":242439,\"originalFilename\":\"pdf_img_p112_1.png\"},{\"accessLevel\":1,\"fileType\":\"document\"}]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.9',1106,1,NULL,'2026-06-22 23:32:47','2026-06-22 23:32:47',NULL,NULL,0),(327470990265290752,'文件管理','上传文件','上传文件','POST','/files/upload','[{\"type\":\"MultipartFile\",\"size\":265004,\"originalFilename\":\"pdf_img_p223_1.png\"},{\"accessLevel\":1,\"fileType\":\"document\"}]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.9',733,0,'Expected one result (or null) to be returned by selectOne(), but found: 2','2026-06-22 23:32:48','2026-06-22 23:32:48',NULL,NULL,0),(327470994660921344,'文件管理','上传文件','上传文件','POST','/files/upload','[{\"type\":\"MultipartFile\",\"size\":255999,\"originalFilename\":\"pdf_img_p224_0.png\"},{\"accessLevel\":1,\"fileType\":\"document\"}]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.9',958,0,'Expected one result (or null) to be returned by selectOne(), but found: 2','2026-06-22 23:32:49','2026-06-22 23:32:49',NULL,NULL,0),(327470997856980992,'文件管理','上传文件','上传文件','POST','/files/upload','[{\"type\":\"MultipartFile\",\"size\":204220,\"originalFilename\":\"pdf_img_p249_1.png\"},{\"accessLevel\":1,\"fileType\":\"document\"}]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.9',1678,1,NULL,'2026-06-22 23:32:50','2026-06-22 23:32:50',NULL,NULL,0),(327471001313087488,'文件管理','上传文件','上传文件','POST','/files/upload','[{\"type\":\"MultipartFile\",\"size\":155536,\"originalFilename\":\"pdf_img_p117_0.png\"},{\"accessLevel\":1,\"fileType\":\"document\"}]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.9',459,1,NULL,'2026-06-22 23:32:50','2026-06-22 23:32:50',NULL,NULL,0),(327471002797871104,'文件管理','上传文件','上传文件','POST','/files/upload','[{\"type\":\"MultipartFile\",\"size\":91939,\"originalFilename\":\"pdf_img_p117_1.png\"},{\"accessLevel\":1,\"fileType\":\"document\"}]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.9',255,1,NULL,'2026-06-22 23:32:51','2026-06-22 23:32:51',NULL,NULL,0),(327471009361956864,'文件管理','上传文件','上传文件','POST','/files/upload','[{\"type\":\"MultipartFile\",\"size\":191225,\"originalFilename\":\"pdf_img_p251_0.png\"},{\"accessLevel\":1,\"fileType\":\"document\"}]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.9',1657,1,NULL,'2026-06-22 23:32:52','2026-06-22 23:32:52',NULL,NULL,0),(327471011203256320,'文件管理','上传文件','上传文件','POST','/files/upload','[{\"type\":\"MultipartFile\",\"size\":181822,\"originalFilename\":\"pdf_img_p228_1.png\"},{\"accessLevel\":1,\"fileType\":\"document\"}]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.9',253,0,'Expected one result (or null) to be returned by selectOne(), but found: 2','2026-06-22 23:32:53','2026-06-22 23:32:53',NULL,NULL,0),(327471014600642560,'文件管理','上传文件','上传文件','POST','/files/upload','[{\"type\":\"MultipartFile\",\"size\":278674,\"originalFilename\":\"pdf_img_p251_1.png\"},{\"accessLevel\":1,\"fileType\":\"document\"}]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.9',1069,1,NULL,'2026-06-22 23:32:53','2026-06-22 23:32:53',NULL,NULL,0),(327471021072453632,'文件管理','上传文件','上传文件','POST','/files/upload','[{\"type\":\"MultipartFile\",\"size\":125130,\"originalFilename\":\"pdf_img_p188_1.png\"},{\"accessLevel\":1,\"fileType\":\"document\"}]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.9',843,1,NULL,'2026-06-22 23:32:55','2026-06-22 23:32:55',NULL,NULL,0),(327471021122785280,'文件管理','上传文件','上传文件','POST','/files/upload','[{\"type\":\"MultipartFile\",\"size\":142880,\"originalFilename\":\"pdf_img_p101_1.png\"},{\"accessLevel\":1,\"fileType\":\"document\"}]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.9',865,1,NULL,'2026-06-22 23:32:55','2026-06-22 23:32:55',NULL,NULL,0),(327471021475106816,'文件管理','上传文件','上传文件','POST','/files/upload','[{\"type\":\"MultipartFile\",\"size\":155670,\"originalFilename\":\"pdf_img_p108_0.png\"},{\"accessLevel\":1,\"fileType\":\"document\"}]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.9',287,1,NULL,'2026-06-22 23:32:55','2026-06-22 23:32:55',NULL,NULL,0),(327471024457256960,'文件管理','上传文件','上传文件','POST','/files/upload','[{\"type\":\"MultipartFile\",\"size\":125471,\"originalFilename\":\"pdf_img_p230_1.png\"},{\"accessLevel\":1,\"fileType\":\"document\"}]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.9',1910,0,'Expected one result (or null) to be returned by selectOne(), but found: 2','2026-06-22 23:32:56','2026-06-22 23:32:56',NULL,NULL,0),(327471027707842560,'文件管理','上传文件','上传文件','POST','/files/upload','[{\"type\":\"MultipartFile\",\"size\":122305,\"originalFilename\":\"pdf_img_p122_1.png\"},{\"accessLevel\":1,\"fileType\":\"document\"}]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.9',242,1,NULL,'2026-06-22 23:32:57','2026-06-22 23:32:57',NULL,NULL,0),(327471031327526912,'文件管理','上传文件','上传文件','POST','/files/upload','[{\"type\":\"MultipartFile\",\"size\":272583,\"originalFilename\":\"pdf_img_p252_1.png\"},{\"accessLevel\":1,\"fileType\":\"document\"}]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.9',1469,1,NULL,'2026-06-22 23:32:57','2026-06-22 23:32:57',NULL,NULL,0),(327471035127566336,'文件管理','上传文件','上传文件','POST','/files/upload','[{\"type\":\"MultipartFile\",\"size\":131885,\"originalFilename\":\"pdf_img_p124_1.png\"},{\"accessLevel\":1,\"fileType\":\"document\"}]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.9',576,1,NULL,'2026-06-22 23:32:58','2026-06-22 23:32:58',NULL,NULL,0),(327471036113227776,'文件管理','上传文件','上传文件','POST','/files/upload','[{\"type\":\"MultipartFile\",\"size\":137040,\"originalFilename\":\"pdf_img_p125_0.png\"},{\"accessLevel\":1,\"fileType\":\"document\"}]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.9',236,1,NULL,'2026-06-22 23:32:59','2026-06-22 23:32:59',NULL,NULL,0),(327471043197407232,'文件管理','上传文件','上传文件','POST','/files/upload','[{\"type\":\"MultipartFile\",\"size\":174949,\"originalFilename\":\"pdf_img_p254_0.png\"},{\"accessLevel\":1,\"fileType\":\"document\"}]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.9',1658,1,NULL,'2026-06-22 23:33:00','2026-06-22 23:33:00',NULL,NULL,0),(327471047257493504,'文件管理','上传文件','上传文件','POST','/files/upload','[{\"type\":\"MultipartFile\",\"size\":112388,\"originalFilename\":\"pdf_img_p127_0.png\"},{\"accessLevel\":1,\"fileType\":\"document\"}]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.9',787,1,NULL,'2026-06-22 23:33:01','2026-06-22 23:33:01',NULL,NULL,0),(327471048545144832,'文件管理','上传文件','上传文件','POST','/files/upload','[{\"type\":\"MultipartFile\",\"size\":121192,\"originalFilename\":\"pdf_img_p254_1.png\"},{\"accessLevel\":1,\"fileType\":\"document\"}]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.9',1215,1,NULL,'2026-06-22 23:33:02','2026-06-22 23:33:02',NULL,NULL,0),(327471052835917824,'文件管理','上传文件','上传文件','POST','/files/upload','[{\"type\":\"MultipartFile\",\"size\":137186,\"originalFilename\":\"pdf_img_p129_0.png\"},{\"accessLevel\":1,\"fileType\":\"document\"}]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.9',286,1,NULL,'2026-06-22 23:33:03','2026-06-22 23:33:03',NULL,NULL,0),(327471054052265984,'文件管理','上传文件','上传文件','POST','/files/upload','[{\"type\":\"MultipartFile\",\"size\":128258,\"originalFilename\":\"pdf_img_p129_1.png\"},{\"accessLevel\":1,\"fileType\":\"document\"}]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.9',233,1,NULL,'2026-06-22 23:33:03','2026-06-22 23:33:03',NULL,NULL,0),(327471061824311296,'文件管理','上传文件','上传文件','POST','/files/upload','[{\"type\":\"MultipartFile\",\"size\":87767,\"originalFilename\":\"pdf_img_p256_0.png\"},{\"accessLevel\":1,\"fileType\":\"document\"}]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.9',1851,1,NULL,'2026-06-22 23:33:05','2026-06-22 23:33:05',NULL,NULL,0),(327471066253496320,'文件管理','上传文件','上传文件','POST','/files/upload','[{\"type\":\"MultipartFile\",\"size\":235746,\"originalFilename\":\"pdf_img_p240_1.png\"},{\"accessLevel\":1,\"fileType\":\"document\"}]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.9',975,0,'Expected one result (or null) to be returned by selectOne(), but found: 2','2026-06-22 23:33:06','2026-06-22 23:33:06',NULL,NULL,0),(327471069902540800,'文件管理','上传文件','上传文件','POST','/files/upload','[{\"type\":\"MultipartFile\",\"size\":149529,\"originalFilename\":\"pdf_img_p256_1.png\"},{\"accessLevel\":1,\"fileType\":\"document\"}]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.9',1672,1,NULL,'2026-06-22 23:33:07','2026-06-22 23:33:07',NULL,NULL,0),(327471078429560832,'文件管理','上传文件','上传文件','POST','/files/upload','[{\"type\":\"MultipartFile\",\"size\":328796,\"originalFilename\":\"pdf_img_p242_1.png\"},{\"accessLevel\":1,\"fileType\":\"document\"}]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.9',1070,0,'Expected one result (or null) to be returned by selectOne(), but found: 2','2026-06-22 23:33:09','2026-06-22 23:33:09',NULL,NULL,0),(327471081193607168,'文件管理','上传文件','上传文件','POST','/files/upload','[{\"type\":\"MultipartFile\",\"size\":159534,\"originalFilename\":\"pdf_img_p243_0.png\"},{\"accessLevel\":1,\"fileType\":\"document\"}]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.9',603,0,'Expected one result (or null) to be returned by selectOne(), but found: 2','2026-06-22 23:33:09','2026-06-22 23:33:09',NULL,NULL,0),(327471085635375104,'文件管理','上传文件','上传文件','POST','/files/upload','[{\"type\":\"MultipartFile\",\"size\":116015,\"originalFilename\":\"pdf_img_p258_0.png\"},{\"accessLevel\":1,\"fileType\":\"document\"}]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.9',1500,1,NULL,'2026-06-22 23:33:10','2026-06-22 23:33:10',NULL,NULL,0),(327471090601431040,'文件管理','上传文件','上传文件','POST','/files/upload','[{\"type\":\"MultipartFile\",\"size\":96790,\"originalFilename\":\"pdf_img_p258_1.png\"},{\"accessLevel\":1,\"fileType\":\"document\"}]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.9',1081,1,NULL,'2026-06-22 23:33:12','2026-06-22 23:33:12',NULL,NULL,0),(327471093675855872,'文件管理','上传文件','上传文件','POST','/files/upload','[{\"type\":\"MultipartFile\",\"size\":127778,\"originalFilename\":\"pdf_img_p138_0.png\"},{\"accessLevel\":1,\"fileType\":\"document\"}]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.9',648,1,NULL,'2026-06-22 23:33:12','2026-06-22 23:33:12',NULL,NULL,0),(327471095475212288,'文件管理','上传文件','上传文件','POST','/files/upload','[{\"type\":\"MultipartFile\",\"size\":74528,\"originalFilename\":\"pdf_img_p246_1.png\"},{\"accessLevel\":1,\"fileType\":\"document\"}]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.9',355,0,'Expected one result (or null) to be returned by selectOne(), but found: 2','2026-06-22 23:33:13','2026-06-22 23:33:13',NULL,NULL,0),(327471100021837824,'文件管理','上传文件','上传文件','POST','/files/upload','[{\"type\":\"MultipartFile\",\"size\":83095,\"originalFilename\":\"pdf_img_p259_1.png\"},{\"accessLevel\":1,\"fileType\":\"document\"}]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.9',1086,1,NULL,'2026-06-22 23:33:14','2026-06-22 23:33:14',NULL,NULL,0),(327471104790761472,'文件管理','上传文件','上传文件','POST','/files/upload','[{\"type\":\"MultipartFile\",\"size\":130810,\"originalFilename\":\"pdf_img_p140_1.png\"},{\"accessLevel\":1,\"fileType\":\"document\"}]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.9',1020,1,NULL,'2026-06-22 23:33:15','2026-06-22 23:33:15',NULL,NULL,0),(327471107525447680,'文件管理','上传文件','上传文件','POST','/files/upload','[{\"type\":\"MultipartFile\",\"size\":218176,\"originalFilename\":\"pdf_img_p249_0.png\"},{\"accessLevel\":1,\"fileType\":\"document\"}]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.9',286,0,'Expected one result (or null) to be returned by selectOne(), but found: 2','2026-06-22 23:33:16','2026-06-22 23:33:16',NULL,NULL,0),(327471114559295488,'文件管理','上传文件','上传文件','POST','/files/upload','[{\"type\":\"MultipartFile\",\"size\":120675,\"originalFilename\":\"pdf_img_p261_0.png\"},{\"accessLevel\":1,\"fileType\":\"document\"}]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.9',1627,1,NULL,'2026-06-22 23:33:17','2026-06-22 23:33:17',NULL,NULL,0),(327471116840996864,'文件管理','上传文件','上传文件','POST','/files/upload','[{\"type\":\"MultipartFile\",\"size\":89317,\"originalFilename\":\"pdf_img_p143_0.png\"},{\"accessLevel\":1,\"fileType\":\"document\"}]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.9',318,1,NULL,'2026-06-22 23:33:18','2026-06-22 23:33:18',NULL,NULL,0),(327471121094021120,'文件管理','上传文件','上传文件','POST','/files/upload','[{\"type\":\"MultipartFile\",\"size\":117141,\"originalFilename\":\"pdf_img_p261_1.png\"},{\"accessLevel\":1,\"fileType\":\"document\"}]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.9',1197,1,NULL,'2026-06-22 23:33:19','2026-06-22 23:33:19',NULL,NULL,0),(327471124537544704,'文件管理','上传文件','上传文件','POST','/files/upload','[{\"type\":\"MultipartFile\",\"size\":105994,\"originalFilename\":\"pdf_img_p262_0.png\"},{\"accessLevel\":1,\"fileType\":\"document\"}]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.9',798,1,NULL,'2026-06-22 23:33:20','2026-06-22 23:33:20',NULL,NULL,0),(327471129423908864,'文件管理','上传文件','上传文件','POST','/files/upload','[{\"type\":\"MultipartFile\",\"size\":140763,\"originalFilename\":\"pdf_img_p147_0.png\"},{\"accessLevel\":1,\"fileType\":\"document\"}]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.9',798,1,NULL,'2026-06-22 23:33:21','2026-06-22 23:33:21',NULL,NULL,0),(327471134431907840,'文件管理','上传文件','上传文件','POST','/files/upload','[{\"type\":\"MultipartFile\",\"size\":174949,\"originalFilename\":\"pdf_img_p254_0.png\"},{\"accessLevel\":1,\"fileType\":\"document\"}]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.9',1023,0,'Expected one result (or null) to be returned by selectOne(), but found: 2','2026-06-22 23:33:22','2026-06-22 23:33:22',NULL,NULL,0),(327471137942540288,'文件管理','上传文件','上传文件','POST','/files/upload','[{\"type\":\"MultipartFile\",\"size\":200480,\"originalFilename\":\"pdf_img_p150_0.png\"},{\"accessLevel\":1,\"fileType\":\"document\"}]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.9',239,1,NULL,'2026-06-22 23:33:23','2026-06-22 23:33:23',NULL,NULL,0),(327471141704830976,'文件管理','上传文件','上传文件','POST','/files/upload','[{\"type\":\"MultipartFile\",\"size\":879550,\"originalFilename\":\"pdf_img_p264_0.png\"},{\"accessLevel\":1,\"fileType\":\"document\"}]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.9',1870,1,NULL,'2026-06-22 23:33:24','2026-06-22 23:33:24',NULL,NULL,0),(327471145098022912,'文件管理','上传文件','上传文件','POST','/files/upload','[{\"type\":\"MultipartFile\",\"size\":109674,\"originalFilename\":\"pdf_img_p152_0.png\"},{\"accessLevel\":1,\"fileType\":\"document\"}]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.9',608,1,NULL,'2026-06-22 23:33:25','2026-06-22 23:33:25',NULL,NULL,0),(327471146519891968,'文件管理','上传文件','上传文件','POST','/files/upload','[{\"type\":\"MultipartFile\",\"size\":167894,\"originalFilename\":\"pdf_img_p153_0.png\"},{\"accessLevel\":1,\"fileType\":\"document\"}]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.9',270,1,NULL,'2026-06-22 23:33:25','2026-06-22 23:33:25',NULL,NULL,0),(327471151041351680,'文件管理','上传文件','上传文件','POST','/files/upload','[{\"type\":\"MultipartFile\",\"size\":11711,\"originalFilename\":\"pdf_img_p265_0.png\"},{\"accessLevel\":1,\"fileType\":\"document\"}]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.9',1148,1,NULL,'2026-06-22 23:33:26','2026-06-22 23:33:26',NULL,NULL,0),(327471154329686016,'文件管理','上传文件','上传文件','POST','/files/upload','[{\"type\":\"MultipartFile\",\"size\":19287,\"originalFilename\":\"pdf_img_p265_1.png\"},{\"accessLevel\":1,\"fileType\":\"document\"}]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.9',764,1,NULL,'2026-06-22 23:33:27','2026-06-22 23:33:27',NULL,NULL,0),(327471162642796544,'文件管理','上传文件','上传文件','POST','/files/upload','[{\"type\":\"MultipartFile\",\"size\":13024,\"originalFilename\":\"pdf_img_p265_2.png\"},{\"accessLevel\":1,\"fileType\":\"document\"}]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.9',1962,1,NULL,'2026-06-22 23:33:29','2026-06-22 23:33:29',NULL,NULL,0),(327471165759164416,'文件管理','上传文件','上传文件','POST','/files/upload','[{\"type\":\"MultipartFile\",\"size\":220692,\"originalFilename\":\"pdf_img_p156_0.png\"},{\"accessLevel\":1,\"fileType\":\"document\"}]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.9',634,1,NULL,'2026-06-22 23:33:30','2026-06-22 23:33:30',NULL,NULL,0),(327471167319445504,'文件管理','上传文件','上传文件','POST','/files/upload','[{\"type\":\"MultipartFile\",\"size\":333635,\"originalFilename\":\"pdf_img_p157_0.png\"},{\"accessLevel\":1,\"fileType\":\"document\"}]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.9',288,1,NULL,'2026-06-22 23:33:30','2026-06-22 23:33:30',NULL,NULL,0),(327471171887042560,'文件管理','上传文件','上传文件','POST','/files/upload','[{\"type\":\"MultipartFile\",\"size\":13486,\"originalFilename\":\"pdf_img_p266_0.png\"},{\"accessLevel\":1,\"fileType\":\"document\"}]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.9',1159,1,NULL,'2026-06-22 23:33:31','2026-06-22 23:33:31',NULL,NULL,0),(327471176567885824,'文件管理','上传文件','上传文件','POST','/files/upload','[{\"type\":\"MultipartFile\",\"size\":7623,\"originalFilename\":\"pdf_img_p266_1.png\"},{\"accessLevel\":1,\"fileType\":\"document\"}]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.9',1133,1,NULL,'2026-06-22 23:33:32','2026-06-22 23:33:32',NULL,NULL,0),(327471179961077760,'文件管理','上传文件','上传文件','POST','/files/upload','[{\"type\":\"MultipartFile\",\"size\":41403,\"originalFilename\":\"pdf_img_p264_1.png\"},{\"accessLevel\":1,\"fileType\":\"document\"}]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.9',270,1,NULL,'2026-06-22 23:33:33','2026-06-22 23:33:33',NULL,NULL,0),(327471182960005120,'文件管理','上传文件','上传文件','POST','/files/upload','[{\"type\":\"MultipartFile\",\"size\":11711,\"originalFilename\":\"pdf_img_p265_0.png\"},{\"accessLevel\":1,\"fileType\":\"document\"}]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.9',685,0,'Expected one result (or null) to be returned by selectOne(), but found: 2','2026-06-22 23:33:34','2026-06-22 23:33:34',NULL,NULL,0),(327471184616755200,'文件管理','上传文件','上传文件','POST','/files/upload','[{\"type\":\"MultipartFile\",\"size\":21470,\"originalFilename\":\"pdf_img_p266_3.png\"},{\"accessLevel\":1,\"fileType\":\"document\"}]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.9',973,1,NULL,'2026-06-22 23:33:34','2026-06-22 23:33:34',NULL,NULL,0),(327471187980587008,'文件管理','上传文件','上传文件','POST','/files/upload','[{\"type\":\"MultipartFile\",\"size\":14962,\"originalFilename\":\"pdf_img_p265_3.png\"},{\"accessLevel\":1,\"fileType\":\"document\"}]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.9',239,0,'Expected one result (or null) to be returned by selectOne(), but found: 2','2026-06-22 23:33:35','2026-06-22 23:33:35',NULL,NULL,0),(327471192091004928,'文件管理','上传文件','上传文件','POST','/files/upload','[{\"type\":\"MultipartFile\",\"size\":13486,\"originalFilename\":\"pdf_img_p266_0.png\"},{\"accessLevel\":1,\"fileType\":\"document\"}]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.9',973,0,'Expected one result (or null) to be returned by selectOne(), but found: 2','2026-06-22 23:33:36','2026-06-22 23:33:36',NULL,NULL,0),(327471197124169728,'文件管理','上传文件','上传文件','POST','/files/upload','[{\"type\":\"MultipartFile\",\"size\":13157,\"originalFilename\":\"pdf_img_p267_1.png\"},{\"accessLevel\":1,\"fileType\":\"document\"}]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.9',1830,1,NULL,'2026-06-22 23:33:37','2026-06-22 23:33:37',NULL,NULL,0),(327471200127291392,'文件管理','上传文件','上传文件','POST','/files/upload','[{\"type\":\"MultipartFile\",\"size\":21470,\"originalFilename\":\"pdf_img_p266_3.png\"},{\"accessLevel\":1,\"fileType\":\"document\"}]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.9',590,0,'Expected one result (or null) to be returned by selectOne(), but found: 2','2026-06-22 23:33:38','2026-06-22 23:33:38',NULL,NULL,0),(327471201935036416,'文件管理','上传文件','上传文件','POST','/files/upload','[{\"type\":\"MultipartFile\",\"size\":13529,\"originalFilename\":\"pdf_img_p267_3.png\"},{\"accessLevel\":1,\"fileType\":\"document\"}]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.9',527,1,NULL,'2026-06-22 23:33:38','2026-06-22 23:33:38',NULL,NULL,0),(327471205860904960,'文件管理','上传文件','上传文件','POST','/files/upload','[{\"type\":\"MultipartFile\",\"size\":103338,\"originalFilename\":\"pdf_img_p165_0.png\"},{\"accessLevel\":1,\"fileType\":\"document\"}]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.9',259,1,NULL,'2026-06-22 23:33:39','2026-06-22 23:33:39',NULL,NULL,0),(327471209149239296,'文件管理','上传文件','上传文件','POST','/files/upload','[{\"type\":\"MultipartFile\",\"size\":13529,\"originalFilename\":\"pdf_img_p267_3.png\"},{\"accessLevel\":1,\"fileType\":\"document\"}]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.9',574,0,'Expected one result (or null) to be returned by selectOne(), but found: 2','2026-06-22 23:33:40','2026-06-22 23:33:40',NULL,NULL,0),(327471213066719232,'文件管理','上传文件','上传文件','POST','/files/upload','[{\"type\":\"MultipartFile\",\"size\":8531,\"originalFilename\":\"pdf_img_p268_1.png\"},{\"accessLevel\":1,\"fileType\":\"document\"}]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.9',1124,1,NULL,'2026-06-22 23:33:41','2026-06-22 23:33:41',NULL,NULL,0),(327471218225713152,'文件管理','上传文件','上传文件','POST','/files/upload','[{\"type\":\"MultipartFile\",\"size\":169322,\"originalFilename\":\"pdf_img_p167_1.png\"},{\"accessLevel\":1,\"fileType\":\"document\"}]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.9',909,1,NULL,'2026-06-22 23:33:42','2026-06-22 23:33:42',NULL,NULL,0),(327471220939427840,'文件管理','上传文件','上传文件','POST','/files/upload','[{\"type\":\"MultipartFile\",\"size\":113466,\"originalFilename\":\"pdf_img_p168_0.png\"},{\"accessLevel\":1,\"fileType\":\"document\"}]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.9',609,1,NULL,'2026-06-22 23:33:43','2026-06-22 23:33:43',NULL,NULL,0),(327471222982053888,'文件管理','上传文件','上传文件','POST','/files/upload','[{\"type\":\"MultipartFile\",\"size\":9934,\"originalFilename\":\"pdf_img_p269_0.png\"},{\"accessLevel\":1,\"fileType\":\"document\"}]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.9',576,1,NULL,'2026-06-22 23:33:43','2026-06-22 23:33:43',NULL,NULL,0),(327471230313697280,'文件管理','上传文件','上传文件','POST','/files/upload','[{\"type\":\"MultipartFile\",\"size\":10792,\"originalFilename\":\"pdf_img_p269_1.png\"},{\"accessLevel\":1,\"fileType\":\"document\"}]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.9',1678,1,NULL,'2026-06-22 23:33:45','2026-06-22 23:33:45',NULL,NULL,0),(327471234340229120,'文件管理','上传文件','上传文件','POST','/files/upload','[{\"type\":\"MultipartFile\",\"size\":8078,\"originalFilename\":\"pdf_img_p269_2.png\"},{\"accessLevel\":1,\"fileType\":\"document\"}]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.9',948,1,NULL,'2026-06-22 23:33:46','2026-06-22 23:33:46',NULL,NULL,0),(327471238773608448,'文件管理','上传文件','上传文件','POST','/files/upload','[{\"type\":\"MultipartFile\",\"size\":22417,\"originalFilename\":\"pdf_img_p269_3.png\"},{\"accessLevel\":1,\"fileType\":\"document\"}]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.9',1003,1,NULL,'2026-06-22 23:33:47','2026-06-22 23:33:47',NULL,NULL,0),(327471243936796672,'文件管理','上传文件','上传文件','POST','/files/upload','[{\"type\":\"MultipartFile\",\"size\":217035,\"originalFilename\":\"pdf_img_p173_0.png\"},{\"accessLevel\":1,\"fileType\":\"document\"}]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.9',814,1,NULL,'2026-06-22 23:33:48','2026-06-22 23:33:48',NULL,NULL,0),(327471248655388672,'文件管理','上传文件','上传文件','POST','/files/upload','[{\"type\":\"MultipartFile\",\"size\":229811,\"originalFilename\":\"pdf_img_p173_1.png\"},{\"accessLevel\":1,\"fileType\":\"document\"}]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.9',1040,1,NULL,'2026-06-22 23:33:49','2026-06-22 23:33:49',NULL,NULL,0),(327471251754979328,'文件管理','上传文件','上传文件','POST','/files/upload','[{\"type\":\"MultipartFile\",\"size\":21061,\"originalFilename\":\"pdf_img_p270_1.png\"},{\"accessLevel\":1,\"fileType\":\"document\"}]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.9',1330,1,NULL,'2026-06-22 23:33:50','2026-06-22 23:33:50',NULL,NULL,0),(327471258738495488,'文件管理','上传文件','上传文件','POST','/files/upload','[{\"type\":\"MultipartFile\",\"size\":11707,\"originalFilename\":\"pdf_img_p270_2.png\"},{\"accessLevel\":1,\"fileType\":\"document\"}]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.9',1626,1,NULL,'2026-06-22 23:33:52','2026-06-22 23:33:52',NULL,NULL,0),(327471264006541312,'文件管理','上传文件','上传文件','POST','/files/upload','[{\"type\":\"MultipartFile\",\"size\":13630,\"originalFilename\":\"pdf_img_p270_3.png\"},{\"accessLevel\":1,\"fileType\":\"document\"}]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.9',1402,1,NULL,'2026-06-22 23:33:53','2026-06-22 23:33:53',NULL,NULL,0),(327471275914170368,'文件管理','上传文件','上传文件','POST','/files/upload','[{\"type\":\"MultipartFile\",\"size\":180958,\"originalFilename\":\"pdf_img_p177_1.png\"},{\"accessLevel\":1,\"fileType\":\"document\"}]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.9',2609,1,NULL,'2026-06-22 23:33:56','2026-06-22 23:33:56',NULL,NULL,0),(327471277805801472,'文件管理','上传文件','上传文件','POST','/files/upload','[{\"type\":\"MultipartFile\",\"size\":12587,\"originalFilename\":\"pdf_img_p271_1.png\"},{\"accessLevel\":1,\"fileType\":\"document\"}]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.9',2962,1,NULL,'2026-06-22 23:33:56','2026-06-22 23:33:56',NULL,NULL,0),(327471289575018496,'文件管理','上传文件','上传文件','POST','/files/upload','[{\"type\":\"MultipartFile\",\"size\":269250,\"originalFilename\":\"pdf_img_p179_0.png\"},{\"accessLevel\":1,\"fileType\":\"document\"}]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.9',1187,1,NULL,'2026-06-22 23:33:59','2026-06-22 23:33:59',NULL,NULL,0),(327471291894468608,'文件管理','上传文件','上传文件','POST','/files/upload','[{\"type\":\"MultipartFile\",\"size\":108099,\"originalFilename\":\"pdf_img_p180_0.png\"},{\"accessLevel\":1,\"fileType\":\"document\"}]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.9',515,1,NULL,'2026-06-22 23:34:00','2026-06-22 23:34:00',NULL,NULL,0),(327471298139787264,'文件管理','上传文件','上传文件','POST','/files/upload','[{\"type\":\"MultipartFile\",\"size\":9060,\"originalFilename\":\"pdf_img_p272_0.png\"},{\"accessLevel\":1,\"fileType\":\"document\"}]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.9',1561,1,NULL,'2026-06-22 23:34:01','2026-06-22 23:34:01',NULL,NULL,0),(327471301696557056,'文件管理','上传文件','上传文件','POST','/files/upload','[{\"type\":\"MultipartFile\",\"size\":91043,\"originalFilename\":\"pdf_img_p181_1.png\"},{\"accessLevel\":1,\"fileType\":\"document\"}]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.9',381,1,NULL,'2026-06-22 23:34:02','2026-06-22 23:34:02',NULL,NULL,0),(327471307119792128,'文件管理','上传文件','上传文件','POST','/files/upload','[{\"type\":\"MultipartFile\",\"size\":15186,\"originalFilename\":\"pdf_img_p272_2.png\"},{\"accessLevel\":1,\"fileType\":\"document\"}]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.9',1534,1,NULL,'2026-06-22 23:34:03','2026-06-22 23:34:03',NULL,NULL,0),(327471310924025856,'文件管理','上传文件','上传文件','POST','/files/upload','[{\"type\":\"MultipartFile\",\"size\":19368,\"originalFilename\":\"pdf_img_p272_3.png\"},{\"accessLevel\":1,\"fileType\":\"document\"}]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.9',888,1,NULL,'2026-06-22 23:34:04','2026-06-22 23:34:04',NULL,NULL,0),(327471317139984384,'文件管理','上传文件','上传文件','POST','/files/upload','[{\"type\":\"MultipartFile\",\"size\":10943,\"originalFilename\":\"pdf_img_p273_0.png\"},{\"accessLevel\":1,\"fileType\":\"document\"}]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.9',1575,1,NULL,'2026-06-22 23:34:06','2026-06-22 23:34:06',NULL,NULL,0),(327471320143106048,'文件管理','上传文件','上传文件','POST','/files/upload','[{\"type\":\"MultipartFile\",\"size\":12585,\"originalFilename\":\"pdf_img_p273_1.png\"},{\"accessLevel\":1,\"fileType\":\"document\"}]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.9',671,1,NULL,'2026-06-22 23:34:06','2026-06-22 23:34:06',NULL,NULL,0),(327471326178709504,'文件管理','上传文件','上传文件','POST','/files/upload','[{\"type\":\"MultipartFile\",\"size\":129188,\"originalFilename\":\"pdf_img_p185_1.png\"},{\"accessLevel\":1,\"fileType\":\"document\"}]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.9',554,1,NULL,'2026-06-22 23:34:08','2026-06-22 23:34:08',NULL,NULL,0),(327471328108089344,'文件管理','上传文件','上传文件','POST','/files/upload','[{\"type\":\"MultipartFile\",\"size\":8901,\"originalFilename\":\"pdf_img_p273_2.png\"},{\"accessLevel\":1,\"fileType\":\"document\"}]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.9',1021,1,NULL,'2026-06-22 23:34:08','2026-06-22 23:34:08',NULL,NULL,0),(327471331442561024,'文件管理','上传文件','上传文件','POST','/files/upload','[{\"type\":\"MultipartFile\",\"size\":156401,\"originalFilename\":\"pdf_img_p187_1.png\"},{\"accessLevel\":1,\"fileType\":\"document\"}]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.9',256,1,NULL,'2026-06-22 23:34:09','2026-06-22 23:34:09',NULL,NULL,0),(327471336089849856,'文件管理','上传文件','上传文件','POST','/files/upload','[{\"type\":\"MultipartFile\",\"size\":10279,\"originalFilename\":\"pdf_img_p274_0.png\"},{\"accessLevel\":1,\"fileType\":\"document\"}]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.9',1298,1,NULL,'2026-06-22 23:34:10','2026-06-22 23:34:10',NULL,NULL,0),(327471339931832320,'文件管理','上传文件','上传文件','POST','/files/upload','[{\"type\":\"MultipartFile\",\"size\":8919,\"originalFilename\":\"pdf_img_p274_1.png\"},{\"accessLevel\":1,\"fileType\":\"document\"}]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.9',893,1,NULL,'2026-06-22 23:34:11','2026-06-22 23:34:11',NULL,NULL,0),(327471343077560320,'文件管理','上传文件','上传文件','POST','/files/upload','[{\"type\":\"MultipartFile\",\"size\":10178,\"originalFilename\":\"pdf_img_p274_2.png\"},{\"accessLevel\":1,\"fileType\":\"document\"}]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.9',730,1,NULL,'2026-06-22 23:34:12','2026-06-22 23:34:12',NULL,NULL,0),(327471347502551040,'文件管理','上传文件','上传文件','POST','/files/upload','[{\"type\":\"MultipartFile\",\"size\":10178,\"originalFilename\":\"pdf_img_p274_2.png\"},{\"accessLevel\":1,\"fileType\":\"document\"}]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.9',1631,1,NULL,'2026-06-22 23:34:13','2026-06-22 23:34:13',NULL,NULL,0),(327471348907642880,'文件管理','上传文件','上传文件','POST','/files/upload','[{\"type\":\"MultipartFile\",\"size\":12157,\"originalFilename\":\"pdf_img_p275_0.png\"},{\"accessLevel\":1,\"fileType\":\"document\"}]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.9',309,1,NULL,'2026-06-22 23:34:13','2026-06-22 23:34:13',NULL,NULL,0),(327471355685638144,'文件管理','上传文件','上传文件','POST','/files/upload','[{\"type\":\"MultipartFile\",\"size\":15149,\"originalFilename\":\"pdf_img_p275_1.png\"},{\"accessLevel\":1,\"fileType\":\"document\"}]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.9',1701,1,NULL,'2026-06-22 23:34:15','2026-06-22 23:34:15',NULL,NULL,0),(327471360139988992,'文件管理','上传文件','上传文件','POST','/files/upload','[{\"type\":\"MultipartFile\",\"size\":13789,\"originalFilename\":\"pdf_img_p275_2.png\"},{\"accessLevel\":1,\"fileType\":\"document\"}]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.9',1024,1,NULL,'2026-06-22 23:34:16','2026-06-22 23:34:16',NULL,NULL,0),(327471361616384000,'文件管理','上传文件','上传文件','POST','/files/upload','[{\"type\":\"MultipartFile\",\"size\":212906,\"originalFilename\":\"pdf_img_p195_0.png\"},{\"accessLevel\":1,\"fileType\":\"document\"}]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.9',294,0,'Expected one result (or null) to be returned by selectOne(), but found: 2','2026-06-22 23:34:16','2026-06-22 23:34:16',NULL,NULL,0),(327471368763478016,'文件管理','上传文件','上传文件','POST','/files/upload','[{\"type\":\"MultipartFile\",\"size\":11193,\"originalFilename\":\"pdf_img_p276_0.png\"},{\"accessLevel\":1,\"fileType\":\"document\"}]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.9',1714,1,NULL,'2026-06-22 23:34:18','2026-06-22 23:34:18',NULL,NULL,0),(327471372169252864,'文件管理','上传文件','上传文件','POST','/files/upload','[{\"type\":\"MultipartFile\",\"size\":97266,\"originalFilename\":\"pdf_img_p197_0.png\"},{\"accessLevel\":1,\"fileType\":\"document\"}]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.9',523,1,NULL,'2026-06-22 23:34:19','2026-06-22 23:34:19',NULL,NULL,0),(327471374497091584,'文件管理','上传文件','上传文件','POST','/files/upload','[{\"type\":\"MultipartFile\",\"size\":64603,\"originalFilename\":\"pdf_img_p197_1.png\"},{\"accessLevel\":1,\"fileType\":\"document\"}]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.9',421,0,'Expected one result (or null) to be returned by selectOne(), but found: 2','2026-06-22 23:34:19','2026-06-22 23:34:19',NULL,NULL,0),(327471377810591744,'文件管理','上传文件','上传文件','POST','/files/upload','[{\"type\":\"MultipartFile\",\"size\":158561,\"originalFilename\":\"pdf_img_p198_0.png\"},{\"accessLevel\":1,\"fileType\":\"document\"}]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.9',824,0,'Expected one result (or null) to be returned by selectOne(), but found: 2','2026-06-22 23:34:20','2026-06-22 23:34:20',NULL,NULL,0),(327471382235582464,'文件管理','上传文件','上传文件','POST','/files/upload','[{\"type\":\"MultipartFile\",\"size\":145865,\"originalFilename\":\"pdf_img_p199_0.png\"},{\"accessLevel\":1,\"fileType\":\"document\"}]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.9',678,0,'Expected one result (or null) to be returned by selectOne(), but found: 2','2026-06-22 23:34:21','2026-06-22 23:34:21',NULL,NULL,0),(327471386056593408,'文件管理','上传文件','上传文件','POST','/files/upload','[{\"type\":\"MultipartFile\",\"size\":15221,\"originalFilename\":\"pdf_img_p277_1.png\"},{\"accessLevel\":1,\"fileType\":\"document\"}]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.9',932,1,NULL,'2026-06-22 23:34:22','2026-06-22 23:34:22',NULL,NULL,0),(327471390989094912,'文件管理','上传文件','上传文件','POST','/files/upload','[{\"type\":\"MultipartFile\",\"size\":13387,\"originalFilename\":\"pdf_img_p277_2.png\"},{\"accessLevel\":1,\"fileType\":\"document\"}]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.9',1256,1,NULL,'2026-06-22 23:34:23','2026-06-22 23:34:23',NULL,NULL,0),(327471394684276736,'文件管理','上传文件','上传文件','POST','/files/upload','[{\"type\":\"MultipartFile\",\"size\":15784,\"originalFilename\":\"pdf_img_p278_0.png\"},{\"accessLevel\":1,\"fileType\":\"document\"}]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.9',847,1,NULL,'2026-06-22 23:34:24','2026-06-22 23:34:24',NULL,NULL,0),(327471399264456704,'文件管理','上传文件','上传文件','POST','/files/upload','[{\"type\":\"MultipartFile\",\"size\":139559,\"originalFilename\":\"pdf_img_p202_1.png\"},{\"accessLevel\":1,\"fileType\":\"document\"}]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.9',1011,0,'Expected one result (or null) to be returned by selectOne(), but found: 2','2026-06-22 23:34:25','2026-06-22 23:34:25',NULL,NULL,0),(327471402804449280,'文件管理','上传文件','上传文件','POST','/files/upload','[{\"type\":\"MultipartFile\",\"size\":19906,\"originalFilename\":\"pdf_img_p278_1.png\"},{\"accessLevel\":1,\"fileType\":\"document\"}]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.9',1892,1,NULL,'2026-06-22 23:34:26','2026-06-22 23:34:26',NULL,NULL,0),(327471408429010944,'文件管理','上传文件','上传文件','POST','/files/upload','[{\"type\":\"MultipartFile\",\"size\":19287,\"originalFilename\":\"pdf_img_p278_2.png\"},{\"accessLevel\":1,\"fileType\":\"document\"}]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.9',1418,1,NULL,'2026-06-22 23:34:27','2026-06-22 23:34:27',NULL,NULL,0),(327471412723978240,'文件管理','上传文件','上传文件','POST','/files/upload','[{\"type\":\"MultipartFile\",\"size\":10165,\"originalFilename\":\"pdf_img_p279_0.png\"},{\"accessLevel\":1,\"fileType\":\"document\"}]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.9',1989,1,NULL,'2026-06-22 23:34:28','2026-06-22 23:34:28',NULL,NULL,0),(327471421133557760,'文件管理','上传文件','上传文件','POST','/files/upload','[{\"type\":\"MultipartFile\",\"size\":11620,\"originalFilename\":\"pdf_img_p279_1.png\"},{\"accessLevel\":1,\"fileType\":\"document\"}]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.9',1912,1,NULL,'2026-06-22 23:34:30','2026-06-22 23:34:30',NULL,NULL,0),(327471425290113024,'文件管理','上传文件','上传文件','POST','/files/upload','[{\"type\":\"MultipartFile\",\"size\":172612,\"originalFilename\":\"pdf_img_p207_1.png\"},{\"accessLevel\":1,\"fileType\":\"document\"}]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.9',869,0,'Expected one result (or null) to be returned by selectOne(), but found: 2','2026-06-22 23:34:31','2026-06-22 23:34:31',NULL,NULL,0),(327471429908041728,'文件管理','上传文件','上传文件','POST','/files/upload','[{\"type\":\"MultipartFile\",\"size\":20922,\"originalFilename\":\"pdf_img_p279_2.png\"},{\"accessLevel\":1,\"fileType\":\"document\"}]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.9',1185,1,NULL,'2026-06-22 23:34:33','2026-06-22 23:34:33',NULL,NULL,0),(327471434198814720,'文件管理','上传文件','上传文件','POST','/files/upload','[{\"type\":\"MultipartFile\",\"size\":14000,\"originalFilename\":\"pdf_img_p280_0.png\"},{\"accessLevel\":1,\"fileType\":\"document\"}]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.9',1115,1,NULL,'2026-06-22 23:34:34','2026-06-22 23:34:34',NULL,NULL,0),(327471438061768704,'文件管理','上传文件','上传文件','POST','/files/upload','[{\"type\":\"MultipartFile\",\"size\":21537,\"originalFilename\":\"pdf_img_p280_1.png\"},{\"accessLevel\":1,\"fileType\":\"document\"}]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.9',892,1,NULL,'2026-06-22 23:34:34','2026-06-22 23:34:34',NULL,NULL,0),(327471440943255552,'文件管理','上传文件','上传文件','POST','/files/upload','[{\"type\":\"MultipartFile\",\"size\":22331,\"originalFilename\":\"pdf_img_p280_2.png\"},{\"accessLevel\":1,\"fileType\":\"document\"}]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.9',873,1,NULL,'2026-06-22 23:34:35','2026-06-22 23:34:35',NULL,NULL,0),(327471446261633024,'文件管理','上传文件','上传文件','POST','/files/upload','[{\"type\":\"MultipartFile\",\"size\":111395,\"originalFilename\":\"pdf_img_p212_1.png\"},{\"accessLevel\":1,\"fileType\":\"document\"}]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.9',1053,0,'Expected one result (or null) to be returned by selectOne(), but found: 2','2026-06-22 23:34:36','2026-06-22 23:34:36',NULL,NULL,0),(327471449466081280,'文件管理','上传文件','上传文件','POST','/files/upload','[{\"type\":\"MultipartFile\",\"size\":152804,\"originalFilename\":\"pdf_img_p213_0.png\"},{\"accessLevel\":1,\"fileType\":\"document\"}]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.9',716,0,'Expected one result (or null) to be returned by selectOne(), but found: 2','2026-06-22 23:34:37','2026-06-22 23:34:37',NULL,NULL,0),(327471453542944768,'文件管理','上传文件','上传文件','POST','/files/upload','[{\"type\":\"MultipartFile\",\"size\":18091,\"originalFilename\":\"pdf_img_p281_1.png\"},{\"accessLevel\":1,\"fileType\":\"document\"}]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.9',971,1,NULL,'2026-06-22 23:34:38','2026-06-22 23:34:38',NULL,NULL,0),(327471457036800000,'文件管理','上传文件','上传文件','POST','/files/upload','[{\"type\":\"MultipartFile\",\"size\":132436,\"originalFilename\":\"pdf_img_p214_1.png\"},{\"accessLevel\":1,\"fileType\":\"document\"}]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.9',571,0,'Expected one result (or null) to be returned by selectOne(), but found: 2','2026-06-22 23:34:39','2026-06-22 23:34:39',NULL,NULL,0),(327471461163995136,'文件管理','上传文件','上传文件','POST','/files/upload','[{\"type\":\"MultipartFile\",\"size\":14376,\"originalFilename\":\"pdf_img_p282_0.png\"},{\"accessLevel\":1,\"fileType\":\"document\"}]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.9',947,1,NULL,'2026-06-22 23:34:40','2026-06-22 23:34:40',NULL,NULL,0),(327471462451646464,'文件管理','上传文件','上传文件','POST','/files/upload','[{\"type\":\"MultipartFile\",\"size\":15116,\"originalFilename\":\"pdf_img_p282_1.png\"},{\"accessLevel\":1,\"fileType\":\"document\"}]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.9',295,1,NULL,'2026-06-22 23:34:40','2026-06-22 23:34:40',NULL,NULL,0),(327471466310406144,'文件管理','上传文件','上传文件','POST','/files/upload','[{\"type\":\"MultipartFile\",\"size\":9255,\"originalFilename\":\"pdf_img_p282_2.png\"},{\"accessLevel\":1,\"fileType\":\"document\"}]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.9',935,1,NULL,'2026-06-22 23:34:41','2026-06-22 23:34:41',NULL,NULL,0),(327471469737152512,'文件管理','上传文件','上传文件','POST','/files/upload','[{\"type\":\"MultipartFile\",\"size\":41349,\"originalFilename\":\"pdf_img_p283_0.png\"},{\"accessLevel\":1,\"fileType\":\"document\"}]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.9',795,1,NULL,'2026-06-22 23:34:42','2026-06-22 23:34:42',NULL,NULL,0),(327471471565869056,'文件管理','上传文件','上传文件','POST','/files/upload','[{\"type\":\"MultipartFile\",\"size\":207081,\"originalFilename\":\"pdf_img_p219_0.png\"},{\"accessLevel\":1,\"fileType\":\"document\"}]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.9',247,0,'Expected one result (or null) to be returned by selectOne(), but found: 2','2026-06-22 23:34:42','2026-06-22 23:34:42',NULL,NULL,0),(327471479610544128,'文件管理','上传文件','上传文件','POST','/files/upload','[{\"type\":\"MultipartFile\",\"size\":23969,\"originalFilename\":\"pdf_img_p283_1.png\"},{\"accessLevel\":1,\"fileType\":\"document\"}]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.9',2097,1,NULL,'2026-06-22 23:34:44','2026-06-22 23:34:44',NULL,NULL,0),(327471482714329088,'文件管理','上传文件','上传文件','POST','/files/upload','[{\"type\":\"MultipartFile\",\"size\":149262,\"originalFilename\":\"pdf_img_p221_0.png\"},{\"accessLevel\":1,\"fileType\":\"document\"}]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.9',537,0,'Expected one result (or null) to be returned by selectOne(), but found: 2','2026-06-22 23:34:45','2026-06-22 23:34:45',NULL,NULL,0),(327471488351473664,'文件管理','上传文件','上传文件','POST','/files/upload','[{\"type\":\"MultipartFile\",\"size\":13005,\"originalFilename\":\"pdf_img_p284_0.png\"},{\"accessLevel\":1,\"fileType\":\"document\"}]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.9',1441,1,NULL,'2026-06-22 23:34:46','2026-06-22 23:34:46',NULL,NULL,0),(327471495783780352,'文件管理','上传文件','上传文件','POST','/files/upload','[{\"type\":\"MultipartFile\",\"size\":16184,\"originalFilename\":\"pdf_img_p284_1.png\"},{\"accessLevel\":1,\"fileType\":\"document\"}]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.9',1753,1,NULL,'2026-06-22 23:34:48','2026-06-22 23:34:48',NULL,NULL,0),(327471501286707200,'文件管理','上传文件','上传文件','POST','/files/upload','[{\"type\":\"MultipartFile\",\"size\":255999,\"originalFilename\":\"pdf_img_p224_0.png\"},{\"accessLevel\":1,\"fileType\":\"document\"}]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.9',1036,0,'Expected one result (or null) to be returned by selectOne(), but found: 2','2026-06-22 23:34:50','2026-06-22 23:34:50',NULL,NULL,0),(327471504088502272,'文件管理','上传文件','上传文件','POST','/files/upload','[{\"type\":\"MultipartFile\",\"size\":108322,\"originalFilename\":\"pdf_img_p224_1.png\"},{\"accessLevel\":1,\"fileType\":\"document\"}]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.9',580,0,'Expected one result (or null) to be returned by selectOne(), but found: 2','2026-06-22 23:34:50','2026-06-22 23:34:50',NULL,NULL,0),(327471508857425920,'文件管理','上传文件','上传文件','POST','/files/upload','[{\"type\":\"MultipartFile\",\"size\":23955,\"originalFilename\":\"pdf_img_p285_0.png\"},{\"accessLevel\":1,\"fileType\":\"document\"}]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.9',1657,1,NULL,'2026-06-22 23:34:51','2026-06-22 23:34:51',NULL,NULL,0),(327471513223696384,'文件管理','上传文件','上传文件','POST','/files/upload','[{\"type\":\"MultipartFile\",\"size\":46861,\"originalFilename\":\"pdf_img_p285_1.png\"},{\"accessLevel\":1,\"fileType\":\"document\"}]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.9',1027,1,NULL,'2026-06-22 23:34:52','2026-06-22 23:34:52',NULL,NULL,0),(327471517061484544,'文件管理','上传文件','上传文件','POST','/files/upload','[{\"type\":\"MultipartFile\",\"size\":10477,\"originalFilename\":\"pdf_img_p285_2.png\"},{\"accessLevel\":1,\"fileType\":\"document\"}]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.9',966,1,NULL,'2026-06-22 23:34:53','2026-06-22 23:34:53',NULL,NULL,0),(327471521629081600,'文件管理','上传文件','上传文件','POST','/files/upload','[{\"type\":\"MultipartFile\",\"size\":22595,\"originalFilename\":\"pdf_img_p286_0.png\"},{\"accessLevel\":1,\"fileType\":\"document\"}]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.9',1306,1,NULL,'2026-06-22 23:34:54','2026-06-22 23:34:54',NULL,NULL,0),(327471526192484352,'文件管理','上传文件','上传文件','POST','/files/upload','[{\"type\":\"MultipartFile\",\"size\":16453,\"originalFilename\":\"pdf_img_p286_1.png\"},{\"accessLevel\":1,\"fileType\":\"document\"}]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.9',1060,1,NULL,'2026-06-22 23:34:55','2026-06-22 23:34:55',NULL,NULL,0),(327471530936242176,'文件管理','上传文件','上传文件','POST','/files/upload','[{\"type\":\"MultipartFile\",\"size\":125471,\"originalFilename\":\"pdf_img_p230_1.png\"},{\"accessLevel\":1,\"fileType\":\"document\"}]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.9',957,0,'Expected one result (or null) to be returned by selectOne(), but found: 2','2026-06-22 23:34:57','2026-06-22 23:34:57',NULL,NULL,0),(327471533549293568,'文件管理','上传文件','上传文件','POST','/files/upload','[{\"type\":\"MultipartFile\",\"size\":12888,\"originalFilename\":\"pdf_img_p286_2.png\"},{\"accessLevel\":1,\"fileType\":\"document\"}]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.9',776,1,NULL,'2026-06-22 23:34:57','2026-06-22 23:34:57',NULL,NULL,0),(327471538083336192,'文件管理','上传文件','上传文件','POST','/files/upload','[{\"type\":\"MultipartFile\",\"size\":15721,\"originalFilename\":\"pdf_img_p286_3.png\"},{\"accessLevel\":1,\"fileType\":\"document\"}]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.9',1049,1,NULL,'2026-06-22 23:34:58','2026-06-22 23:34:58',NULL,NULL,0),(327471545675026432,'文件管理','上传文件','上传文件','POST','/files/upload','[{\"type\":\"MultipartFile\",\"size\":26587,\"originalFilename\":\"pdf_img_p287_0.png\"},{\"accessLevel\":1,\"fileType\":\"document\"}]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.9',1797,1,NULL,'2026-06-22 23:35:00','2026-06-22 23:35:00',NULL,NULL,0),(327471548548124672,'文件管理','上传文件','上传文件','POST','/files/upload','[{\"type\":\"MultipartFile\",\"size\":115185,\"originalFilename\":\"pdf_img_p234_1.png\"},{\"accessLevel\":1,\"fileType\":\"document\"}]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.9',335,1,NULL,'2026-06-22 23:35:01','2026-06-22 23:35:01',NULL,NULL,0),(327471554092994560,'文件管理','上传文件','上传文件','POST','/files/upload','[{\"type\":\"MultipartFile\",\"size\":21850,\"originalFilename\":\"pdf_img_p287_2.png\"},{\"accessLevel\":1,\"fileType\":\"document\"}]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.9',1551,1,NULL,'2026-06-22 23:35:02','2026-06-22 23:35:02',NULL,NULL,0),(327471556269838336,'文件管理','上传文件','上传文件','POST','/files/upload','[{\"type\":\"MultipartFile\",\"size\":65589,\"originalFilename\":\"pdf_img_p236_1.png\"},{\"accessLevel\":1,\"fileType\":\"document\"}]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.9',239,0,'Expected one result (or null) to be returned by selectOne(), but found: 2','2026-06-22 23:35:03','2026-06-22 23:35:03',NULL,NULL,0),(327471559570755584,'文件管理','上传文件','上传文件','POST','/files/upload','[{\"type\":\"MultipartFile\",\"size\":33771,\"originalFilename\":\"pdf_img_p288_0.png\"},{\"accessLevel\":1,\"fileType\":\"document\"}]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.9',1003,1,NULL,'2026-06-22 23:35:03','2026-06-22 23:35:03',NULL,NULL,0),(327471564993990656,'文件管理','上传文件','上传文件','POST','/files/upload','[{\"type\":\"MultipartFile\",\"size\":23184,\"originalFilename\":\"pdf_img_p288_1.png\"},{\"accessLevel\":1,\"fileType\":\"document\"}]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.9',1266,1,NULL,'2026-06-22 23:35:05','2026-06-22 23:35:05',NULL,NULL,0),(327471573168689152,'文件管理','上传文件','上传文件','POST','/files/upload','[{\"type\":\"MultipartFile\",\"size\":184977,\"originalFilename\":\"pdf_img_p240_0.png\"},{\"accessLevel\":1,\"fileType\":\"document\"}]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.9',222,0,'Expected one result (or null) to be returned by selectOne(), but found: 2','2026-06-22 23:35:07','2026-06-22 23:35:07',NULL,NULL,0),(327471577585291264,'文件管理','上传文件','上传文件','POST','/files/upload','[{\"type\":\"MultipartFile\",\"size\":235746,\"originalFilename\":\"pdf_img_p240_1.png\"},{\"accessLevel\":1,\"fileType\":\"document\"}]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.9',962,0,'Expected one result (or null) to be returned by selectOne(), but found: 2','2026-06-22 23:35:08','2026-06-22 23:35:08',NULL,NULL,0),(327471580395474944,'文件管理','上传文件','上传文件','POST','/files/upload','[{\"type\":\"MultipartFile\",\"size\":30048,\"originalFilename\":\"pdf_img_p290_0.png\"},{\"accessLevel\":1,\"fileType\":\"document\"}]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.9',1080,1,NULL,'2026-06-22 23:35:08','2026-06-22 23:35:08',NULL,NULL,0),(327471584531058688,'文件管理','上传文件','上传文件','POST','/files/upload','[{\"type\":\"MultipartFile\",\"size\":328796,\"originalFilename\":\"pdf_img_p242_1.png\"},{\"accessLevel\":1,\"fileType\":\"document\"}]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.9',533,0,'Expected one result (or null) to be returned by selectOne(), but found: 2','2026-06-22 23:35:09','2026-06-22 23:35:09',NULL,NULL,0),(327471588154937344,'文件管理','上传文件','上传文件','POST','/files/upload','[{\"type\":\"MultipartFile\",\"size\":159534,\"originalFilename\":\"pdf_img_p243_0.png\"},{\"accessLevel\":1,\"fileType\":\"document\"}]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.9',895,0,'Expected one result (or null) to be returned by selectOne(), but found: 2','2026-06-22 23:35:10','2026-06-22 23:35:10',NULL,NULL,0),(327471590143037440,'文件管理','上传文件','上传文件','POST','/files/upload','[{\"type\":\"MultipartFile\",\"size\":23051,\"originalFilename\":\"pdf_img_p290_2.png\"},{\"accessLevel\":1,\"fileType\":\"document\"}]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.9',1031,1,NULL,'2026-06-22 23:35:11','2026-06-22 23:35:11',NULL,NULL,0),(327471597411766272,'文件管理','上传文件','上传文件','POST','/files/upload','[{\"type\":\"MultipartFile\",\"size\":22605,\"originalFilename\":\"pdf_img_p291_0.png\"},{\"accessLevel\":1,\"fileType\":\"document\"}]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.9',1534,1,NULL,'2026-06-22 23:35:12','2026-06-22 23:35:12',NULL,NULL,0),(327471602591731712,'文件管理','上传文件','上传文件','POST','/files/upload','[{\"type\":\"MultipartFile\",\"size\":30189,\"originalFilename\":\"pdf_img_p291_1.png\"},{\"accessLevel\":1,\"fileType\":\"document\"}]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.9',1723,1,NULL,'2026-06-22 23:35:14','2026-06-22 23:35:14',NULL,NULL,0),(327471610091147264,'文件管理','上传文件','上传文件','POST','/files/upload','[{\"type\":\"MultipartFile\",\"size\":197703,\"originalFilename\":\"pdf_img_p247_0.png\"},{\"accessLevel\":1,\"fileType\":\"document\"}]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.9',966,0,'Expected one result (or null) to be returned by selectOne(), but found: 2','2026-06-22 23:35:15','2026-06-22 23:35:15',NULL,NULL,0),(327471614251896832,'文件管理','上传文件','上传文件','POST','/files/upload','[{\"type\":\"MultipartFile\",\"size\":36167,\"originalFilename\":\"pdf_img_p292_0.png\"},{\"accessLevel\":1,\"fileType\":\"document\"}]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.9',990,1,NULL,'2026-06-22 23:35:16','2026-06-22 23:35:16',NULL,NULL,0),(327471619750629376,'文件管理','上传文件','上传文件','POST','/files/upload','[{\"type\":\"MultipartFile\",\"size\":21754,\"originalFilename\":\"pdf_img_p292_1.png\"},{\"accessLevel\":1,\"fileType\":\"document\"}]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.9',1164,1,NULL,'2026-06-22 23:35:18','2026-06-22 23:35:18',NULL,NULL,0),(327471623831687168,'文件管理','上传文件','上传文件','POST','/files/upload','[{\"type\":\"MultipartFile\",\"size\":204220,\"originalFilename\":\"pdf_img_p249_1.png\"},{\"accessLevel\":1,\"fileType\":\"document\"}]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.9',1006,0,'Expected one result (or null) to be returned by selectOne(), but found: 2','2026-06-22 23:35:19','2026-06-22 23:35:19',NULL,NULL,0),(327471630102171648,'文件管理','上传文件','上传文件','POST','/files/upload','[{\"type\":\"MultipartFile\",\"size\":22698,\"originalFilename\":\"pdf_img_p292_2.png\"},{\"accessLevel\":1,\"fileType\":\"document\"}]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.9',1471,1,NULL,'2026-06-22 23:35:20','2026-06-22 23:35:20',NULL,NULL,0),(327471631498874880,'文件管理','上传文件','上传文件','POST','/files/upload','[{\"type\":\"MultipartFile\",\"size\":40204,\"originalFilename\":\"pdf_img_p293_0.png\"},{\"accessLevel\":1,\"fileType\":\"document\"}]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.9',316,0,'Expected one result (or null) to be returned by selectOne(), but found: 2','2026-06-22 23:35:21','2026-06-22 23:35:21',NULL,NULL,0),(327471638943764480,'文件管理','上传文件','上传文件','POST','/files/upload','[{\"type\":\"MultipartFile\",\"size\":340206,\"originalFilename\":\"pdf_img_p252_0.png\"},{\"accessLevel\":1,\"fileType\":\"document\"}]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.9',1480,0,'Expected one result (or null) to be returned by selectOne(), but found: 2','2026-06-22 23:35:22','2026-06-22 23:35:22',NULL,NULL,0),(327471643372949504,'文件管理','上传文件','上传文件','POST','/files/upload','[{\"type\":\"MultipartFile\",\"size\":347193,\"originalFilename\":\"pdf_img_p253_0.png\"},{\"accessLevel\":1,\"fileType\":\"document\"}]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.9',569,0,'Expected one result (or null) to be returned by selectOne(), but found: 2','2026-06-22 23:35:23','2026-06-22 23:35:23',NULL,NULL,0),(327471647605002240,'文件管理','上传文件','上传文件','POST','/files/upload','[{\"type\":\"MultipartFile\",\"size\":24223,\"originalFilename\":\"pdf_img_p294_1.png\"},{\"accessLevel\":1,\"fileType\":\"document\"}]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.9',1025,1,NULL,'2026-06-22 23:35:24','2026-06-22 23:35:24',NULL,NULL,0),(327471651048525824,'文件管理','上传文件','上传文件','POST','/files/upload','[{\"type\":\"MultipartFile\",\"size\":24223,\"originalFilename\":\"pdf_img_p294_1.png\"},{\"accessLevel\":1,\"fileType\":\"document\"}]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.9',1607,1,NULL,'2026-06-22 23:35:25','2026-06-22 23:35:25',NULL,NULL,0),(327471655330910208,'文件管理','上传文件','上传文件','POST','/files/upload','[{\"type\":\"MultipartFile\",\"size\":18852,\"originalFilename\":\"pdf_img_p294_2.png\"},{\"accessLevel\":1,\"fileType\":\"document\"}]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.9',1387,1,NULL,'2026-06-22 23:35:26','2026-06-22 23:35:26',NULL,NULL,0),(327471659684597760,'文件管理','上传文件','上传文件','POST','/files/upload','[{\"type\":\"MultipartFile\",\"size\":173782,\"originalFilename\":\"pdf_img_p257_0.png\"},{\"accessLevel\":1,\"fileType\":\"document\"}]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.9',581,0,'Expected one result (or null) to be returned by selectOne(), but found: 2','2026-06-22 23:35:27','2026-06-22 23:35:27',NULL,NULL,0),(327471661316182016,'文件管理','上传文件','上传文件','POST','/files/upload','[{\"type\":\"MultipartFile\",\"size\":32549,\"originalFilename\":\"pdf_img_p295_1.png\"},{\"accessLevel\":1,\"fileType\":\"document\"}]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.9',1073,1,NULL,'2026-06-22 23:35:28','2026-06-22 23:35:28',NULL,NULL,0),(327471665338519552,'文件管理','上传文件','上传文件','POST','/files/upload','[{\"type\":\"MultipartFile\",\"size\":133000,\"originalFilename\":\"pdf_img_p259_0.png\"},{\"accessLevel\":1,\"fileType\":\"document\"}]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.9',342,0,'Expected one result (or null) to be returned by selectOne(), but found: 2','2026-06-22 23:35:29','2026-06-22 23:35:29',NULL,NULL,0),(327471672238149632,'文件管理','上传文件','上传文件','POST','/files/upload','[{\"type\":\"MultipartFile\",\"size\":83095,\"originalFilename\":\"pdf_img_p259_1.png\"},{\"accessLevel\":1,\"fileType\":\"document\"}]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.9',1497,0,'Expected one result (or null) to be returned by selectOne(), but found: 2','2026-06-22 23:35:30','2026-06-22 23:35:30',NULL,NULL,0),(327471673949425664,'文件管理','上传文件','上传文件','POST','/files/upload','[{\"type\":\"MultipartFile\",\"size\":22498,\"originalFilename\":\"pdf_img_p296_0.png\"},{\"accessLevel\":1,\"fileType\":\"document\"}]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.9',1845,1,NULL,'2026-06-22 23:35:31','2026-06-22 23:35:31',NULL,NULL,0),(327471681402703872,'文件管理','上传文件','上传文件','POST','/files/upload','[{\"type\":\"MultipartFile\",\"size\":38035,\"originalFilename\":\"pdf_img_p296_1.png\"},{\"accessLevel\":1,\"fileType\":\"document\"}]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.9',1738,1,NULL,'2026-06-22 23:35:32','2026-06-22 23:35:32',NULL,NULL,0),(327471686427480064,'文件管理','上传文件','上传文件','POST','/files/upload','[{\"type\":\"MultipartFile\",\"size\":23850,\"originalFilename\":\"pdf_img_p297_0.png\"},{\"accessLevel\":1,\"fileType\":\"document\"}]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.9',1394,1,NULL,'2026-06-22 23:35:34','2026-06-22 23:35:34',NULL,NULL,0),(327471690890219520,'文件管理','上传文件','上传文件','POST','/files/upload','[{\"type\":\"MultipartFile\",\"size\":24147,\"originalFilename\":\"pdf_img_p297_1.png\"},{\"accessLevel\":1,\"fileType\":\"document\"}]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.9',1048,1,NULL,'2026-06-22 23:35:35','2026-06-22 23:35:35',NULL,NULL,0),(327471694375686144,'文件管理','上传文件','上传文件','POST','/files/upload','[{\"type\":\"MultipartFile\",\"size\":41403,\"originalFilename\":\"pdf_img_p264_1.png\"},{\"accessLevel\":1,\"fileType\":\"document\"}]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.9',655,1,NULL,'2026-06-22 23:35:36','2026-06-22 23:35:36',NULL,NULL,0),(327471697806626816,'文件管理','上传文件','上传文件','POST','/files/upload','[{\"type\":\"MultipartFile\",\"size\":19629,\"originalFilename\":\"pdf_img_p298_1.png\"},{\"accessLevel\":1,\"fileType\":\"document\"}]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.9',848,1,NULL,'2026-06-22 23:35:36','2026-06-22 23:35:36',NULL,NULL,0),(327471700067356672,'文件管理','上传文件','上传文件','POST','/files/upload','[{\"type\":\"MultipartFile\",\"size\":13024,\"originalFilename\":\"pdf_img_p265_2.png\"},{\"accessLevel\":1,\"fileType\":\"document\"}]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.9',364,0,'Expected one result (or null) to be returned by selectOne(), but found: 2','2026-06-22 23:35:37','2026-06-22 23:35:37',NULL,NULL,0),(327471703066284032,'文件管理','上传文件','上传文件','POST','/files/upload','[{\"type\":\"MultipartFile\",\"size\":14962,\"originalFilename\":\"pdf_img_p265_3.png\"},{\"accessLevel\":1,\"fileType\":\"document\"}]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.9',633,0,'Expected one result (or null) to be returned by selectOne(), but found: 2','2026-06-22 23:35:38','2026-06-22 23:35:38',NULL,NULL,0),(327471710012051456,'文件管理','上传文件','上传文件','POST','/files/upload','[{\"type\":\"MultipartFile\",\"size\":15139,\"originalFilename\":\"pdf_img_p299_0.png\"},{\"accessLevel\":1,\"fileType\":\"document\"}]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.9',1817,1,NULL,'2026-06-22 23:35:39','2026-06-22 23:35:39',NULL,NULL,0),(327471711844962304,'文件管理','上传文件','上传文件','POST','/files/upload','[{\"type\":\"MultipartFile\",\"size\":28924,\"originalFilename\":\"pdf_img_p299_1.png\"},{\"accessLevel\":1,\"fileType\":\"document\"}]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.9',1275,1,NULL,'2026-06-22 23:35:40','2026-06-22 23:35:40',NULL,NULL,0),(327471718958501888,'文件管理','上传文件','上传文件','POST','/files/upload','[{\"type\":\"MultipartFile\",\"size\":18200,\"originalFilename\":\"pdf_img_p299_2.png\"},{\"accessLevel\":1,\"fileType\":\"document\"}]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.9',1612,1,NULL,'2026-06-22 23:35:41','2026-06-22 23:35:41',NULL,NULL,0),(327471720988545024,'文件管理','上传文件','上传文件','POST','/files/upload','[{\"type\":\"MultipartFile\",\"size\":36944,\"originalFilename\":\"pdf_img_p300_0.png\"},{\"accessLevel\":1,\"fileType\":\"document\"}]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.9',902,1,NULL,'2026-06-22 23:35:42','2026-06-22 23:35:42',NULL,NULL,0),(327471724926996480,'文件管理','上传文件','上传文件','POST','/files/upload','[{\"type\":\"MultipartFile\",\"size\":14578,\"originalFilename\":\"pdf_img_p300_1.png\"},{\"accessLevel\":1,\"fileType\":\"document\"}]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.9',873,1,NULL,'2026-06-22 23:35:43','2026-06-22 23:35:43',NULL,NULL,0),(327471732699041792,'文件管理','上传文件','上传文件','POST','/files/upload','[{\"type\":\"MultipartFile\",\"size\":14578,\"originalFilename\":\"pdf_img_p300_1.png\"},{\"accessLevel\":1,\"fileType\":\"document\"}]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.9',2254,1,NULL,'2026-06-22 23:35:45','2026-06-22 23:35:45',NULL,NULL,0),(327471733881835520,'文件管理','上传文件','上传文件','POST','/files/upload','[{\"type\":\"MultipartFile\",\"size\":24137,\"originalFilename\":\"pdf_img_p300_2.png\"},{\"accessLevel\":1,\"fileType\":\"document\"}]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.9',1405,1,NULL,'2026-06-22 23:35:45','2026-06-22 23:35:45',NULL,NULL,0),(327471740131348480,'文件管理','上传文件','上传文件','POST','/files/upload','[{\"type\":\"MultipartFile\",\"size\":10792,\"originalFilename\":\"pdf_img_p269_1.png\"},{\"accessLevel\":1,\"fileType\":\"document\"}]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.9',797,0,'Expected one result (or null) to be returned by selectOne(), but found: 3','2026-06-22 23:35:46','2026-06-22 23:35:46',NULL,NULL,0),(327471741720989696,'文件管理','上传文件','上传文件','POST','/files/upload','[{\"type\":\"MultipartFile\",\"size\":46584,\"originalFilename\":\"pdf_img_p301_1.png\"},{\"accessLevel\":1,\"fileType\":\"document\"}]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.9',1029,1,NULL,'2026-06-22 23:35:47','2026-06-22 23:35:47',NULL,NULL,0),(327471749774053376,'文件管理','上传文件','上传文件','POST','/files/upload','[{\"type\":\"MultipartFile\",\"size\":24798,\"originalFilename\":\"pdf_img_p301_2.png\"},{\"accessLevel\":1,\"fileType\":\"document\"}]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.9',1900,1,NULL,'2026-06-22 23:35:49','2026-06-22 23:35:49',NULL,NULL,0),(327471757126668288,'文件管理','上传文件','上传文件','POST','/files/upload','[{\"type\":\"MultipartFile\",\"size\":21061,\"originalFilename\":\"pdf_img_p270_1.png\"},{\"accessLevel\":1,\"fileType\":\"document\"}]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.9',1554,0,'Expected one result (or null) to be returned by selectOne(), but found: 3','2026-06-22 23:35:51','2026-06-22 23:35:51',NULL,NULL,0),(327471759077019648,'文件管理','上传文件','上传文件','POST','/files/upload','[{\"type\":\"MultipartFile\",\"size\":19990,\"originalFilename\":\"pdf_img_p302_0.png\"},{\"accessLevel\":1,\"fileType\":\"document\"}]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.9',1076,1,NULL,'2026-06-22 23:35:51','2026-06-22 23:35:51',NULL,NULL,0),(327471763371986944,'文件管理','上传文件','上传文件','POST','/files/upload','[{\"type\":\"MultipartFile\",\"size\":9409,\"originalFilename\":\"pdf_img_p271_0.png\"},{\"accessLevel\":1,\"fileType\":\"document\"}]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.9',279,0,'Expected one result (or null) to be returned by selectOne(), but found: 3','2026-06-22 23:35:52','2026-06-22 23:35:52',NULL,NULL,0),(327471766563852288,'文件管理','上传文件','上传文件','POST','/files/upload','[{\"type\":\"MultipartFile\",\"size\":15831,\"originalFilename\":\"pdf_img_p302_2.png\"},{\"accessLevel\":1,\"fileType\":\"document\"}]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.9',353,1,NULL,'2026-06-22 23:35:53','2026-06-22 23:35:53',NULL,NULL,0),(327471770930122752,'文件管理','上传文件','上传文件','POST','/files/upload','[{\"type\":\"MultipartFile\",\"size\":21208,\"originalFilename\":\"pdf_img_p303_0.png\"},{\"accessLevel\":1,\"fileType\":\"document\"}]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.9',1076,1,NULL,'2026-06-22 23:35:54','2026-06-22 23:35:54',NULL,NULL,0),(327471779390033920,'文件管理','上传文件','上传文件','POST','/files/upload','[{\"type\":\"MultipartFile\",\"size\":37153,\"originalFilename\":\"pdf_img_p303_1.png\"},{\"accessLevel\":1,\"fileType\":\"document\"}]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.9',2014,1,NULL,'2026-06-22 23:35:56','2026-06-22 23:35:56',NULL,NULL,0),(327471782858723328,'文件管理','上传文件','上传文件','POST','/files/upload','[{\"type\":\"MultipartFile\",\"size\":37153,\"originalFilename\":\"pdf_img_p303_1.png\"},{\"accessLevel\":1,\"fileType\":\"document\"}]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.9',1816,1,NULL,'2026-06-22 23:35:57','2026-06-22 23:35:57',NULL,NULL,0),(327471786109308928,'文件管理','上传文件','上传文件','POST','/files/upload','[{\"type\":\"MultipartFile\",\"size\":12324,\"originalFilename\":\"pdf_img_p303_2.png\"},{\"accessLevel\":1,\"fileType\":\"document\"}]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.9',748,1,NULL,'2026-06-22 23:35:57','2026-06-22 23:35:57',NULL,NULL,0),(327471788965629952,'文件管理','上传文件','上传文件','POST','/files/upload','[{\"type\":\"MultipartFile\",\"size\":21470,\"originalFilename\":\"pdf_img_p304_0.png\"},{\"accessLevel\":1,\"fileType\":\"document\"}]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.9',983,1,NULL,'2026-06-22 23:35:58','2026-06-22 23:35:58',NULL,NULL,0),(327471794674077696,'文件管理','上传文件','上传文件','POST','/files/upload','[{\"type\":\"MultipartFile\",\"size\":59633,\"originalFilename\":\"pdf_img_p304_1.png\"},{\"accessLevel\":1,\"fileType\":\"document\"}]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.9',1484,1,NULL,'2026-06-22 23:35:59','2026-06-22 23:35:59',NULL,NULL,0),(327471797899497472,'文件管理','上传文件','上传文件','POST','/files/upload','[{\"type\":\"MultipartFile\",\"size\":8919,\"originalFilename\":\"pdf_img_p274_1.png\"},{\"accessLevel\":1,\"fileType\":\"document\"}]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.9',300,0,'Expected one result (or null) to be returned by selectOne(), but found: 3','2026-06-22 23:36:00','2026-06-22 23:36:00',NULL,NULL,0),(327471799711436800,'文件管理','上传文件','上传文件','POST','/files/upload','[{\"type\":\"MultipartFile\",\"size\":30520,\"originalFilename\":\"pdf_img_p305_0.png\"},{\"accessLevel\":1,\"fileType\":\"document\"}]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.9',879,1,NULL,'2026-06-22 23:36:01','2026-06-22 23:36:01',NULL,NULL,0),(327471805319221248,'文件管理','上传文件','上传文件','POST','/files/upload','[{\"type\":\"MultipartFile\",\"size\":15149,\"originalFilename\":\"pdf_img_p275_1.png\"},{\"accessLevel\":1,\"fileType\":\"document\"}]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.9',1085,0,'Expected one result (or null) to be returned by selectOne(), but found: 3','2026-06-22 23:36:02','2026-06-22 23:36:02',NULL,NULL,0),(327471813229678592,'文件管理','上传文件','上传文件','POST','/files/upload','[{\"type\":\"MultipartFile\",\"size\":19521,\"originalFilename\":\"pdf_img_p306_0.png\"},{\"accessLevel\":1,\"fileType\":\"document\"}]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.9',2805,1,NULL,'2026-06-22 23:36:04','2026-06-22 23:36:04',NULL,NULL,0),(327471817512062976,'文件管理','上传文件','上传文件','POST','/files/upload','[{\"type\":\"MultipartFile\",\"size\":21651,\"originalFilename\":\"pdf_img_p306_1.png\"},{\"accessLevel\":1,\"fileType\":\"document\"}]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.9',990,1,NULL,'2026-06-22 23:36:05','2026-06-22 23:36:05',NULL,NULL,0),(327471821186273280,'文件管理','上传文件','上传文件','POST','/files/upload','[{\"type\":\"MultipartFile\",\"size\":23326,\"originalFilename\":\"pdf_img_p276_2.png\"},{\"accessLevel\":1,\"fileType\":\"document\"}]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.9',548,0,'Expected one result (or null) to be returned by selectOne(), but found: 3','2026-06-22 23:36:06','2026-06-22 23:36:06',NULL,NULL,0),(327471823283425280,'文件管理','上传文件','上传文件','POST','/files/upload','[{\"type\":\"MultipartFile\",\"size\":28420,\"originalFilename\":\"pdf_img_p306_2.png\"},{\"accessLevel\":1,\"fileType\":\"document\"}]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.9',1038,1,NULL,'2026-06-22 23:36:06','2026-06-22 23:36:06',NULL,NULL,0),(327471827158962176,'文件管理','上传文件','上传文件','POST','/files/upload','[{\"type\":\"MultipartFile\",\"size\":39389,\"originalFilename\":\"pdf_img_p306_3.png\"},{\"accessLevel\":1,\"fileType\":\"document\"}]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.9',962,1,NULL,'2026-06-22 23:36:07','2026-06-22 23:36:07',NULL,NULL,0),(327471830401159168,'文件管理','上传文件','上传文件','POST','/files/upload','[{\"type\":\"MultipartFile\",\"size\":19906,\"originalFilename\":\"pdf_img_p278_1.png\"},{\"accessLevel\":1,\"fileType\":\"document\"}]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.9',317,0,'Expected one result (or null) to be returned by selectOne(), but found: 3','2026-06-22 23:36:08','2026-06-22 23:36:08',NULL,NULL,0),(327471838185787392,'文件管理','上传文件','上传文件','POST','/files/upload','[{\"type\":\"MultipartFile\",\"size\":24611,\"originalFilename\":\"pdf_img_p307_1.png\"},{\"accessLevel\":1,\"fileType\":\"document\"}]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.9',1848,1,NULL,'2026-06-22 23:36:10','2026-06-22 23:36:10',NULL,NULL,0),(327471843516747776,'文件管理','上传文件','上传文件','POST','/files/upload','[{\"type\":\"MultipartFile\",\"size\":11620,\"originalFilename\":\"pdf_img_p279_1.png\"},{\"accessLevel\":1,\"fileType\":\"document\"}]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.9',1167,0,'Expected one result (or null) to be returned by selectOne(), but found: 3','2026-06-22 23:36:11','2026-06-22 23:36:11',NULL,NULL,0),(327471850059862016,'文件管理','上传文件','上传文件','POST','/files/upload','[{\"type\":\"MultipartFile\",\"size\":14000,\"originalFilename\":\"pdf_img_p280_0.png\"},{\"accessLevel\":1,\"fileType\":\"document\"}]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.9',498,0,'Expected one result (or null) to be returned by selectOne(), but found: 3','2026-06-22 23:36:13','2026-06-22 23:36:13',NULL,NULL,0),(327471855407599616,'文件管理','上传文件','上传文件','POST','/files/upload','[{\"type\":\"MultipartFile\",\"size\":18792,\"originalFilename\":\"pdf_img_p307_3.png\"},{\"accessLevel\":1,\"fileType\":\"document\"}]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.9',1373,1,NULL,'2026-06-22 23:36:14','2026-06-22 23:36:14',NULL,NULL,0),(327471859065032704,'文件管理','上传文件','上传文件','POST','/files/upload','[{\"type\":\"MultipartFile\",\"size\":42092,\"originalFilename\":\"pdf_img_p308_0.png\"},{\"accessLevel\":1,\"fileType\":\"document\"}]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.9',1749,1,NULL,'2026-06-22 23:36:15','2026-06-22 23:36:15',NULL,NULL,0),(327471864060448768,'文件管理','上传文件','上传文件','POST','/files/upload','[{\"type\":\"MultipartFile\",\"size\":15659,\"originalFilename\":\"pdf_img_p281_2.png\"},{\"accessLevel\":1,\"fileType\":\"document\"}]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.9',894,0,'Expected one result (or null) to be returned by selectOne(), but found: 3','2026-06-22 23:36:16','2026-06-22 23:36:16',NULL,NULL,0),(327471866530893824,'文件管理','上传文件','上传文件','POST','/files/upload','[{\"type\":\"MultipartFile\",\"size\":11643,\"originalFilename\":\"pdf_img_p308_1.png\"},{\"accessLevel\":1,\"fileType\":\"document\"}]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.9',1535,1,NULL,'2026-06-22 23:36:17','2026-06-22 23:36:17',NULL,NULL,0),(327471867969540096,'文件管理','上传文件','上传文件','POST','/files/upload','[{\"type\":\"MultipartFile\",\"size\":7412,\"originalFilename\":\"pdf_img_p309_0.png\"},{\"accessLevel\":1,\"fileType\":\"document\"}]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.9',318,1,NULL,'2026-06-22 23:36:17','2026-06-22 23:36:17',NULL,NULL,0),(327471873317277696,'文件管理','上传文件','上传文件','POST','/files/upload','[{\"type\":\"MultipartFile\",\"size\":18553,\"originalFilename\":\"pdf_img_p309_1.png\"},{\"accessLevel\":1,\"fileType\":\"document\"}]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.9',1306,1,NULL,'2026-06-22 23:36:18','2026-06-22 23:36:18',NULL,NULL,0),(327471877352198144,'文件管理','上传文件','上传文件','POST','/files/upload','[{\"type\":\"MultipartFile\",\"size\":19200,\"originalFilename\":\"pdf_img_p309_2.png\"},{\"accessLevel\":1,\"fileType\":\"document\"}]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.9',1020,1,NULL,'2026-06-22 23:36:19','2026-06-22 23:36:19',NULL,NULL,0),(327471883895312384,'文件管理','上传文件','上传文件','POST','/files/upload','[{\"type\":\"MultipartFile\",\"size\":26688,\"originalFilename\":\"pdf_img_p310_0.png\"},{\"accessLevel\":1,\"fileType\":\"document\"}]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.9',1478,1,NULL,'2026-06-22 23:36:21','2026-06-22 23:36:21',NULL,NULL,0),(327471885157797888,'文件管理','上传文件','上传文件','POST','/files/upload','[{\"type\":\"MultipartFile\",\"size\":19635,\"originalFilename\":\"pdf_img_p284_2.png\"},{\"accessLevel\":1,\"fileType\":\"document\"}]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.9',268,0,'Expected one result (or null) to be returned by selectOne(), but found: 3','2026-06-22 23:36:21','2026-06-22 23:36:21',NULL,NULL,0),(327471891642191872,'文件管理','上传文件','上传文件','POST','/files/upload','[{\"type\":\"MultipartFile\",\"size\":19496,\"originalFilename\":\"pdf_img_p310_1.png\"},{\"accessLevel\":1,\"fileType\":\"document\"}]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.9',1685,1,NULL,'2026-06-22 23:36:23','2026-06-22 23:36:23',NULL,NULL,0),(327471893743538176,'文件管理','上传文件','上传文件','POST','/files/upload','[{\"type\":\"MultipartFile\",\"size\":17201,\"originalFilename\":\"pdf_img_p310_2.png\"},{\"accessLevel\":1,\"fileType\":\"document\"}]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.9',463,1,NULL,'2026-06-22 23:36:23','2026-06-22 23:36:23',NULL,NULL,0),(327471897971396608,'文件管理','上传文件','上传文件','POST','/files/upload','[{\"type\":\"MultipartFile\",\"size\":43103,\"originalFilename\":\"pdf_img_p310_3.png\"},{\"accessLevel\":1,\"fileType\":\"document\"}]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.9',1038,1,NULL,'2026-06-22 23:36:24','2026-06-22 23:36:24',NULL,NULL,0),(327471901070987264,'文件管理','上传文件','上传文件','POST','/files/upload','[{\"type\":\"MultipartFile\",\"size\":14998,\"originalFilename\":\"pdf_img_p311_0.png\"},{\"accessLevel\":1,\"fileType\":\"document\"}]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.9',797,1,NULL,'2026-06-22 23:36:25','2026-06-22 23:36:25',NULL,NULL,0),(327471906347421696,'文件管理','上传文件','上传文件','POST','/files/upload','[{\"type\":\"MultipartFile\",\"size\":19021,\"originalFilename\":\"pdf_img_p311_1.png\"},{\"accessLevel\":1,\"fileType\":\"document\"}]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.9',1309,1,NULL,'2026-06-22 23:36:26','2026-06-22 23:36:26',NULL,NULL,0),(327471908557819904,'文件管理','上传文件','上传文件','POST','/files/upload','[{\"type\":\"MultipartFile\",\"size\":16191,\"originalFilename\":\"pdf_img_p287_1.png\"},{\"accessLevel\":1,\"fileType\":\"document\"}]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.9',491,0,'Expected one result (or null) to be returned by selectOne(), but found: 3','2026-06-22 23:36:27','2026-06-22 23:36:27',NULL,NULL,0),(327471911971983360,'文件管理','上传文件','上传文件','POST','/files/upload','[{\"type\":\"MultipartFile\",\"size\":33771,\"originalFilename\":\"pdf_img_p288_0.png\"},{\"accessLevel\":1,\"fileType\":\"document\"}]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.9',265,0,'Expected one result (or null) to be returned by selectOne(), but found: 3','2026-06-22 23:36:27','2026-06-22 23:36:27',NULL,NULL,0),(327471916447305728,'文件管理','上传文件','上传文件','POST','/files/upload','[{\"type\":\"MultipartFile\",\"size\":25402,\"originalFilename\":\"pdf_img_p312_0.png\"},{\"accessLevel\":1,\"fileType\":\"document\"}]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.9',1275,1,NULL,'2026-06-22 23:36:29','2026-06-22 23:36:29',NULL,NULL,0),(327471919194574848,'文件管理','上传文件','上传文件','POST','/files/upload','[{\"type\":\"MultipartFile\",\"size\":18243,\"originalFilename\":\"pdf_img_p289_1.png\"},{\"accessLevel\":1,\"fileType\":\"document\"}]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.9',553,1,NULL,'2026-06-22 23:36:29','2026-06-22 23:36:29',NULL,NULL,0),(327471925829963776,'文件管理','上传文件','上传文件','POST','/files/upload','[{\"type\":\"MultipartFile\",\"size\":29741,\"originalFilename\":\"pdf_img_p313_0.png\"},{\"accessLevel\":1,\"fileType\":\"document\"}]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.9',1641,1,NULL,'2026-06-22 23:36:31','2026-06-22 23:36:31',NULL,NULL,0),(327471927851618304,'文件管理','上传文件','上传文件','POST','/files/upload','[{\"type\":\"MultipartFile\",\"size\":23051,\"originalFilename\":\"pdf_img_p290_2.png\"},{\"accessLevel\":1,\"fileType\":\"document\"}]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.9',226,0,'Expected one result (or null) to be returned by selectOne(), but found: 3','2026-06-22 23:36:31','2026-06-22 23:36:31',NULL,NULL,0),(327471932419215360,'文件管理','上传文件','上传文件','POST','/files/upload','[{\"type\":\"MultipartFile\",\"size\":22605,\"originalFilename\":\"pdf_img_p291_0.png\"},{\"accessLevel\":1,\"fileType\":\"document\"}]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.9',1022,0,'Expected one result (or null) to be returned by selectOne(), but found: 3','2026-06-22 23:36:32','2026-06-22 23:36:32',NULL,NULL,0),(327471938815528960,'文件管理','上传文件','上传文件','POST','/files/upload','[{\"type\":\"MultipartFile\",\"size\":25404,\"originalFilename\":\"pdf_img_p314_0.png\"},{\"accessLevel\":1,\"fileType\":\"document\"}]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.9',1622,1,NULL,'2026-06-22 23:36:34','2026-06-22 23:36:34',NULL,NULL,0),(327471942468767744,'文件管理','上传文件','上传文件','POST','/files/upload','[{\"type\":\"MultipartFile\",\"size\":39437,\"originalFilename\":\"pdf_img_p314_1.png\"},{\"accessLevel\":1,\"fileType\":\"document\"}]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.9',910,1,NULL,'2026-06-22 23:36:35','2026-06-22 23:36:35',NULL,NULL,0),(327471944670777344,'文件管理','上传文件','上传文件','POST','/files/upload','[{\"type\":\"MultipartFile\",\"size\":22698,\"originalFilename\":\"pdf_img_p292_2.png\"},{\"accessLevel\":1,\"fileType\":\"document\"}]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.9',396,0,'Expected one result (or null) to be returned by selectOne(), but found: 3','2026-06-22 23:36:35','2026-06-22 23:36:35',NULL,NULL,0),(327471952182775808,'文件管理','上传文件','上传文件','POST','/files/upload','[{\"type\":\"MultipartFile\",\"size\":58850,\"originalFilename\":\"pdf_img_p315_0.png\"},{\"accessLevel\":1,\"fileType\":\"document\"}]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.9',1801,1,NULL,'2026-06-22 23:36:37','2026-06-22 23:36:37',NULL,NULL,0),(327471957052362752,'文件管理','上传文件','上传文件','POST','/files/upload','[{\"type\":\"MultipartFile\",\"size\":19938,\"originalFilename\":\"pdf_img_p294_0.png\"},{\"accessLevel\":1,\"fileType\":\"document\"}]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.9',1079,0,'Expected one result (or null) to be returned by selectOne(), but found: 3','2026-06-22 23:36:38','2026-06-22 23:36:38',NULL,NULL,0),(327471961368301568,'文件管理','上传文件','上传文件','POST','/files/upload','[{\"type\":\"MultipartFile\",\"size\":15841,\"originalFilename\":\"pdf_img_p315_1.png\"},{\"accessLevel\":1,\"fileType\":\"document\"}]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.9',1999,1,NULL,'2026-06-22 23:36:39','2026-06-22 23:36:39',NULL,NULL,0),(327471965554216960,'文件管理','上传文件','上传文件','POST','/files/upload','[{\"type\":\"MultipartFile\",\"size\":22243,\"originalFilename\":\"pdf_img_p315_2.png\"},{\"accessLevel\":1,\"fileType\":\"document\"}]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.9',995,0,'Expected one result (or null) to be returned by selectOne(), but found: 2','2026-06-22 23:36:40','2026-06-22 23:36:40',NULL,NULL,0),(327471970063093760,'文件管理','上传文件','上传文件','POST','/files/upload','[{\"type\":\"MultipartFile\",\"size\":30345,\"originalFilename\":\"pdf_img_p316_0.png\"},{\"accessLevel\":1,\"fileType\":\"document\"}]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.9',1072,1,NULL,'2026-06-22 23:36:41','2026-06-22 23:36:41',NULL,NULL,0),(327471978736914432,'文件管理','上传文件','上传文件','POST','/files/upload','[{\"type\":\"MultipartFile\",\"size\":24296,\"originalFilename\":\"pdf_img_p316_1.png\"},{\"accessLevel\":1,\"fileType\":\"document\"}]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.9',2077,1,NULL,'2026-06-22 23:36:43','2026-06-22 23:36:43',NULL,NULL,0),(327471981668732928,'文件管理','上传文件','上传文件','POST','/files/upload','[{\"type\":\"MultipartFile\",\"size\":23850,\"originalFilename\":\"pdf_img_p297_0.png\"},{\"accessLevel\":1,\"fileType\":\"document\"}]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.9',660,0,'Expected one result (or null) to be returned by selectOne(), but found: 3','2026-06-22 23:36:44','2026-06-22 23:36:44',NULL,NULL,0),(327471985259057152,'文件管理','上传文件','上传文件','POST','/files/upload','[{\"type\":\"MultipartFile\",\"size\":21875,\"originalFilename\":\"pdf_img_p316_2.png\"},{\"accessLevel\":1,\"fileType\":\"document\"}]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.9',1003,1,NULL,'2026-06-22 23:36:45','2026-06-22 23:36:45',NULL,NULL,0),(327471987079385088,'文件管理','上传文件','上传文件','POST','/files/upload','[{\"type\":\"MultipartFile\",\"size\":19629,\"originalFilename\":\"pdf_img_p298_1.png\"},{\"accessLevel\":1,\"fileType\":\"document\"}]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.9',363,1,NULL,'2026-06-22 23:36:45','2026-06-22 23:36:45',NULL,NULL,0),(327471998953459712,'文件管理','上传文件','上传文件','POST','/files/upload','[{\"type\":\"MultipartFile\",\"size\":15139,\"originalFilename\":\"pdf_img_p299_0.png\"},{\"accessLevel\":1,\"fileType\":\"document\"}]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.9',973,0,'Expected one result (or null) to be returned by selectOne(), but found: 3','2026-06-22 23:36:48','2026-06-22 23:36:48',NULL,NULL,0),(327472001671368704,'文件管理','上传文件','上传文件','POST','/files/upload','[{\"type\":\"MultipartFile\",\"size\":28924,\"originalFilename\":\"pdf_img_p299_1.png\"},{\"accessLevel\":1,\"fileType\":\"document\"}]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.9',602,0,'Expected one result (or null) to be returned by selectOne(), but found: 3','2026-06-22 23:36:49','2026-06-22 23:36:49',NULL,NULL,0),(327472007589531648,'文件管理','上传文件','上传文件','POST','/files/upload','[{\"type\":\"MultipartFile\",\"size\":36944,\"originalFilename\":\"pdf_img_p300_0.png\"},{\"accessLevel\":1,\"fileType\":\"document\"}]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.9',699,0,'Expected one result (or null) to be returned by selectOne(), but found: 3','2026-06-22 23:36:50','2026-06-22 23:36:50',NULL,NULL,0),(327472010395521024,'文件管理','上传文件','上传文件','POST','/files/upload','[{\"type\":\"MultipartFile\",\"size\":14578,\"originalFilename\":\"pdf_img_p300_1.png\"},{\"accessLevel\":1,\"fileType\":\"document\"}]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.9',779,0,'Expected one result (or null) to be returned by selectOne(), but found: 3','2026-06-22 23:36:51','2026-06-22 23:36:51',NULL,NULL,0),(327472015458045952,'文件管理','上传文件','上传文件','POST','/files/upload','[{\"type\":\"MultipartFile\",\"size\":31261,\"originalFilename\":\"pdf_img_p317_3.png\"},{\"accessLevel\":1,\"fileType\":\"document\"}]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.9',1844,1,NULL,'2026-06-22 23:36:52','2026-06-22 23:36:52',NULL,NULL,0),(327472020252135424,'文件管理','上传文件','上传文件','POST','/files/upload','[{\"type\":\"MultipartFile\",\"size\":46584,\"originalFilename\":\"pdf_img_p301_1.png\"},{\"accessLevel\":1,\"fileType\":\"document\"}]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.9',970,0,'Expected one result (or null) to be returned by selectOne(), but found: 2','2026-06-22 23:36:53','2026-06-22 23:36:53',NULL,NULL,0),(327472028632354816,'文件管理','上传文件','上传文件','POST','/files/upload','[{\"type\":\"MultipartFile\",\"size\":26883,\"originalFilename\":\"pdf_img_p318_1.png\"},{\"accessLevel\":1,\"fileType\":\"document\"}]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.9',1999,1,NULL,'2026-06-22 23:36:55','2026-06-22 23:36:55',NULL,NULL,0),(327472032486920192,'文件管理','上传文件','上传文件','POST','/files/upload','[{\"type\":\"MultipartFile\",\"size\":36938,\"originalFilename\":\"pdf_img_p318_2.png\"},{\"accessLevel\":1,\"fileType\":\"document\"}]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.9',920,1,NULL,'2026-06-22 23:36:56','2026-06-22 23:36:56',NULL,NULL,0),(327472037721411584,'文件管理','上传文件','上传文件','POST','/files/upload','[{\"type\":\"MultipartFile\",\"size\":21208,\"originalFilename\":\"pdf_img_p303_0.png\"},{\"accessLevel\":1,\"fileType\":\"document\"}]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.9',1057,0,'Expected one result (or null) to be returned by selectOne(), but found: 3','2026-06-22 23:36:57','2026-06-22 23:36:57',NULL,NULL,0),(327472040229605376,'文件管理','上传文件','上传文件','POST','/files/upload','[{\"type\":\"MultipartFile\",\"size\":37153,\"originalFilename\":\"pdf_img_p303_1.png\"},{\"accessLevel\":1,\"fileType\":\"document\"}]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.9',606,0,'Expected one result (or null) to be returned by selectOne(), but found: 3','2026-06-22 23:36:58','2026-06-22 23:36:58',NULL,NULL,0),(327472044356800512,'文件管理','上传文件','上传文件','POST','/files/upload','[{\"type\":\"MultipartFile\",\"size\":31248,\"originalFilename\":\"pdf_img_p319_1.png\"},{\"accessLevel\":1,\"fileType\":\"document\"}]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.9',1072,1,NULL,'2026-06-22 23:36:59','2026-06-22 23:36:59',NULL,NULL,0),(327472046294568960,'文件管理','上传文件','上传文件','POST','/files/upload','[{\"type\":\"MultipartFile\",\"size\":18021,\"originalFilename\":\"pdf_img_p319_2.png\"},{\"accessLevel\":1,\"fileType\":\"document\"}]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.9',547,1,NULL,'2026-06-22 23:36:59','2026-06-22 23:36:59',NULL,NULL,0),(327472053785595904,'文件管理','上传文件','上传文件','POST','/files/upload','[{\"type\":\"MultipartFile\",\"size\":28930,\"originalFilename\":\"pdf_img_p320_0.png\"},{\"accessLevel\":1,\"fileType\":\"document\"}]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.9',1914,1,NULL,'2026-06-22 23:37:01','2026-06-22 23:37:01',NULL,NULL,0),(327472056981655552,'文件管理','上传文件','上传文件','POST','/files/upload','[{\"type\":\"MultipartFile\",\"size\":21651,\"originalFilename\":\"pdf_img_p306_1.png\"},{\"accessLevel\":1,\"fileType\":\"document\"}]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.9',610,0,'Expected one result (or null) to be returned by selectOne(), but found: 2','2026-06-22 23:37:02','2026-06-22 23:37:02',NULL,NULL,0),(327472058596462592,'文件管理','上传文件','上传文件','POST','/files/upload','[{\"type\":\"MultipartFile\",\"size\":28420,\"originalFilename\":\"pdf_img_p306_2.png\"},{\"accessLevel\":1,\"fileType\":\"document\"}]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.9',339,0,'Expected one result (or null) to be returned by selectOne(), but found: 3','2026-06-22 23:37:02','2026-06-22 23:37:02',NULL,NULL,0),(327472064892112896,'文件管理','上传文件','上传文件','POST','/files/upload','[{\"type\":\"MultipartFile\",\"size\":35459,\"originalFilename\":\"pdf_img_p320_2.png\"},{\"accessLevel\":1,\"fileType\":\"document\"}]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.9',1653,1,NULL,'2026-06-22 23:37:04','2026-06-22 23:37:04',NULL,NULL,0),(327472067337392128,'文件管理','上传文件','上传文件','POST','/files/upload','[{\"type\":\"MultipartFile\",\"size\":24781,\"originalFilename\":\"pdf_img_p321_0.png\"},{\"accessLevel\":1,\"fileType\":\"document\"}]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.9',536,1,NULL,'2026-06-22 23:37:04','2026-06-22 23:37:04',NULL,NULL,0),(327472071204540416,'文件管理','上传文件','上传文件','POST','/files/upload','[{\"type\":\"MultipartFile\",\"size\":18792,\"originalFilename\":\"pdf_img_p307_3.png\"},{\"accessLevel\":1,\"fileType\":\"document\"}]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.9',272,0,'Expected one result (or null) to be returned by selectOne(), but found: 3','2026-06-22 23:37:05','2026-06-22 23:37:05',NULL,NULL,0),(327472077869289472,'文件管理','上传文件','上传文件','POST','/files/upload','[{\"type\":\"MultipartFile\",\"size\":22556,\"originalFilename\":\"pdf_img_p322_0.png\"},{\"accessLevel\":1,\"fileType\":\"document\"}]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.9',1620,1,NULL,'2026-06-22 23:37:07','2026-06-22 23:37:07',NULL,NULL,0),(327472079882555392,'文件管理','上传文件','上传文件','POST','/files/upload','[{\"type\":\"MultipartFile\",\"size\":7412,\"originalFilename\":\"pdf_img_p309_0.png\"},{\"accessLevel\":1,\"fileType\":\"document\"}]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.9',296,1,NULL,'2026-06-22 23:37:07','2026-06-22 23:37:07',NULL,NULL,0),(327472084034916352,'文件管理','上传文件','上传文件','POST','/files/upload','[{\"type\":\"MultipartFile\",\"size\":11842,\"originalFilename\":\"pdf_img_p322_1.png\"},{\"accessLevel\":1,\"fileType\":\"document\"}]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.9',1177,1,NULL,'2026-06-22 23:37:08','2026-06-22 23:37:08',NULL,NULL,0),(327472091853099008,'文件管理','上传文件','上传文件','POST','/files/upload','[{\"type\":\"MultipartFile\",\"size\":26688,\"originalFilename\":\"pdf_img_p310_0.png\"},{\"accessLevel\":1,\"fileType\":\"document\"}]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.9',984,0,'Expected one result (or null) to be returned by selectOne(), but found: 3','2026-06-22 23:37:10','2026-06-22 23:37:10',NULL,NULL,0),(327472095699275776,'文件管理','上传文件','上传文件','POST','/files/upload','[{\"type\":\"MultipartFile\",\"size\":16891,\"originalFilename\":\"pdf_img_p323_0.png\"},{\"accessLevel\":1,\"fileType\":\"document\"}]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.9',940,1,NULL,'2026-06-22 23:37:11','2026-06-22 23:37:11',NULL,NULL,0),(327472100052963328,'文件管理','上传文件','上传文件','POST','/files/upload','[{\"type\":\"MultipartFile\",\"size\":14998,\"originalFilename\":\"pdf_img_p311_0.png\"},{\"accessLevel\":1,\"fileType\":\"document\"}]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.9',842,0,'Expected one result (or null) to be returned by selectOne(), but found: 3','2026-06-22 23:37:12','2026-06-22 23:37:12',NULL,NULL,0),(327472104578617344,'文件管理','上传文件','上传文件','POST','/files/upload','[{\"type\":\"MultipartFile\",\"size\":36148,\"originalFilename\":\"pdf_img_p323_1.png\"},{\"accessLevel\":1,\"fileType\":\"document\"}]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.9',1891,1,NULL,'2026-06-22 23:37:13','2026-06-22 23:37:13',NULL,NULL,0),(327472112937865216,'文件管理','上传文件','上传文件','POST','/files/upload','[{\"type\":\"MultipartFile\",\"size\":25648,\"originalFilename\":\"pdf_img_p323_2.png\"},{\"accessLevel\":1,\"fileType\":\"document\"}]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.9',1911,1,NULL,'2026-06-22 23:37:15','2026-06-22 23:37:15',NULL,NULL,0),(327472116763070464,'文件管理','上传文件','上传文件','POST','/files/upload','[{\"type\":\"MultipartFile\",\"size\":27288,\"originalFilename\":\"pdf_img_p312_1.png\"},{\"accessLevel\":1,\"fileType\":\"document\"}]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.9',751,0,'Expected one result (or null) to be returned by selectOne(), but found: 3','2026-06-22 23:37:16','2026-06-22 23:37:16',NULL,NULL,0),(327472125067792384,'文件管理','上传文件','上传文件','POST','/files/upload','[{\"type\":\"MultipartFile\",\"size\":37807,\"originalFilename\":\"pdf_img_p313_1.png\"},{\"accessLevel\":1,\"fileType\":\"document\"}]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.9',1615,0,'Expected one result (or null) to be returned by selectOne(), but found: 3','2026-06-22 23:37:18','2026-06-22 23:37:18',NULL,NULL,0),(327472129371148288,'文件管理','上传文件','上传文件','POST','/files/upload','[{\"type\":\"MultipartFile\",\"size\":39437,\"originalFilename\":\"pdf_img_p314_1.png\"},{\"accessLevel\":1,\"fileType\":\"document\"}]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.9',275,0,'Expected one result (or null) to be returned by selectOne(), but found: 3','2026-06-22 23:37:19','2026-06-22 23:37:19',NULL,NULL,0),(327472132781117440,'文件管理','上传文件','上传文件','POST','/files/upload','[{\"type\":\"MultipartFile\",\"size\":25288,\"originalFilename\":\"pdf_img_p314_2.png\"},{\"accessLevel\":1,\"fileType\":\"document\"}]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.9',764,0,'Expected one result (or null) to be returned by selectOne(), but found: 3','2026-06-22 23:37:20','2026-06-22 23:37:20',NULL,NULL,0),(327472138590228480,'文件管理','上传文件','上传文件','POST','/files/upload','[{\"type\":\"MultipartFile\",\"size\":14965,\"originalFilename\":\"pdf_img_p324_3.png\"},{\"accessLevel\":1,\"fileType\":\"document\"}]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.9',1423,1,NULL,'2026-06-22 23:37:21','2026-06-22 23:37:21',NULL,NULL,0),(327472143375929344,'文件管理','上传文件','上传文件','POST','/files/upload','[{\"type\":\"MultipartFile\",\"size\":29686,\"originalFilename\":\"pdf_img_p325_0.png\"},{\"accessLevel\":1,\"fileType\":\"document\"}]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.9',1119,1,NULL,'2026-06-22 23:37:23','2026-06-22 23:37:23',NULL,NULL,0),(327472150606909440,'文件管理','上传文件','上传文件','POST','/files/upload','[{\"type\":\"MultipartFile\",\"size\":12868,\"originalFilename\":\"pdf_img_p325_1.png\"},{\"accessLevel\":1,\"fileType\":\"document\"}]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.9',1651,1,NULL,'2026-06-22 23:37:24','2026-06-22 23:37:24',NULL,NULL,0),(327472154243371008,'文件管理','上传文件','上传文件','POST','/files/upload','[{\"type\":\"MultipartFile\",\"size\":23722,\"originalFilename\":\"pdf_img_p325_2.png\"},{\"accessLevel\":1,\"fileType\":\"document\"}]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.9',986,1,NULL,'2026-06-22 23:37:25','2026-06-22 23:37:25',NULL,NULL,0),(327472156076281856,'文件管理','上传文件','上传文件','POST','/files/upload','[{\"type\":\"MultipartFile\",\"size\":27140,\"originalFilename\":\"pdf_img_p317_0.png\"},{\"accessLevel\":1,\"fileType\":\"document\"}]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.9',300,0,'Expected one result (or null) to be returned by selectOne(), but found: 3','2026-06-22 23:37:26','2026-06-22 23:37:26',NULL,NULL,0),(327472158605447168,'文件管理','上传文件','上传文件','POST','/files/upload','[{\"type\":\"MultipartFile\",\"size\":22698,\"originalFilename\":\"pdf_img_p326_0.png\"},{\"accessLevel\":1,\"fileType\":\"document\"}]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.9',885,1,NULL,'2026-06-22 23:37:26','2026-06-22 23:37:26',NULL,NULL,0),(327472164259368960,'文件管理','上传文件','上传文件','POST','/files/upload','[{\"type\":\"MultipartFile\",\"size\":31261,\"originalFilename\":\"pdf_img_p317_3.png\"},{\"accessLevel\":1,\"fileType\":\"document\"}]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.9',1033,0,'Expected one result (or null) to be returned by selectOne(), but found: 3','2026-06-22 23:37:28','2026-06-22 23:37:28',NULL,NULL,0),(327472171087695872,'文件管理','上传文件','上传文件','POST','/files/upload','[{\"type\":\"MultipartFile\",\"size\":28166,\"originalFilename\":\"pdf_img_p326_2.png\"},{\"accessLevel\":1,\"fileType\":\"document\"}]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.9',1758,1,NULL,'2026-06-22 23:37:29','2026-06-22 23:37:29',NULL,NULL,0),(327472174547996672,'文件管理','上传文件','上传文件','POST','/files/upload','[{\"type\":\"MultipartFile\",\"size\":36938,\"originalFilename\":\"pdf_img_p318_2.png\"},{\"accessLevel\":1,\"fileType\":\"document\"}]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.9',651,0,'Expected one result (or null) to be returned by selectOne(), but found: 3','2026-06-22 23:37:30','2026-06-22 23:37:30',NULL,NULL,0),(327472177165242368,'文件管理','上传文件','上传文件','POST','/files/upload','[{\"type\":\"MultipartFile\",\"size\":51167,\"originalFilename\":\"pdf_img_p327_0.png\"},{\"accessLevel\":1,\"fileType\":\"document\"}]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.9',622,1,NULL,'2026-06-22 23:37:31','2026-06-22 23:37:31',NULL,NULL,0),(327472184207478784,'文件管理','上传文件','上传文件','POST','/files/upload','[{\"type\":\"MultipartFile\",\"size\":51167,\"originalFilename\":\"pdf_img_p327_0.png\"},{\"accessLevel\":1,\"fileType\":\"document\"}]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.9',2005,1,NULL,'2026-06-22 23:37:32','2026-06-22 23:37:32',NULL,NULL,0),(327472187990740992,'文件管理','上传文件','上传文件','POST','/files/upload','[{\"type\":\"MultipartFile\",\"size\":19477,\"originalFilename\":\"pdf_img_p320_1.png\"},{\"accessLevel\":1,\"fileType\":\"document\"}]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.9',598,0,'Expected one result (or null) to be returned by selectOne(), but found: 3','2026-06-22 23:37:33','2026-06-22 23:37:33',NULL,NULL,0),(327472193556582400,'文件管理','上传文件','上传文件','POST','/files/upload','[{\"type\":\"MultipartFile\",\"size\":32550,\"originalFilename\":\"pdf_img_p327_2.png\"},{\"accessLevel\":1,\"fileType\":\"document\"}]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.9',1209,1,NULL,'2026-06-22 23:37:35','2026-06-22 23:37:35',NULL,NULL,0),(327472197692166144,'文件管理','上传文件','上传文件','POST','/files/upload','[{\"type\":\"MultipartFile\",\"size\":22556,\"originalFilename\":\"pdf_img_p322_0.png\"},{\"accessLevel\":1,\"fileType\":\"document\"}]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.9',265,0,'Expected one result (or null) to be returned by selectOne(), but found: 3','2026-06-22 23:37:36','2026-06-22 23:37:36',NULL,NULL,0),(327472202293317632,'文件管理','上传文件','上传文件','POST','/files/upload','[{\"type\":\"MultipartFile\",\"size\":11842,\"originalFilename\":\"pdf_img_p322_1.png\"},{\"accessLevel\":1,\"fileType\":\"document\"}]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.9',1057,0,'Expected one result (or null) to be returned by selectOne(), but found: 3','2026-06-22 23:37:37','2026-06-22 23:37:37',NULL,NULL,0),(327472208601550848,'文件管理','上传文件','上传文件','POST','/files/upload','[{\"type\":\"MultipartFile\",\"size\":33847,\"originalFilename\":\"pdf_img_p328_1.png\"},{\"accessLevel\":1,\"fileType\":\"document\"}]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.9',1659,1,NULL,'2026-06-22 23:37:38','2026-06-22 23:37:38',NULL,NULL,0),(327472213332725760,'文件管理','上传文件','上传文件','POST','/files/upload','[{\"type\":\"MultipartFile\",\"size\":21388,\"originalFilename\":\"pdf_img_p328_2.png\"},{\"accessLevel\":1,\"fileType\":\"document\"}]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.9',1562,1,NULL,'2026-06-22 23:37:39','2026-06-22 23:37:39',NULL,NULL,0),(327472217317314560,'文件管理','上传文件','上传文件','POST','/files/upload','[{\"type\":\"MultipartFile\",\"size\":65857,\"originalFilename\":\"pdf_img_p324_1.png\"},{\"accessLevel\":1,\"fileType\":\"document\"}]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.9',642,0,'Expected one result (or null) to be returned by selectOne(), but found: 3','2026-06-22 23:37:40','2026-06-22 23:37:40',NULL,NULL,0),(327472222178512896,'文件管理','上传文件','上传文件','POST','/files/upload','[{\"type\":\"MultipartFile\",\"size\":18054,\"originalFilename\":\"pdf_img_p329_0.png\"},{\"accessLevel\":1,\"fileType\":\"document\"}]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.9',1665,1,NULL,'2026-06-22 23:37:41','2026-06-22 23:37:41',NULL,NULL,0),(327472227773714432,'文件管理','上传文件','上传文件','POST','/files/upload','[{\"type\":\"MultipartFile\",\"size\":12868,\"originalFilename\":\"pdf_img_p325_1.png\"},{\"accessLevel\":1,\"fileType\":\"document\"}]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.9',1052,0,'Expected one result (or null) to be returned by selectOne(), but found: 3','2026-06-22 23:37:43','2026-06-22 23:37:43',NULL,NULL,0),(327472231825412096,'文件管理','上传文件','上传文件','POST','/files/upload','[{\"type\":\"MultipartFile\",\"size\":27310,\"originalFilename\":\"pdf_img_p329_2.png\"},{\"accessLevel\":1,\"fileType\":\"document\"}]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.9',1100,1,NULL,'2026-06-22 23:37:44','2026-06-22 23:37:44',NULL,NULL,0),(327472238951534592,'文件管理','上传文件','上传文件','POST','/files/upload','[{\"type\":\"MultipartFile\",\"size\":24583,\"originalFilename\":\"pdf_img_p326_1.png\"},{\"accessLevel\":1,\"fileType\":\"document\"}]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.9',844,0,'Expected one result (or null) to be returned by selectOne(), but found: 3','2026-06-22 23:37:45','2026-06-22 23:37:45',NULL,NULL,0),(327472242466361344,'文件管理','上传文件','上传文件','POST','/files/upload','[{\"type\":\"MultipartFile\",\"size\":28166,\"originalFilename\":\"pdf_img_p326_2.png\"},{\"accessLevel\":1,\"fileType\":\"document\"}]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.9',684,0,'Expected one result (or null) to be returned by selectOne(), but found: 3','2026-06-22 23:37:46','2026-06-22 23:37:46',NULL,NULL,0),(327472248422273024,'文件管理','上传文件','上传文件','POST','/files/upload','[{\"type\":\"MultipartFile\",\"size\":19143,\"originalFilename\":\"pdf_img_p330_1.png\"},{\"accessLevel\":1,\"fileType\":\"document\"}]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.9',1881,1,NULL,'2026-06-22 23:37:48','2026-06-22 23:37:48',NULL,NULL,0),(327472252490747904,'文件管理','上传文件','上传文件','POST','/files/upload','[{\"type\":\"MultipartFile\",\"size\":24245,\"originalFilename\":\"pdf_img_p330_2.png\"},{\"accessLevel\":1,\"fileType\":\"document\"}]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.9',1223,1,NULL,'2026-06-22 23:37:49','2026-06-22 23:37:49',NULL,NULL,0),(327472256739577856,'文件管理','上传文件','上传文件','POST','/files/upload','[{\"type\":\"MultipartFile\",\"size\":28450,\"originalFilename\":\"pdf_img_p328_0.png\"},{\"accessLevel\":1,\"fileType\":\"document\"}]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.9',260,0,'Expected one result (or null) to be returned by selectOne(), but found: 3','2026-06-22 23:37:50','2026-06-22 23:37:50',NULL,NULL,0),(327472259746893824,'文件管理','上传文件','上传文件','POST','/files/upload','[{\"type\":\"MultipartFile\",\"size\":24776,\"originalFilename\":\"pdf_img_p331_1.png\"},{\"accessLevel\":1,\"fileType\":\"document\"}]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.9',893,1,NULL,'2026-06-22 23:37:50','2026-06-22 23:37:50',NULL,NULL,0),(327472265727971328,'文件管理','上传文件','上传文件','POST','/files/upload','[{\"type\":\"MultipartFile\",\"size\":18054,\"originalFilename\":\"pdf_img_p329_0.png\"},{\"accessLevel\":1,\"fileType\":\"document\"}]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.9',1133,0,'Expected one result (or null) to be returned by selectOne(), but found: 3','2026-06-22 23:37:52','2026-06-22 23:37:52',NULL,NULL,0),(327472272321417216,'文件管理','上传文件','上传文件','POST','/files/upload','[{\"type\":\"MultipartFile\",\"size\":23023,\"originalFilename\":\"pdf_img_p331_2.png\"},{\"accessLevel\":1,\"fileType\":\"document\"}]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.9',2713,1,NULL,'2026-06-22 23:37:53','2026-06-22 23:37:53',NULL,NULL,0),(327472276180176896,'文件管理','上传文件','上传文件','POST','/files/upload','[{\"type\":\"MultipartFile\",\"size\":17525,\"originalFilename\":\"pdf_img_p331_3.png\"},{\"accessLevel\":1,\"fileType\":\"document\"}]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.9',890,1,NULL,'2026-06-22 23:37:54','2026-06-22 23:37:54',NULL,NULL,0),(327472280085073920,'文件管理','上传文件','上传文件','POST','/files/upload','[{\"type\":\"MultipartFile\",\"size\":24245,\"originalFilename\":\"pdf_img_p330_2.png\"},{\"accessLevel\":1,\"fileType\":\"document\"}]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.9',541,1,NULL,'2026-06-22 23:37:55','2026-06-22 23:37:55',NULL,NULL,0),(327472285546057728,'文件管理','上传文件','上传文件','POST','/files/upload','[{\"type\":\"MultipartFile\",\"size\":14255,\"originalFilename\":\"pdf_img_p332_1.png\"},{\"accessLevel\":1,\"fileType\":\"document\"}]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.9',1241,1,NULL,'2026-06-22 23:37:57','2026-06-22 23:37:57',NULL,NULL,0),(327472290809909248,'文件管理','上传文件','上传文件','POST','/files/upload','[{\"type\":\"MultipartFile\",\"size\":23023,\"originalFilename\":\"pdf_img_p331_2.png\"},{\"accessLevel\":1,\"fileType\":\"document\"}]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.9',1109,0,'Expected one result (or null) to be returned by selectOne(), but found: 3','2026-06-22 23:37:58','2026-06-22 23:37:58',NULL,NULL,0),(327472297793425408,'文件管理','上传文件','上传文件','POST','/files/upload','[{\"type\":\"MultipartFile\",\"size\":17525,\"originalFilename\":\"pdf_img_p331_3.png\"},{\"accessLevel\":1,\"fileType\":\"document\"}]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.9',1602,0,'Expected one result (or null) to be returned by selectOne(), but found: 3','2026-06-22 23:37:59','2026-06-22 23:37:59',NULL,NULL,0),(327472301534744576,'文件管理','上传文件','上传文件','POST','/files/upload','[{\"type\":\"MultipartFile\",\"size\":17187,\"originalFilename\":\"pdf_img_p333_0.png\"},{\"accessLevel\":1,\"fileType\":\"document\"}]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.9',1464,1,NULL,'2026-06-22 23:38:00','2026-06-22 23:38:00',NULL,NULL,0),(327472306475634688,'文件管理','上传文件','上传文件','POST','/files/upload','[{\"type\":\"MultipartFile\",\"size\":26426,\"originalFilename\":\"pdf_img_p333_1.png\"},{\"accessLevel\":1,\"fileType\":\"document\"}]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.9',1197,1,NULL,'2026-06-22 23:38:02','2026-06-22 23:38:02',NULL,NULL,0),(327472310011432960,'文件管理','上传文件','上传文件','POST','/files/upload','[{\"type\":\"MultipartFile\",\"size\":21639,\"originalFilename\":\"pdf_img_p334_0.png\"},{\"accessLevel\":1,\"fileType\":\"document\"}]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.9',819,1,NULL,'2026-06-22 23:38:02','2026-06-22 23:38:02',NULL,NULL,0),(327472312590929920,'文件管理','上传文件','上传文件','POST','/files/upload','[{\"type\":\"MultipartFile\",\"size\":21033,\"originalFilename\":\"pdf_img_p334_1.png\"},{\"accessLevel\":1,\"fileType\":\"document\"}]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.9',616,1,NULL,'2026-06-22 23:38:03','2026-06-22 23:38:03',NULL,NULL,0),(327472318127411200,'文件管理','上传文件','上传文件','POST','/files/upload','[{\"type\":\"MultipartFile\",\"size\":13742,\"originalFilename\":\"pdf_img_p334_2.png\"},{\"accessLevel\":1,\"fileType\":\"document\"}]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.9',1299,1,NULL,'2026-06-22 23:38:04','2026-06-22 23:38:04',NULL,NULL,0),(327472321004703744,'文件管理','上传文件','上传文件','POST','/files/upload','[{\"type\":\"MultipartFile\",\"size\":16287,\"originalFilename\":\"pdf_img_p335_0.png\"},{\"accessLevel\":1,\"fileType\":\"document\"}]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.9',611,1,NULL,'2026-06-22 23:38:05','2026-06-22 23:38:05',NULL,NULL,0),(327472327984025600,'文件管理','上传文件','上传文件','POST','/files/upload','[{\"type\":\"MultipartFile\",\"size\":13463,\"originalFilename\":\"pdf_img_p335_1.png\"},{\"accessLevel\":1,\"fileType\":\"document\"}]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.9',1622,1,NULL,'2026-06-22 23:38:07','2026-06-22 23:38:07',NULL,NULL,0),(327472337173745664,'文件管理','上传文件','上传文件','POST','/files/upload','[{\"type\":\"MultipartFile\",\"size\":20425,\"originalFilename\":\"pdf_img_p336_0.png\"},{\"accessLevel\":1,\"fileType\":\"document\"}]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.9',2156,1,NULL,'2026-06-22 23:38:09','2026-06-22 23:38:09',NULL,NULL,0),(327472344530554880,'文件管理','上传文件','上传文件','POST','/files/upload','[{\"type\":\"MultipartFile\",\"size\":24501,\"originalFilename\":\"pdf_img_p336_1.png\"},{\"accessLevel\":1,\"fileType\":\"document\"}]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.9',1707,1,NULL,'2026-06-22 23:38:11','2026-06-22 23:38:11',NULL,NULL,0),(327472349114929152,'文件管理','上传文件','上传文件','POST','/files/upload','[{\"type\":\"MultipartFile\",\"size\":17266,\"originalFilename\":\"pdf_img_p337_0.png\"},{\"accessLevel\":1,\"fileType\":\"document\"}]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.9',1075,1,NULL,'2026-06-22 23:38:12','2026-06-22 23:38:12',NULL,NULL,0),(327472353871269888,'文件管理','上传文件','上传文件','POST','/files/upload','[{\"type\":\"MultipartFile\",\"size\":19637,\"originalFilename\":\"pdf_img_p337_1.png\"},{\"accessLevel\":1,\"fileType\":\"document\"}]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.9',1106,1,NULL,'2026-06-22 23:38:13','2026-06-22 23:38:13',NULL,NULL,0),(327472362314403840,'文件管理','上传文件','上传文件','POST','/files/upload','[{\"type\":\"MultipartFile\",\"size\":15828,\"originalFilename\":\"pdf_img_p337_2.png\"},{\"accessLevel\":1,\"fileType\":\"document\"}]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.9',2039,1,NULL,'2026-06-22 23:38:15','2026-06-22 23:38:15',NULL,NULL,0),(327472366546456576,'文件管理','上传文件','上传文件','POST','/files/upload','[{\"type\":\"MultipartFile\",\"size\":13561,\"originalFilename\":\"pdf_img_p338_0.png\"},{\"accessLevel\":1,\"fileType\":\"document\"}]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.9',962,1,NULL,'2026-06-22 23:38:16','2026-06-22 23:38:16',NULL,NULL,0),(327472372305235968,'文件管理','上传文件','上传文件','POST','/files/upload','[{\"type\":\"MultipartFile\",\"size\":34736,\"originalFilename\":\"pdf_img_p338_1.png\"},{\"accessLevel\":1,\"fileType\":\"document\"}]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.9',1141,1,NULL,'2026-06-22 23:38:17','2026-06-22 23:38:17',NULL,NULL,0),(327472377896243200,'文件管理','上传文件','上传文件','POST','/files/upload','[{\"type\":\"MultipartFile\",\"size\":53731,\"originalFilename\":\"pdf_img_p338_2.png\"},{\"accessLevel\":1,\"fileType\":\"document\"}]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.9',1410,1,NULL,'2026-06-22 23:38:19','2026-06-22 23:38:19',NULL,NULL,0),(327472383290118144,'文件管理','上传文件','上传文件','POST','/files/upload','[{\"type\":\"MultipartFile\",\"size\":20370,\"originalFilename\":\"pdf_img_p339_0.png\"},{\"accessLevel\":1,\"fileType\":\"document\"}]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.9',1378,1,NULL,'2026-06-22 23:38:20','2026-06-22 23:38:20',NULL,NULL,0),(327472388113567744,'文件管理','上传文件','上传文件','POST','/files/upload','[{\"type\":\"MultipartFile\",\"size\":44126,\"originalFilename\":\"pdf_img_p339_1.png\"},{\"accessLevel\":1,\"fileType\":\"document\"}]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.9',1068,1,NULL,'2026-06-22 23:38:21','2026-06-22 23:38:21',NULL,NULL,0),(327472395042557952,'文件管理','上传文件','上传文件','POST','/files/upload','[{\"type\":\"MultipartFile\",\"size\":44126,\"originalFilename\":\"pdf_img_p339_1.png\"},{\"accessLevel\":1,\"fileType\":\"document\"}]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.9',2560,1,NULL,'2026-06-22 23:38:23','2026-06-22 23:38:23',NULL,NULL,0),(327472399727595520,'文件管理','上传文件','上传文件','POST','/files/upload','[{\"type\":\"MultipartFile\",\"size\":40662,\"originalFilename\":\"pdf_img_p340_1.png\"},{\"accessLevel\":1,\"fileType\":\"document\"}]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.9',1044,1,NULL,'2026-06-22 23:38:24','2026-06-22 23:38:24',NULL,NULL,0),(327472404337135616,'文件管理','上传文件','上传文件','POST','/files/upload','[{\"type\":\"MultipartFile\",\"size\":30474,\"originalFilename\":\"pdf_img_p340_2.png\"},{\"accessLevel\":1,\"fileType\":\"document\"}]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.9',986,1,NULL,'2026-06-22 23:38:25','2026-06-22 23:38:25',NULL,NULL,0),(327472408544022528,'文件管理','上传文件','上传文件','POST','/files/upload','[{\"type\":\"MultipartFile\",\"size\":32801,\"originalFilename\":\"pdf_img_p340_3.png\"},{\"accessLevel\":1,\"fileType\":\"document\"}]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.9',1080,1,NULL,'2026-06-22 23:38:26','2026-06-22 23:38:26',NULL,NULL,0),(327472415254908928,'文件管理','上传文件','上传文件','POST','/files/upload','[{\"type\":\"MultipartFile\",\"size\":19753,\"originalFilename\":\"pdf_img_p341_0.png\"},{\"accessLevel\":1,\"fileType\":\"document\"}]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.9',1431,1,NULL,'2026-06-22 23:38:27','2026-06-22 23:38:27',NULL,NULL,0),(327472418211893248,'文件管理','上传文件','上传文件','POST','/files/upload','[{\"type\":\"MultipartFile\",\"size\":45087,\"originalFilename\":\"pdf_img_p341_1.png\"},{\"accessLevel\":1,\"fileType\":\"document\"}]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.9',646,1,NULL,'2026-06-22 23:38:28','2026-06-22 23:38:28',NULL,NULL,0),(327472425036025856,'文件管理','上传文件','上传文件','POST','/files/upload','[{\"type\":\"MultipartFile\",\"size\":25956,\"originalFilename\":\"pdf_img_p341_2.png\"},{\"accessLevel\":1,\"fileType\":\"document\"}]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.9',1662,1,NULL,'2026-06-22 23:38:30','2026-06-22 23:38:30',NULL,NULL,0),(327472433005203456,'文件管理','上传文件','上传文件','POST','/files/upload','[{\"type\":\"MultipartFile\",\"size\":25916,\"originalFilename\":\"pdf_img_p342_0.png\"},{\"accessLevel\":1,\"fileType\":\"document\"}]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.9',1840,1,NULL,'2026-06-22 23:38:32','2026-06-22 23:38:32',NULL,NULL,0),(327472437786710016,'文件管理','上传文件','上传文件','POST','/files/upload','[{\"type\":\"MultipartFile\",\"size\":22045,\"originalFilename\":\"pdf_img_p342_1.png\"},{\"accessLevel\":1,\"fileType\":\"document\"}]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.9',1132,1,NULL,'2026-06-22 23:38:33','2026-06-22 23:38:33',NULL,NULL,0),(327472442673074176,'文件管理','上传文件','上传文件','POST','/files/upload','[{\"type\":\"MultipartFile\",\"size\":30523,\"originalFilename\":\"pdf_img_p342_2.png\"},{\"accessLevel\":1,\"fileType\":\"document\"}]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.9',1102,1,NULL,'2026-06-22 23:38:34','2026-06-22 23:38:34',NULL,NULL,0),(327472446393421824,'文件管理','上传文件','上传文件','POST','/files/upload','[{\"type\":\"MultipartFile\",\"size\":30306,\"originalFilename\":\"pdf_img_p342_3.png\"},{\"accessLevel\":1,\"fileType\":\"document\"}]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.9',837,1,NULL,'2026-06-22 23:38:35','2026-06-22 23:38:35',NULL,NULL,0),(327472453209165824,'文件管理','上传文件','上传文件','POST','/files/upload','[{\"type\":\"MultipartFile\",\"size\":15782,\"originalFilename\":\"pdf_img_p343_0.png\"},{\"accessLevel\":1,\"fileType\":\"document\"}]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.9',1555,1,NULL,'2026-06-22 23:38:36','2026-06-22 23:38:36',NULL,NULL,0),(327472459710337024,'文件管理','上传文件','上传文件','POST','/files/upload','[{\"type\":\"MultipartFile\",\"size\":32980,\"originalFilename\":\"pdf_img_p343_1.png\"},{\"accessLevel\":1,\"fileType\":\"document\"}]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.9',1534,1,NULL,'2026-06-22 23:38:38','2026-06-22 23:38:38',NULL,NULL,0),(327472464441511936,'文件管理','上传文件','上传文件','POST','/files/upload','[{\"type\":\"MultipartFile\",\"size\":19323,\"originalFilename\":\"pdf_img_p343_2.png\"},{\"accessLevel\":1,\"fileType\":\"document\"}]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.9',1026,1,NULL,'2026-06-22 23:38:39','2026-06-22 23:38:39',NULL,NULL,0),(327472470162542592,'文件管理','上传文件','上传文件','POST','/files/upload','[{\"type\":\"MultipartFile\",\"size\":22247,\"originalFilename\":\"pdf_img_p343_3.png\"},{\"accessLevel\":1,\"fileType\":\"document\"}]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.9',779,1,NULL,'2026-06-22 23:38:41','2026-06-22 23:38:41',NULL,NULL,0),(327472475300564992,'文件管理','上传文件','上传文件','POST','/files/upload','[{\"type\":\"MultipartFile\",\"size\":15825,\"originalFilename\":\"pdf_img_p344_0.png\"},{\"accessLevel\":1,\"fileType\":\"document\"}]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.9',1183,1,NULL,'2026-06-22 23:38:42','2026-06-22 23:38:42',NULL,NULL,0),(327472479679418368,'文件管理','上传文件','上传文件','POST','/files/upload','[{\"type\":\"MultipartFile\",\"size\":26105,\"originalFilename\":\"pdf_img_p344_1.png\"},{\"accessLevel\":1,\"fileType\":\"document\"}]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.9',945,1,NULL,'2026-06-22 23:38:43','2026-06-22 23:38:43',NULL,NULL,0),(327472484536422400,'文件管理','上传文件','上传文件','POST','/files/upload','[{\"type\":\"MultipartFile\",\"size\":23466,\"originalFilename\":\"pdf_img_p344_2.png\"},{\"accessLevel\":1,\"fileType\":\"document\"}]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.9',1221,1,NULL,'2026-06-22 23:38:44','2026-06-22 23:38:44',NULL,NULL,0),(327472489573781504,'文件管理','上传文件','上传文件','POST','/files/upload','[{\"type\":\"MultipartFile\",\"size\":20480,\"originalFilename\":\"pdf_img_p344_3.png\"},{\"accessLevel\":1,\"fileType\":\"document\"}]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.9',1202,1,NULL,'2026-06-22 23:38:45','2026-06-22 23:38:45',NULL,NULL,0),(327472492073586688,'文件管理','上传文件','上传文件','POST','/files/upload','[{\"type\":\"MultipartFile\",\"size\":30235,\"originalFilename\":\"pdf_img_p345_0.png\"},{\"accessLevel\":1,\"fileType\":\"document\"}]',NULL,NULL,NULL,'127.0.0.1',NULL,'Java/21.0.9',113,0,'Redis command interrupted','2026-06-22 23:38:46','2026-06-22 23:38:46',NULL,NULL,0),(327701995093889024,'文档审核','审核操作','审核文档','POST','/review/tasks/327406738288545792/review','[327406738288545792,{\"status\":\"approved\"}]',NULL,1000000000000000001,'admin','0:0:0:0:0:0:0:1',NULL,'Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/149.0.0.0 Safari/537.36',293,1,NULL,'2026-06-23 14:50:45','2026-06-23 14:50:45',NULL,NULL,0),(327717280203018240,'文档审核','审核操作','审核文档','POST','/review/tasks/327708822393196544/review','[327708822393196544,{\"status\":\"approved\"}]',NULL,1000000000000000001,'admin','0:0:0:0:0:0:0:1',NULL,'Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/149.0.0.0 Safari/537.36',1336,1,NULL,'2026-06-23 15:51:33','2026-06-23 15:51:33',NULL,NULL,0),(327717544892960768,'文档审核','提交审核','提交文档审核','POST','/review/submit/327396046936346624','[327396046936346624]',NULL,1000000000000000001,'admin','0:0:0:0:0:0:0:1',NULL,'Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/149.0.0.0 Safari/537.36',4215,0,'Unable to connect to Redis','2026-06-23 15:52:31','2026-06-23 15:52:31',NULL,NULL,0),(327717626841272320,'文档审核','提交审核','提交文档审核','POST','/review/submit/327396046936346624','[327396046936346624]',NULL,1000000000000000001,'admin','0:0:0:0:0:0:0:1',NULL,'Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/149.0.0.0 Safari/537.36',696,1,NULL,'2026-06-23 15:52:50','2026-06-23 15:52:50',NULL,NULL,0),(327717769493745664,'文档审核','审核操作','审核文档','POST','/review/tasks/327717626258264064/review','[327717626258264064,{\"status\":\"approved\"}]',NULL,1000000000000000001,'admin','0:0:0:0:0:0:0:1',NULL,'Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/149.0.0.0 Safari/537.36',48,1,NULL,'2026-06-23 15:53:24','2026-06-23 15:53:24',NULL,NULL,0),(327717927744835584,'文档审核','提交审核','提交文档审核','POST','/review/submit/327306488970350592','[327306488970350592]',NULL,1000000000000000001,'admin','0:0:0:0:0:0:0:1',NULL,'Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/149.0.0.0 Safari/537.36',64,1,NULL,'2026-06-23 15:54:02','2026-06-23 15:54:02',NULL,NULL,0),(327719946090057728,'文档审核','提交审核','提交文档审核','POST','/review/submit/327306488970350592','[327306488970350592]',NULL,1000000000000000001,'admin','0:0:0:0:0:0:0:1',NULL,'Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/149.0.0.0 Safari/537.36',977,1,NULL,'2026-06-23 16:02:04','2026-06-23 16:02:04',NULL,NULL,0),(327720138709274624,'文档审核','提交审核','提交文档审核','POST','/review/submit/327283237921624064','[327283237921624064]',NULL,1000000000000000001,'admin','0:0:0:0:0:0:0:1',NULL,'Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/149.0.0.0 Safari/537.36',114,1,NULL,'2026-06-23 16:02:49','2026-06-23 16:02:49',NULL,NULL,0),(327721533747367936,'文档审核','提交审核','提交文档审核','POST','/review/submit/327396046936346624','[327396046936346624]',NULL,1000000000000000001,'admin','0:0:0:0:0:0:0:1',NULL,'Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/149.0.0.0 Safari/537.36',124,1,NULL,'2026-06-23 16:08:22','2026-06-23 16:08:22',NULL,NULL,0),(327723394059603968,'文档审核','提交审核','提交文档审核','POST','/review/submit/327395741289025536','[327395741289025536]',NULL,1000000000000000001,'admin','0:0:0:0:0:0:0:1',NULL,'Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/149.0.0.0 Safari/537.36',2341,1,NULL,'2026-06-23 16:15:47','2026-06-23 16:15:47',NULL,NULL,0),(327723566617464832,'文档审核','审核操作','审核文档','POST','/review/tasks/327723370143682560/review','[327723370143682560,{\"status\":\"approved\"}]',NULL,1000000000000000001,'admin','0:0:0:0:0:0:0:1',NULL,'Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/149.0.0.0 Safari/537.36',69,1,NULL,'2026-06-23 16:16:27','2026-06-23 16:16:27',NULL,NULL,0),(327723580026654720,'文档审核','审核操作','审核文档','POST','/review/tasks/327721532711374848/review','[327721532711374848,{\"status\":\"approved\"}]',NULL,1000000000000000001,'admin','0:0:0:0:0:0:0:1',NULL,'Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/149.0.0.0 Safari/537.36',252,1,NULL,'2026-06-23 16:16:30','2026-06-23 16:16:30',NULL,NULL,0),(327723592647315456,'文档审核','审核操作','审核文档','POST','/review/tasks/327720138084323328/review','[327720138084323328,{\"status\":\"approved\"}]',NULL,1000000000000000001,'admin','0:0:0:0:0:0:0:1',NULL,'Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/149.0.0.0 Safari/537.36',58,1,NULL,'2026-06-23 16:16:33','2026-06-23 16:16:33',NULL,NULL,0),(327723602461986816,'文档审核','审核操作','审核文档','POST','/review/tasks/327719942508122112/review','[327719942508122112,{\"status\":\"approved\"}]',NULL,1000000000000000001,'admin','0:0:0:0:0:0:0:1',NULL,'Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/149.0.0.0 Safari/537.36',52,1,NULL,'2026-06-23 16:16:35','2026-06-23 16:16:35',NULL,NULL,0),(327723614386393088,'文档审核','审核操作','审核文档','POST','/review/tasks/327717927476400128/review','[327717927476400128,{\"status\":\"approved\"}]',NULL,1000000000000000001,'admin','0:0:0:0:0:0:0:1',NULL,'Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/149.0.0.0 Safari/537.36',43,1,NULL,'2026-06-23 16:16:38','2026-06-23 16:16:38',NULL,NULL,0),(327723804774240256,'文档审核','提交审核','提交文档审核','POST','/review/submit/327396046936346624','[327396046936346624]',NULL,1000000000000000001,'admin','0:0:0:0:0:0:0:1',NULL,'Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/149.0.0.0 Safari/537.36',268,1,NULL,'2026-06-23 16:17:23','2026-06-23 16:17:23',NULL,NULL,0),(327723995237584896,'文档审核','提交审核','提交文档审核','POST','/review/submit/327306488970350592','[327306488970350592]',NULL,1000000000000000001,'admin','0:0:0:0:0:0:0:1',NULL,'Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/149.0.0.0 Safari/537.36',614,1,NULL,'2026-06-23 16:18:09','2026-06-23 16:18:09',NULL,NULL,0),(327729023088201728,'文档审核','审核操作','审核文档','POST','/review/tasks/327723994507776000/review','[327723994507776000,{\"status\":\"approved\"}]',NULL,1000000000000000001,'admin','0:0:0:0:0:0:0:1',NULL,'Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/149.0.0.0 Safari/537.36',540,1,NULL,'2026-06-23 16:38:08','2026-06-23 16:38:08',NULL,NULL,0),(327729038137364480,'文档审核','审核操作','审核文档','POST','/review/tasks/327723804149288960/review','[327723804149288960,{\"status\":\"approved\"}]',NULL,1000000000000000001,'admin','0:0:0:0:0:0:0:1',NULL,'Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/149.0.0.0 Safari/537.36',63,1,NULL,'2026-06-23 16:38:11','2026-06-23 16:38:11',NULL,NULL,0),(327729229192105984,'文档审核','提交审核','提交文档审核','POST','/review/submit/327395741289025536','[327395741289025536]',NULL,1000000000000000001,'admin','0:0:0:0:0:0:0:1',NULL,'Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/149.0.0.0 Safari/537.36',78,1,NULL,'2026-06-23 16:38:57','2026-06-23 16:38:57',NULL,NULL,0),(327730655771365376,'文档审核','提交审核','提交文档审核','POST','/review/submit/327405209401823232','[327405209401823232]',NULL,1000000000000000001,'admin','0:0:0:0:0:0:0:1',NULL,'Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/149.0.0.0 Safari/537.36',255,1,NULL,'2026-06-23 16:44:39','2026-06-23 16:44:39',NULL,NULL,0),(327730871853518848,'文档审核','提交审核','提交文档审核','POST','/review/submit/327396046936346624','[327396046936346624]',NULL,1000000000000000001,'admin','0:0:0:0:0:0:0:1',NULL,'Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/149.0.0.0 Safari/537.36',71,1,NULL,'2026-06-23 16:45:28','2026-06-23 16:45:28',NULL,NULL,0),(327731760647507968,'文档审核','提交审核','提交文档审核','POST','/review/submit/327306488970350592','[327306488970350592]',NULL,1000000000000000001,'admin','0:0:0:0:0:0:0:1',NULL,'Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/149.0.0.0 Safari/537.36',132,1,NULL,'2026-06-23 16:49:01','2026-06-23 16:49:01',NULL,NULL,0),(327731836610547712,'文档审核','审核操作','审核文档','POST','/review/tasks/327731760110637056/review','[327731760110637056,{\"status\":\"approved\"}]',NULL,1000000000000000001,'admin','0:0:0:0:0:0:0:1',NULL,'Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/149.0.0.0 Safari/537.36',54,1,NULL,'2026-06-23 16:49:18','2026-06-23 16:49:18',NULL,NULL,0),(327732841876164608,'文档审核','提交审核','提交文档审核','POST','/review/submit/327405209401823232','[327405209401823232]',NULL,1000000000000000001,'admin','0:0:0:0:0:0:0:1',NULL,'Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/149.0.0.0 Safari/537.36',250,1,NULL,'2026-06-23 16:53:18','2026-06-23 16:53:18',NULL,NULL,0),(327732871240486912,'文档审核','提交审核','提交文档审核','POST','/review/submit/327405209401823232','[327405209401823232]',NULL,1000000000000000001,'admin','0:0:0:0:0:0:0:1',NULL,'Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/149.0.0.0 Safari/537.36',78,0,'只有草稿或已发布状态的文档才能提交审核','2026-06-23 16:53:25','2026-06-23 16:53:25',NULL,NULL,0),(327732988542586880,'文档审核','提交审核','提交文档审核','POST','/review/submit/327405209401823232','[327405209401823232]',NULL,1000000000000000001,'admin','0:0:0:0:0:0:0:1',NULL,'Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/149.0.0.0 Safari/537.36',62,0,'只有草稿或已发布状态的文档才能提交审核','2026-06-23 16:53:53','2026-06-23 16:53:53',NULL,NULL,0),(327732998793465856,'文档审核','提交审核','提交文档审核','POST','/review/submit/327405209401823232','[327405209401823232]',NULL,1000000000000000001,'admin','0:0:0:0:0:0:0:1',NULL,'Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/149.0.0.0 Safari/537.36',79,0,'只有草稿或已发布状态的文档才能提交审核','2026-06-23 16:53:55','2026-06-23 16:53:55',NULL,NULL,0),(327733167182188544,'文档审核','提交审核','提交文档审核','POST','/review/submit/327405209401823232','[327405209401823232]',NULL,1000000000000000001,'admin','0:0:0:0:0:0:0:1',NULL,'Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/149.0.0.0 Safari/537.36',203,1,NULL,'2026-06-23 16:54:36','2026-06-23 16:54:36',NULL,NULL,0),(4000000000000000001,'用户管理','LOGIN','用户登录','POST','/api/auth/login',NULL,NULL,1000000000000000001,'admin','127.0.0.1',NULL,NULL,125,1,NULL,'2026-06-19 10:24:37','2026-06-19 10:24:37',NULL,NULL,0),(4000000000000000002,'文档管理','CREATE','创建文档','POST','/api/document',NULL,NULL,1000000000000000002,'editor','127.0.0.1',NULL,NULL,342,1,NULL,'2026-06-19 10:24:37','2026-06-19 10:24:37',NULL,NULL,0),(4000000000000000003,'文档管理','UPDATE','更新文档','PUT','/api/document/1000000000000000001',NULL,NULL,1000000000000000002,'editor','127.0.0.1',NULL,NULL,215,1,NULL,'2026-06-19 10:24:37','2026-06-19 10:24:37',NULL,NULL,0),(4000000000000000004,'系统配置','UPDATE','更新系统配置','PUT','/api/foundation/config',NULL,NULL,1000000000000000001,'admin','127.0.0.1',NULL,NULL,89,1,NULL,'2026-06-19 10:24:37','2026-06-19 10:24:37',NULL,NULL,0),(4000000000000000005,'用户管理','CREATE','创建用户','POST','/api/auth/user',NULL,NULL,1000000000000000001,'admin','127.0.0.1',NULL,NULL,156,1,NULL,'2026-06-19 10:24:37','2026-06-19 10:24:37',NULL,NULL,0);
/*!40000 ALTER TABLE `kb_operation_log` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `kb_system_config`
--

DROP TABLE IF EXISTS `kb_system_config`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `kb_system_config` (
  `id` bigint NOT NULL COMMENT '配置ID',
  `config_key` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '配置键',
  `config_value` text COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '配置值',
  `config_type` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'string' COMMENT '配置类型',
  `category` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '配置分类',
  `description` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '配置描述',
  `is_public` tinyint NOT NULL DEFAULT '0' COMMENT '是否公开',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `create_by` bigint DEFAULT NULL COMMENT '创建人',
  `update_by` bigint DEFAULT NULL COMMENT '更新人',
  `deleted` tinyint NOT NULL DEFAULT '0' COMMENT '删除标识',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_config_key` (`config_key`),
  KEY `idx_category` (`category`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='系统配置表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `kb_system_config`
--

LOCK TABLES `kb_system_config` WRITE;
/*!40000 ALTER TABLE `kb_system_config` DISABLE KEYS */;
INSERT INTO `kb_system_config` VALUES (2000000000000000001,'qwen.api.key','','string','AI','千问API密钥',0,'2026-06-19 10:24:37','2026-06-19 10:24:37',NULL,NULL,0),(2000000000000000002,'qwen.model.name','qwen-max','string','AI','千问模型名称',1,'2026-06-19 10:24:37','2026-06-19 10:24:37',NULL,NULL,0),(2000000000000000003,'qwen.embedding.model','text-embedding-v3','string','AI','千问嵌入模型',1,'2026-06-19 10:24:37','2026-06-19 10:24:37',NULL,NULL,0),(2000000000000000004,'milvus.host','localhost','string','AI','Milvus主机地址',1,'2026-06-19 10:24:37','2026-06-19 10:24:37',NULL,NULL,0),(2000000000000000005,'milvus.port','19530','number','AI','Milvus端口',1,'2026-06-19 10:24:37','2026-06-19 10:24:37',NULL,NULL,0),(2000000000000000006,'rustfs.endpoints','http://localhost:8200','json','STORAGE','RustFS端点列表',1,'2026-06-19 10:24:37','2026-06-19 10:24:37',NULL,NULL,0),(2000000000000000007,'rustfs.bucket','knowledge-docs','string','STORAGE','RustFS存储桶',1,'2026-06-19 10:24:37','2026-06-19 10:24:37',NULL,NULL,0),(2000000000000000008,'file.upload.max.size','52428800','number','STORAGE','文件上传最大大小（字节）',1,'2026-06-19 10:24:37','2026-06-19 10:24:37',NULL,NULL,0),(2000000000000000009,'file.upload.allowed.types','pdf,doc,docx,xls,xlsx,ppt,pptx,txt,md,jpg,jpeg,png,gif,bmp,webp,svg,ico,mp4,avi,mov,wmv,flv,mkv,webm,mp3,wav,flac,aac,ogg,m4a,wma','string','STORAGE','允许上传的文件类型',1,'2026-06-19 10:24:37','2026-06-19 10:24:37',NULL,NULL,0),(2000000000000000010,'email.enabled','true','boolean','NOTIFICATION','是否启用邮件通知',1,'2026-06-19 10:24:37','2026-06-19 10:24:37',NULL,NULL,0),(2000000000000000011,'email.host','smtp.example.com','string','NOTIFICATION','邮件服务器地址',0,'2026-06-19 10:24:37','2026-06-19 10:24:37',NULL,NULL,0),(2000000000000000012,'email.port','587','number','NOTIFICATION','邮件服务器端口',0,'2026-06-19 10:24:37','2026-06-19 10:24:37',NULL,NULL,0),(2000000000000000013,'notification.retention.days','90','number','NOTIFICATION','通知保留天数',1,'2026-06-19 10:24:37','2026-06-19 10:24:37',NULL,NULL,0),(2000000000000000014,'websocket.enabled','true','boolean','NOTIFICATION','是否启用WebSocket推送',1,'2026-06-19 10:24:37','2026-06-19 10:24:37',NULL,NULL,0),(2000000000000000015,'auth.session.timeout','7200','number','SECURITY','会话超时时间（秒）',1,'2026-06-19 10:24:37','2026-06-19 10:24:37',NULL,NULL,0),(2000000000000000016,'auth.password.min.length','8','number','SECURITY','密码最小长度',1,'2026-06-19 10:24:37','2026-06-19 10:24:37',NULL,NULL,0),(2000000000000000017,'auth.password.require.special','true','boolean','SECURITY','密码是否要求特殊字符',1,'2026-06-19 10:24:37','2026-06-19 10:24:37',NULL,NULL,0),(2000000000000000018,'auth.login.max.retry','5','number','SECURITY','登录最大重试次数',1,'2026-06-19 10:24:37','2026-06-19 10:24:37',NULL,NULL,0),(2000000000000000019,'system.name','企业知识库','string','SYSTEM','系统名称',1,'2026-06-19 10:24:37','2026-06-19 10:24:37',NULL,NULL,0),(2000000000000000020,'system.version','1.0.0','string','SYSTEM','系统版本',1,'2026-06-19 10:24:37','2026-06-19 10:24:37',NULL,NULL,0),(2000000000000000021,'system.logo','/logo.png','string','SYSTEM','系统Logo路径',1,'2026-06-19 10:24:37','2026-06-19 10:24:37',NULL,NULL,0),(2000000000000000022,'user.registration.enabled','true','boolean','SYSTEM','是否允许用户注册',1,'2026-06-19 10:24:37','2026-06-19 10:24:37',NULL,NULL,0),(2000000000000000023,'user.default.role','VIEWER','string','SYSTEM','新用户默认角色',1,'2026-06-19 10:24:37','2026-06-19 10:24:37',NULL,NULL,0);
/*!40000 ALTER TABLE `kb_system_config` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping routines for database 'kb_foundation'
--

--
-- Current Database: `kb_statistics`
--

CREATE DATABASE /*!32312 IF NOT EXISTS*/ `kb_statistics` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci */ /*!80016 DEFAULT ENCRYPTION='N' */;

USE `kb_statistics`;

--
-- Temporary view structure for view `kb_ai_conversation`
--

DROP TABLE IF EXISTS `kb_ai_conversation`;
/*!50001 DROP VIEW IF EXISTS `kb_ai_conversation`*/;
SET @saved_cs_client     = @@character_set_client;
/*!50503 SET character_set_client = utf8mb4 */;
/*!50001 CREATE VIEW `kb_ai_conversation` AS SELECT 
 1 AS `id`,
 1 AS `user_id`,
 1 AS `user_name`,
 1 AS `title`,
 1 AS `model_name`,
 1 AS `message_count`,
 1 AS `created_at`,
 1 AS `updated_at`,
 1 AS `deleted`*/;
SET character_set_client = @saved_cs_client;

--
-- Temporary view structure for view `kb_ai_message`
--

DROP TABLE IF EXISTS `kb_ai_message`;
/*!50001 DROP VIEW IF EXISTS `kb_ai_message`*/;
SET @saved_cs_client     = @@character_set_client;
/*!50503 SET character_set_client = utf8mb4 */;
/*!50001 CREATE VIEW `kb_ai_message` AS SELECT 
 1 AS `id`,
 1 AS `conversation_id`,
 1 AS `role`,
 1 AS `content`,
 1 AS `tokens`,
 1 AS `created_at`,
 1 AS `deleted`*/;
SET character_set_client = @saved_cs_client;

--
-- Temporary view structure for view `kb_category`
--

DROP TABLE IF EXISTS `kb_category`;
/*!50001 DROP VIEW IF EXISTS `kb_category`*/;
SET @saved_cs_client     = @@character_set_client;
/*!50503 SET character_set_client = utf8mb4 */;
/*!50001 CREATE VIEW `kb_category` AS SELECT 
 1 AS `id`,
 1 AS `category_name`,
 1 AS `parent_id`,
 1 AS `category_icon`,
 1 AS `description`,
 1 AS `sort`,
 1 AS `document_count`,
 1 AS `status`,
 1 AS `created_at`,
 1 AS `updated_at`,
 1 AS `create_by`,
 1 AS `update_by`,
 1 AS `deleted`*/;
SET character_set_client = @saved_cs_client;

--
-- Temporary view structure for view `kb_comment`
--

DROP TABLE IF EXISTS `kb_comment`;
/*!50001 DROP VIEW IF EXISTS `kb_comment`*/;
SET @saved_cs_client     = @@character_set_client;
/*!50503 SET character_set_client = utf8mb4 */;
/*!50001 CREATE VIEW `kb_comment` AS SELECT 
 1 AS `id`,
 1 AS `document_id`,
 1 AS `content`,
 1 AS `user_id`,
 1 AS `user_name`,
 1 AS `user_avatar`,
 1 AS `parent_id`,
 1 AS `reply_to_id`,
 1 AS `reply_to_name`,
 1 AS `like_count`,
 1 AS `reply_count`,
 1 AS `status`,
 1 AS `created_at`,
 1 AS `updated_at`,
 1 AS `deleted`*/;
SET character_set_client = @saved_cs_client;

--
-- Table structure for table `kb_comment_statistics`
--

DROP TABLE IF EXISTS `kb_comment_statistics`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `kb_comment_statistics` (
  `id` bigint NOT NULL COMMENT '统计ID',
  `comment_id` bigint NOT NULL COMMENT '评论ID',
  `like_count` int NOT NULL DEFAULT '0' COMMENT '点赞次数',
  `reply_count` int NOT NULL DEFAULT '0' COMMENT '回复次数',
  `stat_date` date NOT NULL COMMENT '统计日期',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_comment_date` (`comment_id`,`stat_date`),
  KEY `idx_stat_date` (`stat_date`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='评论统计表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `kb_comment_statistics`
--

LOCK TABLES `kb_comment_statistics` WRITE;
/*!40000 ALTER TABLE `kb_comment_statistics` DISABLE KEYS */;
/*!40000 ALTER TABLE `kb_comment_statistics` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Temporary view structure for view `kb_document`
--

DROP TABLE IF EXISTS `kb_document`;
/*!50001 DROP VIEW IF EXISTS `kb_document`*/;
SET @saved_cs_client     = @@character_set_client;
/*!50503 SET character_set_client = utf8mb4 */;
/*!50001 CREATE VIEW `kb_document` AS SELECT 
 1 AS `id`,
 1 AS `title`,
 1 AS `author_id`,
 1 AS `author_name`,
 1 AS `category_id`,
 1 AS `status`,
 1 AS `view_count`,
 1 AS `like_count`,
 1 AS `favorite_count`,
 1 AS `comment_count`,
 1 AS `is_public`,
 1 AS `is_top`,
 1 AS `is_recommend`,
 1 AS `document_type`,
 1 AS `source`,
 1 AS `cover_image`,
 1 AS `summary`,
 1 AS `sort`,
 1 AS `allow_comment`,
 1 AS `publish_time`,
 1 AS `created_at`,
 1 AS `updated_at`,
 1 AS `create_by`,
 1 AS `update_by`,
 1 AS `deleted`*/;
SET character_set_client = @saved_cs_client;

--
-- Table structure for table `kb_document_statistics`
--

DROP TABLE IF EXISTS `kb_document_statistics`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `kb_document_statistics` (
  `id` bigint NOT NULL COMMENT '统计ID',
  `document_id` bigint NOT NULL COMMENT '文档ID',
  `document_title` varchar(200) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '文档标题',
  `view_count` int NOT NULL DEFAULT '0' COMMENT '浏览次数',
  `like_count` int NOT NULL DEFAULT '0' COMMENT '点赞次数',
  `comment_count` int NOT NULL DEFAULT '0' COMMENT '评论次数',
  `favorite_count` int NOT NULL DEFAULT '0' COMMENT '收藏次数',
  `share_count` int NOT NULL DEFAULT '0' COMMENT '分享次数',
  `stat_date` date NOT NULL COMMENT '统计日期',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_doc_date` (`document_id`,`stat_date`),
  KEY `idx_stat_date` (`stat_date`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='文档统计表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `kb_document_statistics`
--

LOCK TABLES `kb_document_statistics` WRITE;
/*!40000 ALTER TABLE `kb_document_statistics` DISABLE KEYS */;
INSERT INTO `kb_document_statistics` VALUES (326754331514769408,1000000000000000006,'UI设计规范 V2.0',1,0,0,0,0,'2026-06-20','2026-06-21 00:05:03'),(327532671431479296,327283313100328960,'AgentScope2.0发布了',1,0,0,0,0,'2026-06-22','2026-06-23 03:37:54'),(327532671758635008,327313369386323968,'测试',1,0,0,0,0,'2026-06-22','2026-06-23 03:37:54'),(327532672077402112,327366085009608704,'PostgreSQL入门教程',1,0,0,0,0,'2026-06-22','2026-06-23 03:37:54'),(327532672391974912,327381599069016064,'1月度工作任务计划表 ',3,0,0,0,0,'2026-06-22','2026-06-23 03:37:54'),(327532672723324928,327393448317554688,'美团二面：高并发下如何保证接口幂等性？',1,0,0,0,0,'2026-06-22','2026-06-23 03:37:54'),(327532673042092032,327394591991009280,'美团二面：高并发下如何保证接口幂等性？',2,0,0,0,0,'2026-06-22','2026-06-23 03:37:54'),(327532673453133824,1000000000000000003,'MySQL 8.0 性能优化指南',3,1,0,0,0,'2026-06-22','2026-06-23 03:37:54'),(327532673771900928,1000000000000000004,'Docker + Kubernetes 容器化部署',1,0,0,0,0,'2026-06-22','2026-06-23 03:37:54'),(327532674090668032,1000000000000000006,'UI设计规范 V2.0',2,0,0,0,0,'2026-06-22','2026-06-23 03:37:54'),(327532674417823744,1000000000000000009,'报销流程说明',3,0,0,0,0,'2026-06-22','2026-06-23 03:37:54'),(1800000000000000001,1000000000000000001,'Spring Boot 3.x 快速入门指南',1523,89,23,45,12,'2026-06-19','2026-06-19 10:24:37'),(1800000000000000002,1000000000000000002,'React 18 + TypeScript 最佳实践',2187,156,45,67,23,'2026-06-19','2026-06-19 10:24:37'),(1800000000000000003,1000000000000000003,'MySQL 8.0 性能优化指南',3,0,0,89,34,'2026-06-19','2026-06-19 10:24:37'),(1800000000000000004,1000000000000000004,'Docker + Kubernetes 容器化部署',1,0,0,34,8,'2026-06-19','2026-06-19 10:24:37'),(1800000000000000005,1000000000000000005,'企业知识库产品需求文档PRD',987,45,12,23,5,'2026-06-19','2026-06-19 10:24:37'),(1800000000000000006,1000000000000000006,'UI设计规范 V2.0',654,34,8,12,3,'2026-06-19','2026-06-19 10:24:37'),(1800000000000000007,1000000000000000007,'文档审核流程规范',1234,67,15,34,7,'2026-06-19','2026-06-19 10:24:37'),(1800000000000000008,1000000000000000008,'员工入职指南',5678,234,56,89,45,'2026-06-19','2026-06-19 10:24:37'),(1800000000000000009,1000000000000000009,'报销流程说明',3456,123,34,56,21,'2026-06-19','2026-06-19 10:24:37');
/*!40000 ALTER TABLE `kb_document_statistics` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Temporary view structure for view `kb_operation_log`
--

DROP TABLE IF EXISTS `kb_operation_log`;
/*!50001 DROP VIEW IF EXISTS `kb_operation_log`*/;
SET @saved_cs_client     = @@character_set_client;
/*!50503 SET character_set_client = utf8mb4 */;
/*!50001 CREATE VIEW `kb_operation_log` AS SELECT 
 1 AS `id`,
 1 AS `module`,
 1 AS `operation_type`,
 1 AS `operation_desc`,
 1 AS `request_method`,
 1 AS `request_url`,
 1 AS `request_params`,
 1 AS `response_result`,
 1 AS `user_id`,
 1 AS `username`,
 1 AS `ip_address`,
 1 AS `location`,
 1 AS `user_agent`,
 1 AS `execute_time`,
 1 AS `status`,
 1 AS `error_msg`,
 1 AS `created_at`,
 1 AS `updated_at`,
 1 AS `create_by`,
 1 AS `update_by`,
 1 AS `deleted`*/;
SET character_set_client = @saved_cs_client;

--
-- Temporary view structure for view `kb_user`
--

DROP TABLE IF EXISTS `kb_user`;
/*!50001 DROP VIEW IF EXISTS `kb_user`*/;
SET @saved_cs_client     = @@character_set_client;
/*!50503 SET character_set_client = utf8mb4 */;
/*!50001 CREATE VIEW `kb_user` AS SELECT 
 1 AS `id`,
 1 AS `username`,
 1 AS `real_name`,
 1 AS `avatar`,
 1 AS `status`,
 1 AS `email`,
 1 AS `phone`,
 1 AS `department`,
 1 AS `position`,
 1 AS `last_login_time`,
 1 AS `created_at`,
 1 AS `updated_at`,
 1 AS `deleted`*/;
SET character_set_client = @saved_cs_client;

--
-- Table structure for table `kb_user_statistics`
--

DROP TABLE IF EXISTS `kb_user_statistics`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `kb_user_statistics` (
  `id` bigint NOT NULL COMMENT '统计ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `user_name` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '用户姓名',
  `document_count` int NOT NULL DEFAULT '0' COMMENT '文档数量',
  `comment_count` int NOT NULL DEFAULT '0' COMMENT '评论数量',
  `like_count` int NOT NULL DEFAULT '0' COMMENT '点赞次数',
  `view_count` int NOT NULL DEFAULT '0' COMMENT '浏览次数',
  `login_count` int NOT NULL DEFAULT '0' COMMENT '登录次数',
  `stat_date` date NOT NULL COMMENT '统计日期',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_date` (`user_id`,`stat_date`),
  KEY `idx_stat_date` (`stat_date`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户统计表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `kb_user_statistics`
--

LOCK TABLES `kb_user_statistics` WRITE;
/*!40000 ALTER TABLE `kb_user_statistics` DISABLE KEYS */;
INSERT INTO `kb_user_statistics` VALUES (326755589365567488,1000000000000000001,'admin',0,0,0,1,0,'2026-06-20','2026-06-21 00:10:03'),(327533928997064704,1000000000000000001,'admin',0,0,0,18,0,'2026-06-22','2026-06-23 03:42:53'),(1900000000000000001,1000000000000000001,'admin',3,15,45,4,67,'2026-06-19','2026-06-19 10:24:37'),(1900000000000000002,1000000000000000004,'developer',2,23,89,4523,89,'2026-06-19','2026-06-19 10:24:37'),(1900000000000000003,1000000000000000002,'editor',1,12,34,1234,45,'2026-06-19','2026-06-19 10:24:37'),(1900000000000000004,1000000000000000006,'designer',1,8,34,876,23,'2026-06-19','2026-06-19 10:24:37'),(1900000000000000005,1000000000000000005,'product',1,6,23,1567,34,'2026-06-19','2026-06-19 10:24:37'),(1900000000000000006,1000000000000000003,'tester',0,8,15,987,12,'2026-06-19','2026-06-19 10:24:37');
/*!40000 ALTER TABLE `kb_user_statistics` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `kb_view_history`
--

DROP TABLE IF EXISTS `kb_view_history`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `kb_view_history` (
  `id` bigint NOT NULL COMMENT '记录ID（雪花算法生成）',
  `user_id` bigint DEFAULT NULL COMMENT '用户ID（未登录为NULL）',
  `user_name` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '用户姓名',
  `document_id` bigint NOT NULL COMMENT '文档ID',
  `document_title` varchar(200) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '文档标题',
  `view_duration` int DEFAULT NULL COMMENT '浏览时长（秒）',
  `ip_address` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT 'IP地址',
  `user_agent` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '用户代理',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '浏览时间',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_document_id` (`document_id`),
  KEY `idx_create_time` (`created_at`),
  KEY `idx_user_document` (`user_id`,`document_id`),
  KEY `idx_doc_date` (`document_id`,`created_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户浏览历史记录表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `kb_view_history`
--

LOCK TABLES `kb_view_history` WRITE;
/*!40000 ALTER TABLE `kb_view_history` DISABLE KEYS */;
INSERT INTO `kb_view_history` VALUES (326350835661541376,1000000000000000001,'admin',1000000000000000003,'MySQL 8.0 性能优化指南',NULL,NULL,NULL,'2026-06-19 21:21:42'),(326354602180087808,1000000000000000001,'admin',1000000000000000003,'MySQL 8.0 性能优化指南',NULL,NULL,NULL,'2026-06-19 21:36:40'),(326357478621188096,1000000000000000001,'admin',1000000000000000003,'MySQL 8.0 性能优化指南',NULL,NULL,NULL,'2026-06-19 21:48:06'),(326358672169111552,1000000000000000001,'admin',1000000000000000004,'Docker + Kubernetes 容器化部署',NULL,NULL,NULL,'2026-06-19 21:52:50'),(326593531026214912,1000000000000000001,'admin',1000000000000000006,'UI设计规范 V2.0',NULL,NULL,NULL,'2026-06-20 13:26:05'),(327318985823621120,1000000000000000001,'admin',327313369386323968,'测试',NULL,NULL,NULL,'2026-06-22 13:28:47'),(327367380663668736,1000000000000000001,'admin',327283313100328960,'AgentScope2.0发布了',NULL,NULL,NULL,'2026-06-22 16:41:05'),(327374476171939840,1000000000000000001,'admin',1000000000000000009,'报销流程说明',NULL,NULL,NULL,'2026-06-22 17:09:17'),(327377078280392704,1000000000000000001,'admin',1000000000000000009,'报销流程说明',NULL,NULL,NULL,'2026-06-22 17:19:37'),(327377797838409728,1000000000000000001,'admin',1000000000000000003,'MySQL 8.0 性能优化指南',NULL,NULL,NULL,'2026-06-22 17:22:29'),(327382398981509120,1000000000000000001,'admin',327381599069016064,'1月度工作任务计划表 ',NULL,NULL,NULL,'2026-06-22 17:40:46'),(327382563415003136,1000000000000000001,'admin',327381599069016064,'1月度工作任务计划表 ',NULL,NULL,NULL,'2026-06-22 17:41:25'),(327382676581519360,1000000000000000001,'admin',327381599069016064,'1月度工作任务计划表 ',NULL,NULL,NULL,'2026-06-22 17:41:52'),(327386570749513728,1000000000000000001,'admin',327366085009608704,'PostgreSQL入门教程',NULL,NULL,NULL,'2026-06-22 17:57:21'),(327391273621262336,1000000000000000001,'admin',1000000000000000003,'MySQL 8.0 性能优化指南',NULL,NULL,NULL,'2026-06-22 18:16:02'),(327391840288509952,1000000000000000001,'admin',1000000000000000003,'MySQL 8.0 性能优化指南',NULL,NULL,NULL,'2026-06-22 18:18:17'),(327394336125882368,1000000000000000001,'admin',327393448317554688,'美团二面：高并发下如何保证接口幂等性？',NULL,NULL,NULL,'2026-06-22 18:28:12'),(327394728544964608,1000000000000000001,'admin',327394591991009280,'美团二面：高并发下如何保证接口幂等性？',NULL,NULL,NULL,'2026-06-22 18:29:45'),(327394848686608384,1000000000000000001,'admin',327394591991009280,'美团二面：高并发下如何保证接口幂等性？',NULL,NULL,NULL,'2026-06-22 18:30:14'),(327440332834869248,1000000000000000001,'admin',1000000000000000004,'Docker + Kubernetes 容器化部署',NULL,NULL,NULL,'2026-06-22 21:30:59'),(327450864946122752,1000000000000000001,'admin',1000000000000000009,'报销流程说明',NULL,NULL,NULL,'2026-06-22 22:12:49'),(327451116797300736,1000000000000000001,'admin',1000000000000000006,'UI设计规范 V2.0',NULL,NULL,NULL,'2026-06-22 22:13:49'),(327468617358774272,1000000000000000001,'admin',1000000000000000006,'UI设计规范 V2.0',NULL,NULL,NULL,'2026-06-22 23:23:22'),(327689617514041344,1000000000000000001,'admin',1000000000000000004,'Docker + Kubernetes 容器化部署',NULL,NULL,NULL,'2026-06-23 14:01:33'),(327690303890919424,1000000000000000001,'admin',1000000000000000003,'MySQL 8.0 性能优化指南',NULL,NULL,NULL,'2026-06-23 14:04:16'),(327690592870076416,1000000000000000001,'admin',1000000000000000002,'React 18 + TypeScript 最佳实践',NULL,NULL,NULL,'2026-06-23 14:05:25'),(327708786049552384,1000000000000000001,'admin',327395741289025536,'索引优化的10个高效技巧',NULL,NULL,NULL,'2026-06-23 15:17:43'),(327717479822528512,1000000000000000001,'admin',327396046936346624,'线上慢SQL导致CPU飙升，如何处理？',NULL,NULL,NULL,'2026-06-23 15:52:15'),(327717638799233024,1000000000000000001,'admin',327396046936346624,'线上慢SQL导致CPU飙升，如何处理？',NULL,NULL,NULL,'2026-06-23 15:52:53'),(327717825160548352,1000000000000000001,'admin',327306488970350592,'全网爆火的Loop到底是什么？',NULL,NULL,NULL,'2026-06-23 15:53:38'),(327720100838903808,1000000000000000001,'admin',327283237921624064,'AgentScope2.0发布了',NULL,NULL,NULL,'2026-06-23 16:02:40'),(327720152152018944,1000000000000000001,'admin',327283237921624064,'AgentScope2.0发布了',NULL,NULL,NULL,'2026-06-23 16:02:53'),(327721499849003008,1000000000000000001,'admin',327396046936346624,'线上慢SQL导致CPU飙升，如何处理？',NULL,NULL,NULL,'2026-06-23 16:08:14'),(327721554098130944,1000000000000000001,'admin',327396046936346624,'线上慢SQL导致CPU飙升，如何处理？',NULL,NULL,NULL,'2026-06-23 16:08:27'),(327723319333883904,1000000000000000001,'admin',327395741289025536,'索引优化的10个高效技巧',NULL,NULL,NULL,'2026-06-23 16:15:28'),(327723394076381184,1000000000000000001,'admin',327395741289025536,'索引优化的10个高效技巧',NULL,NULL,NULL,'2026-06-23 16:15:45'),(327723743021502464,1000000000000000001,'admin',1000000000000000003,'MySQL 8.0 性能优化指南',NULL,NULL,NULL,'2026-06-23 16:17:09'),(327723818124709888,1000000000000000001,'admin',327396046936346624,'线上慢SQL导致CPU飙升，如何处理？',NULL,NULL,NULL,'2026-06-23 16:17:27'),(327723971682373632,1000000000000000001,'admin',327306488970350592,'全网爆火的Loop到底是什么？',NULL,NULL,NULL,'2026-06-23 16:18:03'),(327724009460469760,1000000000000000001,'admin',327306488970350592,'全网爆火的Loop到底是什么？',NULL,NULL,NULL,'2026-06-23 16:18:12'),(327729197529305088,1000000000000000001,'admin',327395741289025536,'索引优化的10个高效技巧',NULL,NULL,NULL,'2026-06-23 16:38:49'),(327729244513898496,1000000000000000001,'admin',327395741289025536,'索引优化的10个高效技巧',NULL,NULL,NULL,'2026-06-23 16:39:00'),(327730613111099392,1000000000000000001,'admin',327405209401823232,'程序员最常用的10个AI提示词',NULL,NULL,NULL,'2026-06-23 16:44:27'),(327730677703380992,1000000000000000001,'admin',327405209401823232,'程序员最常用的10个AI提示词',NULL,NULL,NULL,'2026-06-23 16:44:42'),(327730846075326464,1000000000000000001,'admin',327396046936346624,'线上慢SQL导致CPU飙升，如何处理？',NULL,NULL,NULL,'2026-06-23 16:45:22'),(327730887200477184,1000000000000000001,'admin',327396046936346624,'线上慢SQL导致CPU飙升，如何处理？',NULL,NULL,NULL,'2026-06-23 16:45:32'),(327731733657161728,1000000000000000001,'admin',327306488970350592,'全网爆火的Loop到底是什么？',NULL,NULL,NULL,'2026-06-23 16:48:54'),(327731779861614592,1000000000000000001,'admin',327306488970350592,'全网爆火的Loop到底是什么？',NULL,NULL,NULL,'2026-06-23 16:49:05'),(327732809621966848,1000000000000000001,'admin',327405209401823232,'程序员最常用的10个AI提示词',NULL,NULL,NULL,'2026-06-23 16:53:10'),(327732861455175680,1000000000000000001,'admin',327405209401823232,'程序员最常用的10个AI提示词',NULL,NULL,NULL,'2026-06-23 16:53:23'),(327733176329965568,1000000000000000001,'admin',327405209401823232,'程序员最常用的10个AI提示词',NULL,NULL,NULL,'2026-06-23 16:54:38'),(327737092845211648,1000000000000000001,'admin',327405209401823232,'程序员最常用的10个AI提示词',NULL,NULL,NULL,'2026-06-23 17:10:11'),(327737102307561472,1000000000000000001,'admin',327405209401823232,'程序员最常用的10个AI提示词',NULL,NULL,NULL,'2026-06-23 17:10:14');
/*!40000 ALTER TABLE `kb_view_history` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `kb_view_record`
--

DROP TABLE IF EXISTS `kb_view_record`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `kb_view_record` (
  `id` bigint NOT NULL COMMENT '记录ID',
  `user_id` bigint DEFAULT NULL COMMENT '用户ID',
  `document_id` bigint NOT NULL COMMENT '文档ID',
  `view_duration` int DEFAULT NULL COMMENT '浏览时长（秒）',
  `ip_address` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT 'IP地址',
  `user_agent` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '用户代理',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '浏览时间',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_document_id` (`document_id`),
  KEY `idx_create_time` (`created_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='浏览记录表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `kb_view_record`
--

LOCK TABLES `kb_view_record` WRITE;
/*!40000 ALTER TABLE `kb_view_record` DISABLE KEYS */;
/*!40000 ALTER TABLE `kb_view_record` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping routines for database 'kb_statistics'
--

--
-- Current Database: `kb_user`
--

USE `kb_user`;

--
-- Final view structure for view `kb_document`
--

/*!50001 DROP VIEW IF EXISTS `kb_document`*/;
/*!50001 SET @saved_cs_client          = @@character_set_client */;
/*!50001 SET @saved_cs_results         = @@character_set_results */;
/*!50001 SET @saved_col_connection     = @@collation_connection */;
/*!50001 SET character_set_client      = utf8mb4 */;
/*!50001 SET character_set_results     = utf8mb4 */;
/*!50001 SET collation_connection      = utf8mb4_0900_ai_ci */;
/*!50001 CREATE ALGORITHM=UNDEFINED */
/*!50013 DEFINER=`root`@`localhost` SQL SECURITY DEFINER */
/*!50001 VIEW `kb_document` AS select `kb_document`.`kb_document`.`id` AS `id`,`kb_document`.`kb_document`.`title` AS `title`,`kb_document`.`kb_document`.`author_id` AS `author_id`,`kb_document`.`kb_document`.`author_name` AS `author_name`,`kb_document`.`kb_document`.`category_id` AS `category_id`,`kb_document`.`kb_document`.`status` AS `status`,`kb_document`.`kb_document`.`view_count` AS `view_count`,`kb_document`.`kb_document`.`like_count` AS `like_count`,`kb_document`.`kb_document`.`favorite_count` AS `favorite_count`,`kb_document`.`kb_document`.`comment_count` AS `comment_count`,`kb_document`.`kb_document`.`is_public` AS `is_public`,`kb_document`.`kb_document`.`is_top` AS `is_top`,`kb_document`.`kb_document`.`is_recommend` AS `is_recommend`,`kb_document`.`kb_document`.`document_type` AS `document_type`,`kb_document`.`kb_document`.`source` AS `source`,`kb_document`.`kb_document`.`cover_image` AS `cover_image`,`kb_document`.`kb_document`.`summary` AS `summary`,`kb_document`.`kb_document`.`sort` AS `sort`,`kb_document`.`kb_document`.`allow_comment` AS `allow_comment`,`kb_document`.`kb_document`.`publish_time` AS `publish_time`,`kb_document`.`kb_document`.`created_at` AS `created_at`,`kb_document`.`kb_document`.`updated_at` AS `updated_at`,`kb_document`.`kb_document`.`create_by` AS `create_by`,`kb_document`.`kb_document`.`update_by` AS `update_by`,`kb_document`.`kb_document`.`deleted` AS `deleted` from `kb_document`.`kb_document` */;
/*!50001 SET character_set_client      = @saved_cs_client */;
/*!50001 SET character_set_results     = @saved_cs_results */;
/*!50001 SET collation_connection      = @saved_col_connection */;

--
-- Current Database: `kb_document`
--

USE `kb_document`;

--
-- Current Database: `kb_ai`
--

USE `kb_ai`;

--
-- Current Database: `kb_search`
--

USE `kb_search`;

--
-- Current Database: `kb_file`
--

USE `kb_file`;

--
-- Current Database: `kb_foundation`
--

USE `kb_foundation`;

--
-- Current Database: `kb_statistics`
--

USE `kb_statistics`;

--
-- Final view structure for view `kb_ai_conversation`
--

/*!50001 DROP VIEW IF EXISTS `kb_ai_conversation`*/;
/*!50001 SET @saved_cs_client          = @@character_set_client */;
/*!50001 SET @saved_cs_results         = @@character_set_results */;
/*!50001 SET @saved_col_connection     = @@collation_connection */;
/*!50001 SET character_set_client      = utf8mb4 */;
/*!50001 SET character_set_results     = utf8mb4 */;
/*!50001 SET collation_connection      = utf8mb4_0900_ai_ci */;
/*!50001 CREATE ALGORITHM=UNDEFINED */
/*!50013 DEFINER=`root`@`localhost` SQL SECURITY DEFINER */
/*!50001 VIEW `kb_ai_conversation` AS select `kb_ai`.`conversation`.`id` AS `id`,`kb_ai`.`conversation`.`user_id` AS `user_id`,`kb_ai`.`conversation`.`user_name` AS `user_name`,`kb_ai`.`conversation`.`title` AS `title`,`kb_ai`.`conversation`.`model_name` AS `model_name`,`kb_ai`.`conversation`.`message_count` AS `message_count`,`kb_ai`.`conversation`.`created_at` AS `created_at`,`kb_ai`.`conversation`.`updated_at` AS `updated_at`,`kb_ai`.`conversation`.`deleted` AS `deleted` from `kb_ai`.`conversation` */;
/*!50001 SET character_set_client      = @saved_cs_client */;
/*!50001 SET character_set_results     = @saved_cs_results */;
/*!50001 SET collation_connection      = @saved_col_connection */;

--
-- Final view structure for view `kb_ai_message`
--

/*!50001 DROP VIEW IF EXISTS `kb_ai_message`*/;
/*!50001 SET @saved_cs_client          = @@character_set_client */;
/*!50001 SET @saved_cs_results         = @@character_set_results */;
/*!50001 SET @saved_col_connection     = @@collation_connection */;
/*!50001 SET character_set_client      = utf8mb4 */;
/*!50001 SET character_set_results     = utf8mb4 */;
/*!50001 SET collation_connection      = utf8mb4_0900_ai_ci */;
/*!50001 CREATE ALGORITHM=UNDEFINED */
/*!50013 DEFINER=`root`@`localhost` SQL SECURITY DEFINER */
/*!50001 VIEW `kb_ai_message` AS select `kb_ai`.`message`.`id` AS `id`,`kb_ai`.`message`.`conversation_id` AS `conversation_id`,`kb_ai`.`message`.`role` AS `role`,`kb_ai`.`message`.`content` AS `content`,`kb_ai`.`message`.`tokens` AS `tokens`,`kb_ai`.`message`.`created_at` AS `created_at`,`kb_ai`.`message`.`deleted` AS `deleted` from `kb_ai`.`message` */;
/*!50001 SET character_set_client      = @saved_cs_client */;
/*!50001 SET character_set_results     = @saved_cs_results */;
/*!50001 SET collation_connection      = @saved_col_connection */;

--
-- Final view structure for view `kb_category`
--

/*!50001 DROP VIEW IF EXISTS `kb_category`*/;
/*!50001 SET @saved_cs_client          = @@character_set_client */;
/*!50001 SET @saved_cs_results         = @@character_set_results */;
/*!50001 SET @saved_col_connection     = @@collation_connection */;
/*!50001 SET character_set_client      = utf8mb4 */;
/*!50001 SET character_set_results     = utf8mb4 */;
/*!50001 SET collation_connection      = utf8mb4_0900_ai_ci */;
/*!50001 CREATE ALGORITHM=UNDEFINED */
/*!50013 DEFINER=`root`@`localhost` SQL SECURITY DEFINER */
/*!50001 VIEW `kb_category` AS select `kb_document`.`kb_category`.`id` AS `id`,`kb_document`.`kb_category`.`category_name` AS `category_name`,`kb_document`.`kb_category`.`parent_id` AS `parent_id`,`kb_document`.`kb_category`.`category_icon` AS `category_icon`,`kb_document`.`kb_category`.`description` AS `description`,`kb_document`.`kb_category`.`sort` AS `sort`,`kb_document`.`kb_category`.`document_count` AS `document_count`,`kb_document`.`kb_category`.`status` AS `status`,`kb_document`.`kb_category`.`created_at` AS `created_at`,`kb_document`.`kb_category`.`updated_at` AS `updated_at`,`kb_document`.`kb_category`.`create_by` AS `create_by`,`kb_document`.`kb_category`.`update_by` AS `update_by`,`kb_document`.`kb_category`.`deleted` AS `deleted` from `kb_document`.`kb_category` */;
/*!50001 SET character_set_client      = @saved_cs_client */;
/*!50001 SET character_set_results     = @saved_cs_results */;
/*!50001 SET collation_connection      = @saved_col_connection */;

--
-- Final view structure for view `kb_comment`
--

/*!50001 DROP VIEW IF EXISTS `kb_comment`*/;
/*!50001 SET @saved_cs_client          = @@character_set_client */;
/*!50001 SET @saved_cs_results         = @@character_set_results */;
/*!50001 SET @saved_col_connection     = @@collation_connection */;
/*!50001 SET character_set_client      = utf8mb4 */;
/*!50001 SET character_set_results     = utf8mb4 */;
/*!50001 SET collation_connection      = utf8mb4_0900_ai_ci */;
/*!50001 CREATE ALGORITHM=UNDEFINED */
/*!50013 DEFINER=`root`@`localhost` SQL SECURITY DEFINER */
/*!50001 VIEW `kb_comment` AS select `kb_document`.`tb_comment`.`id` AS `id`,`kb_document`.`tb_comment`.`document_id` AS `document_id`,`kb_document`.`tb_comment`.`content` AS `content`,`kb_document`.`tb_comment`.`user_id` AS `user_id`,`kb_document`.`tb_comment`.`user_name` AS `user_name`,`kb_document`.`tb_comment`.`user_avatar` AS `user_avatar`,`kb_document`.`tb_comment`.`parent_id` AS `parent_id`,`kb_document`.`tb_comment`.`reply_to_id` AS `reply_to_id`,`kb_document`.`tb_comment`.`reply_to_name` AS `reply_to_name`,`kb_document`.`tb_comment`.`like_count` AS `like_count`,`kb_document`.`tb_comment`.`reply_count` AS `reply_count`,`kb_document`.`tb_comment`.`status` AS `status`,`kb_document`.`tb_comment`.`created_at` AS `created_at`,`kb_document`.`tb_comment`.`updated_at` AS `updated_at`,`kb_document`.`tb_comment`.`deleted` AS `deleted` from `kb_document`.`tb_comment` */;
/*!50001 SET character_set_client      = @saved_cs_client */;
/*!50001 SET character_set_results     = @saved_cs_results */;
/*!50001 SET collation_connection      = @saved_col_connection */;

--
-- Final view structure for view `kb_document`
--

/*!50001 DROP VIEW IF EXISTS `kb_document`*/;
/*!50001 SET @saved_cs_client          = @@character_set_client */;
/*!50001 SET @saved_cs_results         = @@character_set_results */;
/*!50001 SET @saved_col_connection     = @@collation_connection */;
/*!50001 SET character_set_client      = utf8mb4 */;
/*!50001 SET character_set_results     = utf8mb4 */;
/*!50001 SET collation_connection      = utf8mb4_0900_ai_ci */;
/*!50001 CREATE ALGORITHM=UNDEFINED */
/*!50013 DEFINER=`root`@`localhost` SQL SECURITY DEFINER */
/*!50001 VIEW `kb_document` AS select `kb_document`.`kb_document`.`id` AS `id`,`kb_document`.`kb_document`.`title` AS `title`,`kb_document`.`kb_document`.`author_id` AS `author_id`,`kb_document`.`kb_document`.`author_name` AS `author_name`,`kb_document`.`kb_document`.`category_id` AS `category_id`,`kb_document`.`kb_document`.`status` AS `status`,`kb_document`.`kb_document`.`view_count` AS `view_count`,`kb_document`.`kb_document`.`like_count` AS `like_count`,`kb_document`.`kb_document`.`favorite_count` AS `favorite_count`,`kb_document`.`kb_document`.`comment_count` AS `comment_count`,`kb_document`.`kb_document`.`is_public` AS `is_public`,`kb_document`.`kb_document`.`is_top` AS `is_top`,`kb_document`.`kb_document`.`is_recommend` AS `is_recommend`,`kb_document`.`kb_document`.`document_type` AS `document_type`,`kb_document`.`kb_document`.`source` AS `source`,`kb_document`.`kb_document`.`cover_image` AS `cover_image`,`kb_document`.`kb_document`.`summary` AS `summary`,`kb_document`.`kb_document`.`sort` AS `sort`,`kb_document`.`kb_document`.`allow_comment` AS `allow_comment`,`kb_document`.`kb_document`.`publish_time` AS `publish_time`,`kb_document`.`kb_document`.`created_at` AS `created_at`,`kb_document`.`kb_document`.`updated_at` AS `updated_at`,`kb_document`.`kb_document`.`create_by` AS `create_by`,`kb_document`.`kb_document`.`update_by` AS `update_by`,`kb_document`.`kb_document`.`deleted` AS `deleted` from `kb_document`.`kb_document` */;
/*!50001 SET character_set_client      = @saved_cs_client */;
/*!50001 SET character_set_results     = @saved_cs_results */;
/*!50001 SET collation_connection      = @saved_col_connection */;

--
-- Final view structure for view `kb_operation_log`
--

/*!50001 DROP VIEW IF EXISTS `kb_operation_log`*/;
/*!50001 SET @saved_cs_client          = @@character_set_client */;
/*!50001 SET @saved_cs_results         = @@character_set_results */;
/*!50001 SET @saved_col_connection     = @@collation_connection */;
/*!50001 SET character_set_client      = utf8mb4 */;
/*!50001 SET character_set_results     = utf8mb4 */;
/*!50001 SET collation_connection      = utf8mb4_0900_ai_ci */;
/*!50001 CREATE ALGORITHM=UNDEFINED */
/*!50013 DEFINER=`root`@`localhost` SQL SECURITY DEFINER */
/*!50001 VIEW `kb_operation_log` AS select `kb_foundation`.`kb_operation_log`.`id` AS `id`,`kb_foundation`.`kb_operation_log`.`module` AS `module`,`kb_foundation`.`kb_operation_log`.`operation_type` AS `operation_type`,`kb_foundation`.`kb_operation_log`.`operation_desc` AS `operation_desc`,`kb_foundation`.`kb_operation_log`.`request_method` AS `request_method`,`kb_foundation`.`kb_operation_log`.`request_url` AS `request_url`,`kb_foundation`.`kb_operation_log`.`request_params` AS `request_params`,`kb_foundation`.`kb_operation_log`.`response_result` AS `response_result`,`kb_foundation`.`kb_operation_log`.`user_id` AS `user_id`,`kb_foundation`.`kb_operation_log`.`username` AS `username`,`kb_foundation`.`kb_operation_log`.`ip_address` AS `ip_address`,`kb_foundation`.`kb_operation_log`.`location` AS `location`,`kb_foundation`.`kb_operation_log`.`user_agent` AS `user_agent`,`kb_foundation`.`kb_operation_log`.`execute_time` AS `execute_time`,`kb_foundation`.`kb_operation_log`.`status` AS `status`,`kb_foundation`.`kb_operation_log`.`error_msg` AS `error_msg`,`kb_foundation`.`kb_operation_log`.`created_at` AS `created_at`,`kb_foundation`.`kb_operation_log`.`updated_at` AS `updated_at`,`kb_foundation`.`kb_operation_log`.`create_by` AS `create_by`,`kb_foundation`.`kb_operation_log`.`update_by` AS `update_by`,`kb_foundation`.`kb_operation_log`.`deleted` AS `deleted` from `kb_foundation`.`kb_operation_log` */;
/*!50001 SET character_set_client      = @saved_cs_client */;
/*!50001 SET character_set_results     = @saved_cs_results */;
/*!50001 SET collation_connection      = @saved_col_connection */;

--
-- Final view structure for view `kb_user`
--

/*!50001 DROP VIEW IF EXISTS `kb_user`*/;
/*!50001 SET @saved_cs_client          = @@character_set_client */;
/*!50001 SET @saved_cs_results         = @@character_set_results */;
/*!50001 SET @saved_col_connection     = @@collation_connection */;
/*!50001 SET character_set_client      = utf8mb4 */;
/*!50001 SET character_set_results     = utf8mb4 */;
/*!50001 SET collation_connection      = utf8mb4_0900_ai_ci */;
/*!50001 CREATE ALGORITHM=UNDEFINED */
/*!50013 DEFINER=`root`@`localhost` SQL SECURITY DEFINER */
/*!50001 VIEW `kb_user` AS select `kb_user`.`kb_user`.`id` AS `id`,`kb_user`.`kb_user`.`username` AS `username`,`kb_user`.`kb_user`.`real_name` AS `real_name`,`kb_user`.`kb_user`.`avatar` AS `avatar`,`kb_user`.`kb_user`.`status` AS `status`,`kb_user`.`kb_user`.`email` AS `email`,`kb_user`.`kb_user`.`phone` AS `phone`,`kb_user`.`kb_user`.`department` AS `department`,`kb_user`.`kb_user`.`position` AS `position`,`kb_user`.`kb_user`.`last_login_time` AS `last_login_time`,`kb_user`.`kb_user`.`created_at` AS `created_at`,`kb_user`.`kb_user`.`updated_at` AS `updated_at`,`kb_user`.`kb_user`.`deleted` AS `deleted` from `kb_user`.`kb_user` */;
/*!50001 SET character_set_client      = @saved_cs_client */;
/*!50001 SET character_set_results     = @saved_cs_results */;
/*!50001 SET collation_connection      = @saved_col_connection */;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-06-23 17:48:12
