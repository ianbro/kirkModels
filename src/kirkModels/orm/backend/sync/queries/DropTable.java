package kirkModels.orm.backend.sync.queries;

import java.lang.reflect.Constructor;
import java.sql.SQLException;

import iansLibrary.utilities.JSONMappable;
import kirkModels.config.Settings;
import kirkModels.fields.ManyToManyField;
import kirkModels.fields.SavableField;
import kirkModels.orm.queries.Query;

public class DropTable extends Query implements JSONMappable {

	public DropTable(String _dbName, String _tabelName) {
		super(_dbName, _tabelName);
		// TODO Auto-generated constructor stub
		this.setSql();
	}

	@Override
	public void setSql() {
		// TODO Auto-generated method stub
		this.command = this.toString();
	}

	@Override
	public void run() throws SQLException {
		// TODO Auto-generated method stub
		Settings.database.run(this.command);
	}

	@Override
	public String getMySqlString() {
		// TODO Auto-generated method stub
		return "DROP TABLE " + this.dbName + "." + this.tableName + ";";
	}

	@Override
	public String getPsqlString() {
		// TODO Auto-generated method stub
		return "DROP TABLE " + this.tableName + ";";
	}
	
	@Override
	public Constructor getJsonConstructor() {
		// TODO Auto-generated method stub
		Class[] paramTypes = new Class[]{
				String.class,
				String.class,
		};
		try {
			return this.getClass().getConstructor(paramTypes);
		} catch (NoSuchMethodException | SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}
	
	@Override
	public String[] getConstructorFieldOrder() {
		return new String[]{
				"dbName",
				"tableName",
		};
	}

}
