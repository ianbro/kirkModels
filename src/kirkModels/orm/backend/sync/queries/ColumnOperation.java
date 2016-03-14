package kirkModels.orm.backend.sync.queries;

public abstract class ColumnOperation extends Operation {
	
	protected String fieldName;

	public ColumnOperation(String _fieldName) {
		// TODO Auto-generated constructor stub
		this.fieldName = _fieldName;
	}

}
