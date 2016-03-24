package kirkModels.orm.backend.sync;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;

import iansLibrary.utilities.JSONFormat;
import iansLibrary.utilities.ObjectParser;
import kirkModels.config.Settings;
import kirkModels.orm.DbObject;
import kirkModels.orm.backend.sync.queries.CreateTable;
import kirkModels.queries.Query;
import kirkModels.tests.Person;

public final class GenerateSqlSheets {
	
	public PrintWriter[] migrationWriters;
	public Migration[] migrations;
	public Class<? extends DbObject>[] types;
	
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

	public static void makeInitialSql(Migration m) {
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
