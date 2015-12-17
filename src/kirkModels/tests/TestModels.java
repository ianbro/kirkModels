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
		} catch (FileNotFoundException | ParseException | SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println(Settings.database);

		Person p = new Person();
		p.initializeManyToManyFields();
		p.age.set(19);
		p.name.set("Ian Kirkpatrick");
		
//		DbSync syncer = new DbSync(Settings.database.dbConnection, Settings.database.schema);
//		try {
//			syncer.migrateModel(Person.class);
//		} catch (SQLException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
	}

}
