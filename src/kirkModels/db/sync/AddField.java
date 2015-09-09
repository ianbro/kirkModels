package kirkModels.db.sync;

import kirkModels.objects.SQLField;

public class AddField extends SQLOperation {

	public SQLField field;
	
	public AddField(String modelName, SQLField field) {
		super(modelName);
		
		this.field = field;
		this.mySQLString = "ADD " + field.MySqlString();
		this.pSQLString = "ADD " + field.PSqlString();
	}

	
}
