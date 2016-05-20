package kirkModels.fields;

import java.lang.reflect.Constructor;
import java.util.HashMap;

import iansLibrary.data.databases.MetaTableColumn;
import iansLibrary.utilities.JSONMappable;

public class CharField extends SavableField<String> implements JSONMappable {

	public int maxLength;
	
	/**
	 * A field that, when called, will return a {@link String}. This field can be saved to the database and will be saved as a VARCHAR with a length of the parameter maxLength.
	 * @param _label - The name given to this field
	 * @param _isNull - whether this field can be set as <b>null</b>
	 * @param _defaultValue - default value of this field if left null
	 * @param _unique - whether this field contains a unique constraint
	 * @param _maxLength - the maximum length that this field is allowed to have
	 */
	public CharField(String _label, Boolean _isNull, String _defaultValue, Boolean _unique, Integer _maxLength){
		<IntegerField>super(_label, _isNull, _unique, _defaultValue);
		
		if(_defaultValue == null || _defaultValue.equals("null-value")) {
			this.defaultValue = null;
			this.value = null;
		} else {
			this.value = _defaultValue;
		}
		this.maxLength = _maxLength;
		this.MYSQL_TYPE = "VARCHAR(" + this.maxLength + ")";
		this.PSQL_TYPE = "varchar(" + this.maxLength + ")";
		this.JAVA_TYPE = String.class;
	}
	
	@Override
	public Constructor getJsonConstructor(){
		Class[] paramTypes = new Class[]{
				String.class,
				Boolean.class,
				String.class,
				Boolean.class,
				Integer.class
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
				"label",
				"isNull",
				"defaultValue",
				"unique",
				"maxLength"
		};
	}

	@Override
	public String MySqlString() {
		String sql = this.label + " " + this.getMySqlDefinition();
		return sql;
	}

	@Override
	public String PSqlString() {
		String sql = this.label + " " + this.getPsqlDefinition();
		return sql;
	}
	
	public String getMySqlDefinition() {
		String sql = this.MYSQL_TYPE;
		if(!this.isNull){
			sql = sql + " NOT NULL";
		}
		return sql;
	}
	
	public String getPsqlDefinition() {
		String sql = this.PSQL_TYPE;
		if(!this.isNull){
			sql = sql + " NOT NULL";
		}
		return sql;
	}
	
	public String getPseudoPsqlDefinition() {
		return "varchar";
	}
	
	public String getPseudoMySqlDefinition() {
		return "varchar";
	}

	@Override
	public boolean equals(MetaTableColumn _column) {
		// TODO Auto-generated method stub
		if (!this.label.equals(_column.getColumnName())) {
			return false;
		} else if (!_column.getDataType().equalsIgnoreCase(this.MYSQL_TYPE.split("[(]")[0]) ||
					!_column.getDataType().equalsIgnoreCase(this.PSQL_TYPE.split("[(]")[0])) {
			return false;
		} else if ((this.isNull.booleanValue() ? 1 : 0) != _column.getNullable()) {
			return false;
		} else if ((this.defaultValue == null && _column.getDefaultValue() != null)
				|| (this.defaultValue != null && _column.getDefaultValue() == null)) {
			return false;
		} else if (this.defaultValue != null && !this.defaultValue.equals(_column.getDefaultValue())) {
			return false;
		} else if (this.maxLength != _column.getColumnSize()) {
			return false;
		}
		return true;
	}

	@Override
	public boolean isSameColumn(MetaTableColumn _column) {
		// TODO Auto-generated method stub
		if (!this.label.equals(_column.getColumnName())) {
			return false;
		} else if (!_column.getDataType().equalsIgnoreCase("varchar")) {
			return false;
		}
		return true;
	}

	@Override
	public HashMap<String, Object> getDifferences(MetaTableColumn _column) {
		HashMap<String, Object> diffs = new HashMap<String, Object>();
		
		if (this.getDifferenceNullable(_column) != null) {
			diffs.put("nullable", this.getDifferenceNullable(_column));
		}
		if (this.getSizeDifference(_column) != null) {
			diffs.put("size", this.getSizeDifference(_column));
		}
		try {
			this.getDefaultValueDifference(_column);
			diffs.put("default", this.getDefaultValueDifference(_column));
		} catch (NoSuchFieldException e) {//if it throws the exception, that means they are the same.
		}
		
		return diffs;
	}
	
	public Integer getSizeDifference(MetaTableColumn _column) {
		if (this.maxLength == _column.getColumnSize()) {
			return null;
		} else {
			return _column.getColumnSize();
		}
	}
	
	public String getDefaultValueDifference(MetaTableColumn _column) throws NoSuchFieldException {
		if (this.defaultValue == null) {
			if (_column.getDefaultValue() != null) {
				return (String) _column.getDefaultValue();
			} else {
				throw new NoSuchFieldException("The two default values are the same.");
			}
		} else {
			if (_column.getDefaultValue() == null) {
				return (String) _column.getDefaultValue();
			} else {
				if (((String) _column.getDefaultValue()).equals(this.defaultValue)) {
					throw new NoSuchFieldException("The two default values are the same.");
				} else {
					return (String) _column.getDefaultValue();
				}
			}
		}
	}
	
	public Boolean getDifferenceNullable(MetaTableColumn _column) {
		if ((this.isNull.booleanValue() ? 1 : 0) != _column.getNullable()) {
			if (this.isNull) {
				return false;
			} else {
				return true;
			}
		} else {
			return null;
		}
	}
}
