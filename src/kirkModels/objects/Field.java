package kirkModels.objects;

public abstract class Field<T> {

	protected String label;
	public boolean isNull;
	protected T value;
	public boolean unique;
	
	public <T>Field(String label, boolean isNull, boolean unique){
		this.isNull = isNull;
		this.label = label;
		this.unique = unique;
	}
	
	public String toString(){
		return this.value.toString();
	}
	
	public void set(T val){
		this.value = val;
	}
	
	public T get(){
		return this.value;
	}
	
	public abstract String sqlString();
}
