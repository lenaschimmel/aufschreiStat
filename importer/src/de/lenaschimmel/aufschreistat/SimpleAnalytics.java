package de.lenaschimmel.aufschreistat;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;

public class SimpleAnalytics {

	private static Connection con;

	/**
	 * @param args
	 * @throws IOException
	 * @throws ParseException
	 * @throws SQLException
	 * @throws TwitterException 
	 */
	public static void main(String[] args) throws IOException, ParseException,
			SQLException, TwitterException {
		SqlHelper.initDbParams();
		con = SqlHelper.getConnection();

				printKeywordTable();
	}

	private static void printKeywordTable() throws ParseException, SQLException {
		String[] columns = { "", "aufschrei", "gegenschrei", "sexismus", "lanz",
				"jauch", "annewill", "login", "zdflogin", "brüderle", "ebeling", "pütz", "sass", "bruhns" };
		//String[] columns = { "jauch", "#jauch" };

		SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm");

		System.out.print("Zeit;");
		for (String pat : columns)
			System.out.print(((pat.length() == 0) ? "[alle]" : pat) + ";");
		System.out.println();

		long start = sdf.parse("25.01.2013 00:00").getTime();
		long end =   sdf.parse("06.02.2013 00:00").getTime();
		//long start = sdf.parse("06.02.2013 00:00").getTime();
		//long end =   sdf.parse("06.02.2013 04:00").getTime();
		long interval = 1000 * 60 * 60;
		for (long intervalStart = start; intervalStart < end; intervalStart += interval) {
			long intervalEnd = intervalStart + interval;
			System.out.print(sdf.format(new Date(intervalStart)) + ";");

			for (String pat : columns)
				System.out.print(countTweetsForKeyword(intervalStart,
						intervalEnd, "%" + pat + "%") + ";");
			System.out.println();
		}
	}

	private static long countTweetsForKeyword(long intervalStart,
			long intervalEnd, String pattern) throws SQLException {
		Statement stmt = con.createStatement();
		String query = "SELECT COUNT(*) FROM statTweets WHERE created_at > FROM_UNIXTIME("
				+ intervalStart
				/ 1000
				+ ") AND created_at < FROM_UNIXTIME("
				+ intervalEnd / 1000 + ") AND text LIKE '" + pattern + "';";

		ResultSet minResult = stmt.executeQuery(query);
		minResult.next();

		long ret = minResult.getLong(1);
		return ret;
	}
}
