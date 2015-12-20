package kirkModels.tests;

import java.io.File;
import java.io.FileNotFoundException;
import java.sql.SQLException;
import java.util.Scanner;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import iansLibrary.data.databases.MetaDatabase;
import kirkModels.config.Settings;
import kirkModels.orm.backend.scripts.PSqlScript;
import kirkModels.orm.backend.sync.DbSync;

public abstract class TestModels {

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

//		Person ian = new Person();
//		ian.age.set(19);
//		ian.name.set("Ian Kirkpatrick");
//		
//		ian.save();
		
//		Person mom = new Person();
//		mom.age.set(49);
//		mom.name.set("Lori Kirkpatrick");
//		
//		Person dad = new Person();
//		dad.age.set(51);
//		dad.name.set("Daniel Kirkpatrick");
//		
//		mom.save();
//		dad.save();
//		
		
		Person mom = (Person) Person.objects.getById(2);
		Person dad = (Person) Person.objects.getById(3);
		
		Person ian = (Person) Person.objects.getById(1);
		System.out.println(ian);
//		ian.age.set(20);
		ian.mother.setObject(mom);
		ian.father.setObject(dad);
		ian.save();
		
//		DbSync syncer = new DbSync(Settings.database);
//		try {
//			syncer.migrateModel(Person.class);
//		} catch (SQLException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
	}

}
