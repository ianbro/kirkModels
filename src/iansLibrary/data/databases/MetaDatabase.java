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
	public ArrayList<String> getTables() throws SQLException {
		ResultSet tables = this.metaData.getTables(null, null, null, null);
		ArrayList<String> tableNames = new ArrayList<String>();
		
		while (tables.next()) {
			tableNames.add(tables.getString(3));
		}
		return tableNames;
	}
	
	public ArrayList<SavableField<?>> getFields(String tableName) throws SQLException {
		ResultSet fields = this.metaData.getColumns(null, null, tableName, null);
		ArrayList<SavableField<?>> fieldsList = new ArrayList<SavableField<?>>();
		
		while (fields.next()) {
			
		}
		return fieldsList;
	}
	
//	public SavableField<?> getSavableField(ResultSet result) {
		/*
		 * fields I need:
		 * 4. COLUMN_NAME String => column name
		 * 6. TYPE_NAME String => Data source dependent type name,
		 * 7. COLUMN_SIZE int => column size.
		 */
//		String fieldName = result.getString(4);
//		String columnDef = result.getString(13);
//	}
}
