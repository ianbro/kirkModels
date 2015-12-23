package kirkModels.orm.backend.sync;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import iansLibrary.data.databases.MetaDatabase;
import kirkModels.DbObject;
import kirkModels.fields.ManyToManyField;
import kirkModels.orm.backend.scripts.MySqlScript;
import kirkModels.orm.backend.scripts.PSqlScript;
import kirkModels.orm.backend.scripts.Script;

public class DbSync {

	Script script;
	String dbName;
	Connection dbConnection;
	
	public DbSync(MetaDatabase _database){
		this.dbConnection = _database.dbConnection;
		this.dbName = _database.schema;
		switch (_database.language) {
		case "MySQL":
			this.script = new MySqlScript(this.dbName);
			break;
			
		case "postgreSQL":
			this.script = new PSqlScript(this.dbName);
			break;
		default:
			break;
		}
		System.out.println(this.script);
	}
	
	public <T extends DbObject> void migrateModel(Class<T> model) throws SQLException{
		T testInstance = null;
		try {
			testInstance = model.newInstance();
		} catch (InstantiationException | IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		this.migrateFromInstance(testInstance);
		
		for (String fieldName : testInstance.savableFields) {
			Field field = null;
			
			try {
				field = testInstance.getClass().getField(fieldName);
			} catch (NoSuchFieldException | SecurityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			if(ManyToManyField.class.isAssignableFrom(field.getType())){

				DbObject manyToManyField = null;
				
				try {
					manyToManyField = (DbObject) field.get(testInstance);
				} catch (IllegalArgumentException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				this.migrateFromInstance(manyToManyField);
			}
		}
	}
	
	public void migrateFromInstance(DbObject testInstance) throws SQLException{
		String tableString = this.script.getTableString(testInstance);
		Statement statement = null;
		statement = this.dbConnection.createStatement();
		System.out.println(tableString);
		statement.execute(tableString);
	}
}
