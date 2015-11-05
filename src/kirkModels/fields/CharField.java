package kirkModels.fields;

public class CharField extends SavableField<String> {

	public int maxLength;
	
	/**
	 * A field that, when called, will return a {@link String}. This field can be saved to the database and will be saved as a VARCHAR with a length of the parameter maxLength.
	 * @param label - The name given to this field
	 * @param isNull - whether this field can be set as <b>null</b>
	 * @param defaultValue - default value of this field if left null
	 * @param unique - whether this field contains a unique constraint
	 * @param maxLength - the maximum length that this field is allowed to have
	 */
	public CharField(String label, boolean isNull, String defaultValue, boolean unique, int maxLength){
		<IntegerField>super(label, isNull, unique);
		
		this.value = defaultValue;
		this.maxLength = maxLength;
		this.MYSQL_TYPE = "VARCHAR(" + this.maxLength + ")";
		this.PSQL_TYPE = "varchar(" + this.maxLength + ")";
		this.JAVA_TYPE = String.class;
	}

	@Override
	public String MySqlString() {
		String sql = this.label + " " + this.MYSQL_TYPE;
		if(!this.isNull){
			sql = sql + " NOT NULL";
		}
		return sql;
	}

	@Override
	public String PSqlString() {
		String sql = this.label + " " + this.PSQL_TYPE;
		if(!this.isNull){
			sql = sql + " NOT NULL";
		}
		return sql;
	}
}
