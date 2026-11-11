-- MySQL dump 10.13  Distrib 26.7.0, for macos26.6 (arm64)
--
-- Host: localhost    Database: ev_charging_db
-- ------------------------------------------------------
-- Server version	26.7.0

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
SET @MYSQLDUMP_TEMP_LOG_BIN = @@SESSION.SQL_LOG_BIN;
SET @@SESSION.SQL_LOG_BIN= 0;

--
-- GTID state at the beginning of the backup 
--

SET @@GLOBAL.GTID_PURGED=/*!80000 '+'*/ '58c90ea4-b10e-11f1-8314-7fa7c8f67771:1-141';

--
-- Table structure for table `bookings`
--

DROP TABLE IF EXISTS `bookings`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `bookings` (
  `booking_id` int NOT NULL AUTO_INCREMENT,
  `user_id` int NOT NULL,
  `station_id` int NOT NULL,
  `charger_id` int NOT NULL,
  `booking_date` date NOT NULL,
  `start_time` time NOT NULL,
  `duration` int NOT NULL,
  `cost` decimal(10,2) NOT NULL,
  `booking_fee` decimal(10,2) NOT NULL DEFAULT '30.00',
  `energy_consumed` decimal(10,3) NOT NULL DEFAULT '0.000',
  `charging_cost` decimal(10,2) NOT NULL DEFAULT '0.00',
  `status` varchar(20) DEFAULT 'CONFIRMED',
  PRIMARY KEY (`booking_id`),
  KEY `user_id` (`user_id`),
  KEY `station_id` (`station_id`),
  KEY `charger_id` (`charger_id`),
  CONSTRAINT `bookings_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`),
  CONSTRAINT `bookings_ibfk_2` FOREIGN KEY (`station_id`) REFERENCES `stations` (`station_id`),
  CONSTRAINT `bookings_ibfk_3` FOREIGN KEY (`charger_id`) REFERENCES `chargers` (`charger_id`)
) ENGINE=InnoDB AUTO_INCREMENT=26 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `bookings`
--

LOCK TABLES `bookings` WRITE;
/*!40000 ALTER TABLE `bookings` DISABLE KEYS */;
INSERT INTO `bookings` VALUES (1,1,1,1,'2026-09-17','10:00:00',60,300.00,30.00,0.000,0.00,'CANCELLED'),(2,1,1,1,'2026-09-18','10:00:00',60,300.00,30.00,0.000,0.00,'CANCELLED'),(3,3,1,2,'2026-09-17','10:00:00',2,10.00,30.00,0.000,0.00,'CANCELLED'),(4,3,1,2,'2026-09-18','11:00:00',2,10.00,30.00,0.000,0.00,'CANCELLED'),(5,3,1,2,'2026-09-17','10:00:00',2,10.00,30.00,0.000,0.00,'CANCELLED'),(6,1,1,2,'2026-11-25','10:00:00',30,150.00,30.00,0.000,0.00,'CONFIRMED'),(7,9,5,4,'2026-11-25','10:00:00',30,150.00,30.00,0.000,0.00,'CONFIRMED'),(8,9,1,2,'2026-11-25','10:00:00',30,150.00,30.00,0.000,0.00,'CANCELLED'),(9,9,1,2,'2026-11-25','11:00:00',30,150.00,30.00,0.000,0.00,'CONFIRMED'),(10,9,1,1,'2026-11-25','23:40:00',30,150.00,30.00,0.000,0.00,'CONFIRMED'),(11,10,6,5,'2026-12-01','10:00:00',30,150.00,30.00,0.000,0.00,'CANCELLED'),(12,10,6,5,'2026-12-01','10:00:00',30,150.00,30.00,0.000,0.00,'CANCELLED'),(13,10,6,5,'2026-10-02','10:00:00',15,270.00,30.00,0.000,0.00,'CANCELLED'),(14,10,5,4,'2026-11-26','06:00:00',30,1350.00,30.00,0.000,0.00,'CANCELLED'),(15,10,6,7,'2026-10-02','10:00:00',45,2025.00,30.00,0.000,0.00,'CANCELLED'),(16,10,6,8,'2026-10-17','10:00:00',45,0.00,30.00,0.000,0.00,'CANCELLED'),(17,10,6,6,'2026-10-17','10:00:00',30,0.00,30.00,5.544,110.88,'COMPLETED'),(18,10,6,6,'2026-10-17','10:00:00',45,0.00,30.00,0.000,0.00,'CANCELLED'),(19,10,6,8,'2026-10-10','10:00:00',30,0.00,30.00,0.000,0.00,'CANCELLED'),(20,10,6,8,'2026-10-10','10:00:00',30,0.00,30.00,0.252,5.04,'COMPLETED'),(21,10,6,8,'2026-10-04','17:30:00',30,0.00,30.00,0.657,13.14,'COMPLETED'),(22,10,1,1,'2026-10-10','10:00:00',45,0.00,30.00,12.692,253.84,'COMPLETED'),(23,10,6,8,'2026-10-17','10:00:00',45,0.00,30.00,0.036,0.72,'COMPLETED'),(24,10,6,8,'2026-10-10','10:00:00',45,0.00,30.00,0.522,10.44,'COMPLETED'),(25,9,1,2,'2026-10-06','16:30:00',30,0.00,30.00,5.994,119.88,'COMPLETED');
/*!40000 ALTER TABLE `bookings` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `chargers`
--

DROP TABLE IF EXISTS `chargers`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `chargers` (
  `charger_id` int NOT NULL AUTO_INCREMENT,
  `station_id` int NOT NULL,
  `charger_type` varchar(50) NOT NULL,
  `power` decimal(10,2) NOT NULL,
  `available` tinyint(1) DEFAULT '1',
  PRIMARY KEY (`charger_id`),
  KEY `station_id` (`station_id`),
  CONSTRAINT `chargers_ibfk_1` FOREIGN KEY (`station_id`) REFERENCES `stations` (`station_id`)
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `chargers`
--

LOCK TABLES `chargers` WRITE;
/*!40000 ALTER TABLE `chargers` DISABLE KEYS */;
INSERT INTO `chargers` VALUES (1,1,'Fast',60.00,1),(2,1,'fast',120.00,1),(3,2,'Fast',120.00,1),(4,5,'Fast',120.00,1),(5,6,'Fast',60.00,1),(6,6,'Slow',7.50,1),(7,6,'Fast',120.00,1),(8,6,'Slow',3.30,1),(9,7,'Fast',60.00,1);
/*!40000 ALTER TABLE `chargers` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `stations`
--

DROP TABLE IF EXISTS `stations`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `stations` (
  `station_id` int NOT NULL AUTO_INCREMENT,
  `station_name` varchar(100) NOT NULL,
  `location` varchar(200) NOT NULL,
  `total_chargers` int NOT NULL,
  PRIMARY KEY (`station_id`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `stations`
--

LOCK TABLES `stations` WRITE;
/*!40000 ALTER TABLE `stations` DISABLE KEYS */;
INSERT INTO `stations` VALUES (1,'SRM EV Charging Station',' Trichy',10),(2,'Test EV Charging Station','Dindigul',5),(4,'Srm EV station','trichy',4),(5,'SRM EV TRIVHY','TRICHY',4),(6,'SRM EV ','chennai',4),(7,'hari station','trichy',2);
/*!40000 ALTER TABLE `stations` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `users`
--

DROP TABLE IF EXISTS `users`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `users` (
  `user_id` int NOT NULL AUTO_INCREMENT,
  `name` varchar(100) NOT NULL,
  `email` varchar(100) NOT NULL,
  `password` varchar(100) NOT NULL,
  `phone` varchar(15) DEFAULT NULL,
  `role` varchar(20) NOT NULL DEFAULT 'USER',
  PRIMARY KEY (`user_id`),
  UNIQUE KEY `email` (`email`)
) ENGINE=InnoDB AUTO_INCREMENT=14 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `users`
--

LOCK TABLES `users` WRITE;
/*!40000 ALTER TABLE `users` DISABLE KEYS */;
INSERT INTO `users` VALUES (1,'Navin','navin@gmail.com','12345',' 9876543210','USER'),(2,'Navin Test','navintest@gmail.com','','9876543211','USER'),(3,'Navin Test','navintest2@gmail.com',' 12345','9876543211','USER'),(4,'Test User','testuser2026@gmail.com','12345','9876543210','USER'),(5,'navin','navin','navin','960055','USER'),(6,'navin','navin@','12345','9600550306','USER'),(7,'Aravind R','Aravin123@','123','9600550306','USER'),(8,'Navin B','Navinkavi@gmail.com','12345','9600550306','USER'),(9,'Navin','Navinkavi257@gmail.com','12345','9600550306','USER'),(10,'navin','hari123','12345','0000000000','USER'),(11,'System Admin','admin@smartev.com','admin123','9999999999','ADMIN'),(13,'Navin.B','naveenbalu613@gmail.com','12345','9600550306','USER');
/*!40000 ALTER TABLE `users` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `wallet_transactions`
--

DROP TABLE IF EXISTS `wallet_transactions`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `wallet_transactions` (
  `transaction_id` int NOT NULL AUTO_INCREMENT,
  `user_id` int NOT NULL,
  `booking_id` int DEFAULT NULL,
  `transaction_type` varchar(20) NOT NULL,
  `amount` decimal(10,2) NOT NULL,
  `description` varchar(255) NOT NULL,
  `transaction_date` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`transaction_id`),
  KEY `user_id` (`user_id`),
  CONSTRAINT `wallet_transactions_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `wallet_transactions`
--

LOCK TABLES `wallet_transactions` WRITE;
/*!40000 ALTER TABLE `wallet_transactions` DISABLE KEYS */;
INSERT INTO `wallet_transactions` VALUES (1,10,NULL,'CREDIT',440.00,'Wallet top-up of ₹440.00','2026-10-04 12:44:24'),(2,10,NULL,'CREDIT',1.00,'Wallet top-up of ₹1.00','2026-10-04 13:05:00'),(3,10,NULL,'CREDIT',1.00,'Wallet top-up of ₹1.00','2026-10-05 04:16:21'),(4,10,NULL,'CREDIT',10000.00,'Wallet top-up of ₹10000.00','2026-10-05 04:16:44'),(5,10,NULL,'CREDIT',0.06,'Wallet top-up of ₹0.06','2026-10-05 07:34:28'),(6,9,NULL,'CREDIT',1000.00,'Wallet top-up of ₹1000.00','2026-10-06 10:52:42');
/*!40000 ALTER TABLE `wallet_transactions` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `wallets`
--

DROP TABLE IF EXISTS `wallets`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `wallets` (
  `wallet_id` int NOT NULL AUTO_INCREMENT,
  `user_id` int NOT NULL,
  `balance` decimal(10,2) NOT NULL DEFAULT '0.00',
  PRIMARY KEY (`wallet_id`),
  UNIQUE KEY `user_id` (`user_id`),
  CONSTRAINT `wallets_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=350 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `wallets`
--

LOCK TABLES `wallets` WRITE;
/*!40000 ALTER TABLE `wallets` DISABLE KEYS */;
INSERT INTO `wallets` VALUES (1,10,10808.00),(320,9,850.12);
/*!40000 ALTER TABLE `wallets` ENABLE KEYS */;
UNLOCK TABLES;
SET @@SESSION.SQL_LOG_BIN = @MYSQLDUMP_TEMP_LOG_BIN;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-11-11 19:58:01
