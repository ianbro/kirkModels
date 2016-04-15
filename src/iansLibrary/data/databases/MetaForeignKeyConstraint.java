package iansLibrary.data.databases;

public class MetaForeignKeyConstraint {
	
	public static final int PKTABLE_NAME = 3;
	public static final int PKCOLUMN_NAME = 4;
	public static final int FKTABLE_NAME = 7;
	public static final int FKCOLUMN_NAME = 8;

	private String pkTableName;
	private String pkColumnName;
	private String fkTableName;
	private String fkColumnName;
	
	public MetaForeignKeyConstraint(String _pkTableName, String _pkColumnName, String _fkTableName, String _fkColumnName) {
		super();
		this.pkTableName = _pkTableName;
		this.pkColumnName = _pkColumnName;
		this.fkTableName = _fkTableName;
		this.fkColumnName = _fkColumnName;
	}
}
