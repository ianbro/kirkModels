package kirkModels;

import java.util.ArrayList;
import java.util.HashMap;

import kirkModels.fields.SavableField;
import kirkModels.orm.DbManager;

public class QuerySet {

	public static DbManager objects = new DbManager();
	
	public HashMap<String, SavableField> dbFields;
	
	public void delete() {
		
	}
	
	public <T extends DbObject> T toObject(Class<T> type){
		return null;
	}
}
