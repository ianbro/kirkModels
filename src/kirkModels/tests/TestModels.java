package kirkModels.tests;

import java.io.File;
import java.io.FileNotFoundException;
import java.sql.SQLException;
import java.util.Scanner;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import kirkModels.config.MetaDatabase;
import kirkModels.orm.backend.scripts.PSqlScript;

public abstract class TestModels {

	public static void main(String[] args) {
		// TODO Auto-generated method stub

		Person p = new Person();
		p.initializeManyToManyFields();
		p.age.set(19);
		p.name.set("Ian Kirkpatrick");
		PSqlScript a = new PSqlScript("person");
		System.out.println(a.getSaveNewInstanceString(p));
		MetaDatabase db = null;
		try {
			db = new MetaDatabase("vagrant", new File("settings/settings.json"));
		} catch (SQLException | ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println(db.getConnectionURL());
	}

}
