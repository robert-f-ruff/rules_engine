-- MySQL dump 10.13  Distrib 8.3.0, for macos14 (x86_64)
--
-- Host: localhost    Database: rules
-- ------------------------------------------------------
-- Server version	8.3.0

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
-- Table structure for table `rules_action`
--

DROP TABLE IF EXISTS `rules_action`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `rules_action` (
  `name` varchar(30) NOT NULL,
  `function` varchar(30) NOT NULL,
  PRIMARY KEY (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `rules_action`
--

LOCK TABLES `rules_action` WRITE;
/*!40000 ALTER TABLE `rules_action` DISABLE KEYS */;
INSERT INTO `rules_action` VALUES ('Send Email','SendEmail');
/*!40000 ALTER TABLE `rules_action` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `rules_actionparameters`
--

DROP TABLE IF EXISTS `rules_actionparameters`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `rules_actionparameters` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `parameter_number` smallint unsigned NOT NULL,
  `action_id` varchar(30) NOT NULL,
  `parameter_id` varchar(30) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `rules_actionparameters_action_id_94f3dab3_fk_rules_action_name` (`action_id`),
  KEY `rules_actionparamete_parameter_id_ab7b7222_fk_rules_par` (`parameter_id`),
  CONSTRAINT `rules_actionparamete_parameter_id_ab7b7222_fk_rules_par` FOREIGN KEY (`parameter_id`) REFERENCES `rules_parameter` (`name`),
  CONSTRAINT `rules_actionparameters_action_id_94f3dab3_fk_rules_action_name` FOREIGN KEY (`action_id`) REFERENCES `rules_action` (`name`),
  CONSTRAINT `rules_actionparameters_chk_1` CHECK ((`parameter_number` >= 0))
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb3;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `rules_actionparameters`
--

LOCK TABLES `rules_actionparameters` WRITE;
/*!40000 ALTER TABLE `rules_actionparameters` DISABLE KEYS */;
INSERT INTO `rules_actionparameters` VALUES (1,1,'Send Email','Send Email to'),(2,2,'Send Email','Copy Email to');
/*!40000 ALTER TABLE `rules_actionparameters` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `rules_criterion`
--

DROP TABLE IF EXISTS `rules_criterion`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `rules_criterion` (
  `name` varchar(30) NOT NULL,
  `logic` longtext NOT NULL,
  PRIMARY KEY (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `rules_criterion`
--

LOCK TABLES `rules_criterion` WRITE;
/*!40000 ALTER TABLE `rules_criterion` DISABLE KEYS */;
INSERT INTO `rules_criterion` VALUES ('Age Greater Than 40','Patient.AgeGreaterThan=40'),('Blood Glucose Less Than 100','Observation.BloodGlucoseLessThan=100'),('Body Weight Greater Than 200','Observation.BodyWeightGreaterThan=200'),('Patient Is Female','Patient.IsFemale');
/*!40000 ALTER TABLE `rules_criterion` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `rules_parameter`
--

DROP TABLE IF EXISTS `rules_parameter`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `rules_parameter` (
  `name` varchar(30) NOT NULL,
  `data_type` varchar(2) NOT NULL,
  `required` tinyint(1) NOT NULL,
  `help_text` longtext NOT NULL,
  PRIMARY KEY (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `rules_parameter`
--

LOCK TABLES `rules_parameter` WRITE;
/*!40000 ALTER TABLE `rules_parameter` DISABLE KEYS */;
INSERT INTO `rules_parameter` VALUES ('Copy Email to','EM',0,'Enter the email address that a copy of the message is addressed to.'),('Send Email to','EM',1,'Enter the email address that the message is addressed to.');
/*!40000 ALTER TABLE `rules_parameter` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `rules_rule`
--

DROP TABLE IF EXISTS `rules_rule`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `rules_rule` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(30) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb3;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `rules_rule`
--

LOCK TABLES `rules_rule` WRITE;
/*!40000 ALTER TABLE `rules_rule` DISABLE KEYS */;
INSERT INTO `rules_rule` VALUES (1,'Sample Rule #1'),(2,'Sample Rule #2'),(3,'Sample Rule #3');
/*!40000 ALTER TABLE `rules_rule` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `rules_rule_criteria`
--

DROP TABLE IF EXISTS `rules_rule_criteria`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `rules_rule_criteria` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `rule_id` bigint NOT NULL,
  `criterion_id` varchar(30) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `rules_rule_criteria_rule_id_criterion_id_dd37583c_uniq` (`rule_id`,`criterion_id`),
  KEY `rules_rule_criteria_criterion_id_499999e5_fk_rules_cri` (`criterion_id`),
  CONSTRAINT `rules_rule_criteria_criterion_id_499999e5_fk_rules_cri` FOREIGN KEY (`criterion_id`) REFERENCES `rules_criterion` (`name`),
  CONSTRAINT `rules_rule_criteria_rule_id_6f9b1ed1_fk_rules_rule_id` FOREIGN KEY (`rule_id`) REFERENCES `rules_rule` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb3;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `rules_rule_criteria`
--

LOCK TABLES `rules_rule_criteria` WRITE;
/*!40000 ALTER TABLE `rules_rule_criteria` DISABLE KEYS */;
INSERT INTO `rules_rule_criteria` VALUES (2,1,'Age Greater Than 40'),(1,1,'Patient Is Female'),(3,2,'Age Greater Than 40'),(4,2,'Body Weight Greater Than 200'),(5,3,'Blood Glucose Less Than 100');
/*!40000 ALTER TABLE `rules_rule_criteria` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `rules_ruleactionparameters`
--

DROP TABLE IF EXISTS `rules_ruleactionparameters`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `rules_ruleactionparameters` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `parameter_value` longtext NOT NULL,
  `parameter_id` varchar(30) NOT NULL,
  `rule_action_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  KEY `rules_ruleactionpara_rule_action_id_672bd0f1_fk_rules_rul` (`rule_action_id`),
  KEY `rules_ruleactionpara_parameter_id_7ffe8083_fk_rules_par` (`parameter_id`),
  CONSTRAINT `rules_ruleactionpara_parameter_id_7ffe8083_fk_rules_par` FOREIGN KEY (`parameter_id`) REFERENCES `rules_parameter` (`name`),
  CONSTRAINT `rules_ruleactionpara_rule_action_id_672bd0f1_fk_rules_rul` FOREIGN KEY (`rule_action_id`) REFERENCES `rules_ruleactions` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb3;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `rules_ruleactionparameters`
--

LOCK TABLES `rules_ruleactionparameters` WRITE;
/*!40000 ALTER TABLE `rules_ruleactionparameters` DISABLE KEYS */;
INSERT INTO `rules_ruleactionparameters` VALUES (1,'george.jetson@acme.com','Send Email to',1),(2,'george.jetson@acme.com','Send Email to',2),(3,'rosie.robot@acme.com','Copy Email to',2),(4,'george.jetson@acme.com','Send Email to',3),(5,'rosie.robot@acme.com','Send Email to',4);
/*!40000 ALTER TABLE `rules_ruleactionparameters` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `rules_ruleactions`
--

DROP TABLE IF EXISTS `rules_ruleactions`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `rules_ruleactions` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `action_number` smallint unsigned NOT NULL,
  `action_id` varchar(30) NOT NULL,
  `rule_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  KEY `rules_ruleactions_action_id_3895acb1_fk_rules_action_name` (`action_id`),
  KEY `rules_ruleactions_rule_id_1d410cee_fk_rules_rule_id` (`rule_id`),
  CONSTRAINT `rules_ruleactions_action_id_3895acb1_fk_rules_action_name` FOREIGN KEY (`action_id`) REFERENCES `rules_action` (`name`),
  CONSTRAINT `rules_ruleactions_rule_id_1d410cee_fk_rules_rule_id` FOREIGN KEY (`rule_id`) REFERENCES `rules_rule` (`id`),
  CONSTRAINT `rules_ruleactions_chk_1` CHECK ((`action_number` >= 0))
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb3;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `rules_ruleactions`
--

LOCK TABLES `rules_ruleactions` WRITE;
/*!40000 ALTER TABLE `rules_ruleactions` DISABLE KEYS */;
INSERT INTO `rules_ruleactions` VALUES (1,1,'Send Email',1),(2,1,'Send Email',2),(3,1,'Send Email',3),(4,2,'Send Email',3);
/*!40000 ALTER TABLE `rules_ruleactions` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2024-04-02 12:25:53
