package kirkModels.fields;

import java.lang.reflect.Constructor;
import java.util.HashMap;

import iansLibrary.data.databases.MetaTableColumn;
import iansLibrary.utilities.JSONMappable;

public class BooleanField extends SavableField<Boolean> implements JSONMappable {
	
	public Boolean defaultValue;

	public BooleanField(String _label, Boolean _defaultValue) {
		super(_label, false, false, _defaultValue);
		
		if(_defaultValue != null){
			this.value = _defaultValue;
			this.defaultValue = _defaultValue;
		} else{
			try {
				throw new Exception("defaultValue for BooleanField labeled " + _label + " must not be null.");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		this.MYSQL_TYPE = "BIT(1)";
		this.PSQL_TYPE = "BOOLEAN";
		this.JAVA_TYPE = Boolean.class;
	}
	
	public Constructor getJsonConstructor(){
		Class[] paramTypes = new Class[]{
				String.class,
				Boolean.class,
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
				"defaultValue",
		};
	}

	@Override
	public String MySqlString() {
		// TODO Auto-generated method stub
		String sql = "'" + this.label + "' " + this.getMySqlDefinition();
		return sql;
	}

	@Override
	public String PSqlString() {
		// TODO Auto-generated method stub
		String sql = "'" + this.label + "' " + this.getPsqlDefinition();
		return sql;
	}
	
	public String getMySqlDefinition() {
		String sql = this.MYSQL_TYPE;
		if(this.defaultValue == false){
			sql = sql + " DEFAULT " + 0;
		}
		else{
			sql = sql + " DEFAULT " + 1;
		}
		return sql;
	}
	
	public String getPsqlDefinition() {
		String sql = this.PSQL_TYPE;
		sql = sql + " DEFAULT " + this.defaultValue.toString().toUpperCase();
		return sql;
	}

	@Override
	public boolean equals(MetaTableColumn _column) {
		// TODO Auto-generated method stub
				if (!this.label.equals(_column.getColumnName())) {
					return false;
				} else if (!_column.getDataType().equalsIgnoreCase(this.MYSQL_TYPE.split("(")[0]) ||
							!_column.getDataType().equalsIgnoreCase(this.PSQL_TYPE)) {
					return false;
				} else if ((this.isNull.booleanValue() ? 1 : 0) != _column.getNullable()) {
					return false;
				} else if ((this.defaultValue == null && _column.getDefaultValue() != null)
						|| (this.defaultValue != null && _column.getDefaultValue() == null)) {
					return false;
				} else if (this.defaultValue != null && !this.defaultValue.equals(_column.getDefaultValue())) {
					return false;
				}
				return true;
	}

	@Override
	public boolean isSameColumn(MetaTableColumn _column) {
		// TODO Auto-generated method stub
		if (!this.label.equals(_column.getColumnName())) {
			return false;
		} else if (!_column.getDataType().equalsIgnoreCase("bit")
				|| !_column.getDataType().equalsIgnoreCase("boolean")) {
			return false;
		}
		return true;
	}

	/**
	 * @Override
	 */
	public HashMap<String, Object> getDifferences(MetaTableColumn _column) {
		HashMap<String, Object> diffs = new HashMap<String, Object>();
		
		if (this.getDifferenceNullable(_column) != null) {
			diffs.put("nullable", this.getDifferenceNullable(_column));
		} else if (this.getTypeDifference(_column) != null) {
			diffs.put("type", this.getTypeDifference(_column));
		}
		try {
			this.getDefaultValueDifference(_column);
			diffs.put("default", this.getDefaultValueDifference(_column));
		} catch (NoSuchFieldException e) {//if it throws the exception, that means they are the same.
		}
		
		return diffs;
	}
	
	public String getTypeDifference(MetaTableColumn _column) {
		if (this.getPseudoPsqlDefinition().equals(_column.getDataType())) {
			return null;
		} else {
			return _column.getDataType();
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

	/**
	 * @Override
	 * I don't know what will be returned at this point so this method returns null.
	 */
	public String getPseudoPsqlDefinition() {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * @Override
	 * I don't know what will be returned at this point so this method returns null.
	 */
	public String getPseudoMySqlDefinition() {
		// TODO Auto-generated method stub
		return null;
	}
}
