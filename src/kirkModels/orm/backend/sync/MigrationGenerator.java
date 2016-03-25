package kirkModels.orm.backend.sync;

import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;

import javax.swing.text.NumberFormatter;

import iansLibrary.utilities.JSONFormat;
import iansLibrary.utilities.ObjectParser;
import kirkModels.config.Settings;
import kirkModels.orm.DbObject;
import kirkModels.orm.backend.sync.queries.CreateTable;
import kirkModels.queries.Query;
import kirkModels.tests.Person;

public final class MigrationGenerator {
	
	public PrintWriter[] migrationWriters;
	public Migration[] migrations;
	public Class<? extends DbObject>[] types;
	
	public MigrationGenerator() {
		String pathToMigrationFolderAll = Settings.ROOT_FOLDER + "dataBseChanges/";
		
		this.migrationWriters = new PrintWriter[Settings.syncedModels.keySet().size()];
		this.migrations = new Migration[Settings.syncedModels.keySet().size()];
		this.types = new Class[Settings.syncedModels.keySet().size()];
		
		int i = 0;
		for (String key : Settings.syncedModels.keySet()) {
			Class type = Settings.syncedModels.get(key);
			String pathToMigrationFolderSpecific = pathToMigrationFolderAll + type.getName().replace(".", "_");
			
			File migrationFile = this.getMigrationFile(key, pathToMigrationFolderSpecific);
			try {
				PrintWriter pw = new PrintWriter(migrationFile);
				this.migrationWriters[i] = pw;
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				this.migrationWriters[i] = null;
			}
		}
	}
	
	public String getFileName(Integer num) {
		String fileBeginning = String.format("%04d", String.valueOf(num));
		
		fileBeginning = fileBeginning + ".json";
		
		return fileBeginning;
	}
	
	public File getMigrationFile(String keyForSyncedModels, String pathToFolder) {
		File migrationFile = null;
		
		String errors = "";
		
		Class type = Settings.syncedModels.get(keyForSyncedModels);
		File migrationFolder = new File(pathToFolder);
		
		if (migrationFolder.exists()) {
			//loop through migrationFiles in folder.
			File[] migrationFiles = migrationFolder.listFiles(new FileFilter() {
				
				@Override
				public boolean accept(File pathname) {
					if (pathname.getName().contains(".json")) {
						return true;
					}
					return false;
				}
			});
			
			for (File file : migrationFiles) {
				String strNumFile = file.getName().split("_")[0];
				Integer number = Integer.parseInt(strNumFile);
				String pathToCreate = migrationFolder.getAbsolutePath() + this.getFileName(number + 1);
				
				migrationFile = new File(pathToCreate);
				try {
					if (!migrationFile.createNewFile()) {
						errors = errors.concat("Sorry, could not create file: " + pathToCreate + "\r\n" + Thread.getAllStackTraces().get(Thread.currentThread()) + "\r\n");
						migrationFile = null;
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					migrationFile = null;
				}
			}
		} else {
			if (migrationFolder.mkdirs()) {
				String initFilePath = migrationFolder.getAbsolutePath() + "0001_initial.json";
				migrationFile = new File(initFilePath);
				try {
					if (!migrationFile.createNewFile()) {
						//IDK yet what to do because I don't know what case would cause this.
						errors = errors.concat("Sorry, could not create file: " + initFilePath + "\r\n" + Thread.getAllStackTraces().get(Thread.currentThread()) + "\r\n");
						migrationFile = null;
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					migrationFile = null;
				}
			} else {
				migrationFile = null;
				errors = errors.concat("Sorry, could not create folder: " + pathToFolder + "\r\n" + Thread.getAllStackTraces().get(Thread.currentThread()) + "\r\n");
			}
		}
		
		if (!errors.equals("")) {
			System.err.println(errors);
		}
		return migrationFile;
	}
	
	public PrintWriter setupSheet(int indexOfMigration) {
		File sqlFile = null;
		PrintWriter sqlWriter = null;
		DbObject tmpObj = null;
		try {
			tmpObj = this.types[indexOfMigration].newInstance();
			sqlFile = new File("databaseChanges/" + tmpObj.tableName + "/0001_initial");
			sqlWriter = new PrintWriter(sqlFile);
		} catch (InstantiationException | IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			try {
				sqlFile.createNewFile();
				sqlWriter = new PrintWriter(sqlWriter);
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		} finally {
			return sqlWriter;
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
	
	public static void printToSqlSheet(Query _query) {
		
	}

}
