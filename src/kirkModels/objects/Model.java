/**
 * 
 */
package kirkModels.objects;

import kirkModels.db.exceptions.IntegrityException;

/**
 * @author Ian
 * <p>
 * Basic Abstract object. This can be extended and used to create a custom model to be saved to a database
 */
public abstract class Model {

	/*
	 * Used to save the instantiated model object to the database. If the object already exists, it will update.
	 */
	public void save() throws IntegrityException {
		
	}
	
	public void create() throws IntegrityException{
		
	}
}
