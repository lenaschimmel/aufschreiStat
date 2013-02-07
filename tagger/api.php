<?php
session_start();
ini_set("display_errors", 1);
error_reporting(E_ALL);
require_once("config.php");

mysql_connect( $config["mysqlhost"].":".$config["mysqlport"], $config["mysqluser"], $config["mysqlpassword"]);
mysql_set_charset($config["mysqlcharset"]);
mysql_select_db($config["mysqldatabase"]);

setupSession();

if(isset($_POST['query'])) {
		$query = $_POST['query'];
		parseQuery($query);
} else if(isset($_GET['query'])) {
		$query = $_GET['query'];
		parseQuery($query);
} else {
		returnError("NO QUERY");
}

function setupSession() {
	if(!isset($_SESSION['session_id']))
	{
		$session_id = rand();
		$_SESSION['session_id'] = $session_id;
		$sql = mysql_query("INSERT INTO statTaggers SET session_id='$session_id';");
		mysql_query($sql);
		$tagger_id = mysql_insert_id();
		$_SESSION['tagger_id'] = $tagger_id;
	}
	else
	{
		$session_id = $_SESSION['session_id'];
		$tagger_id = getSingleValueFromDb("SELECT id FROM statTaggers WHERE session_id='$session_id';");
		$_SESSION['tagger_id'] = $tagger_id;
	}
}

function parseQuery($query) {
	if($query == "random") {
		getRandomTweet();
	} else if($query == "url" && isset($_POST['url'])) {
		getHtmlForTweet($_POST['url']);
	} else if($query == "tags") {
		getTags();
	} else if($query == "createtag" && isset($_POST['description']) && isset($_POST['parent_id'])){
		createTag();
	} else if($query == "updatetweet" && isset($_POST['tweet'])) {
		updatetweet();
	} else if($query == "updatetag" && isset($_POST['id']) && isset($_POST['tweet'])){
		updatetag();
	} else if($query == "updatelang" && isset($_POST['lang']) && isset($_POST['tweet'])){
		updatelang();
	} else if($query == "statistic") {
		statistic();
	} else {
		returnError("UNKNOWN QUERY");
	}
}

function statistic() {
	$orig = getSingleValueFromDb("SELECT COUNT(*) FROM `statTweets`");
	$rt = getSingleValueFromDb("SELECT COUNT(*) FROM `statRetweets`");
	$sum = $orig + $rt;
	$aufschrei = getSingleValueFromDb("SELECT COUNT(*) FROM `statTweets` WHERE text LIKE '%#aufschrei%'");
	$tagger_id = $_SESSION['tagger_id'];
	returnSingleValue("<ul><li>Original-Tweets: $orig</li><li>Retweets: $rt</li><li>Gesamt-Tweets: $sum</li><li>#aufschrei: $aufschrei</li><li>Deine Nutzer-ID: $tagger_id</li></ul>");
}

function getSingleValueFromDb($query) {
	$sql = mysql_query($query);
	$row = mysql_fetch_row($sql);
	return $row[0];
}

function getRandomTweet() {
	$sql = mysql_query("SELECT id AS id, user_id AS user FROM statTweets ORDER BY RAND() LIMIT 1");
	$return = array();
	while($result = mysql_fetch_assoc($sql)) {
    	    $return[] = $result;
	}
	return returnArray(json_encode($return));
}

function getTags() {
	$sql = mysql_query("SELECT id AS id, label AS label, description as description, parent_id FROM statLabels WHERE reviewed = 1 ORDER BY parent_id, label ASC");
	$return = array();
	while($result = mysql_fetch_assoc($sql)) {
	    //$encodedResult = array_map(utf8_encode, $result);
	    $return[] = $result;
	}
	return returnArray(json_encode($return));
}

function createTag() {
	$label =  mysql_real_escape_string($_POST['label']);
	$description =  mysql_real_escape_string($_POST['description']);
	$parent_id =  mysql_real_escape_string($_POST['parent_id']);
	$sql = mysql_query("INSERT INTO statLabels SET label='$label', description='$description', parent_id='$parent_id';");
	mysql_query($sql);
	$id = mysql_insert_id();
	$return['id'] = $id;
	$return['label'] = $label;
	$return['description'] = $description;
	$return['parent_id'] = $parent_id;
	return returnArray(json_encode($return));
}

function getHtmlForTweet($url) {
	print file_get_contents($url);
}

function updatetweet() {
	$tweet =  mysql_real_escape_string($_POST['tweet']);
	$update_tweet = mysql_query("UPDATE statTweets SET tagged=tagged+1 WHERE id='$tweet'");
}

function updatetag() {
	$id = mysql_real_escape_string($_POST['id']);
	$tweet =  mysql_real_escape_string($_POST['tweet']);
	$update_label = mysql_query("INSERT INTO statTweets_to_labels SET label_id='$id', tweet_id='$tweet', count=1 ON DUPLICATE KEY UPDATE count=count+1");
}

function updatelang() {
	$lang = mysql_real_escape_string($_POST['lang']);
	$tweet =  mysql_real_escape_string($_POST['tweet']);
	$update_lang = mysql_query("INSERT INTO statTweets_to_langs SET lang='$lang', tweet_id='$tweet', count=1 ON DUPLICATE KEY UPDATE count=count+1");
}

function returnArray($result) {
	print "{\"status\": \"ok\", \"result\": ".$result."}";
}

function returnValues($result) {
	print "{\"status\": \"ok\", \"result\": {".$result."}}";
}

function returnSingleValue($result) {
	print "{\"status\": \"ok\", \"result\": \"".$result."\"}";
}

function returnError($error) {
	print "{\"status\": \"error\", \"type\": \"".$error."\"}";
}