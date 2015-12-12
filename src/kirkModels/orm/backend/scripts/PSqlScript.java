package kirkModels.orm.backend.scripts;

import java.lang.reflect.Field;
import java.util.HashMap;

import kirkModels.DbObject;
import kirkModels.fields.ForeignKey;
import kirkModels.fields.SavableField;

public class PSqlScript extends Script {

	public PSqlScript(String _dbName) {
		super(_dbName);
		// TODO Auto-generated constructor stub
	}

	@Override
	public String getTableString(DbObject testInstance) {
		String sql = "CREATE TABLE " + testInstance.getClass().getName().replace('.', '_') + " (";
		sql = sql + this.getFieldStrings(testInstance);
		sql = sql + "\n);";
		return sql;
	}

	@Override
	public String getDeleteString(DbObject instance) {
		String sql = "DELETE FROM " + instance.getClass().getName() + " WHERE id=" + instance.id.val() + ";";
		return sql;
	}

	@Override
	public String getCheckExistsString(DbObject instance) {
		String sql = "SELECT exists(SELECT id FROM " + instance.getClass().getName() + " WHERE id=" + instance.id.val() + ");";
		return sql;
	}

	@Override
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

	@Override
	public String getSaveNewInstanceString(DbObject instance) {
		//INSERT INTO person ( id, name, age ) VALUES ( 1, 'Johnny Joe', 24 );
		String str = "INSERT INTO " + instance.getClass().getName() + " ( ";
		for(int i = 0; i < instance.savableFields.size(); i ++){
			String field = instance.savableFields.get(i);
			str = str + field.toLowerCase();
			if(i < instance.savableFields.size() - 1){
				str = str + ", ";
			}
		}
		str = str + " ) VALUES ( ";
		for(int i = 0; i < instance.savableFields.size(); i ++){
			String fieldVal = null;
			try {
				SavableField instanceField = ((SavableField) (instance.getClass().getField(instance.savableFields.get(i)).get(instance)));
				fieldVal = instanceField.val().toString();
				if (instanceField.JAVA_TYPE.equals(String.class)) {
					str = str + "'";
					fieldVal = fieldVal + "'";
				}
			} catch (IllegalArgumentException | IllegalAccessException | NoSuchFieldException | SecurityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (NullPointerException n){
				fieldVal = "NULL";
			}
			str = str + fieldVal;
			if(i < instance.savableFields.size() - 1){
				str = str + ", ";
			}
		}
		str = str + " );";
		return str;
	}

	@Override
	public String getUpdateInstanceString(DbObject instance) {
		String sql = "UPDATE " + instance.getClass().getName();
		for(int i = 0; i < instance.savableFields.size(); i ++){
			SavableField field = null;
			try {
				field = ((SavableField) (instance.getClass().getField(instance.savableFields.get(i)).get(instance)));
			} catch (IllegalArgumentException | IllegalAccessException | NoSuchFieldException | SecurityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			sql = sql + "\n\tSET " + field.label;
			try{
				if (field.val().getClass().equals(String.class)) {
					sql = sql + "='" + field.val() + "'";
				}
				else {
					sql = sql + "=" + field.val();
				}
			} catch (NullPointerException e){
				sql = sql + "=NULL";
			}
			if (i < instance.savableFields.size() - 1) {
				sql = sql + ",";
			}
		}
		sql = sql + ";";
		return sql;
	}

	@Override
	public <M extends DbObject> String getSelectString(Class<M> type, HashMap<String, Object> conditions) {
		//SELECT * FROM person WHERE name='Johnny Joe' AND age=24;
		String str = "SELECT * FROM " + type.getName();
		if(conditions.size() > 0){
			str = str + " WHERE ";
		}
		int i = 0;
		for(String fieldName : conditions.keySet()){
			System.out.println(fieldName);
			System.out.println(conditions.get(fieldName));
			str = str + fieldName.split("::")[0] + fieldName.split("::")[1];
			if (conditions.get(fieldName).getClass().equals(String.class)) {
				str = str + "'";
			}
			str = str + conditions.get(fieldName);
			if (conditions.get(fieldName).getClass().equals(String.class)) {
				str = str + "'";
			}
			i ++;
			if(i < conditions.size()){
				str = str + " AND ";
			}
		}
		str = str + ";";
		return str;
	}

	@Override
	public <T extends DbObject> String getCountString(Class<T> type, HashMap<String, Object> kwargs) {
		String sql = "SELECT count(*) FROM " + type.getName();
		if (kwargs.size() > 0) {
			sql = sql + " WHERE ";
			int i = 0;
			for(String key : kwargs.keySet()){
				sql = sql + key + "=" + kwargs.get(key);
				i ++;
				if(i < kwargs.size()){
					sql = sql + " AND ";
				}
			}
		}
		sql = sql + ";";
		return sql;
	}
	
	public static String getIntType(Integer maxVal) {
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
