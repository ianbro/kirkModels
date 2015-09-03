package tests;

import java.util.HashMap;

public abstract class SettingsTest {
	
	/**
	 * <h1>public static HashMap<String, String[]> DATABASES_MAP</h1>
	 * <p>
	 * A map of database attributes. The keys are names of the database that they refer to. Calling the name of a database will return the array of String attributes for that database.
	 * <p>
	 * <b>Attribute Names in Order:</b><br>
	 * - url type<br>
	 * - domain/host<br>
	 * - port<br>
	 * - schema name<br>
	 * - username<br>
	 * - password<br>
	 * - language<br>
	 */
	@SuppressWarnings("serial")
	public static HashMap<String, String[]> DATABASES_MAP = new HashMap<String, String[]>(){{
		put("vagrant",
				new String[]{
						"jdbc:postgresql",
						"localhost",
						"5432",
						"vagrant",
						"vagrant",
						"vagrant",
						"postgreSQL"
						}
		);
		
		put("mysql",
				new String[]{
						"jdbc:mysql",
						"localhost",
						"3306",
						"test",
						"root",
						"saline54",
						"MySQL"
						}
		);
	}};
	
	/**
	 * <h1>public static String DB_TO_USE</h1>
	 * <br>
	 * <p>String used to call the database that will be used in the program. This will be called by <b>SettingsTest.DATABASE</b> to get the corresponding database array of attributes by key. This variable is the key that will be used.</p>
	 */
	public static String DB_TO_USE = "vagrant";
	
	/**
	 * <h1>public static String[] DATABASE</h1>
	 * <br>
	 * <p>This variable will use <b>SettingsTest.DB_TO_USE</b> as a key to call the array of database attributes from <b>SettingsTest.DATABASES_MAP.</b><p>
	 */
	public static String[] DATABASE = DATABASES_MAP.get(DB_TO_USE);
}
