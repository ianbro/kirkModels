package kirkModels.db.exceptions;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;

import kirkModels.objects.Model;
import kirkModels.objects.SQLField;

public final class SQLHandler {
	
	public Connection dbConnection;
	public String dbName;
	
	public SQLHandler(Connection conn) throws SQLException{
		this.dbConnection = conn;
		this.dbName = this.dbConnection.getSchema();
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
	
	@SuppressWarnings("unchecked")
	private <T extends Model> T getNextInstance(Class<T> model, ResultSet results) throws SQLException, InstantiationException, IllegalAccessException{
		HashMap<String, Object> fields = new HashMap<String, Object>();
		for(Field field: model.getDeclaredFields()){
			fields.put(field.getName(), results.getObject(field.getName(), field.getType()));
		}
		
		T instance = model.newInstance();
		for (String fieldName : instance.sqlFields.keySet()) {
			SQLField<?> field = instance.sqlFields.get(fieldName)
			field.set(instance.sqlFields.get(fieldName).JAVA_TYPE.cast(fields.get(fieldName)));
		}
		return instance;
	}
}
