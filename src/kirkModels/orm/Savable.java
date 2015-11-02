package kirkModels.orm;

import java.util.HashMap;

import kirkModels.QuerySet;

public interface Savable {
	
	public QuerySet[] all();

	public void save();
	
	public void create();
	
	public QuerySet get(HashMap<String, Object> kwargs);
	
	public QuerySet[] getOrCreate(HashMap<String, Object> kwargs);
	
	public QuerySet[] filter(HashMap<String, Object> kwargs);
	
	public void delete(HashMap<String, Object> kwargs);
}
