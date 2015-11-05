package kirkModels.fields;

public class BooleanField extends SavableField<Boolean> {
	
	public Boolean defaultValue;

	public BooleanField(String label, Boolean defaultValue) {
		super(label, false, false);
		
		if(defaultValue != null){
			this.value = defaultValue;
			this.defaultValue = defaultValue;
		} else{
			try {
				throw new Exception("defaultValue for BooleanField labeled " + label + " must not be null.");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		this.MYSQL_TYPE = "BIT(1)";
		this.PSQL_TYPE = "BOOLEAN";
		this.JAVA_TYPE = Boolean.class;
	}

	@Override
	public String MySqlString() {
		// TODO Auto-generated method stub
		String sql = "'" + this.label + "' " + this.MYSQL_TYPE;
		if(this.defaultValue == false){
			sql = sql + " DEFAULT " + 0;
		}
		else{
			sql = sql + " DEFAULT " + 1;
		}
		return sql + ";";
	}

	@Override
	public String PSqlString() {
		// TODO Auto-generated method stub
		String sql = "'" + this.label + "' " + this.PSQL_TYPE;
		sql = sql + " DEFAULT " + this.defaultValue.toString().toUpperCase() + ";";
		return sql;
	}}
