/**
 * 
 */
package kirkModels.objects;

import java.beans.Statement;
import java.sql.SQLException;
import java.sql.SQLXML;
import java.util.ArrayList;
import java.util.HashMap;

import backend.Settings;
import kirkModels.db.SQLHandler;
import kirkModels.db.exceptions.IntegrityException;
import kirkModels.db.exceptions.MultipleResultsException;

/**
 * An object can be saved to a database and contains a {@link HashMap} of sqlFields that can be saved with it.
 * @author Ian Kirkpatrick
 */
public abstract class Model <M extends Model>{

	public final HashMap<String, SQLField> sqlFields;
	
	public Model(HashMap<String, SQLField> fields){
		fields.put("id", new IntegerField("id", false, 1, true, 2147483647));
		this.sqlFields = fields;
		try{
			this.sqlFields.get("id").set(1);
		}
		catch(Exception e) {
			e.printStackTrace();
			this.sqlFields.get("id").set(count(this.getClass())+1);
		}
	}
	
	/**
	 * <p>
	 * Used to save the instantiated model object to the database. If the object already exists, it will update.
	 * @throws IntegrityException
	 */
	public void save(){
		try {
			if(Settings.sqlHandler.checkExists(this)){
				Settings.sqlHandler.updateInstance(this);
			}
			else{
				Settings.sqlHandler.saveNewInstance(this);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static <T extends Model> ArrayList<T> getAll(Class<T> model){
		ArrayList<T> results = new ArrayList<T>();
		try {
			results = Settings.sqlHandler.getInstances(model, null);
		} catch (InstantiationException | IllegalAccessException | SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return results;
	}
	
	/**
	 * <p>
	 * Used to instantiate a class that extends model and then save it to the database in the same method. Calling this method will instantiate AND save to the database.
	 * @param args - {@link HashMap}<{@link String}, {@link Object}> <b>args</b> - Attributes for this object to save
	 * @throws IntegrityException
	 * @throws SQLException 
	 */
	public static <M extends Model> M create(Class<M> model, HashMap<String, Object> args){
		M instance = null;
		try {
			instance = model.newInstance();
		} catch (InstantiationException | IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		for(String field: args.keySet()){
			Object val = args.get(field);
			((SQLField) instance.sqlFields.get(field)).set(val);
		}
		instance.save();
		return instance;
	}
	
	/**
	 * <p>
	 * Used to instantiate a class that extends model if the instance does not already exist. this method will first search for an instance with the same conditions as are supplied in the parameters. If it finds a result, it will return that/those results as a list. If if does not find any results, it will create an instance with the given parameters and save it to the database.
	 * @return {@link ArrayList}<{@link Model}> list of objects that meet the conditions given
	 * @param args - {@link HashMap}<{@link String}, {@link Object}> <b>args</b> - Attributes for this object to save
	 * @throws IntegrityException
	 * @throws SQLException 
	 * @throws IllegalAccessException 
	 * @throws InstantiationException 
	 */
	public static <M extends Model> ArrayList<M> getOrCreate(Class<M> model, HashMap<String, Object> conditions){
		ArrayList<M> results = null;
		results = filter(model, conditions);
		
		if(results.size() == 0){
			M instance = create(model, conditions);
			results.add(instance);
		}
		
		return results;
	}
	
	/**
	 * <p>
	 * Used to get a specific row in the models table and return it as an Object that extends Model.
	 * If this method finds several results, it will throw an error. You must use filter in this case.
	 * <p>
	 * @param conditions - {@link HashMap}<{@link String}, {@link Object}> <b>conditions</b> - Conditions that determine which row to return.
	 * @return {@link Model} the type that extends Model
	 * @throws SQLException 
	 * @throws MultipleResultsException 
	 */
	public static <M extends Model> M get(Class<M> model, HashMap<String, Object> conditions){
		ArrayList<M> instances = null;
		try {
			instances = Settings.sqlHandler.getInstances(model, conditions);
		} catch (InstantiationException | IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		if(instances.size() > 1){
			try {
				throw new MultipleResultsException();
			} catch (MultipleResultsException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return null;
		}
		else {
			if(instances.size() == 0){
				return null;
			}
			else{
				return instances.get(0);
			}
		}
	}
	
	/**
	 * <p>
	 * Used to get a list of model objects based on the given parameters
	 * <p>
	 * @return {@link ArrayList}<{@link Model}> list of objects that meet the conditions given
	 * @param args - {@link HashMap}<{@link String}, {@link Object}> <b>args</b> - Conditions that must be met to return an instance
	 * @throws SQLException 
	 * @throws IllegalAccessException 
	 * @throws InstantiationException 
	 */
	public static <M extends Model> ArrayList<M> filter(Class<M> model, HashMap<String, Object> conditions){
		ArrayList<M> results = null;
		try {
			results = Settings.sqlHandler.getInstances(model, conditions);
		} catch (InstantiationException | IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return results;
	}
	
	/**
	 * <p>
	 * Used to get the JAVA_TYPE value of a given field.
	 * <p>
	 * @param label - the label of the desired field
	 * @return {@link Object} - the JAVA_TYPE value of the field: label
	 */
	@SuppressWarnings({ "rawtypes" })
	public Object getField(String label){
		SQLField sQLField = sqlFields.get(label);
		return sQLField.value;
	}
	
	/**
	 * <p>
	 * Used to delete a set of instances given by the parameter <b>instances</b>
	 * <p>
	 * @param instances - the set of instances desired to delete from the database
	 */
	public static void delete(ArrayList<Model> instances){
		try {
			Settings.sqlHandler.delete(instances);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * <p>
	 * Used to delete this instance
	 * <p>
	 */
	@SuppressWarnings("serial")
	public void delete(){
		try {
			Settings.sqlHandler.delete(new ArrayList<Model>(){{addAll(this);}});
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * <p>
	 * Returns the number of instances of <b>M</b>
	 * <p>
	 * @param model - the class of which to return a total
	 * @return int - the total number of instances for model <b>M</b>
	 */
	public static <M extends Model> int count(Class<M> model){
		int total = 0;
		try {
			total = Settings.sqlHandler.count(model);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return total;
	}
}
