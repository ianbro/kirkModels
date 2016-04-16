package kirkModels.orm.backend.sync.queries;

import kirkModels.fields.ForeignKey;
import kirkModels.fields.IntegerField;
import kirkModels.fields.SavableField;
import kirkModels.orm.DbObject;

public class AddForeignKey extends Constraint {
	
	protected String fieldName;
	protected ForeignKey foreignKeyDef;

	public AddForeignKey(IntegerField _fromField, DbObject _reference, Integer _default, String onDelete) {
		// TODO Auto-generated constructor stub
		this.foreignKeyDef = new ForeignKey(_fromField.label, _reference.getClass(), _fromField.isNull, _default, _fromField.unique, onDelete);
		this.fieldName = _fromField.label;
	}
	
	public AddForeignKey(ForeignKey _foreignKey) {
		// TODO Auto-generated constructor stub
		this.foreignKeyDef = _foreignKey;
		this.fieldName = _foreignKey.label;
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
