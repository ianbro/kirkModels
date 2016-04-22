package iansLibrary.data.databases;

public class MetaForeignKeyConstraint {
	
	public static final int PKTABLE_NAME = 3;
	public static final int PKCOLUMN_NAME = 4;
	public static final int FKTABLE_NAME = 7;
	public static final int FKCOLUMN_NAME = 8;
	public static final int FK_NAME = 12;

	private String pkTableName;
	private String pkColumnName;
	private String fkTableName;
	private String fkColumnName;
	private String fkConstraintName;

	public MetaForeignKeyConstraint(String _pkTableName, String _pkColumnName, String _fkTableName, String _fkColumnName, String _fkConstraintName) {
		super();
		this.pkTableName = _pkTableName;
		this.pkColumnName = _pkColumnName;
		this.fkTableName = _fkTableName;
		this.fkColumnName = _fkColumnName;
		this.fkConstraintName = _fkConstraintName;
	}

	public String getPkTableName() {
		return pkTableName;
	}

	public String getPkColumnName() {
		return pkColumnName;
	}

	public String getFkTableName() {
		return fkTableName;
	}

	public String getFkColumnName() {
		return fkColumnName;
	}
	
	public String getFkConstraintName() {
		return fkConstraintName;
	}

	public void setFkConstraintName(String fkConstraintName) {
		this.fkConstraintName = fkConstraintName;
	}
}
