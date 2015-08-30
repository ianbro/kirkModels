package tests;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import kirkModels.db.SQLHandler;
import kirkModels.db.exceptions.IntegrityException;
import kirkModels.objects.CharField;
import kirkModels.objects.IntegerField;
import kirkModels.objects.SQLField;

public abstract class Tests {
	
	public static Connection systemConnection;
	public static String dbURL = Settings.DATABASE[0] + "://" + Settings.DATABASE[1] + ":" + Settings.DATABASE[2] + "/" + Settings.DATABASE[3];
	public static SQLHandler sqlHandler;
	
	public static void main(String[] args) throws IntegrityException{
		// TODO Auto-generated method stub
		try {
			systemConnection = DriverManager.getConnection(dbURL, Settings.DATABASE[4], Settings.DATABASE[5]);
			backend.Settings.database = Settings.DATABASE;
			backend.Settings.sqlHandler = sqlHandler;
			backend.Settings.systemConnection = systemConnection;
			sqlHandler = new SQLHandler(systemConnection);
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
		
		TestModel test = new TestModel("Ian Kirkpatrick", 19);
		System.out.println(test.getField("name"));
		System.out.println(test.getField("age"));
		((SQLField<Integer>) test.sqlFields.get("age")).set(20);
		System.out.println(test.getField("age"));
	}
}