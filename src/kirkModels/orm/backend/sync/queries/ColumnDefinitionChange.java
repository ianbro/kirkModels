package kirkModels.orm.backend.sync.queries;

import kirkModels.config.Settings;
import kirkModels.fields.SavableField;

public class ColumnDefinitionChange extends ColumnOperation {
	
	private String mySqlNewDefinition;
	private String pSqlNewDefinition;

	public ColumnDefinitionChange(String _name, SavableField _newDefinition) {
		// TODO Auto-generated constructor stub
		super(_name);
		this.mySqlNewDefinition = _newDefinition.getMySqlDefinition();
		this.pSqlNewDefinition = _newDefinition.getPsqlDefinition();
		
	}
	
	public String getMySqlString() {
		String str = "MODIFY " + this.fieldName + " " + this.mySqlNewDefinition;
		return str;
	}
	
	public String getPsqlString() {
		String str = "ALTER COLUMN " + this.fieldName + " TYPE " + this.pSqlNewDefinition;
		return str;
	}

}
