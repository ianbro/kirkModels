package kirkModels.db.exceptions;

/*
 * Exception raised when the relational integrity of the database is affected, e.g. a foreign key check fails, duplicate key, etc
 */
@SuppressWarnings("serial")
public class IntegrityException extends Exception {

	public IntegrityException(){
		super("An object can not be saved to the database that violates a Unique Constraint");
	}
	
	/*
	 * Thrown when a Unique Constraint is violated in the database.
	 */
	public IntegrityException(String model, String field, String value){
		super(model + " with " + field + ": " + value + " already exists.");
	}
	
	/*
	 * Thrown when a foreign key reference does not exist.
	 */
	public IntegrityException(String model, String field, int foreignKeyId){
		super("Foreign key for " + field + " with id: " + foreignKeyId + " When saving " + model + " does not exist.");
	}
}
