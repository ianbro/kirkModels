package kirkModels.orm;

import java.sql.SQLException;
import java.util.HashMap;

import kirkModels.DbObject;
import kirkModels.config.Settings;
import kirkModels.tests.Person;

public class DbManager implements Savable {

	@Override
	public void create(HashMap<String, Object> kwargs) {
		Settings.database.dbHandler.insertInto(kwargs);
	}

	@Override
	public DbObject get(HashMap<String, Object> kwargs) throws SQLException {
		QuerySet results = Settings.database.dbHandler.selectFrom(kwargs);
		Class<?> type = (Class<?>) kwargs.get("table_label");
		return results.getById((Class)type, results.results.getInt("id"));
	}

	@Override
	public QuerySet getOrCreate(HashMap<String, Object> kwargs) {
		QuerySet results = Settings.database.dbHandler.selectFrom(kwargs);
		if (results.size() == 0) {
			
		}
		return null;
	}

	@Override
	public QuerySet filter(HashMap<String, Object> kwargs) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public QuerySet all() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void delete(HashMap<String, Object> kwargs) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public int count() {
		// TODO Auto-generated method stub
		return 0;
	}

}
