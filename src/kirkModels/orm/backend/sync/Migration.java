package kirkModels.orm.backend.sync;

import java.io.File;
import java.io.FileNotFoundException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Scanner;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import iansLibrary.utilities.JSONClassMapping;
import iansLibrary.utilities.JSONMappable;
import kirkModels.config.Settings;
import kirkModels.orm.DbObject;
import kirkModels.orm.backend.sync.migrationTracking.MigrationFile;
import kirkModels.orm.backend.sync.migrationTracking.MigrationTracking;
import kirkModels.orm.backend.sync.queries.CreateTable;
import kirkModels.orm.backend.sync.queries.DropTable;
import kirkModels.queries.InsertQuery;
import kirkModels.queries.Query;
import kirkModels.queries.scripts.WhereCondition;
import kirkModels.tests.Person;
import kirkModels.utils.exceptions.ObjectAlreadyExistsException;
import kirkModels.utils.exceptions.ObjectNotFoundException;

public class Migration implements JSONMappable{
	
	public String dependsOn = null;
	public Query[] operations;

	public Migration(String _dependsOn, Query[] _operations) {
		// TODO Auto-generated constructor stub
		if (_operations != null && _operations.equals("null-value")) {
			this.dependsOn = null;
		} else {
			this.dependsOn = _dependsOn;
		}
		this.operations = _operations;
	}
	
	/**
	 * creates the initial migration for {@code type}.
	 * @param type
	 */
	public Migration(Class<? extends DbObject> type) {
		this.dependsOn = null;
		try {
			this.operations = new Query[]{
					new CreateTable(Settings.database.name, type.newInstance()),
			};
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			this.operations = new Query[0];
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			this.operations = new Query[0];
		}
	}
	
	public Migration(String dependsOn, boolean blank) {
		this.dependsOn = dependsOn;
		this.operations = new Query[0];
	}
	
	public void run() throws SQLException {
		for (Query query : this.operations) {
			query.run();
		}
	}

	@Override
	public Constructor getJsonConstructor() {
		// TODO Auto-generated method stub
		try {
			return this.getClass().getConstructor(new Class[]{
					String.class,
					Query[].class,
			});
		} catch (NoSuchMethodException | SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public String[] getConstructorFieldOrder() {
		// TODO Auto-generated method stub
		return new String[]{
				"dependsOn",
				"operations"
		};
	}
	
	public static Migration getMigrationFromFile(File source) throws ClassNotFoundException, NoSuchMethodException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, FileNotFoundException {
		Scanner scnr = new Scanner(source).useDelimiter("\\Z");
		String jsonVal = scnr.next();
		scnr.close();
		JSONObject json;
		try {
			json = (JSONObject) new JSONParser().parse(jsonVal);
			return (Migration) JSONClassMapping.jsonAnyToObject(json);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}

}
