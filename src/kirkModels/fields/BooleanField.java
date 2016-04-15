package kirkModels.fields;

import java.lang.reflect.Constructor;

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
		} else if (!_column.getDataType().equalsIgnoreCase(this.MYSQL_TYPE.split("[(]")[0]) ||
				!_column.getDataType().equalsIgnoreCase(this.PSQL_TYPE)) {
			
			return false;
		} else if ((this.isNull.booleanValue() ? 1 : 0) != _column.getNullable()) {
			return false;
		} else if (!this.defaultValue.equals(_column.getDefaultValue())) {
			return false;
		}
		return true;
	}
}
