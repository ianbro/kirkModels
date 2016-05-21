package kirkModels.commands;

import java.io.File;
import java.io.FileNotFoundException;
import java.sql.SQLException;

import org.json.simple.parser.ParseException;

import kirkModels.config.Settings;
import kirkModels.orm.backend.sync.DbSync;
import kirkModels.orm.backend.sync.MigrationGenerator;

/**
 * Abstract class containing the main method to run {@code MigrationGenerator.getMigrations()}.
 * @author kirkp1ia
 *
 */
public abstract class MigrationGeneratorMain {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		try {
			Settings.syncSettings(new File("settings/settings.json"));
			
			Settings.database.connect();
			MigrationGenerator.getMigrations();
		} catch (FileNotFoundException | ParseException | SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
