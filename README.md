# AufschreiStat

## Software zur statistischen Auswertung der #aufschrei-Tweets

Hier entsteht ein Programm zum Sammeln von #aufschrei-Tweets, zum manuellen Versehen diese Tweets mit speziellen Tags, zur automatischen statistischen Auswertung und zum belästigungsfreien Lesen der Tweets.

Nähere Infos zu diesem Software-Projekt sind [in diesem Blogpost](http://lenaschimmel.de/wordpress/index.php/2013/aufschreistat-statistische-analyse-des-aufschreis/) auf [lenaschimmel.de](http://lenaschimmel.de) zu finden. Diskussion und aktuelle Statusmeldungen werden über [die Mailingliste](https://groups.google.com/forum/#!forum/aufschreistat) kommuniziert.

## Aktueller Stand

Diese Software dürfte im aktuellen Status für niemanden interessant sein, sie ist nur deshalb schon online, weil es so einfacher ist als die fertige Software später zu veröffentlichen.

## Kontakt

[@LenaSchimmel](https://twitter.com/LenaSchimmel) ist auf Twitter unterwegs und twittert dort Neuigkeiten zu diesem Projekt unter dem Hashtag [#aufschreistat](https://twitter.com/search?q=%23aufschreistat&src=hash). Per Mail ist sie unter [DieLenaMaria@googlemail.com](mailto:DieLenaMaria@googlemail.com) erreichbar.

## Einrichtung

Im Wurzelverzeichnis sind zwei Dateien anzulegen, hier mit beispielhaften Werten:

### db.properties

    connectString = jdbc:mysql://127.0.0.1/aufschrei
    username = ???
    password = ???

### twitter4j.properties

    user = ???
    password = ???

Für den PastImporter braucht es außerdem OAuth Authorizan Tokens - die zu holen ist noch nicht implementiert. Bei Lena klappt das auch ohne, aber nur, weil die Twitter-App auf ihren Account registriert ist. Der Past-Importer ist damit praktisch für andere (noch) kaum nutzbar.

## Datenbankschema

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
        KEY `coordinates_lat` (`coordinates_lat`,`coordinates_lon`,`created_at`,`in_reply_to_status_id`,`user_id`)
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
      ) ENGINE=MyISAM DEFAULT CHARSET=latin1;
