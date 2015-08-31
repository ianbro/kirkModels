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
	public static String dbURL = Settings.DATABASE[0] + "://" + Settings.DATABASE[1] + ":" + Settings.DATABASE[2] + "/" + Settings.DATABASE[3];
	public static SQLHandler sqlHandler;
	
	@SuppressWarnings("serial")
	public static void main(String[] args) throws IntegrityException{
		// TODO Auto-generated method stub
		try {
			systemConnection = DriverManager.getConnection(dbURL, Settings.DATABASE[4], Settings.DATABASE[5]);
			backend.Settings.database = Settings.DATABASE;
			backend.Settings.systemConnection = systemConnection;
			sqlHandler = new SQLHandler(systemConnection);
			backend.Settings.sqlHandler = sqlHandler;
			System.out.println("Got connection to " + dbURL);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println("No Connection to " + dbURL);
		}
		
//		TestModel migrator = new TestModel("Test Name", 19);
//		try {
//			System.out.println(backend.Settings.sqlHandler.getTableString(migrator));
//			backend.Settings.sqlHandler.createTable(migrator);
//		} catch (SQLException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		
//		try {
//			TestModel test = TestModel.create(TestModel.class, new HashMap<String, Object>(){{put("name", "Joe Zimbo"); put("age", 23);}});
//		} catch (SQLException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		
		TestModel test1 = null;
		try {
			test1 = TestModel.get(TestModel.class, new HashMap<String, Object>(){{put("name", "Ian Kirkpatrick");}});
		} catch (SQLException | MultipleResultsException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		((IntegerField)test1.sqlFields.get("age")).set(24);
		try {
			test1.save();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}