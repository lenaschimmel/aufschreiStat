package de.lenaschimmel.aufschreistat;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import twitter4j.Query;
import twitter4j.QueryResult;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;

public class PastImporter {

	private static Connection con;

	/**
	 * @param args
	 * @throws IOException
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 * @throws TwitterException
	 * @throws SQLException
	 */
	public static void main(String[] args) throws IOException,
			ParserConfigurationException, SAXException, TwitterException,
			SQLException {
		SqlHelper.initDbParams();
		SqlHelper.getConnection();

		con = SqlHelper.getConnection();
		if (con == null) {
			System.err.println("Connection error on startup. Exiting.");
			System.exit(1);
		}

		importMissingPrecedingTweets();

		//importPastTweets();
	}

	private static void importMissingPrecedingTweets() throws SQLException,
			TwitterException {
		Twitter twitter = TwitterFactory.getSingleton();

		Statement stmt = con.createStatement();
		String query = "SELECT l.in_reply_to_status_id, l.text, l.created_at FROM statTweets l WHERE l.in_reply_to_status_id > -1	AND l.in_reply_to_status_id NOT	IN (SELECT r.id	FROM statTweets r)";

		ResultSet result = stmt.executeQuery(query);
		while (result.next()) {
			Status status = null;
			long id = result.getLong(1);
			try {
				String text = result.getString(2);
				Timestamp createdAt = result.getTimestamp(3);
				status = twitter.showStatus(id);
				System.out.print("Orig: ");
				SqlHelper.insertTweet(status);
				System.out.println("Reply: " + createdAt + ": " + text + "\n");
				SqlHelper.insertUser(status.getUser());
			} catch (Exception e1) {
				System.out.println(e1.getMessage());
				SqlHelper.insertDummyTweet(id, e1.getMessage());
			}
			try {
				if (status != null
						&& status.getRateLimitStatus().getRemaining() > 100)
					Thread.sleep(2000);
				else
					Thread.sleep(6000);
			} catch (InterruptedException e) {
			}
		}

	}

	public static void importPastTweets() throws SQLException {
		Statement stmt = con.createStatement();
		ResultSet minResult = stmt
				.executeQuery("SELECT min(id) FROM statTweets;");
		minResult.next();

		Twitter twitter = TwitterFactory.getSingleton();
		long minId = minResult.getLong(1);
		minId = 0;

		while (true) {
			try {
				Query query = new Query("#aufschrei");
				query.setMaxId(minId);
				query.setCount(100);

				QueryResult result = twitter.search(query);
				List<Status> tweets = result.getTweets();
				int inserted = 0;
				
				for (Status status : tweets) {
					SqlHelper.insertTweet(status);
					SqlHelper.insertUser(status.getUser());
					inserted++;
				
					if (status.getId() < minId || minId == 0)
						minId = status.getId();

				}
				System.out
						.println("#### Inserted "
								+ inserted
								+ " tweets. Waiting a little bit. Curent rate limit: "
								+ result.getRateLimitStatus().getRemaining());
				try {
					if (result.getRateLimitStatus().getRemaining() > 100)
						Thread.sleep(2000);
					else
						Thread.sleep(6000);
				} catch (InterruptedException e) {
				}
			} catch (TwitterException e) {
				e.printStackTrace();
			}
		}
	}
}
