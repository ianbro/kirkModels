package kirkModels.orm.backend.sync.queries;

import kirkModels.fields.ForeignKey;
import kirkModels.fields.IntegerField;
import kirkModels.fields.SavableField;
import kirkModels.orm.DbObject;

public class AddForeignKey extends ColumnOperation {
	
	protected ForeignKey foreignKeyDef;

	public AddForeignKey(IntegerField _fromField, DbObject _reference, Integer _default, String onDelete) {
		// TODO Auto-generated constructor stub
		super(_fromField.label);
		this.foreignKeyDef = new ForeignKey(_fromField.label, _reference.getClass(), _fromField.isNull, _default, _fromField.unique, onDelete);
	}
	
	public AddForeignKey(ForeignKey _foreignKey) {
		// TODO Auto-generated constructor stub
		super(_foreignKey.label);
		this.foreignKeyDef = _foreignKey;
	}

	@Override
	public String getMySqlString() {
		// TODO Auto-generated method stub
		return "ADD CONSTRAINT " + this.foreignKeyDef.symbol + " FOREIGN KEY (" + this.fieldName + ") " + this.foreignKeyDef.MySqlString().split("::")[1];
	}

	@Override
	public String getPsqlString() {
		// TODO Auto-generated method stub
		return "ADD CONSTRAINT " + this.foreignKeyDef.symbol + " FOREIGN KEY (" + this.fieldName + ") " + this.foreignKeyDef.PSqlString().split("::")[1];
	}

}
