package kirkModels.objects;

import kirkModels.db.scripts.MySqlScript;
import kirkModels.db.scripts.PSqlScript;

public class IntegerField extends SQLField<Integer> {

	public final String MYSQL_TYPE;
	public final String PSQL_TYPE;
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
		<IntegerField>super(label, isNull, unique);
		
		if(defaultValue == null){
			defaultValue = null;
		}
		this.value = defaultValue;
		this.maxVal = maxValue;
		this.JAVA_TYPE = Integer.class;
		
		this.MYSQL_TYPE = MySqlScript.getIntType(maxValue);
		this.PSQL_TYPE = PSqlScript.getIntType(maxValue);
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
