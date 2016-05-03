package kirkModels.orm.backend.sync.migrationTracking;

import java.sql.SQLException;
import java.util.ArrayList;

import kirkModels.config.Settings;
import kirkModels.fields.CharField;
import kirkModels.fields.ForeignKey;
import kirkModels.orm.DbObject;
import kirkModels.orm.QuerySet;
import kirkModels.orm.backend.sync.queries.CreateTable;
import kirkModels.queries.scripts.WhereCondition;

public class MigrationFile extends DbObject implements Comparable<MigrationFile>{

	public static QuerySet<MigrationFile> objects;
	
	public CharField file_name = new CharField("file_name", false, "0001_initial.json", false, 100);
	public CharField dir_path = new CharField("dir_path", false, null, false, 400);
	public CharField model_name = new CharField("model_name", false, null, false, 100);
	public ForeignKey<MigrationTracking> migration_tracker = new ForeignKey<MigrationTracking>("migration_tracker", MigrationTracking.class, false, null, false, " CASCADE");
	
	public Class<? extends DbObject> getModelClass() {
		try {
			return (Class<? extends DbObject>) Class.forName(model_name.val());
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}
	
	public String getPathToFile() {
		if (this.dir_path.val().endsWith("/")) {
			return this.dir_path.val() + this.file_name.val();
		} else {
			return this.dir_path.val() + "/" + this.file_name.val();
		}
	}
	
	public static void syncTable(){
		CreateTable createFileTable = new CreateTable(Settings.database.schema, new MigrationFile());
		try {
			createFileTable.run();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public MigrationFile getNextMigrationFile() {
		QuerySet<MigrationFile> migs = MigrationFile.objects.filter(new ArrayList<WhereCondition>(){{
			add(new WhereCondition("migration_tracker", WhereCondition.EQUALS, migration_tracker.val()));
		}});
		
		if (migs.count() > 0) {
			for (MigrationFile migrationFile : migs) {
				if (this.compareTo(migrationFile) == -1) {
					return migrationFile;
				}
			}
		}
		
		return null;
	}

	@Override
	public int compareTo(MigrationFile o) {
		// TODO Auto-generated method stub
		if (this.migration_tracker.val().equals(o.migration_tracker.val())) {
			int thisNum = Integer.parseInt(this.file_name.val().split("_")[0]);
			int otherNum = Integer.parseInt(o.file_name.val().split("_")[0]);
			
			return thisNum - otherNum;
		} else {
			return 0;
		}
	}
}
