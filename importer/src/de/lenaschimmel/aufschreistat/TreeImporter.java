package de.lenaschimmel.aufschreistat;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.SQLException;

public class TreeImporter {

	/**
	 * @param args
	 * @throws IOException 
	 * @throws SQLException 
	 */
	public static void main(String[] args) throws IOException, SQLException {
		SqlHelper.initDbParams();
		SqlHelper.getConnection();
		
		BufferedReader br = new BufferedReader(new FileReader(args[0]));
		
		String line = br.readLine();
		Long parent[] = new Long[10];
		while(line != null)
		{
			int indent = getIndent(line);
			System.out.println(indent + ": " + line.trim());
			//parent[indent + 1] = SqlHelper.insertLabel(parent[indent], line.trim());
			line = br.readLine();
		}
	}

	private static int getIndent(String line) {
		int count = 0;
		for(char c : line.toCharArray())
			if(c == ' ')
				count++;
		return count / 4;
	}

}

