package kirkModels;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;

import kirkModels.config.Settings;
import kirkModels.fields.CharField;
import kirkModels.fields.IntegerField;
import kirkModels.fields.ManyToManyField;
import kirkModels.fields.SavableField;
import kirkModels.orm.QuerySet;

public abstract class DbObject {

	public static QuerySet objects;
	
	public IntegerField id = new IntegerField("id", false, 1, true, null);
	public ArrayList<String> savableFields = new ArrayList<String>();
	
	/**
	 * When instantiating a DbObject, if it contains a many to many field, you must call manyToManyfield.setHostId(id).
	 * This tells the field what instance is the host instance.
	 */
	public DbObject(){
		int id = 1;
		//get id to set this to
		for(Field field : this.getClass().getFields()){
			if(!field.getType().isInstance(ManyToManyField.class) && !SavableField.class.isAssignableFrom(field.getType())){
				continue;
			}
			else{
				this.savableFields.add(field.getName());
			}
		}
	}
	
	public void delete() {
		if(this.exists()){
			Settings.database.dbHandler.deleteFrom(this);
		}
	}
	
	public void save() {
		if(!this.exists()){
			Settings.database.dbHandler.insertInto(this);
		} else {
			Settings.database.dbHandler.update(this);
		}
	}
	
	public boolean exists(){
		return Settings.database.dbHandler.checkExists(this);
	}
	
	/**
	 * for each ManyToManyField in this class, call:
	 * <br>
	 * <br>
	 * thatField.setHostId(this.id);
	 * <br>
	 * <br>
	 * Essentially, this tells the field what to look for when filtering this instances queryset for that relationship.
	 * <br>
	 * <br>
	 * For example: the following class contains the following fields:
	 * <br>
	 * <br>
	 * * public CharField <b>name</b> = new CharField("name", false, null, false, 10);
	 * <br>
	 * * public IntegerField <b>age</b> = new IntegerField("age", false, null, false, 150);
	 * <br>
	 * * public ManyToManyField<Person, Person> <b>friends</b> = new ManyToManyField<>(Person.class, Person.class);
	 * <br>
	 * * public ManyToManyField<Person, Person> <b>enemies</b> = new ManyToManyField<>(Person.class, Person.class);
	 * <br>
	 * <br>
	 * So this will look like so:
	 * <br>
	 * <br>
	 * public void initializeManyToManyFields(){
	 * <br>
	 * <b>friends</b>.setHostId(this.id.val());
	 * <br>
	 * <b>enemies</b>.setHostId(this.id.val());
	 * <br>
	 * }
	 * <br>
	 * <br>
	 */
	public abstract void initializeManyToManyFields();
}
