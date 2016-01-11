package kirkModels.orm.backend.sync;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import iansLibrary.data.databases.MetaDatabase;
import kirkModels.fields.ManyToManyField;
import kirkModels.orm.DbObject;
import kirkModels.queries.CreateTableQuery;

public class DbSync {

	String dbName;
	Connection dbConnection;
	
	public DbSync(MetaDatabase _database){
		this.dbConnection = _database.dbConnection;
		this.dbName = _database.schema;
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
		
		for (String fieldName : testInstance.manyToManyFields) {
			Field field = null;
			
			try {
				field = testInstance.getClass().getField(fieldName);
			} catch (NoSuchFieldException | SecurityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

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
	
	public void migrateFromInstance(DbObject testInstance) throws SQLException{
		CreateTableQuery query = new CreateTableQuery(this.dbName, testInstance);
		query.run();
	}
}
