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
import kirkModels.db.sync.DBSynchronization;
import kirkModels.objects.ForeignKey;
import kirkModels.objects.Model;
import kirkModels.objects.SQLField;
import tests.TestModel;

public final class SQLHandler {
	
	public Connection dbConnection;
	public String dbName;
	private SqlScript sqlScript;
	
	/**
	 * The languages available for this {@link SQLHandler} that are mapped to a new instance of the corresponding {@link SqlScript}.
	 */
	private HashMap<String, SqlScript> lanuages;
	
	/**
	 * <p>
	 * Standard constructor for this {@link SQLHandler}. This will set the {@link SqlScript} as well.
	 * <p>
	 * @param conn - The connection to use for the database that this will be applied to.
	 */
	public SQLHandler(Connection conn){
		this.dbConnection = conn;
		this.dbName = Settings.database[3];
		this.lanuages = new HashMap<String, SqlScript>(){{
			put("MySQL",		new MySqlScript(dbName));
			put("postgreSQL",		new PSqlScript());
		}};
		this.sqlScript = this.lanuages.get(Settings.database[6]);
	}

	/**
	 * <p>
	 * The handler used to get the sql used to get an instance of the given <b>model</b> and then execute the query and return a set of instances based on the given <b>conditions</b>.
	 * <p>
	 * @param model - The type of which to return values.
	 * @param conditions - In order for a row to be returned from the table, it must meet these conditions.
	 * @return {@link ArrayList}<b>" of T"</b> - a list of type <b>T</b> that meet the given <b>conditions</b>.
	 * @throws SQLException
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 */
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
	
	/**
	 * <p>
	 * Gets a single instance from the result set given. The result set must have {@link ResultSet}.next() called already to be used.
	 * <p>
	 * @param model
	 * @param results
	 * @return
	 * @throws SQLException
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 */
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
	
	/**
	 * <p>
	 * Used to save changes to a specific instance. This instance must be pre-existing in the database.
	 * <p>
	 * @param instance - the specific object to be updated in the database.
	 * @throws SQLException
	 */
	public void updateInstance(Model instance) throws SQLException{
		Statement statement = this.dbConnection.createStatement();
		
		String sql = this.sqlScript.getUpdateString(instance);
		System.out.println(sql);
		statement.execute(sql);
	}
	
	/**
	 * <p>
	 * Used to save a specific instance to the database that did not exist before hand.
	 * <p>
	 * @param instance - The new specific instance to be saved to the database
	 * @throws SQLException
	 */
	@SuppressWarnings("rawtypes")
	public void saveNewInstance(Model instance) throws SQLException{
		Statement statement = this.dbConnection.createStatement();
		String sql = this.sqlScript.getSaveNewInstanceString(instance);
		System.out.println(sql);
		statement.execute(sql);
	}
	
	/**
	 * <p>
	 * Used to check if the specific instance exists in the database. This will return true if it does and false if it does not.
	 * <p>
	 * @param instance - The instance to check
	 * @return {@link boolean} - true if instance exists, false if not
	 * @throws SQLException
	 */
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
	
	/**
	 * <p>
	 * Used to delete a set of instances from the database
	 * <p>
	 * @param instances - The set of instances/rows to delete from the database
	 * @throws SQLException
	 */
	@SuppressWarnings("rawtypes")
	public void delete(ArrayList<Model> instances) throws SQLException{
		Statement statement = this.dbConnection.createStatement();
		for(Model instance: instances){
			String sql = this.sqlScript.getDeleteString(instance);
			statement.executeQuery(sql);
		}
	}
	
	/**
	 * <p>
	 * Used to create a table for the first time based on an instantiated model object. This will map the java class's {@link SQLField}s to the table as columns.
	 * <p>
	 * @param model - The instantiated object used to create the table
	 * @throws SQLException
	 */
	public void createTable(Model model) throws SQLException{
		Statement statement = this.dbConnection.createStatement();
		String sql = this.sqlScript.getTableString(model);
		statement.execute(sql);
	}
	
	/**
	 * <p>
	 * Finds the total number of rows in a table
	 * <p>
	 * @param type - The model of which to count saved items
	 * @return {@link Integer} - the total number of rows for the <b>type</b> given
	 * @throws SQLException
	 */
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
	
	/**
	 * <p>
	 * Used to change fields/columns in a table
	 * <p>
	 * @param changes - the {@link DBSynchronization} object that contains the changes to make
	 * @throws SQLException
	 */
	public void updateTable(DBSynchronization changes) throws SQLException{
		Statement statement = this.dbConnection.createStatement();
		String sql = null;
		statement.execute(sql);
	}
}
