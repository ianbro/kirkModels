package kirkModels.orm;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

import kirkModels.orm.queries.scripts.WhereCondition;
import kirkModels.utils.exceptions.ObjectAlreadyExistsException;
import kirkModels.utils.exceptions.ObjectNotFoundException;

public interface Savable <T extends Model> {
	
	public QuerySet<T> all();
	
	public T create(ArrayList<WhereCondition> conditions) throws ObjectAlreadyExistsException;
	
	public T get(ArrayList<WhereCondition> conditions) throws ObjectNotFoundException;
	
	public Entry<T, Boolean> getOrCreate(ArrayList<WhereCondition> conditions);
	
	public QuerySet<T> filter(ArrayList<WhereCondition> conditions);
	
	public void delete(ArrayList<WhereCondition> conditions) throws ObjectNotFoundException;
	
	public int count();
}
