package kirkModels.db;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;

import backend.Settings;
import kirkModels.objects.Model;
import kirkModels.objects.SQLField;

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
		String sql = "SELECT * FROM " + this.dbName + "." + model.getSimpleName().toLowerCase() + " WHERE";
		for(String var: conditions.keySet()){
			if(!var.equals(String.valueOf(conditions.keySet().toArray()[0]))){
				sql = sql + " AND";
			}
			Object val = conditions.get(var);
			sql = sql + " " + var + "=" + val.toString();
		}
		sql = sql + ";";
		
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
		for (String fieldName : instance.sqlFields.keySet()) {
			SQLField field = instance.sqlFields.get(fieldName);
			field.set(instance.sqlFields.get(fieldName).JAVA_TYPE.cast(fields.get(fieldName)));
		}
		return instance;
	}
	
	public void updateInstance(Model instance) throws SQLException{
		Statement statement = this.dbConnection.createStatement();
		
		String sql = "UPDATE" + this.dbName + "." + instance.getClass().getSimpleName() + " SET";
		for(String field: instance.sqlFields.keySet()){
			if(!field.equals(instance.sqlFields.keySet().toArray()[0])){
				sql = sql + ", ";
			}
			sql = sql + field + "=" + instance.sqlFields.get(field);
		}
		sql = sql + " WHERE id=" + instance.getField("id") + ";";
		
		statement.executeQuery(sql);
	}
	
	@SuppressWarnings("rawtypes")
	public void saveNewInstance(Model instance) throws SQLException{
		Statement statement = this.dbConnection.createStatement();
		String sql = "INSERT INTO " + this.dbName + "." + instance.getClass().getSimpleName() + " (";
		for(String field: instance.sqlFields.keySet()){
			if(!field.equals(instance.sqlFields.keySet().toArray()[0])){
				sql = sql + ", ";
			}
			sql = sql + field;
		}
		sql = sql + ") VALUES (";
		for(SQLField<?> field: instance.sqlFields.values()){
			if(!field.equals(instance.sqlFields.values().toArray()[0])){
				sql = sql + ", ";
			}
			sql = sql + field.get();
		}
		sql = sql + ");";
		
		statement.executeQuery(sql);
	}
	
	public boolean checkExists(Model instance) throws SQLException{
		Statement statement = this.dbConnection.createStatement();
		String sql = "SELECT id FROM " + this.dbName + "." + instance.getClass().getSimpleName() + " WHERE id=" + instance.getField("id") + ";";
		ResultSet resutlts = statement.executeQuery(sql);
		resutlts.next();
		if(instance.getField("id").equals(resutlts.getInt("id"))){
			return true;
		}
		else {
			return false;
		}
	}
}
