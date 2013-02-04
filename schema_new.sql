-- phpMyAdmin SQL Dump
-- version 3.5.1
-- http://www.phpmyadmin.net
--
-- Host: localhost
-- Generation Time: Feb 04, 2013 at 02:12 PM
-- Server version: 5.5.25
-- PHP Version: 5.4.4

SET SQL_MODE="NO_AUTO_VALUE_ON_ZERO";
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;

--
-- Database: `aufschreiStat`
--

-- --------------------------------------------------------

--
-- Table structure for table `statLabels`
--

CREATE TABLE IF NOT EXISTS `statLabels` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `label` varchar(60) NOT NULL,
  `description` varchar(200) NOT NULL,
  `parent_id` bigint(20) NOT NULL,
  UNIQUE KEY `id` (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `statTweets`
--

CREATE TABLE IF NOT EXISTS `statTweets` (
  `id` bigint(20) NOT NULL COMMENT 'official Twitter id',
  `internal_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'countet upwards from 0',
  `user_id` bigint(20) NOT NULL COMMENT 'official twitter user id',
  `text` varchar(240) NOT NULL,
  `timestamp` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `reply_status_id` bigint(20) DEFAULT NULL COMMENT 'official twitter id of the status that this one replies to, or null',
  `tagged` int(11) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `internal_id` (`internal_id`),
  KEY `user_id` (`user_id`,`timestamp`,`reply_status_id`)
) ENGINE=MyISAM  DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `statTweets_to_labels`
--

CREATE TABLE IF NOT EXISTS `statTweets_to_labels` (
  `label_id` bigint(20) NOT NULL,
  `tweet_id` bigint(20) NOT NULL,
  `count` int(11) NOT NULL DEFAULT '1',
  UNIQUE KEY `_index` (`label_id`,`tweet_id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `statTweets_to_langs`
--

CREATE TABLE IF NOT EXISTS `statTweets_to_langs` (
  `tweet_id` bigint(20) NOT NULL,
  `lang` varchar(5) NOT NULL,
  `count` int(11) NOT NULL DEFAULT '1',
  UNIQUE KEY `_tweet_id` (`tweet_id`,`lang`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `statUsers`
--

CREATE TABLE IF NOT EXISTS `statUsers` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'official Twitter id',
  `screen_name` varchar(60) NOT NULL,
  `name` varchar(60) NOT NULL,
  `url` varchar(240) DEFAULT NULL,
  `profile_image_url` varchar(240) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=MyISAM  DEFAULT CHARSET=latin1;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
