package kirkModels.orm;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import kirkModels.DbObject;
import kirkModels.config.Settings;
import kirkModels.fields.ManyToManyField;
import kirkModels.fields.SavableField;

public class QuerySet<T extends DbObject> implements Savable<T>, Iterable<T>{
	
	private ArrayList<T> storage;
	public ResultSet results;
	private Class<T> tableName;
	private HashMap<String, Object> kwargs;
	
	public QuerySet(ResultSet results, HashMap<String, Object> kwargs){
		this.results = results;
		this.kwargs = kwargs;
		this.setTableName();
		this.updateStorage();
	}
	
	public QuerySet(Class<T> tableName){
		this.tableName = tableName;
		this.results = Settings.database.dbHandler.selectFrom(tableName, new HashMap<String, Object>());
		this.kwargs = new HashMap<String, Object>();
		this.updateStorage();
	}
	
	public QuerySet(HashMap<String, Object> kwargs){
		this.results = Settings.database.dbHandler.selectFrom(tableName, kwargs);
		this.setTableName();
		this.kwargs = kwargs;
		this.updateStorage();
	}
	
	private void updateStorage(){
		this.storage = new ArrayList<T>();
		this.results = Settings.database.dbHandler.selectFrom(this.tableName, this.kwargs);
//		try {
//			this.results.last();
//		} catch (SQLException e1) {
//			// TODO Auto-generated catch block
//			System.out.println("error");
//			e1.printStackTrace();
//		}
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
//					System.out.println(this.results.getRow());
					T newInstance = this.getObjectFromResults(index);
//					System.out.println(this.results.getRow());
					this.storage.add(newInstance);
				}
			}
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private T getObjectFromResults(int index){
		T object = null;
		try {
			if (this.cursorToRow(index)) {
				object = tableName.newInstance();
				for (int i = 0; i < object.savableFields.size(); i++) {
					String fieldName = object.savableFields.get(i);
					Class<?> fieldType = object.getClass().getField(fieldName).getType();
					if (fieldType.isAssignableFrom(ManyToManyField.class)) {
						Class<?>[] cArg = new Class[1];
						cArg[0] = Object.class;
						fieldType.getMethod("getObjects", cArg).invoke(object, new Object[0]);
					}
					else{
						Class<?>[] cArg = new Class[1];
						cArg[0] = Object.class;
						
						SavableField field = (SavableField) object.getClass().getField(fieldName).get(object);
						
						Method getMethod = fieldType.getMethod("set", cArg);
						Object fieldVal = this.results.getObject(fieldName);
						
						if(fieldVal != null){
							getMethod.invoke(field, fieldVal);
						}
					}
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
			String tableName = this.results.getMetaData().getTableName(1).replace('_', '.');
			try {
				this.tableName = (Class<T>) Class.forName(tableName);
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public HashMap<String, Object> combineKwargs(HashMap<String, Object> newKwargs){
		for(String key : this.kwargs.keySet()){
			newKwargs.put(key, this.kwargs.get(key));
		}
		return newKwargs;
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
		return this.storage.indexOf(other);
	}
	
	
	
	

	
	
	public T getById(int id){
		for (T instance : this.storage) {
			
			if (instance.id.val() == id) {
				return instance;
			}
			
		}
		
		// if loop finishes and no instance is returned, throw an error cause no instance has this id.
		try {
			throw new Exception(this.tableName.getSimpleName() + " with id of " + id + " does not exist.");
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
	public T create(HashMap<String, Object> kwargs) {
		kwargs = this.combineKwargs(kwargs);
		
		T newInstance = null;
		try {
			newInstance = this.tableName.newInstance();
		} catch (InstantiationException | IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		newInstance.initializeManyToManyFields();
		
		for (String fieldName : kwargs.keySet()) {
			Object value = kwargs.get(fieldName);
			newInstance.getField(fieldName).set(value);
		}
		
		newInstance.save();
		
		this.storage.add(newInstance);
		
		return newInstance;
	}

	@Override
	public T get(HashMap<String, Object> kwargs) throws SQLException {
		kwargs = this.combineKwargs(kwargs);
		QuerySet<T> set = this.filter(kwargs);
		if(set.count() == 1){
			return set.getRow(0);
		}
		else {
			
			try {
				throw new Exception("Found several results of " + this.tableName + " instance for kwargs: " + kwargs);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			return null;
		}
	}

	@Override
	public QuerySet<T> getOrCreate(HashMap<String, Object> kwargs) throws SQLException {
		kwargs = this.combineKwargs(kwargs);
		QuerySet<T> results = this.filter(kwargs);
		if (results.count() > 0) {
			return results;
		}
		else {
			T newInstance = this.create(kwargs);
			QuerySet<T> querySet = new QuerySet<T>(kwargs).filter(kwargs);
			return querySet;
		}
	}

	@Override
	public QuerySet<T> filter(HashMap<String, Object> kwargs) {
		kwargs = this.combineKwargs(kwargs);
		QuerySet<T> newQuerySet = new QuerySet<>(kwargs);
		return newQuerySet;
	}

	@Override
	public QuerySet<T> all() {
		return this;
	}

	@Override
	public void delete(HashMap<String, Object> kwargs) throws Exception {
		kwargs = this.combineKwargs(kwargs);
		QuerySet<T> results = this.filter(kwargs);
		if (results.count() < 1) {
			throw new Exception(this.tableName + " object with kwargs: " + kwargs + " does not exist.");
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
