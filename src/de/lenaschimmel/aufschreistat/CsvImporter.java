package de.lenaschimmel.aufschreistat;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;

import com.csvreader.CsvReader;

public class CsvImporter {

	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {
		SqlHelper.initDbParams();
		SqlHelper.getConnection();

		BufferedReader br = new BufferedReader(new FileReader(args[0]));
		br.mark(5000);
		String line = br.readLine();
		String[] firstLineParts = line.split(";");
		if (firstLineParts.length > 3
				&& firstLineParts[2].replace('"', ' ').trim()
						.equals("Visual Properties"))
			parseNodeFile(br);
		else
			parseSovietTvFile(br);

	}

	private static void parseSovietTvFile(BufferedReader br)
			throws IOException {
		SimpleDateFormat parserSDF = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

		br.reset();
		CsvReader reader = new CsvReader(br);
		reader.setEscapeMode(CsvReader.ESCAPE_MODE_BACKSLASH);
		boolean hasLine = reader.readRecord();
	
		int lineCount = 0;
		int tweetCount = 0;
		int userCount = 0;

		while (hasLine) {
			lineCount++;
		

			try {
				long id = Long.parseLong(reader.get(1));
				String url = reader.get(5);
				String screenName = url.substring(url.lastIndexOf('/')+1);

				long user_id = getUserByProfileUrl(screenName);
				if (user_id == 0) {
					user_id = Main.insertUser(0, screenName, reader.get(3),
							null,reader.get(4));
					userCount++;
					System.out.println("Inserted user " + userCount);
				}
				String text = reader.get(6);
				long timestamp = parserSDF.parse(reader.get(2)).getTime();
				Long reply_status_id = null;
				Main.insertTweet(id, user_id, text, timestamp, reply_status_id);
				tweetCount++;
				System.out.println("Inserted tweet " + tweetCount + ": " + text);
			} catch (Exception e) {
				System.err.println("Faulty line.");
				e.printStackTrace();
			}

			hasLine = reader.readRecord();
		}
		System.out.println("Processed " + lineCount + " lines");
	}

	private static long getUserByProfileUrl(String screenName)
			throws SQLException {
		PreparedStatement stmt = SqlHelper.getGetUserIdStmt();
		stmt.setString(1, screenName);
		ResultSet result = stmt.executeQuery();
		if (result.next())
			return result.getLong(1);

		return 0;
	}


	private static void parseNodeFile(BufferedReader br) throws IOException {
		
		
		
		
		
		// Attention - this method is not yet ready.
		
		
		
		
		SimpleDateFormat parserSDF = new SimpleDateFormat("dd.mm.yyyy HH:mm");

		br.reset();
		CsvReader reader = new CsvReader(br);
		reader.setEscapeMode(CsvReader.ESCAPE_MODE_DOUBLED);
		// ignore first two lines
		reader.readRecord();
		reader.readRecord();
		boolean hasLine = reader.readRecord();
	
		int lineCount = 0;
		int tweetCount = 0;
		int userCount = 0;

		while (hasLine) {
			lineCount++;
		

			try {
				String tweetUrl = reader.get(21);
				long id = Long.parseLong(tweetUrl.substring(tweetUrl.lastIndexOf('/')+1));
				String url = null;
				String screenName = reader.get(0);

				long user_id = getUserByProfileUrl(screenName);
				if (user_id == 0) {
					user_id = Main.insertUser(0, screenName, reader.get(3),
							null,reader.get(4));
					userCount++;
					System.out.println("Inserted user " + userCount);
				}
				String text = reader.get(6);
				long timestamp = parserSDF.parse(reader.get(2)).getTime();
				Long reply_status_id = null;
				Main.insertTweet(id, user_id, text, timestamp, reply_status_id);
				tweetCount++;
				System.out.println("Inserted tweet " + tweetCount + ": " + text);
			} catch (Exception e) {
				System.err.println("Faulty line.");
				e.printStackTrace();
			}

			hasLine = reader.readRecord();
		}
		System.out.println("Processed " + lineCount + " lines");
	}

}
