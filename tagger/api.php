<?php

include_once("db_connect.php");

if(isset($_POST['query'])) {
		$query = $_POST['query'];
		parseQuery($query);
} else {
		returnError("NO QUERY");
}

function parseQuery($query) {
	if($query == "random") {
		getRandomTweet();
	} else if($query == "url" && isset($_POST['url'])) {
		getHtmlForTweet($_POST['url']);
	} else if($query == "tags") {
		getTags();
	} else if($query == "updatetweet" && isset($_POST['tweet'])) {
		updatetweet();
	} else if($query == "updatetag" && isset($_POST['id']) && isset($_POST['tweet'])){
		updatetag();
	} else if($query == "updatelang" && isset($_POST['lang']) && isset($_POST['tweet'])){
		updatelang();
	} else {
		returnError("UNKNOWN QUERY");
	}
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
	$sql = mysql_query("SELECT id AS id, label AS label FROM statLabels");
	$return = array();
	while($result = mysql_fetch_assoc($sql)) {
    	$return[] = $result;
	}
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
	$update_label = mysql_query("INSERT INTO statTweetsToLabels SET label_id='$id', tweet_id='$tweet', count=1 ON DUPLICATE KEY UPDATE count=count+1");
}

function updatelang() {
	$lang = mysql_real_escape_string($_POST['lang']);
	$tweet =  mysql_real_escape_string($_POST['tweet']);
	$update_lang = mysql_query("INSERT INTO statTweetsToLangs SET lang='$lang', tweet_id='$tweet', count=1 ON DUPLICATE KEY UPDATE count=count+1");
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