package kirkModels.orm.backend.sync.queries;

import java.lang.reflect.Constructor;

import iansLibrary.utilities.JSONMappable;
import kirkModels.fields.ForeignKey;
import kirkModels.fields.IntegerField;
import kirkModels.fields.SavableField;
import kirkModels.orm.DbObject;

public class AddForeignKey extends ColumnOperation implements JSONMappable {
	
	public ForeignKey foreignKeyDef;

	public AddForeignKey(IntegerField _fromField, DbObject _reference, Integer _default, String onDelete) {
		// TODO Auto-generated constructor stub
		super(_fromField.label);
		this.foreignKeyDef = new ForeignKey(_fromField.label, _reference.getClass(), _fromField.isNull, _default, _fromField.unique, onDelete);
	}
	
	public AddForeignKey(ForeignKey _foreignKeyDef) {
		// TODO Auto-generated constructor stub
		super(_foreignKeyDef.label);
		this.foreignKeyDef = _foreignKeyDef;
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

	@Override
	public Constructor getJsonConstructor() {
		// TODO Auto-generated method stub
		Class[] paramTypes = new Class[]{
				ForeignKey.class
		};
		try {
			return this.getClass().getConstructor(paramTypes);
		} catch (NoSuchMethodException | SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}
	
	@Override
	public String[] getConstructorFieldOrder() {
		return new String[]{
				"foreignKeyDef"
		};
	}

}
