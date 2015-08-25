/**
 * 
 */
package kirkModels.objects;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

import kirkModels.db.exceptions.IntegrityException;

/**
 * @author Ian
 * <p>
 * Basic Abstract object. This can be extended and used to create a custom model to be saved to a database
 * <p>
 * type T is the class that is extending Model
 */
public abstract class Model <T> {

	@SuppressWarnings("rawtypes")
	public final HashMap<String, Field> fields;
	
	public Model(HashMap<String, Field> fields){
		fields.put("id", new IntegerField("id", false, 1, true, true, 2147483647));
		this.fields = fields;
	}
	
	/**
	 * <p>
	 * Used to save the instantiated model object to the database. If the object already exists, it will update.
	 * @throws IntegrityException
	 */
	public void save() throws IntegrityException, SQLException {
		
	}
	
	/**
	 * <p>
	 * Used to instantiate a class that extends model and then save it to the database in the same method. Calling this method will instantiate AND save to the database.
	 * @param 
	 * - {@link HashMap}<{@link String}, {@link Object}> <b>args</b> - Attributes for this object to save
	 * @throws IntegrityException
	 */
	public void create(HashMap<String, Object> args) throws IntegrityException{
		
	}
	
	/**
	 * <p>
	 * Used to instantiate a class that extends model if the instance does not already exist. this method will first search for an instance with the same conditions as are supplied in the parameters. If it finds a result, it will return that/those results as a list. If if does not find any results, it will create an instance with the given parameters and save it to the database.
	 * @return {@link ArrayList}<{@link T}> list of objects that meet the conditions given
	 * @param 
	 * - {@link HashMap}<{@link String}, {@link Object}> <b>args</b> - Attributes for this object to save
	 * @throws IntegrityException
	 */
	public ArrayList<T> getOrCreate(HashMap<String, Object> args) throws IntegrityException{
		ArrayList<T> results = new ArrayList<T>();
		return results;
	}
	
	/**
	 * <p>
	 * Used to get a specific row in the models table and return it as an Object that extends Model.
	 * <p>
	 * @return {@link T} the type that extends Model
	 */
	public T get() {
		T value = null;
		return value;
	}
	
	/**
	 * <p>
	 * Used to get a list of model objects based on the given parameters
	 * <p>
	 * @return {@link ArrayList}<{@link T}> list of objects that meet the conditions given
	 * @param 
	 * - {@link HashMap}<{@link String}, {@link Object}> <b>args</b> - Conditions that must be met to return an instance
	 */
	public ArrayList<T> filter(HashMap<String, Object> args){
		ArrayList<T> results = new ArrayList<T>();
		return results;
	}
}
