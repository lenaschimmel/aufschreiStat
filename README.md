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

Siehe Datei schema.sql
