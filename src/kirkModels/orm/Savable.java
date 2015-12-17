package kirkModels.orm;

import java.sql.SQLException;
import java.util.HashMap;

import kirkModels.DbObject;

public interface Savable <T extends DbObject> {
	
	public QuerySet<T> all();
	
	public T create(HashMap<String, Object> kwargs);
	
	public T get(HashMap<String, Object> kwargs) throws SQLException;
	
	public QuerySet<T> getOrCreate(HashMap<String, Object> kwargs) throws SQLException;
	
	public QuerySet<T> filter(HashMap<String, Object> kwargs) throws SQLException;
	
	public void delete(HashMap<String, Object> kwargs) throws Exception;
	
	public int count();
}
