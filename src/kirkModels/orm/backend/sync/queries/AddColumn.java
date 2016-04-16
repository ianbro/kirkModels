package kirkModels.orm.backend.sync.queries;

import java.sql.SQLException;

import kirkModels.config.Settings;
import kirkModels.fields.ForeignKey;
import kirkModels.fields.ManyToManyField;
import kirkModels.fields.SavableField;
import kirkModels.orm.DbObject;

public class AddColumn extends ColumnOperation {
	
	private SavableField field;
	private ManyToManyField m2mField;

	public AddColumn(SavableField _field) {
		super(_field.label);
		this.field = _field;
	}
	
	public AddColumn(ManyToManyField _field) {
		super(null);
		this.m2mField = _field;
	}

	public String getMySqlString() {
		// TODO Auto-generated method stub
		String sql = "ADD COLUMN ";
		sql = sql + this.field.MySqlString().split("::")[0];
		return sql;
	}

	public String getPsqlString() {
		// TODO Auto-generated method stub
		String sql = "ADD COLUMN ";
		sql = sql + this.field.PSqlString().split("::")[0];
		return sql;
	}

}
