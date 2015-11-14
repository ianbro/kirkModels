package kirkModels.orm.backend;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;

import kirkModels.DbObject;
import kirkModels.fields.SavableField;
import kirkModels.orm.QuerySet;
import kirkModels.orm.backend.scripts.PSqlScript;
import kirkModels.orm.backend.scripts.Script;
import kirkModels.utils.Utilities;

public class DbHandler {

	Script script;
	String dbName;
	Connection dbConnection;
	
	public DbHandler(Connection _dbConnection, String _dbName, String language){
		this.dbConnection = _dbConnection;
		this.dbName = _dbName;
		switch(language){
		case "postreSQL":
			this.script = new PSqlScript(this.dbName);
			break;
		default:
			this.script = null;
		}
	}
	
	public void run(String sql) throws SQLException{
		Statement statement = this.dbConnection.createStatement();
		statement.execute(sql);
	}
	
	public QuerySet executeQuery(String sql) throws SQLException{
		Statement statement = this.dbConnection.createStatement();
		return new QuerySet(statement.executeQuery(sql));
	}
	
	public boolean checkExists(DbObject instance){
		QuerySet results = null;
		try{
			String sql = this.script.getCheckExistsString(instance);
			results = this.executeQuery(sql);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Boolean exists = false;
		try {
			exists = results.results.getBoolean("exists");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return exists;
	}
	
	public void insertInto(DbObject instance){
		String sql = this.script.getSaveNewInstanceString(instance);
		try {
			this.run(sql);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void insertInto(HashMap<String, Object> kwargs){
		DbObject instance = Utilities.instantiateDbObject(kwargs);
		this.insertInto(instance);
	}
	
	public void update(DbObject instance){
		String sql = this.script.getUpdateInstanceString(instance);
		try {
			this.run(sql);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void deleteFrom(DbObject instance){
		String sql = this.script.getDeleteString(instance);
		try {
			this.run(sql);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public QuerySet selectFrom(HashMap<String, Object> kwargs){
		String sql = this.script.getSelectString((Class)kwargs.get("table_label"), kwargs);
		QuerySet results = null;
		try {
			results = this.executeQuery(sql);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return results;
	}
}
