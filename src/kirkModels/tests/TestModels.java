package kirkModels.tests;

import java.io.File;
import java.io.FileNotFoundException;
import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;

import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import iansLibrary.data.databases.MetaDatabase;
import iansLibrary.utilities.JSONClassMapping;
import iansLibrary.utilities.ModdedDate;
import kirkModels.config.Settings;
import kirkModels.fields.CharField;
import kirkModels.fields.ForeignKey;
import kirkModels.fields.IntegerField;
import kirkModels.fields.SavableField;
import kirkModels.orm.backend.sync.DbSync;
import kirkModels.orm.backend.sync.GenerateSqlSheets;
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
		
		DbSync s = new DbSync(Settings.database, new ArrayList<String>(){{
			add("dataBaseChanges/kirkModels_orm_backend_sync_migrationTracking/0001_initial.json");
		}});
		
		try {
			s.readMigrations();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
//		try {
//			s.instantiateField((JSONArray) new JSONParser().parse("[\"kirkModels.fields.CharField\", \"name\", false, null, false, 45]"));
//		} catch (ClassNotFoundException | NoSuchMethodException | ParseException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		
//		TestQuerySets.run();
		
//		GenerateSqlSheets.makeInitialSql(Person.class);
	}
	
	public static void testSelectQuery(){
		WhereCondition id = new WhereCondition("id", WhereCondition.CONTAINED_IN, new ArrayList<Integer>(){{add(1); add(3);}});
		WhereCondition name_first = new WhereCondition("name_first", WhereCondition.EQUALS, "Ian");
		WhereCondition name_last = new WhereCondition("name_last", WhereCondition.NOT_EQUAL_TO, "Johnson");
		
		System.out.println(id.getMySqlString());
		System.out.println(name_first.getMySqlString());
		System.out.println(name_last.getMySqlString());
		
		SelectQuery q = new SelectQuery("kirkmodels_test_person",
			new ArrayList<String>(){{
				add("id");
			}},
			new ArrayList<WhereCondition>(){{
				add(id);
				add(name_first);
				add(name_last);
			}}
		);
	}
	
	public static void testInsertQuery(){
		InsertValue id = new InsertValue("id", 2);
		InsertValue name_first = new InsertValue("name_first", "Ian");
		InsertValue name_last = new InsertValue("name_last", "Kirkpatrick");
		
		System.out.println(id);
		System.out.println(name_first);
		System.out.println(name_last);
		
		InsertQuery q = new InsertQuery("kirkmodels_test_person",
			new ArrayList<InsertValue>(){{
				add(id);
				add(name_first);
				add(name_last);
			}}
		);
		
		System.out.println(q.getCommand());
	}

	public static void testDeleteQuery(){
		WhereCondition id = new WhereCondition("id", WhereCondition.CONTAINED_IN, new ArrayList<Integer>(){{add(1); add(3);}});
		WhereCondition name_first = new WhereCondition("name_first", WhereCondition.EQUALS, "Ian");
		WhereCondition name_last = new WhereCondition("name_last", WhereCondition.NOT_EQUAL_TO, "Johnson");
		
		System.out.println(id.getMySqlString());
		System.out.println(name_first.getMySqlString());
		System.out.println(name_last.getMySqlString());
		
		DeleteQuery q = new DeleteQuery("kirkmodels_test_person",
			new ArrayList<WhereCondition>(){{
				add(id);
				add(name_first);
				add(name_last);
			}}
		);
		
		System.out.println(q.getCommand());
	}

	public static void testUpdateQuery(){
		WhereCondition id = new WhereCondition("id", WhereCondition.CONTAINED_IN, new ArrayList<Integer>(){{add(1); add(3);}});
		
		WhereCondition name_first = new WhereCondition("name_first", WhereCondition.EQUALS, "Ian");
		WhereCondition name_last = new WhereCondition("name_last", WhereCondition.NOT_EQUAL_TO, "Johnson");
		
		System.out.println(id.getMySqlString());
		System.out.println(name_first.getMySqlString());
		System.out.println(name_last.getMySqlString());
		
		UpdateQuery q = new UpdateQuery("kirkmodels_test_person",
			new ArrayList<WhereCondition>(){{
				add(name_first);
				add(name_last);
			}},
			new ArrayList<WhereCondition>(){{
				add(id);
			}}
		);
		
		System.out.println(q.getCommand());
	}
}
