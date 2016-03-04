package kirkModels.orm.backend.sync;

import java.io.File;
import java.io.FileNotFoundException;
import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Scanner;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import iansLibrary.data.databases.MetaDatabase;
import kirkModels.fields.ManyToManyField;
import kirkModels.orm.DbObject;
import kirkModels.orm.backend.sync.queries.CreateTable;

public class DbSync {

	String dbName;
	Connection dbConnection;
	
	/**
	 * contains string paths to all the migration files.
	 */
	public ArrayList<String> migrationFolder;
	public ArrayList<JSONObject> migrations = new ArrayList<JSONObject>();
	
	public DbSync(MetaDatabase _database){
		this.dbConnection = _database.dbConnection;
		this.dbName = _database.schema;
	}
	
	public DbSync(MetaDatabase _database, ArrayList<String> migrationFolder){
		this.dbConnection = _database.dbConnection;
		this.dbName = _database.schema;
		this.migrationFolder = migrationFolder;
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
		CreateTable query = new CreateTable(this.dbName, testInstance);
		query.run();
	}
	
	public void readMigrations() throws FileNotFoundException, ParseException {
		for (String path : this.migrationFolder) {
			File f = new File(path);
			Scanner scnr = new Scanner(f);
			scnr.useDelimiter("//Z");
			String jsonStr = scnr.next();
			JSONObject json = (JSONObject) new JSONParser().parse(jsonStr);
			this.migrations.add(json);
			scnr.close();
		}
		System.out.println(this.migrations.get(0));
	}
	
	public Object instantiateField(JSONArray json) throws ClassNotFoundException, NoSuchMethodException {
		Object field = null;
		ArrayList<Class<?>> argTypes = new ArrayList<Class<?>>();
		for (int i = 1; i < json.size(); i++) {
			Object arg = json.get(i);
			argTypes.add(arg.getClass());
		}
		field = Class.forName((String) json.get(0)).getConstructor((Class<?>[]) argTypes.toArray());
		return field;
	}
}
