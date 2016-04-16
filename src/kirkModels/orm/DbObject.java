package kirkModels.orm;

import java.lang.reflect.Field;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import javax.swing.JOptionPane;

import iansLibrary.data.databases.MetaTable;
import iansLibrary.data.databases.MetaTableColumn;
import kirkModels.config.Settings;
import kirkModels.fields.CharField;
import kirkModels.fields.ForeignKey;
import kirkModels.fields.IntegerField;
import kirkModels.fields.ManyToManyField;
import kirkModels.fields.SavableField;
import kirkModels.orm.backend.sync.queries.AddColumn;
import kirkModels.orm.backend.sync.queries.AddForeignKey;
import kirkModels.orm.backend.sync.queries.ColumnDefinitionChange;
import kirkModels.orm.backend.sync.queries.ColumnOperation;
import kirkModels.orm.backend.sync.queries.DropField;
import kirkModels.orm.backend.sync.queries.RenameField;
import kirkModels.queries.DeleteQuery;
import kirkModels.queries.InsertQuery;
import kirkModels.queries.Query;
import kirkModels.queries.UpdateQuery;
import kirkModels.queries.scripts.WhereCondition;
import kirkModels.tests.Person;
import kirkModels.utils.exceptions.ObjectNotFoundException;

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
			Settings.setObjectsForModel(this.getClass());
		}
		
		this.initializeManyToManyFields();
	}
	
	public static int getNewId(DbObject instance){
		int newId = DbObject.getObjectsForGenericType(instance.getClass()).count() + 1;
		boolean idWorks = false;
		
		while (!idWorks){
			newId ++;
			
			WhereCondition c = new WhereCondition("id", WhereCondition.EQUALS, newId);
			
			try {
				DbObject o = DbObject.getObjectsForGenericType(instance.getClass()).get(new ArrayList<WhereCondition>(){{
					add(c);
				}});
			} catch (ObjectNotFoundException e) {
				// TODO Auto-generated catch block
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
	
	public Object getFieldGeneric(String name) {
		Object field = null;
		try {
			field = this.getClass().getField(name).get(this);
		} catch (IllegalArgumentException | IllegalAccessException | NoSuchFieldException | SecurityException e) {
			// TODO Auto-generated catch block
			System.out.println(name + " field not found for class: " + this.getClass());
		}
		return field;
	}
	
	public SavableField getField(String name){
		SavableField field = null;
		try {
			field = (SavableField) this.getClass().getField(name).get(this);
		} catch (IllegalArgumentException | IllegalAccessException | NoSuchFieldException | SecurityException e) {
			// TODO Auto-generated catch block
			System.out.println(name + " field not found for class: " + this.getClass());
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
	
//	public ArrayList<ColumnOperation> getOperationDifferences(MetaTable _tableDef) {
//		if (this.savableFields.size() > _tableDef.columns.size()) {
//			// some fields have been added to this class.
//		} else if (this.savableFields.size() < _tableDef.columns.size()) {
//			//some fields have been removed to this class.
//		} else {
//			/*
//			 * no fields have been added
//			 * but we need to make sure that the fields have not been altered.
//			 */
//		}
//	}
	
	/**
	 * If fields have been added, get the difference between database state and the current
	 * class definition
	 * @return
	 */
	public ArrayList<ColumnOperation> getOperationDifferencesFieldsAdded(MetaTable _tableDef) {
		ArrayList<ColumnOperation> operations = new ArrayList<ColumnOperation>();
		ArrayList<String> fieldsDealtWith = new ArrayList<String>(); //field names that have already been handeled
		
		for (MetaTableColumn column : _tableDef.columns) {
			Object field = this.getFieldGeneric(column.getColumnName());
			if (field == null) {
				//this field has been dropped
				/*
				 * Query user and ask if they renamed the field to something else
				 * or deleted it. if they renamed it, ask for what field they renamed it to.
				 */
				String[] operation = this.queryUserForRenameOrDrop(column.getColumnName());
				
				if (operation[0].equals("drop")) {
					operations.add(new DropField(column.getColumnName(), DropField.CASCADE));
				} else {
					operations.add(new RenameField(column.getColumnName(), operation[1]));
				}
				fieldsDealtWith.add(operation[1]);
			} else {
				System.out.println(column.getColumnName() + ": " + column.getDataType());
				if (field instanceof SavableField) {
					System.out.println("savableField: " + ((SavableField) field).label + " " + ((SavableField) field).getPsqlDefinition());
					System.out.println(((SavableField) field).equals(column));
					if (!((SavableField) field).equals(column)) {
						operations.add(new ColumnDefinitionChange(column.getColumnName(), (SavableField) field));
					}
				} else if (field instanceof ManyToManyField) {
					//the field has been changed to a many to many field
					/*
					 * to change to a m2m field:
					 * 1. drop the column in the table
					 * 2. migrate the m2m field as a new table. this will be done elsewhere.
					 */
					operations.add(new DropField(column.getColumnName(), DropField.CASCADE));
				}
			}
			fieldsDealtWith.add(column.getColumnName());
		}
		
		for (String fieldName : this.savableFields) {
			if (!fieldsDealtWith.contains(fieldName)) {
				//this field needs to be added
				operations.add(new AddColumn(this.getField(fieldName)));
				fieldsDealtWith.add(fieldName);
			}
		}
		
		return operations;
	}
	
	/**
	 * determines whether a field was dropped or renamed
	 * @param _columnOrigionalName
	 * @return
	 */
	public String[] queryUserForRenameOrDrop(String _columnOrigionalName) {
		String option = JOptionPane.showInputDialog("We have detected a change in the schema for the class: " + this.getClass().getSimpleName() + " at column: " + _columnOrigionalName + ". did you drop this field or rename it? type \"drop\" or \"rename\" respectively.");
		if (option.equalsIgnoreCase("drop")) {
			return new String[]{"drop"};
		} else if (option.equalsIgnoreCase("rename")) {
			//ask user what they renamed it to
			String newName = this.getNewNameOfField();
			return new String[]{"rename", newName};
		} else {
			return this.queryUserForRenameOrDrop(_columnOrigionalName, true);
		}
	}
	
	/**
	 * determines whether a field was dropped or renamed.
	 * this assumes the user did not enter either drop or rename previously.
	 * @param _columnOrigionalName
	 * @param error
	 * @return
	 */
	public String[] queryUserForRenameOrDrop(String _columnOrigionalName, boolean error) {
		String option = JOptionPane.showInputDialog("Sorry, that option is not a valid option. please type either \"drop\" or \"rename\".");
		if (option.equalsIgnoreCase("drop")) {
			return new String[]{"drop"};
		} else if (option.equalsIgnoreCase("rename")) {
			//ask user what they renamed it to
			String newName = this.getNewNameOfField();
			return new String[]{"rename", newName};
		} else {
			return this.queryUserForRenameOrDrop(_columnOrigionalName, true);
		}
	}

	/**
	 * ask user what they renamed a field to
	 * @return
	 */
	public String getNewNameOfField(){
		String newName = JOptionPane.showInputDialog("Please enter the new name of the field. NOTE: this is case sensitive.");
		if (this.getFieldGeneric(newName) == null) {
			return this.getNewNameOfField(true);
		} else {
			return newName;
		}
	}
	
	/**
	 * ask user what they renamed a field to
	 * This assumes that they previously entered an invalid name of a field.
	 * @return
	 */
	private String getNewNameOfField(boolean error){
		String newName = JOptionPane.showInputDialog("Sorry, that field does not exist in your class definition for class: " + this.getClass().getSimpleName() + ". Please enter the new name of the field. NOTE: this is case sensitive.");
		if (this.getFieldGeneric(newName) == null) {
			return this.getNewNameOfField(true);
		} else {
			return newName;
		}
	}
}
