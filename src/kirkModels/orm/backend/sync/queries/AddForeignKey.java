package kirkModels.orm.backend.sync.queries;

import kirkModels.fields.ForeignKey;
import kirkModels.fields.SavableField;
import kirkModels.orm.DbObject;

public class AddForeignKey extends Constraint {
	
	protected String fieldName;
	protected ForeignKey foreignKeyDef;

	public AddForeignKey(SavableField _fromField, DbObject _reference, boolean isNull, int defaultValue, boolean unique, String onDelete) {
		// TODO Auto-generated constructor stub
		this.foreignKeyDef = new ForeignKey(_fromField.label, _reference.getClass(), isNull, defaultValue, unique, onDelete);
		this.fieldName = _fromField.label;
	}

	@Override
	public String getMySqlString() {
		// TODO Auto-generated method stub
		return "ADD CONSTRAINT " + this.foreignKeyDef.symbol + " FOREIGN KEY (" + this.fieldName + ") REFERENCES" + this.foreignKeyDef.MySqlString().split(" REFERENCES")[1];
	}

	@Override
	public String getPsqlString() {
		// TODO Auto-generated method stub
		return "ADD CONSTRAINT " + this.foreignKeyDef.symbol + " FOREIGN KEY (" + this.fieldName + ") REFERENCES" + this.foreignKeyDef.PSqlString().split(" REFERENCES")[1];
	}

}
