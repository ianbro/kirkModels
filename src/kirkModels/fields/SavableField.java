package kirkModels.fields;

import java.lang.reflect.Field;
import java.util.HashMap;

public abstract class SavableField <T> {
	
	//database info
	public String label;
	public Boolean isNull;
	public Boolean unique;
	public T defaultValue;
	
	//used by java only
	public Class<T> JAVA_TYPE;
	protected T value;
	public String MYSQL_TYPE;
	public String PSQL_TYPE;
	
	public SavableField(String _label, Boolean _isNull, Boolean _unique, T _defaultValue){
		this.isNull = _isNull;
		this.label = _label;
		this.unique = _unique;
		this.defaultValue = _defaultValue;
	}
	
	/**
	 * Used to print the value of this field as a {@link String}. This will return the value as this sqlFields JAVA_TYPE type. for instance, if called on an IntegerField, it will return an Integer, not an IntegerField. a CharField will return a String and so on. Relationship sqlFields will return 1 or more instantiated objects that math the reference. It will then return that value as a String.
	 * <p>
	 * @return String - The value of this field as a String
	 */
	public String toString(){
		if (this.value == null || this.value.getClass().isPrimitive()) {
			return this.label + ", " + this.getClass().getName() + ": " + String.valueOf(this.value);
		} else {
			return this.label + ", " + this.getClass().getName() + ": " + this.value.toString();
		}
	}
	
	/**
	 * Used almost exactly like the "=" operator. This will set <b>val</b> to the callable value of this field.
	 * <br>
	 * @param value - The value to set this field to
	 */
	@SuppressWarnings("unchecked")
	public void set(Object value){
		this.value = (T) value;
	}
	
	/**
	 * Used to return the value of this field. This will return the value as this sqlFields JAVA_TYPE type. for instance, if called on an IntegerField, it will return an Integer, not an IntegerField. a CharField will return a String and so on. Relationship sqlFields will return 1 or more instantiated objects that math the reference.
	 * @return T - the value that is stored in this field
	 */
	public T val(){
		return this.value;
	}
	
	/**
	 * Used when saving this field to the database. This method converts the field into the MySQL equivalent of this field. For instance, an IntegerField will be returned as "INT [ <b>properties</b> ]"
	 * @return String - the MySQL equivalent of this field.
	 */
	public abstract String MySqlString();
	
	/**
	 * Used when saving this field to the database. This method converts the field into the PostgreSQL equivalent of this field. For instance, an IntegerField will be returned as "INT [ <b>properties</b> ]"
	 * @return String - the PostgreSQL equivalent of this field.
	 */
	public abstract String PSqlString();
	
	public abstract String getMySqlDefinition();
	
	public abstract String getPsqlDefinition();

}
