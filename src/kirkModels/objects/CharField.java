package kirkModels.objects;

public class CharField extends SQLField<String> {

	public final String SQL_TYPE;
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
		this.SQL_TYPE = "VARCHAR(" + this.maxLength + ")";
		this.JAVA_TYPE = String.class;
	}

	@Override
	public String sqlString() {
		String sql = this.label + " " + this.SQL_TYPE;
		if(!this.isNull){
			sql = sql + " NOT NULL";
		}
		return sql;
	}
}
