package de.lenaschimmel.aufschreistat;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
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

		Statement stmt = con.createStatement();
		ResultSet minResult = stmt.executeQuery("SELECT min(id) FROM statTweets;");
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

					if (status.getId() < minId || minId == 0)
						minId = status.getId();

					inserted++;
				}
				System.out.println("#### Inserted " + inserted
						+ " tweets, skipped " + skipped
						+ " retweets. Waiting a little bit. Curent rate limit: "
						+ result.getRateLimitStatus().getRemaining());
				try {
					if(result.getRateLimitStatus().getRemaining() > 100)
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
