package kirkModels.queries.scripts;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

import kirkModels.DbObject;
import kirkModels.fields.ManyToManyField;
import kirkModels.fields.SavableField;

public class MySqlScript extends Script {

	public MySqlScript(String _dbName) {
		super(_dbName);
		// TODO Auto-generated constructor stub
	}

	public String getTableString(DbObject testInstance){
		String sql = "CREATE TABLE " + this.dbName + "." + testInstance.tableName + " (";
		sql = sql + this.getFieldStrings(testInstance);
		sql = sql + "\n);";
		return sql;
	}
	
	public String getCheckExistsString(DbObject instance) {
		String sql = "SELECT COUNT(id) from " + this.dbName + "." + instance.tableName + " WHERE id=" + instance.id.val() + ";";
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
	
	public String getIntType(Integer maxVal) {
		if(maxVal != null){
			if(maxVal <= 127){
				return "TINYINT";
			}
			else if(maxVal <= 32767){
				return "SMALLINT";
			}
			else if (maxVal <= 8388607) {
				return "MEDIUMINT";
			}
			else{  // if (maxVal <= 2147483647)
				return "INT";
			}
		}
		else {
			return "INT";
		}
	}
}
