package de.lenaschimmel.aufschreistat;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;

import twitter4j.FilterQuery;
import twitter4j.StallWarning;
import twitter4j.Status;
import twitter4j.StatusDeletionNotice;
import twitter4j.StatusListener;
import twitter4j.TwitterException;
import twitter4j.TwitterStream;
import twitter4j.TwitterStreamFactory;
import twitter4j.User;

import com.mysql.jdbc.exceptions.jdbc4.MySQLIntegrityConstraintViolationException;

public class Main {

	private static final class PrintingStatusListener implements StatusListener {
		public void onStatus(Status status) {
			 User user = status.getUser();
			System.out.println(user.getName() + " : " + status.getText());
			try {
				insertTweet(status.getId(), user.getId(), status.getText(),  status.getCreatedAt().getTime()
			, status.getInReplyToStatusId());
				try {
					insertUser(user.getId(), user.getScreenName(), user.getName(), user.getURL(), user.getMiniProfileImageURL());
				} catch (MySQLIntegrityConstraintViolationException e) {
					// e.printStackTrace();
					// ignore, this is just a duplicate user entry, that's normal
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

		public void onDeletionNotice(StatusDeletionNotice statusDeletionNotice) {}

		public void onTrackLimitationNotice(int numberOfLimitedStatuses) {}

		public void onException(Exception ex) {
		    ex.printStackTrace();
		}

		@Override
		public void onScrubGeo(long arg0, long arg1) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onStallWarning(StallWarning arg0) {
			// TODO Auto-generated method stub
			
		}
	}

	private static PreparedStatement insertTweetStmt;
	private static PreparedStatement insertUserStmt;

	private static void insertTweet(long id, long user_id, String text, long timestamp, Long reply_status_id) throws SQLException
	{
		insertTweetStmt.setLong(1, id);
		insertTweetStmt.setLong(2, user_id);
		insertTweetStmt.setString(3, text);
		insertTweetStmt.setTimestamp(4,new Timestamp(timestamp));
		if(reply_status_id != null && reply_status_id.longValue() != 0)
			insertTweetStmt.setLong(5, reply_status_id);
		else
			insertTweetStmt.setNull(5, Types.BIGINT);
		insertTweetStmt.executeUpdate();
	}
	
	private static void insertUser(long id, String screen_name, String name, String url, String profile_image_url) throws SQLException
	{
		insertUserStmt.setLong(1, id);
		insertUserStmt.setString(2, screen_name);
		insertUserStmt.setString(3, name);
		insertUserStmt.setString(4, url);
		insertUserStmt.setString(5, profile_image_url);
		insertUserStmt.executeUpdate();
	}
	
	public static void main(String[] args) throws TwitterException, IOException, SQLException{
		SqlHelper.initDbParams();
		
		Connection con = SqlHelper.getConnection();
		insertTweetStmt = con.prepareStatement("INSERT INTO tweets SET id = ?, user_id = ?, text = ?, timestamp = ?, reply_status_id = ?;");
		insertUserStmt = con.prepareStatement("INSERT INTO users SET id = ?, screen_name = ?, name = ?, url = ?, profile_image_url = ?;");
		
	    StatusListener listener = new PrintingStatusListener();
	    TwitterStream twitterStream = new TwitterStreamFactory().getInstance();
	    
	    String[] hashtags = {"#aufschrei"};
	    
	    FilterQuery query = new FilterQuery(0, null, hashtags);
	    
	    
	    twitterStream.addListener(listener);
		twitterStream.filter(query);
	}

}
