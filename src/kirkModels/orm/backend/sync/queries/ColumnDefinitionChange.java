package kirkModels.orm.backend.sync.queries;

import java.util.HashMap;

import kirkModels.config.Settings;
import kirkModels.fields.SavableField;

public class ColumnDefinitionChange extends ColumnOperation {
	
	public static final String TYPE = "CHANGE_TYPE";
	public static final String DEFAULT_CHANGE = "CHANGE_DEFAULT";
	public static final String DEFAULT_DROP = "DROP_DEFAULT";
	public static final String NULL_NO = "SET_NOT_NULL";
	public static final String NULL_YES = "SET_NULLABLE";
	public static final String INCREASE_SIZE = "INCREASE_SIZE";
	
	private static final HashMap<String, String> PSQL_OPERATIONS = new HashMap<String, String>(){{
		put("CHANGE_TYPE", "ALTER COLUMN %s SET DATA TYPE %s");
		put("CHANGE_DEFAULT", "ALTER COLUMN %s SET DEFAULT %s");
		put("DROP_DEFAULT", "ALTER COLUMN %s DROP DEFAULT");
		put("SET_NOT_NULL", "ALTER COLUMN %s SET NOT NULL");
		put("SET_NULLABLE", "ALTER COLUMN %s DROP NOT NULL");
		put("INCREASE_SIZE", "ALTER COLUMN %s SET DATA TYPE %s");
	}};
	
	private static final HashMap<String, String> MYSQL_OPERATIONS = new HashMap<String, String>(){{
		put("CHANGE_TYPE", "MODIFY COLUMN %s %s");
		put("CHANGE_DEFAULT", "ALTER COLUMN %s SET DEFAULT %s");
		put("DROP_DEFAULT", "ALTER COLUMN %s DROP DEFAULT");
		put("SET_NOT_NULL", "MODIFY COLUMN %s %s");
		put("SET_NULLABLE", "MODIFY COLUMN %s %s");
		put("INCREASE_SIZE", "MODIFY COLUMN %s %s");
	}};
	
	private SavableField newDef;
	private String type;
	
	private String mySqlNewDefinition;
	private String pSqlNewDefinition;

	public ColumnDefinitionChange(String _name, SavableField _newDefinition, String _type) {
		// TODO Auto-generated constructor stub
		super(_name);
		
		this.newDef = _newDefinition;
		this.type = _type;
		
		this.mySqlNewDefinition = _newDefinition.getMySqlDefinition();
		this.pSqlNewDefinition = _newDefinition.getPsqlDefinition();
		
	}
	
	public String getMySqlString() {
		String template;
		switch (this.type) {
		case TYPE:
			template = MYSQL_OPERATIONS.get(TYPE);
			return String.format(template, this.fieldName, this.newDef.getMySqlDefinition());
		case DEFAULT_CHANGE:
			template = MYSQL_OPERATIONS.get(DEFAULT_CHANGE);
			return String.format(template, this.fieldName, this.newDef.defaultValue);
		case DEFAULT_DROP:
			template = MYSQL_OPERATIONS.get(DEFAULT_DROP);
			return String.format(template, this.fieldName);
		case NULL_NO:
			template = MYSQL_OPERATIONS.get(NULL_NO);
			return String.format(template, this.fieldName, this.newDef.getMySqlDefinition());
		case NULL_YES:
			template = MYSQL_OPERATIONS.get(NULL_YES);
			return String.format(template, this.fieldName, this.newDef.getMySqlDefinition());
		case INCREASE_SIZE:
			template = MYSQL_OPERATIONS.get(INCREASE_SIZE);
			return String.format(template, this.fieldName, this.newDef.getMySqlDefinition());
		default:
			return "";
		}
	}
	
	public String getPsqlString() {
		String template;
		switch (this.type) {
		case TYPE:
			template = PSQL_OPERATIONS.get(TYPE);
			return String.format(template, this.fieldName, this.newDef.PSQL_TYPE);
		case DEFAULT_CHANGE:
			Object defVal = this.newDef.defaultValue;
			if (this.newDef.defaultValue instanceof String) {
				defVal = "'" + defVal + "'";
			}
			template = PSQL_OPERATIONS.get(DEFAULT_CHANGE);
			return String.format(template, this.fieldName, defVal);
		case DEFAULT_DROP:
			template = PSQL_OPERATIONS.get(DEFAULT_DROP);
			return String.format(template, this.fieldName);
		case NULL_NO:
			template = PSQL_OPERATIONS.get(NULL_NO);
			return String.format(template, this.fieldName);
		case NULL_YES:
			template = PSQL_OPERATIONS.get(NULL_YES);
			return String.format(template, this.fieldName);
		case INCREASE_SIZE:
			template = PSQL_OPERATIONS.get(INCREASE_SIZE);
			return String.format(template, this.fieldName, this.newDef.PSQL_TYPE);
		default:
			return "";
		}
	}

}
