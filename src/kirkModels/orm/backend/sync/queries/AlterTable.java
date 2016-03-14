package kirkModels.orm.backend.sync.queries;

import java.sql.SQLException;

import kirkModels.config.Settings;
import kirkModels.queries.Query;

public class AlterTable extends Query {
	
	public Operation[] operations;

	public AlterTable(String _dbName, String _tabelName, Operation[] _operations) {
		super(_dbName, _tabelName);
		// TODO Auto-generated constructor stub
		
		this.operations = _operations;
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
		String sql = "ALTER TABLE " + this.dbName + "." + this.tableName;
		
		for (int i = 0; i < this.operations.length - 1; i++) {
			Operation oper = this.operations[i];
			sql = sql + "\n\t" + oper.getMySqlString() + ",";
		}
		sql = sql + "\n\t" + this.operations[this.operations.length].getMySqlString();
		
		sql = end(sql);
		return sql;
	}

	@Override
	public String getPsqlString() {
String sql = "ALTER TABLE " + this.tableName;
		
		for (int i = 0; i < this.operations.length - 1; i++) {
			Operation oper = this.operations[i];
			sql = sql + "\n\t" + oper.getPsqlString() + ",";
		}
		sql = sql + "\n\t" + this.operations[this.operations.length - 1].getPsqlString();
		
		sql = end(sql);
		return sql;
	}

}
