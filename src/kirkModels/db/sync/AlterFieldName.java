package kirkModels.db.sync;

import kirkModels.objects.SQLField;

public class AlterFieldName extends SQLOperation {

	public String columnName;
	public String newName;
	public String dataType;
	
	public AlterFieldName(String modelName, String columnName, String newName, String dataType) {
		super(modelName);
		
		this.columnName = columnName;
		this.newName = newName;
		this.dataType = dataType;
		
		this.pSQLString = "RENAME COLUMN " + this.columnName + " TO " + this.newName;
		this.mySQLString = "CHANGE " + this.columnName + " TO " + this.newName + " " + this.dataType.toUpperCase();
	}

}
