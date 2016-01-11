package kirkModels.queries;

import java.sql.SQLException;
import java.util.ArrayList;

import kirkModels.config.Settings;
import kirkModels.orm.DbObject;
import kirkModels.queries.scripts.WhereCondition;

public class DeleteQuery extends WhereConditionedQuery {

	public DeleteQuery(String _tabelName, ArrayList<WhereCondition> _conditions) {
		super(Settings.database.schema, _tabelName, _conditions);
		// TODO Auto-generated constructor stub
		
		this.setSql();
	}
	
	public void setSql(){
		this.command = this.toString();
	}

	@Override
	public void run() throws SQLException {
		// TODO Auto-generated method stub
		Settings.database.run(this.toString());
	}
	
	public String getMySqlString(){
		String sql = "DELETE FROM " + this.dbName + "." + this.tableName;
		
		sql = sql + super.getMySqlString();
		
		sql = end(sql);
		
		return sql;
	}
	
	public String getPsqlString(){
		String sql = "DELETE FROM " + this.tableName;
		
		sql = sql + super.getPsqlString();
		
		sql = end(sql);
		
		return sql;
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
