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
	$sql = mysql_query("SELECT _id AS id, _user AS user FROM tweets ORDER BY RAND() LIMIT 1");
	$return = array();
	while($result = mysql_fetch_assoc($sql)) {
    	$return[] = $result;
	}
	return returnArray(json_encode($return));
}

function getTags() {
	$sql = mysql_query("SELECT _id AS id, _label AS label FROM labels");
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
	$update_tweet = mysql_query("UPDATE tweets SET _tagged=_tagged+1 WHERE _id='$tweet'");
}

function updatetag() {
	$id = mysql_real_escape_string($_POST['id']);
	$tweet =  mysql_real_escape_string($_POST['tweet']);
	$update_label = mysql_query("INSERT INTO tweets_to_labels SET _label_id='$id', _tweet_id='$tweet', _count=1 ON DUPLICATE KEY UPDATE _count=_count+1");
}

function updatelang() {
	$lang = mysql_real_escape_string($_POST['lang']);
	$tweet =  mysql_real_escape_string($_POST['tweet']);
	$update_lang = mysql_query("INSERT INTO tweets_to_langs SET _lang='$lang', _tweet_id='$tweet', _count=1 ON DUPLICATE KEY UPDATE _count=_count+1");
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