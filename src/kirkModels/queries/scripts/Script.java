package kirkModels.queries.scripts;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;

import kirkModels.DbObject;

public abstract class Script {
	
	public String dbName;

	public Script(String _dbName) {
		this.dbName = _dbName;
	}
	
	public abstract String getTableString(DbObject testInstance);
	
	public abstract String getCheckExistsString(DbObject instance);
	
	public abstract Boolean exists(ResultSet results);
	
	public abstract String getFieldStrings(DbObject instance);
	
	public abstract String getIntType(Integer maxVal);
}
