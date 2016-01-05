package kirkModels.orm;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;

import kirkModels.DbObject;
import kirkModels.config.Settings;
import kirkModels.fields.ManyToManyField;
import kirkModels.fields.SavableField;
import kirkModels.queries.SelectQuery;
import kirkModels.queries.scripts.WhereCondition;

public class QuerySet<T extends DbObject> implements Savable<T>, Iterable<T>{
	
	public ArrayList<T> storage;
	public ResultSet results;
	private Class<T> type;
	public ArrayList<WhereCondition> conditions;
	String tableName;
	
	public QuerySet(ResultSet results, ArrayList<WhereCondition> conditions){
		this.results = results;
		this.conditions = conditions;
		this.setTableName();
		this.updateStorage();
	}
	
	public QuerySet(Class<T> type){
		this.type = type;
		this.tableName = this.type.getName().replace(".", "_");
		
		SelectQuery query = new SelectQuery(this.tableName, new ArrayList<WhereCondition>());
		try {
			query.run();
			this.results = query.results;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		this.conditions = new ArrayList<WhereCondition>();
		this.updateStorage();
	}
	
	public QuerySet(Class<T> type, ArrayList<WhereCondition> conditions){
		this.type = type;
		this.tableName = this.type.getName().replace(".", "_");
		
		SelectQuery query = new SelectQuery(this.tableName, conditions);
		try {
			query.run();
			this.results = query.results;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		this.conditions = conditions;
		this.updateStorage();
	}
	
	public QuerySet(Class<T> type, String tableName, ArrayList<WhereCondition> conditions){
		this.type = type;
		this.tableName = tableName;

		SelectQuery query = new SelectQuery(this.tableName, conditions);
		try {
			query.run();
			this.results = query.results;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		this.conditions = conditions;
		this.updateStorage();
	}
	
	private void updateStorage(){
		this.storage = new ArrayList<T>();
		if(this.results == null){

			SelectQuery query = new SelectQuery(this.tableName, this.conditions);
			try {
				query.run();
				this.results = query.results;
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}

		try {
			
			while (this.results.next()) {
				
				int index = 0;
				
				try {
					index = this.results.getRow();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				if (index > 0) {
					T newInstance = this.getObjectFromResults(index);
					
					newInstance.initializeManyToManyFields();
					
					this.storage.add(newInstance);
				}
			}
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void getM2MObject(ManyToManyField object, ResultSet results) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, SQLException, NoSuchMethodException, SecurityException, NoSuchFieldException{
		String col1Name = results.getMetaData().getColumnName(1);
		String col2Name = results.getMetaData().getColumnName(2);
		String col3Name = results.getMetaData().getColumnName(3);
		
		object.getField(col1Name).set(results.getInt(col1Name));
		object.getField(col1Name).label = col1Name;
		
		object.getField(col2Name).set(results.getInt(col2Name));
		object.getField(col2Name).label = col2Name;
		
		object.getField(col3Name).set(results.getInt(col3Name));
		object.getField(col3Name).label = col3Name;
		
		object.tableName = results.getMetaData().getTableName(1);
	}
	
	private void getDbObject(T object, ResultSet results) throws NoSuchFieldException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SQLException{
		for (int i = 0; i < object.savableFields.size(); i++) {
			String fieldNameTemp = object.savableFields.get(i);
			String fieldName = object.getField(fieldNameTemp).label;
			Class<?> fieldType = object.getClass().getField(fieldNameTemp).getType();
			if (fieldType.isAssignableFrom(ManyToManyField.class)) {
				Class<?>[] cArg = new Class[1];
				cArg[0] = Object.class;
				fieldType.getMethod("getObjects", cArg).invoke(object, new Object[0]);
			}
			else{
				Class<?>[] cArg = new Class[1];
				cArg[0] = Object.class;
				
				SavableField field = (SavableField) object.getClass().getField(fieldNameTemp).get(object);
				
				Method getMethod = fieldType.getMethod("set", cArg);
				Object fieldVal = this.results.getObject(fieldName);
				
				if(fieldVal != null){
					getMethod.invoke(field, fieldVal);
				}
			}
		}
	}
	
	private T getObjectFromResults(int index){
		T object = null;
		try {
			if (this.cursorToRow(index)) {
				object = type.newInstance();
				if (ManyToManyField.class.isAssignableFrom(object.getClass())) {
					this.getM2MObject((ManyToManyField) object, this.results);
				}else {
					this.getDbObject(object, results);
				}
			}
			else {
				// throw an error because the results don't have a value at this index.
				throw new Error("There is no object at the index: " + index);
			}
		} catch (SQLException | InstantiationException | IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchFieldException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return object;
	}
	
	public void setTableName(){
		try {
			this.tableName = this.results.getMetaData().getTableName(1);
			this.type = (Class<T>) Settings.syncedModels.get(this.tableName.replace('_', '.'));
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public ArrayList<WhereCondition> combineConditions(ArrayList<WhereCondition> newConditions){
		ArrayList<WhereCondition> tempNewConditions = new ArrayList<WhereCondition>();
		
		for(WhereCondition condition : this.conditions){
			if (!tempNewConditions.contains(condition)) {
				tempNewConditions.add(condition);
			}
		}
		
		for (WhereCondition condition : newConditions) {
			if (!tempNewConditions.contains(condition)) {
				tempNewConditions.add(condition);
			}
		}
		
		return tempNewConditions;
	}
	
	public boolean cursorToRow(int i) throws SQLException{
		this.results.first();
		this.results.previous();
		boolean found = false;
		int count = 0;
		while(!found){
			if(this.results.next()){
				count ++;
			}
			else{
				break;
			}
			if(count == i){
				found = true;
			}
		}
		if(count == i){
			found = true;
		}
//		while(this.results.previous()){}
		return found;
	}

	public String toString(){
		String str = "<";
		
		for(int i = 0; i < this.count(); i ++){
			if(i > 0){
				str = str + ", ";
			}
			
			@SuppressWarnings("unchecked")
			T reference = null;
			reference = this.getRow(i);
			
			str = str + reference.toString();
		}
		
		str = str + ">";
		return str;
	}

	@Override
	public Iterator<T> iterator() {
		// TODO Auto-generated method stub
		return this.storage.iterator();
	}
	
	public int indexOf(T other){
		for(int i = 0; i < this.storage.size(); i ++){
			T object = this.storage.get(i);
			if(object.id.val() == other.id.val()){
				return i;
			}
		}
		return -1;
	}
	
	public String getTableName(){
		return this.tableName;
	}
	
	
	
	

	
	
	public T getById(int id){
		for (T instance : this.storage) {
			
			if (instance.id.val() == id) {
				return instance;
			}
			
		}
		
		// if loop finishes and no instance is returned, throw an error cause no instance has this id.
		try {
			throw new Exception(this.type.getSimpleName() + " with id of " + id + " does not exist.");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	public T getRow(int i){
		T instance = null;
		try {
			instance = this.storage.get(i);
		} catch (IndexOutOfBoundsException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return instance;
	}
	
	@Override
	public int count(){
		return this.storage.size();
	}
	
	@Override
	public T create(ArrayList<WhereCondition> conditions) {
		conditions = this.combineConditions(conditions);
		
		T newInstance = null;
		try {
			newInstance = this.type.newInstance();
		} catch (InstantiationException | IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		for (WhereCondition condition : conditions) {
			Object value = condition.value;
			newInstance.getField(condition.fieldName).set(value);
		}
		
		newInstance.save();
		
		this.storage.add(newInstance);
		
		return newInstance;
	}

	@Override
	public T get(ArrayList<WhereCondition> conditions) throws SQLException {
		conditions = this.combineConditions(conditions);
		
		QuerySet<T> set = this.filter(conditions);
		if(set.count() == 1){
			return set.getRow(0);
		} else if (set.count() == 0) {
			
			try {
				throw new Exception("Found no results of " + this.type + " instance for kwargs: "
									+ conditions);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			return null;
		} else {
			
			try {
				throw new Exception("Found several results of " + this.type + " instance for kwargs: "
									+ Arrays.toString(conditions.toArray()));
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			return null;
		}
	}

	@Override
	public QuerySet<T> getOrCreate(ArrayList<WhereCondition> conditions) throws SQLException {
		conditions = this.combineConditions(conditions);
		
		QuerySet<T> results = this.filter(conditions);
		
		if (results.count() > 0) {
			return results;
		}
		else {
			T newInstance = this.create(conditions);
			QuerySet<T> querySet = this.filter(conditions);
			return querySet;
		}
	}
	
	public void addOperators(ArrayList<WhereCondition> conditions, int operator){
		
		for (WhereCondition c : conditions) {
			c.type = operator;
		}
	}

	@Override
	public QuerySet<T> filter(ArrayList<WhereCondition> conditions) {
		// This makes it so that the queryset will be an empty one which I can then just add stuff to it's storage.
		// plan on creating a constructor that makes a blank queryset instead.
		ArrayList<WhereCondition> nonPassableConditions = new ArrayList<WhereCondition>();
		WhereCondition c = new WhereCondition("id", WhereCondition.EQUALS, 0);
		
		nonPassableConditions.add(c);
		
		ArrayList<WhereCondition> tempConditions = this.combineConditions(conditions);
		
		QuerySet<T> newQuerySet = new QuerySet<T>(this.type, this.tableName, nonPassableConditions);
		
		for (T instance : this.storage) {
			if(instance.meetsConditions(tempConditions)){
				newQuerySet.storage.add(instance);
			}
		}
		
		newQuerySet.conditions = tempConditions;
		
		return newQuerySet;
	}

	@Override
	public QuerySet<T> all() {
		return this;
	}

	@Override
	public void delete(ArrayList<WhereCondition> conditions) throws Exception {
		conditions = this.combineConditions(conditions);
		QuerySet<T> results = this.filter(conditions);
		if (results.count() < 1) {
			throw new Exception(this.type + " object with kwargs: " + conditions + " does not exist.");
		}
		else {
			for (T instance : results) {
				int index = this.indexOf(instance);
				instance.delete();
				this.storage.remove(index);
			}
		}
	}
}
