package kirkModels.orm.backend.sync;

import java.io.File;
import java.io.FileFilter;
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
import kirkModels.config.Settings;
import kirkModels.fields.ManyToManyField;
import kirkModels.orm.DbObject;
import kirkModels.orm.QuerySet;
import kirkModels.orm.backend.sync.migrationTracking.MigrationFile;
import kirkModels.orm.backend.sync.migrationTracking.MigrationTracking;
import kirkModels.orm.backend.sync.queries.CreateTable;
import kirkModels.queries.Query;
import kirkModels.queries.scripts.WhereCondition;
import kirkModels.utils.exceptions.ObjectNotFoundException;

public class DbSync {

	String dbName;
	Connection dbConnection;
	
	public static void migrate() throws ClassNotFoundException, NoSuchMethodException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, FileNotFoundException, SQLException, ObjectNotFoundException {
		if (Settings.database.getSpecificTable(new MigrationTracking().tableName) == null) {
			MigrationTracking.syncTable();
			MigrationFile.syncTable();
		}
		Settings.setObjectsForModel(MigrationTracking.class);
		Settings.setObjectsForModel(MigrationFile.class);
		syncBaseDatabase();
		migrateModels();
	}
	
	public static void syncBaseDatabase() {
		for (String tableName : Settings.syncedModels.keySet()) {
			Class<? extends DbObject> modelClass = Settings.syncedModels.get(tableName);
			MigrationTracking mt = MigrationTracking.objects.getOrCreate(new ArrayList<WhereCondition>(){{
				add(new WhereCondition("model_name", WhereCondition.EQUALS, modelClass.getName()));
			}}).getRow(0);
			
			for (int i = 0; i < getMigrationFilesForModel(getMigrationFolderForModel(modelClass)).length; i++) {
				File migFile = getMigrationFilesForModel(getMigrationFolderForModel(modelClass))[i];
				String fileName = migFile.getName();
				String dirName = migFile.getParentFile().getAbsolutePath().replace(Settings.BINARY_ROOT, "");
				MigrationFile f = MigrationFile.objects.getOrCreate(new ArrayList<WhereCondition>(){{
					add(new WhereCondition("file_name", WhereCondition.EQUALS, fileName));
					add(new WhereCondition("dir_path", WhereCondition.EQUALS, dirName));
					add(new WhereCondition("model_name", WhereCondition.EQUALS, modelClass.getName()));
					add(new WhereCondition("migration_tracker", WhereCondition.EQUALS, mt.id.val()));
				}}).getRow(0);
			}
		}
	}
	
	public static File[] getMigrationFilesForModel(File migrationFolder) {
		File[] migrationFiles = migrationFolder.listFiles(new FileFilter() {
			
			@Override
			public boolean accept(File pathname) {
				if (pathname.getName().contains(".json")) {
					return true;
				}
				return false;
			}
		});
		return migrationFiles;
	}
	
	public static File getMigrationFolderForModel(Class<? extends DbObject> _modelClass) {
		try {
			String path = Settings.MIGRATION_FOLDER + _modelClass.newInstance().tableName + "-migrations/";
			return new File(path);
		} catch (InstantiationException | IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}
	
	public static void migrateModels() throws ClassNotFoundException, NoSuchMethodException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, FileNotFoundException, SQLException, ObjectNotFoundException {
		for (String modelKey : Settings.syncedModels.keySet()) {
			Class<? extends DbObject> modelClass = Settings.syncedModels.get(modelKey);

			runNextMigrationForModel(modelClass);
		}
	}
	
	public static void runNextMigrationForModel(Class<? extends DbObject> modelClass) throws ClassNotFoundException, NoSuchMethodException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, FileNotFoundException, SQLException, ObjectNotFoundException {
		MigrationTracking migrationTracker = MigrationTracking.objects.get(new ArrayList<WhereCondition>(){{
			add(new WhereCondition("model_name", WhereCondition.EQUALS, modelClass.getName()));
		}});
		
		MigrationFile toRun = null;
		if (migrationTracker.last_ran.val() != null) {
			toRun = MigrationFile.objects.get(new ArrayList<WhereCondition>(){{
				add(new WhereCondition("file_name", WhereCondition.EQUALS, migrationTracker.last_ran.val()));
				add(new WhereCondition("migration_tracker", WhereCondition.EQUALS, migrationTracker.id.val()));
			}}).getNextMigrationFile();
		} else {
			QuerySet<MigrationFile> migs = MigrationFile.objects.filter(new ArrayList<WhereCondition>(){{
				add(new WhereCondition("migration_tracker", WhereCondition.EQUALS, migrationTracker.id.val()));
			}});
			
			if (migs.count() > 0) {
				toRun = MigrationFile.objects.get(new ArrayList<WhereCondition>(){{
					add(new WhereCondition("file_name", WhereCondition.EQUALS, "0001_initial.json"));
					add(new WhereCondition("migration_tracker", WhereCondition.EQUALS, migrationTracker.id.val()));
				}});
			}
		}
		
		if (toRun != null) {
			Migration toMigrate = Migration.getMigrationFromFile(new File(toRun.getPathToFile()));
			String dependsFileName = toMigrate.dependsOn.split("/")[toMigrate.dependsOn.split("/").length - 1];
			String dependsDirName = toMigrate.dependsOn.replace(dependsFileName, "");
			try {
				MigrationFile depended = MigrationFile.objects.get(new ArrayList<WhereCondition>(){{
					add(new WhereCondition("file_name", WhereCondition.EQUALS, dependsFileName));
					add(new WhereCondition("dir_path", WhereCondition.EQUALS, dependsDirName));
				}});
				if (depended.compareTo(toRun) == 0) {
					// it is a migration that is not for modelClass.
					
				} else {
					// assume it's already run.
					toMigrate.run();
					migrationTracker.setLastRan(toRun);
					migrationTracker.save();
					runNextMigrationForModel(modelClass);
				}
			} catch (ObjectNotFoundException e) {
				toMigrate.run();
				migrationTracker.setLastRan(toRun);
				migrationTracker.save();
				runNextMigrationForModel(modelClass);
			}
		}
	}
	
	
}
