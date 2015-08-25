package kirkModels.objects;

public class IntegerField extends Field<Integer> {

	public final String SQL_TYPE;
	public final Class<Integer> JAVA_TYPE = Integer.class;
	public Integer maxVal;
	public boolean autoIncrement;
	
	public IntegerField(String label, boolean isNull, Integer defaultValue, boolean unique, boolean autoIncrement, Integer maxValue) {
		<IntegerField>super(label, isNull, unique);
		
		this.autoIncrement = autoIncrement;
		this.value = defaultValue;
		this.maxVal = maxValue;
		if(this.maxVal != null){
			if(this.maxVal < 255){
				this.SQL_TYPE = "TINYINT";
			}
			else if(this.maxVal < 65535){
				this.SQL_TYPE = "SMALLINT";
			}
			else if(this.maxVal < 16777215){
				this.SQL_TYPE = "MEDIUMINT";
			}
			else{
				this.SQL_TYPE = "INT";
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
		return sql;
	}
}
