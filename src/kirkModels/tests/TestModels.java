package kirkModels.tests;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Scanner;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import iansLibrary.data.databases.MetaDatabase;
import iansLibrary.data.databases.MetaTable;
import iansLibrary.data.databases.MetaTableColumn;
import iansLibrary.utilities.JSONClassMapping;
import iansLibrary.utilities.JSONFormat;
import iansLibrary.utilities.ModdedDate;
import iansLibrary.utilities.ObjectParser;
import kirkModels.config.Settings;
import kirkModels.fields.CharField;
import kirkModels.fields.ForeignKey;
import kirkModels.fields.IntegerField;
import kirkModels.fields.ManyToManyField;
import kirkModels.fields.SavableField;
import kirkModels.orm.backend.sync.DbSync;
import kirkModels.orm.backend.sync.MigrationGenerator;
import kirkModels.orm.backend.sync.Migration;
import kirkModels.orm.backend.sync.queries.AddColumn;
import kirkModels.orm.backend.sync.queries.AddForeignKey;
import kirkModels.orm.backend.sync.queries.AlterTable;
import kirkModels.orm.backend.sync.queries.ColumnDefinitionChange;
import kirkModels.orm.backend.sync.queries.CreateTable;
import kirkModels.orm.backend.sync.queries.DropField;
import kirkModels.orm.backend.sync.queries.Operation;
import kirkModels.orm.backend.sync.queries.RenameField;
import kirkModels.orm.backend.sync.queries.RenameTable;
import kirkModels.queries.DeleteQuery;
import kirkModels.queries.InsertQuery;
import kirkModels.queries.SelectQuery;
import kirkModels.queries.UpdateQuery;
import kirkModels.queries.scripts.WhereCondition;
import kirkModels.utils.exceptions.ObjectAlreadyExistsException;
import kirkModels.utils.exceptions.ObjectNotFoundException;
import kirkModels.queries.scripts.InsertValue;

public abstract class TestModels {

	@SuppressWarnings("unchecked")
	public static void main(String[] args) throws ObjectNotFoundException {
		// TODO Auto-generated method stub

		MetaDatabase db = null;
		try {
			Settings.syncSettings(new File("settings/settings.json"));
			
//			new DbSync(Settings.database).migrateModel(Person.class);
			
//			Settings.database.connect();
//			Settings.setObjectsForModels();
		} catch (FileNotFoundException | ParseException | SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
//		Migration m = new Migration(Person.class);
//		
//		MigrationGenerator.makeInitialSql(m);
		
		DbSync s = new DbSync(Settings.database, new ArrayList<String>(){{
			add(Settings.MIGRATION_FOLDER + "kirkModels_orm_backend_sync_migrationTracking/0001_initial.json");
		}});
		
//		try {
//			s.readMigrations();
//		} catch (FileNotFoundException | ClassNotFoundException | NoSuchMethodException | InstantiationException
//				| IllegalAccessException | IllegalArgumentException | InvocationTargetException | ParseException
//				| SQLException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}

//		MigrationGenerator gen = new MigrationGenerator(Settings.MIGRATION_FOLDER);
//		try {
//			gen.generateMigrationFiles();
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		
//		try {
//			MetaTable ts = Settings.database.getTables().get(2);
//			MetaTableColumn cm = ts.columns.get(1);
//			System.out.println(cm.getColumnName());
//			System.out.println(new Person().age.equals(cm));
//		} catch (SQLException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		
		try {
			System.out.println(MigrationGenerator.getTableDifferences(Person.class, Settings.database.getSpecificTable(new Person().tableName)));
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
