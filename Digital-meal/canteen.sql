-- MySQL dump 10.13  Distrib 8.0.42, for Win64 (x86_64)
--
-- Host: 127.0.0.1    Database: canpiaoguanli
-- ------------------------------------------------------
-- Server version	8.0.42

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
-- Table structure for table `agencies`
--

DROP TABLE IF EXISTS `agencies`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `agencies` (
  `AgenciesId` varchar(64) NOT NULL COMMENT '部门id',
  `AgenciesName` varchar(20) NOT NULL COMMENT '部门名称',
  `Status` int NOT NULL,
  `FK_Creator` varchar(10) NOT NULL,
  `CreateDatetime` varchar(15) NOT NULL,
  `CreateIP` varchar(15) NOT NULL,
  PRIMARY KEY (`AgenciesId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='部门';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `agencies`
--

LOCK TABLES `agencies` WRITE;
/*!40000 ALTER TABLE `agencies` DISABLE KEYS */;
/*!40000 ALTER TABLE `agencies` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `canteen`
--

DROP TABLE IF EXISTS `canteen`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `canteen` (
  `CanteenId` varchar(64) NOT NULL COMMENT '食堂id',
  `CanteenName` varchar(10) NOT NULL COMMENT '食堂名称',
  `Status` int NOT NULL,
  `FK_Creator` varchar(10) NOT NULL,
  `CreateDatetime` varchar(15) NOT NULL,
  `CreateIP` varchar(15) NOT NULL,
  PRIMARY KEY (`CanteenId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='食堂';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `canteen`
--

LOCK TABLES `canteen` WRITE;
/*!40000 ALTER TABLE `canteen` DISABLE KEYS */;
/*!40000 ALTER TABLE `canteen` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `order`
--

DROP TABLE IF EXISTS `order`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `order` (
  `OrderId` varchar(64) NOT NULL COMMENT '订单id',
  `OrderNumber` varchar(10) NOT NULL COMMENT '订单编号',
  `AgenciesId` varchar(64) NOT NULL COMMENT '部门id',
  `PassUserId` varchar(64) NOT NULL COMMENT '通过用户id',
  `Status` int NOT NULL,
  `FK_Creator` varchar(10) NOT NULL,
  `CreateDatetime` varchar(15) NOT NULL,
  `CreateIP` varchar(15) NOT NULL,
  `Suggestion` varchar(300) NOT NULL COMMENT '建议',
  `ExpirationTime` varchar(15) NOT NULL COMMENT '过期时间',
  `PassTime` varchar(10) NOT NULL COMMENT '通过时间',
  `StartUserid` varchar(64) NOT NULL COMMENT '发起者id',
  PRIMARY KEY (`OrderId`),
  KEY `order_agencies_AgenciesId_fk` (`AgenciesId`),
  KEY `order_user_UserId_fk` (`PassUserId`),
  KEY `order_user_UserId_fk_2` (`StartUserid`),
  CONSTRAINT `order_agencies_AgenciesId_fk` FOREIGN KEY (`AgenciesId`) REFERENCES `agencies` (`AgenciesId`),
  CONSTRAINT `order_user_UserId_fk` FOREIGN KEY (`PassUserId`) REFERENCES `user` (`UserId`),
  CONSTRAINT `order_user_UserId_fk_2` FOREIGN KEY (`StartUserid`) REFERENCES `user` (`UserId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='订单';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `order`
--

LOCK TABLES `order` WRITE;
/*!40000 ALTER TABLE `order` DISABLE KEYS */;
/*!40000 ALTER TABLE `order` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `ott`
--

DROP TABLE IF EXISTS `ott`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `ott` (
  `OrderId` varchar(64) NOT NULL COMMENT '订单id',
  `TypeID` varchar(64) NOT NULL COMMENT '类型id',
  `OredrAmount` int NOT NULL COMMENT '数量',
  `Status` int NOT NULL,
  `FK_Creator` varchar(10) DEFAULT NULL,
  `CreateDatetime` varchar(15) NOT NULL,
  `CreateIP` varchar(15) NOT NULL,
  `OTTID` varchar(64) NOT NULL,
  PRIMARY KEY (`OTTID`),
  KEY `ott_order_OrderId_fk` (`OrderId`),
  KEY `ott_tickettype_TypeID_fk` (`TypeID`),
  CONSTRAINT `ott_order_OrderId_fk` FOREIGN KEY (`OrderId`) REFERENCES `order` (`OrderId`),
  CONSTRAINT `ott_tickettype_TypeID_fk` FOREIGN KEY (`TypeID`) REFERENCES `tickettype` (`TypeID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='订单下单项';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `ott`
--

LOCK TABLES `ott` WRITE;
/*!40000 ALTER TABLE `ott` DISABLE KEYS */;
/*!40000 ALTER TABLE `ott` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `role`
--

DROP TABLE IF EXISTS `role`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `role` (
  `RoleId` varchar(64) NOT NULL COMMENT '角色id',
  `RoleName` varchar(20) NOT NULL COMMENT '角色姓名',
  `Status` int NOT NULL,
  `FK_Creator` varchar(10) NOT NULL,
  `CreateDateIP` varchar(15) NOT NULL,
  `CreateIP` varchar(15) NOT NULL,
  PRIMARY KEY (`RoleId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='角色';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `role`
--

LOCK TABLES `role` WRITE;
/*!40000 ALTER TABLE `role` DISABLE KEYS */;
/*!40000 ALTER TABLE `role` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `stall`
--

DROP TABLE IF EXISTS `stall`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `stall` (
  `StallId` varchar(64) NOT NULL COMMENT '档口id',
  `StallName` varchar(10) NOT NULL COMMENT '档口名称',
  `CanteenId` varchar(64) NOT NULL COMMENT '食堂id',
  `Statue` int NOT NULL,
  `FK_Creator` varchar(10) NOT NULL,
  `CreateDatetime` varchar(15) NOT NULL,
  `CreateIP` varchar(15) DEFAULT NULL,
  `StallLocation` varchar(10) NOT NULL COMMENT '档口位置',
  `StallNumber` varchar(64) NOT NULL,
  `principal` varchar(11) NOT NULL COMMENT '负责人电话',
  `PrincipalId` varchar(64) NOT NULL,
  PRIMARY KEY (`StallId`),
  KEY `stall_canteen_CanteenId_fk` (`CanteenId`),
  KEY `stall_user_UserId_fk` (`PrincipalId`),
  CONSTRAINT `stall_canteen_CanteenId_fk` FOREIGN KEY (`CanteenId`) REFERENCES `canteen` (`CanteenId`),
  CONSTRAINT `stall_user_UserId_fk` FOREIGN KEY (`PrincipalId`) REFERENCES `user` (`UserId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='档口';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `stall`
--

LOCK TABLES `stall` WRITE;
/*!40000 ALTER TABLE `stall` DISABLE KEYS */;
/*!40000 ALTER TABLE `stall` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `ticket`
--

DROP TABLE IF EXISTS `ticket`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `ticket` (
  `TickerId` varchar(64) NOT NULL COMMENT '餐票id',
  `TypeID` varchar(64) NOT NULL COMMENT '餐票类型id',
  `StallId` varchar(64) NOT NULL COMMENT '档口id',
  `UsedId` varchar(64) NOT NULL COMMENT '使用者用户id（档口人员）',
  `CheckId` varchar(64) NOT NULL COMMENT '核销人id（后勤人员）',
  `UseTime` varchar(10) DEFAULT NULL COMMENT '使用时间',
  `OrderId` varchar(64) NOT NULL COMMENT '订单id',
  `ExpirationTime` varchar(10) NOT NULL COMMENT '过期时间',
  `CheckTime` varchar(10) DEFAULT NULL COMMENT '核销时间',
  `Staus` int NOT NULL,
  `Fk_Creator` varchar(10) NOT NULL,
  `CreateDatetime` varchar(15) NOT NULL,
  `CreateIP` varchar(15) NOT NULL,
  `typename` varchar(10) NOT NULL,
  PRIMARY KEY (`TickerId`),
  KEY `ticket_tickettype_TypeID_fk` (`TypeID`),
  KEY `ticket_stall_StallId_fk` (`StallId`),
  KEY `ticket_user_UserId_fk` (`UsedId`),
  KEY `ticket_user_UserId_fk_2` (`CheckId`),
  KEY `ticket_order_OrderId_fk` (`OrderId`),
  CONSTRAINT `ticket_order_OrderId_fk` FOREIGN KEY (`OrderId`) REFERENCES `order` (`OrderId`),
  CONSTRAINT `ticket_stall_StallId_fk` FOREIGN KEY (`StallId`) REFERENCES `stall` (`StallId`),
  CONSTRAINT `ticket_tickettype_TypeID_fk` FOREIGN KEY (`TypeID`) REFERENCES `tickettype` (`TypeID`),
  CONSTRAINT `ticket_user_UserId_fk` FOREIGN KEY (`UsedId`) REFERENCES `user` (`UserId`),
  CONSTRAINT `ticket_user_UserId_fk_2` FOREIGN KEY (`CheckId`) REFERENCES `user` (`UserId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='餐票';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `ticket`
--

LOCK TABLES `ticket` WRITE;
/*!40000 ALTER TABLE `ticket` DISABLE KEYS */;
/*!40000 ALTER TABLE `ticket` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `tickettype`
--

DROP TABLE IF EXISTS `tickettype`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `tickettype` (
  `TypeID` varchar(64) NOT NULL COMMENT '类型id',
  `TypeName` varchar(10) NOT NULL COMMENT '类型名称',
  `Value` int NOT NULL COMMENT '面额',
  `CanteenId` varchar(64) NOT NULL COMMENT '食堂id',
  `Status` int NOT NULL,
  `FK_Creator` varchar(10) NOT NULL,
  `CreateDatetime` varchar(15) DEFAULT NULL,
  `CreateIP` varchar(15) NOT NULL,
  `remark` varchar(600) NOT NULL COMMENT '备注',
  PRIMARY KEY (`TypeID`),
  KEY `tickettype_canteen_CanteenId_fk` (`CanteenId`),
  CONSTRAINT `tickettype_canteen_CanteenId_fk` FOREIGN KEY (`CanteenId`) REFERENCES `canteen` (`CanteenId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='餐票类型';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `tickettype`
--

LOCK TABLES `tickettype` WRITE;
/*!40000 ALTER TABLE `tickettype` DISABLE KEYS */;
/*!40000 ALTER TABLE `tickettype` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user`
--

DROP TABLE IF EXISTS `user`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `user` (
  `UserId` varchar(64) NOT NULL COMMENT '用户id',
  `UserName` varchar(20) NOT NULL COMMENT '账号',
  `RoleId` varchar(64) NOT NULL COMMENT '角色id',
  `AgenciesId` varchar(64) NOT NULL COMMENT '部门id',
  `FullName` varchar(10) NOT NULL COMMENT '真实姓名',
  `Password` varchar(20) NOT NULL COMMENT '密码',
  `Status` int NOT NULL,
  `FK_Creator` varchar(10) NOT NULL,
  `CreateDatetime` varchar(15) NOT NULL,
  `CreateIP` varchar(15) NOT NULL,
  `phonenumber` varchar(11) NOT NULL COMMENT '电话',
  PRIMARY KEY (`UserId`),
  KEY `user_role_RoleId_fk` (`RoleId`),
  KEY `user_agencies_AgenciesId_fk` (`AgenciesId`),
  CONSTRAINT `user_agencies_AgenciesId_fk` FOREIGN KEY (`AgenciesId`) REFERENCES `agencies` (`AgenciesId`),
  CONSTRAINT `user_role_RoleId_fk` FOREIGN KEY (`RoleId`) REFERENCES `role` (`RoleId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='用户';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user`
--

LOCK TABLES `user` WRITE;
/*!40000 ALTER TABLE `user` DISABLE KEYS */;
/*!40000 ALTER TABLE `user` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2025-07-28 11:20:02
