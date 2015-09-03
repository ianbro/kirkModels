package kirkModels.db;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;

import backend.Settings;
import kirkModels.db.scripts.MySqlScript;
import kirkModels.db.scripts.PSqlScript;
import kirkModels.db.scripts.SqlScript;
import kirkModels.objects.ForeignKey;
import kirkModels.objects.Model;
import kirkModels.objects.SQLField;
import tests.TestModel;

public final class SQLHandler {
	
	public Connection dbConnection;
	public String dbName;
	private SqlScript sqlScript;
	
	private HashMap<String, SqlScript> lanuages;
	
	public SQLHandler(Connection conn) throws SQLException{
		this.dbConnection = conn;
		this.dbName = Settings.database[3];
		this.lanuages = new HashMap<String, SqlScript>(){{
			put("MySQL",		new MySqlScript(dbName));
			put("postgreSQL",		new PSqlScript());
		}};
		this.sqlScript = this.lanuages.get(Settings.database[6]);
	}

	public <T extends Model> ArrayList<T> getInstances(Class<T> model, HashMap<String, Object> conditions) throws SQLException, InstantiationException, IllegalAccessException{
		ArrayList<T> instances = new ArrayList<T>();
		Statement statement = this.dbConnection.createStatement();
		
		//////Setting up Query//////
		String sql = this.sqlScript.getSelectString(model, conditions);
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
			try{
				fields.put(field.getName(), results.getObject(field.getName(), field.getType()));
			}
			catch (SQLFeatureNotSupportedException e) {
				fields.put(field.getName(), field.getType().cast(results.getObject(field.getName())));
			}
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
		
		String sql = this.sqlScript.getUpdateString(instance);
		System.out.println(sql);
		statement.execute(sql);
	}
	
	@SuppressWarnings("rawtypes")
	public void saveNewInstance(Model instance) throws SQLException{
		Statement statement = this.dbConnection.createStatement();
		String sql = this.sqlScript.getSaveNewInstanceString(instance);
		System.out.println(sql);
		statement.execute(sql);
	}
	
	public boolean checkExists(Model instance) throws SQLException{
		Statement statement = this.dbConnection.createStatement();
		String sql = this.sqlScript.getCheckExistsString(instance);
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
			String sql = this.sqlScript.getDeleteString(instance);
			statement.executeQuery(sql);
		}
	}
	
	public void createTable(Model model) throws SQLException{
		Statement statement = this.dbConnection.createStatement();
		String sql = this.sqlScript.getTableString(model);
		statement.execute(sql);
	}
	
	public <T> Integer count(Class<T> type) throws SQLException{
		Statement statement = this.dbConnection.createStatement();
		
		String[] sql = this.sqlScript.getCountString(type).split("<SPLIT>");
		
		ResultSet result = statement.executeQuery(sql[0]);
		Integer total = 0;
		if(result.next()){
			total = result.getInt(sql[1]);
		}
		
		return total;
	}
	
	public void updateTable(Model model) throws SQLException{
		Statement statement = this.dbConnection.createStatement();
		String sql = null;
		statement.execute(sql);
	}
}
