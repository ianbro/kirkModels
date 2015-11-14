package kirkModels.config;

import java.io.File;
import java.io.FileNotFoundException;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Scanner;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.sql.Connection;

import kirkModels.orm.backend.DbHandler;
import kirkModels.utils.Utilities;

public class MetaDatabase {

	public String name;
	public String urlHeader;
	public String host;
	public String port;
	public String schema;
	public String username;
	public String password;
	public String language;
	
	public Connection dbConnection;
	public DbHandler dbHandler;
	
	public MetaDatabase(String _name, File configFile) throws SQLException, ParseException{
		this.name = _name;
		this.readConfigs(configFile);
		this.connect();
		this.dbHandler = new DbHandler(this.dbConnection, this.schema, this.language);
	}
	
	private void readConfigs(File configFile) throws ParseException{
		Scanner configReader = null;
		try {
			configReader = new Scanner(configFile).useDelimiter("\\A");
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		JSONObject databaseMap = ((JSONObject)Utilities.json(configReader.next()).get("databases"));
		
		JSONObject trueDbMap = ((JSONObject)databaseMap.get(this.name));
		
		this.urlHeader = (String) trueDbMap.get("urlHeader");
		this.host = (String) trueDbMap.get("host");
		this.port = (String) trueDbMap.get("port");
		this.schema = (String) trueDbMap.get("schema");
		this.username = (String) trueDbMap.get("username");
		this.password = (String) trueDbMap.get("password");
		this.language = (String) trueDbMap.get("language");
	}
	
	public String getConnectionURL(){
		return this.urlHeader + "://" + this.host + ":" + this.port + "/" + this.schema;
	}
	
	public void connect() throws SQLException{
		this.dbConnection = DriverManager.getConnection(this.getConnectionURL());
	}
}
