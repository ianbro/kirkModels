package iansLibrary.data.databases;

import java.io.File;
import java.io.FileNotFoundException;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Scanner;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.sql.Connection;
import java.sql.DatabaseMetaData;

import kirkModels.config.Settings;
import kirkModels.fields.SavableField;
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
	public DatabaseMetaData metaData;
	
	public MetaDatabase(String _name, File configFile) throws SQLException, ParseException{
		this.name = _name;
		this.readConfigs(configFile);
		this.connect();
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
		
		configReader.close();
		
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
		String connectionURL = this.urlHeader + "://" + this.host + ":" + this.port + "/" + this.schema;
		return connectionURL;
	}
	
	public void connect() throws SQLException{
		this.dbConnection = DriverManager.getConnection(this.getConnectionURL(), this.username, this.password);
		this.metaData = this.dbConnection.getMetaData();
	}
	
	public void run(String sql) throws SQLException{
		Statement statement = this.dbConnection.createStatement();
		statement.execute(sql);
	}
	
	public ResultSet executeQuery(String sql) throws SQLException{
		Statement statement = this.dbConnection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
		return statement.executeQuery(sql);
	}
	
	public String toString(){
		return this.language + " database at: " + this.getConnectionURL();
	}
	
	/**
	 * documentation for this stuff is here: {@link http://tutorials.jenkov.com/jdbc/databasemetadata.html}
	 * @throws SQLException
	 */
	public ArrayList<MetaTable> getTables() throws SQLException {
		ResultSet tables = this.metaData.getTables(null, null, null, null);
		ArrayList<MetaTable> tableNames = new ArrayList<MetaTable>();
		
		while (tables.next()) {
			tableNames.add(new MetaTable(this, tables.getString(3)));
		}
		return tableNames;
	}
	
	public MetaTable getSpecificTable(String tableLabel) throws SQLException {
		for (MetaTable table : this.getTables()) {
			if (table.getTableName().equals(tableLabel)) {
				return table;
			}
		}
		return null;
	}
}
