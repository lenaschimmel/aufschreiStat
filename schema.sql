-- phpMyAdmin SQL Dump
-- version 2.11.8.1
-- http://www.phpmyadmin.net
--
-- Host: localhost
-- Erstellungszeit: 03. Februar 2013 um 01:58
-- Server Version: 5.1.63
-- PHP-Version: 5.3.6-13ubuntu3.7

SET SQL_MODE="NO_AUTO_VALUE_ON_ZERO";

--
-- Datenbank: `aufschreistat`
--

-- --------------------------------------------------------

--
-- Tabellenstruktur für Tabelle `statLabels`
--

CREATE TABLE IF NOT EXISTS `statLabels` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `label` varchar(200) NOT NULL DEFAULT '[unknown]',
  `description` text NOT NULL,
  `parent_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `parent_id` (`parent_id`)
) ENGINE=MyISAM  DEFAULT CHARSET=latin1 AUTO_INCREMENT=3 ;

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
  `internal_id` bigint(20) NOT NULL AUTO_INCREMENT,
  `coordinates_lat` float NOT NULL,
  `coordinates_lon` float NOT NULL,
  `text` varchar(240) NOT NULL,
  `retweet_count` int(11) NOT NULL,
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `in_reply_to_status_id` bigint(20) NOT NULL,
  `user_id` bigint(20) NOT NULL,
  `tagged` int(11) NOT NULL DEFAULT '0' COMMENT 'Wie oft wurde dies getaggt?',
  PRIMARY KEY (`internal_id`),
  KEY `coordinates_lat` (`coordinates_lat`,`coordinates_lon`,`created_at`,`in_reply_to_status_id`,`user_id`),
  KEY `id` (`id`),
  FULLTEXT KEY `text` (`text`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Tabellenstruktur für Tabelle `statTweetsToLabels`
--

CREATE TABLE IF NOT EXISTS `statTweetsToLabels` (
  `label_id` bigint(20) NOT NULL,
  `tweet_id` bigint(20) NOT NULL,
  `count` int(11) NOT NULL DEFAULT '0',
  PRIMARY KEY (`label_id`,`tweet_id`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Tabellenstruktur für Tabelle `statTweetsToLangs`
--

CREATE TABLE IF NOT EXISTS `statTweetsToLangs` (
  `lang` varchar(10) NOT NULL,
  `tweet_id` bigint(20) NOT NULL,
  `count` int(11) NOT NULL DEFAULT '0',
  PRIMARY KEY (`lang`,`tweet_id`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

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
