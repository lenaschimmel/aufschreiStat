Aufschrei.Controller = (function(app) {

	var last_tweets,
	all_tags,
	used_tags,
	current = "-1",
	working = false;

	init = function() {
		initUI();
		initMemebers();
		getRandomTweetFromDatabase();
		getTagsFromDatabase();
	},

	initUI = function() {
		$('.nexttweet').click(function(e) {
			if(working) return;
			if(used_tags.length > 0) saveTagsToDatabase();
			getRandomTweetFromDatabase();
			$('#usedtags').html('');
			$('.library_entry').removeClass('used');
			used_tags = new Array();
			displayButton();
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
  				url: '/tagger/api.php',
  				data: {query: "random"},
  				success: getTweetFromTwitter
		});
	},

	getTagsFromDatabase = function() {
		$.ajax({
  				type: "POST",
  				url: '/tagger/api.php',
  				data: {query: "tags"},
  				success: showTags
		});
	},

	saveTagsToDatabase = function() {
		$.each(used_tags, function(index, value) {
			sendTagToDatabase(value);
		});
	},

	sendTagToDatabase = function(tag_id) {
		console.log("sending tag to database: " + tag_id + " for tweet: " + current);
		$.ajax({
  				type: "POST",
  				url: '/tagger/api.php',
  				data: {query: "updatetag", id: tag_id, tweet: current},
  				success: null
		});
	}

	getTweetFromTwitter = function(tweet) {
		last_tweets.push($.parseJSON(tweet).result[0].id);
		current = $.parseJSON(tweet).result[0].id;
		$.ajax({
  				type: "POST",
  				url: '/tagger/api.php',
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

	
		if(tweet_text == '' || user_screenname == '') {
			getRandomTweetFromDatabase();
			return;
		}

		displayTweet(tweet_text, user_screenname);
		
		working = false;
	},

	clear = function() {
		$('#user_name').html('');
		$('#text').html('');
		$('#spinner').fadeIn();
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
