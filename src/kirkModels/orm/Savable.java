package kirkModels.orm;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

import kirkModels.queries.scripts.WhereCondition;
import kirkModels.utils.exceptions.ObjectAlreadyExistsException;
import kirkModels.utils.exceptions.ObjectNotFoundException;

public interface Savable <T extends DbObject> {
	
	public QuerySet<T> all();
	
	public T create(ArrayList<WhereCondition> conditions) throws ObjectAlreadyExistsException;
	
	public T get(ArrayList<WhereCondition> conditions) throws ObjectNotFoundException;
	
	public QuerySet<T> getOrCreate(ArrayList<WhereCondition> conditions);
	
	public QuerySet<T> filter(ArrayList<WhereCondition> conditions);
	
	public void delete(ArrayList<WhereCondition> conditions) throws ObjectNotFoundException;
	
	public int count();
}
