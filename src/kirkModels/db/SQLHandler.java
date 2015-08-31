package kirkModels.db;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;

import backend.Settings;
import kirkModels.objects.ForeignKey;
import kirkModels.objects.Model;
import kirkModels.objects.SQLField;
import tests.TestModel;

public final class SQLHandler {
	
	public Connection dbConnection;
	public String dbName;
	
	public SQLHandler(Connection conn) throws SQLException{
		this.dbConnection = conn;
		this.dbName = Settings.database[3];
	}

	public <T extends Model> ArrayList<T> getInstances(Class<T> model, HashMap<String, Object> conditions) throws SQLException, InstantiationException, IllegalAccessException{
		ArrayList<T> instances = new ArrayList<T>();
		Statement statement = this.dbConnection.createStatement();
		
		//////Setting up Query//////
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
		System.out.println(sql);
		//////Executing and getting Query//////
		ResultSet results = statement.executeQuery(sql);
		while(results.next()){
			instances.add(this.getNextInstance(model, results));
		}
		
		return instances;
	}
	
	@SuppressWarnings("rawtypes")
	private <T extends Model> T getNextInstance(Class<T> model, ResultSet results) throws SQLException, InstantiationException, IllegalAccessException{
		HashMap<String, Object> fields = new HashMap<String, Object>();
		for(Field field: model.getDeclaredFields()){
			fields.put(field.getName(), results.getObject(field.getName(), field.getType()));
		}
		
		T instance = model.newInstance();
		for (Object fieldNameObject : instance.sqlFields.keySet()) {
			String fieldName = (String) fieldNameObject;
			SQLField fieldValue = (SQLField) instance.sqlFields.get(fieldName);
			fieldValue.set(fieldValue.JAVA_TYPE.cast(fields.get(fieldName)));
		}
		((SQLField) instance.sqlFields.get("id")).set(results.getInt("id"));
		return instance;
	}
	
	public void updateInstance(Model instance) throws SQLException{
		Statement statement = this.dbConnection.createStatement();
		
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
		System.out.println(sql);
		statement.execute(sql);
	}
	
	@SuppressWarnings("rawtypes")
	public void saveNewInstance(Model instance) throws SQLException{
		Statement statement = this.dbConnection.createStatement();
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
		System.out.println(sql);
		statement.execute(sql);
	}
	
	public boolean checkExists(Model instance) throws SQLException{
		Statement statement = this.dbConnection.createStatement();
		String sql = "SELECT id FROM " + this.dbName + "." + instance.getClass().getSimpleName().toLowerCase() + " WHERE id=" + instance.getField("id") + ";";
		System.out.println(sql);
		ResultSet results = statement.executeQuery(sql);
		if(results.next()){
			System.out.println(instance.getField("id"));
			System.out.println(results.getInt("id"));
			if(instance.getField("id").equals(results.getInt("id"))){
				return true;
			}
			else {
				return false;
			}
		}
		else{
			return false;
		}
	}
	
	@SuppressWarnings("rawtypes")
	public void delete(ArrayList<Model> instances) throws SQLException{
		Statement statement = this.dbConnection.createStatement();
		for(Model instance: instances){
			String sql = "DELETE FROM " + this.dbName + "." + instance.getClass().getSimpleName().toLowerCase() + " WHERE id=" + instance.getField("id");
			statement.executeQuery(sql);
		}
	}
	
	public void createTable(Model model) throws SQLException{
		Statement statement = this.dbConnection.createStatement();
		String sql = this.getTableString(model);
		statement.execute(sql);
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
				sql = sql + field.sqlString().split("<SPLIT>")[0];
			}
			else {
				sql = sql + "\t" + field.sqlString();
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
				sql = sql + "\t" + field.sqlString().split("<SPLIT>")[1];
				if(!fieldObject.equals(instance.sqlFields.values().toArray()[instance.sqlFields.size()-1])){
					sql = sql + ",\n";
				}
			}
		}
		return sql;
	}
	
	public <T> Integer count(Class<T> type) throws SQLException{
		Statement statement = this.dbConnection.createStatement();
		
		String sql = "SELECT COUNT(*) FROM " + type.getSimpleName().toLowerCase();
		
		ResultSet result = statement.executeQuery(sql);
		Integer total = 0;
		if(result.next()){
			total = result.getInt("COUNT(*)");
		}
		
		return total;
	}
}
