package kirkModels.orm;

import java.io.File;
import java.io.FileNotFoundException;
import java.sql.SQLException;

import org.json.simple.parser.ParseException;

import kirkModels.config.Settings;
import kirkModels.orm.backend.sync.MigrationGenerator;

/**
 * Core class that contains methods often used in development for the project.
 * @author kirkp1ia
 *
 */
public final class Project {

	/**
	 * This is an abstract class but because it is also final, the abstract keyword cannot be applied. Therefore, this disables instantiation.
	 */
	private Project(){}
	
	/**
	 * <p>initializes global variables needed in the project.</p>
	 * <p>
	 * - Read settings from {@code _pathToFile}.
	 * <br>
	 * - Connect {@code Settings.database} to the database referred to in the settings JSON file.
	 * <br>
	 * - read the database in to the objects for each Model.
	 * </p>
	 * @param _pathToFile
	 * @throws FileNotFoundException if no file at {@code _pathToFile} is found.
	 * @throws ParseException if a syntax error is found in the file at {@code _pathToFile}. This file must be of JSON format.
	 * @throws SQLException if Settings cannot connect the MetaDatabase object stored in {@code Settings.database} to the database using the configurations found in the file at {@code _pathToFile}.
	 */
	public static void initialize(String _pathToFile) throws FileNotFoundException, ParseException, SQLException {
		Settings.syncSettings(new File(_pathToFile));
			
		Settings.database.connect();

		Settings.setObjectsForModels();
	}
}
