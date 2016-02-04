package kirkModels.orm.backend.sync.queries;

import kirkModels.fields.SavableField;

public class RenameField extends ColumnOperation {
	
	protected String newName;

	public RenameField(SavableField _field, String _newName) {
		super(_field.label);
		this.newName = _newName;
	}

	@Override
	public String getMySqlString() {
		// TODO Auto-generated method stub
		return "RENAME " + this.fieldName + " TO " + this.newName;
	}

	@Override
	public String getPsqlString() {
		// TODO Auto-generated method stub
		return "RENAME COLUMN " + this.fieldName + " TO " + this.newName;
	}

}
