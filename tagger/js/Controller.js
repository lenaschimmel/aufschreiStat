Aufschrei.Controller = (function(app) {

	var last_tweets,
	all_tags,
	used_tags,
	current_tweet = "-1",
	current_lang = "-1",
	working = false,
	api_url = 'api.php',

	init = function() {
		initUI();
		initMemebers();
		getRandomTweetFromDatabase();
		getTagsFromDatabase();
	},

	initUI = function() {
		$('.nexttweet').click(function(e) {
			if(working) return;
			if(used_tags.length > 0) setTweetAsTaggedInDatabase();
			if(used_tags.length > 0) saveTagsToDatabase();
			if(current_lang != "-1") saveLangToDatabase();
			getRandomTweetFromDatabase();
			displayButton();
		});

		$('.lang').click(function(e) {
			setLanguage($(e.target).attr('lang'));
		});
	},

	initMemebers = function() {
		last_tweets = new Array();
		all_tags = new Array();
		used_tags = new Array();
	},

	getRandomTweetFromDatabase = function() {
		clear();
		working = true;
		$.ajax({
  				type: "POST",
  				url: api_url,
  				data: {query: "random"},
  				success: getTweetFromTwitter
		});
	},

	getTagsFromDatabase = function() {
		$.ajax({
  				type: "POST",
  				url: api_url,
  				data: {query: "tags"},
  				success: showTags
		});
	},

	saveTagsToDatabase = function() {
		var tweet = current_tweet;
		$.each(used_tags, function(index, value) {
			sendTagToDatabase(value,tweet);
		});
	},

	setTweetAsTaggedInDatabase = function() {
		var tweet = current_tweet;
		$.ajax({
  				type: "POST",
  				url: api_url,
  				data: {query: "updatetweet", tweet: tweet},
  				success: null
		});
	},

	sendTagToDatabase = function(tag_id,current_tweet) {
		$.ajax({
  				type: "POST",
  				url: api_url,
  				data: {query: "updatetag", id: tag_id, tweet: current_tweet},
  				success: null
		});
	},

	saveLangToDatabase = function() {
		var tweet = current_tweet;
		var lang = current_lang;
		$.ajax({
  				type: "POST",
  				url: api_url,
  				data: {query: "updatelang", lang: lang, tweet: tweet},
  				success: null
		});
	},

	getTweetFromTwitter = function(tweet) {
		last_tweets.push($.parseJSON(tweet).result[0].id);
		current_tweet = $.parseJSON(tweet).result[0].id;
		$.ajax({
  				type: "POST",
  				url: api_url,
  				data: {query: "url", url: 'https://twitter.com/'+$.parseJSON(tweet).result[0].user+'/status/'+$.parseJSON(tweet).result[0].id},
  				success: showTweet
		});
	},

	showTags = function(tags) {
		var obj = $.parseJSON(tags);

		$.each(obj.result, function(index, value) {
			all_tags.push(value);
  			$('#tag_library').append('<li class="library_entry" id='+value.id+'>'+value.label+'</li>');
		});

		$(".library_entry").click(function(e) {
			var $target = $(e.target);
			

			addTagToTweet(e.target.id);
		});

	},

	showTweet = function(html) {
		var tweet_text = $(".js-tweet-text.tweet-text", html).html();
		var user_screenname = $(".username.js-action-profile-name", html).html();
	
		if(tweet_text == null || user_screenname == null) {
			console.log("the tweet is a lie!");
			getRandomTweetFromDatabase();
			return;
		}

		displayTweet(tweet_text, user_screenname);
		
		working = false;
	},

	setLanguage = function(lang) {
		if(working) return;
		$('.lang').removeClass('used');
		$('.lang.'+lang).addClass('used');
		current_lang = lang;
	},

	clear = function() {
		$('#user_name').html('');
		$('#text').html('');
		$('#spinner').fadeIn();
		$('.lang').removeClass('used');
		$('#usedtags').html('');

		$('.library_entry').removeClass('used');
		used_tags = new Array();

		current_tweet = "-1";
		current_lang = "-1";
	},

	displayButton = function() {
		if(used_tags.length > 0) {
			$('.nexttweet').attr('value', 'save & get another tweet');
		} else {
			$('.nexttweet').attr('value', 'get another tweet');
		}
	},

	displayTweet = function(text, username) {
		$('#spinner').hide();
		$('#user_name').html(username);
		$('#text').html(text);

	},

	addTagToTweet = function(tag_id) {
		$('#used_tags').fadeOut().html('').fadeIn();
		if(working) return;
		$.each(all_tags, function(index, value) {
			if(value.id == tag_id) {
				if($.inArray(value.id, used_tags) != -1) {
					used_tags = jQuery.grep(used_tags, function(value) {
  						return value != tag_id;
					});
					$('.item[id="'+tag_id+'"]').fadeOut().remove();
					$('.library_entry[id="'+tag_id+'"]').removeClass('used');
				} else {
					used_tags.push(value.id);	
					$('#usedtags').append('<span class="item" id="'+tag_id+'"">'+value.label+'</span>');
					$('.library_entry[id="'+tag_id+'"]').addClass('used');
				}
			}
		});
		displayButton();
	}

	return {
		init : init
	};

}(Aufschrei));
