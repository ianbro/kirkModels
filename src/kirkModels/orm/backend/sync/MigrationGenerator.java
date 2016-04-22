package kirkModels.orm.backend.sync;

import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;

import javax.swing.text.NumberFormatter;

import iansLibrary.data.databases.MetaTable;
import iansLibrary.utilities.JSONFormat;
import iansLibrary.utilities.ObjectParser;
import kirkModels.config.Settings;
import kirkModels.fields.ManyToManyField;
import kirkModels.orm.DbObject;
import kirkModels.orm.backend.sync.queries.AlterTable;
import kirkModels.orm.backend.sync.queries.ColumnDefinitionChange;
import kirkModels.orm.backend.sync.queries.ColumnOperation;
import kirkModels.orm.backend.sync.queries.CreateTable;
import kirkModels.orm.backend.sync.queries.Operation;
import kirkModels.queries.Query;
import kirkModels.tests.Person;

public final class MigrationGenerator {
	
	public String rootMigrationFolderPath;
	public PrintWriter[] migrationWriters;
	public Migration[] migrations;
	public Class<? extends DbObject>[] types;
	
	public MigrationGenerator(String _migrationFolder) {
		this.rootMigrationFolderPath = _migrationFolder;
	}
	
	/**
	 * loops through each syncedModel and creates a migration file for it.
	 * @throws IOException
	 */
	public void generateMigrationFiles() throws IOException {
		/*
		 * reset migrationWriters and migrations
		 */
		this.migrationWriters = new PrintWriter[Settings.syncedModels.keySet().size()];
		this.migrations = new Migration[Settings.syncedModels.keySet().size()];
		this.types = new Class[Settings.syncedModels.keySet().size()];
		
		int i = 0; //keeping track of index in the array attributes containing printwriters and migrations
		
		for (String key : Settings.syncedModels.keySet()) {
			this.genterateSingleMigrationFile(key, i);
		}
	}
	
	/**
	 * Creates the migration file for a given class type.
	 * @param _key - the key which will be sent to {@code Settings.syncedModels} to retrieve the class type to migrate.
	 * @param _indexInStorage - the index in which this migration file will be stored in migration array attributes.
	 * @throws IOException
	 */
	public void genterateSingleMigrationFile(String _key, int _indexInStorage) throws IOException {
		/*
		 * The model class that we will generate migrations for.
		 */
		Class type = Settings.syncedModels.get(_key);
		
		/*
		 * path to the model class's migration folder within the root migration folder
		 */
		String pathToMigrationFolderSpecific = this.rootMigrationFolderPath + type.getName().replace(".", "_") + "-migrations/";
		
		/*
		 * instantiated migration folder
		 */
		File migrationFile = this.getMigrationFile(_key, pathToMigrationFolderSpecific);
		
		try {
			PrintWriter pw = new PrintWriter(migrationFile);
			this.migrationWriters[_indexInStorage] = pw;
			pw.close();
			this.types[_indexInStorage] = type;
			/*
			 * From here, generate a migration for type and add it to this.migrations at index i.
			 * later, we will loop through this.migrations and call those migrations.
			 */
//			this.migrations[i] = this.makeMigration(type);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			this.migrationWriters[_indexInStorage] = null;
		}
	}
	
	/**
	 * namespaces a file with the format "####". It should have 4 digits padded by zeros.
	 * @param num
	 * @return
	 */
	public String getFileName(Integer num) {
		String fileBeginning = String.format("%04d", num);
		
		fileBeginning = fileBeginning;
		
		return fileBeginning;
	}
	
	/**
	 * creates a new migration file in the migration folder at {@code _migrationFolder}. if folder is empty,
	 * this method will create a file called "0001_initial.json". otherwise, it will create a file called "????_.json"
	 * where ???? is the number of json files in this folder + 1 and padded by zeros.
	 * @param _migrationFolder
	 * @return File - the newly created migration file
	 * @throws IOException
	 */
	public File createMigrationFile(File _migrationFolder) throws IOException {
		/*
		 * search for any json files in migrtionFolder which will be pre-existing migration files.
		 * 
		 * found at http://stackoverflow.com/questions/13515150/how-to-get-file-from-directory-with-pattern-filter
		 */
		File[] migrationFiles = _migrationFolder.listFiles(new FileFilter() {
			
			@Override
			public boolean accept(File pathname) {
				if (pathname.getName().contains(".json")) {
					return true;
				}
				return false;
			}
		});
		
		/*
		 * If the folder exists, make sure that it contains other migration files. if not, skip this and
		 * create the initial migration file for this class.
		 */
		if (migrationFiles.length > 0) {
			// there are migration files in the migration folder
			
			/*
			 * Take the length of migrationFiles and add one to it. set the result as the index
			 * of the migration file which we will create and return in this method.
			 */
			int nextIndex = migrationFiles.length + 1;
			
			/*
			 * name of the file to create and return.
			 */
			String nextFileName = _migrationFolder.getAbsolutePath() + "/" + this.getFileName(nextIndex) + "_.json";
			
			//abstract next file. don't know at this point in the method if this file exists or not. It shouldn't.
			File nextMigrationFile = new File(nextFileName);
			
			/*
			 * Make sure the file doesn't exist. if it does, throw an error.
			 * This means that some migration file is missing so the number of files don't match the highest file index.
			 */
			if (nextMigrationFile.exists()) {
				throw new IOException("It looks as if a migration file has been deleted. there should be " + (nextIndex - 1) + " files in the migration folder: " + _migrationFolder.getAbsolutePath());
			} else {
				/*
				 * create the new migration file and return it.
				 */
				nextMigrationFile.createNewFile();
				return nextMigrationFile;
			}
		} else {
			//no migration files found.
			// get name for initial file to create
			String initialMigrationName = _migrationFolder.getAbsolutePath() + "/" + "0001_initial.json";
			
			//create the initial file and return it.
			File initialFile = new File(initialMigrationName);
			
			initialFile.createNewFile();
			return initialFile;
		}
	}
	
	/**
	 * This method returns the file that will contain the migration operations for the class
	 * returned when sending {@code keyForSyncedModels} to {@code Settings.syncedModels}.
	 * @param keyForSyncedModels - key used to get the model type to which the migration file coresponds
	 * @param pathToFolder - path to base migration folder
	 * @return
	 * @throws IOException - if a migration file has been deleted.
	 */
	public File getMigrationFile(String keyForSyncedModels, String pathToFolder) throws IOException {
		/*
		 * the file returned will be used to print a migration operation to.
		 * 
		 * This file should be contained in the following directory system:
		 * 
		 * - <migrationFolder>
		 * 		- <DbObject_type_folderName>-migrations
		 * 			- [migrationfiles]
		 */
		
		/*
		 * This file is the folder that contains the migration files for the given type.
		 */
		File migrationFolder = new File(pathToFolder);
		
		if (migrationFolder.exists()) {
			File nextMigrationFile = this.createMigrationFile(migrationFolder);
			System.out.println("next one: " + nextMigrationFile.getAbsolutePath());
			return nextMigrationFile;
		} else {
			if (migrationFolder.mkdirs()) { //create folder to store migration files
				File newMigrationInitFile = this.createMigrationFile(migrationFolder);
				System.out.println("new one: " + newMigrationInitFile.getAbsolutePath());
				return newMigrationInitFile;
			} else {
				return null;
			}
		}
	}

	public void makeInitialSql(Migration m) {
		try {
			Object jsonObject = ObjectParser.anyObjectToJSON(m);
			
			String json = JSONFormat.formatJSON(jsonObject, 0);
			
			PrintWriter migrationWriter = new PrintWriter(new File("dataBaseChanges/kirkModels_orm_backend_sync_migrationTracking/0001_initial.json"));
			migrationWriter.println(json);
			migrationWriter.close();
		} catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException
				| ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public boolean tableExists(MetaTable _table) {
		for (Class modelClass : Settings.syncedModels.values()) {
			try {
				DbObject modelObject = (DbObject) modelClass.newInstance();
				if (modelObject.tableName.equals(_table.getTableName())) {
					return true;
				}
			} catch (InstantiationException | IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return false;
	}
	
	public ArrayList<Query> getTableDifferences(Class<? extends DbObject> _newDef, MetaTable _databaseState) throws SQLException {
		ArrayList<Query> queries = new ArrayList<Query>();
		
		try {
			DbObject modelObject = (DbObject) _newDef.newInstance();
			MetaTable savedTable = Settings.database.getSpecificTable(modelObject.tableName);
			if (savedTable == null) {
				return new ArrayList<Query>(){{ add(new CreateTable(modelObject.tableName, modelObject)); }};
			} else {
				//table was found but there may be differences
				ArrayList<ColumnOperation> diffs = modelObject.getOperationDifferences(savedTable);
				ArrayList<CreateTable> m2mFieldAdds = new ArrayList<CreateTable>();
				for (String m2mFieldName : modelObject.manyToManyFields) {
					ManyToManyField m2mField = (ManyToManyField) modelObject.getFieldGeneric(m2mFieldName);
					if (Settings.database.getSpecificTable(m2mField.tableName) == null) {
						m2mFieldAdds.add(new CreateTable(m2mField.tableName, m2mField));
					}
				}
				
				if (diffs.size() > 0) {
					AlterTable at = new AlterTable(Settings.database.schema, modelObject.tableName, (Operation[]) diffs.toArray());
					queries.add(at);
				}
				
				if (m2mFieldAdds.size() > 0) {
					queries.addAll(m2mFieldAdds);
				}
			}
		} catch (InstantiationException | IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void printToSqlSheet(Query _query) {
		
	}

}
