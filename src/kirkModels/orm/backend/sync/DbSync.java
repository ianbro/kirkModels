package kirkModels.orm.backend.sync;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import kirkModels.DbObject;
import kirkModels.orm.backend.scripts.PSqlScript;
import kirkModels.orm.backend.scripts.Script;

public class DbSync {

	PSqlScript psqlScript;
	String dbName;
	Connection dbConnection;
	
	public DbSync(Connection _dbConnection, String _dbName){
		this.dbConnection = _dbConnection;
		this.dbName = _dbName;
		this.psqlScript = new PSqlScript(this.dbName);
	}
	
	public <T extends DbObject> void migrateModel(Class<T> model) throws SQLException{
		T testInstance = null;
		try {
			testInstance = model.newInstance();
		} catch (InstantiationException | IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		String tableString = this.psqlScript.getTableString(testInstance);
		Statement statement = null;
		statement = this.dbConnection.createStatement();
		
		statement.execute(tableString);
	}
}
