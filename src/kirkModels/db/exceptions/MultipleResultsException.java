package kirkModels.db.exceptions;

@SuppressWarnings("serial")
public class MultipleResultsException extends Exception {

	public MultipleResultsException(){
		super("Get method returned multiple results. Use filter instead");
	}
}
