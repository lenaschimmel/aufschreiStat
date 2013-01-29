package de.lenaschimmel.aufschreistat;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;

public class SqlHelper {
	static Connection con;
	private static String connectString;
	private static String username;
	private static String password;
	
	static void initDbParams() throws IOException {
		Properties properties = new Properties();
		BufferedInputStream stream = new BufferedInputStream(new FileInputStream("db.properties"));
		properties.load(stream);
		stream.close();
		connectString = properties.getProperty("connectString");
		username = properties.getProperty("username");
		password = properties.getProperty("password");
	}

	public static Connection getConnection() {
		if (con != null) {
			try {
				java.sql.Statement stmt = con.createStatement();
				ResultSet rset = stmt.executeQuery("SELECT NOW() FROM DUAL");
			} catch (SQLException e) {
				System.err.println("Detected db error, trying to reconnect.");
				con = null;
			}
		}
		if (con == null) {
			try {
				Class.forName("com.mysql.jdbc.Driver");
				con = DriverManager.getConnection(connectString, username, password);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return con;
	}
}
