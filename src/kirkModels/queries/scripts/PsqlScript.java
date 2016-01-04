package kirkModels.queries.scripts;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

import kirkModels.DbObject;
import kirkModels.fields.SavableField;

public class PsqlScript extends Script {

	public PsqlScript(String _dbName) {
		super(_dbName);
		// TODO Auto-generated constructor stub
	}

	public String getTableString(DbObject testInstance) {
		String sql = "CREATE TABLE " + testInstance.getClass().getName().replace('.', '_') + " (";
		sql = sql + this.getFieldStrings(testInstance);
		sql = sql + "\n);";
		return sql;
	}

	public String getCheckExistsString(DbObject instance) {
		String sql = "SELECT exists(SELECT id FROM " + instance.getClass().getName() + " WHERE id=" + instance.id.val() + ");";
		return sql;
	}
	
	public Boolean exists(ResultSet results){
		Integer exists = 0;
		
		try {
			exists = results.getInt("count(id)");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		if (exists > 0) {
			return true;
		}
		else {
			return false;
		}
	}

	public String getFieldStrings(DbObject instance) {
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

	public String getIntType(Integer maxVal) {
		if(maxVal != null){
			if(maxVal <= 32767){
				return "smallint";
			}
			else if(maxVal <= 2147483647){
				return "integer";
			}
			else {
				return "bigint";
			}
		}
		else {
			return "integer";
		}
	}

}
