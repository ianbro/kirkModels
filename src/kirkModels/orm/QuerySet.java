package kirkModels.orm;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

import kirkModels.DbObject;
import kirkModels.fields.ManyToManyField;
import kirkModels.fields.SavableField;

public class QuerySet{
	
	public ResultSet results;
	
	public QuerySet(ResultSet results){
		this.results = results;
	}
	
	public DbObject get(int i) throws SQLException{
		if(this.toRow(i)){
			int id = this.results.getInt("id");
			return DbObject.objects.get(new HashMap<String, Object>(){{put("id", id);}});
		}
		this.toRow(1);
		return null;
	}
	
	public <T extends DbObject> T getById(Class<T> type, int id) throws SQLException{
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
	
	public boolean toRow(int i) throws SQLException{
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

	public int size(){
		int count = 0;
		try {
			while(this.results.next()){
				count ++;
			}
			this.toRow(1);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return count;
	}

	public String toString(){
		String str = "<";
		
		for(int i = 0; i < this.size(); i ++){
			if(i > 0){
				str = str + ", ";
			}
			
			@SuppressWarnings("unchecked")
			DbObject reference = null;
			try {
				reference = this.get(i);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			str = str + reference.toString();
		}
		
		str = str + ">";
		return str;
	}
}
