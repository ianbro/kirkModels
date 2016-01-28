package kirkModels.fields;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

import kirkModels.orm.DbObject;
import kirkModels.orm.QuerySet;
import kirkModels.queries.scripts.WhereCondition;
import kirkModels.utils.exceptions.ObjectNotFoundException;

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
	
	public T getRef() throws ObjectNotFoundException{
		
		if (this.value != null && this.referencedInstant != null) {
			return this.referencedInstant;
		} else if (this.value != null && this.referencedInstant == null) {
			ArrayList<WhereCondition> conditions = new ArrayList<WhereCondition>();
			WhereCondition id = new WhereCondition("id", WhereCondition.EQUALS, value);
			conditions.add(id);
			
			return DbObject.getObjectsForGenericType(this.referenceClass).get(conditions);
		} else if (this.value == null) {
			return null;
		}
		
		return null;
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
		
		try {
			ref_value = this.getRef();
		} catch (ObjectNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		if(ref_value != null){
			return ref_value.toString();
		} else {
			return "NONE";
		}
	}

	//FOREIGN KEY (" + this.referenceClass.getSimpleName().toLowerCase() + "_id, " + this.label + ")\n\t\t
}
