package kirkModels.queries;

import kirkModels.queries.Query;

import java.sql.SQLException;

import kirkModels.DbObject;
import kirkModels.config.Settings;
import kirkModels.fields.SavableField;

public class CreateTableQuery extends Query {
	
	// This will be an instance of the class we want to migrate.
	// Information in classes can only be obtained through instantiated classes so
	// this is that instantiated object. it is not an actual saved object.
	public DbObject tempObject;

	public CreateTableQuery(String _dbName, DbObject _tempObject) {
		super(_dbName, _tempObject.tableName);
		// TODO Auto-generated constructor stub
		
		this.tempObject = _tempObject;
		
		this.setSql();
	}
	
	public String getMySqlFieldStrings(DbObject instance) {
		String sql = "";
		
		for (int i = 0; i < instance.savableFields.size(); i++) {
			SavableField field = null;
			try {
				field = ((SavableField) (instance.getClass().getField(instance.savableFields.get(i)).get(instance)));
			} catch (IllegalArgumentException | IllegalAccessException | NoSuchFieldException | SecurityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ClassCastException e) {
				// field is a ManyToManyField so We sync this another way. not included in the table string.
				continue;
			}
			sql = sql + "\n\t" + field.MySqlString();
			if(i != instance.savableFields.size() - 1){
				sql = sql + ",";
			}
		}
		
		return sql;
	}
	
	public String getPsqlFieldStrings(DbObject instance) {
		String sql = "";
		
		for (int i = 0; i < instance.savableFields.size(); i++) {
			SavableField field = null;
			try {
				field = ((SavableField) (instance.getClass().getField(instance.savableFields.get(i)).get(instance)));
			} catch (IllegalArgumentException | IllegalAccessException | NoSuchFieldException | SecurityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			sql = sql + "\n\t" + field.PSqlString();
			if(i != instance.savableFields.size() - 1){
				sql = sql + ",";
			}
		}
		
		return sql;
	}
	
	public String getFieldStrings(DbObject instance) {
		String language = Settings.database.language;
		
		String sql = "";
		
		switch (language) {
		case "MySQL":
			
			sql = this.getMySqlFieldStrings(instance);
			break;
			
		case "postgreSQL":
			
			sql = this.getPsqlFieldStrings(instance);
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
		String sql = "CREATE TABLE " + this.dbName + "." + this.tempObject.tableName + " (";
		sql = sql + this.getFieldStrings(this.tempObject);
		sql = sql + "\n);";
		return sql;
	}

	@Override
	public String getPsqlString() {
		// TODO Auto-generated method stub
		String sql = "CREATE TABLE " + this.tempObject.getClass().getName().replace('.', '_') + " (";
		sql = sql + this.getFieldStrings(this.tempObject);
		sql = sql + "\n);";
		return sql;
	}
	
}
