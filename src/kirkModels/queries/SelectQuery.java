package kirkModels.queries;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;

import kirkModels.DbObject;
import kirkModels.config.Settings;
import kirkModels.orm.QuerySet;
import kirkModels.queries.scripts.WhereCondition;

public class SelectQuery extends WhereConditionedQuery {
	
	public ArrayList<String> fields = new ArrayList<String>();
	
	public ResultSet results;

	public SelectQuery(String _tabelName, ArrayList<WhereCondition> _conditions) {
		super(Settings.database.schema, _tabelName, _conditions);
		
		this.fields.add("*");
		this.setSql();
	}
	
	public SelectQuery(String _tabelName, ArrayList<String> fields, ArrayList<WhereCondition> _conditions) {
		super(Settings.database.schema, _tabelName, _conditions);
		
		this.fields = fields;
		this.setSql();
	}
	
	public void setSql(){
		this.command = this.toString();
	}
	
	public void run() throws SQLException{
		ResultSet results = Settings.database.dbHandler.executeQuery(this.command);
		
		this.results = results;
	}
	
	public String getFieldsString(){
		String fields = "";
		
		for (String field : this.fields) {
			fields = fields + field;
			
			if (this.fields.indexOf(field) < this.fields.size() - 1) {
				fields = fields + ", ";
			}
		}
		
		return fields;
	}

	@Override
	public String getMySqlString() {
		String str = "SELECT ";
		
		str = str + this.getFieldsString();
		
		str = str + " FROM " + this.dbName + "." + this.tableName;
		
		str = str + super.getMySqlString();
		
		str = end(str);
		return str;
	}

	@Override
	public String getPsqlString() {
		String str = "SELECT ";
		
		str = str + this.getFieldsString();
		
		str = str + " FROM " + this.tableName;
		
		str = str + super.getPsqlString();
		
		str = end(str);
		
		return str;
	}
	
	public String toString(){
		String language = Settings.database.language;
		
		String sql = "";
		
		switch (language) {
		case "MySQL":
			
			sql = this.getMySqlString();
			break;
			
		case "postgreSQL":
			
			sql = this.getPsqlString();
			break;

		default:
			
			sql = "No default language.";
			break;
		}
		
		return sql;
	}
}
