package de.lenaschimmel.aufschreistat;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
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
			try {
				System.out.println(status.getCreatedAt() + " : "
						+ status.getText());
				SqlHelper.insertTweet(status);
				SqlHelper.insertUser(status.getUser());
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

		public void onDeletionNotice(StatusDeletionNotice statusDeletionNotice) {
		}

		public void onTrackLimitationNotice(int numberOfLimitedStatuses) {
		}

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

	public static void main(String[] args) throws TwitterException,
			IOException, SQLException {
		SqlHelper.initDbParams();

		Connection con = SqlHelper.getConnection();
		if (con == null) {
			System.err.println("Connection error on startup. Exiting.");
			System.exit(1);
		}

		StatusListener listener = new PrintingStatusListener();
		TwitterStream twitterStream = new TwitterStreamFactory().getInstance();

		String[] hashtags = { "#aufschrei" };

		FilterQuery query = new FilterQuery(0, null, hashtags);

		twitterStream.addListener(listener);
		twitterStream.filter(query);
		
		System.out.println("Tweets müssten jetzt ankommen...");
	}
}
