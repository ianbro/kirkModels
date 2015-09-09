package kirkModels.db.sync;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;


import backend.Settings;
import kirkModels.db.scripts.MySqlScript;
import kirkModels.db.scripts.PSqlScript;
import kirkModels.db.scripts.SqlScript;

public class DBSynchronization {
	
	public String modelName;
	public Connection dbConnection;
	public String dbName;
	private String language;
	
	public ArrayList<SQLOperation> operations;
	
	public DBSynchronization(Connection conn){
		this.dbConnection = conn;
		this.language = Settings.database[6];
	}
	
	public String getMySqlString(){
		String sql = "";
		for(SQLOperation operation: this.operations){
			sql = sql + "ALTER TABLE " + this.modelName + " " + operation.mySQLString + ";\n";
		}
		return sql;
	}
	
	public String getPSqlString(){
		String sql = "";
		for(SQLOperation operation: this.operations){
			sql = sql + "ALTER TABLE " + this.modelName + " " + operation.pSQLString + ";\n";
		}
		return sql;
	}
	
	public void execute() throws SQLException{
		Statement statement = this.dbConnection.createStatement();
		String sql = "";
		switch (language) {
			case "MySQL":
				sql = this.getMySqlString();
				break;
			case "postgreSQL":
				sql = this.getPSqlString();
				break;
			default:
				break;
		}
		statement.execute(sql);
	}
}
