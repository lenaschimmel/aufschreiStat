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
			if(line.trim().length() > 0)
			{
				int indent = getIndent(line);
				long newId = SqlHelper.insertLabel(parent[indent], line.trim());
				System.out.println(indent + ".   " + newId + " = " + line.trim() + ", parent = " + parent[indent]);
				parent[indent + 1] = newId;
			}
			line = br.readLine();
		}
	}

	private static int getIndent(String line) {
		int count = 0;
		for(char c : line.toCharArray())
			if(c == ' ')
				count++;
			else
				break;
		return count / 4;
	}

}

