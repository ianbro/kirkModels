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
	public ArrayList<Migration> migrations = new ArrayList<Migration>();
	
	public DbSync(MetaDatabase _database){
		this.dbConnection = _database.dbConnection;
		this.dbName = _database.schema;
	}
	
	public DbSync(MetaDatabase _database, ArrayList<String> migrationFolder){
		this.dbConnection = _database.dbConnection;
		this.dbName = _database.schema;
		this.migrationFolder = migrationFolder;
	}
	
	
}
