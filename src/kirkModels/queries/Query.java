package kirkModels.queries;

import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;

import kirkModels.DbObject;
import kirkModels.config.Settings;
import kirkModels.orm.QuerySet;
import kirkModels.queries.scripts.WhereCondition;

public abstract class Query {

	public String tableName;
	public String dbName;
	
	protected String command;
	
	public Query(String _dbName, String _tabelName) {
		this.dbName = _dbName;
		this.tableName = _tabelName;
	}
	
	public abstract void setSql();
	
	public abstract void run() throws SQLException;
	
	public abstract String getMySqlString();
	
	public abstract String getPsqlString();
	
	public String getCommand(){
		return this.command;
	}
	
	public String end(String sql){
		sql = sql + ";";
		
		return sql;
	}
}
