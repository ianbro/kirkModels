package kirkModels.orm.backend.sync.migrationTracking;

import java.sql.SQLException;

import kirkModels.config.Settings;
import kirkModels.fields.CharField;
import kirkModels.orm.DbObject;
import kirkModels.orm.QuerySet;
import kirkModels.orm.backend.sync.queries.CreateTable;

public class SqlSheetTracking extends DbObject {

	public static QuerySet<SqlSheetTracking> objects;

	public static CharField model_name = new CharField("model_name", false, null, true, 50);	
	public CharField last_ran = new CharField("last_ran", true, null, false, 50);
	
	public String toString() {
		return this.model_name.val() + " - last ran: " + this.last_ran.val();
	}
	
	public static void syncTable(){
		CreateTable createTracking = new CreateTable(Settings.database.schema, new SqlSheetTracking());
		try {
			createTracking.run();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
