package kirkModels.db.sync;

public class DropField extends SQLOperation {

	public String fieldName;
	
	public DropField(String modelName, String fieldName) {
		super(modelName);
		
		this.fieldName = fieldName;
		this.mySQLString = "DROP COLUMN " + this.fieldName;
		this.pSQLString = "DROP COLUMN " + this.fieldName;
	}

}
