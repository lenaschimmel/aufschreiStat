-- phpMyAdmin SQL Dump
-- version 3.5.6-rc1
-- http://www.phpmyadmin.net
--
-- Host: localhost
-- Erstellungszeit: 02. Feb 2013 um 00:00
-- Server Version: 5.5.29-1~dotdeb.0-log
-- PHP-Version: 5.4.11-1~dotdeb.0

SET SQL_MODE="NO_AUTO_VALUE_ON_ZERO";
SET time_zone = "+00:00";

--
-- Datenbank: `rbtest`
--

-- --------------------------------------------------------

--
-- Tabellenstruktur für Tabelle `statRetweets`
--

CREATE TABLE IF NOT EXISTS `statRetweets` (
  `status_id` bigint(20) NOT NULL,
  `user_id` bigint(20) NOT NULL,
  `retweeted_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`status_id`,`user_id`),
  KEY `retweeted_at` (`retweeted_at`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Tabellenstruktur für Tabelle `statTweets`
--

CREATE TABLE IF NOT EXISTS `statTweets` (
  `id` bigint(20) NOT NULL,
  `coordinates_lat` float NOT NULL,
  `coordinates_lon` float NOT NULL,
  `text` varchar(240) NOT NULL,
  `retweet_count` int(11) NOT NULL,
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `in_reply_to_status_id` bigint(20) NOT NULL,
  `user_id` bigint(20) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `coordinates_lat` (`coordinates_lat`,`coordinates_lon`,`created_at`,`in_reply_to_status_id`,`user_id`),
  FULLTEXT KEY `text` (`text`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Tabellenstruktur für Tabelle `statUsers`
--

CREATE TABLE IF NOT EXISTS `statUsers` (
  `id` bigint(20) NOT NULL,
  `screen_name` varchar(40) NOT NULL,
  `name` varchar(60) NOT NULL,
  `description` varchar(200) NOT NULL,
  `lang` varchar(10) NOT NULL,
  `followers_count` int(11) NOT NULL,
  `statuses_count` int(11) NOT NULL,
  `url` varchar(250) NOT NULL,
  `profile_image_url` varchar(250) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `screen_name` (`screen_name`,`lang`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;
