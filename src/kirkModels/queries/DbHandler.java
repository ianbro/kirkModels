package kirkModels.queries;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;

import kirkModels.DbObject;
import kirkModels.queries.scripts.WhereCondition;
import kirkModels.queries.scripts.MySqlScript;
import kirkModels.queries.scripts.PsqlScript;
import kirkModels.queries.scripts.Script;
import kirkModels.utils.Utilities;

public class DbHandler {

	public Script script;
	public String dbName;
	Connection dbConnection;
	
	public DbHandler(Connection _dbConnection, String _dbName, String language){
		this.dbConnection = _dbConnection;
		this.dbName = _dbName;
		if(language.equals("postgreSQL")){
			this.script = new PsqlScript(this.dbName);
		} else {
			this.script = new MySqlScript(this.dbName);
		}
	}
	
	public void run(String sql) throws SQLException{
		Statement statement = this.dbConnection.createStatement();
		statement.execute(sql);
	}
	
	public ResultSet executeQuery(String sql) throws SQLException{
		Statement statement = this.dbConnection.createStatement();
		return statement.executeQuery(sql);
	}
	
	public boolean checkExists(DbObject instance){
		ResultSet results = null;
		try{
			String sql = this.script.getCheckExistsString(instance);
			results = this.executeQuery(sql);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Boolean exists = false;
		
		try {
			results.next();
			exists = this.script.exists(results);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return exists;
	}
}
