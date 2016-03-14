package kirkModels.orm.backend.sync;

import java.io.File;
import java.io.FileNotFoundException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
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
import iansLibrary.utilities.JSONClassMapping;
import kirkModels.fields.ManyToManyField;
import kirkModels.orm.DbObject;
import kirkModels.orm.backend.sync.queries.CreateTable;
import kirkModels.queries.Query;

public class DbSync {

	String dbName;
	Connection dbConnection;
	
	/**
	 * contains string paths to all the migration files.
	 */
	public ArrayList<String> migrationFolder;
	public ArrayList<JSONObject> migrations = new ArrayList<JSONObject>();
	public ArrayList<Query> operations = new ArrayList<Query>();
	
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
	
	public void readMigrations() throws FileNotFoundException, ParseException, ClassNotFoundException, NoSuchMethodException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
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
		for (int i = 0; i < this.migrations.size(); i++) {
			this.addNextMigration(i);
		}
		System.out.println(this.operations.get(0));
	}
	
	private void addNextMigration(int index) throws ClassNotFoundException, NoSuchMethodException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException{
		JSONObject migration = this.migrations.get(index);
		for (Object jsonQuery : (JSONArray) migration.get("operations")) {
			System.out.println("json: " + jsonQuery);
			this.operations.add((Query) JSONClassMapping.jsonObjectToObject((JSONObject) jsonQuery));
		}
	}
}
