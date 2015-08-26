package tests;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import kirkModels.db.exceptions.IntegrityException;
import kirkModels.objects.IntegerField;
import kirkModels.objects.CharField;

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
		CharField string = new CharField("Name", false, "Hello", false, 10);
		System.out.println(string);
		string.set("I am Cool!");
		System.out.println(string);
		System.out.println(string.sqlString());
		
		IntegerField integer = new IntegerField("id", false, 1, true, true, 10000);
		System.out.println(integer);
		integer.set(5);
		System.out.println(integer);
		System.out.println(integer.sqlString());
	}
}