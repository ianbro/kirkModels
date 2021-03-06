package kirkModels.db.scripts;

import java.util.HashMap;

import kirkModels.objects.Model;
import kirkModels.objects.SQLField;

public abstract class SqlScript {

	/**
	 * <p>
	 * returns a sql {@link String} of the table for the given model.
	 * <p>
	 * @param model - the instantiated object of which to build the table around
	 * @return {@link String} - the sql value of the table for <b>model</b>
	 */
	public abstract String getTableString(Model model);
	
	/**
	 * <p>
	 * gets the sql {@link String} used to delete a specific <b>instance</b> from the database
	 * <p>
	 * @param instance - The instance to delete from the database
	 * @return {@link String} - the sql used to delete the <b>instance</b> from the database
	 */
	public abstract String getDeleteString(Model instance);
	
	/**
	 * <p>
	 * returns the sql {@link String} used to check if an instance exists in the database
	 * <p>
	 * @param instance - The model used to check if it exists
	 * @return {@link String} - The sql used to check if the <b>instance</b> exists
	 */
	public abstract String getCheckExistsString(Model instance);
	
	/**
	 * <p>
	 * returns the sql {@link String} used to save a new <b>instance</b> to the database
	 * <p>
	 * @param instance - The instance which will be saved
	 * @return - {@link String} - The sql used to save a new <b> instance</b>
	 */
	public abstract String getSaveNewInstanceString(Model instance);
	
	/**
	 * <p>
	 * returns the sql {@link String} used to update an <b>instance</b> in the database
	 * <p>
	 * @param instance - The instance which will be updated
	 * @return - {@link String} - The sql used to update an <b> instance</b>
	 */
	public abstract String getUpdateInstanceString(Model instance);
	
	/**
	 * <p>
	 * returns the sql {@link String} used to get a set of instances of <b>M</b>
	 * <p>
	 * @param <M> - The model type that will be used for the table name
	 * @param type - The class used to find the table to select from
	 * @param conditions - The field conditions which must be met in order for a row to be returned
	 * @return {@link String} - The sql used to select a set of instances from a table.
	 */
	public abstract <M extends Model> String getSelectString(Class<M> type, HashMap<String, Object> conditions);
	
	/**
	 * <p>
	 * returns the sql {@link String} used to find the number of rows in a table
	 * <p>
	 * @param <M> - The model type that will be used for the table name
	 * @param type - The class used to find the table to count the rows of
	 * @return - {@link String} - the sql used to find the count of <b>M</b>
	 */
	public abstract <M extends Model> String getCountString(Class<M> type);
}
