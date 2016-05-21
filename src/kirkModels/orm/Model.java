package kirkModels.orm;

import java.lang.reflect.Field;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import javax.swing.JOptionPane;

import iansLibrary.data.databases.MetaForeignKeyConstraint;
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
import kirkModels.orm.backend.sync.queries.DropConstraint;
import kirkModels.orm.backend.sync.queries.DropField;
import kirkModels.orm.backend.sync.queries.Operation;
import kirkModels.orm.backend.sync.queries.RenameField;
import kirkModels.orm.queries.DeleteQuery;
import kirkModels.orm.queries.InsertQuery;
import kirkModels.orm.queries.Query;
import kirkModels.orm.queries.UpdateQuery;
import kirkModels.orm.queries.scripts.WhereCondition;
import kirkModels.tests.Person;
import kirkModels.utils.exceptions.ObjectNotFoundException;

/**
 * <p>Class to be extended in order to reflect a class to the database and run queries on for that class.</p>
 * <p>
 * 	This class is fairly simple to set up. When extending this class, the child class must have the following ellements:<br>
 * 	- {@code public static QuerySet<*classType*> objects;}<br>
 *  - any fields to be reflected onto the database. These fields must have the public keyword applied to them.<br><br>
 * 
 * 	For example, assume a class Person exists. Person extends Model because the developer wants to map the class as a table
 * 		onto a database. The developer wants the class to contain 2 field: name(string) and age(integer) the class definition
 * 		must at least have the following definition:<br><br><br>
 * 
 * 		class Person extends Model {<br><br>
 * 			
 * 			public static QuerySet<Person> objects;<br><br>
 * 			
 * 			public CharField name = new CharField("name", *parameters*);<br>
 * 			public IntegerField age = new IntegerField("age", *parameters*);<br><br>
 * 
 * 		}
 * </p>
 * Note: you must always have a constructor that takes no parameters.<br><br>
 * @author kirkp1ia
 *
 */
public abstract class Model {

	/**
	 * <p>Query Set object that represents the instances of the class that extends Model.
	 * This essentially a list of objects in the database that are contained in the table represented by this class.</p>
	 */
	@SuppressWarnings("rawtypes")
	public static QuerySet objects;
	
	/**
	 * Every Model must have a field called id that will 
	 * be used as the primary key for the object.
	 */
	public IntegerField id = new IntegerField("id", false, null, true, null);
	
	/**
	 * A list of all fields (excluding ManyToManyField's. 
	 * These are stored in {@code manyToManyFields}.) that 
	 * will be mapped onto the database.
	 */
	public ArrayList<String> savableFields = new ArrayList<String>();
	
	/**
	 * A list of ManyToManyFields that will be represented 
	 * as seperate tables handling the relationships between 
	 * the host objects (this object) and the related objects.
	 */
	public ArrayList<String> manyToManyFields = new ArrayList<String>();
	
	/**
	 * Name of the table which represents this class in the database.<br>
	 * By default, table names are the the full name of the class 
	 * including packages with the '.'s replaced with '_'s and every 
	 * character turned lowercase. so a class foo.bar.Person would 
	 * have a table name of "foo_bar_person".
	 */
	public String tableName;
	
	/**
	 * Constructs the model object. This will add any Savable Fields 
	 * to the list {@code savableFields} and any Many To Many Fields 
	 * to the list {@code manyToManyFields}.
	 */
	public Model(){
		
		// Set default tablename for this model.
		this.tableName = this.getClass().getName().replace(".", "_").toLowerCase();
		
		/*
		 * Adding fields of class SavableField to this.savableFields
		 * Adding fields of class Many To Many Field to this.manyToManyFields
		 */
		for(Field field : this.getClass().getFields()){
			if(!ManyToManyField.class.isAssignableFrom(field.getType()) && !SavableField.class.isAssignableFrom(field.getType())){
				// Then this field is neither a Many To Many Field or a SavableField.
				// So don't do anything with this field. It has no effect on the ORM.
				continue;
			} else if (ManyToManyField.class.isAssignableFrom(field.getType())) {
				// Then this field is a ManyToManyField
				this.manyToManyFields.add(field.getName());
			} else{
				// Then this field is a SavableField
				this.savableFields.add(field.getName());
			}
		}
	}
	
	/**
	 * Delete this object from the database.
	 */
	public void delete() {
		/**
		 * If this instance is actually saved to the database, delete it
		 */
		if(((QuerySet) Model.getObjectsForGenericType(this.getClass())).exists(this)){
			/*
			 * Then the object exists
			 */
			
			// Create the delete query for this item
			DeleteQuery query = new DeleteQuery(this.tableName, new ArrayList<WhereCondition>(){{
				add(new WhereCondition("id", WhereCondition.EQUALS, id.val()));
			}});
			
			try {
				query.run();
				Settings.setObjectsForModel(this.getClass()); // Refresh the storage for this class to reflect the deletion.
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * Saves this object to the database. If it already exists, it simply updates it. 
	 * This method sets the id automatically if the object doesn't exist.
	 */
	public void save() {

		if(this.id.val() == 0 || ! ((QuerySet) Model.getObjectsForGenericType(this.getClass())).exists(this)){
			/*
			 *  Then the object does not exist already in the database so we will add it.
			 */
			
			int newId = Model.getNewId(this);
			this.id.set(newId);
			
			// Prepare the query to add this object to the database.
			InsertQuery query = new InsertQuery(this);
			try {
				query.run();
				Settings.setObjectsForModel(this.getClass()); // Refresh the storage for this class to reflect the new instance.
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		} else {
			/*
			 * Then the object exists. So it will simply be updated in the database.
			 */
			
			UpdateQuery query = new UpdateQuery(this);
			try {
				query.run();
				Settings.setObjectsForModel(this.getClass()); // Refresh the storage for this calss to reflect the change in this object.
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		
		this.initializeManyToManyFields();
	}
	
	public static int getNewId(Model instance){
		int newId = Model.getObjectsForGenericType(instance.getClass()).count() + 1;
		boolean idWorks = false;
		
		while (!idWorks){
			newId ++;
			
			WhereCondition c = new WhereCondition("id", WhereCondition.EQUALS, newId);
			
			try {
				Model o = Model.getObjectsForGenericType(instance.getClass()).get(new ArrayList<WhereCondition>(){{
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
	
	public static <T extends Model> QuerySet<T> getObjectsForGenericType(Class<T> type){
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
	
	/**
	 * get the difference between database state and the current
	 * class definition
	 * @return
	 */
	public ArrayList<Operation> getOperationDifferences(MetaTable _tableDef) {
		ArrayList<Operation> operations = new ArrayList<Operation>();
		ArrayList<String> fieldsDealtWith = new ArrayList<String>(); //field names that have already been handeled
		
		for (MetaTableColumn column : _tableDef.columns) {
			Object field = this.getFieldGeneric(column.getColumnName());
			if (field == null) {
				if (_tableDef.getForeignKeyConstraint(column.getColumnName()) != null) {
					operations.add(new DropConstraint(_tableDef.getForeignKeyConstraint(column.getColumnName()).getFkConstraintName()));
				}
				
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
					fieldsDealtWith.add(operation[1]);
				}
			} else {
				if (field instanceof SavableField) {
					//see if the field once had a foreignkey constraint on it. if so, add a drop constraint for that.
					if (_tableDef.getForeignKeyConstraint(((SavableField) field).label) != null && !(field instanceof ForeignKey)) {
						operations.add(new DropConstraint(_tableDef.getForeignKeyConstraint(((SavableField) field).label).getFkConstraintName()));
					}
					
					if (!((SavableField) field).equals(column)) {
						/*
						 * refer to the method below: getOperationsForField
						 */
						if (field instanceof ForeignKey) {
							if (_tableDef.getForeignKeyConstraint(((SavableField) field).label) == null) {
								operations.add(new AddForeignKey((ForeignKey) field));
							}
						}
						ArrayList<ColumnOperation> operationsForCurrentField = this.getOperationsForField((SavableField) field, column);
						operations.addAll(operationsForCurrentField);
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
		
		/*
		 * Now go add fields that have been added but are not yet reflected in the database.
		 */
		for (String fieldName : this.savableFields) {
			if (!fieldsDealtWith.contains(fieldName)) {
				//this field needs to be added
				operations.add(new AddColumn(this.getField(fieldName)));
				if (this.getField(fieldName) instanceof ForeignKey) {
					operations.add(new AddForeignKey((ForeignKey) this.getField(fieldName)));
				}
				fieldsDealtWith.add(fieldName);
			}
		}
		
		return operations;
	}
	
	public ArrayList<ColumnOperation> getOperationsForField(SavableField _newField, MetaTableColumn _column) {
		ArrayList<ColumnOperation> operations = new ArrayList<ColumnOperation>();
		
		HashMap<String, Object> diffs = _newField.getDifferences(_column);
		for (String operationDesc : diffs.keySet()) {
			if (operationDesc.equals("type")) {
				operations.add(new ColumnDefinitionChange(_newField.label, _newField, ColumnDefinitionChange.TYPE));
			} else if (operationDesc.equals("nullable")) {
				if (_newField.isNull) {
					operations.add(new ColumnDefinitionChange(_newField.label, _newField, ColumnDefinitionChange.NULL_YES));
				} else {
					operations.add(new ColumnDefinitionChange(_newField.label, _newField, ColumnDefinitionChange.NULL_NO));
				}
			} else if (operationDesc.equals("default")) {
				if (_newField.defaultValue == null) {
					operations.add(new ColumnDefinitionChange(_newField.label, _newField, ColumnDefinitionChange.DEFAULT_DROP));
				} else {
					operations.add(new ColumnDefinitionChange(_newField.label, _newField, ColumnDefinitionChange.DEFAULT_CHANGE));
				}
			} else if (operationDesc.equals("size")) {
				if (((CharField) _newField).maxLength > _column.getColumnSize()) {
					operations.add(new ColumnDefinitionChange(_newField.label, _newField, ColumnDefinitionChange.INCREASE_SIZE));
				} else {
					operations.add(new DropField(_newField.label, DropField.CASCADE));
					operations.add(new AddColumn(_newField));
				}
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
		String option = JOptionPane.showInputDialog("We have detected a change in the dbName for the class: " + this.getClass().getSimpleName() + " at column: " + _columnOrigionalName + ". did you drop this field or rename it? type \"drop\" or \"rename\" respectively.");
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
