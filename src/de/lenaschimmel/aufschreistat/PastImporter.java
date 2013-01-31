package de.lenaschimmel.aufschreistat;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
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

		Connection con = SqlHelper.getConnection();
		if (con == null) {
			System.err.println("Connection error on startup. Exiting.");
			System.exit(1);
		}

		// Statement stmt = con.createStatement();
		// ResultSet minResult =
		// stmt.executeQuery("SELECT min(id) FROM tweets;");
		// minResult.next();

		Twitter twitter = TwitterFactory.getSingleton();
		long minId = 0; // 295900492507254785L;
		// minResult.getLong(1);
		// 294747096463073280L;

		while (true) {
			Query query = new Query("#aufschrei");
			query.setMaxId(minId);
			query.setCount(100);

			QueryResult result = twitter.search(query);
			List<Status> tweets = result.getTweets();
			int inserted = 0;
			int skipped = 0;
			for (Status status : tweets) {
				if (status.getRetweetedStatus() != null) {
					skipped++;
					continue;
				}
				System.out.println(status.getCreatedAt() + " : "
						+ status.getText());

				SqlHelper.insertTweet(status);
				SqlHelper.insertUser(status.getUser());

				if (status.getId() < minId)
					minId = status.getId();

				inserted++;
			}
			System.out.println("#### Inserted " + inserted
					+ " tweets, skipped " + skipped
					+ " retweets. Waiting a little bit. Curent rate limit: "
					+ result.getRateLimitStatus().getRemaining());
			try {
				Thread.sleep(6000);
			} catch (InterruptedException e) {
			}
		}
	}
}