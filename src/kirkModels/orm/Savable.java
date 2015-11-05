package kirkModels.orm;

import java.util.HashMap;

import kirkModels.DbObject;

public interface Savable {
	
	public QuerySet all();
	
	public void create(HashMap<String, Object> kwargs);
	
	public DbObject get(HashMap<String, Object> kwargs);
	
	public QuerySet getOrCreate(HashMap<String, Object> kwargs);
	
	public QuerySet filter(HashMap<String, Object> kwargs);
	
	public void delete(HashMap<String, Object> kwargs);
}
