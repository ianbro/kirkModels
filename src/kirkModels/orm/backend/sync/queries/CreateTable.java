package kirkModels.orm.backend.sync.queries;

import kirkModels.queries.Query;

import java.sql.SQLException;
import java.util.ArrayList;

import kirkModels.config.Settings;
import kirkModels.fields.ForeignKey;
import kirkModels.fields.SavableField;
import kirkModels.orm.DbObject;

public class CreateTable extends Query {
	
	// This will be an instance of the class we want to migrate.
	// Information in classes can only be obtained through instantiated classes so
	// this is that instantiated object. it is not an actual saved object.
	public ArrayList<ForeignKey> foreignKeys = new ArrayList<ForeignKey>();
	public ArrayList<SavableField> fields = new ArrayList<SavableField>();

	public CreateTable(String _dbName, DbObject _tempObject) {
		super(_dbName, _tempObject.tableName);
		// TODO Auto-generated constructor stub
		
		for (String fieldName : _tempObject.savableFields) {
			this.fields.add(_tempObject.getField(fieldName));
		}
		
		this.setSql();
	}
	
	public CreateTable(String _dbName, String _tableName, SavableField[] _fields){
		super(_dbName, _tableName);
		
		for (SavableField field : _fields) {
			this.fields.add(field);
		}
		
		this.setSql();
	}
	
	public String getMySqlFieldStrings() {
		String sql = "";
		
		for (int i = 0; i < this.fields.size(); i++) {
			SavableField field = fields.get(i);
			sql = sql + "\n\t" + field.MySqlString().split("::")[0];

			if (field instanceof ForeignKey) {
				this.foreignKeys.add((ForeignKey) field);
			}
			
			if(i != this.fields.size() - 1){
				sql = sql + ",";
			}
		}
		
		return sql;
	}
	
	public String getPsqlFieldStrings() {
		String sql = "";
		
		for (int i = 0; i < this.fields.size(); i++) {
			SavableField field = this.fields.get(i);
			sql = sql + "\n\t" + field.PSqlString().split("::")[0];
			
			if (field instanceof ForeignKey) {
				this.foreignKeys.add((ForeignKey) field);
			}
			
			if(i != this.fields.size() - 1){
				sql = sql + ",";
			}
		}
		
		return sql;
	}
	
	public String getFieldStrings() {
		String language = Settings.database.language;
		
		String sql = "";
		
		switch (language) {
		case "MySQL":
			
			sql = this.getMySqlFieldStrings();
			break;
			
		case "postgreSQL":
			
			sql = this.getPsqlFieldStrings();
			break;

		default:
			
			sql = "No default language.";
			break;
		}
		
		return sql;
	}
	
	public String getMySqlForeignKeyStrings() {
		String sql = "";
		
		for (int i = 0; i < foreignKeys.size() - 1; i++) {
			ForeignKey fk = foreignKeys.get(i);
			sql = sql + "\n\tCONSRAINT " + fk.symbol + " FOREIGN KEY " + fk.label + " " + fk.MySqlString().split("::")[1] + ",";
		}
		ForeignKey fk = foreignKeys.get(foreignKeys.size() - 1);
		sql = sql + "\n\tCONSRAINT " + fk.symbol + " FOREIGN KEY " + fk.label + " " + fk.MySqlString().split("::")[1];
		
		return sql;
	}
	
	public String getPsqlForeignKeyStrings() {
		String sql = "";
		
		for (int i = 0; i < foreignKeys.size() - 1; i++) {
			ForeignKey fk = foreignKeys.get(i);
			sql = sql + "\n\tCONSRAINT " + fk.symbol + " FOREIGN KEY " + fk.label + " " + fk.MySqlString().split("::")[1] + ",";
		}
		ForeignKey fk = foreignKeys.get(foreignKeys.size() - 1);
		sql = sql + "\n\tCONSRAINT " + fk.symbol + " FOREIGN KEY " + fk.label + " " + fk.MySqlString().split("::")[1];
		
		return sql;
	}
	
	public String getForeignKeyStrings() {
		String language = Settings.database.language;
		
		String sql = "";
		
		switch (language) {
		case "MySQL":
			
			sql = this.getMySqlForeignKeyStrings();
			break;
			
		case "postgreSQL":
			
			sql = this.getPsqlForeignKeyStrings();
			break;

		default:
			
			sql = "No default language.";
			break;
		}
		
		return sql;
	}

	@Override
	public void setSql() {
		// TODO Auto-generated method stub
		this.command = this.toString();
	}

	@Override
	public void run() throws SQLException {
		// TODO Auto-generated method stub
		Settings.database.run(this.command);
	}

	@Override
	public String getMySqlString() {
		// TODO Auto-generated method stub
		String sql = "CREATE TABLE " + this.dbName + "." + this.tableName + " (";
		sql = sql + this.getFieldStrings();
		if (foreignKeys.size() > 0) {
			sql = sql + "," + this.getForeignKeyStrings();
		}
		sql = sql + "\n);";
		return sql;
	}

	@Override
	public String getPsqlString() {
		// TODO Auto-generated method stub
		String sql = "CREATE TABLE " + this.tableName + " (";
		sql = sql + this.getFieldStrings();
		if (foreignKeys.size() > 0) {
			sql = sql + "," + this.getForeignKeyStrings();
		}
		sql = sql + "\n);";
		return sql;
	}
	
}
