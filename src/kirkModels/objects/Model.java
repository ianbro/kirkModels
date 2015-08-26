/**
 * 
 */
package kirkModels.objects;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

import kirkModels.db.exceptions.IntegrityException;

/**
 * An object can be saved to a database and contains a {@link HashMap} of sqlFields that can be saved with it.
 * @author Ian Kirkpatrick
 */
public abstract class Model extends Object {

	public final HashMap<String, SQLField<?>> sqlFields;
	
	public Model(HashMap<String, SQLField<?>> sqlFields){
		sqlFields.put("id", new IntegerField("id", false, 1, true, true, 2147483647));
		this.sqlFields = sqlFields;
	}
	
	/**
	 * <p>
	 * Used to save the instantiated model object to the database. If the object already exists, it will update.
	 * @throws IntegrityException
	 */
	public static void save() throws IntegrityException, SQLException {
		
	}
	
	/**
	 * <p>
	 * Used to instantiate a class that extends model and then save it to the database in the same method. Calling this method will instantiate AND save to the database.
	 * @param args - {@link HashMap}<{@link String}, {@link Object}> <b>args</b> - Attributes for this object to save
	 * @throws IntegrityException
	 */
	public static void create(HashMap<String, Object> args) throws IntegrityException{
		
	}
	
	/**
	 * <p>
	 * Used to instantiate a class that extends model if the instance does not already exist. this method will first search for an instance with the same conditions as are supplied in the parameters. If it finds a result, it will return that/those results as a list. If if does not find any results, it will create an instance with the given parameters and save it to the database.
	 * @return {@link ArrayList}<{@link Model}> list of objects that meet the conditions given
	 * @param args - {@link HashMap}<{@link String}, {@link Object}> <b>args</b> - Attributes for this object to save
	 * @throws IntegrityException
	 */
	public static ArrayList<Model> getOrCreate(HashMap<String, Object> args) throws IntegrityException{
		ArrayList<Model> results = new ArrayList<Model>();
		return results;
	}
	
	/**
	 * <p>
	 * Used to get a specific row in the models table and return it as an Object that extends Model.
	 * If this method finds several results, it will throw an error. You must use filter in this case.
	 * <p>
	 * @param conditions - {@link HashMap}<{@link String}, {@link Object}> <b>conditions</b> - Conditions that determine which row to return.
	 * @return {@link Model} the type that extends Model
	 */
	public static Model get(HashMap<String, Object> conditions) {
		Model value = null;
		return value;
	}
	
	/**
	 * <p>
	 * Used to get a list of model objects based on the given parameters
	 * <p>
	 * @return {@link ArrayList}<{@link Model}> list of objects that meet the conditions given
	 * @param args - {@link HashMap}<{@link String}, {@link Object}> <b>args</b> - Conditions that must be met to return an instance
	 */
	public static ArrayList<Model> filter(HashMap<String, Object> args){
		ArrayList<Model> results = new ArrayList<Model>();
		return results;
	}
	
	@SuppressWarnings({ "rawtypes" })
	public Object getField(String label){
		SQLField sQLField = sqlFields.get(label);
		return sQLField.value;
	}
}
