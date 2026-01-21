mysqldump: [Warning] Using a password on the command line interface can be insecure.
-- MySQL dump 10.13  Distrib 8.0.26, for Win64 (x86_64)
--
-- Host: localhost    Database: teaching_employment_platform
-- ------------------------------------------------------
-- Server version	8.0.26

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
-- Table structure for table `t_classroom`
--

DROP TABLE IF EXISTS `t_classroom`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `t_classroom` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '教室ID',
  `school_id` bigint DEFAULT NULL COMMENT '所属学校ID',
  `building` varchar(50) DEFAULT NULL COMMENT '鎵?湪妤兼爧',
  `floor` varchar(20) DEFAULT NULL COMMENT '妤煎眰',
  `classroom_no` varchar(50) DEFAULT NULL COMMENT '鏁欏?缂栧彿',
  `classroom_name` varchar(200) DEFAULT NULL COMMENT '教室名称',
  `capacity` int DEFAULT NULL COMMENT '容纳人数',
  `classroom_type` varchar(50) DEFAULT NULL COMMENT '鏁欏?绫诲瀷',
  `has_projector` int DEFAULT '0' COMMENT '鏄?惁鏈夋姇褰变华锛?-鏄?0-鍚',
  `has_computer` int DEFAULT '0' COMMENT '鏄?惁鏈夌數鑴戯細1-鏄?0-鍚',
  `has_multimedia` int DEFAULT '0' COMMENT '鏄?惁澶氬獟浣撴暀瀹わ細1-鏄?0-鍚',
  `equipment` text COMMENT '设备清单',
  `status` int DEFAULT '1' COMMENT '状态：1-可用 0-不可用',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_school_id` (`school_id`),
  KEY `idx_classroom_no` (`classroom_no`),
  CONSTRAINT `t_classroom_ibfk_1` FOREIGN KEY (`school_id`) REFERENCES `t_school` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=71 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='教室表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `t_classroom`
--

LOCK TABLES `t_classroom` WRITE;
/*!40000 ALTER TABLE `t_classroom` DISABLE KEYS */;
INSERT INTO `t_classroom` VALUES (1,1,'信息楼','F1','101','信息楼-101',60,'多媒体教室',0,0,0,NULL,1,'2026-01-18 15:19:19','2026-01-18 19:45:56'),(2,1,'信息楼','F1','102','信息楼-102',60,'多媒体教室',0,0,0,NULL,1,'2026-01-18 15:19:19','2026-01-18 19:45:56'),(3,2,'理科楼','F1','301','理科楼-301',80,'阶梯教室',0,0,0,NULL,1,'2026-01-18 15:19:19','2026-01-18 19:45:56'),(37,1,'A栋教学楼','1F','A101','A101多媒体教室',60,'多媒体教室',1,0,1,'投影仪：爱普生EB-C760X，音响：环绕立体声，空调：格力2匹',1,'2026-01-18 19:50:08','2026-01-18 19:50:08'),(38,1,'A栋教学楼','1F','A102','A102多媒体教室',60,'多媒体教室',1,0,1,'投影仪：爱普生EB-C760X，音响：环绕立体声，空调：格力2匹',1,'2026-01-18 19:50:08','2026-01-18 19:50:08'),(39,1,'A栋教学楼','1F','A103','A103普通教室',50,'普通教室',0,0,0,'空调：格力1.5匹，白板：4米长',1,'2026-01-18 19:50:08','2026-01-18 19:50:08'),(40,1,'A栋教学楼','1F','A104','A104普通教室',50,'普通教室',0,0,0,'空调：格力1.5匹，白板：4米长',1,'2026-01-18 19:50:08','2026-01-18 19:50:08'),(41,1,'A栋教学楼','2F','A201','A201阶梯教室',120,'阶梯教室',1,0,1,'投影仪：索尼VPL-EX470，音响：专业级扩音系统，麦克风：无线麦2个，空调：大金3匹',1,'2026-01-18 19:50:08','2026-01-18 19:50:08'),(42,1,'A栋教学楼','2F','A202','A202阶梯教室',120,'阶梯教室',1,0,1,'投影仪：索尼VPL-EX470，音响：专业级扩音系统，麦克风：无线麦2个，空调：大金3匹',1,'2026-01-18 19:50:08','2026-01-18 19:50:08'),(43,1,'A栋教学楼','2F','A203','A203实验室',40,'实验室',1,0,1,'投影仪：爱普生，实验台：40个，空调：格力2匹',1,'2026-01-18 19:50:08','2026-01-18 19:50:08'),(44,1,'A栋教学楼','2F','A204','A204实验室',40,'实验室',1,0,1,'投影仪：爱普生，实验台：40个，空调：格力2匹',1,'2026-01-18 19:50:08','2026-01-18 19:50:08'),(45,1,'A栋教学楼','3F','A301','A301计算机教室',60,'计算机教室',1,1,1,'电脑：联想启天M540（60台），投影仪：爱普生，空调：格力3匹',1,'2026-01-18 19:50:08','2026-01-18 19:50:08'),(46,1,'A栋教学楼','3F','A302','A302计算机教室',60,'计算机教室',1,1,1,'电脑：联想启天M540（60台），投影仪：爱普生，空调：格力3匹',1,'2026-01-18 19:50:08','2026-01-18 19:50:08'),(47,1,'A栋教学楼','3F','A303','A303语音教室',50,'语音教室',1,1,1,'电脑：50台，语音设备：专业语音教学系统，耳机：50个，投影仪：爱普生',1,'2026-01-18 19:50:08','2026-01-18 19:50:08'),(48,1,'A栋教学楼','3F','A304','A304多媒体教室',60,'多媒体教室',1,0,1,'投影仪：爱普生EB-C760X，音响：环绕立体声，空调：格力2匹',1,'2026-01-18 19:50:08','2026-01-18 19:50:08'),(49,1,'A栋教学楼','4F','A401','A401普通教室',50,'普通教室',0,0,0,'空调：格力1.5匹，白板：4米长',1,'2026-01-18 19:50:08','2026-01-18 19:50:08'),(50,1,'A栋教学楼','4F','A402','A402普通教室',50,'普通教室',0,0,0,'空调：格力1.5匹，白板：4米长',1,'2026-01-18 19:50:08','2026-01-18 19:50:08'),(51,1,'A栋教学楼','4F','A403','A403普通教室',50,'普通教室',0,0,0,'空调：格力1.5匹，白板：4米长',1,'2026-01-18 19:50:08','2026-01-18 19:50:08'),(52,2,'B栋教学楼','1F','B101','B101多媒体教室',80,'多媒体教室',1,0,1,'投影仪：明基EX7800，音响：JBL，空调：格力3匹',1,'2026-01-18 19:50:08','2026-01-18 19:50:08'),(53,2,'B栋教学楼','1F','B102','B102多媒体教室',80,'多媒体教室',1,0,1,'投影仪：明基EX7800，音响：JBL，空调：格力3匹',1,'2026-01-18 19:50:08','2026-01-18 19:50:08'),(54,2,'B栋教学楼','1F','B103','B103普通教室',60,'普通教室',0,0,0,'空调：格力2匹，白板：5米长',1,'2026-01-18 19:50:08','2026-01-18 19:50:08'),(55,2,'B栋教学楼','1F','B104','B104普通教室',60,'普通教室',0,0,0,'空调：格力2匹，白板：5米长',1,'2026-01-18 19:50:08','2026-01-18 19:50:08'),(56,2,'B栋教学楼','2F','B201','B201阶梯教室',150,'阶梯教室',1,0,1,'投影仪：巴可RLDMi，音响：专业级音响系统，麦克风：U段无线麦4个，空调：大金5匹',1,'2026-01-18 19:50:08','2026-01-18 19:50:08'),(57,2,'B栋教学楼','2F','B202','B202阶梯教室',150,'阶梯教室',1,0,1,'投影仪：巴可RLDMi，音响：专业级音响系统，麦克风：U段无线麦4个，空调：大金5匹',1,'2026-01-18 19:50:08','2026-01-18 19:50:08'),(58,2,'B栋教学楼','3F','B301','B301计算机教室',80,'计算机教室',1,1,1,'电脑：戴尔OptiPlex 7090（80台），投影仪：明基，空调：格力5匹',1,'2026-01-18 19:50:08','2026-01-18 19:50:08'),(59,2,'B栋教学楼','3F','B302','B302计算机教室',80,'计算机教室',1,1,1,'电脑：戴尔OptiPlex 7090（80台），投影仪：明基，空调：格力5匹',1,'2026-01-18 19:50:08','2026-01-18 19:50:08'),(60,2,'B栋教学楼','3F','B303','B303语音教室',60,'语音教室',1,1,1,'电脑：60台，语音设备：新视野语音教学系统，耳机：60个，投影仪：明基',1,'2026-01-18 19:50:08','2026-01-18 19:50:08'),(61,2,'B栋教学楼','3F','B304','B304实验室',50,'实验室',1,0,1,'投影仪：明基，实验台：50个，空调：格力3匹',1,'2026-01-18 19:50:08','2026-01-18 19:50:08'),(62,2,'C栋教学楼','1F','C101','C101多媒体教室',70,'多媒体教室',1,0,1,'投影仪：松下PT-X400，音响：先锋，空调：大金2.5匹',1,'2026-01-18 19:50:08','2026-01-18 19:50:08'),(63,2,'C栋教学楼','1F','C102','C102普通教室',55,'普通教室',0,0,0,'空调：大金2匹，白板：4.5米长',1,'2026-01-18 19:50:08','2026-01-18 19:50:08'),(64,2,'C栋教学楼','1F','C103','C103普通教室',55,'普通教室',0,0,0,'空调：大金2匹，白板：4.5米长',1,'2026-01-18 19:50:08','2026-01-18 19:50:08'),(65,2,'C栋教学楼','2F','C201','C201阶梯教室',130,'阶梯教室',1,0,1,'投影仪：NEC M420SK，音响：雅马哈，麦克风：舒尔无线麦，空调：大金4匹',1,'2026-01-18 19:50:08','2026-01-18 19:50:08'),(66,2,'C栋教学楼','2F','C202','C202实验室',45,'实验室',1,0,1,'投影仪：松下，实验台：45个，空调：大金2.5匹',1,'2026-01-18 19:50:08','2026-01-18 19:50:08'),(67,2,'C栋教学楼','3F','C301','C301计算机教室',70,'计算机教室',1,1,1,'电脑：惠普ProDesk 600（70台），投影仪：松下，空调：大金4匹',1,'2026-01-18 19:50:08','2026-01-18 19:50:08'),(68,2,'C栋教学楼','3F','C302','C302语音教室',55,'语音教室',1,1,1,'电脑：55台，语音设备：蓝鸽语音系统，耳机：55个，投影仪：松下',1,'2026-01-18 19:50:08','2026-01-18 19:50:08'),(69,2,'C栋教学楼','3F','C303','C303多媒体教室',70,'多媒体教室',1,0,1,'投影仪：松下PT-X400，音响：先锋，空调：大金2.5匹',1,'2026-01-18 19:50:08','2026-01-18 19:50:08'),(70,1,'D栋教学楼','1F','D101','D101测试教室',50,'普通教室',1,1,1,'测试教室',1,'2026-01-18 20:20:35','2026-01-18 20:20:35');
/*!40000 ALTER TABLE `t_classroom` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `t_company`
--

DROP TABLE IF EXISTS `t_company`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `t_company` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '企业ID',
  `company_name` varchar(100) NOT NULL COMMENT '企业名称',
  `company_code` varchar(50) DEFAULT NULL COMMENT '企业编码',
  `credit_code` varchar(50) DEFAULT NULL,
  `short_name` varchar(100) DEFAULT NULL,
  `logo` varchar(255) DEFAULT NULL,
  `industry` varchar(50) DEFAULT NULL COMMENT '行业',
  `scale` varchar(50) DEFAULT NULL COMMENT '规模',
  `stage` varchar(50) DEFAULT NULL,
  `location` varchar(200) DEFAULT NULL COMMENT '地址',
  `city` varchar(100) DEFAULT NULL,
  `address` text,
  `website` varchar(255) DEFAULT NULL COMMENT '网站',
  `description` text COMMENT '企业简介',
  `benefits` text,
  `contact_person` varchar(50) DEFAULT NULL COMMENT '联系人',
  `contact_position` varchar(100) DEFAULT NULL,
  `contact_phone` varchar(20) DEFAULT NULL COMMENT '联系电话',
  `contact_email` varchar(100) DEFAULT NULL COMMENT '联系邮箱',
  `verify_status` varchar(50) DEFAULT NULL,
  `verify_time` datetime DEFAULT NULL,
  `reject_reason` text,
  `status` int DEFAULT '1',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `company_code` (`company_code`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='企业表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `t_company`
--

LOCK TABLES `t_company` WRITE;
/*!40000 ALTER TABLE `t_company` DISABLE KEYS */;
INSERT INTO `t_company` VALUES (1,'阿里巴巴集团',NULL,NULL,'阿里巴巴',NULL,'互联网','1000人以上','listing','杭州','测试','测试','https://www.alibaba.com','测试','测试','张HR','测试','18059224290','111@111.com',NULL,NULL,NULL,1,'2026-01-18 15:19:19','2026-01-20 18:00:56'),(2,'腾讯科技',NULL,NULL,NULL,NULL,'互联网','大型企业',NULL,'深圳',NULL,NULL,'https://www.tencent.com',NULL,NULL,'李HR',NULL,'0755-12345678',NULL,NULL,NULL,NULL,1,'2026-01-18 15:19:19','2026-01-19 10:37:10'),(3,'字节跳动',NULL,NULL,NULL,NULL,'互联网','大型企业',NULL,'北京',NULL,NULL,'https://www.bytedance.com',NULL,NULL,'王HR',NULL,'010-12345678',NULL,NULL,NULL,NULL,1,'2026-01-18 15:19:19','2026-01-19 10:37:10'),(4,'华为技术',NULL,NULL,NULL,NULL,'科技','大型企业',NULL,'深圳',NULL,NULL,'https://www.huawei.com',NULL,NULL,'赵HR',NULL,'0755-87654321',NULL,NULL,NULL,NULL,1,'2026-01-18 15:19:19','2026-01-19 10:37:10'),(5,'美团',NULL,NULL,NULL,NULL,'互联网','大型企业',NULL,'北京',NULL,NULL,'https://www.meituan.com',NULL,NULL,'钱HR',NULL,'010-87654321',NULL,NULL,NULL,NULL,1,'2026-01-18 15:19:19','2026-01-19 10:37:10'),(6,'测试企业',NULL,'123456789012345678','测试','11','互联网/IT','500人以上','listing',NULL,'北京','北京市朝阳区','https://test.com','111','111',NULL,'经理','13800000000','test@test.com','approved','2026-01-19 10:56:45',NULL,1,'2026-01-19 10:56:20','2026-01-19 10:56:20');
/*!40000 ALTER TABLE `t_company` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `t_course`
--

DROP TABLE IF EXISTS `t_course`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `t_course` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '课程ID',
  `course_code` varchar(50) NOT NULL COMMENT '课程编码',
  `course_name` varchar(100) NOT NULL COMMENT '课程名称',
  `course_type` varchar(50) DEFAULT NULL COMMENT '课程类型',
  `school_id` bigint DEFAULT NULL,
  `credits` decimal(3,1) DEFAULT NULL COMMENT '学分',
  `hours` int DEFAULT NULL COMMENT '学时',
  `start_date` date DEFAULT NULL,
  `end_date` date DEFAULT NULL,
  `max_students` int DEFAULT '50',
  `current_students` int DEFAULT '0',
  `description` text COMMENT '课程描述',
  `teacher_id` bigint DEFAULT NULL COMMENT '授课教师ID',
  `classroom_id` bigint DEFAULT NULL,
  `status` varchar(20) DEFAULT 'active' COMMENT '状态：active-激活 inactive-停用',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `course_code` (`course_code`),
  KEY `teacher_id` (`teacher_id`),
  CONSTRAINT `t_course_ibfk_1` FOREIGN KEY (`teacher_id`) REFERENCES `t_teacher` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='课程表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `t_course`
--

LOCK TABLES `t_course` WRITE;
/*!40000 ALTER TABLE `t_course` DISABLE KEYS */;
INSERT INTO `t_course` VALUES (1,'CS101','Java程序设计','专业必修',NULL,4.0,64,NULL,NULL,50,0,'学习基本数据结构',1,NULL,'active','2026-01-18 15:19:19','2026-01-19 16:29:58'),(2,'CS102','Web前端开发','专业必修',NULL,3.0,48,NULL,NULL,50,0,'学习网络原理',1,NULL,'active','2026-01-18 15:19:19','2026-01-19 16:29:58'),(3,'CS103','数据库原理','专业必修',NULL,4.0,64,NULL,NULL,50,0,'学习操作系统设计',1,NULL,'active','2026-01-18 15:19:19','2026-01-19 16:29:58'),(4,'CS104','数据结构与算法','专业必修',NULL,3.0,48,NULL,NULL,50,0,'学习软件开发方法',2,NULL,'completed','2026-01-18 15:19:19','2026-01-19 17:03:42'),(5,'CS105','Spring框架开发','专业选修',NULL,3.0,48,NULL,NULL,50,0,'学习机器学习算法',1,NULL,'completed','2026-01-18 15:19:19','2026-01-19 17:03:42'),(6,'111','111','普通课程',1,3.0,48,'2026-01-01','2026-01-30',50,0,'111111',4,1,'pending','2026-01-19 10:08:50','2026-01-19 10:08:50');
/*!40000 ALTER TABLE `t_course` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `t_course_record`
--

DROP TABLE IF EXISTS `t_course_record`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `t_course_record` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '记录ID',
  `course_id` bigint NOT NULL COMMENT '课程ID',
  `student_id` bigint NOT NULL COMMENT '学员ID',
  `week_number` int DEFAULT NULL COMMENT '周次',
  `content` text COMMENT '课程内容',
  `homework` text COMMENT '作业',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `course_id` (`course_id`),
  KEY `student_id` (`student_id`),
  CONSTRAINT `t_course_record_ibfk_1` FOREIGN KEY (`course_id`) REFERENCES `t_course` (`id`),
  CONSTRAINT `t_course_record_ibfk_2` FOREIGN KEY (`student_id`) REFERENCES `t_student` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='课程记录表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `t_course_record`
--

LOCK TABLES `t_course_record` WRITE;
/*!40000 ALTER TABLE `t_course_record` DISABLE KEYS */;
/*!40000 ALTER TABLE `t_course_record` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `t_course_student`
--

DROP TABLE IF EXISTS `t_course_student`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `t_course_student` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `course_id` bigint NOT NULL COMMENT '课程ID',
  `student_id` bigint NOT NULL COMMENT '学员ID',
  `enrollment_date` date DEFAULT NULL COMMENT '选课日期',
  `status` int DEFAULT '1' COMMENT '鐘舵?锛?-鍦ㄨ? 0-宸查?璇',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `progress` decimal(5,2) DEFAULT '0.00' COMMENT '瀛︿範杩涘害(%)',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_course_student` (`course_id`,`student_id`),
  KEY `student_id` (`student_id`),
  CONSTRAINT `t_course_student_ibfk_1` FOREIGN KEY (`course_id`) REFERENCES `t_course` (`id`),
  CONSTRAINT `t_course_student_ibfk_2` FOREIGN KEY (`student_id`) REFERENCES `t_student` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=27 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='课程学生关联表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `t_course_student`
--

LOCK TABLES `t_course_student` WRITE;
/*!40000 ALTER TABLE `t_course_student` DISABLE KEYS */;
INSERT INTO `t_course_student` VALUES (11,1,1,'2026-01-01',1,'2026-01-19 16:24:22','2026-01-19 16:24:22',75.50),(12,2,1,'2026-01-01',1,'2026-01-19 16:24:22','2026-01-19 16:24:22',60.00),(13,3,1,'2026-01-01',1,'2026-01-19 16:24:22','2026-01-19 16:24:22',45.00),(14,4,1,'2026-01-01',1,'2026-01-19 16:24:22','2026-01-19 17:03:42',100.00),(15,5,1,'2026-01-01',1,'2026-01-19 16:24:22','2026-01-19 17:03:42',100.00);
/*!40000 ALTER TABLE `t_course_student` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `t_daily_report`
--

DROP TABLE IF EXISTS `t_daily_report`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `t_daily_report` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '日报ID',
  `student_id` bigint NOT NULL COMMENT '学员ID',
  `report_date` date NOT NULL COMMENT '日报日期',
  `today_content` text COMMENT '今日学习内容',
  `today_progress` text COMMENT '今日进度',
  `problems` text COMMENT '遇到的问题',
  `tomorrow_plan` text COMMENT '明日计划',
  `study_hours` decimal(4,1) DEFAULT NULL COMMENT '学习时长（小时）',
  `code_lines` int DEFAULT NULL,
  `attachment_url` varchar(255) DEFAULT NULL COMMENT '附件URL',
  `status` varchar(20) DEFAULT 'draft' COMMENT '状态：draft-草稿 submitted-已提交 reviewed-已批阅',
  `submit_time` datetime DEFAULT NULL,
  `review_time` datetime DEFAULT NULL,
  `teacher_comment` text COMMENT '教师批阅',
  `rating` int DEFAULT NULL COMMENT '评分（1-5）',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_student_date` (`student_id`,`report_date`),
  CONSTRAINT `t_daily_report_ibfk_1` FOREIGN KEY (`student_id`) REFERENCES `t_student` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='日报表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `t_daily_report`
--

LOCK TABLES `t_daily_report` WRITE;
/*!40000 ALTER TABLE `t_daily_report` DISABLE KEYS */;
INSERT INTO `t_daily_report` VALUES (2,2,'2025-01-15','学习了TCP协议','理解了三次握手','四次挥手要复习','复习TCP',5.5,NULL,NULL,'reviewed',NULL,NULL,'掌握不错',4,'2026-01-18 15:19:19','2026-01-18 15:19:19'),(3,1,'2026-01-19','今天学习了python','很好','没有','继续',5.0,NULL,'','reviewed',NULL,'2026-01-19 18:53:03','很好',3,'2026-01-19 16:26:43','2026-01-19 16:26:43');
/*!40000 ALTER TABLE `t_daily_report` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `t_education`
--

DROP TABLE IF EXISTS `t_education`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `t_education` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `school_name` varchar(100) NOT NULL COMMENT '学校名称',
  `major` varchar(100) NOT NULL COMMENT '专业',
  `degree` varchar(20) NOT NULL COMMENT '学历(本科/硕士/博士/专科)',
  `start_date` date NOT NULL COMMENT '开始时间',
  `end_date` date DEFAULT NULL COMMENT '结束时间(空表示至今)',
  `description` text COMMENT '描述/备注',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_user_dates` (`user_id`,`start_date` DESC),
  CONSTRAINT `t_education_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `t_user` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='教育经历表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `t_education`
--

LOCK TABLES `t_education` WRITE;
/*!40000 ALTER TABLE `t_education` DISABLE KEYS */;
INSERT INTO `t_education` VALUES (1,7,'清华大学','计算机科学与技术','本科','2021-09-01','2025-06-30','GPA 3.8','2026-01-18 15:19:19','2026-01-18 15:19:19'),(2,7,'北京四中','理科','高中','2018-09-01','2021-06-30','理科班','2026-01-18 15:19:19','2026-01-18 15:19:19'),(3,8,'清华大学','软件工程','本科','2021-09-01','2025-06-30','GPA 3.6','2026-01-18 15:19:19','2026-01-18 15:19:19'),(4,9,'清华大学','计算机科学与技术','本科','2021-09-01','2025-06-30','主修AI','2026-01-18 15:19:19','2026-01-18 15:19:19'),(5,10,'北京大学','人工智能','硕士','2021-09-01','2024-06-30','机器学习方向','2026-01-18 15:19:19','2026-01-18 15:19:19');
/*!40000 ALTER TABLE `t_education` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `t_exam`
--

DROP TABLE IF EXISTS `t_exam`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `t_exam` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '考试ID',
  `course_id` bigint DEFAULT NULL COMMENT '课程ID（可选，为空表示独立考试）',
  `exam_type` varchar(50) DEFAULT NULL,
  `exam_name` varchar(200) DEFAULT NULL,
  `description` text COMMENT '考试说明',
  `duration` int DEFAULT NULL COMMENT '考试时长（分钟）',
  `start_time` datetime DEFAULT NULL COMMENT '开始时间',
  `end_time` datetime DEFAULT NULL COMMENT '结束时间',
  `total_score` decimal(5,2) DEFAULT NULL,
  `passing_score` int DEFAULT NULL,
  `status` varchar(20) DEFAULT 'draft' COMMENT '状态：draft-草稿 published-已发布 started-进行中 ended-已结束',
  `is_random` int DEFAULT '0',
  `question_count` int DEFAULT NULL,
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `course_id` (`course_id`),
  CONSTRAINT `t_exam_ibfk_1` FOREIGN KEY (`course_id`) REFERENCES `t_course` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='考试表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `t_exam`
--

LOCK TABLES `t_exam` WRITE;
/*!40000 ALTER TABLE `t_exam` DISABLE KEYS */;
/*!40000 ALTER TABLE `t_exam` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `t_exam_question`
--

DROP TABLE IF EXISTS `t_exam_question`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `t_exam_question` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '鍏宠仈ID',
  `exam_id` bigint NOT NULL COMMENT '璇曞嵎ID',
  `question_id` bigint NOT NULL COMMENT '棰樼洰ID',
  `question_score` decimal(5,2) NOT NULL COMMENT '棰樼洰鍒嗗?',
  `sort_order` int NOT NULL COMMENT '鎺掑簭',
  `generate_type` varchar(20) DEFAULT 'manual' COMMENT '鐢熸垚鏂瑰紡锛歮anual-鎵嬪姩 auto-鑷?姩',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '鍒涘缓鏃堕棿',
  PRIMARY KEY (`id`),
  KEY `idx_exam_id` (`exam_id`),
  KEY `idx_question_id` (`question_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='璇曞嵎棰樼洰鍏宠仈琛';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `t_exam_question`
--

LOCK TABLES `t_exam_question` WRITE;
/*!40000 ALTER TABLE `t_exam_question` DISABLE KEYS */;
/*!40000 ALTER TABLE `t_exam_question` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `t_exam_record`
--

DROP TABLE IF EXISTS `t_exam_record`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `t_exam_record` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '璁板綍ID',
  `exam_id` bigint NOT NULL COMMENT '璇曞嵎ID',
  `student_id` bigint NOT NULL COMMENT '瀛︾敓ID',
  `start_time` datetime NOT NULL COMMENT '寮??鏃堕棿',
  `submit_time` datetime DEFAULT NULL COMMENT '鎻愪氦鏃堕棿',
  `objective_score` decimal(5,2) DEFAULT '0.00' COMMENT '瀹㈣?棰樺緱鍒',
  `subjective_score` decimal(5,2) DEFAULT NULL COMMENT '涓昏?棰樺緱鍒嗭紙鎵规敼鍚庯級',
  `total_score` decimal(5,2) DEFAULT NULL COMMENT '鎬诲垎',
  `exam_status` varchar(20) DEFAULT 'in_progress' COMMENT '鑰冭瘯鐘舵?锛歩n_progress-杩涜?涓?submitted-宸叉彁浜?graded-宸叉壒鏀',
  `question_snapshot` json DEFAULT NULL COMMENT '棰樼洰蹇?収锛堜繚瀛樺?鐢熺湅鍒扮殑棰樼洰椤哄簭锛',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '鍒涘缓鏃堕棿',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '鏇存柊鏃堕棿',
  PRIMARY KEY (`id`),
  KEY `idx_exam_id` (`exam_id`),
  KEY `idx_student_id` (`student_id`),
  KEY `idx_exam_status` (`exam_status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='鑰冭瘯璁板綍琛';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `t_exam_record`
--

LOCK TABLES `t_exam_record` WRITE;
/*!40000 ALTER TABLE `t_exam_record` DISABLE KEYS */;
/*!40000 ALTER TABLE `t_exam_record` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `t_guidance_record`
--

DROP TABLE IF EXISTS `t_guidance_record`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `t_guidance_record` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '涓婚敭ID',
  `student_id` bigint NOT NULL COMMENT '瀛﹀憳ID',
  `teacher_id` bigint NOT NULL COMMENT '鏁欏笀ID',
  `guidance_type` varchar(50) NOT NULL COMMENT '鎸囧?绫诲瀷锛歝areer_planning-鑱屼笟瑙勫垝锛宺esume_guidance-绠?巻鎸囧?锛宨nterview_guidance-闈㈣瘯鎸囧?锛宻kill_improvement-鎶?兘鎻愬崌锛宲sychological_counseling-蹇冪悊杈呭?锛宱ther-鍏朵粬',
  `content` text NOT NULL COMMENT '鎸囧?鍐呭?',
  `next_action` text COMMENT '鍚庣画璺熻繘璁″垝',
  `guidance_date` datetime NOT NULL COMMENT '鎸囧?鏃堕棿',
  `location` varchar(100) DEFAULT NULL COMMENT '鎸囧?鍦扮偣锛堢嚎涓?绾夸笅锛',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '鍒涘缓鏃堕棿',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '鏇存柊鏃堕棿',
  PRIMARY KEY (`id`),
  KEY `idx_student_id` (`student_id`),
  KEY `idx_teacher_id` (`teacher_id`),
  KEY `idx_guidance_type` (`guidance_type`),
  KEY `idx_guidance_date` (`guidance_date`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='鎸囧?璁板綍琛';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `t_guidance_record`
--

LOCK TABLES `t_guidance_record` WRITE;
/*!40000 ALTER TABLE `t_guidance_record` DISABLE KEYS */;
INSERT INTO `t_guidance_record` VALUES (3,1,1,'career_planning','测试','测试','2026-01-20 05:23:00','线上','2026-01-20 13:24:23','2026-01-20 13:24:23');
/*!40000 ALTER TABLE `t_guidance_record` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `t_homework`
--

DROP TABLE IF EXISTS `t_homework`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `t_homework` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '作业ID',
  `course_id` bigint NOT NULL COMMENT '课程ID',
  `teacher_id` bigint DEFAULT NULL COMMENT '鏁欏笀ID',
  `title` varchar(200) NOT NULL COMMENT '作业标题',
  `description` text COMMENT '浣滀笟鎻忚堪',
  `content` text COMMENT '作业内容',
  `max_score` decimal(5,2) DEFAULT '100.00' COMMENT '婊″垎',
  `homework_type` varchar(50) DEFAULT 'assignment' COMMENT '浣滀笟绫诲瀷锛歛ssignment-浣滀笟 project-椤圭洰',
  `status` varchar(20) DEFAULT 'published' COMMENT '鐘舵?锛歞raft-鑽夌? published-宸插彂甯?closed-宸叉埅姝',
  `attachment_url` varchar(255) DEFAULT NULL COMMENT '附件URL',
  `deadline` datetime DEFAULT NULL COMMENT '截止时间',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `course_id` (`course_id`),
  CONSTRAINT `t_homework_ibfk_1` FOREIGN KEY (`course_id`) REFERENCES `t_course` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=14 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='作业表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `t_homework`
--

LOCK TABLES `t_homework` WRITE;
/*!40000 ALTER TABLE `t_homework` DISABLE KEYS */;
INSERT INTO `t_homework` VALUES (1,1,1,'Java基础语法练习','完成第1-3章的课后练习题，包括变量声明、条件语句、循环语句等基础语法练习','完成第1-3章的课后练习题，包括变量声明、条件语句、循环语句等基础语法练习',100.00,'assignment','published',NULL,'2025-01-25 23:59:59','2026-01-18 15:19:19','2026-01-19 16:45:52'),(2,2,1,'面向对象编程实践','设计一个学生类，包含姓名、学号、年龄等属性，实现构造方法、getter/setter方法和toString方法','设计一个学生类，包含姓名、学号、年龄等属性，实现构造方法、getter/setter方法和toString方法',100.00,'assignment','published',NULL,'2025-01-28 23:59:59','2026-01-18 15:19:19','2026-01-19 16:45:52'),(3,1,1,'集合框架应用','使用ArrayList、HashMap等集合类实现一个简单的学生成绩管理系统','使用ArrayList、HashMap等集合类实现一个简单的学生成绩管理系统',100.00,'project','published',NULL,'2026-02-10 23:59:59','2026-01-15 10:00:00','2026-01-19 16:45:52'),(4,2,1,'HTML页面设计','设计一个个人主页，包含个人信息、联系方式、照片等模块','设计一个个人主页，包含个人信息、联系方式、照片等模块',100.00,'assignment','published',NULL,'2026-01-28 23:59:59','2026-01-10 10:00:00','2026-01-19 16:45:52'),(5,2,1,'CSS样式练习','为HTML页面添加样式，实现响应式布局，适配不同屏幕尺寸','为HTML页面添加样式，实现响应式布局，适配不同屏幕尺寸',100.00,'assignment','published',NULL,'2026-02-05 23:59:59','2026-01-14 10:00:00','2026-01-19 16:45:52'),(6,2,1,'JavaScript交互开发','使用JavaScript实现表单验证、轮播图等交互功能','使用JavaScript实现表单验证、轮播图等交互功能',100.00,'project','published',NULL,'2026-02-15 23:59:59','2026-01-18 10:00:00','2026-01-19 16:45:52'),(7,3,1,'数据库设计练习','设计一个图书管理系统的数据库，包含书籍表、读者表、借阅记录表等','设计一个图书管理系统的数据库，包含书籍表、读者表、借阅记录表等',100.00,'assignment','published',NULL,'2026-01-30 23:59:59','2026-01-11 10:00:00','2026-01-19 16:45:52'),(8,3,1,'SQL查询练习','编写复杂的SQL查询语句，包括多表连接、子查询、聚合函数等','编写复杂的SQL查询语句，包括多表连接、子查询、聚合函数等',100.00,'assignment','published',NULL,'2026-02-08 23:59:59','2026-01-16 10:00:00','2026-01-19 16:45:52'),(9,4,1,'链表实现','使用Java实现单链表，包括插入、删除、查找等操作','使用Java实现单链表，包括插入、删除、查找等操作',100.00,'project','published',NULL,'2026-02-12 23:59:59','2026-01-13 10:00:00','2026-01-19 16:45:52'),(10,4,1,'排序算法比较','实现冒泡排序、快速排序、归并排序等算法，并比较性能','实现冒泡排序、快速排序、归并排序等算法，并比较性能',100.00,'project','published',NULL,'2026-02-20 23:59:59','2026-01-17 10:00:00','2026-01-19 16:45:52'),(11,5,1,'Spring IOC练习','使用Spring的IOC容器实现一个简单的用户管理系统','使用Spring的IOC容器实现一个简单的用户管理系统',100.00,'project','published',NULL,'2026-02-18 23:59:59','2026-01-19 10:00:00','2026-01-19 16:45:52'),(12,5,1,'Spring MVC实战','使用Spring MVC开发一个RESTful API接口','使用Spring MVC开发一个RESTful API接口',100.00,'project','published',NULL,'2026-02-25 23:59:59','2026-01-19 10:00:00','2026-01-19 16:45:52'),(13,5,1,'测试作业','测试',NULL,100.00,'assignment','published',NULL,'2026-01-31 00:00:00','2026-01-19 19:06:33','2026-01-19 19:06:33');
/*!40000 ALTER TABLE `t_homework` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `t_homework_submission`
--

DROP TABLE IF EXISTS `t_homework_submission`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `t_homework_submission` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '提交ID',
  `homework_id` bigint NOT NULL COMMENT '作业ID',
  `student_id` bigint NOT NULL COMMENT '学员ID',
  `content` text COMMENT '提交内容',
  `attachment_url` varchar(255) DEFAULT NULL COMMENT '附件URL',
  `submit_time` datetime DEFAULT NULL COMMENT '提交时间',
  `score` decimal(5,2) DEFAULT NULL COMMENT '分数',
  `comment` text COMMENT '批改意见',
  `status` varchar(20) DEFAULT 'submitted' COMMENT '鎻愪氦鐘舵?锛歞raft-鑽夌? submitted-宸叉彁浜?graded-宸叉壒鏀',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `homework_id` (`homework_id`),
  KEY `student_id` (`student_id`),
  CONSTRAINT `t_homework_submission_ibfk_1` FOREIGN KEY (`homework_id`) REFERENCES `t_homework` (`id`),
  CONSTRAINT `t_homework_submission_ibfk_2` FOREIGN KEY (`student_id`) REFERENCES `t_student` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='作业提交表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `t_homework_submission`
--

LOCK TABLES `t_homework_submission` WRITE;
/*!40000 ALTER TABLE `t_homework_submission` DISABLE KEYS */;
INSERT INTO `t_homework_submission` VALUES (1,1,1,'已完成所有练习题，包括：\n1. 变量声明：int、double、String等类型的变量声明和使用\n2. 条件语句：if-else、switch-case的使用\n3. 循环语句：for、while、do-while循环的应用\n代码已上传至附件。',NULL,'2026-01-18 14:30:00',95.00,'完成得很好，代码规范，注释清晰。继续保持！','graded','2026-01-18 14:30:00','2026-01-19 10:00:00'),(2,4,1,'个人主页HTML代码已完成，包含以下模块：\n- 个人信息区：姓名、性别、年龄、专业\n- 联系方式区：电话、邮箱、微信\n- 个人简介区：自我介绍\n- 照片展示区：使用img标签\n页面结构清晰，使用了语义化标签。',NULL,'2026-01-20 16:45:00',88.00,'HTML结构清晰，语义化标签使用得当。建议进一步优化页面布局。','graded','2026-01-20 16:45:00','2026-01-21 09:00:00'),(3,7,1,'图书管理系统数据库设计如下：\n\n表结构：\n1. books表（书籍表）\n   - book_id (主键)\n   - title (书名)\n   - author (作者)\n   - isbn (ISBN号)\n   - publish_date (出版日期)\n   - price (价格)\n\n2. readers表（读者表）\n   - reader_id (主键)\n   - name (姓名)\n   - phone (电话)\n   - email (邮箱)\n\n3. borrow_records表（借阅记录表）\n   - record_id (主键)\n   - book_id (外键)\n   - reader_id (外键)\n   - borrow_date (借阅日期)\n   - return_date (归还日期)\n\n详细的ER图和SQL建表语句请见附件。',NULL,'2026-01-22 10:20:00',NULL,NULL,'submitted','2026-01-22 10:20:00','2026-01-22 10:20:00'),(4,9,1,'单链表实现完成，包含以下功能：\n- Node类：节点类，包含数据和next指针\n- LinkedList类：链表类\n  - add(data): 在链表尾部添加节点\n  - remove(index): 删除指定位置的节点\n  - get(index): 获取指定位置的节点\n  - size(): 返回链表长度\n  - print(): 打印链表所有节点\n\n代码已测试通过，详见附件。',NULL,'2026-01-23 15:10:00',NULL,NULL,'submitted','2026-01-23 15:10:00','2026-01-23 15:10:00'),(5,11,1,'完成',NULL,'2026-01-19 16:59:01',88.00,'代码结构清晰，使用了Spring的IOC容器，建议增强异常处理','graded','2026-01-19 16:59:01','2026-01-19 16:59:01'),(6,2,1,'111',NULL,'2026-01-19 17:11:36',NULL,NULL,'submitted','2026-01-19 17:11:36','2026-01-19 17:11:36'),(7,13,1,'测试提交',NULL,'2026-01-19 19:07:07',99.00,'测试批改','graded','2026-01-19 19:07:07','2026-01-19 19:07:07');
/*!40000 ALTER TABLE `t_homework_submission` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `t_internship`
--

DROP TABLE IF EXISTS `t_internship`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `t_internship` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `company_name` varchar(100) NOT NULL COMMENT '公司名称',
  `position` varchar(100) NOT NULL COMMENT '职位',
  `department` varchar(100) DEFAULT NULL COMMENT '部门',
  `start_date` date NOT NULL COMMENT '开始时间',
  `end_date` date DEFAULT NULL COMMENT '结束时间(空表示至今)',
  `description` text COMMENT '工作描述/职责',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_user_dates` (`user_id`,`start_date` DESC),
  CONSTRAINT `t_internship_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `t_user` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='实习经历表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `t_internship`
--

LOCK TABLES `t_internship` WRITE;
/*!40000 ALTER TABLE `t_internship` DISABLE KEYS */;
INSERT INTO `t_internship` VALUES (1,7,'阿里巴巴','前端开发实习生','淘宝','2024-07-01','2024-09-30','Vue组件开发','2026-01-18 15:19:19','2026-01-18 15:19:19'),(2,7,'字节跳动','后端开发实习生','今日头条','2023-07-01','2023-09-30','Go后端开发','2026-01-18 15:19:19','2026-01-18 15:19:19'),(3,8,'腾讯','前端开发实习生','微信','2024-07-01','2024-10-31','小程序开发','2026-01-18 15:19:19','2026-01-18 15:19:19'),(4,9,'华为','算法工程师实习生','2012实验室','2024-06-01','2024-09-30','推荐算法','2026-01-18 15:19:19','2026-01-18 15:19:19'),(5,10,'美团','数据分析师实习生','商业分析','2023-07-01','2023-12-31','用户行为分析','2026-01-18 15:19:19','2026-01-18 15:19:19');
/*!40000 ALTER TABLE `t_internship` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `t_interview`
--

DROP TABLE IF EXISTS `t_interview`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `t_interview` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '面试ID',
  `application_id` bigint DEFAULT NULL COMMENT '申请ID',
  `position_id` bigint DEFAULT NULL COMMENT '职位ID',
  `student_id` bigint NOT NULL COMMENT '学员ID',
  `round` int DEFAULT '1' COMMENT '闈㈣瘯杞??',
  `company_id` bigint NOT NULL COMMENT '企业ID',
  `interview_type` varchar(20) DEFAULT 'online' COMMENT '面试类型：online-线上 offline-线下',
  `interview_time` datetime DEFAULT NULL COMMENT '面试时间',
  `duration` int DEFAULT NULL COMMENT '面试时长（分钟）',
  `interview_url` varchar(255) DEFAULT NULL COMMENT '面试链接',
  `meeting_link` varchar(255) DEFAULT NULL COMMENT '浼氳?閾炬帴锛堢嚎涓婇潰璇曪級',
  `location` varchar(200) DEFAULT NULL COMMENT '面试地点',
  `interviewer` varchar(100) DEFAULT NULL COMMENT '闈㈣瘯瀹',
  `interviewer_contact` varchar(50) DEFAULT NULL COMMENT '闈㈣瘯瀹樿仈绯绘柟寮',
  `status` varchar(20) DEFAULT 'pending' COMMENT '状态：pending-待面试 completed-已完成 accepted-已接受 rejected-已拒绝',
  `score` decimal(5,2) DEFAULT NULL COMMENT '面试评分',
  `remark` text COMMENT '澶囨敞',
  `comment` text COMMENT '闈㈣瘯璇勪环',
  `result` varchar(20) DEFAULT NULL COMMENT '面试结果：pass-通过 fail-未通过 pending-待定',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `student_id` (`student_id`),
  KEY `company_id` (`company_id`),
  KEY `position_id` (`position_id`),
  CONSTRAINT `t_interview_ibfk_1` FOREIGN KEY (`student_id`) REFERENCES `t_student` (`id`),
  CONSTRAINT `t_interview_ibfk_2` FOREIGN KEY (`company_id`) REFERENCES `t_company` (`id`),
  CONSTRAINT `t_interview_ibfk_3` FOREIGN KEY (`position_id`) REFERENCES `t_position` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='面试表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `t_interview`
--

LOCK TABLES `t_interview` WRITE;
/*!40000 ALTER TABLE `t_interview` DISABLE KEYS */;
INSERT INTO `t_interview` VALUES (3,NULL,3,3,1,3,'offline','2025-01-10 14:00:00',90,NULL,NULL,NULL,NULL,NULL,'completed',92.00,'算法很好',NULL,'pass','2026-01-18 15:19:19','2026-01-18 15:19:19'),(4,NULL,4,4,1,4,'online','2025-01-20 14:00:00',60,NULL,NULL,NULL,NULL,NULL,'scheduled',NULL,NULL,NULL,'pending','2026-01-18 15:19:19','2026-01-18 15:19:19'),(5,4,1,1,1,1,'online','2026-02-06 00:00:00',NULL,NULL,'111','','111','111','scheduled',NULL,NULL,NULL,'pending','2026-01-19 11:41:59','2026-01-19 11:41:59');
/*!40000 ALTER TABLE `t_interview` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `t_job_application`
--

DROP TABLE IF EXISTS `t_job_application`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `t_job_application` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '申请ID',
  `position_id` bigint NOT NULL COMMENT '职位ID',
  `student_id` bigint NOT NULL COMMENT '学员ID',
  `resume_id` bigint DEFAULT NULL COMMENT '绠?巻ID',
  `company_id` bigint NOT NULL COMMENT '企业ID',
  `status` varchar(20) DEFAULT 'pending' COMMENT '状态：pending-待处理 screened-已筛选 test_passed-笔试通过 interview_passed-面试通过 hired-已录用 rejected-已拒绝',
  `current_stage` varchar(50) DEFAULT NULL COMMENT '褰撳墠闃舵?锛歳esume-绠?巻绛涢? test-绗旇瘯 interview-闈㈣瘯 offer-offer鍙戞斁 hired-鍏ヨ亴',
  `application_time` datetime DEFAULT NULL COMMENT '鐢宠?鏃堕棿',
  `hr_remark` varchar(500) DEFAULT NULL COMMENT 'HR澶囨敞',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '0:鏈?垹闄?1:宸插垹闄',
  PRIMARY KEY (`id`),
  KEY `position_id` (`position_id`),
  KEY `student_id` (`student_id`),
  KEY `company_id` (`company_id`),
  CONSTRAINT `t_job_application_ibfk_1` FOREIGN KEY (`position_id`) REFERENCES `t_position` (`id`),
  CONSTRAINT `t_job_application_ibfk_2` FOREIGN KEY (`student_id`) REFERENCES `t_student` (`id`),
  CONSTRAINT `t_job_application_ibfk_3` FOREIGN KEY (`company_id`) REFERENCES `t_company` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='求职申请表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `t_job_application`
--

LOCK TABLES `t_job_application` WRITE;
/*!40000 ALTER TABLE `t_job_application` DISABLE KEYS */;
INSERT INTO `t_job_application` VALUES (1,1,1,NULL,1,'interview_passed',NULL,'2025-01-10 10:00:00',NULL,'2026-01-18 15:19:19','2026-01-18 15:19:19',0),(3,4,4,NULL,4,'test_passed','test','2025-01-15 11:00:00',NULL,'2026-01-18 15:19:19','2026-01-19 11:28:46',0),(4,1,1,NULL,1,'test_passed','interview','2026-01-19 11:28:46',NULL,'2026-01-19 11:28:46','2026-01-19 11:28:46',0),(5,7,1,NULL,1,'pending','resume',NULL,NULL,'2026-01-20 15:27:37','2026-01-20 15:27:37',0),(6,7,1,NULL,1,'pending','resume','2026-01-20 15:32:45',NULL,'2026-01-20 15:32:45','2026-01-20 15:32:45',0);
/*!40000 ALTER TABLE `t_job_application` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `t_menu`
--

DROP TABLE IF EXISTS `t_menu`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `t_menu` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '菜单ID',
  `parent_id` bigint DEFAULT '0' COMMENT '父菜单ID，0表示根菜单',
  `menu_name` varchar(50) NOT NULL COMMENT '菜单名称',
  `menu_type` varchar(20) NOT NULL COMMENT '菜单类型：directory-目录 menu-菜单 button-按钮',
  `path` varchar(200) DEFAULT NULL COMMENT '路由路径',
  `component` varchar(200) DEFAULT NULL COMMENT '组件路径',
  `icon` varchar(100) DEFAULT NULL COMMENT '菜单图标',
  `sort_order` int DEFAULT '0' COMMENT '排序',
  `visible` int DEFAULT '1' COMMENT '是否可见：1-是 0-否',
  `status` int DEFAULT '1' COMMENT '状态：1-启用 0-禁用',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=14 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='菜单表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `t_menu`
--

LOCK TABLES `t_menu` WRITE;
/*!40000 ALTER TABLE `t_menu` DISABLE KEYS */;
INSERT INTO `t_menu` VALUES (1,0,'系统管理','menu','/system',NULL,'Setting',1,1,1,'2026-01-18 15:21:39','2026-01-18 15:21:39'),(4,0,'系统管理','menu',NULL,NULL,NULL,0,1,1,'2026-01-18 15:22:04','2026-01-18 15:22:04'),(5,0,'教学管理','menu',NULL,NULL,NULL,0,1,1,'2026-01-18 15:22:04','2026-01-18 15:22:04'),(6,5,'用户管理','menu',NULL,NULL,NULL,0,1,1,'2026-01-18 15:22:11','2026-01-18 15:22:11'),(7,5,'角色管理','menu',NULL,NULL,NULL,0,1,1,'2026-01-18 15:22:11','2026-01-18 15:22:11'),(8,5,'菜单管理','menu',NULL,NULL,NULL,0,1,1,'2026-01-18 15:22:11','2026-01-18 15:22:11'),(9,6,'学校管理','menu',NULL,NULL,NULL,0,1,1,'2026-01-18 15:22:11','2026-01-18 15:22:11'),(10,6,'教室管理','menu',NULL,NULL,NULL,0,1,1,'2026-01-18 15:22:11','2026-01-18 15:22:11'),(11,6,'课程管理','menu',NULL,NULL,NULL,0,1,1,'2026-01-18 15:22:11','2026-01-18 15:22:11'),(12,6,'学员管理','menu',NULL,NULL,NULL,0,1,1,'2026-01-18 15:22:11','2026-01-18 15:22:11'),(13,6,'教师管理','menu',NULL,NULL,NULL,0,1,1,'2026-01-18 15:22:11','2026-01-18 15:22:11');
/*!40000 ALTER TABLE `t_menu` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `t_notification`
--

DROP TABLE IF EXISTS `t_notification`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `t_notification` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '閫氱煡ID',
  `user_id` bigint DEFAULT NULL COMMENT '鎺ユ敹鐢ㄦ埛ID锛圢ULL琛ㄧず鎵?湁鐢ㄦ埛锛',
  `type` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'system' COMMENT '閫氱煡绫诲瀷锛歴ystem-绯荤粺 notice-鍏?憡 homework-浣滀笟 report-鏃ユ姤 job-鑱屼綅 application-鐢宠? test-绗旇瘯 interview-闈㈣瘯 offer-offer',
  `title` varchar(200) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '鏍囬?',
  `content` text COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '鍐呭?',
  `related_id` bigint DEFAULT NULL COMMENT '鍏宠仈瀵硅薄ID锛堝?浣滀笟ID銆佽亴浣岻D绛夛級',
  `is_read` tinyint NOT NULL DEFAULT '0' COMMENT '鏄?惁宸茶?锛?-鏈?? 1-宸茶?',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '鍒涘缓鏃堕棿',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '鏇存柊鏃堕棿',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_type` (`type`),
  KEY `idx_is_read` (`is_read`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB AUTO_INCREMENT=39 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='娑堟伅閫氱煡琛';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `t_notification`
--

LOCK TABLES `t_notification` WRITE;
/*!40000 ALTER TABLE `t_notification` DISABLE KEYS */;
INSERT INTO `t_notification` VALUES (20,1,'notice','🎉 通知管理系统上线啦！','亲爱的用户们：\n\n本平台的通知管理系统已正式上线！\n\n🔔 管理员功能：\n- 可以通过\"系统管理 > 通知管理\"发送系统公告\n- 查看所有通知列表\n- 管理通知状态\n\n📢 所有用户功能：\n- 可以通过侧边栏\"系统公告\"查看最新公告\n- 支持标记已读/全部标记已读\n- 查看公告详情\n\n感谢您的使用！',NULL,1,'2026-01-18 19:59:34','2026-01-18 19:59:34'),(21,2,'notice','🎉 通知管理系统上线啦！','亲爱的用户们：\n\n本平台的通知管理系统已正式上线！\n\n🔔 管理员功能：\n- 可以通过\"系统管理 > 通知管理\"发送系统公告\n- 查看所有通知列表\n- 管理通知状态\n\n📢 所有用户功能：\n- 可以通过侧边栏\"系统公告\"查看最新公告\n- 支持标记已读/全部标记已读\n- 查看公告详情\n\n感谢您的使用！',NULL,0,'2026-01-18 19:59:34','2026-01-18 19:59:34'),(22,4,'notice','🎉 通知管理系统上线啦！','亲爱的用户们：\n\n本平台的通知管理系统已正式上线！\n\n🔔 管理员功能：\n- 可以通过\"系统管理 > 通知管理\"发送系统公告\n- 查看所有通知列表\n- 管理通知状态\n\n📢 所有用户功能：\n- 可以通过侧边栏\"系统公告\"查看最新公告\n- 支持标记已读/全部标记已读\n- 查看公告详情\n\n感谢您的使用！',NULL,0,'2026-01-18 19:59:34','2026-01-18 19:59:34'),(23,5,'notice','🎉 通知管理系统上线啦！','亲爱的用户们：\n\n本平台的通知管理系统已正式上线！\n\n🔔 管理员功能：\n- 可以通过\"系统管理 > 通知管理\"发送系统公告\n- 查看所有通知列表\n- 管理通知状态\n\n📢 所有用户功能：\n- 可以通过侧边栏\"系统公告\"查看最新公告\n- 支持标记已读/全部标记已读\n- 查看公告详情\n\n感谢您的使用！',NULL,0,'2026-01-18 19:59:34','2026-01-18 19:59:34'),(24,7,'notice','🎉 通知管理系统上线啦！','亲爱的用户们：\n\n本平台的通知管理系统已正式上线！\n\n🔔 管理员功能：\n- 可以通过\"系统管理 > 通知管理\"发送系统公告\n- 查看所有通知列表\n- 管理通知状态\n\n📢 所有用户功能：\n- 可以通过侧边栏\"系统公告\"查看最新公告\n- 支持标记已读/全部标记已读\n- 查看公告详情\n\n感谢您的使用！',NULL,1,'2026-01-18 19:59:34','2026-01-18 19:59:34'),(25,8,'notice','🎉 通知管理系统上线啦！','亲爱的用户们：\n\n本平台的通知管理系统已正式上线！\n\n🔔 管理员功能：\n- 可以通过\"系统管理 > 通知管理\"发送系统公告\n- 查看所有通知列表\n- 管理通知状态\n\n📢 所有用户功能：\n- 可以通过侧边栏\"系统公告\"查看最新公告\n- 支持标记已读/全部标记已读\n- 查看公告详情\n\n感谢您的使用！',NULL,0,'2026-01-18 19:59:34','2026-01-18 19:59:34'),(26,9,'notice','🎉 通知管理系统上线啦！','亲爱的用户们：\n\n本平台的通知管理系统已正式上线！\n\n🔔 管理员功能：\n- 可以通过\"系统管理 > 通知管理\"发送系统公告\n- 查看所有通知列表\n- 管理通知状态\n\n📢 所有用户功能：\n- 可以通过侧边栏\"系统公告\"查看最新公告\n- 支持标记已读/全部标记已读\n- 查看公告详情\n\n感谢您的使用！',NULL,0,'2026-01-18 19:59:34','2026-01-18 19:59:34'),(27,10,'notice','🎉 通知管理系统上线啦！','亲爱的用户们：\n\n本平台的通知管理系统已正式上线！\n\n🔔 管理员功能：\n- 可以通过\"系统管理 > 通知管理\"发送系统公告\n- 查看所有通知列表\n- 管理通知状态\n\n📢 所有用户功能：\n- 可以通过侧边栏\"系统公告\"查看最新公告\n- 支持标记已读/全部标记已读\n- 查看公告详情\n\n感谢您的使用！',NULL,0,'2026-01-18 19:59:34','2026-01-18 19:59:34'),(28,11,'notice','🎉 通知管理系统上线啦！','亲爱的用户们：\n\n本平台的通知管理系统已正式上线！\n\n🔔 管理员功能：\n- 可以通过\"系统管理 > 通知管理\"发送系统公告\n- 查看所有通知列表\n- 管理通知状态\n\n📢 所有用户功能：\n- 可以通过侧边栏\"系统公告\"查看最新公告\n- 支持标记已读/全部标记已读\n- 查看公告详情\n\n感谢您的使用！',NULL,0,'2026-01-18 19:59:34','2026-01-18 19:59:34'),(29,12,'notice','🎉 通知管理系统上线啦！','亲爱的用户们：\n\n本平台的通知管理系统已正式上线！\n\n🔔 管理员功能：\n- 可以通过\"系统管理 > 通知管理\"发送系统公告\n- 查看所有通知列表\n- 管理通知状态\n\n📢 所有用户功能：\n- 可以通过侧边栏\"系统公告\"查看最新公告\n- 支持标记已读/全部标记已读\n- 查看公告详情\n\n感谢您的使用！',NULL,0,'2026-01-18 19:59:34','2026-01-18 19:59:34'),(30,13,'notice','🎉 通知管理系统上线啦！','亲爱的用户们：\n\n本平台的通知管理系统已正式上线！\n\n🔔 管理员功能：\n- 可以通过\"系统管理 > 通知管理\"发送系统公告\n- 查看所有通知列表\n- 管理通知状态\n\n📢 所有用户功能：\n- 可以通过侧边栏\"系统公告\"查看最新公告\n- 支持标记已读/全部标记已读\n- 查看公告详情\n\n感谢您的使用！',NULL,0,'2026-01-18 19:59:34','2026-01-18 19:59:34'),(31,14,'notice','🎉 通知管理系统上线啦！','亲爱的用户们：\n\n本平台的通知管理系统已正式上线！\n\n🔔 管理员功能：\n- 可以通过\"系统管理 > 通知管理\"发送系统公告\n- 查看所有通知列表\n- 管理通知状态\n\n📢 所有用户功能：\n- 可以通过侧边栏\"系统公告\"查看最新公告\n- 支持标记已读/全部标记已读\n- 查看公告详情\n\n感谢您的使用！',NULL,0,'2026-01-18 19:59:34','2026-01-18 19:59:34'),(32,15,'notice','🎉 通知管理系统上线啦！','亲爱的用户们：\n\n本平台的通知管理系统已正式上线！\n\n🔔 管理员功能：\n- 可以通过\"系统管理 > 通知管理\"发送系统公告\n- 查看所有通知列表\n- 管理通知状态\n\n📢 所有用户功能：\n- 可以通过侧边栏\"系统公告\"查看最新公告\n- 支持标记已读/全部标记已读\n- 查看公告详情\n\n感谢您的使用！',NULL,0,'2026-01-18 19:59:34','2026-01-18 19:59:34'),(33,16,'notice','🎉 通知管理系统上线啦！','亲爱的用户们：\n\n本平台的通知管理系统已正式上线！\n\n🔔 管理员功能：\n- 可以通过\"系统管理 > 通知管理\"发送系统公告\n- 查看所有通知列表\n- 管理通知状态\n\n📢 所有用户功能：\n- 可以通过侧边栏\"系统公告\"查看最新公告\n- 支持标记已读/全部标记已读\n- 查看公告详情\n\n感谢您的使用！',NULL,0,'2026-01-18 19:59:34','2026-01-18 19:59:34'),(34,17,'notice','🎉 通知管理系统上线啦！','亲爱的用户们：\n\n本平台的通知管理系统已正式上线！\n\n🔔 管理员功能：\n- 可以通过\"系统管理 > 通知管理\"发送系统公告\n- 查看所有通知列表\n- 管理通知状态\n\n📢 所有用户功能：\n- 可以通过侧边栏\"系统公告\"查看最新公告\n- 支持标记已读/全部标记已读\n- 查看公告详情\n\n感谢您的使用！',NULL,0,'2026-01-18 19:59:34','2026-01-18 19:59:34'),(35,18,'notice','🎉 通知管理系统上线啦！','亲爱的用户们：\n\n本平台的通知管理系统已正式上线！\n\n🔔 管理员功能：\n- 可以通过\"系统管理 > 通知管理\"发送系统公告\n- 查看所有通知列表\n- 管理通知状态\n\n📢 所有用户功能：\n- 可以通过侧边栏\"系统公告\"查看最新公告\n- 支持标记已读/全部标记已读\n- 查看公告详情\n\n感谢您的使用！',NULL,0,'2026-01-18 19:59:34','2026-01-18 19:59:34'),(36,19,'notice','🎉 通知管理系统上线啦！','亲爱的用户们：\n\n本平台的通知管理系统已正式上线！\n\n🔔 管理员功能：\n- 可以通过\"系统管理 > 通知管理\"发送系统公告\n- 查看所有通知列表\n- 管理通知状态\n\n📢 所有用户功能：\n- 可以通过侧边栏\"系统公告\"查看最新公告\n- 支持标记已读/全部标记已读\n- 查看公告详情\n\n感谢您的使用！',NULL,0,'2026-01-18 19:59:34','2026-01-18 19:59:34'),(37,20,'notice','🎉 通知管理系统上线啦！','亲爱的用户们：\n\n本平台的通知管理系统已正式上线！\n\n🔔 管理员功能：\n- 可以通过\"系统管理 > 通知管理\"发送系统公告\n- 查看所有通知列表\n- 管理通知状态\n\n📢 所有用户功能：\n- 可以通过侧边栏\"系统公告\"查看最新公告\n- 支持标记已读/全部标记已读\n- 查看公告详情\n\n感谢您的使用！',NULL,0,'2026-01-18 19:59:34','2026-01-18 19:59:34'),(38,21,'notice','🎉 通知管理系统上线啦！','亲爱的用户们：\n\n本平台的通知管理系统已正式上线！\n\n🔔 管理员功能：\n- 可以通过\"系统管理 > 通知管理\"发送系统公告\n- 查看所有通知列表\n- 管理通知状态\n\n📢 所有用户功能：\n- 可以通过侧边栏\"系统公告\"查看最新公告\n- 支持标记已读/全部标记已读\n- 查看公告详情\n\n感谢您的使用！',NULL,0,'2026-01-18 19:59:34','2026-01-18 19:59:34');
/*!40000 ALTER TABLE `t_notification` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `t_offer`
--

DROP TABLE IF EXISTS `t_offer`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `t_offer` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'OfferID',
  `application_id` bigint DEFAULT NULL COMMENT '鐢宠?ID',
  `student_id` bigint NOT NULL COMMENT '学员ID',
  `company_id` bigint NOT NULL COMMENT '企业ID',
  `position_id` bigint DEFAULT NULL COMMENT '职位ID',
  `offer_no` varchar(50) DEFAULT NULL COMMENT 'Offer缂栧彿',
  `position_name` varchar(100) DEFAULT NULL COMMENT '职位名称',
  `city` varchar(100) DEFAULT NULL COMMENT '鍏ヨ亴鍩庡競',
  `salary` decimal(10,2) DEFAULT NULL COMMENT '薪资',
  `salary_unit` varchar(20) DEFAULT NULL COMMENT '钖?祫鍗曚綅锛歮onth-鎸夋湀 year-鎸夊勾',
  `onboard_date` date DEFAULT NULL COMMENT '鍏ヨ亴鏃ユ湡',
  `status` varchar(20) DEFAULT 'pending' COMMENT '状态：pending-待接受 accepted-已接受 declined-已拒绝',
  `accept_deadline` date DEFAULT NULL COMMENT '鎺ュ彈鎴??鏃ユ湡',
  `accept_time` datetime DEFAULT NULL COMMENT '鎺ュ彈鏃堕棿',
  `email_status` varchar(20) DEFAULT NULL COMMENT '閭?欢鍙戦?鐘舵?锛歱ending-寰呭彂閫?sent-宸插彂閫?failed-鍙戦?澶辫触',
  `email_send_time` datetime DEFAULT NULL COMMENT '閭?欢鍙戦?鏃堕棿',
  `attachment_url` varchar(255) DEFAULT NULL COMMENT '闄勪欢URL锛圤ffer PDF锛',
  `remark` text COMMENT '澶囨敞',
  `offer_file_url` varchar(255) DEFAULT NULL COMMENT 'Offer鏂囦欢URL',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `student_id` (`student_id`),
  KEY `company_id` (`company_id`),
  CONSTRAINT `t_offer_ibfk_1` FOREIGN KEY (`student_id`) REFERENCES `t_student` (`id`),
  CONSTRAINT `t_offer_ibfk_2` FOREIGN KEY (`company_id`) REFERENCES `t_company` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='Offer表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `t_offer`
--

LOCK TABLES `t_offer` WRITE;
/*!40000 ALTER TABLE `t_offer` DISABLE KEYS */;
INSERT INTO `t_offer` VALUES (1,NULL,3,3,3,NULL,'算法工程师',NULL,35000.00,NULL,'2025-03-01','accepted',NULL,NULL,NULL,NULL,NULL,NULL,NULL,'2026-01-18 15:19:19','2026-01-18 15:19:19'),(2,NULL,1,1,1,'OFFER-20260118-001','Java后端开发工程师','杭州',20000.00,'year','2025-03-15','rejected','2026-02-18',NULL,'sent','2026-01-19 17:58:49',NULL,'诚邀加入阿里巴巴，共创未来！',NULL,'2026-01-18 15:19:19','2026-01-19 18:11:57');
/*!40000 ALTER TABLE `t_offer` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `t_permission`
--

DROP TABLE IF EXISTS `t_permission`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `t_permission` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '权限ID',
  `permission_code` varchar(100) NOT NULL COMMENT '权限编码',
  `permission_name` varchar(100) NOT NULL COMMENT '权限名称',
  `resource_type` varchar(20) DEFAULT NULL COMMENT '资源类型：menu-菜单 button-按钮 api-接口',
  `description` varchar(200) DEFAULT NULL COMMENT '权限描述',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `permission_code` (`permission_code`)
) ENGINE=InnoDB AUTO_INCREMENT=36 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='权限表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `t_permission`
--

LOCK TABLES `t_permission` WRITE;
/*!40000 ALTER TABLE `t_permission` DISABLE KEYS */;
INSERT INTO `t_permission` VALUES (1,'user:view','View Users','menu','View user list and details','2026-01-18 15:48:41','2026-01-18 15:48:41'),(2,'user:create','Create User','button','Create new user','2026-01-18 15:48:41','2026-01-18 15:48:41'),(3,'user:update','Update User','button','Update user information','2026-01-18 15:48:41','2026-01-18 15:48:41'),(4,'user:delete','Delete User','button','Delete user','2026-01-18 15:48:41','2026-01-18 15:48:41'),(5,'user:export','Export Users','button','Export user data','2026-01-18 15:48:41','2026-01-18 15:48:41'),(6,'role:view','View Roles','menu','View role list and details','2026-01-18 15:48:41','2026-01-18 15:48:41'),(7,'role:create','Create Role','button','Create new role','2026-01-18 15:48:41','2026-01-18 15:48:41'),(8,'role:update','Update Role','button','Update role information','2026-01-18 15:48:41','2026-01-18 15:48:41'),(9,'role:delete','Delete Role','button','Delete role','2026-01-18 15:48:41','2026-01-18 15:48:41'),(10,'role:assign','Assign Permissions','button','Assign permissions to role','2026-01-18 15:48:41','2026-01-18 15:48:41'),(11,'menu:view','View Menus','menu','View menu list and details','2026-01-18 15:48:41','2026-01-18 15:48:41'),(12,'menu:create','Create Menu','button','Create new menu','2026-01-18 15:48:41','2026-01-18 15:48:41'),(13,'menu:update','Update Menu','button','Update menu information','2026-01-18 15:48:41','2026-01-18 15:48:41'),(14,'menu:delete','Delete Menu','button','Delete menu','2026-01-18 15:48:41','2026-01-18 15:48:41'),(15,'school:view','View Schools','menu','View school list','2026-01-18 15:48:41','2026-01-18 15:48:41'),(16,'school:create','Create School','button','Create new school','2026-01-18 15:48:41','2026-01-18 15:48:41'),(17,'school:update','Update School','button','Update school information','2026-01-18 15:48:41','2026-01-18 15:48:41'),(18,'school:delete','Delete School','button','Delete school','2026-01-18 15:48:41','2026-01-18 15:48:41'),(19,'teacher:view','View Teachers','menu','View teacher list','2026-01-18 15:48:41','2026-01-18 15:48:41'),(20,'teacher:create','Create Teacher','button','Create new teacher','2026-01-18 15:48:41','2026-01-18 15:48:41'),(21,'teacher:update','Update Teacher','button','Update teacher information','2026-01-18 15:48:41','2026-01-18 15:48:41'),(22,'teacher:delete','Delete Teacher','button','Delete teacher','2026-01-18 15:48:41','2026-01-18 15:48:41'),(23,'student:view','View Students','menu','View student list','2026-01-18 15:48:41','2026-01-18 15:48:41'),(24,'student:create','Create Student','button','Create new student','2026-01-18 15:48:41','2026-01-18 15:48:41'),(25,'student:update','Update Student','button','Update student information','2026-01-18 15:48:41','2026-01-18 15:48:41'),(26,'student:delete','Delete Student','button','Delete student','2026-01-18 15:48:41','2026-01-18 15:48:41'),(27,'company:view','View Companies','menu','View company list','2026-01-18 15:48:41','2026-01-18 15:48:41'),(28,'company:create','Create Company','button','Create new company','2026-01-18 15:48:41','2026-01-18 15:48:41'),(29,'company:update','Update Company','button','Update company information','2026-01-18 15:48:41','2026-01-18 15:48:41'),(30,'company:delete','Delete Company','button','Delete company','2026-01-18 15:48:41','2026-01-18 15:48:41'),(31,'position:view','View Positions','menu','View position list','2026-01-18 15:48:41','2026-01-18 15:48:41'),(32,'position:create','Create Position','button','Create new position','2026-01-18 15:48:41','2026-01-18 15:48:41'),(33,'position:update','Update Position','button','Update position information','2026-01-18 15:48:41','2026-01-18 15:48:41'),(34,'position:delete','Delete Position','button','Delete position','2026-01-18 15:48:41','2026-01-18 15:48:41');
/*!40000 ALTER TABLE `t_permission` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `t_position`
--

DROP TABLE IF EXISTS `t_position`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `t_position` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '职位ID',
  `company_id` bigint NOT NULL COMMENT '企业ID',
  `position_name` varchar(100) NOT NULL COMMENT '职位名称',
  `position_type` varchar(20) DEFAULT 'fulltime' COMMENT '职位类型：fulltime-全职 parttime-兼职 internship-实习',
  `work_city` varchar(200) DEFAULT NULL COMMENT '宸ヤ綔鍩庡競',
  `salary_min` decimal(10,2) DEFAULT NULL COMMENT '最低薪资',
  `salary_max` decimal(10,2) DEFAULT NULL COMMENT '最高薪资',
  `salary_unit` varchar(20) DEFAULT NULL COMMENT '钖?祫鍗曚綅锛歮onth-鎸夋湀 year-鎸夊勾 day-鎸夊ぉ hour-鎸夋椂',
  `location` varchar(200) DEFAULT NULL COMMENT '工作地点',
  `requirements` text COMMENT '职位要求',
  `education_require` varchar(50) DEFAULT NULL COMMENT '瀛﹀巻瑕佹眰',
  `experience_require` varchar(50) DEFAULT NULL COMMENT '宸ヤ綔骞撮檺瑕佹眰',
  `tech_stack` text COMMENT '鎶?湳鏍堬紙JSON鏁扮粍锛',
  `capability_radar` text COMMENT '鑳藉姏闆疯揪鍥炬暟鎹?紙JSON瀵硅薄锛',
  `recruit_num` int DEFAULT '1' COMMENT '鎷涜仒浜烘暟',
  `hired_count` int DEFAULT '0' COMMENT '宸叉嫑浜烘暟',
  `application_count` int DEFAULT '0' COMMENT '绠?巻鎶曢?鏁',
  `publish_time` datetime DEFAULT NULL COMMENT '鍙戝竷鏃堕棿',
  `deadline` date DEFAULT NULL COMMENT '鎴??鏃ユ湡',
  `description` text COMMENT '职位描述',
  `recruitment_count` int DEFAULT '1' COMMENT '招聘人数',
  `status` varchar(20) DEFAULT 'active' COMMENT '状态：active-激活 inactive-停用',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `company_id` (`company_id`),
  CONSTRAINT `t_position_ibfk_1` FOREIGN KEY (`company_id`) REFERENCES `t_company` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='职位表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `t_position`
--

LOCK TABLES `t_position` WRITE;
/*!40000 ALTER TABLE `t_position` DISABLE KEYS */;
INSERT INTO `t_position` VALUES (1,1,'Java后端开发工程师','fulltime','杭州',15000.00,25000.00,NULL,'杭州','熟练掌握Java和Spring框架',NULL,NULL,NULL,'[{\"name\":\"Java基础\",\"value\":8},{\"name\":\"Spring框架\",\"value\":7},{\"name\":\"MySQL数据库\",\"value\":6},{\"name\":\"Redis缓存\",\"value\":5},{\"name\":\"微服务架构\",\"value\":6}]',5,0,0,NULL,NULL,'负责后端系统开发',5,'active','2026-01-18 15:19:19','2026-01-20 19:50:17'),(2,1,'前端开发工程师','fulltime','杭州',12000.00,20000.00,NULL,'杭州','熟练掌握Vue/React',NULL,NULL,NULL,'[{\"name\":\"HTML/CSS\",\"value\":8},{\"name\":\"JavaScript\",\"value\":8},{\"name\":\"Vue框架\",\"value\":7},{\"name\":\"工程化工具\",\"value\":6}]',3,0,0,NULL,NULL,'负责前端页面开发',3,'active','2026-01-18 15:19:19','2026-01-20 19:50:17'),(3,2,'Python开发工程师','fulltime','深圳',18000.00,28000.00,NULL,'深圳','精通Python和Django/Flask',NULL,NULL,NULL,NULL,4,0,0,NULL,NULL,'负责AI平台开发',4,'active','2026-01-18 15:19:19','2026-01-19 10:36:29'),(4,3,'算法工程师','fulltime','北京',25000.00,40000.00,NULL,'北京','熟悉机器学习算法',NULL,NULL,NULL,NULL,3,0,0,NULL,NULL,'负责推荐算法优化',3,'active','2026-01-18 15:19:19','2026-01-19 10:36:29'),(5,4,'嵌入式开发工程师','fulltime','深圳',18000.00,28000.00,NULL,'深圳','熟悉C/C++和嵌入式开发',NULL,NULL,NULL,NULL,6,0,0,NULL,NULL,'负责嵌入式系统开发',6,'active','2026-01-18 15:19:19','2026-01-19 10:36:29'),(6,5,'数据分析师','fulltime','北京',15000.00,22000.00,NULL,'北京','熟悉SQL和数据分析',NULL,NULL,NULL,NULL,4,0,0,NULL,NULL,'负责业务数据分析',4,'active','2026-01-18 15:19:19','2026-01-19 10:36:29'),(7,1,'测试','fulltime','测试',1111.00,111111.00,'month',NULL,'测试测试测试测试测试测试','bachelor','unlimited','测试测试测试测试测试测试',NULL,3,0,0,'2026-01-20 15:01:48','2026-01-31','测试测试测试测试测试测试',1,'active','2026-01-20 15:00:36','2026-01-20 15:00:36');
/*!40000 ALTER TABLE `t_position` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `t_position_recommendation`
--

DROP TABLE IF EXISTS `t_position_recommendation`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `t_position_recommendation` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '涓婚敭ID',
  `student_id` bigint NOT NULL COMMENT '瀛﹀憳ID',
  `position_id` bigint NOT NULL COMMENT '鑱屼綅ID',
  `teacher_id` bigint NOT NULL COMMENT '鏁欏笀ID',
  `reason` text COMMENT '鎺ㄨ崘鐞嗙敱',
  `remark` text COMMENT '澶囨敞淇℃伅',
  `status` varchar(20) DEFAULT 'pending' COMMENT '鐘舵?锛歱ending-寰呮煡鐪嬶紝viewed-宸叉煡鐪嬶紝applied-宸叉姇閫掞紝rejected-宸叉嫆缁',
  `view_time` datetime DEFAULT NULL COMMENT '瀛﹀憳鏌ョ湅鏃堕棿',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '鍒涘缓鏃堕棿',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '鏇存柊鏃堕棿',
  PRIMARY KEY (`id`),
  KEY `idx_student_id` (`student_id`),
  KEY `idx_position_id` (`position_id`),
  KEY `idx_teacher_id` (`teacher_id`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='鑱屼綅鎺ㄨ崘琛';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `t_position_recommendation`
--

LOCK TABLES `t_position_recommendation` WRITE;
/*!40000 ALTER TABLE `t_position_recommendation` DISABLE KEYS */;
INSERT INTO `t_position_recommendation` VALUES (1,1,1,0,'测试','测试','viewed','2026-01-20 14:13:43','2026-01-20 14:13:37','2026-01-20 14:13:37');
/*!40000 ALTER TABLE `t_position_recommendation` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `t_question_bank`
--

DROP TABLE IF EXISTS `t_question_bank`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `t_question_bank` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '棰樼洰ID',
  `question_type` varchar(20) NOT NULL COMMENT '棰樺瀷锛歴ingle_choice-鍗曢? multiple_choice-澶氶? true_false-鍒ゆ柇 short_answer-绠?瓟',
  `knowledge_point` varchar(100) NOT NULL COMMENT '鐭ヨ瘑鐐瑰垎绫',
  `difficulty` varchar(20) DEFAULT 'medium' COMMENT '闅惧害锛歟asy-绠?崟 medium-涓?瓑 hard-鍥伴毦',
  `question_text` text NOT NULL COMMENT '棰樺共',
  `options` json DEFAULT NULL COMMENT '閫夐」锛圝SON鏍煎紡锛屼粎瀹㈣?棰橈級',
  `correct_answer` text NOT NULL COMMENT '姝ｇ‘绛旀?',
  `analysis` text COMMENT '绛旀?瑙ｆ瀽',
  `company_id` bigint DEFAULT NULL COMMENT '鎵?睘浼佷笟ID锛坣ull琛ㄧず鍩虹?棰樺簱锛',
  `status` varchar(20) DEFAULT 'active' COMMENT '鐘舵?锛歛ctive-婵?椿 draft-鑽夌?',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '鍒涘缓鏃堕棿',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '鏇存柊鏃堕棿',
  PRIMARY KEY (`id`),
  KEY `idx_knowledge_point` (`knowledge_point`),
  KEY `idx_company_id` (`company_id`)
) ENGINE=InnoDB AUTO_INCREMENT=29 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='棰樺簱琛';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `t_question_bank`
--

LOCK TABLES `t_question_bank` WRITE;
/*!40000 ALTER TABLE `t_question_bank` DISABLE KEYS */;
INSERT INTO `t_question_bank` VALUES (2,'single_choice','Java基础','easy','Java中基本数据类型int占用多少字节？','[\"A. 2字节\", \"B. 4字节\", \"C. 8字节\", \"D. 1字节\"]','B','Java中int类型占用4个字节，共32位。',NULL,'active','2026-01-20 19:50:17','2026-01-20 19:50:17'),(3,'single_choice','Java基础','easy','以下哪个是Java的合法标识符？','[\"A. 2variable\", \"B. class\", \"C. _name\", \"D. java-name\"]','C','标识符可以包含字母、数字、下划线和美元符号，但不能以数字开头，不能是关键字。',NULL,'active','2026-01-20 19:50:17','2026-01-20 19:50:17'),(4,'single_choice','Java基础','medium','关于Java中的String，下列说法正确的是：','[\"A. String是基本数据类型\", \"B. String是不可变的\", \"C. String可以直接用==比较内容\", \"D. String的长度可以修改\"]','B','String是引用类型，不可变对象，应该用equals()比较内容。',NULL,'active','2026-01-20 19:50:17','2026-01-20 19:50:17'),(5,'single_choice','面向对象','medium','关于Java继承，下列说法错误的是：','[\"A. Java只支持单继承\", \"B. 子类可以继承父类的所有非私有成员\", \"C. 构造方法不能被继承\", \"D. final类可以被继承\"]','D','final类不能被继承，这是final关键字的作用之一。',NULL,'active','2026-01-20 19:50:17','2026-01-20 19:50:17'),(6,'multiple_choice','Java基础','medium','Java中哪些关键字用于异常处理？','[\"A. try\", \"B. catch\", \"C. finally\", \"D. throw\"]','A,B,C,D','try-catch-finally是异常处理的基本结构，throw用于抛出异常。',NULL,'active','2026-01-20 19:50:17','2026-01-20 19:50:17'),(7,'multiple_choice','集合框架','medium','关于ArrayList和LinkedList的区别，以下说法正确的是：','[\"A. ArrayList基于数组实现\", \"B. LinkedList基于链表实现\", \"C. ArrayList查询效率高\", \"D. LinkedList插入删除效率高\"]','A,B,C,D','ArrayList底层是数组，查询快但增删慢；LinkedList底层是链表，增删快但查询慢。',NULL,'active','2026-01-20 19:50:17','2026-01-20 19:50:17'),(8,'true_false','Java基础','easy','Java中接口可以被多重继承。',NULL,'true','一个接口可以继承多个父接口，这是Java接口的重要特性。',NULL,'active','2026-01-20 19:50:17','2026-01-20 19:50:17'),(9,'true_false','面向对象','medium','抽象类可以有构造方法。',NULL,'true','抽象类可以有构造方法，主要用于子类初始化时调用。',NULL,'active','2026-01-20 19:50:17','2026-01-20 19:50:17'),(10,'short_answer','Java基础','medium','请简述Java中==和equals()的区别。',NULL,'==是比较运算符，用于比较基本数据类型的值或引用类型的内存地址；equals()是Object类的方法，用于比较对象的内容。String类重写了equals()方法，用于比较字符串内容。','==比较引用或基本类型值，equals()比较对象内容（需重写）',NULL,'active','2026-01-20 19:50:17','2026-01-20 19:50:17'),(11,'short_answer','面向对象','hard','请简述Java中重载(Overload)和重写(Override)的区别。',NULL,'重载：同一个类中方法名相同但参数列表不同（参数个数、类型、顺序）。重写：子类重新定义父类的方法，方法签名必须完全一致，访问修饰符不能更严格。','重载是同一个类的多态性，重写是子类对父类的重新实现',NULL,'active','2026-01-20 19:50:17','2026-01-20 19:50:17'),(12,'short_answer','集合框架','hard','请简述HashMap的实现原理。',NULL,'HashMap基于数组+链表/红黑树实现。通过key的hash值计算数组下标，相同hash值的用链表存储（链地址法）。JDK8后，链表长度超过8时转为红黑树以提高查询效率。','数组+链表+红黑树，通过hash值定位，冲突用链地址法',NULL,'active','2026-01-20 19:50:17','2026-01-20 19:50:17'),(13,'single_choice','HTML/CSS','easy','HTML中用于创建超链接的标签是？','[\"A. <link>\", \"B. <a>\", \"C. <href>\", \"D. <url>\"]','B','<a>标签用于创建超链接，href属性指定链接地址。',NULL,'active','2026-01-20 19:50:17','2026-01-20 19:50:17'),(14,'single_choice','JavaScript','medium','JavaScript中声明常量的关键字是？','[\"A. var\", \"B. let\", \"C. const\", \"D. final\"]','C','const用于声明常量，声明后不可重新赋值。',NULL,'active','2026-01-20 19:50:17','2026-01-20 19:50:17'),(15,'single_choice','Vue基础','medium','Vue中用于双向数据绑定的指令是？','[\"A. v-bind\", \"B. v-model\", \"C. v-on\", \"D. v-if\"]','B','v-model用于表单元素的双向数据绑定。',NULL,'active','2026-01-20 19:50:17','2026-01-20 19:50:17'),(16,'multiple_choice','CSS','medium','CSS中三种定位方式包括：','[\"A. relative\", \"B. absolute\", \"C. fixed\", \"D. static\"]','A,B,C','static是默认定位方式，relative、absolute、fixed是常用定位方式。',NULL,'active','2026-01-20 19:50:17','2026-01-20 19:50:17'),(17,'true_false','JavaScript','easy','JavaScript是Java的脚本版本。',NULL,'false','JavaScript和Java是完全不同的两种语言，只是名称相似。',NULL,'active','2026-01-20 19:50:17','2026-01-20 19:50:17'),(18,'short_answer','Vue基础','medium','请简述Vue的生命周期钩子函数。',NULL,'主要钩子：beforeCreate（创建前）、created（创建完成）、beforeMount（挂载前）、mounted（挂载完成）、beforeUpdate（更新前）、updated（更新完成）、beforeDestroy（销毁前）、destroyed（销毁完成）。','从创建到销毁的完整生命周期，每个阶段有对应的钩子函数',NULL,'active','2026-01-20 19:50:17','2026-01-20 19:50:17'),(19,'single_choice','SQL基础','easy','SQL中用于查询数据的关键字是？','[\"A. GET\", \"B. SELECT\", \"C. QUERY\", \"D. FIND\"]','B','SELECT是SQL中用于查询数据的关键字。',NULL,'active','2026-01-20 19:50:17','2026-01-20 19:50:17'),(20,'single_choice','MySQL','medium','MySQL中用于限制查询结果数量的关键字是？','[\"A. TOP\", \"B. LIMIT\", \"C. ROWCOUNT\", \"D. MAX\"]','B','MySQL使用LIMIT限制结果数量，如LIMIT 10表示返回前10条记录。',NULL,'active','2026-01-20 19:50:17','2026-01-20 19:50:17'),(21,'multiple_choice','数据库理论','medium','数据库事务的ACID特性包括：','[\"A. 原子性\", \"B. 一致性\", \"C. 隔离性\", \"D. 持久性\"]','A,B,C,D','ACID是事务的四个基本特性：原子性、一致性、隔离性、持久性。',NULL,'active','2026-01-20 19:50:17','2026-01-20 19:50:17'),(22,'true_false','索引优化','medium','索引越多，查询性能越好。',NULL,'false','索引可以提高查询性能，但会降低增删改性能，需要根据实际查询需求合理创建索引。',NULL,'active','2026-01-20 19:50:17','2026-01-20 19:50:17'),(23,'short_answer','MySQL','hard','请简述MySQL中InnoDB和MyISAM的区别。',NULL,'主要区别：1）事务支持：InnoDB支持事务，MyISAM不支持；2）锁机制：InnoDB支持行锁，MyISAM只支持表锁；3）外键：InnoDB支持外键，MyISAM不支持；4）崩溃恢复：InnoDB有崩溃恢复能力，MyISAM没有。','InnoDB支持事务和行锁，更适合高并发；MyISAM表锁，适合读多写少',NULL,'active','2026-01-20 19:50:17','2026-01-20 19:50:17'),(24,'single_choice','Spring核心','easy','Spring的核心功能是？','[\"A. MVC框架\", \"B. IoC和AOP\", \"C. ORM框架\", \"D. 数据库连接池\"]','B','IoC（控制反转）和AOP（面向切面编程）是Spring的两大核心功能。',NULL,'active','2026-01-20 19:50:17','2026-01-20 19:50:17'),(25,'single_choice','Spring Boot','medium','Spring Boot用于简化Spring应用的？','[\"A. 测试\", \"B. 开发和配置\", \"C. 部署\", \"D. 监控\"]','B','Spring Boot通过自动配置和约定优于配置，大大简化了Spring应用的开发和配置。',NULL,'active','2026-01-20 19:50:17','2026-01-20 19:50:17'),(26,'multiple_choice','Spring MVC','medium','Spring MVC的主要组件包括：','[\"A. DispatcherServlet\", \"B. Controller\", \"C. ViewResolver\", \"D. HandlerMapping\"]','A,B,C,D','Spring MVC的核心组件包括前端控制器、处理器、视图解析器、处理器映射器等。',NULL,'active','2026-01-20 19:50:17','2026-01-20 19:50:17'),(27,'true_false','Spring核心','medium','@Autowired默认按byType方式进行自动装配。',NULL,'true','@Autowired默认按类型装配，如果有多个类型匹配则按名称装配。',NULL,'active','2026-01-20 19:50:17','2026-01-20 19:50:17'),(28,'short_answer','Spring核心','hard','请简述IoC（控制反转）和DI（依赖注入）的概念和区别。',NULL,'IoC（Inversion of Control）是一种设计思想，将对象的创建和管理交给Spring容器；DI（Dependency Injection）是IoC的实现方式，通过构造方法、setter方法等注入依赖。IoC是目标，DI是手段。','IoC是设计思想，DI是IoC的具体实现方式',NULL,'active','2026-01-20 19:50:17','2026-01-20 19:50:17');
/*!40000 ALTER TABLE `t_question_bank` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `t_role`
--

DROP TABLE IF EXISTS `t_role`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `t_role` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '角色ID',
  `role_code` varchar(50) NOT NULL COMMENT '角色编码',
  `role_name` varchar(50) NOT NULL COMMENT '角色名称',
  `description` varchar(200) DEFAULT NULL COMMENT '角色描述',
  `status` int DEFAULT '1' COMMENT '状态：1-启用 0-禁用',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `role_code` (`role_code`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='角色表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `t_role`
--

LOCK TABLES `t_role` WRITE;
/*!40000 ALTER TABLE `t_role` DISABLE KEYS */;
INSERT INTO `t_role` VALUES (1,'admin','管理员','拥有系统所有权限',1,'2026-01-18 15:19:19','2026-01-18 15:19:19'),(2,'college_head','学院负责人','负责管理学院的全部教学和就业工作',1,'2026-01-18 15:19:19','2026-01-18 15:19:19'),(3,'teacher','教师','管理课程和学员',1,'2026-01-18 15:19:19','2026-01-18 15:19:19'),(4,'user','学员','学员身份',1,'2026-01-18 15:19:19','2026-01-18 15:19:19'),(5,'enterprise_contact','企业对接人','管理企业招聘',1,'2026-01-18 15:19:19','2026-01-18 15:19:19');
/*!40000 ALTER TABLE `t_role` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `t_role_menu_relation`
--

DROP TABLE IF EXISTS `t_role_menu_relation`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `t_role_menu_relation` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `role_id` bigint NOT NULL COMMENT '角色ID',
  `menu_id` bigint NOT NULL COMMENT '菜单ID',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_role_menu` (`role_id`,`menu_id`),
  KEY `menu_id` (`menu_id`),
  CONSTRAINT `t_role_menu_relation_ibfk_1` FOREIGN KEY (`role_id`) REFERENCES `t_role` (`id`) ON DELETE CASCADE,
  CONSTRAINT `t_role_menu_relation_ibfk_2` FOREIGN KEY (`menu_id`) REFERENCES `t_menu` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='角色菜单关联表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `t_role_menu_relation`
--

LOCK TABLES `t_role_menu_relation` WRITE;
/*!40000 ALTER TABLE `t_role_menu_relation` DISABLE KEYS */;
/*!40000 ALTER TABLE `t_role_menu_relation` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `t_role_permission_relation`
--

DROP TABLE IF EXISTS `t_role_permission_relation`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `t_role_permission_relation` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `role_id` bigint NOT NULL COMMENT '角色ID',
  `permission_id` bigint NOT NULL COMMENT '权限ID',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_role_permission` (`role_id`,`permission_id`),
  KEY `permission_id` (`permission_id`),
  CONSTRAINT `t_role_permission_relation_ibfk_1` FOREIGN KEY (`role_id`) REFERENCES `t_role` (`id`) ON DELETE CASCADE,
  CONSTRAINT `t_role_permission_relation_ibfk_2` FOREIGN KEY (`permission_id`) REFERENCES `t_permission` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='角色权限关联表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `t_role_permission_relation`
--

LOCK TABLES `t_role_permission_relation` WRITE;
/*!40000 ALTER TABLE `t_role_permission_relation` DISABLE KEYS */;
/*!40000 ALTER TABLE `t_role_permission_relation` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `t_school`
--

DROP TABLE IF EXISTS `t_school`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `t_school` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '学校ID',
  `school_name` varchar(100) NOT NULL COMMENT '学校名称',
  `school_code` varchar(50) DEFAULT NULL COMMENT '学校编码',
  `address` varchar(200) DEFAULT NULL COMMENT '地址',
  `contact_person` varchar(50) DEFAULT NULL COMMENT '联系人',
  `contact_phone` varchar(20) DEFAULT NULL COMMENT '联系电话',
  `description` text COMMENT '学校简介',
  `status` int DEFAULT '1' COMMENT '状态：1-启用 0-禁用',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `school_code` (`school_code`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='学校表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `t_school`
--

LOCK TABLES `t_school` WRITE;
/*!40000 ALTER TABLE `t_school` DISABLE KEYS */;
INSERT INTO `t_school` VALUES (1,'清华大学','TSINGHUA','北京市海淀区','张老师','010-62785000',NULL,1,'2026-01-18 15:19:19','2026-01-18 15:19:19'),(2,'北京大学','PKU','北京市海淀区','李老师','010-62751234',NULL,1,'2026-01-18 15:19:19','2026-01-18 15:19:19'),(4,'复旦大学','FUDAN','上海市杨浦区邯郸路220号','王老师','13900139000','复旦大学是中国著名的高等学府之一，创建于1905年，位于中国上海。',1,'2026-01-18 19:48:49','2026-01-18 19:48:49');
/*!40000 ALTER TABLE `t_school` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `t_student`
--

DROP TABLE IF EXISTS `t_student`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `t_student` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '学员ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `student_no` varchar(50) NOT NULL COMMENT '学号',
  `school_id` bigint DEFAULT NULL COMMENT '所属学校ID',
  `gender` int DEFAULT NULL COMMENT '性别：1-男 2-女',
  `phone` varchar(20) DEFAULT NULL COMMENT '手机号',
  `email` varchar(100) DEFAULT NULL COMMENT '邮箱',
  `grade` varchar(20) DEFAULT NULL COMMENT '年级',
  `major` varchar(100) DEFAULT NULL COMMENT '专业',
  `class_name` varchar(50) DEFAULT NULL COMMENT '班级',
  `enrollment_date` date DEFAULT NULL COMMENT '入学日期',
  `graduation_date` date DEFAULT NULL COMMENT '预计毕业日期',
  `political_status` varchar(50) DEFAULT NULL COMMENT '政治面貌',
  `nation` varchar(50) DEFAULT NULL COMMENT '民族',
  `birth_date` date DEFAULT NULL COMMENT '出生日期',
  `id_card` varchar(18) DEFAULT NULL COMMENT '身份证号',
  `address` varchar(200) DEFAULT NULL COMMENT '家庭地址',
  `guardian_name` varchar(50) DEFAULT NULL COMMENT '监护人姓名',
  `guardian_phone` varchar(20) DEFAULT NULL COMMENT '监护人电话',
  `description` text COMMENT '备注信息',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `qq` varchar(20) DEFAULT NULL COMMENT 'QQ号码',
  `wechat` varchar(50) DEFAULT NULL COMMENT '微信号',
  `health_status` varchar(20) DEFAULT '健康' COMMENT '健康状况',
  `bio` text COMMENT '个人简介',
  PRIMARY KEY (`id`),
  UNIQUE KEY `student_no` (`student_no`),
  KEY `user_id` (`user_id`),
  KEY `school_id` (`school_id`),
  KEY `idx_student_no` (`student_no`),
  CONSTRAINT `t_student_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `t_user` (`id`),
  CONSTRAINT `t_student_ibfk_2` FOREIGN KEY (`school_id`) REFERENCES `t_school` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='学员表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `t_student`
--

LOCK TABLES `t_student` WRITE;
/*!40000 ALTER TABLE `t_student` DISABLE KEYS */;
INSERT INTO `t_student` VALUES (1,7,'2021001001',1,1,'13800002001','zhangsan@student.com','2021','计算机科学与技术','计科2101','2021-09-01',NULL,'mass','Han','2003-05-16','110101200305151234','Beijing Chaoyang',NULL,NULL,NULL,'2026-01-18 15:19:19','2026-01-19 13:48:22','987654321','zhangsan_wx','健康','Love programming and good team player'),(2,8,'2021001002',NULL,2,'13800002002','lisi@student.com','2021级','软件工程','软工2101','2021-09-01',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'2026-01-18 15:19:19','2026-01-18 15:19:19',NULL,NULL,'健康',NULL),(3,9,'2021001003',NULL,1,'13800002003','wangwu@student.com','2021级','计算机科学与技术','计科2102','2021-09-01',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'2026-01-18 15:19:19','2026-01-18 15:19:19',NULL,NULL,'健康',NULL),(4,10,'2021002001',NULL,2,'13800002004','zhaoliu@student.com','2021级','人工智能','人工智能2101','2021-09-01',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'2026-01-18 15:19:19','2026-01-18 15:19:19',NULL,NULL,'健康',NULL),(5,11,'2021002002',NULL,1,'13800002005','qianqi@student.com','2021级','数据科学','数据科学2101','2021-09-01',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'2026-01-18 15:19:19','2026-01-18 15:19:19',NULL,NULL,'健康',NULL),(6,12,'2021002003',NULL,2,'13800002006','sunba@student.com','2021级','软件工程','软工2102','2021-09-01',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'2026-01-18 15:19:19','2026-01-18 15:19:19',NULL,NULL,'健康',NULL),(7,13,'2021002004',NULL,1,'13800002007','zhoujiu@student.com','2021级','计算机科学与技术','计科2103','2021-09-01',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'2026-01-18 15:19:19','2026-01-18 15:19:19',NULL,NULL,'健康',NULL),(8,14,'2021001004',NULL,2,'13800002008','wushi@student.com','2021级','人工智能','人工智能2102','2021-09-01',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'2026-01-18 15:19:19','2026-01-18 15:19:19',NULL,NULL,'健康',NULL),(9,15,'2021002005',NULL,1,'13800002009','zhengyi@student.com','2021级','数据科学','数据科学2102','2021-09-01',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'2026-01-18 15:19:19','2026-01-18 15:19:19',NULL,NULL,'健康',NULL),(10,16,'2021002006',NULL,2,'13800002010','wanger@student.com','2021级','软件工程','软工2103','2021-09-01',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'2026-01-18 15:19:19','2026-01-18 15:19:19',NULL,NULL,'健康',NULL);
/*!40000 ALTER TABLE `t_student` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `t_student_answer`
--

DROP TABLE IF EXISTS `t_student_answer`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `t_student_answer` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '绛旈?ID',
  `exam_record_id` bigint NOT NULL COMMENT '鑰冭瘯璁板綍ID',
  `question_id` bigint NOT NULL COMMENT '棰樼洰ID',
  `student_answer` text COMMENT '瀛︾敓绛旀?',
  `score` decimal(5,2) DEFAULT NULL COMMENT '寰楀垎',
  `comment` text COMMENT '鏁欏笀璇勮?',
  `is_correct` tinyint DEFAULT NULL COMMENT '鏄?惁姝ｇ‘锛?-閿欒? 1-姝ｇ‘ null-寰呮壒鏀',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '鍒涘缓鏃堕棿',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '鏇存柊鏃堕棿',
  PRIMARY KEY (`id`),
  KEY `idx_exam_record_id` (`exam_record_id`),
  KEY `idx_question_id` (`question_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='瀛︾敓绛旈?琛';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `t_student_answer`
--

LOCK TABLES `t_student_answer` WRITE;
/*!40000 ALTER TABLE `t_student_answer` DISABLE KEYS */;
/*!40000 ALTER TABLE `t_student_answer` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `t_student_course`
--

DROP TABLE IF EXISTS `t_student_course`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `t_student_course` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `student_id` bigint NOT NULL COMMENT '学员ID',
  `course_name` varchar(200) NOT NULL COMMENT '课程名称',
  `score` decimal(5,2) DEFAULT NULL COMMENT '成绩',
  `semester` varchar(20) DEFAULT NULL COMMENT '学期',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_student_id` (`student_id`),
  CONSTRAINT `t_student_course_ibfk_1` FOREIGN KEY (`student_id`) REFERENCES `t_student` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='学员课程成绩表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `t_student_course`
--

LOCK TABLES `t_student_course` WRITE;
/*!40000 ALTER TABLE `t_student_course` DISABLE KEYS */;
INSERT INTO `t_student_course` VALUES (1,1,'数据结构与算法',90.00,'2021秋季','2026-01-19 13:09:27','2026-01-19 13:09:27'),(2,1,'计算机网络',85.00,'2022春季','2026-01-19 13:09:27','2026-01-19 13:09:27'),(3,1,'操作系统',88.00,'2022秋季','2026-01-19 13:09:27','2026-01-19 13:09:27'),(4,1,'数据库系统',92.00,'2023春季','2026-01-19 13:09:27','2026-01-19 13:09:27');
/*!40000 ALTER TABLE `t_student_course` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `t_student_education`
--

DROP TABLE IF EXISTS `t_student_education`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `t_student_education` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `student_id` bigint NOT NULL COMMENT '学员ID',
  `school_name` varchar(200) NOT NULL COMMENT '学校名称',
  `college` varchar(200) DEFAULT NULL COMMENT '学院',
  `major` varchar(200) NOT NULL COMMENT '专业',
  `grade` varchar(50) DEFAULT NULL COMMENT '年级',
  `class_name` varchar(100) DEFAULT NULL COMMENT '班级',
  `education` varchar(50) DEFAULT NULL COMMENT '学历(本科/硕士/博士/专科)',
  `enrollment_date` date DEFAULT NULL COMMENT '入学时间',
  `graduation_date` date DEFAULT NULL COMMENT '预计毕业时间',
  `duration` varchar(20) DEFAULT NULL COMMENT '学制',
  `status` varchar(20) DEFAULT '在校' COMMENT '在校状态',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_student_id` (`student_id`),
  CONSTRAINT `t_student_education_ibfk_1` FOREIGN KEY (`student_id`) REFERENCES `t_student` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='学员教育经历表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `t_student_education`
--

LOCK TABLES `t_student_education` WRITE;
/*!40000 ALTER TABLE `t_student_education` DISABLE KEYS */;
INSERT INTO `t_student_education` VALUES (1,1,'某某大学','计算机科学与技术学院','计算机科学与技术','2021级','计科2101班','本科','2021-09-01','2025-06-30','4年','在校','2026-01-19 13:29:19','2026-01-19 13:29:19');
/*!40000 ALTER TABLE `t_student_education` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `t_student_exam_record`
--

DROP TABLE IF EXISTS `t_student_exam_record`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `t_student_exam_record` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '记录ID',
  `exam_id` bigint NOT NULL COMMENT '考试ID',
  `student_id` bigint NOT NULL COMMENT '学员ID',
  `start_time` datetime DEFAULT NULL COMMENT '开始时间',
  `end_time` datetime DEFAULT NULL COMMENT '结束时间',
  `score` decimal(5,2) DEFAULT NULL COMMENT '得分',
  `status` varchar(20) DEFAULT 'not_started' COMMENT '状态：not_started-未开始 in_progress-进行中 completed-已完成',
  `answers` text COMMENT '答案（JSON格式）',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_exam_student` (`exam_id`,`student_id`),
  KEY `student_id` (`student_id`),
  CONSTRAINT `t_student_exam_record_ibfk_1` FOREIGN KEY (`exam_id`) REFERENCES `t_exam` (`id`),
  CONSTRAINT `t_student_exam_record_ibfk_2` FOREIGN KEY (`student_id`) REFERENCES `t_student` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='学员考试记录表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `t_student_exam_record`
--

LOCK TABLES `t_student_exam_record` WRITE;
/*!40000 ALTER TABLE `t_student_exam_record` DISABLE KEYS */;
/*!40000 ALTER TABLE `t_student_exam_record` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `t_student_preference`
--

DROP TABLE IF EXISTS `t_student_preference`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `t_student_preference` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `student_id` bigint NOT NULL COMMENT '学员ID',
  `expected_cities` text COMMENT '期望城市(JSON数组)',
  `expected_positions` text COMMENT '期望职位(JSON数组)',
  `salary_min` decimal(10,2) DEFAULT NULL COMMENT '期望最低薪资',
  `salary_max` decimal(10,2) DEFAULT NULL COMMENT '期望最高薪资',
  `salary_unit` varchar(10) DEFAULT 'month' COMMENT '薪资单位(month/year)',
  `job_types` varchar(100) DEFAULT NULL COMMENT '工作性质(多个用逗号分隔:全职,实习,兼职)',
  `company_sizes` varchar(100) DEFAULT NULL COMMENT '企业规模偏好(多个用逗号分隔)',
  `seeking_status` varchar(20) DEFAULT 'actively' COMMENT '求职状态(actively/passively/not_seeking/employed)',
  `self_evaluation` text COMMENT '自我评价',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_student_id` (`student_id`),
  CONSTRAINT `t_student_preference_ibfk_1` FOREIGN KEY (`student_id`) REFERENCES `t_student` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='学员求职偏好表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `t_student_preference`
--

LOCK TABLES `t_student_preference` WRITE;
/*!40000 ALTER TABLE `t_student_preference` DISABLE KEYS */;
INSERT INTO `t_student_preference` VALUES (1,1,'[\"北京\",\"上海\",\"深圳\"]','[\"Java开发工程师\",\"后端开发工程师\"]',8000.00,15000.00,'month','全职,实习','中型企业,大型企业','actively','本人热爱编程，具有良好的学习能力和团队协作精神，希望能在一个有挑战性的环境中不断提升自己','2026-01-19 13:09:27','2026-01-19 13:09:27');
/*!40000 ALTER TABLE `t_student_preference` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `t_student_project`
--

DROP TABLE IF EXISTS `t_student_project`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `t_student_project` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `student_id` bigint NOT NULL COMMENT '学员ID',
  `project_name` varchar(200) NOT NULL COMMENT '项目名称',
  `role` varchar(100) DEFAULT NULL COMMENT '担任角色',
  `start_date` date DEFAULT NULL COMMENT '开始时间',
  `end_date` date DEFAULT NULL COMMENT '结束时间',
  `description` text COMMENT '项目描述',
  `technologies` text COMMENT '使用技术(多个技术用逗号分隔)',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_student_id` (`student_id`),
  CONSTRAINT `t_student_project_ibfk_1` FOREIGN KEY (`student_id`) REFERENCES `t_student` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='学员项目经验表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `t_student_project`
--

LOCK TABLES `t_student_project` WRITE;
/*!40000 ALTER TABLE `t_student_project` DISABLE KEYS */;
INSERT INTO `t_student_project` VALUES (1,1,'在线图书商城系统','后端开发','2023-03-01','2023-06-30','基于Spring Boot + Vue的在线图书商城，实现了用户管理、商品管理、购物车、订单等功能','Java,Spring Boot,Vue,MySQL','2026-01-19 13:09:27','2026-01-19 13:09:27'),(2,1,'学生信息管理系统','全栈开发','2023-09-01','2023-12-31','为学校开发的学生信息管理系统，支持学生信息的增删改查、成绩管理、课程管理等功能','Vue,Element Plus,Java,MyBatis-Plus','2026-01-19 13:09:27','2026-01-19 13:09:27');
/*!40000 ALTER TABLE `t_student_project` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `t_student_resume`
--

DROP TABLE IF EXISTS `t_student_resume`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `t_student_resume` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `student_id` bigint NOT NULL COMMENT '学员ID',
  `resume_name` varchar(200) NOT NULL COMMENT '简历名称',
  `file_url` varchar(500) NOT NULL COMMENT '文件URL',
  `file_size` varchar(20) DEFAULT NULL COMMENT '文件大小',
  `upload_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '上传时间',
  PRIMARY KEY (`id`),
  KEY `idx_student_id` (`student_id`),
  CONSTRAINT `t_student_resume_ibfk_1` FOREIGN KEY (`student_id`) REFERENCES `t_student` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=12 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='学员简历表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `t_student_resume`
--

LOCK TABLES `t_student_resume` WRITE;
/*!40000 ALTER TABLE `t_student_resume` DISABLE KEYS */;
INSERT INTO `t_student_resume` VALUES (11,1,'电子签名.pdf','http://localhost:8080/api/uploads/resume/2026/01/20/650fe011628e4146811d6f830667f443.pdf','57898','2026-01-20 11:03:47');
/*!40000 ALTER TABLE `t_student_resume` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `t_student_skill`
--

DROP TABLE IF EXISTS `t_student_skill`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `t_student_skill` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `student_id` bigint NOT NULL COMMENT '学员ID',
  `skill_name` varchar(100) NOT NULL COMMENT '技能名称',
  `skill_level` varchar(20) NOT NULL COMMENT '熟练程度(了解/入门/熟悉/掌握/精通)',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_student_id` (`student_id`),
  CONSTRAINT `t_student_skill_ibfk_1` FOREIGN KEY (`student_id`) REFERENCES `t_student` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='学员技能标签表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `t_student_skill`
--

LOCK TABLES `t_student_skill` WRITE;
/*!40000 ALTER TABLE `t_student_skill` DISABLE KEYS */;
INSERT INTO `t_student_skill` VALUES (1,1,'Java','掌握','2026-01-19 13:09:27','2026-01-19 13:09:27'),(2,1,'Vue.js','熟悉','2026-01-19 13:09:27','2026-01-19 13:09:27'),(3,1,'MySQL','掌握','2026-01-19 13:09:27','2026-01-19 13:09:27'),(4,1,'Python','入门','2026-01-19 13:09:27','2026-01-19 13:09:27');
/*!40000 ALTER TABLE `t_student_skill` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `t_talent_pool`
--

DROP TABLE IF EXISTS `t_talent_pool`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `t_talent_pool` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '人才ID',
  `student_id` bigint NOT NULL COMMENT '学员ID',
  `company_id` bigint NOT NULL COMMENT '企业ID',
  `source` varchar(50) DEFAULT NULL COMMENT '来源：application-求职推荐 interview-面试推荐',
  `status` varchar(20) DEFAULT 'active' COMMENT '状态：active-活跃 inactive-不活跃',
  `notes` text COMMENT '备注',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `position_name` varchar(100) DEFAULT NULL COMMENT '意向职位',
  `resume_id` bigint DEFAULT NULL COMMENT '绠?巻ID',
  `source_id` bigint DEFAULT NULL COMMENT '鏉ユ簮ID锛堝?鐢宠?ID銆侀潰璇旾D锛',
  `tags` varchar(500) DEFAULT NULL COMMENT '浜烘墠鏍囩?锛堝?涓?爣绛剧敤閫楀彿鍒嗛殧锛',
  `category` varchar(50) DEFAULT NULL COMMENT '浜烘墠鍒嗙被锛歠rontend-鍓嶇? backend-鍚庣? mobile-绉诲姩绔?product-浜у搧 design-璁捐? operation-杩愯惀 other-鍏朵粬',
  `skill_tags` varchar(500) DEFAULT NULL COMMENT '鎶?兘鏍囩?锛圝SON鏁扮粍鏍煎紡锛',
  `experience_level` varchar(50) DEFAULT NULL COMMENT '缁忛獙绾у埆锛歫unior-鍒濈骇 middle-涓?骇 senior-楂樼骇 expert-涓撳?',
  `education` varchar(50) DEFAULT NULL COMMENT '瀛﹀巻',
  `expected_salary_min` decimal(10,2) DEFAULT NULL COMMENT '鏈熸湜钖?祫鏈?綆',
  `expected_salary_max` decimal(10,2) DEFAULT NULL COMMENT '鏈熸湜钖?祫鏈?珮',
  `expected_city` varchar(100) DEFAULT NULL COMMENT '鏈熸湜鍩庡競',
  `rating` int DEFAULT '3' COMMENT '浜烘墠璇勫垎锛?-5鏄燂級',
  `priority` varchar(20) DEFAULT 'normal' COMMENT '浼樺厛绾э細low-浣?normal-涓?high-楂?urgent-绱ф?',
  `last_contact_date` date DEFAULT NULL COMMENT '鏈?悗鑱旂郴鏃ユ湡',
  `last_contact_method` varchar(50) DEFAULT NULL COMMENT '鏈?悗鑱旂郴鏂瑰紡锛歟mail-閭?欢 phone-鐢佃瘽 message-绔欏唴淇?interview-闈㈣瘯',
  `next_contact_date` date DEFAULT NULL COMMENT '涓嬫?鑱旂郴鏃ユ湡',
  `remark` text COMMENT '澶囨敞淇℃伅',
  `contact_count` int DEFAULT '0' COMMENT '鑱旂郴娆℃暟',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_student_company` (`student_id`,`company_id`),
  KEY `company_id` (`company_id`),
  CONSTRAINT `t_talent_pool_ibfk_1` FOREIGN KEY (`student_id`) REFERENCES `t_student` (`id`),
  CONSTRAINT `t_talent_pool_ibfk_2` FOREIGN KEY (`company_id`) REFERENCES `t_company` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='人才库表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `t_talent_pool`
--

LOCK TABLES `t_talent_pool` WRITE;
/*!40000 ALTER TABLE `t_talent_pool` DISABLE KEYS */;
INSERT INTO `t_talent_pool` VALUES (2,5,2,'application','inactive','暂不匹配','2026-01-18 15:19:19','2026-01-18 15:19:19',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,3,'normal',NULL,NULL,NULL,NULL,0),(3,1,1,'manual','active',NULL,'2026-01-20 16:58:33','2026-01-20 17:00:33','前端工程师',NULL,NULL,'Vue,React','frontend','测试',NULL,NULL,NULL,NULL,NULL,3,'high',NULL,NULL,NULL,'测试',0);
/*!40000 ALTER TABLE `t_talent_pool` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `t_teacher`
--

DROP TABLE IF EXISTS `t_teacher`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `t_teacher` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '教师ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `school_id` bigint DEFAULT NULL COMMENT '所属学校ID',
  `teacher_no` varchar(50) NOT NULL COMMENT '教师工号',
  `name` varchar(50) DEFAULT NULL COMMENT '姓名',
  `gender` int DEFAULT NULL COMMENT '性别：1-男 2-女',
  `id_card` varchar(18) DEFAULT NULL COMMENT '韬?唤璇佸彿',
  `birth_date` date DEFAULT NULL COMMENT '鍑虹敓鏃ユ湡',
  `address` varchar(200) DEFAULT NULL COMMENT '瀹跺涵鍦板潃',
  `phone` varchar(20) DEFAULT NULL COMMENT '手机号',
  `email` varchar(100) DEFAULT NULL COMMENT '邮箱',
  `department` varchar(100) DEFAULT NULL COMMENT '部门',
  `title` varchar(50) DEFAULT NULL COMMENT '职称',
  `education` varchar(50) DEFAULT NULL COMMENT '学历',
  `entry_date` date DEFAULT NULL COMMENT '鍏ヨ亴鏃ユ湡',
  `specialization` varchar(100) DEFAULT NULL COMMENT '专业方向',
  `description` text COMMENT '简介',
  `is_deleted` int DEFAULT '0' COMMENT '是否删除：1-已删除 0-未删除',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `teacher_no` (`teacher_no`),
  KEY `user_id` (`user_id`),
  KEY `school_id` (`school_id`),
  CONSTRAINT `t_teacher_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `t_user` (`id`),
  CONSTRAINT `t_teacher_ibfk_2` FOREIGN KEY (`school_id`) REFERENCES `t_school` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='教师表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `t_teacher`
--

LOCK TABLES `t_teacher` WRITE;
/*!40000 ALTER TABLE `t_teacher` DISABLE KEYS */;
INSERT INTO `t_teacher` VALUES (1,4,NULL,'T2021001','王教授',1,NULL,NULL,NULL,'13800001001','wang@tsinghua.edu.cn','计算机系','教授',NULL,NULL,NULL,NULL,0,'2026-01-18 15:19:19','2026-01-18 15:19:19'),(2,5,NULL,'T2021002','李老师',2,NULL,NULL,NULL,'13800001002','li2@tsinghua.edu.cn','软件学院','讲师',NULL,NULL,NULL,NULL,0,'2026-01-18 15:19:19','2026-01-18 15:19:19'),(4,25,1,'111','1111',1,'14124','2015-01-01','1231','18034929592','111@111.com','计算机学院','教授','博士','2026-01-01','人工智能','131',0,'2026-01-19 09:59:02','2026-01-19 09:59:02');
/*!40000 ALTER TABLE `t_teacher` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `t_teaching_material`
--

DROP TABLE IF EXISTS `t_teaching_material`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `t_teaching_material` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '资料ID',
  `course_id` bigint DEFAULT NULL COMMENT '课程ID',
  `teacher_id` bigint DEFAULT NULL COMMENT '鏁欏笀ID',
  `category` varchar(100) DEFAULT NULL COMMENT '鍒嗙被鏍囩?',
  `tags` varchar(500) DEFAULT NULL COMMENT '鏍囩?(閫楀彿鍒嗛殧)',
  `title` varchar(200) NOT NULL COMMENT '资料标题',
  `material_type` varchar(20) DEFAULT NULL COMMENT '资料类型：document-文档 video-视频 other-其他',
  `file_url` varchar(255) DEFAULT NULL COMMENT '文件URL',
  `file_size` bigint DEFAULT NULL COMMENT '鏂囦欢澶у皬(瀛楄妭)',
  `description` text COMMENT '资料描述',
  `is_public` tinyint DEFAULT '0' COMMENT '鏄?惁瀵瑰?鍛樺叕寮?細0-绉佹湁 1-鍏?紑',
  `download_count` int DEFAULT '0' COMMENT '涓嬭浇娆℃暟',
  `view_count` int DEFAULT '0' COMMENT '鏌ョ湅娆℃暟',
  `status` varchar(20) DEFAULT 'active' COMMENT '鐘舵?锛歛ctive-婵?椿 inactive-鍋滅敤',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `course_id` (`course_id`),
  CONSTRAINT `t_teaching_material_ibfk_1` FOREIGN KEY (`course_id`) REFERENCES `t_course` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='教学资料表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `t_teaching_material`
--

LOCK TABLES `t_teaching_material` WRITE;
/*!40000 ALTER TABLE `t_teaching_material` DISABLE KEYS */;
INSERT INTO `t_teaching_material` VALUES (1,5,1,'测试','测试','电子签名','document','/uploads/materials/202601/c9489fa4-f5b5-4682-8d22-02ee63d8d1a6.pdf',57898,'测试',0,3,1,'active','2026-01-19 19:34:30','2026-01-19 19:34:30');
/*!40000 ALTER TABLE `t_teaching_material` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `t_teaching_plan`
--

DROP TABLE IF EXISTS `t_teaching_plan`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `t_teaching_plan` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '鏁欏?璁″垝ID',
  `course_id` bigint NOT NULL COMMENT '璇剧▼ID锛堝叧鑱攖_course锛',
  `lesson_number` int NOT NULL COMMENT '璇炬?搴忓彿锛堢?鍑犳?璇撅紝浠?寮??锛',
  `week_number` int NOT NULL COMMENT '鍛ㄦ?锛堝?搴攖imetable鐨剋eek_number锛',
  `title` varchar(200) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '鏈??璇炬爣棰',
  `content` text COLLATE utf8mb4_unicode_ci COMMENT '鏁欏?鍐呭?鎻忚堪',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '鍒涘缓鏃堕棿',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '鏇存柊鏃堕棿',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_course_lesson` (`course_id`,`lesson_number`),
  KEY `idx_course_id` (`course_id`),
  KEY `idx_week_number` (`week_number`),
  CONSTRAINT `fk_teaching_plan_course` FOREIGN KEY (`course_id`) REFERENCES `t_course` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=14 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='鏁欏?璁″垝琛';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `t_teaching_plan`
--

LOCK TABLES `t_teaching_plan` WRITE;
/*!40000 ALTER TABLE `t_teaching_plan` DISABLE KEYS */;
INSERT INTO `t_teaching_plan` VALUES (1,1,1,1,'第1次课','学习1','2026-01-20 00:23:26','2026-01-20 00:23:26'),(2,1,2,2,'第2次课','','2026-01-20 00:23:27','2026-01-20 00:23:27'),(3,1,3,3,'第3次课','','2026-01-20 00:23:27','2026-01-20 00:23:27'),(4,1,4,4,'第4次课','','2026-01-20 00:23:27','2026-01-20 00:23:27'),(5,1,5,5,'第5次课','','2026-01-20 00:23:27','2026-01-20 00:23:27'),(6,1,6,6,'第6次课','','2026-01-20 00:23:27','2026-01-20 00:23:27'),(7,1,7,7,'第7次课','','2026-01-20 00:23:27','2026-01-20 00:23:27'),(8,1,8,8,'第8次课','','2026-01-20 00:23:27','2026-01-20 00:23:27'),(9,1,9,9,'第9次课','','2026-01-20 00:23:27','2026-01-20 00:23:27'),(10,1,10,10,'第10次课','','2026-01-20 00:23:27','2026-01-20 00:23:27'),(11,1,11,11,'第11次课','','2026-01-20 00:23:27','2026-01-20 00:23:27'),(12,1,12,12,'第12次课','','2026-01-20 00:23:27','2026-01-20 00:23:27');
/*!40000 ALTER TABLE `t_teaching_plan` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `t_tech_stack_template`
--

DROP TABLE IF EXISTS `t_tech_stack_template`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `t_tech_stack_template` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '妯℃澘ID',
  `template_name` varchar(100) NOT NULL COMMENT '妯℃澘鍚嶇О',
  `dimensions` json NOT NULL COMMENT '鎶?兘缁村害锛圝SON鏍煎紡锛',
  `position_type` varchar(50) DEFAULT NULL COMMENT '閫傜敤宀椾綅绫诲瀷',
  `is_default` tinyint DEFAULT '1' COMMENT '鏄?惁榛樿?妯℃澘锛?-鍚?1-鏄',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '鍒涘缓鏃堕棿',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=16 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='鎶?湳鏍堟ā鏉胯〃';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `t_tech_stack_template`
--

LOCK TABLES `t_tech_stack_template` WRITE;
/*!40000 ALTER TABLE `t_tech_stack_template` DISABLE KEYS */;
INSERT INTO `t_tech_stack_template` VALUES (6,'Java开发工程师','[{\"name\": \"Java基础\", \"value\": 8}, {\"name\": \"Spring框架\", \"value\": 7}, {\"name\": \"MySQL数据库\", \"value\": 6}, {\"name\": \"Redis缓存\", \"value\": 5}, {\"name\": \"微服务架构\", \"value\": 6}, {\"name\": \"项目经验\", \"value\": 7}]','backend',1,'2026-01-20 19:49:27'),(7,'前端开发工程师','[{\"name\": \"HTML/CSS\", \"value\": 8}, {\"name\": \"JavaScript\", \"value\": 8}, {\"name\": \"Vue框架\", \"value\": 7}, {\"name\": \"工程化工具\", \"value\": 6}, {\"name\": \"性能优化\", \"value\": 5}, {\"name\": \"项目经验\", \"value\": 6}]','frontend',1,'2026-01-20 19:49:27'),(8,'Python开发工程师','[{\"name\": \"Python基础\", \"value\": 8}, {\"name\": \"Django/Flask\", \"value\": 7}, {\"name\": \"数据处理\", \"value\": 6}, {\"name\": \"机器学习\", \"value\": 5}, {\"name\": \"数据库\", \"value\": 6}, {\"name\": \"项目经验\", \"value\": 6}]','backend',1,'2026-01-20 19:49:27'),(9,'全栈工程师','[{\"name\": \"Java/Python\", \"value\": 6}, {\"name\": \"前端开发\", \"value\": 7}, {\"name\": \"数据库\", \"value\": 6}, {\"name\": \"系统设计\", \"value\": 5}, {\"name\": \"DevOps\", \"value\": 5}, {\"name\": \"项目经验\", \"value\": 7}]','fullstack',1,'2026-01-20 19:49:27'),(10,'数据分析工程师','[{\"name\": \"Python\", \"value\": 8}, {\"name\": \"SQL\", \"value\": 8}, {\"name\": \"数据可视化\", \"value\": 7}, {\"name\": \"统计分析\", \"value\": 6}, {\"name\": \"机器学习\", \"value\": 5}, {\"name\": \"业务理解\", \"value\": 6}]','data',1,'2026-01-20 19:49:27'),(11,'Java开发工程师','[{\"name\": \"Java基础\", \"value\": 8}, {\"name\": \"Spring框架\", \"value\": 7}, {\"name\": \"MySQL数据库\", \"value\": 6}, {\"name\": \"Redis缓存\", \"value\": 5}, {\"name\": \"微服务架构\", \"value\": 6}, {\"name\": \"项目经验\", \"value\": 7}]','backend',1,'2026-01-20 19:50:17'),(12,'前端开发工程师','[{\"name\": \"HTML/CSS\", \"value\": 8}, {\"name\": \"JavaScript\", \"value\": 8}, {\"name\": \"Vue框架\", \"value\": 7}, {\"name\": \"工程化工具\", \"value\": 6}, {\"name\": \"性能优化\", \"value\": 5}, {\"name\": \"项目经验\", \"value\": 6}]','frontend',1,'2026-01-20 19:50:17'),(13,'Python开发工程师','[{\"name\": \"Python基础\", \"value\": 8}, {\"name\": \"Django/Flask\", \"value\": 7}, {\"name\": \"数据处理\", \"value\": 6}, {\"name\": \"机器学习\", \"value\": 5}, {\"name\": \"数据库\", \"value\": 6}, {\"name\": \"项目经验\", \"value\": 6}]','backend',1,'2026-01-20 19:50:17'),(14,'全栈工程师','[{\"name\": \"Java/Python\", \"value\": 6}, {\"name\": \"前端开发\", \"value\": 7}, {\"name\": \"数据库\", \"value\": 6}, {\"name\": \"系统设计\", \"value\": 5}, {\"name\": \"DevOps\", \"value\": 5}, {\"name\": \"项目经验\", \"value\": 7}]','fullstack',1,'2026-01-20 19:50:17'),(15,'数据分析工程师','[{\"name\": \"Python\", \"value\": 8}, {\"name\": \"SQL\", \"value\": 8}, {\"name\": \"数据可视化\", \"value\": 7}, {\"name\": \"统计分析\", \"value\": 6}, {\"name\": \"机器学习\", \"value\": 5}, {\"name\": \"业务理解\", \"value\": 6}]','data',1,'2026-01-20 19:50:17');
/*!40000 ALTER TABLE `t_tech_stack_template` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `t_timetable`
--

DROP TABLE IF EXISTS `t_timetable`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `t_timetable` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '课程表ID',
  `course_id` bigint DEFAULT NULL COMMENT '课程ID',
  `student_id` bigint DEFAULT NULL COMMENT '学员ID',
  `teacher_id` bigint DEFAULT NULL COMMENT '教师ID',
  `classroom_id` bigint DEFAULT NULL COMMENT '教室ID',
  `week_number` int DEFAULT '1' COMMENT '周次',
  `day_of_week` int DEFAULT NULL COMMENT '星期几：1-7（周一到周日）',
  `start_period` int DEFAULT NULL COMMENT '开始节次',
  `end_period` int DEFAULT NULL COMMENT '结束节次',
  `semester` varchar(20) DEFAULT NULL COMMENT '学期',
  `school_year` varchar(20) DEFAULT NULL COMMENT '学年',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `course_id` (`course_id`),
  KEY `student_id` (`student_id`),
  KEY `teacher_id` (`teacher_id`),
  KEY `classroom_id` (`classroom_id`),
  CONSTRAINT `t_timetable_ibfk_1` FOREIGN KEY (`course_id`) REFERENCES `t_course` (`id`),
  CONSTRAINT `t_timetable_ibfk_2` FOREIGN KEY (`student_id`) REFERENCES `t_student` (`id`),
  CONSTRAINT `t_timetable_ibfk_3` FOREIGN KEY (`teacher_id`) REFERENCES `t_teacher` (`id`),
  CONSTRAINT `t_timetable_ibfk_4` FOREIGN KEY (`classroom_id`) REFERENCES `t_classroom` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='课程表（时间表）';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `t_timetable`
--

LOCK TABLES `t_timetable` WRITE;
/*!40000 ALTER TABLE `t_timetable` DISABLE KEYS */;
INSERT INTO `t_timetable` VALUES (1,1,1,1,1,1,1,1,2,'2024-2025-1','2024-2025','2026-01-18 15:19:19','2026-01-18 15:19:19'),(2,2,1,1,1,1,3,1,2,'2024-2025-1','2024-2025','2026-01-18 15:19:19','2026-01-18 15:19:19'),(3,3,1,1,2,1,2,1,2,'2024-2025-1','2024-2025','2026-01-18 15:19:19','2026-01-18 15:19:19'),(4,1,1,1,1,2,1,3,4,'2024-2025-1','2024-2025','2026-01-18 15:19:19','2026-01-18 15:19:19');
/*!40000 ALTER TABLE `t_timetable` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `t_user`
--

DROP TABLE IF EXISTS `t_user`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `t_user` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '用户ID',
  `username` varchar(50) NOT NULL COMMENT '用户名',
  `password` varchar(255) NOT NULL COMMENT '密码（加密）',
  `real_name` varchar(50) DEFAULT NULL COMMENT '真实姓名',
  `phone` varchar(20) DEFAULT NULL COMMENT '手机号',
  `email` varchar(100) DEFAULT NULL COMMENT '邮箱',
  `avatar` varchar(255) DEFAULT NULL COMMENT '头像URL',
  `role_id` bigint NOT NULL COMMENT '角色ID',
  `school_id` bigint DEFAULT NULL COMMENT '所属学校ID',
  `company_id` bigint DEFAULT NULL COMMENT '所属企业ID（企业对接人专用）',
  `student_number` varchar(50) DEFAULT NULL COMMENT '学号（学员专用）',
  `resume_url` varchar(255) DEFAULT NULL COMMENT '简历文件URL（学员专用）',
  `status` int DEFAULT '1' COMMENT '状态：1-正常 0-停用',
  `is_deleted` int DEFAULT '0' COMMENT '是否删除：1-已删除 0-未删除',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `username` (`username`),
  KEY `idx_username` (`username`),
  KEY `idx_role_id` (`role_id`),
  CONSTRAINT `t_user_ibfk_1` FOREIGN KEY (`role_id`) REFERENCES `t_role` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=26 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='用户表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `t_user`
--

LOCK TABLES `t_user` WRITE;
/*!40000 ALTER TABLE `t_user` DISABLE KEYS */;
INSERT INTO `t_user` VALUES (1,'admin','$2a$10$qmFLwX8HzMX5IruHJeDgtuD71lR.Ng2nit5kOJGpFzuOSbTpB2AJq','系统管理员','13800000000','admin@teaching.com',NULL,1,NULL,NULL,NULL,NULL,1,0,'2026-01-18 15:19:19','2026-01-18 17:52:58'),(2,'college_head1','$2a$10$Em4V9p6JreU0AkZd4mXl7eJFV7rRfJ8HDnE/.umMqeaOYlgHAV/TO','张院长（测试修改）','13800000001','zhang@tsinghua.edu.cn',NULL,2,1,NULL,NULL,NULL,1,0,'2026-01-18 15:19:19','2026-01-18 17:53:01'),(4,'teacher01','$2a$10$Em4V9p6JreU0AkZd4mXl7eJFV7rRfJ8HDnE/.umMqeaOYlgHAV/TO','王教授','13800001001','wang@tsinghua.edu.cn',NULL,3,1,NULL,NULL,NULL,1,0,'2026-01-18 15:19:19','2026-01-18 16:04:48'),(5,'teacher02','$2a$10$Em4V9p6JreU0AkZd4mXl7eJFV7rRfJ8HDnE/.umMqeaOYlgHAV/TO','李老师','13800001002','li2@tsinghua.edu.cn',NULL,3,1,NULL,NULL,NULL,1,0,'2026-01-18 15:19:19','2026-01-18 16:04:48'),(7,'student01','$2a$10$Em4V9p6JreU0AkZd4mXl7eJFV7rRfJ8HDnE/.umMqeaOYlgHAV/TO','张三','13800002001','zhangsan@student.com',NULL,4,1,NULL,'2021001001','http://localhost:8080/api/uploads/resume/2026/01/20/650fe011628e4146811d6f830667f443.pdf',1,0,'2026-01-18 15:19:19','2026-01-20 15:45:18'),(8,'student02','$2a$10$Em4V9p6JreU0AkZd4mXl7eJFV7rRfJ8HDnE/.umMqeaOYlgHAV/TO','李四','13800002002','lisi@student.com',NULL,4,1,NULL,'2021001002',NULL,1,0,'2026-01-18 15:19:19','2026-01-18 16:04:48'),(9,'student03','$2a$10$Em4V9p6JreU0AkZd4mXl7eJFV7rRfJ8HDnE/.umMqeaOYlgHAV/TO','王五','13800002003','wangwu@student.com',NULL,4,1,NULL,'2021001003',NULL,1,0,'2026-01-18 15:19:19','2026-01-18 16:04:48'),(10,'student04','$2a$10$Em4V9p6JreU0AkZd4mXl7eJFV7rRfJ8HDnE/.umMqeaOYlgHAV/TO','赵六','13800002004','zhaoliu@student.com',NULL,4,2,NULL,'2021002001',NULL,1,0,'2026-01-18 15:19:19','2026-01-18 16:04:48'),(11,'student05','$2a$10$Em4V9p6JreU0AkZd4mXl7eJFV7rRfJ8HDnE/.umMqeaOYlgHAV/TO','钱七','13800002005','qianqi@student.com',NULL,4,2,NULL,'2021002002',NULL,1,0,'2026-01-18 15:19:19','2026-01-18 16:04:48'),(12,'student06','$2a$10$Em4V9p6JreU0AkZd4mXl7eJFV7rRfJ8HDnE/.umMqeaOYlgHAV/TO','孙八','13800002006','sunba@student.com',NULL,4,2,NULL,'2021002003',NULL,1,0,'2026-01-18 15:19:19','2026-01-18 16:04:48'),(13,'student07','$2a$10$Em4V9p6JreU0AkZd4mXl7eJFV7rRfJ8HDnE/.umMqeaOYlgHAV/TO','周九','13800002007','zhoujiu@student.com',NULL,4,2,NULL,'2021002004',NULL,1,0,'2026-01-18 15:19:19','2026-01-18 16:04:48'),(14,'student08','$2a$10$Em4V9p6JreU0AkZd4mXl7eJFV7rRfJ8HDnE/.umMqeaOYlgHAV/TO','吴十','13800002008','wushi@student.com',NULL,4,1,NULL,'2021001004',NULL,1,0,'2026-01-18 15:19:19','2026-01-18 16:04:48'),(15,'student09','$2a$10$Em4V9p6JreU0AkZd4mXl7eJFV7rRfJ8HDnE/.umMqeaOYlgHAV/TO','郑一','13800002009','zhengyi@student.com',NULL,4,2,NULL,'2021002005',NULL,1,0,'2026-01-18 15:19:19','2026-01-18 16:04:48'),(16,'student10','$2a$10$Em4V9p6JreU0AkZd4mXl7eJFV7rRfJ8HDnE/.umMqeaOYlgHAV/TO','王二','13800002010','wanger@student.com',NULL,4,2,NULL,'2021002006',NULL,1,0,'2026-01-18 15:19:19','2026-01-18 16:04:48'),(17,'enterprise01','$2a$10$Em4V9p6JreU0AkZd4mXl7eJFV7rRfJ8HDnE/.umMqeaOYlgHAV/TO','张HR','13800003001','zhanghr@alibaba.com',NULL,5,NULL,1,NULL,NULL,1,0,'2026-01-18 15:19:19','2026-01-18 16:04:48'),(18,'enterprise02','$2a$10$Em4V9p6JreU0AkZd4mXl7eJFV7rRfJ8HDnE/.umMqeaOYlgHAV/TO','李HR','13800003002','lihr@tencent.com',NULL,5,NULL,2,NULL,NULL,1,0,'2026-01-18 15:19:19','2026-01-18 16:04:48'),(19,'enterprise03','$2a$10$Em4V9p6JreU0AkZd4mXl7eJFV7rRfJ8HDnE/.umMqeaOYlgHAV/TO','王HR','13800003003','wanghr@bytedance.com',NULL,5,NULL,3,NULL,NULL,1,0,'2026-01-18 15:19:19','2026-01-18 16:04:48'),(20,'enterprise04','$2a$10$Em4V9p6JreU0AkZd4mXl7eJFV7rRfJ8HDnE/.umMqeaOYlgHAV/TO','赵HR','13800003004','zhaohr@huawei.com',NULL,5,NULL,4,NULL,NULL,1,0,'2026-01-18 15:19:19','2026-01-18 16:04:48'),(21,'enterprise05','$2a$10$Em4V9p6JreU0AkZd4mXl7eJFV7rRfJ8HDnE/.umMqeaOYlgHAV/TO','钱HR','13800003005','qianhr@meituan.com',NULL,5,NULL,5,NULL,NULL,1,0,'2026-01-18 15:19:19','2026-01-18 16:04:48'),(24,'T2025001','123456','张三','13912345678','zhangsan@example.com',NULL,3,1,NULL,NULL,NULL,1,0,'2026-01-19 09:57:27','2026-01-19 09:57:27'),(25,'111','123456','1111','18034929592','111@111.com',NULL,3,1,NULL,NULL,NULL,1,0,'2026-01-19 09:59:02','2026-01-19 09:59:02');
/*!40000 ALTER TABLE `t_user` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `t_user_activity`
--

DROP TABLE IF EXISTS `t_user_activity`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `t_user_activity` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '娲诲姩ID',
  `user_id` bigint NOT NULL COMMENT '鐢ㄦ埛ID',
  `activity_type` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '娲诲姩绫诲瀷锛歭ogin-鐧诲綍 logout-鐧诲嚭 profile_update-璧勬枡淇?敼 password_change-瀵嗙爜淇?敼 course_enroll-璇剧▼娉ㄥ唽 homework_submit-浣滀笟鎻愪氦 report_submit-鏃ユ姤鎻愪氦 job_apply-鑱屼綅鐢宠? test_complete-绗旇瘯瀹屾垚 interview_attend-闈㈣瘯閫氱煡 offer_received-鏀跺埌offer',
  `title` varchar(200) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '娲诲姩鏍囬?',
  `description` text COLLATE utf8mb4_unicode_ci COMMENT '娲诲姩鎻忚堪',
  `related_id` bigint DEFAULT NULL COMMENT '鍏宠仈瀵硅薄ID锛堝?璇剧▼ID銆佷綔涓欼D绛夛級',
  `related_type` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '鍏宠仈瀵硅薄绫诲瀷',
  `ip_address` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT 'IP鍦板潃',
  `user_agent` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '鐢ㄦ埛浠ｇ悊',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '娲诲姩鏃堕棿',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_activity_type` (`activity_type`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='鐢ㄦ埛娲诲姩璁板綍琛';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `t_user_activity`
--

LOCK TABLES `t_user_activity` WRITE;
/*!40000 ALTER TABLE `t_user_activity` DISABLE KEYS */;
/*!40000 ALTER TABLE `t_user_activity` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `t_written_test`
--

DROP TABLE IF EXISTS `t_written_test`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `t_written_test` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '笔试ID',
  `application_id` bigint DEFAULT NULL,
  `position_id` bigint DEFAULT NULL COMMENT '关联职位ID',
  `student_id` bigint DEFAULT NULL,
  `test_url` varchar(255) DEFAULT NULL COMMENT '笔试链接',
  `duration` int DEFAULT NULL COMMENT '笔试时长（分钟）',
  `start_time` datetime DEFAULT NULL COMMENT '开始时间',
  `end_time` datetime DEFAULT NULL COMMENT '结束时间',
  `score` int DEFAULT NULL,
  `total_score` int DEFAULT NULL,
  `status` varchar(50) DEFAULT NULL,
  `comment` text,
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `position_id` (`position_id`),
  KEY `idx_application_id` (`application_id`),
  KEY `idx_student_id` (`student_id`),
  CONSTRAINT `t_written_test_ibfk_2` FOREIGN KEY (`position_id`) REFERENCES `t_position` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='笔试表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `t_written_test`
--

LOCK TABLES `t_written_test` WRITE;
/*!40000 ALTER TABLE `t_written_test` DISABLE KEYS */;
INSERT INTO `t_written_test` VALUES (1,3,4,4,'111',60,'2026-01-31 00:00:00','2027-01-01 00:00:00',99,100,'completed','很好','2026-01-19 11:53:11','2026-01-19 11:53:11'),(2,4,1,1,'https://exam.example.com/java-basic-test',90,'2026-01-18 14:00:00','2026-01-18 15:30:00',85,100,'completed','Java基础扎实，需加强多线程编程','2026-01-19 18:31:53','2026-01-19 18:31:53'),(3,NULL,1,1,'https://exam.example.com/java-advanced-test',120,'2026-01-25 10:00:00','2026-01-25 12:00:00',NULL,NULL,'pending',NULL,'2026-01-19 18:31:53','2026-01-19 18:31:53');
/*!40000 ALTER TABLE `t_written_test` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-01-20 20:00:41
