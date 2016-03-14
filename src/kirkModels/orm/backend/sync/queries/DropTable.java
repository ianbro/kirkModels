package kirkModels.orm.backend.sync.queries;

import java.sql.SQLException;

import kirkModels.config.Settings;
import kirkModels.queries.Query;

public class DropTable extends Query {

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

}
