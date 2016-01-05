package kirkModels.tests;

import java.io.File;
import java.io.FileNotFoundException;
import java.sql.SQLException;
import java.util.ArrayList;

import org.json.simple.parser.ParseException;

import iansLibrary.data.databases.MetaDatabase;
import kirkModels.config.Settings;
import kirkModels.orm.backend.sync.DbSync;
import kirkModels.queries.DeleteQuery;
import kirkModels.queries.InsertQuery;
import kirkModels.queries.SelectQuery;
import kirkModels.queries.UpdateQuery;
import kirkModels.queries.scripts.WhereCondition;
import kirkModels.queries.scripts.InsertValue;

public abstract class TestModels {

	@SuppressWarnings("unchecked")
	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub

		MetaDatabase db = null;
		try {
			Settings.syncSettings(new File("settings/settings.json"));
			Settings.database.connect();
			Settings.setObjectsForModels();
		} catch (FileNotFoundException | ParseException | SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		DbSync s = new DbSync(Settings.database);
		
		s.migrateModel(Person.class);
		
		Person ian = null;
		Person jesus = null;
		Person wynton = null;
		Person mom = null;
		
		try {
			ian = (Person) Person.objects.get(new ArrayList<WhereCondition>(){{
				add(new WhereCondition("name", WhereCondition.EQUALS, "Ian Kirkpatrick"));
			}});
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		try {
			wynton = (Person) Person.objects.get(new ArrayList<WhereCondition>(){{
				add(new WhereCondition("name", WhereCondition.EQUALS, "Wynton Kirkpatrick"));
			}});
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		try {
			jesus = (Person) Person.objects.get(new ArrayList<WhereCondition>(){{
				add(new WhereCondition("name", WhereCondition.EQUALS, "Jesus Christ"));
			}});
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		try {
			mom = (Person) Person.objects.get(new ArrayList<WhereCondition>(){{
				add(new WhereCondition("name", WhereCondition.EQUALS, "Lori Kirkpatrick"));
			}});
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		System.out.println(ian);
		System.out.println(jesus);
		System.out.println(wynton);
		System.out.println(mom);
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
		
		System.out.println(q.getCommand());
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
