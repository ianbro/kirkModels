package kirkModels.orm.backend.sync.queries;

import java.sql.SQLException;

import kirkModels.config.Settings;
import kirkModels.fields.ForeignKey;
import kirkModels.fields.ManyToManyField;
import kirkModels.fields.SavableField;
import kirkModels.orm.DbObject;

public class AddField extends ColumnOperation {
	
	private SavableField field;
	private ManyToManyField m2mField;

	public AddField(SavableField _field) {
		super(_field.label);
		this.field = _field;
	}
	
	public AddField(ManyToManyField _field) {
		super(null);
		this.m2mField = _field;
	}
	
	public String addMySqlForeignKey() {
		String sql = "ADD CONSTRAINT " + ((ForeignKey) this.field).symbol + " FOREIGN KEY (" + this.fieldName + ") " + this.field.MySqlString().split("::")[1];
		
		return sql;
	}
	
	public String addPsqlForeignKey() {
		String sql = "ADD CONSTRAINT " + ((ForeignKey) this.field).symbol + " FOREIGN KEY (" + this.fieldName + ") " + this.field.PSqlString().split("::")[1];
		
		return sql;
	}

	public String getMySqlString() {
		// TODO Auto-generated method stub
		String sql = "ADD COLUMN ";
		sql = sql + this.field.MySqlString().split("::")[0];
		if (this.field instanceof ForeignKey) {
			sql = sql + ",\n\t" + this.addMySqlForeignKey();
		}
		return sql;
	}

	public String getPsqlString() {
		// TODO Auto-generated method stub
		String sql = "ADD COLUMN ";
		sql = sql + this.field.PSqlString().split("::")[0];
		if (this.field instanceof ForeignKey) {
			sql = sql + ",\n\t" + this.addMySqlForeignKey();
		}
		return sql;
	}

}
