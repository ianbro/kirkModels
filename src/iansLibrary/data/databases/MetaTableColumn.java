package iansLibrary.data.databases;

public class MetaTableColumn {
	
	public static final int COLUMN_NAME = 4;
	public static final int DATA_TYPE = 5;
	public static final int NULLABLE = 11;
	public static final int COLUMN_DEF = 13;
	public static final int COLUMN_SIZE = 7;

	private String columnName;
	private String dataType;
	private int nullable;
	private Object defaultValue;
	private int columnSize;
	
	public MetaTableColumn(String _columnName, String _dataType, int _nullable, Object _defaultValue, int _columnSize) {
		super();
		this.columnName = _columnName;
		this.dataType = _dataType;
		this.nullable = _nullable;
		this.defaultValue = _defaultValue;
		this.columnSize = _columnSize;
	}
	
	
}
