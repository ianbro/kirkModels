package kirkModels.db.sync;

public abstract class SQLOperation {

	public String mySQLString;
	public String pSQLString;
	protected String modelName;
	
	public SQLOperation(String modelName){
		
	}
}
