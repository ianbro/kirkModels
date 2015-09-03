package kirkModels.db.scripts;

import java.util.HashMap;

import kirkModels.objects.Model;
import kirkModels.objects.SQLField;

public abstract class SqlScript {

	public abstract String getTableString(Model model);
	
	public abstract String getDeleteString(Model instance);
	
	public abstract String getCheckExistsString(Model instance);
	
	public abstract String getSaveNewInstanceString(Model instance);
	
	public abstract String getUpdateString(Model instance);
	
	public abstract String getSelectString(Class model, HashMap<String, Object> conditions);
	
	public abstract String getCountString(Class type);
}
