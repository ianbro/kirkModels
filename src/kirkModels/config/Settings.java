package kirkModels.config;

import java.io.File;
import java.io.FileNotFoundException;
import java.sql.SQLException;
import java.util.HashMap;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

import iansLibrary.data.databases.MetaDatabase;
import kirkModels.fields.ManyToManyField;
import kirkModels.orm.Model;
import kirkModels.orm.QuerySet;
import kirkModels.tests.Person;
import kirkModels.utils.Utilities;

public abstract class Settings {

	/**
	 * The global database object that contains meta data for the project. All SQL queries are run using the Meta Data in this object. The project also uses this object to run migrations and generate migration files.
	 */
	public static MetaDatabase database;
	
	public static HashMap<String, Class<? extends Model>> syncedModels = new HashMap<String, Class<? extends Model>>();
	
	public static String ROOT_FOLDER;
	
	public static String BINARY_ROOT;
	
	public static String MIGRATION_FOLDER;
	
	public static void syncSettings(File configFile) throws FileNotFoundException, ParseException, SQLException{
		JSONObject settingsJson = Utilities.json(configFile);
		
		//set database
		try{
			database = new MetaDatabase((String) settingsJson.get("defaultDb"), configFile);
		} catch (SQLException e){
			throw new SQLException("The config file with path \"" + configFile.getAbsolutePath() + "\" does not contain a valid database connection.");
		}
		
		//set syncedModels
		JSONArray tempSyncedModels = (JSONArray) settingsJson.get("synced_models");
		for (int i = 0; i < tempSyncedModels.size(); i++) {
			String model = (String) tempSyncedModels.get(i);
			try {
				syncedModels.put(model.replace(".", "_").toLowerCase(), (Class<? extends Model>) Class.forName(model));
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		//set migration folder
		MIGRATION_FOLDER = (String) settingsJson.get("migrations_folder");
		
		//set Root Folder for project development
		ROOT_FOLDER = (String) settingsJson.get("project_development_root_parent");
		
		//set root folder for binary files
		BINARY_ROOT = (String) settingsJson.get("binary_root_parent");
	}
	
	public static void setObjectsForModels(){
		for (Class<? extends Model> type : syncedModels.values()) {
			QuerySet<? extends Model> objects = new QuerySet(type);
			
			try {
				type.getField("objects").set(null, objects);
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (NoSuchFieldException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SecurityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		updateStoragesForManyToManyFields();
	}
	
	public static void updateStoragesForManyToManyFields(){
		for (Class<? extends Model> type : syncedModels.values()) {
			try {
				for(Model object : (QuerySet<? extends Model>) type.getField("objects").get(null)){
					
					for (String fieldName : object.manyToManyFields) {
						Object field = null;
						try {
							field = object.getClass().getField(fieldName).get(object);
						} catch (IllegalArgumentException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (IllegalAccessException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (NoSuchFieldException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (SecurityException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						
						ManyToManyField temp_field = (ManyToManyField) field;
						temp_field.getObjects();
					}
					
				}
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (NoSuchFieldException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SecurityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public static void setObjectsForModel(Class<?> type){
		QuerySet<? extends Model> objects = new QuerySet(type);
		
		try {
			type.getField("objects").set(null, objects);
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchFieldException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		updateStorageForManyToManyFields(type);
	}
	
	public static void updateStorageForManyToManyFields(Class<?> type){
		try {
			for(Model object : (QuerySet<? extends Model>) type.getField("objects").get(null)){
				
				for (String fieldName : object.manyToManyFields) {
					Object field = null;
					try {
						field = object.getClass().getField(fieldName).get(object);
					} catch (IllegalArgumentException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IllegalAccessException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (NoSuchFieldException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (SecurityException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
					ManyToManyField temp_field = (ManyToManyField) field;
					temp_field.getObjects();
				}
				
			}
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchFieldException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
