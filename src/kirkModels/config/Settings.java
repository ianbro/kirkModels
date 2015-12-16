package kirkModels.config;

import java.io.File;
import java.io.FileNotFoundException;
import java.sql.SQLException;

import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

import iansLibrary.data.databases.MetaDatabase;
import kirkModels.utils.Utilities;

public abstract class Settings {

	public static MetaDatabase database;
	
	public static void syncSettings(File configFile) throws FileNotFoundException, ParseException, SQLException{
		JSONObject settingsJson = Utilities.json(configFile);
		try{
			database = new MetaDatabase((String) settingsJson.get("defaultDb"), configFile);
		} catch (SQLException e){
			throw new SQLException("The config file with path \"" + configFile.getAbsolutePath() + "\" does not contain a valid database connection.");
		}
	}
}
