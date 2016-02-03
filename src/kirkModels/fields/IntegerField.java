package kirkModels.fields;

import kirkModels.config.Settings;

public class IntegerField extends SavableField<Integer> {

	public Integer maxVal;
	
	/**
	 * A field that, when called, will return an {@link Integer}. This field can be saved to a database and, depending on the maxValue parameter, will be saved as a TINYINT, SMALLINT, or a MEDIUMINT in SQL.
	 * @param label - The name given to this field
	 * @param isNull - whether this field can be set as <b>null</b>
	 * @param defaultValue - default value of this field if left null
	 * @param unique - whether this field contains a unique constraint
	 * @param autoIncrement - whether this field will automatically increment
	 * @param maxValue - the maximum value that this field is allowed to be
	 */
	public IntegerField(String label, boolean isNull, Integer defaultValue, boolean unique, Integer maxValue) {
		super(label, isNull, unique);
		
		this.value = defaultValue;
		this.maxVal = maxValue;
		this.JAVA_TYPE = Integer.class;
		
		this.MYSQL_TYPE = this.getMySqlIntType(maxValue);
		this.PSQL_TYPE = this.getPsqlIntType(maxValue);
	}
	
	public IntegerField() {
		super("", true, false);
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

}
