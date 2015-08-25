package kirkModels.objects;

public class VarChar extends Field<String> {

	public final String SQL_TYPE;
	public final Class<Integer> JAVA_TYPE = Integer.class;
	public int maxLength;
	
	public VarChar(String label, boolean isNull, String defaultValue, boolean unique, int maxLength){
		<IntegerField>super(label, isNull, unique);
		
		this.value = defaultValue;
		this.maxLength = maxLength;
		this.SQL_TYPE = "VARCHAR(" + this.maxLength + ")";
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
