package kirkModels.fields;

import kirkModels.config.Settings;
import kirkModels.queries.scripts.MySqlScript;
import kirkModels.queries.scripts.PsqlScript;

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
		
		this.MYSQL_TYPE = Settings.database.dbHandler.script.getIntType(maxValue);
		this.PSQL_TYPE = Settings.database.dbHandler.script.getIntType(maxValue);
	}
	
	public IntegerField() {
		super("", true, false);
	}

	@Override
	public String MySqlString() {
		String sql = this.label + " " + this.MYSQL_TYPE;
		if(!this.isNull){
			sql = sql + " NOT NULL";
		}
		if(this.label.equals("id")){
			sql = sql + " PRIMARY KEY";
		}
		return sql;
	}
	
	@Override
	public String PSqlString() {
		String sql = this.label + " " + this.PSQL_TYPE;
		if(!this.isNull){
			sql = sql + " NOT NULL";
		}
		if(this.label.equals("id")){
			sql = sql + " PRIMARY KEY";
		}
		return sql;
	}

}
