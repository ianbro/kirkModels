package tests;

import java.awt.Color;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import kirkModels.db.exceptions.IntegrityException;

public abstract class Tests {
	
	public static Connection systemConnection;
	public static String dbURL = Settings.DATABASE[0] + "://" + Settings.DATABASE[1] + ":" + Settings.DATABASE[2] + "/" + Settings.DATABASE[3];
	
	public static void main(String[] args) throws IntegrityException{
		// TODO Auto-generated method stub
		try {
			systemConnection = DriverManager.getConnection(dbURL, Settings.DATABASE[4], Settings.DATABASE[5]);
			System.out.println("Got connection to " + dbURL);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println("No Connection to " + dbURL);
		}
		System.out.println();
		throw new IntegrityException("model_name", "id", "1");
	}
}