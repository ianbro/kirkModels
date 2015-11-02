package kirkModels.fields;

import java.util.HashMap;

public abstract class SavableField <T> {
	
	public static HashMap<String, Class<?>> FIELD_MAP = new HashMap<String, Class<?>>()
	{{
		/* name of class (with package extension) maps to specific SavableField's
		  ex:
		  put("java.lang.Integer", IntegerField.class);
		 */
		
		put("java.lang.Integer", IntegerField.class);
	}};
	
	//database info
	public String label;
	public boolean isNull;
	public boolean unique;
	
	//used by java only
	public Class<T> JAVA_TYPE;
	protected T value;
	protected String SQLType;
	
	public SavableField(String label, boolean isNull, boolean unique){
		this.isNull = isNull;
		this.label = label;
		this.unique = unique;
	}
	
	/**
	 * Used to print the value of this field as a {@link String}. This will return the value as this sqlFields JAVA_TYPE type. for instance, if called on an IntegerField, it will return an Integer, not an IntegerField. a CharField will return a String and so on. Relationship sqlFields will return 1 or more instantiated objects that math the reference. It will then return that value as a String.
	 * <p>
	 * @return String - The value of this field as a String
	 */
	public String toString(){
		return this.value.toString();
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
	public T get(){
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

}
