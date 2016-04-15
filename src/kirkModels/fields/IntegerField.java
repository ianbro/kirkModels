package kirkModels.fields;

import java.lang.reflect.Constructor;

import iansLibrary.data.databases.MetaTableColumn;
import iansLibrary.utilities.JSONMappable;
import kirkModels.config.Settings;

public class IntegerField extends SavableField<Integer> implements JSONMappable {

	public Integer maxVal;
	
	/**
	 * A field that, when called, will return an {@link Integer}. This field can be saved to a database and, depending on the maxValue parameter, will be saved as a TINYINT, SMALLINT, or a MEDIUMINT in SQL.
	 * @param _label - The name given to this field
	 * @param _isNull - whether this field can be set as <b>null</b>
	 * @param _defaultValue - default value of this field if left null
	 * @param _unique - whether this field contains a unique constraint
	 * @param autoIncrement - whether this field will automatically increment
	 * @param _maxVal - the maximum value that this field is allowed to be
	 */
	public IntegerField(String _label, Boolean _isNull, Integer _defaultValue, Boolean _unique, Integer _maxVal) {
		super(_label, _isNull, _unique, _defaultValue);
		
		if (_defaultValue == null || _defaultValue == Integer.MIN_VALUE) {
			this.value = null;
		} else {
			this.value = _defaultValue;
		}
		this.maxVal = _maxVal;
		this.JAVA_TYPE = Integer.class;
		
		this.MYSQL_TYPE = this.getMySqlIntType(_maxVal);
		this.PSQL_TYPE = this.getPsqlIntType(_maxVal);
	}
	
	public IntegerField() {
		super("", true, false, null);
	}
	
	@Override
	public Constructor getJsonConstructor(){
		Class[] paramTypes = new Class[]{
				String.class,
				Boolean.class,
				Integer.class,
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
				"maxVal"
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
	
	public String getMySqlIntType(Integer maxVal) {
		if(maxVal != null){
			if(maxVal <= 127){
				return "TINYINT";
			}
			else if(maxVal <= 32767){
				return "SMALLINT";
			}
			else if (maxVal <= 8388607) {
				return "MEDIUMINT";
			}
			else{  // if (maxVal <= 2147483647)
				return "INT";
			}
		}
		else {
			return "INT";
		}
	}
	
	public String getMySqlDefinition() {
		String def = this.MYSQL_TYPE;
		if(!this.isNull){
			def = def + " NOT NULL";
		}
		if(this.label.equals("id")){
			def = def + " PRIMARY KEY";
		}
		
		return def;
	}
	
	public String getPsqlDefinition() {
		String def = this.PSQL_TYPE;
		if(!this.isNull){
			def = def + " NOT NULL";
		}
		if(this.label.equals("id")){
			def = def + " PRIMARY KEY";
		}
		
		return def;
	}
	
	public String getPsqlIntType(Integer maxVal) {
		if(maxVal != null){
			if(maxVal <= 32767){
				return "smallint";
			}
			else if(maxVal <= 2147483647){
				return "integer";
			}
			else {
				return "bigint";
			}
		}
		else {
			return "integer";
		}
	}

	@Override
	public boolean equals(MetaTableColumn _column) {
		// TODO Auto-generated method stub
		if (!this.label.equals(_column.getColumnName())) {
			return false;
		} else if (!_column.getDataType().equalsIgnoreCase(this.MYSQL_TYPE) ||
				!_column.getDataType().equalsIgnoreCase(this.PSQL_TYPE)) {
			
			return false;
		} else if ((this.isNull.booleanValue() ? 1 : 0) != _column.getNullable()) {
			return false;
		} else if (!this.defaultValue.equals(_column.getDefaultValue())) {
			return false;
		} else if (this.maxVal != _column.getColumnSize()) {
			return false;
		}
		return true;
	}

}
