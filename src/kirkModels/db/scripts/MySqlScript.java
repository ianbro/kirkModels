package kirkModels.db.scripts;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;

import kirkModels.objects.ForeignKey;
import kirkModels.objects.Model;
import kirkModels.objects.SQLField;

public final class MySqlScript extends SqlScript{

	public String dbName;
	
	public MySqlScript(String dbName){
		this.dbName = dbName;
	}
	
	public String getTableString(Model model){
		String sql = "CREATE TABLE " + this.dbName + "." + model.getClass().getSimpleName().toLowerCase() + " (\n";
		sql = sql + getFieldStrings(model) + getForeignKeyStrings(model);
		sql = sql + "\n);";
		return sql;
	}
	
	private String getFieldStrings(Model instance){
		String sql = "";
		for(Object fieldObject: instance.sqlFields.values()){
			SQLField field = (SQLField) fieldObject;
			if(field.getClass().equals(ForeignKey.class)){
				sql = sql + field.MySqlString().split("<SPLIT>")[0];
			}
			else {
				sql = sql + "\t" + field.MySqlString();
			}
			if(!fieldObject.equals(instance.sqlFields.values().toArray()[instance.sqlFields.size()-1])){
				sql = sql + ",\n";
			}
		}
		return sql;
	}
	
	@SuppressWarnings("rawtypes")
	private String getForeignKeyStrings(Model instance){
		String sql = "";
		
		for(Object fieldObject: instance.sqlFields.values()){
			SQLField field = (SQLField)fieldObject;
			if(field.getClass().equals(ForeignKey.class)){
				if(fieldObject.equals(instance.sqlFields.values().toArray()[0])){
					sql = sql + ",/n";
				}
				sql = sql + "\t" + field.MySqlString().split("<SPLIT>")[1];
				if(!fieldObject.equals(instance.sqlFields.values().toArray()[instance.sqlFields.size()-1])){
					sql = sql + ",\n";
				}
			}
		}
		return sql;
	}

	public String getDeleteString(Model instance){
		String sql = "DELETE FROM " + this.dbName + "." + instance.getClass().getSimpleName().toLowerCase() + " WHERE id=" + instance.getField("id");
		return sql;
	}

	@Override
	public String getCheckExistsString(Model instance) {
		String sql = "SELECT id FROM " + this.dbName + "." + instance.getClass().getSimpleName().toLowerCase() + " WHERE id=" + instance.getField("id") + ";";
		return sql;
	}

	@Override
	public String getSaveNewInstanceString(Model instance) {
		String sql = "INSERT INTO " + this.dbName + "." + instance.getClass().getSimpleName().toLowerCase() + " (";
		for(Object fieldObject: instance.sqlFields.keySet()){
			String field = (String) fieldObject;
			if(!field.equals(instance.sqlFields.keySet().toArray()[0])){
				sql = sql + ", ";
			}
			sql = sql + field;
		}
		sql = sql + ") VALUES (";
		for(Object fieldObject: instance.sqlFields.values()){
			Object field = ((SQLField) fieldObject).get();
			if(field.getClass().equals(String.class)){
				field = "'" + field + "'";
			}
			
			if(!fieldObject.equals(instance.sqlFields.values().toArray()[0])){
				sql = sql + ", ";
			}
			sql = sql + (field);
		}
		sql = sql + ");";
		return sql;
	}

	@Override
	public String getUpdateString(Model instance) {
		String sql = "UPDATE " + this.dbName + "." + instance.getClass().getSimpleName().toLowerCase() + " SET ";
		for(Object fieldObject: instance.sqlFields.keySet()){
			String field = (String) fieldObject;
			
			Object fieldValue = ((SQLField) instance.sqlFields.get(fieldObject)).get();
			if(fieldValue.getClass().equals(String.class)){
				fieldValue = "'" + fieldValue + "'";
			}
			
			if(!field.equals(instance.sqlFields.keySet().toArray()[0])){
				sql = sql + ", ";
			}
			sql = sql + field + "=" + fieldValue;
		}
		sql = sql + " WHERE id=" + instance.getField("id") + ";";
		return sql;
	}

	@Override
	public String getSelectString(Class model, HashMap<String, Object> conditions) {
		String sql = "SELECT * FROM " + this.dbName + "." + model.getSimpleName().toLowerCase();
		if(conditions != null){
			sql = sql + " WHERE";
			for(String var: conditions.keySet()){
				if(!var.equals(String.valueOf(conditions.keySet().toArray()[0]))){
					sql = sql + " AND";
				}
				Object val = conditions.get(var);
				if(val.getClass().equals(String.class)){
					val = "'" + val + "'";
				}
				sql = sql + " " + var + "=" + val.toString();
			}
		}
		sql = sql + ";";
		return sql;
	}

	public static String getIntType(Integer maxVal) {
		if(maxVal != null){
			if(maxVal < 255){
				return "TINYINT";
			}
			else if(maxVal < 65535){
				return "SMALLINT";
			}
			else {
				return "MEDIUMINT";
			}
		}
		else {
			return "MEDIUMINT";
		}
	}
	
	@Override
	public String getCountString(Class type){
		String sql = "SELECT COUNT(*) FROM " + type.getSimpleName().toLowerCase();
		
		sql =  sql + "<SPLIT>" + "COUNT(*)";
		
		return sql;
	}
}
