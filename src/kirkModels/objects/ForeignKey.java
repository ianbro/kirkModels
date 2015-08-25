package kirkModels.objects;

public class ForeignKey<T extends Model> extends IntegerField {

	

	public ForeignKey(String label, boolean isNull, T defaultValue, boolean unique) {
		super(label, isNull, ((IntegerField)defaultValue.fields.get("id")).get(), unique, false, 16777215);
	}

	@Override
	public String sqlString() {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public String toString(){
		return null;
	}

}