package kirkModels.orm;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

import kirkModels.DbObject;
import kirkModels.queries.scripts.WhereCondition;

public interface Savable <T extends DbObject> {
	
	public QuerySet<T> all();
	
	public T create(ArrayList<WhereCondition> conditions);
	
	public T get(ArrayList<WhereCondition> conditions) throws SQLException;
	
	public QuerySet<T> getOrCreate(ArrayList<WhereCondition> conditions) throws SQLException;
	
	public QuerySet<T> filter(ArrayList<WhereCondition> conditions) throws SQLException;
	
	public void delete(ArrayList<WhereCondition> conditions) throws Exception;
	
	public int count();
}
