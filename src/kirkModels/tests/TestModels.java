package kirkModels.tests;

import java.io.File;
import java.io.FileNotFoundException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import iansLibrary.data.databases.MetaDatabase;
import kirkModels.DbObject;
import kirkModels.config.Settings;
import kirkModels.orm.QuerySet;
import kirkModels.orm.backend.scripts.PSqlScript;
import kirkModels.orm.backend.sync.DbSync;

public abstract class TestModels {

	@SuppressWarnings("unchecked")
	public static void main(String[] args) {
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
		
//		DbSync syncer = new DbSync(Settings.database);
//		try {
//			syncer.migrateModel(Person.class);
//		} catch (SQLException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		
		HashMap<String, Object> kwargs = new HashMap<String, Object>(){{
			put("name", "Ian Kirkpatrick");
			put("age", 19);
		}};
		
		Person ian = (Person) Person.objects.create(kwargs);
		
		kwargs = new HashMap<String, Object>(){{
			put("name", "Dan Kirkpatrick");
			put("age", 51);
		}};
		
		Person dad = (Person) Person.objects.create(kwargs);
		
		kwargs = new HashMap<String, Object>(){{
			put("name", "Lori Kirkpatrick");
			put("age", 47);
		}};
		
		Person mom = (Person) Person.objects.create(kwargs);
		
		ian.mother.setObject(mom);
		ian.father.setObject(dad);
		ian.save();
		
		kwargs = new HashMap<String, Object>(){{
			put("name", "Wynton Kirkpatrick");
			put("age", 18);
			put("mother", mom.id.val());
			put("father", dad.id.val());
		}};
		
		Person wynton = (Person) Person.objects.create(kwargs);
		
		kwargs = new HashMap<String, Object>(){{
			put("name", "Barack Obama");
			put("age", 55);
		}};
		
		Person barack = (Person) Person.objects.create(kwargs);
		
		kwargs = new HashMap<String, Object>(){{
			put("name", "Jesus Christ");
			put("age", 6000);
		}};
		
		Person jesus = (Person) Person.objects.create(kwargs);
		
		ian.friends.add(jesus);
		ian.enemies.add(barack);
		ian.save();
	}

}
