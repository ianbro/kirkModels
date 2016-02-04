package kirkModels.orm.backend.sync.queries;

import kirkModels.config.Settings;

public abstract class Operation {

	public abstract String getMySqlString();
	
	public abstract String getPsqlString();
	
	public String toString(){
		String language = Settings.database.language;
		
		String sql = "";
		
		switch (language) {
		case "MySQL":
			
			sql = this.getMySqlString();
			break;
			
		case "postgreSQL":
			
			sql = this.getPsqlString();
			break;

		default:
			
			sql = "No default language.";
			break;
		}
		
		return sql;
	}

}
