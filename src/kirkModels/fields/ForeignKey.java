package kirkModels.fields;

import java.sql.SQLException;
import java.util.HashMap;

import kirkModels.DbObject;

public class ForeignKey<T extends DbObject> extends IntegerField {
	
	public T referencedInstant;
	public Class<T> referenceClass;
	
	public String onDelete;

	public ForeignKey(String label, Class<T> reference, boolean isNull, Integer defaultValue, boolean unique, String onDelete) {
		super(label, isNull, defaultValue, unique, null);
		// TODO Auto-generated constructor stub
		this.referenceClass = reference;
		if(defaultValue != null){
			this.set(defaultValue);
		}
		this.onDelete = onDelete;
	}
	
	public ForeignKey() {
		super("", true, null, false, null);
	}
	
	@Override
	/**
	 * set the id value for this. this should not be an actual DbObject, but the id of an existing one.
	 */
	public void set(Object value){
		super.set(value);
		int val = this.value;
		try {
			this.referencedInstant = (T) DbObject.objects.get(new HashMap<String, Object>(){{
				put("id", val);
			}});
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * set the DbObject that this field will reference
	 * @param value
	 */
	public void setObject(T value){
		// T in this case is not an int, but the object instance that is being referenced
		if (value.id.val() == null) {
			try {
				throw new Exception(value.getClass() + " object 'value' does not exist in the database.");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {
			int valueID = (Integer)value.id.val();
			this.value = valueID;
			this.referencedInstant = value;
		}
	}
	
	public T getRef(){
		return this.referencedInstant;
	}

	@Override
	public String MySqlString() {
		String sql = super.MySqlString();
		sql = sql + " REFERENCES " + this.referenceClass.getName().replace(".", "_") + "(id)\n\t\tON UPDATE CASCADE ON DELETE " + this.onDelete;
		return sql;
	}
	
	public String PSqlString(){
		String sql = super.PSqlString();
		sql = sql + " REFERENCES " + this.referenceClass.getName().replace(".", "_") + "(id)\n\t\tON UPDATE CASCADE ON DELETE " + this.onDelete;
		return sql;
	}
	
	@Override
	public String toString(){
		T ref_value = null;
		ref_value = this.getRef();
		if(ref_value != null){
			return ref_value.toString();
		} else {
			return "NONE";
		}
	}

	//FOREIGN KEY (" + this.referenceClass.getSimpleName().toLowerCase() + "_id, " + this.label + ")\n\t\t
}
