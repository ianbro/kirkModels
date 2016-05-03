package kirkModels.orm.backend.sync.migrationTracking;

import java.sql.SQLException;

import kirkModels.config.Settings;
import kirkModels.fields.CharField;
import kirkModels.orm.DbObject;
import kirkModels.orm.QuerySet;
import kirkModels.orm.backend.sync.queries.CreateTable;

public class MigrationTracking extends DbObject {

	public static QuerySet<MigrationTracking> objects;

	public static CharField model_name = new CharField("model_name", false, null, true, 100);
	public CharField last_ran = new CharField("last_ran", true, null, true, 100);
	
	public void setLastRan(MigrationFile f) {
		this.last_ran.set(f.file_name.val());
	}
	
	public String toString() {
		return this.model_name.val() + " - last ran: " + this.last_ran.val();
	}
	
	public static void syncTable(){
		CreateTable createTracking = new CreateTable(Settings.database.schema, new MigrationTracking());
		try {
			createTracking.run();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
