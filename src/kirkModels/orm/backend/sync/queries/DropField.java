package kirkModels.orm.backend.sync.queries;

import kirkModels.fields.ManyToManyField;
import kirkModels.fields.SavableField;

public class DropField extends ColumnOperation {
	
	public static final String CASCADE = " CASCADE";
	public static final String RESTRICT = " RESTRICT";
	/**
	 * returns "".
	 */
	public static final String DEFAULT = "";
	
	protected String option = "";

	public DropField(SavableField _field, String _option) {
		super(_field.label);

		this.option = _option;
	}
	
	public DropField(ManyToManyField _field) {
		super(_field.tableName);
		// TODO Auto-generated constructor stub
	}

	@Override
	public String getMySqlString() {
		// TODO Auto-generated method stub
		return "DROP COLUMN " + this.fieldName;
	}

	@Override
	public String getPsqlString() {
		// TODO Auto-generated method stub
		return "DROP COLUMN " + this.fieldName + this.option;
	}

}
