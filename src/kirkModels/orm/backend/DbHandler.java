package kirkModels.orm.backend;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import kirkModels.DbObject;
import kirkModels.orm.QuerySet;
import kirkModels.orm.backend.scripts.PSqlScript;

public class DbHandler {

	PSqlScript psqlScript;
	String dbName;
	Connection dbConnection;
	
	public DbHandler(Connection _dbConnection, String _dbName){
		this.dbConnection = _dbConnection;
		this.dbName = _dbName;
		this.psqlScript = new PSqlScript(this.dbName);
	}
	
	public boolean checkExists(DbObject instance){
		QuerySet results = null;
		try{
			String sql = this.psqlScript.getCheckExistsString(instance);
			Statement statement = this.dbConnection.createStatement();
			results = new QuerySet(statement.executeQuery(sql));
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
}
