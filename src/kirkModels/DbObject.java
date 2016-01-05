package kirkModels;

import java.lang.reflect.Field;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import kirkModels.config.Settings;
import kirkModels.fields.CharField;
import kirkModels.fields.IntegerField;
import kirkModels.fields.ManyToManyField;
import kirkModels.fields.SavableField;
import kirkModels.orm.QuerySet;
import kirkModels.queries.DeleteQuery;
import kirkModels.queries.InsertQuery;
import kirkModels.queries.UpdateQuery;
import kirkModels.queries.scripts.WhereCondition;
import kirkModels.tests.Person;

public abstract class DbObject {

	public static QuerySet objects;
	
	public IntegerField id = new IntegerField("id", false, 0, true, null);
	public ArrayList<String> savableFields = new ArrayList<String>();
	public ArrayList<String> manyToManyFields = new ArrayList<String>();
	public String tableName;
	
	/**
	 * When instantiating a DbObject, if it contains a many to many field, you must call manyToManyfield.setHostId(id).
	 * This tells the field what instance is the host instance.
	 */
	public DbObject(){
		this.tableName = this.getClass().getName().replace(".", "_").toLowerCase();
		int id = 1;
		//get id to set this to
		for(Field field : this.getClass().getFields()){
			if(!ManyToManyField.class.isAssignableFrom(field.getType()) && !SavableField.class.isAssignableFrom(field.getType())){
				continue;
			} if (ManyToManyField.class.isAssignableFrom(field.getType())) {
				this.manyToManyFields.add(field.getName());
			}
			else{
				this.savableFields.add(field.getName());
			}
		}
	}
	
	public void delete() {
		if(((QuerySet) DbObject.getObjectsForGenericType(this.getClass())).exists(this)){
			DeleteQuery query = new DeleteQuery(this.tableName, new ArrayList<WhereCondition>(){{
				add(new WhereCondition("id", WhereCondition.EQUALS, id.val()));
			}});
			
			try {
				query.run();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public void save() {

		if(this.id.val() == 0 || ! ((QuerySet) DbObject.getObjectsForGenericType(this.getClass())).exists(this)){
			int newId = DbObject.getNewId(this);
			this.id.set(newId);
			
			InsertQuery query = new InsertQuery(this);
			try {
				query.run();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			Settings.setObjectsForModels();
		} else {
			UpdateQuery query = new UpdateQuery(this);
			try {
				query.run();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		this.initializeManyToManyFields();
	}
	
	public static int getNewId(DbObject instance){
		int newId = DbObject.getObjectsForGenericType(instance.getClass()).count() + 1;
		boolean idWorks = false;
		
		while (!idWorks){
			newId ++;
			try {
				WhereCondition c = new WhereCondition("id", WhereCondition.EQUALS, newId);
				
				DbObject o = DbObject.getObjectsForGenericType(instance.getClass()).get(new ArrayList<WhereCondition>(){{
					add(c);
				}});
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (Exception e) {
				// not found so id is unique
				idWorks = true;
			}
		}
		
		return newId;
	}
	
	public boolean meetsConditions(ArrayList<WhereCondition> conditions){
		for (WhereCondition c : conditions) {
			if(!this.meetsSpecificCondition(c)){
				return false;
			}
		}
		return true;
	}
	
	public boolean meetsSpecificCondition(WhereCondition c){
		switch (c.type) {
		case WhereCondition.EQUALS:
			
			SavableField field = this.getField(c.fieldName);
			
			if(!field.val().equals(c.value)){
				return false;
			}
			break;
			
		case WhereCondition.CONTAINED_IN:
			boolean contained = false;
			ArrayList<Object> values = (ArrayList<Object>) c.value;
			
			for (Object value : values) {
				if(this.getField(c.fieldName).val().equals(value)){
					contained = true;
				}
			}
			
			if (!contained) {
				return false;
			}
			break;

		default:
			return false;
		}
		
		return true;
	}
	
	public SavableField getField(String name){
		SavableField field = null;
		try {
			field = (SavableField) this.getClass().getField(name).get(this);
		} catch (IllegalArgumentException | IllegalAccessException | NoSuchFieldException | SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return field;
	}
	
	public QuerySet getM2MSet(String fieldName){
		ManyToManyField field = null;
		try {
			field = (ManyToManyField) (this.getClass().getField(fieldName).get(this));
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchFieldException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return field.all();
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
	public void initializeManyToManyFields(){
		for (String fieldName : this.manyToManyFields) {
			Object field = null;
			try {
				field = this.getClass().getField(fieldName).get(this);
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (NoSuchFieldException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SecurityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			ManyToManyField temp_field = (ManyToManyField) field;
			temp_field.setHostId(this.id.val());
		}
	}
	
	public static <T extends DbObject> QuerySet<T> getObjectsForGenericType(Class<T> type){
		Field objectsTemp = null;
		try {
			objectsTemp = type.getField("objects");
		} catch (NoSuchFieldException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		try {
			return (QuerySet<T>) objectsTemp.get(null);
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
}
