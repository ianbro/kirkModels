package kirkModels.orm.backend.sync.queries;

import java.sql.SQLException;

import kirkModels.config.Settings;
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

	public String getMySqlString() {
		// TODO Auto-generated method stub
		String sql = "ADD COLUMN ";
		sql = sql + this.field.MySqlString();
		return sql;
	}

	public String getPsqlString() {
		// TODO Auto-generated method stub
		String sql = "ADD COLUMN ";
		sql = sql + this.field.PSqlString();
		return sql;
	}

}
