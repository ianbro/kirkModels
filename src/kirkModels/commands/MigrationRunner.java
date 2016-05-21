package kirkModels.commands;

import java.io.File;
import java.io.FileNotFoundException;
import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;

import org.json.simple.parser.ParseException;

import iansLibrary.data.databases.MetaDatabase;
import kirkModels.config.Settings;
import kirkModels.orm.backend.sync.DbSync;
import kirkModels.utils.exceptions.ObjectNotFoundException;

public abstract class MigrationRunner {

	public static void main(String[] args) throws ClassNotFoundException, NoSuchMethodException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, ObjectNotFoundException {
		// TODO Auto-generated method stub
		try {
			Settings.syncSettings(new File("settings/settings.json"));
			
			Settings.database.connect();
			DbSync.migrate();
		} catch (FileNotFoundException | ParseException | SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
