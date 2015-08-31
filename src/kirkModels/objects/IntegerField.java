package kirkModels.objects;

public class IntegerField extends SQLField<Integer> {

	public final String SQL_TYPE;
	public Integer maxVal;
	public boolean autoIncrement;
	
	/**
	 * A field that, when called, will return an {@link Integer}. This field can be saved to a database and, depending on the maxValue parameter, will be saved as a TINYINT, SMALLINT, or a MEDIUMINT in SQL.
	 * @param label - The name given to this field
	 * @param isNull - whether this field can be set as <b>null</b>
	 * @param defaultValue - default value of this field if left null
	 * @param unique - whether this field contains a unique constraint
	 * @param autoIncrement - whether this field will automatically increment
	 * @param maxValue - the maximum value that this field is allowed to be
	 */
	public IntegerField(String label, boolean isNull, Integer defaultValue, boolean unique, boolean autoIncrement, Integer maxValue) {
		<IntegerField>super(label, isNull, unique);
		
		this.autoIncrement = autoIncrement;
		if(defaultValue == null){
			defaultValue = null;
		}
		this.value = defaultValue;
		this.maxVal = maxValue;
		this.JAVA_TYPE = Integer.class;
		
		if(this.maxVal != null){
			if(this.maxVal < 255){
				this.SQL_TYPE = "TINYINT";
			}
			else if(this.maxVal < 65535){
				this.SQL_TYPE = "SMALLINT";
			}
			else {
				this.SQL_TYPE = "MEDIUMINT";
			}
		}
		else {
			this.SQL_TYPE = "MEDIUMINT";
		}
	}

	@Override
	public String sqlString() {
		String sql = this.label + " " + this.SQL_TYPE;
		if(!this.isNull){
			sql = sql + " NOT NULL";
		}
		if(this.autoIncrement){
			sql = sql + " AUTO_INCREMENT";
		}
		if(this.label.equals("id")){
			sql = sql + " PRIMARY KEY";
		}
		return sql;
	}
}
