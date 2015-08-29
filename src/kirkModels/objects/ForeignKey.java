package kirkModels.objects;

import java.util.HashMap;

public final class ForeignKey<T extends Model> extends IntegerField {

	protected final Class<T> tableRef;
	protected String onDelete;
	
	public ForeignKey(Class<T> reference, String label, boolean isNull, T defaultValue, boolean unique, String onDelete) {
		super(label, isNull, (Integer) defaultValue.getField("id"), unique, false, 16777215);
		this.tableRef = reference;
		this.onDelete = onDelete;
	}

	@Override
	public String sqlString() {
		String sql = super.sqlString();
		sql = sql + "%" + "FOREIGN KEY (" + this.tableRef.getSimpleName() + "_id, " + this.label + ")\n  REFERENCES " + this.tableRef.getSimpleName() + "(id)\n  ON UPDATE CASCADE ON DELETE " + this.onDelete;
		return null;
	}
	
	public void setObject(T value){
		// T in this case is not an int, but the object instance that is being referenced
		int valueID = (Integer)value.getField("id");
		this.value = valueID;
	}
	
	@SuppressWarnings({ "unchecked", "serial" })
	public T getRef(){
		Integer ref_id = this.value;
		HashMap<String, Object> conditions = new HashMap<String, Object>(){{
			put("id", ref_id);
		}};
		T ref = (T) T.get(conditions);
		return ref;
	}
	
	@Override
	public String toString(){
		T ref_value = this.getRef();
		return ref_value.toString();
	}

}