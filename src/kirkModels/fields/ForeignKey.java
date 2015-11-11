package kirkModels.fields;

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
	
	@Override
	public void set(Object value){
		super.set(value);
		int val = this.value;
		this.referencedInstant = (T) DbObject.objects.get(new HashMap<String, Object>(){{
			put("id", val);
		}});
	}
	
	public void setObject(T value){
		// T in this case is not an int, but the object instance that is being referenced
		int valueID = (Integer)value.id.val();
		this.value = valueID;
	}
	
	public T getRef(){
		return this.referencedInstant;
	}

	@Override
	public String MySqlString() {
		String sql = super.MySqlString();
		sql = sql + " REFERENCES " + this.referenceClass.getSimpleName().toLowerCase() + "(id)\n\t\tON UPDATE CASCADE ON DELETE " + this.onDelete;
		return sql;
	}
	
	public String PSqlString(){
		String sql = super.PSqlString();
		sql = sql + " REFERENCES " + this.referenceClass.getSimpleName().toLowerCase() + "(id)\n\t\tON UPDATE CASCADE ON DELETE " + this.onDelete;
		return sql;
	}
	
	@Override
	public String toString(){
		T ref_value = null;
		ref_value = this.getRef();
		return ref_value.toString();
	}

	//FOREIGN KEY (" + this.referenceClass.getSimpleName().toLowerCase() + "_id, " + this.label + ")\n\t\t
}
