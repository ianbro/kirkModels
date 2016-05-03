package kirkModels.orm.backend.sync;

import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.sql.SQLException;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

import javax.swing.text.NumberFormatter;

import org.json.simple.parser.JSONParser;

import iansLibrary.data.databases.MetaTable;
import iansLibrary.utilities.JSONClassMapping;
import iansLibrary.utilities.JSONFormat;
import iansLibrary.utilities.ObjectParser;
import kirkModels.config.Settings;
import kirkModels.fields.ManyToManyField;
import kirkModels.orm.DbObject;
import kirkModels.orm.backend.sync.migrationTracking.MigrationFile;
import kirkModels.orm.backend.sync.migrationTracking.MigrationTracking;
import kirkModels.orm.backend.sync.queries.AlterTable;
import kirkModels.orm.backend.sync.queries.ColumnDefinitionChange;
import kirkModels.orm.backend.sync.queries.ColumnOperation;
import kirkModels.orm.backend.sync.queries.CreateTable;
import kirkModels.orm.backend.sync.queries.DropTable;
import kirkModels.orm.backend.sync.queries.Operation;
import kirkModels.orm.backend.sync.queries.RenameTable;
import kirkModels.queries.Query;
import kirkModels.queries.scripts.WhereCondition;
import kirkModels.tests.Person;
import kirkModels.utils.exceptions.ObjectAlreadyExistsException;
import kirkModels.utils.exceptions.ObjectNotFoundException;

public final class MigrationGenerator {
	
	public String rootMigrationFolderPath;
	public PrintWriter migrationWriters;
	public Migration migrations;
	public File migrationFile;
	public Class<? extends DbObject> types;
	
	public MigrationGenerator(Class<? extends DbObject> _type) {
		this.types = _type;
		try {
			this.rootMigrationFolderPath = Settings.MIGRATION_FOLDER + this.types.newInstance().tableName + "-migrations/";
		} catch (InstantiationException | IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * Creates the migration file for a given class type.
	 * @param _key - the key which will be sent to {@code Settings.syncedModels} to retrieve the class type to migrate.
	 * @param _indexInStorage - the index in which this migration file will be stored in migration array attributes.
	 * @throws IOException
	 */
	public void genterateMigrationFile() throws IOException {
		
		/*
		 * instantiated migration folder
		 */
		this.migrationFile = this.getMigrationFile();
		
		try {
			PrintWriter pw = new PrintWriter(this.migrationFile);
			this.migrationWriters = pw;
			/*
			 * From here, generate a migration for type and add it to this.migrations at index i.
			 * later, we will loop through this.migrations and call those migrations.
			 */
//			this.migrations[i] = this.makeMigration(type);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			this.migrationWriters = null;
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
	public File getMigrationFile() throws IOException {
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
		File migrationFolder = new File(this.rootMigrationFolderPath);
		
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
	
	/**
	 * This method will loop through the fields in a class and determin the differences between them and
	 * the fields contained in the database. It will also loop through the database and determin which
	 * fields have been dropped.<br><br>
	 * This method assumes the table exists in the database for the type {@code _newDef}.
	 * @param _newDef
	 * @param _databaseState
	 * @return
	 * @throws SQLException
	 */
	public ArrayList<Query> getTableDifferences() throws SQLException {
		MetaTable databaseState = null;
		try {
			databaseState = Settings.database.getSpecificTable(this.types.newInstance().tableName);
		} catch (InstantiationException | IllegalAccessException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		ArrayList<Query> queries = new ArrayList<Query>();
		
		try {
			//make a temporary object out of that class. This is because the fields need to be instantiated in order
			//		to do anything with them.
			DbObject modelObject = (DbObject) this.types.newInstance();
			//table to compare the fields with modelObject
			MetaTable savedTable = Settings.database.getSpecificTable(modelObject.tableName);
			
			if (savedTable == null) {
				//If this is true, then the class has been added recently since the last migration file
				return new ArrayList<Query>(){{ add(new CreateTable(modelObject.tableName, modelObject)); }};
			} else {
				//table was found but there may be differences
				ArrayList<Operation> diffs = modelObject.getOperationDifferences(savedTable);
				ArrayList<CreateTable> m2mFieldAdds = new ArrayList<CreateTable>();
				ArrayList<DropTable> m2mFieldRemoves = new ArrayList<DropTable>();
				
				//adding create table for m2m field
				for (String m2mFieldName : modelObject.manyToManyFields) {
					ManyToManyField m2mField = (ManyToManyField) modelObject.getFieldGeneric(m2mFieldName);
					if (Settings.database.getSpecificTable(m2mField.tableName) == null) {
						m2mFieldAdds.add(new CreateTable(m2mField.tableName, m2mField));
					}
				}
				
				for (MetaTable metaTable : Settings.database.getTables()) {
					if (metaTable.getTableName().split("___").length == 4 && metaTable.getTableName().split("___")[0].equals("mtm")) {
						/*
						 * If this m2m table belongs to this this model. We don't know if it's necesarilly the right field yet.
						 */
						if (metaTable.getTableName().split("___")[2].equals(databaseState.getTableName().split("_")[databaseState.getTableName().split("_").length])) {
							/*
							 * does the field exist still? if not, drop this table/m2m field
							 */
							if (!modelObject.manyToManyFields.contains(metaTable.getTableName().split("___")[1])) {
								//Then this field has been dropped.
								m2mFieldRemoves.add(new DropTable(Settings.database.schema, metaTable.getTableName()));
							} else {
								//The field still exists but may have been altered
								String metaTableRefName = metaTable.getTableName().split("___")[3];
								ManyToManyField m2mf = (ManyToManyField) modelObject.getFieldGeneric(metaTable.getTableName().split("___")[1]);
								
								if (!metaTableRefName.equals(m2mf.refClass.getSimpleName().toLowerCase())) {
									/*
									 * Then the field has been altered. drop the table. It has laready been taken care of above while
									 * adding tables that have been created as new.
									 */
									m2mFieldRemoves.add(new DropTable(Settings.database.schema, metaTable.getTableName()));
								}
								
							}
						}
					}
				}
				
				if (diffs.size() > 0) {
					Operation[] operations = new Operation[diffs.size()];
					AlterTable at = new AlterTable(Settings.database.schema, modelObject.tableName, diffs.toArray(operations));
					queries.add(at);
				}
				
				if (m2mFieldAdds.size() > 0) {
					queries.addAll(m2mFieldAdds);
				}
			}
			return queries;
		} catch (InstantiationException | IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	public String getLastMigrationRan() {
		try {
			File migrationFolder = new File(this.rootMigrationFolderPath + this.types.newInstance().tableName + "-migrations/");
			File[] migrationFiles = migrationFolder.listFiles(new FileFilter() {
				
				@Override
				public boolean accept(File pathname) {
					if (pathname.getName().contains(".json")) {
						return true;
					}
					return false;
				}
			});
			int max = 0;
			for (File migFile : migrationFiles) {
				int otherNum = Integer.parseInt(migFile.getName().split("_")[0]);
				if (max - otherNum > 0) {
					max = otherNum;
				}
			}
			for (File migFile : migrationFiles) {
				if (Integer.parseInt(migFile.getName().split("_")[0]) == max) {
					return migFile.getName();
				}
			}
		} catch (InstantiationException | IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	public void generate() {
		String tableName = null;
		try {
			tableName = this.types.newInstance().tableName;
		} catch (InstantiationException e3) {
			// TODO Auto-generated catch block
			e3.printStackTrace();
		} catch (IllegalAccessException e3) {
			// TODO Auto-generated catch block
			e3.printStackTrace();
		}
		
		try {
			if (Settings.database.getSpecificTable(this.types.getName()) == null) {
				//table has been added so we need to make a create table query.
				try {
					this.genterateMigrationFile();
					this.migrations = new Migration(this.types);
					return;
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		} catch (SQLException e3) {
			// TODO Auto-generated catch block
			e3.printStackTrace();
		}
		
		try {
			this.types.getField("DROP_TABLE");
			/*
			 * add drop table stuff here.
			 */
			Migration newMigration = null;
			
			String last = this.getLastMigrationRan();
			if (last == null) {
				newMigration = new Migration(this.getLastMigrationRan(), new Query[]{
						new DropTable(Settings.database.schema, tableName)
				});
			} else {
				newMigration = new Migration(null, new Query[]{
						new DropTable(Settings.database.schema, tableName)
				});
			}
			
			this.genterateMigrationFile();
			this.migrations = newMigration;
			
		} catch (NoSuchFieldException e) {
			//This field does not exist so now see if this table was renamed.
			
			try {
				Field renameField = this.types.getField("RENAME_TABLE");
				Migration newMigration = null;
				
				String last = this.getLastMigrationRan();
				if (last == null) {
					String newName = (String) renameField.get(null);
					newMigration = new Migration(last, new Query[]{
							new AlterTable(Settings.database.schema, this.types.newInstance().tableName, new Operation[]{
									new RenameTable(newName)
							})
					});
				} else {
					String newName = (String) renameField.get(null);
					newMigration = new Migration(null, new Query[]{
							new AlterTable(Settings.database.schema, this.types.newInstance().tableName, new Operation[]{
									new RenameTable(newName)
							})
					});
				}
				this.genterateMigrationFile();
				this.migrations = newMigration;
			} catch (NoSuchFieldException e1) {
				//This field does not exist so now get the differences in the class from the database.
				
				try {
					ArrayList<Query> queryDifferences = this.getTableDifferences();
					
					
					if (queryDifferences != null) {
						Query[] queries = new Query[queryDifferences.size()];
						queries = queryDifferences.toArray(queries);
						if (this.getLastMigrationRan() == null) {
							this.genterateMigrationFile();
							this.migrations = new Migration(this.getLastMigrationRan(), queries);
						} else {
							//This is the initial migration
							this.genterateMigrationFile();
							this.migrations = new Migration(null, queries);
						}
					}
				} catch (SQLException e2) {
					// TODO Auto-generated catch block
					e2.printStackTrace();
				} catch (IOException e2) {
					// TODO Auto-generated catch block
					e2.printStackTrace();
				}
				
			} catch (SecurityException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (IllegalArgumentException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (IllegalAccessException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (InstantiationException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static ArrayList<MigrationGenerator> getMigrations(){
		ArrayList<MigrationGenerator> migrations = new ArrayList<MigrationGenerator>();
//		ArrayList<PrintWriter> migrationWriters = new ArrayList<PrintWriter>();
		
		/*
		 * loop through synced models and evaluate the differences between this software and the database
		 */
		int index = 0;
		for (String tableName : Settings.syncedModels.keySet()) {
			Class<? extends DbObject> classType = Settings.syncedModels.get(tableName);
			MigrationGenerator migGen = new MigrationGenerator(classType);
			migGen.generate();
			migGen.printToSqlSheet();
			migrations.add(migGen);
		}
		
		return migrations;
	}
	
	public void printToSqlSheet() {
		try {
			String jsonToPrint = JSONFormat.formatJSON(ObjectParser.anyObjectToJSON(this.migrations), 0);
			this.migrationWriters.print(jsonToPrint);
			this.migrationWriters.close();
		} catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException
				| ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public String toString() {
		try {
			return this.types.newInstance().tableName;
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

}
