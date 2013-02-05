Aufschrei.Controller = (function(app) {

	var last_tweets,
	tags,
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

		$('#newtag').hide();

		$('#newtag .field.text.label').keyup(function() {
			if($('#newtag .field.text.label').val().length > 2) {
				$('#newtag .field.button.savetodb.disabled').removeClass('disabled');
				$('#newtag .field.button.savetodb').addClass('enabled');
			} else {
				$('#newtag .field.button.savetodb.enabled').removeClass('enabled');
				$('#newtag .field.button.savetodb').addClass('disabled');
			}
		});

		$('#newtag .field.button.savetodb').click(function(e) {
			if($(e.target).hasClass('enabled')) {
				saveNewTagToDatabase($('#newtag .field.text.label').val(), $('#newtag .field.text.description').val(), $('#newtag').attr('parent_id'));
			}
		});

		$('#newtag #close').click(function(e) {
			if(working) return;
			$('#newtag').fadeOut();
		});
	},

	initMemebers = function() {
		last_tweets = new Array();
		all_tags = new Array();
		used_tags = new Array();
		tags = new Array();
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

	showTags = function(tags_json) {
		var obj = $.parseJSON(tags_json);
		tags = obj;
		buildTagTree();

		/**
		$(".library_entry").click(function(e) {
			var $target = $(e.target);
			addOrRemoveTagToTweet(e.target.id);
			return false;
		});
		*/

	},

	buildTagTree = function() {
		$('#library > #root').html('');
		$.each(tags.result, function(index, value) {
			if(!value.parent_id || value.parent_id == 0) {
				$('#library > #root').append('<li id='+value.id+' parent='+value.parent_id+' class="closed" description='+value.description+'><span class="label" id='+value.id+' title="'+value.description+'">'+value.label+'</span><span class="addtag" parent="'+value.parent_id+'" id="'+value.id+'">+</span></li>');
				$('#library li[id='+value.id+'] > .label').addClass('child');
				$('#library li[id='+value.id+'] > .label').tipTip();
			} else {
				var node = $('#library li[id='+value.parent_id+']');
				$(node).append('<li id='+value.id+' parent='+value.parent_id+' class="closed" description='+value.description+'><span class="label" id='+value.id+' title="'+value.description+'">'+value.label+'</span><span class="addtag" parent="'+value.parent_id+'" id="'+value.id+'">+</span></li>');
				$('#library li[id='+value.parent_id+'] > li[id='+value.id+'] > .label').addClass('child');
				$('#library li[id='+value.parent_id+'] > li[id='+value.id+'] > .label').tipTip();
				$('#library li[id='+value.parent_id+'] > li[id='+value.id+']').addClass('hidden');
				$('#library li[id='+value.parent_id+'] > .label').removeClass('child');	
				$('#library li[id='+value.parent_id+'] > .label').addClass('parent');	
				$('#library li[id='+value.parent_id+'] > .label').attr('title', 'Click to open/close tag category');
				$('#library li[id='+value.parent_id+'] > .label').tipTip();
			}

			$('#library li[id='+value.id+'] > .addtag').click(function(e) {
				shownewTagDialog($(e.target).attr("id"));
			});

			$('#library li[id='+value.id+'] > .label').click(function(e) {
				if($(e.target).hasClass('parent')) {
					var node = $('#library li[id='+$(e.target).attr("id")+']');
					node.toggleClass('closed');
					node.children('li').toggleClass('hidden');
				} else {
					if(working) return;
					$(e.target).toggleClass('used');
					addOrRemoveTagToTweet($(e.target).attr("id"));
				}

			});
		});
	}

	createTag = function(tag) {
		var parentElement;
		all_tags[tag.id] = tag;
		if(!tag.parent_id)
	  		parentElement = $('#tag_library');
		else
			parentElement = $('#sublist'+tag.parent_id);

		var buttonId = "addSubtagTo" + tag.id;

		parentElement.append('<li class="library_entry unused" id="' + tag.id + '" title="' + tag.description + '">' + tag.label +
					'<span class="addButton" id="'+ buttonId  +'" title="Click to add subtag">new</span><ul id="sublist' + tag.id + '"></ul></li>');

		$('#'+buttonId).click(function(target) {
			var label = prompt("Name für den neuen Tag");
			var description = prompt("Beschreibung für den neuen Tag");
			createTagInDatabase(label, description, tag.id);
		});
	},

	saveNewTagToDatabase = function(label, description, parent_id) {
		if(parent != '') {
			$.ajax({
  				type: "POST",
  				url: api_url,
  				data: {query: "createtag", label: label, description : description, parent_id : parent_id},
  				success: processNewTagFromDatabase
		});
		}
	}

	createTagInDatabase = function(label, description, parent_id) {
		working = true;
		$.ajax({
  				type: "POST",
  				url: api_url,
  				data: {query: "createtag", label: label, description : description, parent_id : parent_id},
  				success: createTagFromServer
		});
	},

	processNewTagFromDatabase = function(json) {
		working = false;
		var obj = $.parseJSON(json);
		if(obj.status == "ok") {
			tags.result.push(obj.result);
			console.log(tags)
			addOrRemoveTagToTweet(obj.result.id);
			$('#newtag').attr('parent_id', '');
			$('#newtag > #breadcrumbs').html('');
			$('#newtag').fadeOut();
		} else {
			$('#newtag').attr('parent_id', '');
			$('#newtag > #breadcrumbs').html('');
			$('#newtag').fadeOut();
		}
		buildTagTree();
	},

	createTagFromServer = function(json) {
		var answer = $.parseJSON(json);
		createTag(answer.result);
		addTagToTweet(answer.result.id);
	},

	showTweet = function(html) {
		var tweet_text = $(".js-tweet-text.tweet-text", html).html();
		var user_screenname = $(".username.js-action-profile-name", html).html();
		var time = $("span.metadata span", html).html();
		if(tweet_text == null || user_screenname == null) {
			console.log("the tweet is a lie!");
			getRandomTweetFromDatabase();
			return;
		}

		displayTweet(tweet_text, user_screenname, time);
		working = false;
	},

	setLanguage = function(lang) {
		if(working) return;
		$('.lang').removeClass('used');
		$('.lang.'+lang).addClass('used');
		current_lang = lang;
	},

	clear = function() {
		console.log('clear');
		$('#user_name').html('');
		$('#time').html('');
		$('#text').html('');
		$('#spinner').fadeIn();
		$('.lang').removeClass('used');
		$('.label').removeClass('used');
		$('#usedtags').html('Used Tags:<br/><br/>');

		$('#library li').addClass('closed');
		$('#library li').not('[parent="0"]').addClass('hidden');


		$('#newtag').attr('parent_id', '');
		$('#newtag > #breadcrumbs').html('');
		$('#newtag').fadeOut();

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

	displayTweet = function(text, username, time) {
		$('#spinner').hide();
		$('#user_name').html(username.replace('@',''));
		$('#text').html(text);
		$('#time').html(time);
	},

	isTagEnabled = function(tag_id) {
		//var tagObject = all_tags[tag_id];
		//if(tagObject) {
	    	    return($.inArray(tag_id, used_tags) != -1);
		//}
	},

	addOrRemoveTagToTweet = function(tag_id) {
		if(working) return;

		if(isTagEnabled(tag_id)) {
			removeTagFromTweet(tag_id);
		} else {
			addTagToTweet(tag_id);
		}
		displayButton();
	},

	addTagToTweet = function(tag_id)
	{
		$.each(tags.result, function(index, value) {
			if(value.id == tag_id) {
				tag = value;
			}
		});

		//if(isTagEnabled(tag_id))
		//	return;

		//$('#used_tags').fadeOut().html('').fadeIn();
		//var tagObject = all_tags[tag_id];
		//if(tagObject) {
	    		used_tags.push(tag_id);	
			$('#usedtags').append('<span class="item" id="'+tag_id+'"">'+tag.label+'</span>');
			$('.library_entry[id="'+tag_id+'"]').addClass('used').removeClass('unused');
			//if(tagObject.parent_id)
				//addTagToTweet(tagObject.parent_id);
		//}
	},

	shownewTagDialog = function(parent_id) {
		var breadcrumbs = "";
		for(var id=parent_id;id>0;) {
			breadcrumbs = "/" + ($('#library li[id='+id+'] > .label').html()) + breadcrumbs;
			id = $('#library li[id='+id+']').attr('parent');
		}
		$('#newtag > #breadcrumbs').html(breadcrumbs);
		$('#newtag').attr('parent_id', parent_id);
		$('#newtag').fadeIn();
		$('#newtag').scrollTop();
	},

	removeTagFromTweet = function(tag_id)
	{

		if(!isTagEnabled(tag_id))
			return;

		//$('#used_tags').fadeOut().html('').fadeIn();
		
		//var tagObject = all_tags[tag_id];
		//if(tagObject) {
	    	used_tags = jQuery.grep(used_tags, function(value) {
			   return value != tag_id;
			});
			$('.item[id="'+tag_id+'"]').fadeOut().remove();
			$('.library_entry[id="'+tag_id+'"]').removeClass('used').addClass('unused');

			
		//}
	}// No comma here, that's right!

	return {
		init : init
	};

}(Aufschrei));
