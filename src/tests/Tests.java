package tests;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;

import kirkModels.db.SQLHandler;
import kirkModels.db.exceptions.IntegrityException;
import kirkModels.db.exceptions.MultipleResultsException;
import kirkModels.objects.CharField;
import kirkModels.objects.IntegerField;
import kirkModels.objects.SQLField;

public abstract class Tests {
	
	public static Connection systemConnection;
	public static String dbURL = SettingsTest.DATABASE[0] + "://" + SettingsTest.DATABASE[1] + ":" + SettingsTest.DATABASE[2] + "/" + SettingsTest.DATABASE[3];
	public static SQLHandler sqlHandler;
	
	@SuppressWarnings("serial")
	public static void main(String[] args) throws IntegrityException{
		// TODO Auto-generated method stub
		try {
			systemConnection = DriverManager.getConnection(dbURL, SettingsTest.DATABASE[4], SettingsTest.DATABASE[5]);
			backend.Settings.database = SettingsTest.DATABASE;
			backend.Settings.systemConnection = systemConnection;
			sqlHandler = new SQLHandler(systemConnection);
			backend.Settings.sqlHandler = sqlHandler;
			System.out.println("Got connection to " + dbURL);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println("No Connection to " + dbURL);
		}
		
		Profile migrator = new Profile();
		try {
			backend.Settings.sqlHandler.createTable(migrator);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}