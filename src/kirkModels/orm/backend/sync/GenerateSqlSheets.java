package kirkModels.orm.backend.sync;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;

import kirkModels.config.Settings;
import kirkModels.orm.DbObject;
import kirkModels.orm.backend.sync.queries.CreateTable;
import kirkModels.queries.Query;

public final class GenerateSqlSheets {
	
	public static PrintWriter setupSheet(Class<? extends DbObject> _type) {
		File sqlFile = null;
		PrintWriter sqlWriter = null;
		DbObject tmpObj = null;
		try {
			tmpObj = _type.newInstance();
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
			sqlWriter.println("Table Name: " + tmpObj.tableName + "\n===================================================\nMySql initial statement:\n");
			return sqlWriter;
		}
	}

	public static void makeInitialSql(Class<? extends DbObject> _type) {
		try {
			CreateTable query = new CreateTable(Settings.database.schema, _type.newInstance());
			System.out.println(query.getMySqlString());
		} catch (InstantiationException | IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void printToSqlSheet(Query _query) {
		
	}

}
