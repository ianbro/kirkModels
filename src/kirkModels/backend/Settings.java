package kirkModels.backend;

import java.sql.Connection;

import kirkModels.db.SQLHandler;

public abstract class Settings {

	public static SQLHandler sqlHandler;
	
	public static String[] database;
	
	public static Connection systemConnection;
}
