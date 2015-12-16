package kirkModels.orm;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

import com.sun.javafx.collections.ImmutableObservableList;

import kirkModels.DbObject;
import kirkModels.config.Settings;
import kirkModels.fields.ManyToManyField;
import kirkModels.fields.SavableField;

public class QuerySet<T extends DbObject> implements Savable{
	
	private ArrayList<DbObject> storage;
	private ResultSet results;
	private Class<T> tableName;
	private HashMap<String, Object> kwargs;
	
	public QuerySet(ResultSet results, HashMap<String, Object> kwargs){
		this.results = results;
		this.kwargs = kwargs;
		this.setTableName();
	}
	
	public QuerySet(Class<T> tableName){
		this.tableName = tableName;
		this.results = Settings.database.dbHandler.selectFrom(tableName, new HashMap<String, Object>()).results;
		this.kwargs = new HashMap<String, Object>();
	}
	
	public QuerySet(HashMap<String, Object> kwargs){
		QuerySet results = Settings.database.dbHandler.selectFrom(tableName, kwargs);
		this.results = results.results;
		this.setTableName();
		this.kwargs = kwargs;
	}
	
	public T getObjectFromResults(int index){
		T object = null;
		try {
			if (this.cursorToRow(index)) {
				object = tableName.newInstance();
				for (int i = 0; i < object.savableFields.size(); i++) {
					String fieldName = object.savableFields.get(i);
					Class[] cArg = new Class[1];
					cArg[0] = Object.class;
					object.getClass().getField(fieldName).getType().getMethod("set", cArg).invoke(object, this.results.getObject(fieldName));
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
	
	public <T extends DbObject> T getById(Class<T> type, int id) throws SQLException{
		this.cursorToRow(1);
		while(this.results.next()){
			if(this.results.getInt("id") == id){
				T newInstance = null;
				try {
					newInstance = type.newInstance();
				} catch (InstantiationException | IllegalAccessException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				int i = 1;
				for(Field field : type.getFields()){
					if(!field.getType().isInstance(ManyToManyField.class) && !SavableField.class.isAssignableFrom(field.getType())){
						continue;
					}
					else{
						try {
							Class[] cArg = new Class[1];
							cArg[0] = Object.class;
							newInstance.getClass().getField(field.getName()).getType().getMethod("set", cArg).invoke(newInstance, this.results.getObject(field.getName()));
						} catch (NoSuchMethodException | SecurityException | NoSuchFieldException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
				return newInstance;
			}
		}
		try {
			throw new Exception(type.getSimpleName() + " with id of " + id + " does not exist.");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	public DbObject getRow(int i){
		DbObject instance = null;
		try {
			this.cursorToRow(i);
			instance = this.getById(tableName, this.results.getInt("id"));
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return instance;
	}
	
	public boolean cursorToRow(int i) throws SQLException{
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
		while(this.results.previous()){}
		return found;
	}
	
	
	
	
	
	
	

	@Override
	public int count(){
		int count = 0;
		try {
			this.cursorToRow(1);
			while(this.results.next()){
				count ++;
			}
			this.cursorToRow(1);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return count;
	}
	
	@Override
	public void create(HashMap<String, Object> kwargs) {
		kwargs = this.combineKwargs(kwargs);
		Settings.database.dbHandler.insertInto(kwargs);
	}

	@Override
	public DbObject get(HashMap<String, Object> kwargs) throws SQLException {
		kwargs = this.combineKwargs(kwargs);
		QuerySet results = Settings.database.dbHandler.selectFrom(tableName, kwargs);
		return this.getById(tableName, results.results.getInt("id"));
	}

	@Override
	public QuerySet getOrCreate(HashMap<String, Object> kwargs) throws SQLException {
		kwargs = this.combineKwargs(kwargs);
		QuerySet results = Settings.database.dbHandler.selectFrom(tableName, kwargs);
		if (results.count() == 0) {
			this.create(kwargs);
			return null;
		}
		else{
			return this.filter(kwargs);
		}
	}

	@Override
	public QuerySet filter(HashMap<String, Object> kwargs) {
		kwargs = this.combineKwargs(kwargs);
		QuerySet results = Settings.database.dbHandler.selectFrom(tableName, kwargs);
		return results;
	}

	@Override
	public QuerySet all() {
		QuerySet results = Settings.database.dbHandler.selectFrom(tableName, this.kwargs);
		return null;
	}

	@Override
	public void delete(HashMap<String, Object> kwargs) {
		kwargs = this.combineKwargs(kwargs);
		QuerySet results = this.filter(kwargs);
		try {
			while(results.results.next()){
				Integer id = results.results.getInt("id");
				Settings.database.dbHandler.deleteFrom(this.get(new HashMap<String, Object>(){{put("id", id);}}));
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public String toString(){
		String str = "<";
		
		for(int i = 0; i < this.count(); i ++){
			if(i > 0){
				str = str + ", ";
			}
			
			@SuppressWarnings("unchecked")
			DbObject reference = null;
			reference = this.getRow(i);
			
			str = str + reference.toString();
		}
		
		str = str + ">";
		return str;
	}
}
